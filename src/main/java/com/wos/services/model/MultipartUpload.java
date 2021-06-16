package com.wos.services.model;

import com.wos.services.internal.utils.ServiceUtils;

import java.util.Date;

/**
 * Multipart upload
 */
public class MultipartUpload {
    private String uploadId;

    private String bucketName;

    private String objectKey;

    private Date initiatedDate;

    private StorageClassEnum storageClass;

    private Owner owner;

    private Owner initiator;

    public MultipartUpload(String uploadId, String objectKey, Date initiatedDate, StorageClassEnum storageClass,
            Owner owner, Owner initiator) {
        super();
        this.uploadId = uploadId;
        this.objectKey = objectKey;
        this.initiatedDate = ServiceUtils.cloneDateIgnoreNull(initiatedDate);
        this.storageClass = storageClass;
        this.owner = owner;
        this.initiator = initiator;
    }

    public MultipartUpload(String uploadId, String bucketName, String objectKey, Date initiatedDate,
            StorageClassEnum storageClass, Owner owner, Owner initiator) {
        super();
        this.uploadId = uploadId;
        this.bucketName = bucketName;
        this.objectKey = objectKey;
        this.initiatedDate = ServiceUtils.cloneDateIgnoreNull(initiatedDate);
        this.storageClass = storageClass;
        this.owner = owner;
        this.initiator = initiator;
    }

    /**
     * Creator of the multipart upload
     * 
     * @return Creator of the multipart upload
     */
    public Owner getInitiator() {
        return initiator;
    }

    /**
     * Query the creator of the multipart upload.
     * 
     * @return Owner of the multipart upload
     */
    public Owner getOwner() {
        return owner;
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
     * Obtain the storage class of the object generated via the multipart
     * upload.
     * 
     * @return Storage class of the object generated via the multipart upload
     */

    public String getStorageClass() {
        return storageClass != null ? storageClass.getCode() : null;
    }

    /**
     * Obtain the storage class of the object generated via the multipart
     * upload.
     * 
     * @return Storage class of the object generated via the multipart upload
     */
    public StorageClassEnum getObjectStorageClass() {
        return storageClass;
    }

    /**
     * Obtain the creation time of the multipart upload.
     * 
     * @return Creation time of the multipart upload
     */
    public Date getInitiatedDate() {
        return ServiceUtils.cloneDateIgnoreNull(this.initiatedDate);
    }

}
