package com.wos.services.model.avOperation;


import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementRef;
import java.util.ArrayList;
import java.util.List;

/**
 * Xml Param Map
 */
public class XmlMap {

    @XmlElementRef(name = "xmlMap")
    private List<JAXBElement<String>> elements = new ArrayList<>();
    private static ObjectFactory objectFactory = new ObjectFactory();

    public void put(String key, String value) {
        JAXBElement<String> ele = objectFactory.createXmlMap(key, value);
        this.elements.add(ele);
    }
}
