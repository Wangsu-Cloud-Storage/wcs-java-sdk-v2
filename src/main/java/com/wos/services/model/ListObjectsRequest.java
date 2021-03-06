package com.wos.services.model;

/**
 * Parameters in a request for listing objects in a bucket
 */
public class ListObjectsRequest extends GenericRequest {
    private String bucketName;

    private String prefix;

    private String marker;

    private int maxKeys;

    private String delimiter;

    public ListObjectsRequest() {
    }

    /**
     * Constructor
     * 
     * @param bucketName
     *            Bucket name
     */
    public ListObjectsRequest(String bucketName) {
        this.bucketName = bucketName;
    }

    /**
     * Constructor
     * 
     * @param bucketName
     *            Bucket name
     * @param maxKeys
     *            Maximum number of objects to be listed
     */
    public ListObjectsRequest(String bucketName, int maxKeys) {
        this.bucketName = bucketName;
        this.maxKeys = maxKeys;
    }

    /**
     * Constructor
     * 
     * @param bucketName
     *            Bucket name
     * @param prefix
     *            Object name prefix, used for filtering objects to be listed
     * @param marker
     *            Start position for listing objects
     * @param delimiter
     *            Character used for grouping object names
     * @param maxKeys
     *            Maximum number of objects to be listed
     */
    public ListObjectsRequest(String bucketName, String prefix, String marker, String delimiter, int maxKeys) {
        this.bucketName = bucketName;
        this.prefix = prefix;
        this.marker = marker;
        this.delimiter = delimiter;
        this.maxKeys = maxKeys;
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
     * Set the bucket name.
     * 
     * @param bucketName
     *            Bucket name
     */
    public void setBucketName(String bucketName) {
        this.bucketName = bucketName;
    }

    /**
     * Obtain the object name prefix used for filtering objects to be listed.
     * 
     * @return Object name prefix
     */
    public String getPrefix() {
        return prefix;
    }

    /**
     * Set the object name prefix used for filtering objects to be listed.
     * 
     * @param prefix
     *            Object name prefix
     */
    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    /**
     * Obtain the start position for listing objects.
     * 
     * @return Start position marker
     */
    public String getMarker() {
        return marker;
    }

    /**
     * Set the start position for listing objects.
     * 
     * @param marker
     *            Start position marker
     */
    public void setMarker(String marker) {
        this.marker = marker;
    }

    /**
     * Obtain the maximum number of objects to be listed.
     * 
     * @return Maximum number of objects to be listed
     */
    public int getMaxKeys() {
        return maxKeys;
    }

    /**
     * Set the maximum number of objects to be listed.
     * 
     * @param maxKeys
     *            Maximum number of objects to be listed
     */
    public void setMaxKeys(int maxKeys) {
        this.maxKeys = maxKeys;
    }

    /**
     * Obtain the character used for grouping object names.
     * 
     * @return Character for grouping object names
     */
    public String getDelimiter() {
        return delimiter;
    }

    /**
     * Set the character used for grouping object names.
     * 
     * @param delimiter
     *            Character for grouping object names
     */
    public void setDelimiter(String delimiter) {
        this.delimiter = delimiter;
    }

    @Override
    public String toString() {
        return "ListObjectsRequest [bucketName=" + bucketName + ", prefix=" + prefix + ", marker=" + marker
                + ", maxKeys=" + maxKeys + ", delimiter=" + delimiter + "]";
    }

}
