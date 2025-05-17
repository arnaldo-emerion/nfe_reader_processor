package br.com.arcasoftware.sbs.builder;

import br.com.arcasoftware.sbs.enums.EnumException;
import br.com.arcasoftware.sbs.exception.ValidationException;
import br.com.arcasoftware.sbs.model.nfe.Destinatario;
import br.com.arcasoftware.sbs.model.nfe.Emitente;
import br.com.arcasoftware.sbs.model.nfe.ErroProcessamento;
import br.com.arcasoftware.sbs.model.nfe.NFe;
import br.com.arcasoftware.sbs.model.nfe.NFeCOFINS;
import br.com.arcasoftware.sbs.model.nfe.NFeICMS;
import br.com.arcasoftware.sbs.model.nfe.NFeIPI;
import br.com.arcasoftware.sbs.model.nfe.NFeItem;
import br.com.arcasoftware.sbs.model.nfe.NFePIS;
import br.com.arcasoftware.sbs.model.nfe.NFeTotalICMS;
import br.com.arcasoftware.sbs.model.nfe.Produto;
import br.com.arcasoftware.sbs.model.nfe.Transportadora;
import br.com.arcasoftware.sbs.service.DestinatarioService;
import br.com.arcasoftware.sbs.service.EmitenteService;
import br.com.arcasoftware.sbs.service.ErroProcessamentoService;
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
import java.util.Optional;

import static br.com.arcasoftware.sbs.utils.XMLUtils.*;


public class NFeBuilder {

    public static final String X_NOME = "xNome";
    private final Document document;
    private final XPath xpath;
    private final DestinatarioService destinatarioService;
    private final EmitenteService emitenteService;
    private final ProdutoService produtoService;
    private final TransportadoraService transportadoraService;
    private final String userName;
    private final ErroProcessamentoService erroProcessamentoService;

    private NFe nfe;

    public NFeBuilder(Document document, XPath xpath, DestinatarioService destinatarioService, EmitenteService emitenteService, ProdutoService produtoService,
                      TransportadoraService transportadoraService, String userName, ErroProcessamentoService erroProcessamentoService) {
        this.document = document;
        this.xpath = xpath;
        this.destinatarioService = destinatarioService;
        this.emitenteService = emitenteService;
        this.produtoService = produtoService;
        this.transportadoraService = transportadoraService;
        this.userName = userName;
        this.erroProcessamentoService = erroProcessamentoService;
    }

    public NFeBuilder comNFe() {
        Element xmlIDE = (Element) XMLUtils.getObject(document, xpath, "//infNFe/ide", XPathConstants.NODE);

        String dhEmi = extractTextValue(xmlIDE.getElementsByTagName("dhEmi"));
        String dEmi = extractTextValue(xmlIDE.getElementsByTagName("dEmi"));
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
                extractTextValue(xmlIDE.getElementsByTagName("cUF")),
                extractTextValue(xmlIDE.getElementsByTagName("cNF")),
                extractTextValue(xmlIDE.getElementsByTagName("natOp")),
                extractTextValue(xmlIDE.getElementsByTagName("nNF")),
                dataEmissao,
                extractTextValue(xmlIDE.getElementsByTagName("tpNF")),
                extractTextValue(xmlIDE.getElementsByTagName("cMunFG")),
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

        String cnpj = extractTextValue(xmlIDE.getElementsByTagName("CNPJ"));

        Optional<Transportadora> transportadoraFromDB = this.transportadoraService.getByUserCreateAndCnpj(userName, cnpj);

        if (transportadoraFromDB.isPresent()) {
            this.nfe.setTransportadora(transportadoraFromDB.get());
        } else {
            Transportadora transportadora = new Transportadora(
                    cnpj,
                    extractTextValue(xmlIDE.getElementsByTagName(X_NOME)),
                    extractTextValue(xmlIDE.getElementsByTagName("IE")),
                    extractTextValue(xmlIDE.getElementsByTagName("UF")),
                    extractTextValue(xmlIDE.getElementsByTagName("xMun")),
                    extractTextValue(xmlIDE.getElementsByTagName("xEnder"))
            );

            transportadora.setUserCreate(userName);
            Transportadora savedTransportadora;
            try {
                savedTransportadora = this.transportadoraService.save(transportadora);
            } catch (DataIntegrityViolationException ex) {
                this.erroProcessamentoService.save(new ErroProcessamento(userName, "Transportadora: " + ex.getMessage()));
                savedTransportadora = this.transportadoraService.getByUserCreateAndCnpj(userName, cnpj).orElseThrow(() -> new ValidationException(EnumException.TRANSPORTADORA_NOT_FOUND));
            }

            this.nfe.setTransportadora(savedTransportadora);
        }

        return this;
    }

