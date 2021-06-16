package com.wos.services.model;

import java.util.HashMap;
import java.util.Map;

/**
 * 
 * Response to a request for temporarily authorized access
 *
 */
public class TemporarySignatureResponse {
    private String signedUrl;

    private Map<String, String> actualSignedRequestHeaders;

    public TemporarySignatureResponse(String signedUrl) {
        this.signedUrl = signedUrl;
    }

    /**
     * Obtain the URL of the temporarily authorized access.
     * 
     * @return URL of the temporarily authorized access
     */
    public String getSignedUrl() {
        return signedUrl;
    }

    /**
     * Obtain the request headers.
     * 
     * @return Request headers
     */
    public Map<String, String> getActualSignedRequestHeaders() {
        if (actualSignedRequestHeaders == null) {
            this.actualSignedRequestHeaders = new HashMap<String, String>();
        }
        return actualSignedRequestHeaders;
    }

    @Override
    public String toString() {
        return "TemporarySignatureResponse [signedUrl=" + signedUrl + ", actualSignedRequestHeaders="
                + actualSignedRequestHeaders + "]";
    }

}
