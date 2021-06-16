package com.wos.services.model;

import java.util.concurrent.ConcurrentHashMap;

/*
 * Status information of the batch uploading of objects
 */
public interface UploadProgressStatus extends TaskProgressStatus {

    /**
     * Obtain the total size of uploaded objects.
     * 
     * @return Total size of uploaded objects. The value -1 indicates that the
     *         total size is still being calculated.
     */
    public long getTotalSize();

    /**
     * Obtain the size of transferred data in bytes.
     * 
     * @return Size of data in bytes that have been transferred
     */
    public long getTransferredSize();

    /**
     * Obtain the instantaneous speed.
     * 
     * @return Instantaneous speed
     */
    public double getInstantaneousSpeed();

    /**
     * Obtain the average speed.
     * 
     * @return Average speed
     */
    public double getAverageSpeed();

    /**
     * Obtain the progress of the current uploading task.
     * 
     * @return taskTable Progress of the current uploading task
     */
    public ConcurrentHashMap<String, ProgressStatus> getTaskTable();

    /**
     * Obtain the upload progress of a specified object.
     * 
     * @param key
     *            Object name
     * @return Upload progress of a specified object
     */
    public ProgressStatus getTaskStatus(String key);
}
