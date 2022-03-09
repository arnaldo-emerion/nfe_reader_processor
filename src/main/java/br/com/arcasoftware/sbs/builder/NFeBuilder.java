package br.com.arcasoftware.sbs.builder;

import br.com.arcasoftware.sbs.enums.EnumException;
import br.com.arcasoftware.sbs.exception.ValidationException;
import br.com.arcasoftware.sbs.model.nfe.*;
import br.com.arcasoftware.sbs.service.DestinatarioService;
import br.com.arcasoftware.sbs.service.EmitenteService;
import br.com.arcasoftware.sbs.service.ProdutoService;
import br.com.arcasoftware.sbs.service.TransportadoraService;
import br.com.arcasoftware.sbs.utils.XMLUtils;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.util.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;


public class NFeBuilder {

    public static final String X_NOME = "xNome";
    private final Document document;
    private final XPath xpath;
    private final DestinatarioService destinatarioService;
    private final EmitenteService emitenteService;
    private final ProdutoService produtoService;
    private final TransportadoraService transportadoraService;
    private final String userName;

    private NFe nfe;
    private List<Produto> allItemsForThisEmitenteAndUser;

    public NFeBuilder(Document document, XPath xpath, DestinatarioService destinatarioService, EmitenteService emitenteService, ProdutoService produtoService,
                      TransportadoraService transportadoraService, String userName) {
        this.document = document;
        this.xpath = xpath;
        this.destinatarioService = destinatarioService;
        this.emitenteService = emitenteService;
        this.produtoService = produtoService;
        this.transportadoraService = transportadoraService;
        this.userName = userName;
    }

    public NFeBuilder comNFe() {
        Element xmlIDE = (Element) XMLUtils.getObject(document, xpath, "//infNFe/ide", XPathConstants.NODE);

        String dhEmi = XMLUtils.extractTextValue(xmlIDE.getElementsByTagName("dhEmi"));
        String dEmi = XMLUtils.extractTextValue(xmlIDE.getElementsByTagName("dEmi"));
        String dataEmissaoString = dhEmi != null ? dhEmi : dEmi;
        String formato = dataEmissaoString.contains("T") ? "yyyy-MM-dd'T'HH:mm:ssX" : "yyyy-MM-dd";
        Date dataEmissao;
        try {
            dataEmissao = new SimpleDateFormat(formato).parse(dataEmissaoString);
        } catch (ParseException e) {
            throw new ValidationException(EnumException.FORMATO_DE_DATA_NAO_RECONHECIDO);
        }

        String infAdicional = (String) XMLUtils.getObject(document, xpath, "//infNFe/infAdic/infCpl", XPathConstants.STRING);
        String chaveNotaFiscal = (String) XMLUtils.getObject(document, xpath, "//protNFe/infProt/chNFe", XPathConstants.STRING);

        this.nfe = new NFe(
                XMLUtils.extractTextValue(xmlIDE.getElementsByTagName("cUF")),
                XMLUtils.extractTextValue(xmlIDE.getElementsByTagName("cNF")),
                XMLUtils.extractTextValue(xmlIDE.getElementsByTagName("natOp")),
                XMLUtils.extractTextValue(xmlIDE.getElementsByTagName("nNF")),
                dataEmissao,
                XMLUtils.extractTextValue(xmlIDE.getElementsByTagName("tpNF")),
                XMLUtils.extractTextValue(xmlIDE.getElementsByTagName("cMunFG")),
                chaveNotaFiscal,
                infAdicional,
                null,
                null,
                null,
                null,
                null,
                null
        );

        return this;
    }

