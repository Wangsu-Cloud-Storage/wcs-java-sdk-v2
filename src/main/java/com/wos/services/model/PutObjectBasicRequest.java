package com.wos.services.model;

public abstract class PutObjectBasicRequest extends GenericRequest {

    protected String bucketName;

    protected String objectKey;

    protected String successRedirectLocation;


    /**
     * Obtain the bucket name.
     * 
     * @return Bucket name
     */
    public String getBucketName() {
        return bucketName;
    }

    /**
     * Set the bucket name.
     * 
     * @param bucketName
     *            Bucket name
     */
    public void setBucketName(String bucketName) {
        this.bucketName = bucketName;
    }

    /**
     * Obtain the object name.
     * 
     * @return Object name
     */
    public String getObjectKey() {
        return objectKey;
    }

    /**
     * Set the object name.
     * 
     * @param objectKey
     *            Object name
     * 
     */
    public void setObjectKey(String objectKey) {
        this.objectKey = objectKey;
    }

    /**
     * Obtain the redirection address after a successfully responded request.
     * 
     * @return Redirection address
     */
    public String getSuccessRedirectLocation() {
        return successRedirectLocation;
    }

    /**
     * Set the redirection address after a successfully responded request.
     * 
     * @param successRedirectLocation
     *            Redirection address
     */
    public void setSuccessRedirectLocation(String successRedirectLocation) {
        this.successRedirectLocation = successRedirectLocation;
    }
}
