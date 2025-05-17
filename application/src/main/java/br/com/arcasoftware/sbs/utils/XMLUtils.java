package br.com.arcasoftware.sbs.utils;

import br.com.arcasoftware.sbs.enums.EnumException;
import br.com.arcasoftware.sbs.exception.ValidationException;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import javax.xml.namespace.QName;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpressionException;

public class XMLUtils {

    private XMLUtils() {
        // hiding public constructor
    }

    public static Object getObject(Document document, XPath xpath, String expression, QName type) {
        try {
            return xpath.compile(expression).evaluate(document, type);
        } catch (XPathExpressionException e) {
            throw new ValidationException(EnumException.INVALID_XML_PASSED_IN);
        }
    }

    public static int extractIntegerValue(NodeList aList) {
        String textValue = extractTextValue(aList, null);
        return null == textValue ? 0 : Integer.parseInt(textValue);
    }

    public static double extractDoubleValue(NodeList aList) {
        String textValue = extractTextValue(aList, null);
        return null == textValue ? 0 : Double.parseDouble(textValue);
    }

    public static long extractLongValue(NodeList aList) {
        String textValue = extractTextValue(aList, null);
        return null == textValue ? 0 : Long.parseLong(textValue);
    }

    public static String extractTextValue(NodeList aList) {
        return extractTextValue(aList, null);
    }

    public static String extractTextValue(NodeList aList, String aDefaultValue) {
        if (aList == null || aList.getLength() == 0) {
            return aDefaultValue;
        }
        return aList.item(0).getTextContent();
    }

}
