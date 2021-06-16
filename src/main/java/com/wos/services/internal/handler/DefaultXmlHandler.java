package com.wos.services.internal.handler;

import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

public abstract class DefaultXmlHandler extends DefaultHandler {

    private StringBuilder currText = null;

    @Override
    public void startDocument() {
        return;
    }

    @Override
    public void endDocument() {
        return;
    }

    @Override
    public void startElement(String uri, String name, String qName, Attributes attrs) {
        this.currText = new StringBuilder();
        this.startElement(name, attrs);
    }

    public void startElement(String name, Attributes attrs) {
        this.startElement(name);
    }

    public void startElement(String name) {
        return;
    }

    @Override
    public void endElement(String uri, String name, String qName) {
        String elementText = this.currText.toString();
        this.endElement(name, elementText);
    }

    public abstract void endElement(String name, String content);

    public void characters(char[] ch, int start, int length) {
        this.currText.append(ch, start, length);
    }
}
