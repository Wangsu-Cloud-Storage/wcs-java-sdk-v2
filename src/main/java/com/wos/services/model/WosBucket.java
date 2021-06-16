package com.wos.services.model;

import com.wos.services.internal.utils.ServiceUtils;

import java.util.Date;

/**
 * Buckets in WOS
 * 
 */
public class WosBucket extends S3Bucket {

    protected String endpoint;

    protected String region;

    public WosBucket() {

    }

    /**
     * Constructor
     * 
     * @param bucketName Bucket name
     */
    public WosBucket(String bucketName) {
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
     * Set the bucket name. The value can contain only lowercase letters,
     * digits, hyphens (-), and periods (.).
     * 
     * @param bucketName
     *            Bucket name
     */
    public void setBucketName(String bucketName) {
        this.bucketName = bucketName;
    }

    /**
     * Obtain the owner of the bucket.
     * 
     * @return Owner of the bucket
     */
    public Owner getOwner() {
        return owner;
    }

    /**
     * Set the owner of the bucket.
     * 
     * @param bucketOwner
     *            Owner of the bucket
     */
    public void setOwner(Owner bucketOwner) {
        this.owner = bucketOwner;
    }

    /**
     * Obtain the creation time of the bucket.
     * 
     * @return Creation time of the bucket
     */
    public Date getCreationDate() {
        return ServiceUtils.cloneDateIgnoreNull(this.creationDate);
    }

    /**
     * Set the creation time of the bucket.
     * 
     * @param bucketCreationDate
     *            Creation time of the bucket
     */
    public void setCreationDate(Date bucketCreationDate) {
        this.creationDate = ServiceUtils.cloneDateIgnoreNull(bucketCreationDate);
    }

    /**
     * Obtain the bucket ACL.
     * 
     * @return Bucket ACL
     */
    public AccessControlList getAcl() {
        return acl;
    }

    /**
     * Set the bucket ACL.
     * 
     * @param acl
     *            Bucket ACL
     */
    public void setAcl(AccessControlList acl) {
        this.acl = acl;
    }

    /**
     * Obtain the bucket storage class.
     * 
     * @return Bucket storage class
     */
    public String getStorageClass() {
        return this.storageClass != null ? this.storageClass.getCode() : null;
    }

    /**
     * Set the bucket storage class.
     * 
     * @param storageClass
     *            Bucket storage class
     */
    public void setStorageClass(String storageClass) {
        this.storageClass = StorageClassEnum.getValueFromCode(storageClass);
    }

    public StorageClassEnum getBucketStorageClass() {
        return storageClass;
    }

    /**
     * Set the bucket storage class.
     * 
     * @param storageClass
     *            Bucket storage class
     */
    public void setBucketStorageClass(StorageClassEnum storageClass) {
        this.storageClass = storageClass;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    @Override
    public String toString() {
        return "WosBucket [bucketName=" + bucketName + ", owner=" + owner + ", creationDate=" + creationDate
                + ", storageClass=" + storageClass + ", metadata=" + metadata + ", acl=" + acl + ", endpoint="
                + endpoint + ", region=" + region + "]";
    }
}
