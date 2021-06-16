package com.wos.services.model;

/**
 * Response to a request for combining parts
 */
public class CompleteMultipartUploadResult extends HeaderResponse {
    private String bucketName;

    private String objectKey;

    private String etag;

    private String location;

    private String objectUrl;

    public CompleteMultipartUploadResult(String bucketName, String objectKey, String etag, String location, String objectUrl) {
        this.bucketName = bucketName;
        this.objectKey = objectKey;
        this.etag = etag;
        this.location = location;
        this.objectUrl = objectUrl;
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

    /**
     * Obtain the ETag of the object involved in the multipart upload.
     * 
     * @return ETag of the object involved in the multipart upload
     */
    public String getEtag() {
        return etag;
    }

    /**
     * Obtain the URI of the object after part combination.
     * 
     * @return URI of the object obtained after part combination
     */
    public String getLocation() {
        return location;
    }

    /**
     * Obtain the full path to the object after part combination.
     * 
     * @return Full path to the object
     */
    public String getObjectUrl() {
        return objectUrl;
    }

    @Override
    public String toString() {
        return "CompleteMultipartUploadResult [bucketName=" + bucketName + ", objectKey=" + objectKey + ", etag=" + etag
                + ", location=" + location + ", objectUrl=" + objectUrl + "]";
    }

}
