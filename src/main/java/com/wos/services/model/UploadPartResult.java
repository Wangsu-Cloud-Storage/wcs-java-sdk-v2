package com.wos.services.model;

/**
 * Response to a part upload request
 */
public class UploadPartResult extends HeaderResponse {
    private int partNumber;

    private String etag;

    /**
     * Obtain the part number.
     * 
     * @return Part number
     */
    public int getPartNumber() {
        return partNumber;
    }

    public void setPartNumber(int partNumber) {
        this.partNumber = partNumber;
    }

    /**
     * Obtain the ETag of the part.
     * 
     * @return Part ETag
     */
    public String getEtag() {
        return etag;
    }

    public void setEtag(String objEtag) {
        this.etag = objEtag;
    }

    @Override
    public String toString() {
        return "UploadPartResult [partNumber=" + partNumber + ", etag=" + etag + "]";
    }
}
