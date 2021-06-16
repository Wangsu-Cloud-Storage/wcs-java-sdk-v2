package com.wos.services.internal;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * OEF异常信息
 *
 */
public class OefExceptionMessage {
    @JsonProperty(value = "message")
    private String message;

    @JsonProperty(value = "code")
    private String code;

    @JsonProperty(value = "request_id")
    private String requestId;

    public OefExceptionMessage() {

    }

    /**
     * 构造函数
     * 
     * @param message
     *            错误信息
     * @param code
     *            错误码
     * @param requestId
     *            请求ID
     */
    public OefExceptionMessage(String message, String code, String requestId) {
        this.message = message;
        this.code = code;
        this.requestId = requestId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    @Override
    public String toString() {
        return "OefExceptionMessage [message=" + message + ", code=" + code + ", request_id" + requestId + "]";
    }
}
