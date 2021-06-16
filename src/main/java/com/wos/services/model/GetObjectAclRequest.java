package com.wos.services.model;

/**
 * Parameters in a request for obtaining object ACLs
 *
 *
 */
public class GetObjectAclRequest extends GenericRequest {
    private String bucketName;

    private String objectKey;

    public GetObjectAclRequest() {

    }

    /**
     * Constructor
     * 
     * @param bucketName
     *            Bucket name
     * @param objectKey
     *            Object name
     */
    public GetObjectAclRequest(String bucketName, String objectKey) {
        this.bucketName = bucketName;
        this.objectKey = objectKey;
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
     * Set name for the bucket to which the object belongs.
     * 
     * @param bucketName
     *            Name of the bucket to which the object belongs
     */
    public void setBucketName(String bucketName) {
        this.bucketName = bucketName;
    }

    /**
     * Obtain the name of the object.
     * 
     * @return Object name
     */
    public String getObjectKey() {
        return objectKey;
    }

    /**
     * Set a name for an object.
     * 
     * @param objectKey
     *            Object name
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
