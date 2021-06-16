package com.wos.services.internal;

import java.io.Serializable;
import java.util.Properties;

import com.wos.log.ILogger;
import com.wos.log.LoggerBuilder;

public class WosProperties implements Serializable {

    private static final long serialVersionUID = -822234326095333142L;

    private static final ILogger LOG = LoggerBuilder.getLogger(WosProperties.class);

    private final Properties properties = new Properties();

    public void setProperty(String propertyName, String propertyValue) {
        if (propertyValue == null) {
            this.clearProperty(propertyName);
        } else {
            this.properties.put(propertyName, trim(propertyValue));
        }
    }

    public void clearProperty(String propertyName) {
        this.properties.remove(propertyName);
    }

    public void clearAllProperties() {
        this.properties.clear();
    }

    public String getStringProperty(String propertyName, String defaultValue) {
        String stringValue = trim(properties.getProperty(propertyName, defaultValue));
        if (LOG.isDebugEnabled() && !"httpclient.proxy-user".equals(propertyName)
                && !"httpclient.proxy-password".equals(propertyName)) {
            LOG.debug(propertyName + "=" + stringValue);
        }
        return stringValue;
    }

    public int getIntProperty(String propertyName, int defaultValue) throws NumberFormatException {
        String value = trim(properties.getProperty(propertyName, String.valueOf(defaultValue)));
        if (LOG.isDebugEnabled()) {
            LOG.debug(propertyName + "=" + value);
        }
        return Integer.parseInt(value);
    }

    public boolean getBoolProperty(String propertyName, boolean defaultValue) throws IllegalArgumentException {
        String boolValue = trim(properties.getProperty(propertyName, String.valueOf(defaultValue)));
        if (LOG.isDebugEnabled()) {
            LOG.debug(propertyName + "=" + boolValue);
        }

        if (!"true".equalsIgnoreCase(boolValue) && !"false".equalsIgnoreCase(boolValue)) {
            throw new IllegalArgumentException("Boolean value '" + boolValue + "' for wos property '" + propertyName
                    + "' must be 'true' or 'false' (case-insensitive)");
        }

        return Boolean.parseBoolean(boolValue);
    }

    public boolean containsKey(String propertyName) {
        return properties.containsKey(propertyName);
    }

    private static String trim(String str) {
        if (null == str) {
            return null;
        }
        return str.trim();
    }

}