    public NFeBuilder comEmitente() {
        Element xmlEmit = (Element) XMLUtils.getObject(document, xpath, "//infNFe/emit", XPathConstants.NODE);
        Element enderEmit = (Element) XMLUtils.getObject(document, xpath, "//infNFe/emit/enderEmit", XPathConstants.NODE);

        String cnpj = extractTextValue(xmlEmit.getElementsByTagName("CNPJ"));

        Optional<Emitente> emitenteFromDB = this.emitenteService.getByUserCreateAndCnpj(userName, cnpj);

        if (emitenteFromDB.isPresent()) {
            this.nfe.setEmitente(emitenteFromDB.get());
        } else {
            Emitente emitente = new Emitente(
                    cnpj,
                    extractTextValue(xmlEmit.getElementsByTagName(X_NOME)),
                    extractTextValue(xmlEmit.getElementsByTagName("xFant")),
                    extractTextValue(xmlEmit.getElementsByTagName("IE")),
                    extractIntegerValue(xmlEmit.getElementsByTagName("CRT")),
                    extractTextValue(enderEmit.getElementsByTagName("UF")),
                    extractTextValue(enderEmit.getElementsByTagName("xMun")),
                    extractTextValue(enderEmit.getElementsByTagName("xBairro")),
                    extractTextValue(enderEmit.getElementsByTagName("fone")),
                    extractTextValue(enderEmit.getElementsByTagName("xLgr")),
                    extractLongValue(enderEmit.getElementsByTagName("cPais")),
                    extractTextValue(enderEmit.getElementsByTagName("xPais")),
                    extractTextValue(enderEmit.getElementsByTagName("CEP"))
            );

            Emitente savedemitente;
            emitente.setUserCreate(userName);
            try {
                savedemitente = this.emitenteService.save(emitente);
            } catch (DataIntegrityViolationException ex) {
                this.erroProcessamentoService.save(new ErroProcessamento(userName, "Emitente: " + ex.getMessage()));
                savedemitente = this.emitenteService.getByUserCreateAndCnpj(userName, cnpj).orElseThrow(() -> new ValidationException(EnumException.EMITENTE_NOT_FOUND));
            }

            this.nfe.setEmitente(savedemitente);
        }

        return this;
    }

