package com.wos.services.model;

import com.wos.services.internal.utils.ServiceUtils;

import java.util.Date;

/**
 * Response to a request for copying an object
 */
public class CopyObjectResult extends HeaderResponse {
    private String etag;

    private Date lastModified;

    private StorageClassEnum storageClass;

    public CopyObjectResult(String etag, Date lastModified, StorageClassEnum storageClass) {
        this.etag = etag;
        this.lastModified = ServiceUtils.cloneDateIgnoreNull(lastModified);
        this.storageClass = storageClass;
    }

    /**
     * Obtain the ETag of the destination object.
     * 
     * @return ETag value of the destination object
     */
    public String getEtag() {
        return etag;
    }

    /**
     * Last modification time of the destination object
     * 
     * @return Last modification time of the destination object
     */
    public Date getLastModified() {
        return ServiceUtils.cloneDateIgnoreNull(this.lastModified);
    }


    /**
     * Obtain the storage class of the destination object.
     * 
     * @return Object storage class
     */
    public StorageClassEnum getObjectStorageClass() {
        return storageClass;
    }

    @Override
    public String toString() {
        return "CopyObjectResult [etag=" + etag + ", lastModified=" + lastModified
                + ", storageClass=" + storageClass + "]";
    }

}
