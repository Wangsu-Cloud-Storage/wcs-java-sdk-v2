package com.wos.services.model;

/**
 * Response to the request for restoring Archive objects
 */
public class RestoreObjectResult extends HeaderResponse {

    private String bucketName;
    private String objectKey;


    public RestoreObjectResult(String bucketName, String objectKey) {
        this.bucketName = bucketName;
        this.objectKey = objectKey;
    }
    
    public String getBucketName() {
        return bucketName;
    }

    public String getObjectKey() {
        return objectKey;
    }

}