    public NFeBuilder comDestinatario() {
        Element xmlDest = (Element) XMLUtils.getObject(document, xpath, "//infNFe/dest", XPathConstants.NODE);
        Element enderDest = (Element) XMLUtils.getObject(document, xpath, "//infNFe/dest/enderDest", XPathConstants.NODE);

        String cnpj = extractTextValue(xmlDest.getElementsByTagName("CNPJ"));

        if (null == cnpj) {
            cnpj = extractTextValue(xmlDest.getElementsByTagName("CPF"));
        }

        Optional<Destinatario> destinatarioFromDB = this.destinatarioService.getByUserCreateAndEmitenteAndCnpj(userName, this.nfe.getEmitente(), cnpj);

        if (destinatarioFromDB.isPresent()) {
            nfe.setDestinatario(destinatarioFromDB.get());
        } else {
            Destinatario destinatario = new Destinatario(
                    cnpj,
                    extractTextValue(xmlDest.getElementsByTagName(X_NOME)),
                    extractTextValue(xmlDest.getElementsByTagName("IE")),
                    extractTextValue(enderDest.getElementsByTagName("UF")),
                    extractTextValue(enderDest.getElementsByTagName("xMun")),
                    extractTextValue(enderDest.getElementsByTagName("xBairro")),
                    extractTextValue(enderDest.getElementsByTagName("fone")),
                    extractTextValue(enderDest.getElementsByTagName("CEP")),
                    extractTextValue(enderDest.getElementsByTagName("xLgr")),
                    extractTextValue(enderDest.getElementsByTagName("nro")),
                    extractLongValue(enderDest.getElementsByTagName("cPais")),
                    extractTextValue(enderDest.getElementsByTagName("xPais")),
                    extractIntegerValue(xmlDest.getElementsByTagName("indIEDest")),
                    this.nfe.getEmitente()
            );

            Destinatario savedDestinatario;
            destinatario.setUserCreate(userName);
            try {
                savedDestinatario = this.destinatarioService.save(destinatario);
            } catch (DataIntegrityViolationException ex) {
                this.erroProcessamentoService.save(new ErroProcessamento(userName, "Destinatario: " + ex.getMessage()));
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
                extractDoubleValue(xmlEmit.getElementsByTagName("vBC")),
                extractDoubleValue(xmlEmit.getElementsByTagName("vICMS")),
                extractDoubleValue(xmlEmit.getElementsByTagName("vBCST")),
                extractDoubleValue(xmlEmit.getElementsByTagName("vST")),
                extractDoubleValue(xmlEmit.getElementsByTagName("vProd")),
                extractDoubleValue(xmlEmit.getElementsByTagName("vFrete")),
                extractDoubleValue(xmlEmit.getElementsByTagName("vSeg")),
                extractDoubleValue(xmlEmit.getElementsByTagName("vDesc")),
                extractDoubleValue(xmlEmit.getElementsByTagName("vII")),
                extractDoubleValue(xmlEmit.getElementsByTagName("vIPI")),
                extractDoubleValue(xmlEmit.getElementsByTagName("vPIS")),
                extractDoubleValue(xmlEmit.getElementsByTagName("vCOFINS")),
                extractDoubleValue(xmlEmit.getElementsByTagName("vOutro")),
                extractDoubleValue(xmlEmit.getElementsByTagName("vNF")),
                extractDoubleValue(xmlEmit.getElementsByTagName("vICMSDeson")),
                extractDoubleValue(xmlEmit.getElementsByTagName("vFCP")),
                extractDoubleValue(xmlEmit.getElementsByTagName("vICMSUFDest")),
                extractDoubleValue(xmlEmit.getElementsByTagName("vICMSUFRemet")),
                extractDoubleValue(xmlEmit.getElementsByTagName("vFCPSTRet")),
                extractDoubleValue(xmlEmit.getElementsByTagName("pFCPSTRet")),
                extractDoubleValue(xmlEmit.getElementsByTagName("vIPIDevol")),
                extractDoubleValue(xmlEmit.getElementsByTagName("vTotTrib"))

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

                String cProd = extractTextValue(item.getElementsByTagName("cProd"));

                Optional<Produto> produtoFromDB = this.produtoService.getByUserCreateAndCodigo(userName, cProd);

                Produto p;
                if (produtoFromDB.isPresent()) {
                    p = produtoFromDB.get();
                } else {
                    Produto produto = new Produto(
                            cProd,
                            extractTextValue(item.getElementsByTagName("cEAN")),
                            extractTextValue(item.getElementsByTagName("xProd")),
                            extractTextValue(item.getElementsByTagName("NCM")),
                            extractTextValue(item.getElementsByTagName("uCom")),
                            this.nfe.getEmitente()
                    );

                    Produto savedProduto;
                    produto.setUserCreate(userName);
                    try {
                        savedProduto = this.produtoService.save(produto);
                    } catch (DataIntegrityViolationException ex) {
                        this.erroProcessamentoService.save(new ErroProcessamento(userName, "Produto: " + ex.getMessage()));
                        savedProduto = this.produtoService.getByUserCreateAndCodigo(userName, cProd).orElseThrow(() -> new ValidationException(EnumException.EMITENTE_NOT_FOUND));
                    }

                    p = savedProduto;
                }

                Element tagImposto = (Element) item.getElementsByTagName("imposto").item(0).getChildNodes();
                NFeICMS nFeICMS = extractICMSForItem(null, tagImposto, userName);
                NFeIPI nFeIPI = extractIPIForItem(null, tagImposto, userName);
                NFePIS nFePIS = extractPISForItem(null, tagImposto, userName);
                NFeCOFINS nFeCOFINS = extractCOFINSForItem(null, tagImposto, userName);

                NFeItem nFeItem = new NFeItem(this.nfe,
                        p,
                        extractTextValue(item.getElementsByTagName("CFOP")),
                        extractTextValue(item.getElementsByTagName("uTrib")),
                        extractDoubleValue(item.getElementsByTagName("qTrib")),
                        extractDoubleValue(item.getElementsByTagName("vUnTrib")),
                        extractDoubleValue(item.getElementsByTagName("vProd")),
                        nItem,
                        nFeICMS,
                        nFeIPI,
                        nFePIS,
                        nFeCOFINS);
                nFeItem.setUserCreate(userName);

                if (null != nFeICMS) {
                    nFeICMS.setNFeItem(nFeItem);
                }
                if (null != nFeIPI) {
                    nFeIPI.setNFeItem(nFeItem);
                }
                if (null != nFePIS) {
                    nFePIS.setNFeItem(nFeItem);
                }
                if (null != nFeCOFINS) {
                    nFeCOFINS.setNFeItem(nFeItem);
                }

                this.nfe.getNFeItemList().add(nFeItem);
            }
        }
        return this;
    }

