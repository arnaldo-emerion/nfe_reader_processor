package br.com.arcasoftware.sbs.service;

import br.com.arcasoftware.sbs.builder.NFeBuilder;
import br.com.arcasoftware.sbs.enums.EnumException;
import br.com.arcasoftware.sbs.exception.ValidationException;
import br.com.arcasoftware.sbs.model.HistoricoProcessamento;
import br.com.arcasoftware.sbs.model.nfe.NFe;
import br.com.arcasoftware.sbs.utils.XMLUtils;
import org.glassfish.jersey.internal.util.SimpleNamespaceResolver;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
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
public class NfeProcessor {

    private final DestinatarioService destinatarioService;
    private final EmitenteService emitenteService;
    private final ProdutoService produtoService;
    private final TransportadoraService transportadoraService;
    private final NFeService nFeService;
    private final HistoricoProcessamentoService historicoProcessamentoService;
    private final ErroProcessamentoService erroProcessamentoService;

    public NfeProcessor(DestinatarioService destinatarioService, EmitenteService emitenteService, ProdutoService produtoService,
                        TransportadoraService transportadoraService, NFeService nFeService, HistoricoProcessamentoService historicoProcessamentoService,
                        ErroProcessamentoService erroProcessamentoService) {
        this.destinatarioService = destinatarioService;
        this.emitenteService = emitenteService;
        this.produtoService = produtoService;
        this.transportadoraService = transportadoraService;
        this.nFeService = nFeService;
        this.historicoProcessamentoService = historicoProcessamentoService;
        this.erroProcessamentoService = erroProcessamentoService;
    }

    public void processNFe(InputStream file, String fileName, String userName) {
        NFe nfe;
        boolean processadaCorretamente = false;
        String motivo = null;
        try {
            nfe = this.process(file, userName);
            nfe.setUserCreate(userName);
            nfe.setFileName(fileName);
            this.nFeService.save(nfe);
            processadaCorretamente = true;
            motivo = "Nota fiscal processada corretamente";
        } catch (ValidationException | DataIntegrityViolationException ex) {
            motivo = ex.getMessage();
            throw ex;
        } finally {
            HistoricoProcessamento historicoProcessamento = new HistoricoProcessamento(fileName, Calendar.getInstance(), processadaCorretamente, motivo);
            historicoProcessamento.setUserCreate(userName);
            this.historicoProcessamentoService.save(historicoProcessamento);
        }
    }

    private NFe process(InputStream nfe, String userName) {
        DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();

        try {
            docFactory.setFeature("http://xml.org/sax/features/external-general-entities", false);
            docFactory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
        } catch (Exception ex) {
            throw new ValidationException(EnumException.INTERNAL_SERVER_ERROR);
        }

        DocumentBuilder docBuilder;
        Document document;
        try {
            docBuilder = docFactory.newDocumentBuilder();
            document = docBuilder.parse(nfe, "UTF-8");
        } catch (ParserConfigurationException | IOException | SAXException e) {
            throw new ValidationException(EnumException.INVALID_XML_PASSED_IN);
        }

        XPathFactory factory = XPathFactory.newInstance();
        XPath xpath = factory.newXPath();
        xpath.setNamespaceContext(new SimpleNamespaceResolver("xmlns", "http://www.portalfiscal.inf.br/nfe"));

        Object protocoloNode = XMLUtils.getObject(document, xpath, "//protNFe", XPathConstants.NODE);

        if (null == protocoloNode) {
            throw new ValidationException(EnumException.NFE_WITHOUT_PROTOCOL);
        }

        String chNFe = (String) XMLUtils.getObject(document, xpath, "//protNFe/infProt/chNFe", XPathConstants.STRING);

        List<NFe> byChNFe = this.nFeService.findByUserCreateAndChaveNFe(userName, chNFe);

        if (!byChNFe.isEmpty()) {
            this.nFeService.delete(byChNFe.get(0));
        }

        return new NFeBuilder(document, xpath, destinatarioService, emitenteService, produtoService, transportadoraService, userName, erroProcessamentoService)
                .comNFe()
                .comTransportadora()
                .comEmitente()
                .comDestinatario()
                .comItens()
                .comNFeTotalICMS()
                .build();
    }
}
