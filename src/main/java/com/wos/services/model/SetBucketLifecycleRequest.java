package com.wos.services.model;

/**
 * Configure lifecycle rules for a bucket.
 *
 *
 */
public class SetBucketLifecycleRequest extends BaseBucketRequest {
    private LifecycleConfiguration lifecycleConfig;

    public SetBucketLifecycleRequest(String bucketName, LifecycleConfiguration lifecycleConfig) {
        super(bucketName);
        this.lifecycleConfig = lifecycleConfig;
    }

    public LifecycleConfiguration getLifecycleConfig() {
        return lifecycleConfig;
    }

    public void setLifecycleConfig(LifecycleConfiguration lifecycleConfig) {
        this.lifecycleConfig = lifecycleConfig;
    }

    @Override
    public String toString() {
        return "SetBucketLifecycleRequest [lifecycleConfig=" + lifecycleConfig + ", getBucketName()=" + getBucketName()
                + ", isRequesterPays()=" + isRequesterPays() + "]";
    }
}
