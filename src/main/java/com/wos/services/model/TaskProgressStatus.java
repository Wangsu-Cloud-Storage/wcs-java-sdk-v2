package com.wos.services.model;

public interface TaskProgressStatus {

    /**
     * Obtain the upload progress in percentage.
     * 
     * @return Upload progress in percentage
     */
    public int getExecPercentage();

    /**
     * Obtain the number of objects being uploaded.
     * 
     * @return Number of objects being uploaded
     */
    public int getExecTaskNum();

    /**
     * Obtain the number of objects that have been successfully uploaded.
     * 
     * @return Number of objects that have been successfully uploaded
     */
    public int getSucceedTaskNum();

    /**
     * Obtain the number of objects that fail to be uploaded.
     * 
     * @return Number of objects that fail to be uploaded
     */
    public int getFailTaskNum();

    /**
     * Obtain the total number of objects in the upload task.
     * 
     * @return Total number of objects in the upload task
     */
    public int getTotalTaskNum();
}
