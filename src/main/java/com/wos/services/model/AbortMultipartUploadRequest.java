package com.wos.services.model;

/**
 * Parameters in a request for aborting an multipart upload
 */
public class AbortMultipartUploadRequest extends GenericRequest {
    private String uploadId;

    private String bucketName;

    private String objectKey;

    public AbortMultipartUploadRequest() {

    }

    /**
     * Constructor
     * 
     * @param bucketName
     *            Bucket name
     * @param objectKey
     *            Object name
     * @param uploadId
     *            Multipart upload ID
     */
    public AbortMultipartUploadRequest(String bucketName, String objectKey, String uploadId) {
        this.bucketName = bucketName;
        this.objectKey = objectKey;
        this.uploadId = uploadId;
    }

    /**
     * Obtain the multipart upload ID.
     * 
     * @return Multipart upload ID
     */
    public String getUploadId() {
        return uploadId;
    }

    /**
     * Set the multipart upload ID.
     * 
     * @param uploadId
     *            Multipart upload ID
     */
    public void setUploadId(String uploadId) {
        this.uploadId = uploadId;
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
     * Name of the bucket to which the multipart upload belongs
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
     * Set the name of the object involved in the multipart upload.
     * 
     * @param objectKey
     *            Name of the object involved in the multipart upload
     */
    public void setObjectKey(String objectKey) {
        this.objectKey = objectKey;
    }

    @Override
    public String toString() {
        return "AbortMultipartUploadRequest [uploadId=" + uploadId + ", bucketName=" + bucketName + ", objectKey="
                + objectKey + "]";
    }

}
