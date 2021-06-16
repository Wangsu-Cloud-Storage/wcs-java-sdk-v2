package com.wos.services.model;

/**
 * Request parameters for deleting an object.
 *
 *
 */
public class DeleteObjectRequest extends GenericRequest {
    private String bucketName;

    private String objectKey;

    public DeleteObjectRequest() {

    }

    /**
     * Constructor
     * 
     * @param bucketName
     *            Bucket name
     * @param objectKey
     *            Object name
     */
    public DeleteObjectRequest(String bucketName, String objectKey) {
        this.bucketName = bucketName;
        this.objectKey = objectKey;
    }

    /**
     * Obtain the name of the bucket to which the to-be-deleted object belongs.
     * 
     * @return Name of the bucket to which the to-be-deleted object belongs
     */
    public String getBucketName() {
        return bucketName;
    }

    /**
     * Set the name of the bucket to which the to-be-deleted object belongs.
     * 
     * @param bucketName
     *            Name of the bucket to which the to-be-deleted object belongs
     */
    public void setBucketName(String bucketName) {
        this.bucketName = bucketName;
    }

    /**
     * Obtain the name of the object to be deleted.
     * 
     * @return Name of the object to be deleted
     */
    public String getObjectKey() {
        return objectKey;
    }

    /**
     * Set the name of the object to be deleted.
     * 
     * @param objectKey
     *            Name of the object to be deleted
     */
    public void setObjectKey(String objectKey) {
        this.objectKey = objectKey;
    }

    @Override
    public String toString() {
        return "AbortMultipartUploadRequest [bucketName=" + bucketName + ", objectKey="
                + objectKey + "]";
    }

}