    public NFeBuilder comTransportadora() {
        Element xmlIDE = (Element) XMLUtils.getObject(document, xpath, "//infNFe/transp/transporta", XPathConstants.NODE);

        if (null == xmlIDE) {
            return this;
        }

        String cnpj = XMLUtils.extractTextValue(xmlIDE.getElementsByTagName("CNPJ"));

        Optional<Transportadora> transportadoraFromDB = this.transportadoraService.getByUserCreateAndCnpj(userName, cnpj);

        if (transportadoraFromDB.isPresent()) {
            this.nfe.setTransportadora(transportadoraFromDB.get());
        } else {
            Transportadora transportadora = new Transportadora(
                    cnpj,
                    XMLUtils.extractTextValue(xmlIDE.getElementsByTagName(X_NOME)),
                    XMLUtils.extractTextValue(xmlIDE.getElementsByTagName("IE")),
                    XMLUtils.extractTextValue(xmlIDE.getElementsByTagName("UF")),
                    XMLUtils.extractTextValue(xmlIDE.getElementsByTagName("xMun")),
                    XMLUtils.extractTextValue(xmlIDE.getElementsByTagName("xEnder"))
            );

            transportadora.setUserCreate(userName);
            Transportadora savedTransportadora;
            try {
                savedTransportadora = this.transportadoraService.save(transportadora);
            } catch (DataIntegrityViolationException ex) {
                savedTransportadora = this.transportadoraService.getByUserCreateAndCnpj(userName, cnpj).orElseThrow(() -> new ValidationException(EnumException.TRANSPORTADORA_NOT_FOUND));
            }

            this.nfe.setTransportadora(savedTransportadora);
        }

        return this;
    }

    public NFeBuilder comEmitente() {
        Element xmlEmit = (Element) XMLUtils.getObject(document, xpath, "//infNFe/emit", XPathConstants.NODE);
        Element enderEmit = (Element) XMLUtils.getObject(document, xpath, "//infNFe/emit/enderEmit", XPathConstants.NODE);

        String cnpj = XMLUtils.extractTextValue(xmlEmit.getElementsByTagName("CNPJ"));

        Optional<Emitente> emitenteFromDB = this.emitenteService.getByUserCreateAndCnpj(userName, cnpj);

        if (emitenteFromDB.isPresent()) {
            this.nfe.setEmitente(emitenteFromDB.get());
        } else {
            Emitente emitente = new Emitente(
                    cnpj,
                    XMLUtils.extractTextValue(xmlEmit.getElementsByTagName(X_NOME)),
                    XMLUtils.extractTextValue(xmlEmit.getElementsByTagName("xFant")),
                    XMLUtils.extractTextValue(xmlEmit.getElementsByTagName("IE")),
                    XMLUtils.extractTextValue(xmlEmit.getElementsByTagName("CRT")),
                    XMLUtils.extractTextValue(enderEmit.getElementsByTagName("UF")),
                    XMLUtils.extractTextValue(enderEmit.getElementsByTagName("xMun")),
                    XMLUtils.extractTextValue(enderEmit.getElementsByTagName("xBairro")),
                    XMLUtils.extractTextValue(enderEmit.getElementsByTagName("fone")),
                    XMLUtils.extractTextValue(enderEmit.getElementsByTagName("xLgr")),
                    XMLUtils.extractTextValue(enderEmit.getElementsByTagName("cPais")),
                    XMLUtils.extractTextValue(enderEmit.getElementsByTagName("xPais")),
                    XMLUtils.extractTextValue(enderEmit.getElementsByTagName("CEP"))
            );

            Emitente savedemitente;
            emitente.setUserCreate(userName);
            try {
                savedemitente = this.emitenteService.save(emitente);
            } catch (DataIntegrityViolationException ex) {
                savedemitente = this.emitenteService.getByUserCreateAndCnpj(userName, cnpj).orElseThrow(() -> new ValidationException(EnumException.EMITENTE_NOT_FOUND));
            }

            this.nfe.setEmitente(savedemitente);
        }

        allItemsForThisEmitenteAndUser = this.produtoService.getByUserCreateAndEmitente(userName, nfe.getEmitente());

        return this;
    }

