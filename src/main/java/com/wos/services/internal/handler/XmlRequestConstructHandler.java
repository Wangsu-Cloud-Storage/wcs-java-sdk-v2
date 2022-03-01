package com.wos.services.internal.handler;

import com.wos.log.ILogger;
import com.wos.log.LoggerBuilder;
import org.apache.commons.lang3.StringUtils;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.namespace.QName;
import java.io.StringWriter;


public class XmlRequestConstructHandler {
    private static final ILogger LOGGER = LoggerBuilder.getLogger(XmlRequestConstructHandler.class);

    public static String convertToDifferentRootXml(Object obj, String rootElementName){
        if (null == obj) {
            return null;
        }
        StringWriter writer = new StringWriter();
        try {
            if(StringUtils.isEmpty(rootElementName)) {
                return null;
            }
            JAXBContext context = JAXBContext.newInstance(obj.getClass());
            Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            marshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
            marshaller.setProperty(Marshaller.JAXB_FRAGMENT, false);
            QName name = new QName(rootElementName);
            JAXBElement<Object> jaxbElement = new JAXBElement<>(name, Object.class, obj);
            marshaller.marshal(jaxbElement, writer);
        } catch (JAXBException e) {
            LOGGER.error(e.getMessage(), e);
        }
        return writer.toString();
    }
}
