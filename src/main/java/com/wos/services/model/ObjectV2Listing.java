package com.wos.services.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Response to a request for listing objects in a bucket
 */
public class ObjectV2Listing extends HeaderResponse {
    private List<WosObject> objectSummaries;

    private List<String> commonPrefixes;

    private List<WosObject> extenedCommonPrefixes;

    private String bucketName;

    private boolean truncated;

    private String prefix;

    private String nextContinuationToken;

    private String startAfter;

    private String keyCount;

    private int maxKeys;

    private String delimiter;

    private String encodingType;

    private String continuationToken;

    public ObjectV2Listing(List<WosObject> objectSummaries, List<String> commonPrefixes, String bucketName,
                           boolean truncated, String prefix, String nextContinuationToken, String startAfter,
                           String keyCount, int maxKeys, String delimiter, String encodingType,
                           String continuationToken) {
        super();
        this.objectSummaries = objectSummaries;
        this.commonPrefixes = commonPrefixes;
        this.bucketName = bucketName;
        this.truncated = truncated;
        this.prefix = prefix;
        this.nextContinuationToken = nextContinuationToken;
        this.startAfter = startAfter;
        this.keyCount = keyCount;
        this.maxKeys = maxKeys;
        this.delimiter = delimiter;
        this.encodingType = encodingType;
        this.continuationToken = continuationToken;
    }

    public ObjectV2Listing(List<WosObject> objectSummaries, List<String> commonPrefixes, String bucketName,
                           boolean truncated, String prefix, String nextContinuationToken, String startAfter,
                           String keyCount, int maxKeys, String delimiter, String encodingType,
                           String continuationToken, List<WosObject> extenedCommonPrefixes) {
        this(objectSummaries, commonPrefixes, bucketName, truncated, prefix, nextContinuationToken, startAfter, keyCount, maxKeys, delimiter,
                encodingType, continuationToken);
        this.extenedCommonPrefixes = extenedCommonPrefixes;
    }

    /**
     * Obtain the list of objects in the bucket.
     * 
     * @return List of objects in the bucket
     */
    public List<WosObject> getObjects() {
        if (this.objectSummaries == null) {
            this.objectSummaries = new ArrayList<WosObject>();
        }
        return objectSummaries;
    }


    public List<S3Object> getObjectSummaries() {
        List<S3Object> objects = new ArrayList<S3Object>(this.objectSummaries.size());
        objects.addAll(this.objectSummaries);
        return objects;
    }

    /**
     * Obtain the list of prefixes to the names of grouped objects.
     * 
     * @return List of prefixes to the names of grouped objects
     */
    public List<String> getCommonPrefixes() {
        if (this.commonPrefixes == null) {
            this.commonPrefixes = new ArrayList<String>();
        }
        return commonPrefixes;
    }

    /**
     * Obtain the list of prefixes to the names of grouped objects.
     *
     * @return List of prefixes to the names of grouped objects
     */
    public List<WosObject> getExtenedCommonPrefixes() {
        if (this.extenedCommonPrefixes == null) {
            this.extenedCommonPrefixes = new ArrayList<WosObject>();
        }
        return extenedCommonPrefixes;
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
     * Check whether the query result list is truncated. Value "true" indicates
     * that the results are incomplete while value "false" indicates that the
     * results are complete.
     * 
     * @return Truncation identifier
     */
    public boolean isTruncated() {
        return truncated;
    }

    /**
     * Obtain the object name prefix used for filtering objects to be listed.
     * 
     * @return Object name prefix used for listing versioning objects
     */
    public String getPrefix() {
        return prefix;
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
     * Obtain the character for grouping object names.
     * 
     * @return Character for grouping object names
     */
    public String getDelimiter() {
        return delimiter;
    }

    public String getNextContinuationToken() {
        return nextContinuationToken;
    }

    public String getStartAfter() {
        return startAfter;
    }

    public String getKeyCount() {
        return keyCount;
    }

    public String getEncodingType() {
        return encodingType;
    }

    public String getContinuationToken() {
        return continuationToken;
    }

    @Override
    public String toString() {
        return "ObjectListing [objectSummaries=" + objectSummaries + ", commonPrefixes=" + commonPrefixes
                + ", bucketName=" + bucketName + ", truncated=" + truncated + ", prefix=" + prefix + ", nextContinuationToken="
                + nextContinuationToken + ", maxKeys=" + maxKeys + ", delimiter=" + delimiter + ", startAfter=" + startAfter
                + ", keyCount=" + keyCount+ ", encodingType=" + encodingType+ ", continuationToken=" + continuationToken + "]";
    }

}
