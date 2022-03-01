package com.wos.services.model.avOperation;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;

@XmlRegistry
public class ObjectFactory {

    @XmlElementDecl(name = "xmlMap")
    public JAXBElement<String> createXmlMap(String key, String value) {
        QName name = new QName(key);
        return new JAXBElement<>(name, String.class, value);
    }
}
