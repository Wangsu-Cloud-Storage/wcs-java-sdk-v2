package com.wos.services.model;

import java.util.Map;

/**
 * Response to an object upload request
 */
public class PutObjectResult extends HeaderResponse {

    private String bucketName;

    private String objectKey;

    private String etag;

    private StorageClassEnum storageClass;

    private String objectUrl;

    public PutObjectResult(String bucketName, String objectKey, String etag,
                           StorageClassEnum storageClass, String objectUrl) {
        super();
        this.bucketName = bucketName;
        this.objectKey = objectKey;
        this.etag = etag;
        this.storageClass = storageClass;
        this.objectUrl = objectUrl;
    }

    public PutObjectResult(String bucketName, String objectKey, String etag, String objectUrl,
                           Map<String, Object> responseHeaders, int statusCode) {
        super();
        this.bucketName = bucketName;
        this.objectKey = objectKey;
        this.etag = etag;
        this.objectUrl = objectUrl;
        this.responseHeaders = responseHeaders;
        this.statusCode = statusCode;
    }

    /**
     * Obtain the ETag of the object.
     * 
     * @return ETag of the object
     */
    public String getEtag() {
        return etag;
    }

    /**
     * Obtain the name of the bucket to which the object belongs.
     * 
     * @return Name of the bucket to which the object belongs
     */
    public String getBucketName() {
        return bucketName;
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
     * Obtain the object storage class.
     * 
     * @return Object storage class
     */
    public StorageClassEnum getObjectStorageClass() {
        return storageClass;
    }

    /**
     * Obtain the full path to the object.
     * 
     * @return Full path to the object
     */
    public String getObjectUrl() {
        return objectUrl;
    }

    @Override
    public String toString() {
        return "PutObjectResult [bucketName=" + bucketName + ", objectKey=" + objectKey + ", etag=" + etag
                + ", storageClass=" + storageClass + ", objectUrl=" + objectUrl + "]";
    }

}
