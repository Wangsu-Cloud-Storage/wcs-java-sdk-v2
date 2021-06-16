package com.wos.services.model;

/**
 * Parameters in a request for listing objects in a bucket
 */
public class ListObjectsV2Request extends GenericRequest {
    private String bucketName;

    private String prefix;

    private int maxKeys;

    private String delimiter;

    private int listTimeout;

    private String startAfter;

    private boolean fetchOwner;

    private String encodingType;

    private String continuationToken;

    public ListObjectsV2Request() {
    }

    /**
     * Constructor
     *
     * @param bucketName
     *            Bucket name
     */
    public ListObjectsV2Request(String bucketName) {
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
    public ListObjectsV2Request(String bucketName, int maxKeys) {
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
     * @param delimiter
     *            Character used for grouping object names
     * @param maxKeys
     *            Maximum number of objects to be listed
     * @param startAfter
     *            startAfter of objects to be listed
     * @param fetchOwner
     *            fetchOwner of objects to be listed
     * @param encodingType
     *            encodingType of objects to be listed
     * @param continuationToken
     *            continuationToken of objects to be listed
     */
    public ListObjectsV2Request(String bucketName, String prefix, String delimiter, int maxKeys, String startAfter, boolean fetchOwner, String encodingType, String continuationToken) {
        this.bucketName = bucketName;
        this.prefix = prefix;
        this.delimiter = delimiter;
        this.maxKeys = maxKeys;
        this.startAfter = startAfter;
        this.fetchOwner = fetchOwner;
        this.encodingType = encodingType;
        this.continuationToken = continuationToken;
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

    public int getListTimeout() {
        return listTimeout;
    }

    public void setListTimeout(int listTimeout) {
        this.listTimeout = listTimeout;
    }

    public String getStartAfter() {
        return startAfter;
    }

    public void setStartAfter(String startAfter) {
        this.startAfter = startAfter;
    }

    public boolean getFetchOwner() {
        return fetchOwner;
    }

    public void setFetchOwner(boolean fetchOwner) {
        this.fetchOwner = fetchOwner;
    }

    public String getEncodingType() {
        return encodingType;
    }

    public void setEncodingType(String encodingType) {
        this.encodingType = encodingType;
    }

    public String getContinuationToken() {
        return continuationToken;
    }

    public void setContinuationToken(String continuationToken) {
        this.continuationToken = continuationToken;
    }

    @Override
    public String toString() {
        return "ListObjectsRequest [bucketName=" + bucketName + ", prefix=" + prefix
                + ", maxKeys=" + maxKeys + ", delimiter=" + delimiter + ", listTimeout=" + listTimeout
                + ", startAfter=" + startAfter + ", fetchOwner=" + fetchOwner + ", encodingType=" + encodingType
                + ", continuationToken=" + continuationToken + "]";
    }

}
