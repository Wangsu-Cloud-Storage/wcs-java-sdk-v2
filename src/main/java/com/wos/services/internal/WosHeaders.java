package com.wos.services.internal;

public class WosHeaders implements IHeaders {

    private WosHeaders() {

    }

    private static WosHeaders instance = new WosHeaders();

    public static IHeaders getInstance() {
        return instance;
    }

    @Override
    public String defaultStorageClassHeader() {
        return this.headerPrefix() + "storage-class";
    }

    @Override
    public String aclHeader() {
        return this.headerPrefix() + "acl";
    }

    @Override
    public String requestIdHeader() {
        return this.headerPrefix() + "request-id";
    }

    @Override
    public String storageClassHeader() {
        return this.headerPrefix() + "storage-class";
    }

    @Override
    public String expiresHeader() {
        return this.headerPrefix() + "expires";
    }

    @Override
    public String metadataDirectiveHeader() {
        return this.headerPrefix() + "metadata-directive";
    }

    @Override
    public String headerPrefix() {
        return Constants.WOS_HEADER_PREFIX;
    }

    @Override
    public String headerMetaPrefix() {
        return Constants.WOS_HEADER_META_PREFIX;
    }

    @Override
    public String dateHeader() {
        return this.headerPrefix() + "date";
    }

    @Override
    public String bucketRegionHeader() {
        return this.headerPrefix() + "bucket-location";
    }

    @Override
    public String deleteMarkerHeader() {
        return this.headerPrefix() + "delete-marker";
    }

    @Override
    public String copySourceIfModifiedSinceHeader() {
        return this.headerPrefix() + "copy-source-if-modified-since";
    }

    @Override
    public String copySourceIfUnmodifiedSinceHeader() {
        return this.headerPrefix() + "copy-source-if-unmodified-since";
    }

    @Override
    public String copySourceIfNoneMatchHeader() {
        return this.headerPrefix() + "copy-source-if-none-match";
    }

    @Override
    public String copySourceIfMatchHeader() {
        return this.headerPrefix() + "copy-source-if-match";
    }

    @Override
    public String copySourceHeader() {
        return this.headerPrefix() + "copy-source";
    }

    @Override
    public String expirationHeader() {
        return this.headerPrefix() + "expiration";
    }

    @Override
    public String copySourceRangeHeader() {
        return this.headerPrefix() + "copy-source-range";
    }

    @Override
    public String contentSha256Header() {
        return this.headerPrefix() + "content-sha256";
    }

    @Override
    public String securityTokenHeader() {
        return this.headerPrefix() + "security-token";
    }
}
