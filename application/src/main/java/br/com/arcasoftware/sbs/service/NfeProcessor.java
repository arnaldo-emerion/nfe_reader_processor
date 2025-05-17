package br.com.arcasoftware.sbs.service;

import br.com.arcasoftware.sbs.builder.CFeBuilder;
import br.com.arcasoftware.sbs.builder.NFeBuilder;
import br.com.arcasoftware.sbs.enums.EnumException;
import br.com.arcasoftware.sbs.exception.ValidationException;
import br.com.arcasoftware.sbs.model.HistoricoProcessamento;
import br.com.arcasoftware.sbs.model.cupomfiscal.CupomFiscal;
import br.com.arcasoftware.sbs.model.cupomfiscal.CupomFiscalModel;
import br.com.arcasoftware.sbs.model.dto.S3ObjectDTO;
import br.com.arcasoftware.sbs.model.dto.TipoDocumento;
import br.com.arcasoftware.sbs.model.nfe.NFe;
import br.com.arcasoftware.sbs.utils.XMLUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;
import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.List;

@Service
@Slf4j
public class NfeProcessor {

    private final DestinatarioService destinatarioService;
    private final EmitenteService emitenteService;
    private final ProdutoService produtoService;
    private final TransportadoraService transportadoraService;
    private final NFeService nFeService;
    private final HistoricoProcessamentoService historicoProcessamentoService;
    private final ErroProcessamentoService erroProcessamentoService;
    private final CFeService cFeService;
    private final CFeEmitenteService cFeEmitenteService;
    private final CFeDestinatarioService cFeDestinatarioService;
    private final CFeProdutoService cFeProdutoService;

    public NfeProcessor(DestinatarioService destinatarioService,
                        EmitenteService emitenteService,
                        ProdutoService produtoService,
                        TransportadoraService transportadoraService,
                        NFeService nFeService,
                        HistoricoProcessamentoService historicoProcessamentoService,
                        ErroProcessamentoService erroProcessamentoService,
                        CFeService cFeService,
                        CFeEmitenteService cFeEmitenteService,
                        CFeDestinatarioService cFeDestinatarioService,
                        CFeProdutoService cFeProdutoService) {
        this.destinatarioService = destinatarioService;
        this.emitenteService = emitenteService;
        this.produtoService = produtoService;
        this.transportadoraService = transportadoraService;
        this.nFeService = nFeService;
        this.historicoProcessamentoService = historicoProcessamentoService;
        this.erroProcessamentoService = erroProcessamentoService;
        this.cFeService = cFeService;
        this.cFeEmitenteService = cFeEmitenteService;
        this.cFeDestinatarioService = cFeDestinatarioService;
        this.cFeProdutoService = cFeProdutoService;
    }

    public void processNFe(S3ObjectDTO s3ObjectDTO) {
        boolean processadaCorretamente = false;
        String motivo = null;
        try {
            this.process(s3ObjectDTO.getInputStream(), s3ObjectDTO.getFileName(), s3ObjectDTO.getUserName());
            processadaCorretamente = true;
            motivo = "Nota fiscal processada corretamente";
        } catch (ValidationException | DataIntegrityViolationException ex) {
            motivo = ex.getMessage();
            throw ex;
        }  catch (Exception ex) {
            motivo = ex.getMessage();
            throw ex;
        }finally {
            HistoricoProcessamento historicoProcessamento = new HistoricoProcessamento(s3ObjectDTO.getFileName(), Calendar.getInstance(), processadaCorretamente, motivo);
            historicoProcessamento.setUserCreate(s3ObjectDTO.getUserName());
            this.historicoProcessamentoService.save(historicoProcessamento);
        }
    }

    private void process(InputStream inputStream, String fileName, String userName) {
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder;
        Document document;
        try {
            docBuilder = docFactory.newDocumentBuilder();
            document = docBuilder.parse(inputStream, "UTF-8");
        } catch (ParserConfigurationException | IOException | SAXException e) {
            throw new ValidationException(EnumException.INVALID_XML_PASSED_IN);
        }
        XPathFactory factory = XPathFactory.newInstance();
        XPath xpath = factory.newXPath();
        TipoDocumento tipoDocumento = getTipoDocumento(document, xpath);
        switch (tipoDocumento) {
            case NFE: {
                Object protocoloNode = XMLUtils.getObject(document, xpath, "//protNFe", XPathConstants.NODE);

                if (null == protocoloNode) {
                    throw new ValidationException(EnumException.NFE_WITHOUT_PROTOCOL);
                }

                String chNFe = (String) XMLUtils.getObject(document, xpath, "//protNFe/infProt/chNFe", XPathConstants.STRING);

                List<NFe> byChNFe = this.nFeService.findByUserCreateAndChaveNFe(userName, chNFe);

                if (!byChNFe.isEmpty()) {
                    this.nFeService.delete(byChNFe.get(0));
                }

                NFe nfe = new NFeBuilder(document, xpath, destinatarioService, emitenteService, produtoService, transportadoraService, userName, erroProcessamentoService)
                        .comNFe()
                        .comTransportadora()
                        .comEmitente()
                        .comDestinatario()
                        .comItens()
                        .comNFeTotalICMS()
                        .build();

                nfe.setUserCreate(userName);
                nfe.setFileName(fileName);
                this.nFeService.save(nfe);
                break;
            }
            case CFE: {
                Object idCFeNode = XMLUtils.getObject(document, xpath, "//infCFe", XPathConstants.NODE);
                String chaveCFe = ((Element) idCFeNode).getAttribute("Id");
                List<CupomFiscalModel> byChCFe = this.cFeService.findByUserCreateAndChaveNFe(userName, chaveCFe);
                if (!byChCFe.isEmpty()) {
                    this.cFeService.delete(byChCFe.get(0));
                }
                CupomFiscal cupomFiscal = new CFeBuilder(
                        document,
                        xpath,
                        userName,
                        erroProcessamentoService,
                        cFeEmitenteService,
                        cFeDestinatarioService,
                        cFeProdutoService)
                        .withIde(chaveCFe)
                        .withEmitente()
                        .withDestinatario()
                        .withItems()
                        .withICMSTotal()
                        .build();
                cupomFiscal.setUserCreate(userName);
                this.cFeService.save(cupomFiscal);
                break;
            }
        }

    }

    private TipoDocumento getTipoDocumento(Document document, XPath xpath) {
        if (null != XMLUtils.getObject(document, xpath, "//protNFe", XPathConstants.NODE)) {
            return TipoDocumento.NFE;
        }
        if (null != XMLUtils.getObject(document, xpath, "//CFe", XPathConstants.NODE)) {
            return TipoDocumento.CFE;
        }
        throw new RuntimeException("Tipo de Documento ainda não disponível para processamento");
    }
}
