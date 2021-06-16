package com.wos.services.model;

import java.util.Map;
import java.util.TreeMap;

import com.wos.services.internal.Constants;
import com.wos.services.internal.InternalHeaderResponse;

/**
 * Public response result, including the request ID and response headers
 *
 */
public class HeaderResponse extends InternalHeaderResponse {

    public HeaderResponse() {

    }

    /**
     * Obtain response headers.
     * 
     * @return Response headers
     */
    public Map<String, Object> getResponseHeaders() {
        if (responseHeaders == null) {
            responseHeaders = new TreeMap<String, Object>(String.CASE_INSENSITIVE_ORDER);
        }
        return responseHeaders;
    }

    /**
     * Obtain the request ID returned by the server.
     * 
     * @return Request ID returned by the server
     */
    public String getRequestId() {
        Object id = this.getResponseHeaders().get(Constants.REQUEST_ID_HEADER);
        return id == null ? "" : id.toString();
    }

    /**
     * Obtain the HTTP status code returned by the server.
     * 
     * @return HTTP status code returned by the server
     */
    public int getStatusCode() {
        return statusCode;
    }

    @Override
    public String toString() {
        return "HeaderResponse [responseHeaders=" + responseHeaders + ", statusCode=" + statusCode + "]";
    }

}
