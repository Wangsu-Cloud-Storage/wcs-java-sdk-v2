package com.wos.services.model;

import java.util.Date;

import com.wos.services.internal.utils.ServiceUtils;

/**
 * Response to a request for copying a part
 */
public class CopyPartResult extends HeaderResponse {
    private int partNumber;

    private String etag;

    private Date lastModified;

    public CopyPartResult(int partNumber, String etag, Date lastModified) {
        this.partNumber = partNumber;
        this.etag = etag;
        this.lastModified = ServiceUtils.cloneDateIgnoreNull(lastModified);
    }

    /**
     * Obtain the part number of the to-be-copied part.
     * 
     * @return Part number
     */
    public int getPartNumber() {
        return partNumber;
    }

    /**
     * Obtain the ETag of the to-be-copied part.
     * 
     * @return ETag of the to-be-copied part
     */
    public String getEtag() {
        return etag;
    }

    /**
     * Obtain the last modification time of the to-be-copied part.
     * 
     * @return Last modification time of the to-be-copied part
     */
    public Date getLastModified() {
        return ServiceUtils.cloneDateIgnoreNull(this.lastModified);
    }

    @Override
    public String toString() {
        return "CopyPartResult [partNumber=" + partNumber + ", etag=" + etag + ", lastModified=" + lastModified + "]";
    }

}
