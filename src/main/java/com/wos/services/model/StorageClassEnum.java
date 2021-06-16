package com.wos.services.model;

/**
 *
 * Storage class
 *
 */
public enum StorageClassEnum {

    /**
     * Standard
     */
    standard,

    /**
     * Infrequent Access
     */
    ia,

    /**
     * Archive
     */
    archive;

    public String getCode() {
        return this.name();
    }

    public static StorageClassEnum getValueFromCode(String code) {
        if ("standard".equals(code)) {
            return standard;
        } else if ("ia".equals(code)) {
            return ia;
        } else if ("archive".equals(code)) {
            return archive;
        }
        return null;
    }
}
