package com.wos.services.model;

/**
 * Basic class of bucket requests
 *
 *
 *
 */
public class BaseBucketRequest extends GenericRequest {
    private String bucketName;

    public BaseBucketRequest() {

    }

    public BaseBucketRequest(String bucketName) {
        this.bucketName = bucketName;
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

    @Override
    public String toString() {
        return "BaseBucketRequest [bucketName=" + bucketName + ", isRequesterPays()=" + isRequesterPays() + "]";
    }
}
