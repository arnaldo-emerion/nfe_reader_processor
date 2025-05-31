package br.com.arcasoftware.sbs.builder;

import br.com.arcasoftware.sbs.enums.EnumEstados;
import br.com.arcasoftware.sbs.enums.EnumException;
import br.com.arcasoftware.sbs.exception.ValidationException;
import br.com.arcasoftware.sbs.model.cupomfiscal.CFeCofins;
import br.com.arcasoftware.sbs.model.cupomfiscal.CFeDestinatario;
import br.com.arcasoftware.sbs.model.cupomfiscal.CFeEmitente;
import br.com.arcasoftware.sbs.model.cupomfiscal.CFeIcms;
import br.com.arcasoftware.sbs.model.cupomfiscal.CFeIcmsTotal;
import br.com.arcasoftware.sbs.model.cupomfiscal.CFeItem;
import br.com.arcasoftware.sbs.model.cupomfiscal.CFePis;
import br.com.arcasoftware.sbs.model.cupomfiscal.CFeProduto;
import br.com.arcasoftware.sbs.model.cupomfiscal.CupomFiscal;
import br.com.arcasoftware.sbs.model.nfe.ErroProcessamento;
import br.com.arcasoftware.sbs.service.CFeDestinatarioService;
import br.com.arcasoftware.sbs.service.CFeEmitenteService;
import br.com.arcasoftware.sbs.service.CFeProdutoService;
import br.com.arcasoftware.sbs.service.ErroProcessamentoService;
import br.com.arcasoftware.sbs.utils.XMLUtils;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.util.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Optional;

import static br.com.arcasoftware.sbs.utils.XMLUtils.extractDoubleValue;
import static br.com.arcasoftware.sbs.utils.XMLUtils.extractIntegerValue;
import static br.com.arcasoftware.sbs.utils.XMLUtils.extractTextValue;

public class CFeBuilder {
    private final Document document;
    private final XPath xpath;
    private final String userName;
    private final ErroProcessamentoService erroProcessamentoService;
    private final CFeEmitenteService cFeEmitenteService;
    private final CFeDestinatarioService cFeDestinatarioService;
    private final CFeProdutoService cFeProdutoService;
    private CupomFiscal cupomFiscal;

    public CFeBuilder(Document document,
                      XPath xpath,
                      String userName,
                      ErroProcessamentoService erroProcessamentoService,
                      CFeEmitenteService cFeEmitenteService,
                      CFeDestinatarioService cFeDestinatarioService,
                      CFeProdutoService cFeProdutoService) {
        this.document = document;
        this.xpath = xpath;
        this.userName = userName;
        this.erroProcessamentoService = erroProcessamentoService;
        this.cFeEmitenteService = cFeEmitenteService;
        this.cFeDestinatarioService = cFeDestinatarioService;
        this.cFeProdutoService = cFeProdutoService;
    }

    public CFeBuilder withIde(String chaveCFe) {
        Element xmlIDE = (Element) XMLUtils.getObject(document, xpath, "//infCFe/ide", XPathConstants.NODE);
        String cUFCodigo = extractTextValue(xmlIDE.getElementsByTagName("cUF"));
        String cNF = extractTextValue(xmlIDE.getElementsByTagName("cNF"));
        String nCFe = extractTextValue(xmlIDE.getElementsByTagName("nCFe"));
        String dEmi = extractTextValue(xmlIDE.getElementsByTagName("dEmi"));
        String hEmi = extractTextValue(xmlIDE.getElementsByTagName("hEmi"));
        String cnpj = extractTextValue(xmlIDE.getElementsByTagName("CNPJ"));

        Instant dataEmissao = LocalDateTime.parse(dEmi.concat(hEmi), DateTimeFormatter.ofPattern("yyyyMMddHHmmss"))
                .atZone(ZoneId.of("America/Sao_Paulo")).toInstant();

        String cUF = EnumEstados.getSiglaFromCodigo(Integer.parseInt(cUFCodigo));

        CupomFiscal cupomFiscal = new CupomFiscal();
        cupomFiscal.setUf(cUF);
        cupomFiscal.setNf(cNF);
        cupomFiscal.setNumCupomFiscal(nCFe);
        cupomFiscal.setDataEmissao(dataEmissao);
        cupomFiscal.setCnpj(cnpj);
        cupomFiscal.setChaveCFe(chaveCFe);
        cupomFiscal.setUserCreate(userName);
        cupomFiscal.setNatOperacao("VENDA");
        this.cupomFiscal = cupomFiscal;
        return this;
    }

