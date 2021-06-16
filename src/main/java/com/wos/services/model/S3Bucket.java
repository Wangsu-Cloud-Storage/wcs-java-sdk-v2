package com.wos.services.model;

import com.wos.services.internal.utils.ServiceUtils;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class S3Bucket extends HeaderResponse {

    protected String bucketName;

    protected Owner owner;

    protected Date creationDate;

    protected String location;

    protected StorageClassEnum storageClass;

    protected Map<String, Object> metadata = new HashMap<String, Object>();

    protected AccessControlList acl;

    public S3Bucket() {

    }

    public S3Bucket(String bucketName, String location) {
        this.bucketName = bucketName;
        this.location = location;
    }

    public String getBucketName() {
        return bucketName;
    }

    public void setBucketName(String bucketName) {
        this.bucketName = bucketName;
    }

    public Owner getOwner() {
        return owner;
    }

    public void setOwner(Owner bucketOwner) {
        this.owner = bucketOwner;
    }

    public Date getCreationDate() {
        return ServiceUtils.cloneDateIgnoreNull(this.creationDate);
    }

    public void setCreationDate(Date bucketCreationDate) {
        this.creationDate = ServiceUtils.cloneDateIgnoreNull(bucketCreationDate);
    }

    public Map<String, Object> getMetadata() {
        return metadata;
    }

    public void setMetadata(Map<String, Object> metadata) {
        this.metadata.putAll(metadata);
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public AccessControlList getAcl() {
        return acl;
    }

    public void setAcl(AccessControlList acl) {
        this.acl = acl;
    }

    public String getStorageClass() {
        return this.storageClass != null ? this.storageClass.getCode() : null;
    }

    public void setStorageClass(String storageClass) {
        this.storageClass = StorageClassEnum.getValueFromCode(storageClass);
    }

    public StorageClassEnum getBucketStorageClass() {
        return storageClass;
    }

    public void setBucketStorageClass(StorageClassEnum storageClass) {
        this.storageClass = storageClass;
    }

    @Override
    public String toString() {
        return "WosBucket [bucketName=" + bucketName + ", owner=" + owner + ", creationDate=" + creationDate
                + ", location=" + location + ", storageClass=" + storageClass + ", metadata=" + metadata + ", acl="
                + acl + "]";
    }
}