    public NFeBuilder comDestinatario() {
        Element xmlDest = (Element) XMLUtils.getObject(document, xpath, "//infNFe/dest", XPathConstants.NODE);
        Element enderDest = (Element) XMLUtils.getObject(document, xpath, "//infNFe/dest/enderDest", XPathConstants.NODE);

        String cnpj = XMLUtils.extractTextValue(xmlDest.getElementsByTagName("CNPJ"));

        if (null == cnpj) {
            cnpj = XMLUtils.extractTextValue(xmlDest.getElementsByTagName("CPF"));
        }

        Optional<Destinatario> destinatarioFromDB = this.destinatarioService.getByUserCreateAndEmitenteAndCnpj(userName, this.nfe.getEmitente(), cnpj);

        if (destinatarioFromDB.isPresent()) {
            nfe.setDestinatario(destinatarioFromDB.get());
        } else {
            Destinatario destinatario = new Destinatario(
                    cnpj,
                    XMLUtils.extractTextValue(xmlDest.getElementsByTagName(X_NOME)),
                    XMLUtils.extractTextValue(xmlDest.getElementsByTagName("IE")),
                    XMLUtils.extractTextValue(enderDest.getElementsByTagName("UF")),
                    XMLUtils.extractTextValue(enderDest.getElementsByTagName("xMun")),
                    XMLUtils.extractTextValue(enderDest.getElementsByTagName("xBairro")),
                    XMLUtils.extractTextValue(enderDest.getElementsByTagName("fone")),
                    XMLUtils.extractTextValue(enderDest.getElementsByTagName("CEP")),
                    XMLUtils.extractTextValue(enderDest.getElementsByTagName("xLgr")),
                    XMLUtils.extractTextValue(enderDest.getElementsByTagName("nro")),
                    XMLUtils.extractTextValue(enderDest.getElementsByTagName("cPais")),
                    XMLUtils.extractTextValue(enderDest.getElementsByTagName("xPais")),
                    XMLUtils.extractTextValue(xmlDest.getElementsByTagName("indIEDest")),
                    this.nfe.getEmitente()
            );

            Destinatario savedDestinatario;
            destinatario.setUserCreate(userName);
            try {
                savedDestinatario = this.destinatarioService.save(destinatario);
            } catch (DataIntegrityViolationException ex) {
                savedDestinatario = this.destinatarioService.getByUserCreateAndCnpj(userName, cnpj).orElseThrow(() -> new ValidationException(EnumException.DESTINATARIO_NAO_ENCONTRADO));
            }

            this.nfe.setDestinatario(savedDestinatario);
        }

        return this;
    }

    public NFeBuilder comNFeTotalICMS() {
        Element xmlEmit = (Element) XMLUtils.getObject(document, xpath, "//infNFe/total/ICMSTot", XPathConstants.NODE);

        NFeTotalICMS nFeTotalICMS = new NFeTotalICMS(
                this.nfe,
                XMLUtils.extractDoubleValue(xmlEmit.getElementsByTagName("vBC")),
                XMLUtils.extractDoubleValue(xmlEmit.getElementsByTagName("vICMS")),
                XMLUtils.extractDoubleValue(xmlEmit.getElementsByTagName("vBCST")),
                XMLUtils.extractDoubleValue(xmlEmit.getElementsByTagName("vST")),
                XMLUtils.extractDoubleValue(xmlEmit.getElementsByTagName("vProd")),
                XMLUtils.extractDoubleValue(xmlEmit.getElementsByTagName("vFrete")),
                XMLUtils.extractDoubleValue(xmlEmit.getElementsByTagName("vSeg")),
                XMLUtils.extractDoubleValue(xmlEmit.getElementsByTagName("vDesc")),
                XMLUtils.extractDoubleValue(xmlEmit.getElementsByTagName("vII")),
                XMLUtils.extractDoubleValue(xmlEmit.getElementsByTagName("vIPI")),
                XMLUtils.extractDoubleValue(xmlEmit.getElementsByTagName("vPIS")),
                XMLUtils.extractDoubleValue(xmlEmit.getElementsByTagName("vCOFINS")),
                XMLUtils.extractDoubleValue(xmlEmit.getElementsByTagName("vOutro")),
                XMLUtils.extractDoubleValue(xmlEmit.getElementsByTagName("vNF")),
                XMLUtils.extractDoubleValue(xmlEmit.getElementsByTagName("vICMSDeson")),
                XMLUtils.extractDoubleValue(xmlEmit.getElementsByTagName("vFCP")),
                XMLUtils.extractDoubleValue(xmlEmit.getElementsByTagName("vICMSUFDest")),
                XMLUtils.extractDoubleValue(xmlEmit.getElementsByTagName("vICMSUFRemet")),
                XMLUtils.extractDoubleValue(xmlEmit.getElementsByTagName("vFCPSTRet")),
                XMLUtils.extractDoubleValue(xmlEmit.getElementsByTagName("pFCPSTRet")),
                XMLUtils.extractDoubleValue(xmlEmit.getElementsByTagName("vIPIDevol")),
                XMLUtils.extractDoubleValue(xmlEmit.getElementsByTagName("vTotTrib"))

        );

        nFeTotalICMS.setUserCreate(userName);
        this.nfe.setNFeTotalICMS(nFeTotalICMS);

        return this;
    }