    private NFeICMS extractICMSForItem(NFeItem nFeItem, Element tagImposto, String userName) {
        Element nodeICMS = (Element) tagImposto.getElementsByTagName("ICMS").item(0);
        if (null == nodeICMS) return null;
        int orig = extractIntegerValue(nodeICMS.getElementsByTagName("orig"));
        String cst = extractTextValue(nodeICMS.getElementsByTagName("CST"));
        String csosn = extractTextValue(nodeICMS.getElementsByTagName("CSOSN"));
        int modBC = extractIntegerValue(nodeICMS.getElementsByTagName("modBC"));
        double vBC = extractDoubleValue(nodeICMS.getElementsByTagName("vBC"));
        double pICMS = extractDoubleValue(nodeICMS.getElementsByTagName("pICMS"));
        double vICMS = extractDoubleValue(nodeICMS.getElementsByTagName("vICMS"));
        double modBCST = extractDoubleValue(nodeICMS.getElementsByTagName("modBCST"));
        double pMVAST = extractDoubleValue(nodeICMS.getElementsByTagName("pMVAST"));
        double vBCST = extractDoubleValue(nodeICMS.getElementsByTagName("vBCST"));
        double pICMSST = extractDoubleValue(nodeICMS.getElementsByTagName("pICMSST"));
        double vICMSST = extractDoubleValue(nodeICMS.getElementsByTagName("vICMSST"));

        String cstReal = "";
        if (StringUtils.hasText(cst)) {
            cstReal = cst;
        } else {
            cstReal = csosn;
        }
        NFeICMS nFeICMS = new NFeICMS(nFeItem, orig, cstReal, modBC, vBC, pICMS, vICMS, modBCST, pMVAST, vBCST, pICMSST, vICMSST);
        nFeICMS.setUserCreate(userName);
        return nFeICMS;
    }

    private NFeIPI extractIPIForItem(NFeItem nFeItem, Element tagImposto, String userName) {
        Element nodeICMS = (Element) tagImposto.getElementsByTagName("IPI").item(0);
        if (null == nodeICMS) return null;
        int cEnq = extractIntegerValue(nodeICMS.getElementsByTagName("cEnq"));
        String cst = extractTextValue(nodeICMS.getElementsByTagName("CST"));
        double vBC = extractDoubleValue(nodeICMS.getElementsByTagName("vBC"));
        double pIPI = extractDoubleValue(nodeICMS.getElementsByTagName("pIPI"));
        double vIPI = extractDoubleValue(nodeICMS.getElementsByTagName("vIPI"));

        NFeIPI nFeIPI = new NFeIPI(nFeItem, cEnq, cst, vBC, pIPI, vIPI);
        nFeIPI.setUserCreate(userName);
        return nFeIPI;
    }

    private NFePIS extractPISForItem(NFeItem nFeItem, Element tagImposto, String userName) {
        Element nodeICMS = (Element) tagImposto.getElementsByTagName("PIS").item(0);
        if (null == nodeICMS) return null;
        String cst = extractTextValue(nodeICMS.getElementsByTagName("CST"));
        double vBC = extractDoubleValue(nodeICMS.getElementsByTagName("vBC"));
        double pPIS = extractDoubleValue(nodeICMS.getElementsByTagName("pPIS"));
        double vPIS = extractDoubleValue(nodeICMS.getElementsByTagName("vPIS"));

        NFePIS nFePIS = new NFePIS(nFeItem, cst, vBC, pPIS, vPIS);
        nFePIS.setUserCreate(userName);
        return nFePIS;
    }

    private NFeCOFINS extractCOFINSForItem(NFeItem nFeIteme, Element tagImposto, String userName) {
        Element nodeICMS = (Element) tagImposto.getElementsByTagName("COFINS").item(0);
        if (null == nodeICMS) return null;
        String cst = extractTextValue(nodeICMS.getElementsByTagName("CST"));
        double vBC = extractDoubleValue(nodeICMS.getElementsByTagName("vBC"));
        double pPIS = extractDoubleValue(nodeICMS.getElementsByTagName("pCOFINS"));
        double vPIS = extractDoubleValue(nodeICMS.getElementsByTagName("vCOFINS"));

        NFeCOFINS nFeCOFINS = new NFeCOFINS(nFeIteme, cst, vBC, pPIS, vPIS);
        nFeCOFINS.setUserCreate(userName);
        return nFeCOFINS;
    }

    public NFe build() {
        return this.nfe;
    }

}
