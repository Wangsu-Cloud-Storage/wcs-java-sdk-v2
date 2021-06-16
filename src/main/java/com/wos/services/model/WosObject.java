package com.wos.services.model;

import java.io.InputStream;

/**
 * Objects in Wos
 */
public class WosObject extends S3Object {

    /**
     * Obtain the name of the bucket to which the object belongs.
     * 
     * @return Name of the bucket to which the object belongs
     */
    public String getBucketName() {
        return bucketName;
    }

    /**
     * Set the bucket to which the object belongs.
     * 
     * @param bucketName
     *            Name of the bucket to which the object belongs
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

    /**
     * Obtain the object properties, including "content-type", "content-length",
     * and customized metadata.
     * 
     * @return Object properties
     */
    public ObjectMetadata getMetadata() {
        if (metadata == null) {
            this.metadata = new ObjectMetadata();
        }
        return metadata;
    }

    /**
     * Set the object properties, including "content-type", "content-length",
     * and customized metadata.
     * 
     * @param metadata
     *            Object properties
     */
    public void setMetadata(ObjectMetadata metadata) {
        this.metadata = metadata;
    }

    /**
     * Obtain the data stream of the object.
     * 
     * @return Data stream of the object
     */
    public InputStream getObjectContent() {
        return objectContent;
    }

    /**
     * Set the data stream of the object.
     * 
     * @param objectContent
     *            Object data stream
     */
    public void setObjectContent(InputStream objectContent) {
        this.objectContent = objectContent;
    }

    /**
     * Obtain the owner of the object.
     * 
     * @return Owner of the object
     */
    public Owner getOwner() {
        return owner;
    }

    /**
     * Set the owner of the object.
     * 
     * @param owner
     *            Owner of the object
     */
    public void setOwner(Owner owner) {
        this.owner = owner;
    }

    @Override
    public String toString() {
        return "WosObject [bucketName=" + bucketName + ", objectKey=" + objectKey + ", owner=" + owner + ", metadata="
                + metadata + ", objectContent=" + objectContent + "]";
    }
}