    public CFeBuilder withEmitente() {
        Element xmlEmit = (Element) XMLUtils.getObject(document, xpath, "//infCFe/emit", XPathConstants.NODE);
        Element enderEmit = (Element) XMLUtils.getObject(document, xpath, "//infCFe/emit/enderEmit", XPathConstants.NODE);

        String cnpj = extractTextValue(xmlEmit.getElementsByTagName("CNPJ"));
        Optional<CFeEmitente> emitenteFromDB = this.cFeEmitenteService.getByUserCreateAndCnpj(userName, cnpj);

        if (emitenteFromDB.isPresent()) {
            this.cupomFiscal.setEmitente(emitenteFromDB.get());
        } else {
            String razaoSocial = extractTextValue(xmlEmit.getElementsByTagName("xNome"));
            String nomeFantasia = extractTextValue(xmlEmit.getElementsByTagName("xFant"));
            String logradouro = extractTextValue(enderEmit.getElementsByTagName("xLgr"));
            String numero = extractTextValue(enderEmit.getElementsByTagName("nro"));
            String cpl = extractTextValue(enderEmit.getElementsByTagName("xCpl"));
            String bairro = extractTextValue(enderEmit.getElementsByTagName("xBairro"));
            String municipio = extractTextValue(enderEmit.getElementsByTagName("xMun"));
            String cep = extractTextValue(enderEmit.getElementsByTagName("CEP"));
            String ie = extractTextValue(xmlEmit.getElementsByTagName("IE"));
            int regimeTRibutario = extractIntegerValue(xmlEmit.getElementsByTagName("cRegTrib"));
            String indRatISSQN = extractTextValue(xmlEmit.getElementsByTagName("indRatISSQN"));
            CFeEmitente cFeEmitente = new CFeEmitente();
            cFeEmitente.setCnpj(cnpj);
            cFeEmitente.setRazaoSocial(razaoSocial);
            cFeEmitente.setNomeFantasia(nomeFantasia);
            cFeEmitente.setLogradouro(logradouro);
            cFeEmitente.setNumero(numero);
            cFeEmitente.setCpl(cpl);
            cFeEmitente.setBairro(bairro);
            cFeEmitente.setMunicipio(municipio);
            cFeEmitente.setCep(cep);
            cFeEmitente.setIe(ie);
            cFeEmitente.setRegimeTributario(regimeTRibutario);
            cFeEmitente.setIndRatISSQN(indRatISSQN);
            cFeEmitente.setUserCreate(userName);

            CFeEmitente savedEmitente;
            try {
                synchronized (this) {
                    savedEmitente = this.cFeEmitenteService.save(cFeEmitente);
                }
            } catch (DataIntegrityViolationException ex) {
                this.erroProcessamentoService.save(new ErroProcessamento(userName, "Emitente: " + ex.getMessage()));
                savedEmitente = this.cFeEmitenteService.getByUserCreateAndCnpj(userName, cnpj).orElseThrow(() -> new ValidationException(EnumException.EMITENTE_NOT_FOUND));
            }

            this.cupomFiscal.setEmitente(savedEmitente);
        }
        return this;
    }

