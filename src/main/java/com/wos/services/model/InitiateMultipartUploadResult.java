package com.wos.services.model;

/**
 * Response to a request for initializing a multipart upload
 */
public class InitiateMultipartUploadResult extends HeaderResponse {
    private String uploadId;

    private String bucketName;

    private String objectKey;

    public InitiateMultipartUploadResult(String bucketName, String objectKey, String uploadId) {
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
     * Obtain the name of the bucket to which the multipart upload belongs.
     * 
     * @return Name of the bucket to which the multipart upload belongs
     */
    public String getBucketName() {
        return bucketName;
    }

    /**
     * Obtain the name of the object involved in the multipart upload.
     * 
     * @return Name of the object involved in the multipart upload
     */
    public String getObjectKey() {
        return objectKey;
    }

    @Override
    public String toString() {
        return "InitiateMultipartUploadResult [uploadId=" + uploadId + ", bucketName=" + bucketName + ", objectKey="
                + objectKey + "]";
    }

}
