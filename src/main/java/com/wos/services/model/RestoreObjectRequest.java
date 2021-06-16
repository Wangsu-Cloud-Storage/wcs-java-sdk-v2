package com.wos.services.model;

/**
 *
 * Parameters in a request for restoring an Archive object
 *
 */
public class RestoreObjectRequest extends BaseObjectRequest {

    /**
     * 
     * Status of the Archive object
     *
     */
    public static class RestoreObjectStatus extends HeaderResponse {

        private int code;
        /**
         * The object has been restored and can be downloaded.
         */
        public static final RestoreObjectStatus AVALIABLE = new RestoreObjectStatus(200);
        /**
         * The object is being restored and cannot be downloaded.
         */
        public static final RestoreObjectStatus INPROGRESS = new RestoreObjectStatus(202);

        private RestoreObjectStatus(int code) {
            this.code = code;
        }

        /**
         * Obtain the status code of the object.
         * 
         * @return Status code of the object
         */
        public int getCode() {
            return this.code;
        }

        public static RestoreObjectStatus valueOf(int retCode) {
            return retCode == 200 ? AVALIABLE : retCode == 202 ? INPROGRESS : new RestoreObjectStatus(retCode);
        }
    }

    private int days;

    public RestoreObjectRequest() {

    }

    /**
     * Constructor
     * 
     * @param bucketName
     *            Bucket name
     * @param objectKey
     *            Object name
     * @param days
     *            Retention period of the restored object
     */
    public RestoreObjectRequest(String bucketName, String objectKey, int days) {
        super(bucketName, objectKey);
        this.days = days;
    }


    /**
     * Obtain the retention period of the restored object. The value ranges from
     * 1 to 30 (in days).
     * 
     * @return Retention period of the restored object
     */
    public int getDays() {
        return days;
    }

    /**
     * Set the retention period of the restored object. The value ranges from 1
     * to 30 (in days).
     * 
     * @param days
     *            Retention period of the restored object
     */
    public void setDays(int days) {
        this.days = days;
    }

    @Override
    public String toString() {
        return "RestoreObjectRequest [days=" + days + ", getBucketName()=" + getBucketName()
                + ", getObjectKey()=" + getObjectKey() + "]";
    }
}
