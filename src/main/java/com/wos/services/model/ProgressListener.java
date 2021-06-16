package com.wos.services.model;

/**
 * Data transfer listener
 * 
 */
public interface ProgressListener {

    /**
     * Data transfer callback function
     * 
     * @param status
     *            Data transfer status
     */
    public void progressChanged(ProgressStatus status);

}
