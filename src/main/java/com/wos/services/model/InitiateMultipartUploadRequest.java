package com.wos.services.model;

/**
 * Parameters in a request for initializing a multipart upload
 */
public class InitiateMultipartUploadRequest extends PutObjectBasicRequest {
    private ObjectMetadata metadata;

    private int expires;

    public InitiateMultipartUploadRequest() {

    }

    /**
     * Constructor
     * 
     * @param bucketName
     *            Name of the bucket to which the multipart upload belongs
     * @param objectKey
     *            Name of the object involved in the multipart upload
     */
    public InitiateMultipartUploadRequest(String bucketName, String objectKey) {
        this.bucketName = bucketName;
        this.objectKey = objectKey;
    }

    /**
     * Obtain the expiration time of the object generated after the multipart
     * upload is complete.
     * 
     * @return Expiration time of the object
     */
    public int getExpires() {
        return expires;
    }

    /**
     * Set the expiration time of the object generated after the multipart
     * upload is complete. The value must be an integer.
     * 
     * @param expires
     *            Expiration time of the object
     */
    public void setExpires(int expires) {
        this.expires = expires;
    }

    /**
     * Obtain the name of the bucket to which the multipart upload belongs.
     * 
     * @return Name of the bucket to which the multipart upload belongs
     */
    public String getBucketName() {
        return bucketName;
    }

    /**
     * Set the name for the bucket to which the multipart upload belongs.
     * 
     * @param bucketName
     *            Name of the bucket to which the multipart upload belongs
     */
    public void setBucketName(String bucketName) {
        this.bucketName = bucketName;
    }

    /**
     * Obtain the name of the object involved in the multipart upload.
     * 
     * @return Name of the object involved in the multipart upload
     */
    public String getObjectKey() {
        return objectKey;
    }

    /**
     * Set the name for the object involved in the multipart upload.
     * 
     * @param objectKey
     *            Name of the object involved in the multipart upload
     */
    public void setObjectKey(String objectKey) {
        this.objectKey = objectKey;
    }

    /**
     * Set object properties, including customized metadata. "content-type" is
     * supported.
     * 
     * @return Object properties
     */
    public ObjectMetadata getMetadata() {
        return metadata;
    }

    /**
     * Obtain object properties, including customized metadata. "content-type"
     * is supported.
     * 
     * @param metadata
     *            Object properties
     */
    public void setMetadata(ObjectMetadata metadata) {
        this.metadata = metadata;
    }

    @Override
    public String toString() {
        return "InitiateMultipartUploadRequest [bucketName=" + bucketName + ", objectKey=" + objectKey + ", metadata=" + metadata
                + ", expires=" + expires + "]";
    }

}
