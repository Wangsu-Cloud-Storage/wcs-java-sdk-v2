package com.wos.services.model;

/**
 * HTTP/HTTPS request method
 */
public enum HttpMethodEnum {
    /**
     * GET method, normally used for query
     */
    GET("Get"),

    /**
     * PUT method, normally used for adding and modification
     */
    PUT("Put"),

    /**
     * POST method, normally used for adding
     */
    POST("Post"),

    /**
     * DELETE method, normally used for deletion
     */
    DELETE("Delete"),

    /**
     * HEAD method, normally used to query response headers
     */
    HEAD("Head"),

    /**
     * OPTIONS method, normally used for preflight
     */
    OPTIONS("Options");

    private String operationType;

    private HttpMethodEnum(String operationType) {
        if (operationType == null) {
            throw new IllegalArgumentException("operation type code is null");
        }
        this.operationType = operationType;
    }

    public String getOperationType() {
        return this.operationType.toUpperCase();
    }

    public static HttpMethodEnum getValueFromStringCode(String operationType) {
        if (operationType == null) {
            throw new IllegalArgumentException("operation type is null");
        }

        for (HttpMethodEnum installMode : HttpMethodEnum.values()) {
            if (installMode.getOperationType().equals(operationType.toUpperCase())) {
                return installMode;
            }
        }

        throw new IllegalArgumentException("operation type is illegal");
    }
}
