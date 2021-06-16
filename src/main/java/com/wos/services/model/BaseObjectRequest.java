package com.wos.services.model;

/**
 * Basic class of object requests
 *
 *
 */
public class BaseObjectRequest extends GenericRequest {
    private String bucketName;
    private String objectKey;

    public BaseObjectRequest() {
    }

    public BaseObjectRequest(String bucketName, String objectKey) {
        this.bucketName = bucketName;
        this.objectKey = objectKey;
    }

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
     */
    public void setObjectKey(String objectKey) {
        this.objectKey = objectKey;
    }


    @Override
    public String toString() {
        return "BaseObjectRequest [bucketName=" + bucketName + ", objectKey=" + objectKey
                + ", isRequesterPays()=" + isRequesterPays() + "]";
    }
}
