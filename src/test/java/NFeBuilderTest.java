import br.com.arcasoftware.sbs.builder.NFeBuilder;
import br.com.arcasoftware.sbs.enums.EnumException;
import br.com.arcasoftware.sbs.exception.ValidationException;
import br.com.arcasoftware.sbs.model.nfe.NFe;
import br.com.arcasoftware.sbs.model.nfe.NFeCOFINS;
import br.com.arcasoftware.sbs.model.nfe.NFeICMS;
import br.com.arcasoftware.sbs.model.nfe.NFeIPI;
import br.com.arcasoftware.sbs.model.nfe.NFePIS;
import br.com.arcasoftware.sbs.service.DestinatarioService;
import br.com.arcasoftware.sbs.service.EmitenteService;
import br.com.arcasoftware.sbs.service.ErroProcessamentoService;
import br.com.arcasoftware.sbs.service.ProdutoService;
import br.com.arcasoftware.sbs.service.TransportadoraService;
import org.glassfish.jersey.internal.util.SimpleNamespaceResolver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;

class NFeBuilderTest {

    private Document document;
    private XPath xpath;
    private DestinatarioService destinatarioService;
    private EmitenteService emitenteService;
    private ProdutoService produtoService;
    private TransportadoraService transportadoraService;
    private ErroProcessamentoService erroProcessamentoService;
    private Path rootPath;

    @BeforeEach
    void setUp() {
        this.destinatarioService = Mockito.mock(DestinatarioService.class);
        this.emitenteService = Mockito.mock(EmitenteService.class);
        this.produtoService = Mockito.mock(ProdutoService.class);
        this.transportadoraService = Mockito.mock(TransportadoraService.class);
        this.erroProcessamentoService = Mockito.mock(ErroProcessamentoService.class);

        XPathFactory factory = XPathFactory.newInstance();
        this.xpath = factory.newXPath();
        xpath.setNamespaceContext(new SimpleNamespaceResolver("xmlns", "http://www.portalfiscal.inf.br/nfe"));

        rootPath = Paths.get("src", "test", "resources");
    }

    @Test
    void comNFe() {
        Document doc = this.getDocument("xml/111011-35211107022495000107550010001110111560552211.xml");
        NFeBuilder nFeBuilder = getnFeBuilder(doc);
        NFe nFe = nFeBuilder.comNFe().build();

        assertEquals("35", nFe.getCUF());
        assertEquals("56055221", nFe.getCNF());
        assertEquals("VENDA", nFe.getNatOperacao());
        assertEquals("111011", nFe.getNNF());
        //assertEquals("2021-11-19T09:53:30-03:00", nFe.getDataEmissao());
        assertEquals("1", nFe.getTpNf());
        assertEquals("3550308", nFe.getCMunFG());
        assertEquals("35211107022495000107550010001110111560552211", nFe.getChaveNFe());
        assertEquals("Informacao adicional da nota fiscal", nFe.getInfoAdicional());

    }

    @Test
    void comTransportadora() {
    }

    @Test
    void comEmitente() {
    }

    @Test
    void comDestinatario() {
    }

    @Test
    void comNFeTotalICMS() {
    }

    @Test
    void comItensICMSTagShoulBeFulfiled() {
        Document doc = this.getDocument("xml/111011-35211107022495000107550010001110111560552211.xml");
        NFeBuilder nFeBuilder = getnFeBuilder(doc);
        NFe nFe = nFeBuilder.comNFe().comItens().build();
        NFeICMS nFeICMS = nFe.getNFeItemList().get(0).getNFeICMS();

        assertEquals(0, nFeICMS.getOrig());
        assertEquals("10", nFeICMS.getCst());
        assertEquals(0, nFeICMS.getModBC());
        assertEquals(400, nFeICMS.getVBC());
        assertEquals(12, nFeICMS.getPICMS());
        assertEquals(48, nFeICMS.getVICMS());
        assertEquals(0, nFeICMS.getModBCST());
        assertEquals(88.96, nFeICMS.getPMVAST());
        assertEquals(755.84, nFeICMS.getVBCST());
        assertEquals(18, nFeICMS.getPICMSST());
        assertEquals(88.05, nFeICMS.getVICMSST());
    }

    @Test
    void comItensIPITagShouldBeFulfiled() {
        Document doc = this.getDocument("xml/111011-35211107022495000107550010001110111560552211.xml");
        NFeBuilder nFeBuilder = getnFeBuilder(doc);
        NFe nFe = nFeBuilder.comNFe().comItens().build();
        NFeIPI nFeIPI = nFe.getNFeItemList().get(0).getNFeIPI();

        assertEquals(999, nFeIPI.getCEnq());
        assertEquals("50", nFeIPI.getCst());
        assertEquals(3, nFeIPI.getVBC());
        assertEquals(10, nFeIPI.getPIPI());
        assertEquals(0.3, nFeIPI.getVIPI());
    }

    @Test
    void comItensPISTagShouldBeFulfiled() {
        Document doc = this.getDocument("xml/111011-35211107022495000107550010001110111560552211.xml");
        NFeBuilder nFeBuilder = getnFeBuilder(doc);
        NFe nFe = nFeBuilder.comNFe().comItens().build();
        NFePIS nFePIS = nFe.getNFeItemList().get(0).getNFePIS();

        assertEquals("01", nFePIS.getCst());
        assertEquals(400, nFePIS.getVBC());
        assertEquals(0.65, nFePIS.getPPIS());
        assertEquals(2.6, nFePIS.getVPIS());
    }

    @Test
    void comItensCOFINSTagShouldBeFulfiled() {
        Document doc = this.getDocument("xml/111011-35211107022495000107550010001110111560552211.xml");
        NFeBuilder nFeBuilder = getnFeBuilder(doc);
        NFe nFe = nFeBuilder.comNFe().comItens().build();
        NFeCOFINS nFeCOFINS = nFe.getNFeItemList().get(0).getNFeCOFINS();

        assertEquals("01", nFeCOFINS.getCst());
        assertEquals(400, nFeCOFINS.getVBC());
        assertEquals(3, nFeCOFINS.getPCOFINS());
        assertEquals(12, nFeCOFINS.getVCOFINS());
    }

    @Test
    void build() {
    }

    private NFeBuilder getnFeBuilder(Document doc) {
        NFeBuilder nFeBuilder = new NFeBuilder(
                doc,
                this.xpath,
                this.destinatarioService,
                this.emitenteService,
                this.produtoService,
                this.transportadoraService,
                "qual-nome",
                erroProcessamentoService);
        return nFeBuilder;
    }

    @Test
    public void teste() throws ParseException {
        Date dataEmissao = new SimpleDateFormat("yyyyMMddHHmmss").parse("20220430100508");
        Date horaEmissao = new SimpleDateFormat("HHmmss").parse("100508");
        dataEmissao.setTime(horaEmissao.getTime());
        ZonedDateTime atedZone = LocalDateTime.parse("20220430100508", DateTimeFormatter.ofPattern("yyyyMMddHHmmss"))
                .atZone(ZoneId.of("America/Sao_Paulo"));

        System.out.println();
    }

    private Document getDocument(String fileName) {
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
            Path pathResolved = this.rootPath.resolve(fileName);
            docBuilder = docFactory.newDocumentBuilder();
            document = docBuilder.parse(Files.newInputStream(pathResolved), "UTF-8");
        } catch (ParserConfigurationException | IOException | SAXException e) {
            throw new ValidationException(EnumException.INVALID_XML_PASSED_IN);
        }

        return document;
    }
}