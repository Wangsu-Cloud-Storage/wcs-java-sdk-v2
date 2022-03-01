package com.wos.services.model.avOperation;

import javax.xml.bind.annotation.adapters.XmlAdapter;
import java.util.Map;

public class MapAdapter extends XmlAdapter<XmlMap, Map<String, String>> {
    @Override
    public Map<String, String> unmarshal(XmlMap v) throws Exception {
        return null;
    }

    @Override
    public XmlMap marshal(Map<String, String> v) throws Exception {
        if (v != null) {
            XmlMap xmlMap = new XmlMap();
            for (Map.Entry<String, String> entry : v.entrySet()) {
                xmlMap.put(entry.getKey(), entry.getValue());
            }
            return xmlMap;
        }
        return null;
    }
}
