package com.wos.services.model;

/**
 * Basic class of all requests, which encapsulates common parameters used by all requests.
 *
 *
 */
public class GenericRequest {
    /**
     * If the requester-pays function is enabled, the requester pays for his/her operations on the bucket.
     */
    private boolean isRequesterPays;
    
    /**
     * If the requester is allowed to pay, true is returned. Otherwise, false is returned.
     *
     * <p>
     * If the requester-pays function is enabled for a bucket, this attribute must be set to true when the bucket is requested by a requester other than the bucket owner. Otherwise, status code 403 is returned.
     *
     * <p>
     * After the requester-pays function is enabled, anonymous access to the bucket is not allowed.
     *
     * @return If the requester is allowed to pay, true is returned. Otherwise, false is returned.
     */
    public boolean isRequesterPays() {
        return isRequesterPays;
    }

    /**
     * Used to configure whether to enable the requester-pays function.
     *
     * <p>
     * If the requester-pays function is enabled for a bucket, this attribute must be set to true when the bucket is requested by a requester other than the bucket owner. Otherwise, status code 403 is returned.
     *
     * <p>
     * After the requester-pays function is enabled, anonymous access to the bucket is not allowed.
     *
     * @param isRequesterPays True indicates to enable the requester-pays function. False indicates to disable the requester-pays function.
     */
    public void setRequesterPays(boolean isRequesterPays) {
        this.isRequesterPays = isRequesterPays;
    }

    @Override
    public String toString() {
        return "GenericRequest [isRequesterPays=" + isRequesterPays + "]";
    }
}
