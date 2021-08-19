package com.wos.services.internal;

public interface IHeaders {
    String defaultStorageClassHeader();

    String aclHeader();

    String requestIdHeader();

    String bucketRegionHeader();

    String storageClassHeader();

    String expiresHeader();

    String copySourceHeader();

    String copySourceRangeHeader();

    String metadataDirectiveHeader();

    String dateHeader();

    String deleteMarkerHeader();

    String headerPrefix();

    String headerMetaPrefix();

    String contentSha256Header();

    String expirationHeader();

    String copySourceIfModifiedSinceHeader();

    String copySourceIfUnmodifiedSinceHeader();

    String copySourceIfNoneMatchHeader();

    String copySourceIfMatchHeader();

    String securityTokenHeader();

}
