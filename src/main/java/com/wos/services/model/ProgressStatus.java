package com.wos.services.model;

/**
 * Data transfer status
 *
 */
public interface ProgressStatus {

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
     * Obtain the transfer progress
     * 
     * @return Transfer progress
     */
    public int getTransferPercentage();

    /**
     * Obtain the number of new bytes.
     * 
     * @return Number of bytes transferred since last progress refresh
     */
    public long getNewlyTransferredBytes();

    /**
     * Obtain the number of transferred bytes.
     * 
     * @return Number of bytes that have been transferred
     */
    public long getTransferredBytes();

    /**
     * Obtain the number of bytes to be transferred.
     * 
     * @return Number of the total bytes to be transferred
     */
    public long getTotalBytes();
}
