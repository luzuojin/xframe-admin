package dev.xframe.admin.utils;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import dev.xframe.utils.XCaught;

public class XmlBuilder {
    private Document dom;
    private  Element emt;
    public XmlBuilder(String tag) {
        try {
            dom = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
            emt = dom.createElement(tag);
            dom.appendChild(emt);
        } catch (Exception e) {
            throw XCaught.wrapException(e);
        }
    }
    private XmlBuilder(Document doc, Element emt) {
        this.dom = doc;
        this.emt = emt;
    }
    public XmlBuilder addElement(String tag) {
        Element element = dom.createElement(tag);
        emt.appendChild(element);
        return new XmlBuilder(dom, element);
    }
    public XmlBuilder addText(int text) {
        return addText(String.valueOf(text));
    }
    public XmlBuilder addText(String text) {
        emt.appendChild(dom.createTextNode(text));
        return this;
    }
    public XmlBuilder addAttribute(String name, int value) {
        return addAttribute(name, String.valueOf(value));
    }
    public XmlBuilder addAttribute(String name, String value) {
        emt.setAttribute(name, value);
        return this;
    }
    public String toString() {
        return new String(toByteArray(), StandardCharsets.UTF_8);
    }
    public byte[] toByteArray() {
        try {
            Transformer tr = TransformerFactory.newInstance().newTransformer();
            tr.setOutputProperty(OutputKeys.INDENT, "yes");
            tr.setOutputProperty(OutputKeys.METHOD, "xml");
            tr.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            tr.setOutputProperty(OutputKeys.DOCTYPE_PUBLIC, "yes");
            tr.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            tr.transform(new DOMSource(dom), new StreamResult(out));
            return out.toByteArray();
        } catch (Exception e) {
            return XCaught.throwException(e);
        }
    }
}
