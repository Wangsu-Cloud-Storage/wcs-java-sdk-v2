package com.wos.services.model;

/**
 * HTTP type
 *
 */
public enum HttpProtocolTypeEnum {

    /**
     * HTTP 1.1
     */
    HTTP1_1("http1.1"), /**
                         * HTTP 2.0
                         */
    HTTP2_0("http2.0");

    private String code;

    private HttpProtocolTypeEnum(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public static HttpProtocolTypeEnum getValueFromCode(String code) {
        for (HttpProtocolTypeEnum val : HttpProtocolTypeEnum.values()) {
            if (val.code.equals(code)) {
                return val;
            }
        }
        return HTTP1_1;
    }

}