    public NFeBuilder comItens() {
        NodeList nodeListItens = (NodeList) XMLUtils.getObject(document, xpath, "//infNFe/det", XPathConstants.NODESET);
        if (nodeListItens.getLength() > 0) {
            this.nfe.setNFeItemList(new ArrayList<>());

            for (int i = 0; i < nodeListItens.getLength(); i++) {
                Element item = (Element) nodeListItens.item(i);

                int nItem = 0;
                String nItem1FromAttr = item.getAttribute("nItem");

                if (StringUtils.hasText(nItem1FromAttr)) {
                    nItem = Integer.parseInt(nItem1FromAttr);
                }

                String cProd = XMLUtils.extractTextValue(item.getElementsByTagName("cProd"));

                Optional<Produto> produtoFromDB = this.allItemsForThisEmitenteAndUser.stream().filter(p -> p.getCodigo().equals(cProd)).findAny();

                Produto p;
                if (produtoFromDB.isPresent()) {
                    p = produtoFromDB.get();
                } else {
                    Produto produto = new Produto(
                            cProd,
                            XMLUtils.extractTextValue(item.getElementsByTagName("cEAN")),
                            XMLUtils.extractTextValue(item.getElementsByTagName("xProd")),
                            XMLUtils.extractTextValue(item.getElementsByTagName("NCM")),
                            XMLUtils.extractTextValue(item.getElementsByTagName("uCom")),
                            this.nfe.getEmitente()
                    );

                    Produto savedProduto;
                    produto.setUserCreate(userName);
                    try {
                        savedProduto = this.produtoService.save(produto);
                    } catch (DataIntegrityViolationException ex) {
                        savedProduto = this.produtoService.getByUserCreateAndCodigo(userName, cProd).orElseThrow(() -> new ValidationException(EnumException.EMITENTE_NOT_FOUND));
                    }

                    p = savedProduto;
                }

                NFeItem nFeItem = new NFeItem(this.nfe,
                        p,
                        XMLUtils.extractTextValue(item.getElementsByTagName("CFOP")),
                        XMLUtils.extractTextValue(item.getElementsByTagName("uTrib")),
                        XMLUtils.extractDoubleValue(item.getElementsByTagName("qTrib")),
                        XMLUtils.extractDoubleValue(item.getElementsByTagName("vUnTrib")),
                        XMLUtils.extractDoubleValue(item.getElementsByTagName("vProd")),
                        nItem);
                nFeItem.setUserCreate(userName);
                this.nfe.getNFeItemList().add(nFeItem);
            }
        }
        return this;
    }

    public NFe build() {
        return this.nfe;
    }

}
