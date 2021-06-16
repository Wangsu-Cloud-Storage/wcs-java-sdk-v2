package com.wos.services.model;

/**
 *
 * Redirection protocol
 *
 */
public enum ProtocolEnum {

    /**
     * Use HTTP for redirection.
     */
    HTTP("http"),

    /**
     * Use HTTPS for redirection.
     */
    HTTPS("https");

    private String code;

    private ProtocolEnum(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public static ProtocolEnum getValueFromCode(String code) {
        for (ProtocolEnum val : ProtocolEnum.values()) {
            if (val.code.equals(code)) {
                return val;
            }
        }
        return null;
    }
}