    public CFeBuilder withDestinatario() {
        Element xmlDest = (Element) XMLUtils.getObject(document, xpath, "//infCFe/dest", XPathConstants.NODE);
        String cpf = extractTextValue(xmlDest.getElementsByTagName("CPF"));

        if (!StringUtils.hasText(cpf)) return this;

        Optional<CFeDestinatario> destinatarioFromDB = this.cFeDestinatarioService.getByUserCreateAndCpf(userName, cpf);

        if (destinatarioFromDB.isPresent()) {
            this.cupomFiscal.setDestinatario(destinatarioFromDB.get());
        } else {
            CFeDestinatario cFeDestinatario = new CFeDestinatario();
            cFeDestinatario.setCpf(cpf);
            cFeDestinatario.setUserCreate(userName);

            CFeDestinatario savedDestinatario;
            try {
                synchronized (this) {
                    savedDestinatario = this.cFeDestinatarioService.save(cFeDestinatario);
                }
            } catch (DataIntegrityViolationException ex) {
                this.erroProcessamentoService.save(new ErroProcessamento(userName, "Destinatario: " + ex.getMessage()));
                savedDestinatario = this.cFeDestinatarioService.getByUserCreateAndCpf(userName, cpf).orElseThrow(() -> new ValidationException(EnumException.DESTINATARIO_NAO_ENCONTRADO));
            }

            this.cupomFiscal.setDestinatario(savedDestinatario);
        }
        return this;
    }

    public CFeBuilder withItems() {
        NodeList nodeListItens = (NodeList) XMLUtils.getObject(document, xpath, "//infCFe/det", XPathConstants.NODESET);
        if (nodeListItens.getLength() > 0) {
            this.cupomFiscal.setCFeItemList(new ArrayList<>());
            for (int i = 0; i < nodeListItens.getLength(); i++) {
                Element item = (Element) nodeListItens.item(i);

                int nItem = 0;
                String nItem1FromAttr = item.getAttribute("nItem");

                if (StringUtils.hasText(nItem1FromAttr)) {
                    nItem = Integer.parseInt(nItem1FromAttr);
                }

                String cProd = extractTextValue(item.getElementsByTagName("cProd"));

                Optional<CFeProduto> produtoFromDB = this.cFeProdutoService.getByUserCreateAndCodigo(userName, cProd);

                CFeProduto p;
                if (produtoFromDB.isPresent()) {
                    p = produtoFromDB.get();
                } else {
                    CFeProduto produto = new CFeProduto(
                            0L, userName,
                            cProd,
                            extractTextValue(item.getElementsByTagName("xProd")),
                            extractTextValue(item.getElementsByTagName("NCM")),
                            cupomFiscal.getEmitente().getCnpj()
                    );

                    CFeProduto savedProduto;
                    try {
                        synchronized (this) {
                            savedProduto = this.cFeProdutoService.save(produto);
                        }
                    } catch (DataIntegrityViolationException ex) {
                        this.erroProcessamentoService.save(new ErroProcessamento(userName, "Produto: " + ex.getMessage()));
                        savedProduto = this.cFeProdutoService.getByUserCreateAndCodigo(userName, cProd).orElseThrow(() -> new ValidationException(EnumException.EMITENTE_NOT_FOUND));
                    }

                    p = savedProduto;
                }

                CFeItem cFeItem = new CFeItem();
                cFeItem.setCFeProduto(p);
                cFeItem.setCfop(extractTextValue(item.getElementsByTagName("CFOP")));
                cFeItem.setUnidade(extractTextValue(item.getElementsByTagName("uCom")));
                cFeItem.setQuantidade(extractDoubleValue(item.getElementsByTagName("qCom")));
                cFeItem.setValorUnitario(extractDoubleValue(item.getElementsByTagName("vUnCom")));
                cFeItem.setValorProduto(extractDoubleValue(item.getElementsByTagName("vProd")));
                cFeItem.setIndRegra(extractTextValue(item.getElementsByTagName("indRegra")));
                cFeItem.setDesconto(extractDoubleValue(item.getElementsByTagName("vDesc")));
                cFeItem.setValorUnitario(extractDoubleValue(item.getElementsByTagName("vOutro")));
                cFeItem.setValorTotalItem(extractDoubleValue(item.getElementsByTagName("vItem")));

                Element tagImposto = (Element) item.getElementsByTagName("imposto").item(0).getChildNodes();
                cFeItem.setCFeIcms(extractICMSForItem(tagImposto, cFeItem));
                cFeItem.setCFePis(extractPISForItem(tagImposto, cFeItem));
                cFeItem.setCFeCofins(extractCofinsForItem(tagImposto, cFeItem));

                cFeItem.setCupomFiscal(this.cupomFiscal);
                cFeItem.setUserCreate(userName);

                this.cupomFiscal.getCFeItemList().add(cFeItem);
            }
        }

        return this;
    }

