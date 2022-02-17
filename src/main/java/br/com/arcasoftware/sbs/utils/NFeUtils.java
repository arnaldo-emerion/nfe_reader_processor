package br.com.arcasoftware.sbs.utils;

import br.com.arcasoftware.sbs.business.ValidNFeTypes;
import br.com.arcasoftware.sbs.enums.EnumException;
import br.com.arcasoftware.sbs.enums.NFeTypeEnum;
import br.com.arcasoftware.sbs.exception.ValidationException;
import org.w3c.dom.Document;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import java.util.List;

public class NFeUtils {

    private static final List<NFeTypeEnum> validTypes;

    private NFeUtils() {
        // hiding public constructor
    }

    static {
        validTypes = new ValidNFeTypes().getAllValidNFeTypes();
    }

    public static boolean isValidTypeNfe(Document document, XPath xPath) {
        String natOpElement = "//natOp";
        boolean isNullTag = isNullTag(document, xPath, natOpElement);

        if (isNullTag) {
            return false;
        }

        String natOp = (String) XMLUtils.getObject(document, xPath, natOpElement, XPathConstants.STRING);

        return natOp.toUpperCase().contains("VENDA");
    }

    public static boolean isNullTag(Document document, XPath xPath, String tag) {
        return null == XMLUtils.getObject(document, xPath, tag, XPathConstants.NODE);
    }

}
