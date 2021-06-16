package com.wos.services.internal.ext;

import com.wos.services.WosConfiguration;

public class ExtWosConfiguration extends WosConfiguration {

    private boolean retryOnConnectionFailureInOkhttp;

    // times for retryOnRetryOnUnexpectedEndException;
    private int maxRetryOnUnexpectedEndException;

    public ExtWosConfiguration() {
        super();
        this.retryOnConnectionFailureInOkhttp = ExtWosConstraint.DEFAULT_RETRY_ON_CONNECTION_FAILURE_IN_OKHTTP;
        this.maxRetryOnUnexpectedEndException = ExtWosConstraint.DEFAULT_MAX_RETRY_ON_UNEXPECTED_END_EXCEPTION;
    }

    public boolean isRetryOnConnectionFailureInOkhttp() {
        return retryOnConnectionFailureInOkhttp;
    }

    public void retryOnConnectionFailureInOkhttp(boolean retryOnConnectionFailureInOkhttp) {
        this.retryOnConnectionFailureInOkhttp = retryOnConnectionFailureInOkhttp;
    }

    public int getMaxRetryOnUnexpectedEndException() {
        return maxRetryOnUnexpectedEndException;
    }

    public void setMaxRetryOnUnexpectedEndException(int maxRetryOnUnexpectedEndException) {
        this.maxRetryOnUnexpectedEndException = maxRetryOnUnexpectedEndException;
    }
}
