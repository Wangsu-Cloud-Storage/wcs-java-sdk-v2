package com.wos.services.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Parameters in an object batch deletion request
 */
public class DeleteObjectsRequest extends GenericRequest {
    private String bucketName;

    private boolean quiet;

    private List<String> objectKeys;

    public DeleteObjectsRequest() {

    }

    /**
     * Constructor
     * 
     * @param bucketName
     *            Bucket name
     */
    public DeleteObjectsRequest(String bucketName) {
        this.bucketName = bucketName;
    }

    /**
     * Constructor
     * 
     * @param bucketName
     *            Bucket name
     * @param quiet
     *            Deletion response mode. "false" indicates that the "verbose"
     *            mode is used and "true" indicates that the "quiet" mode is
     *            used.
     * @param objectKeys
     *            To-be-deleted object array
     */
    public DeleteObjectsRequest(String bucketName, boolean quiet, String[] objectKeys) {
        this.bucketName = bucketName;
        this.quiet = quiet;
        this.setObjectKeys(objectKeys);
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
     * Obtain the response mode of the batch deletion. "false" indicates that
     * the "verbose" mode is used and "true" indicates that the "quiet" mode is
     * used.
     * 
     * @return Response mode of the object batch deletion request
     */
    public boolean isQuiet() {
        return quiet;
    }

    /**
     * Set the response mode for the batch deletion. "false" indicates that the
     * "verbose" mode is used and "true" indicates that the "quiet" mode is
     * used.
     * 
     * @param quiet
     *            Response mode of the object batch deletion request
     */
    public void setQuiet(boolean quiet) {
        this.quiet = quiet;
    }

    /**
     * Obtain the list of to-be-deleted objects.
     * 
     * @return List of to-be-deleted objects
     */
    public List<String> getKeyList() {
        if (this.objectKeys == null) {
            this.objectKeys = new ArrayList<String>();
        }
        return this.objectKeys;
    }

    /**
     * Add an object to be deleted.
     *
     * @param objectKey
     *            Object name
     * @return Object newly added to the deletion list
     */
    public String addKey(String objectKey) {
        this.getKeyList().add(objectKey);
        return objectKey;
    }


    /**
     * Obtain the to-be-deleted object array.
     * 
     * @return To-be-deleted object array
     */
    public String[] getObjectKeys() {
        return this.getKeyList().toArray(new String[this.getKeyList().size()]);
    }

    /**
     * Specify the to-be-deleted object array.
     * 
     * @param objectKeys
     *            To-be-deleted object array
     */
    public void setObjectKeys(String[] objectKeys) {
        if (objectKeys != null && objectKeys.length > 0) {
            this.objectKeys = new ArrayList<String>(Arrays.asList(objectKeys));
        }
    }

    @Override
    public String toString() {
        return "DeleteObjectsRequest [bucketName=" + bucketName + ", quiet=" + quiet + ", objectKeys="
                + this.objectKeys + "]";
    }

}
