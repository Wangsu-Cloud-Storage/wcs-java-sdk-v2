package com.wos.services.model;

/**
 * 
 * Response to a request for browser-based authorized access
 *
 */
public class PostSignatureResponse {
    private String policy;

    private String originPolicy;

    private String signature;

    private String expiration;

    private String token;

    private String algorithm;

    private String credential;

    private String date;

    public PostSignatureResponse() {

    }

    public PostSignatureResponse(String policy, String originPolicy, String signature, String expiration,
            String accessKey) {
        this.policy = policy;
        this.originPolicy = originPolicy;
        this.signature = signature;
        this.expiration = expiration;
        this.token = accessKey + ":" + signature + ":" + policy;
    }

    public PostSignatureResponse(String policy, String originPolicy, String algorithm, String credential, String date,
                                 String signature, String expiration) {
        this.policy = policy;
        this.originPolicy = originPolicy;
        this.algorithm = algorithm;
        this.credential = credential;
        this.date = date;
        this.signature = signature;
        this.expiration = expiration;
    }

    /**
     * Obtain the signature algorithm.
     *
     * @return Signature algorithm
     */
    public String getAlgorithm() {
        return algorithm;
    }

    /**
     * Obtain the credential information
     *
     * @return Credential information
     */
    public String getCredential() {
        return credential;
    }

    /**
     * Obtain the date in the ISO 8601 format.
     *
     * @return Date in the ISO 8601 format
     */
    public String getDate() {
        return date;
    }

    /**
     * Obtain the security policy of the request in the Base64 format.
     * 
     * @return Security policy in the Base64 format
     */
    public String getPolicy() {
        return policy;
    }

    /**
     * Obtain the security policy of the request in the original format.
     * 
     * @return Security policy in the original format
     */
    public String getOriginPolicy() {
        return originPolicy;
    }

    /**
     * Obtain the signature string.
     * 
     * @return Signature string
     */
    public String getSignature() {
        return signature;
    }

    /**
     * Obtain the expiration date of the request.
     * 
     * @return Expiration date
     */
    public String getExpiration() {
        return expiration;
    }

    /**
     * Obtain the token
     * 
     * @return token
     */
    public String getToken() {
        return token;
    }

    @Override
    public String toString() {
        return "PostSignatureResponse [" +
                "policy='" + policy + '\'' +
                ", originPolicy='" + originPolicy + '\'' +
                ", signature='" + signature + '\'' +
                ", expiration='" + expiration + '\'' +
                ", token='" + token + '\'' +
                ", algorithm='" + algorithm + '\'' +
                ", credential='" + credential + '\'' +
                ", date='" + date + '\'' +
                ']';
    }
}