    public CFeBuilder withICMSTotal() {
        Element xmlICMSTot = (Element) XMLUtils.getObject(document, xpath, "//infCFe/total/ICMSTot", XPathConstants.NODE);
        Element total = (Element) XMLUtils.getObject(document, xpath, "//infCFe/total", XPathConstants.NODE);

        CFeIcmsTotal cFeIcmsTotal = new CFeIcmsTotal();
        cFeIcmsTotal.setVIcms(extractDoubleValue(xmlICMSTot.getElementsByTagName("vICMS")));
        cFeIcmsTotal.setVProd(extractDoubleValue(xmlICMSTot.getElementsByTagName("vProd")));
        cFeIcmsTotal.setVDesc(extractDoubleValue(xmlICMSTot.getElementsByTagName("vDesc")));
        cFeIcmsTotal.setVPis(extractDoubleValue(xmlICMSTot.getElementsByTagName("vPIS")));
        cFeIcmsTotal.setVCofins(extractDoubleValue(xmlICMSTot.getElementsByTagName("vCOFINS")));
        cFeIcmsTotal.setVPisST(extractDoubleValue(xmlICMSTot.getElementsByTagName("vPISST")));
        cFeIcmsTotal.setVCofinsST(extractDoubleValue(xmlICMSTot.getElementsByTagName("vCOFINSST")));
        cFeIcmsTotal.setVOutro(extractDoubleValue(xmlICMSTot.getElementsByTagName("vOutro")));
        cFeIcmsTotal.setVCFe(extractDoubleValue(total.getElementsByTagName("vCFe")));
        cFeIcmsTotal.setUserCreate(userName);
        cFeIcmsTotal.setCupomFiscal(this.cupomFiscal);

        this.cupomFiscal.setCFeIcmsTotal(cFeIcmsTotal);
        return this;
    }

    public CupomFiscal build() {
        return this.cupomFiscal;
    }

    private CFeIcms extractICMSForItem(Element tagImposto, CFeItem cFeItem) {
        Element node = (Element) tagImposto.getElementsByTagName("ICMS").item(0);

        CFeIcms cFeIcms = new CFeIcms();
        cFeIcms.setOrigem(extractIntegerValue(node.getElementsByTagName("Orig")));
        cFeIcms.setCst(extractTextValue(node.getElementsByTagName("CST")));
        cFeIcms.setPercentual(extractDoubleValue(node.getElementsByTagName("pICMS")));
        cFeIcms.setValor(extractDoubleValue(node.getElementsByTagName("vICMS")));
        cFeIcms.setUserCreate(userName);
        cFeIcms.setCFeItem(cFeItem);
        return cFeIcms;
    }

    private CFePis extractPISForItem(Element tagImposto, CFeItem cFeItem) {
        Element node = (Element) tagImposto.getElementsByTagName("PIS").item(0);

        CFePis cFePis = new CFePis();
        cFePis.setCst(extractTextValue(node.getElementsByTagName("CST")));
        cFePis.setPercentual(extractDoubleValue(node.getElementsByTagName("pICMS")));
        cFePis.setValor(extractDoubleValue(node.getElementsByTagName("vICMS")));
        cFePis.setUserCreate(userName);
        cFePis.setCFeItem(cFeItem);
        return cFePis;
    }

    private CFeCofins extractCofinsForItem(Element tagImposto, CFeItem cFeItem) {
        Element node = (Element) tagImposto.getElementsByTagName("COFINS").item(0);

        CFeCofins cFeCofins = new CFeCofins();
        cFeCofins.setCst(extractTextValue(node.getElementsByTagName("CST")));
        cFeCofins.setPercentual(extractDoubleValue(node.getElementsByTagName("pICMS")));
        cFeCofins.setValor(extractDoubleValue(node.getElementsByTagName("vICMS")));
        cFeCofins.setUserCreate(userName);
        cFeCofins.setCFeItem(cFeItem);
        return cFeCofins;
    }
}
