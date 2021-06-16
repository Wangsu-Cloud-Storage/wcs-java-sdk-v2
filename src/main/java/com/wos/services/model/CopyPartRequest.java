package com.wos.services.model;

/**
 * Parameters in the request for copying a part
 */
public class CopyPartRequest extends GenericRequest {
    private String uploadId;

    private String sourceBucketName;

    private String sourceObjectKey;

    private String destinationBucketName;

    private String destinationObjectKey;

    private Long byteRangeStart;

    private Long byteRangeEnd;

    private int partNumber;

    public CopyPartRequest() {

    }

    /**
     * Constructor
     * 
     * @param uploadId
     *            Multipart upload ID
     * @param sourceBucketName
     *            Source bucket name
     * @param sourceObjectKey
     *            Source object name
     * @param destinationBucketName
     *            Destination bucket name
     * @param destinationObjectKey
     *            Destination object name
     * @param partNumber
     *            Part number
     */
    public CopyPartRequest(String uploadId, String sourceBucketName, String sourceObjectKey,
            String destinationBucketName, String destinationObjectKey, int partNumber) {
        this.uploadId = uploadId;
        this.sourceBucketName = sourceBucketName;
        this.sourceObjectKey = sourceObjectKey;
        this.destinationBucketName = destinationBucketName;
        this.destinationObjectKey = destinationObjectKey;
        this.partNumber = partNumber;
    }

    /**
     * Obtain the start position for copying.
     * 
     * @return Start position for copying
     */
    public Long getByteRangeStart() {
        return byteRangeStart;
    }

    /**
     * Set the start position for copying.
     * 
     * @param byteRangeStart
     *            Start position for copying
     * 
     */
    public void setByteRangeStart(Long byteRangeStart) {
        this.byteRangeStart = byteRangeStart;
    }

    /**
     * Obtain the end position for copying.
     * 
     * @return End position for copying
     */
    public Long getByteRangeEnd() {
        return byteRangeEnd;
    }

    /**
     * Set the end position for copying.
     * 
     * @param byteRangeEnd
     *            End position for copying
     * 
     */
    public void setByteRangeEnd(Long byteRangeEnd) {
        this.byteRangeEnd = byteRangeEnd;
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
     * Set the part number of the to-be-copied part.
     * 
     * @param partNumber
     *            Part number
     * 
     */
    public void setPartNumber(int partNumber) {
        this.partNumber = partNumber;
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
     * Set the multipart upload ID.
     * 
     * @param uploadId
     *            Multipart upload ID
     */
    public void setUploadId(String uploadId) {
        this.uploadId = uploadId;
    }

    /**
     * Obtain the source bucket name.
     * 
     * @return Source bucket name
     */
    public String getSourceBucketName() {
        return sourceBucketName;
    }

    /**
     * Set the source bucket name.
     * 
     * @param bucketName
     *            Source bucket name
     * 
     */
    public void setSourceBucketName(String bucketName) {
        this.sourceBucketName = bucketName;
    }

    /**
     * Obtain the source object name.
     * 
     * @return Source object name
     */
    public String getSourceObjectKey() {
        return sourceObjectKey;
    }

    /**
     * Set the source object name.
     * 
     * @param objectKey
     *            Source object name
     * 
     */
    public void setSourceObjectKey(String objectKey) {
        this.sourceObjectKey = objectKey;
    }

    /**
     * Obtain the name of the bucket (destination bucket) to which the multipart
     * upload belongs.
     * 
     * @return Name of the bucket to which the multipart upload belongs
     */
    public String getDestinationBucketName() {
        return destinationBucketName;
    }

    /**
     * Set the name of the bucket (destination bucket) to which the multipart
     * upload belongs.
     * 
     * @param destBucketName
     *            Name of the bucket to which the multipart upload belongs
     * 
     */
    public void setDestinationBucketName(String destBucketName) {
        this.destinationBucketName = destBucketName;
    }

    /**
     * Obtain the name of the object (destination object) involved in the
     * multipart upload.
     * 
     * @return Name of the object involved in the multipart upload
     * 
     */
    public String getDestinationObjectKey() {
        return destinationObjectKey;
    }

    /**
     * Set the name of the object (destination object) involved in the multipart
     * upload.
     * 
     * @param destObjectKey
     *            Name of the object involved in the multipart upload
     * 
     */
    public void setDestinationObjectKey(String destObjectKey) {
        this.destinationObjectKey = destObjectKey;
    }


    @Override
    public String toString() {
        return "CopyPartRequest [uploadId=" + uploadId + ", sourceBucketName=" + sourceBucketName + ", sourceObjectKey="
                + sourceObjectKey + ", destinationBucketName=" + destinationBucketName + ", destinationObjectKey="
                + destinationObjectKey + ", byteRangeStart=" + byteRangeStart + ", byteRangeEnd=" + byteRangeEnd
                + ", partNumber=" + partNumber + "]";
    }

}
