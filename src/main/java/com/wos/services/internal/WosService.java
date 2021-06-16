package com.wos.services.internal;

import com.wos.log.ILogger;
import com.wos.log.LoggerBuilder;
import com.wos.services.internal.Constants.CommonHeaders;
import com.wos.services.internal.Constants.WosRequestParams;
import com.wos.services.internal.handler.XmlResponsesSaxParser;
import com.wos.services.internal.io.HttpMethodReleaseInputStream;
import com.wos.services.internal.io.ProgressInputStream;
import com.wos.services.internal.security.BasicSecurityKey;
import com.wos.services.internal.utils.*;
import com.wos.services.model.*;
import com.wos.services.model.RestoreObjectRequest.RestoreObjectStatus;
import okhttp3.Response;

import java.io.BufferedInputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class WosService extends RequestConvertor {

    private static final ILogger log = LoggerBuilder.getLogger(WosService.class);

    protected WosService() {

    }

    protected void verifyResponseContentType(Response response) throws ServiceException {
        if (this.wosProperties.getBoolProperty(WosConstraint.VERIFY_RESPONSE_CONTENT_TYPE, true)) {
            String contentType = response.header(CommonHeaders.CONTENT_TYPE);
            if (!Mimetypes.MIMETYPE_XML.equalsIgnoreCase(contentType)
                    && !Mimetypes.MIMETYPE_TEXT_XML.equalsIgnoreCase(contentType)) {
                throw new ServiceException(
                        "Expected XML document response from WOS but received content type " + contentType);
            }
        }
    }

    protected InitiateMultipartUploadResult initiateMultipartUploadImpl(InitiateMultipartUploadRequest request)
            throws ServiceException {

        TransResult result = this.transInitiateMultipartUploadRequest(request);

        Response httpResponse = performRestPost(request.getBucketName(), request.getObjectKey(), result.getHeaders(),
                result.getParams(), null, false);

        this.verifyResponseContentType(httpResponse);

        InitiateMultipartUploadResult multipartUpload = getXmlResponseSaxParser()
                .parse(new HttpMethodReleaseInputStream(httpResponse),
                        XmlResponsesSaxParser.InitiateMultipartUploadHandler.class, true)
                .getInitiateMultipartUploadResult();
        setResponseHeaders(multipartUpload, this.cleanResponseHeaders(httpResponse));
        setStatusCode(multipartUpload, httpResponse.code());
        return multipartUpload;
    }

    protected HeaderResponse abortMultipartUploadImpl(AbortMultipartUploadRequest request) throws ServiceException {
        Map<String, String> requestParameters = new HashMap<String, String>();
        requestParameters.put(WosRequestParams.UPLOAD_ID, request.getUploadId());

        Response response = performRestDelete(request.getBucketName(), request.getObjectKey(), requestParameters,
                null);
        HeaderResponse ret = build(this.cleanResponseHeaders(response));
        setStatusCode(ret, response.code());
        return ret;
    }

    protected CompleteMultipartUploadResult completeMultipartUploadImpl(CompleteMultipartUploadRequest request)
            throws ServiceException {
        Map<String, String> requestParameters = new HashMap<String, String>();
        requestParameters.put(WosRequestParams.UPLOAD_ID, request.getUploadId());

        Map<String, String> metadata = new HashMap<String, String>();
        metadata.put(CommonHeaders.CONTENT_TYPE, Mimetypes.MIMETYPE_XML);

        Response response = performRestPost(request.getBucketName(), request.getObjectKey(), metadata,
                requestParameters, createRequestBody(Mimetypes.MIMETYPE_XML,
                        this.getIConvertor().transCompleteMultipartUpload(request.getPartEtag())),
                false);

        this.verifyResponseContentType(response);

        XmlResponsesSaxParser.CompleteMultipartUploadHandler handler = getXmlResponseSaxParser().parse(
                new HttpMethodReleaseInputStream(response), XmlResponsesSaxParser.CompleteMultipartUploadHandler.class,
                true);

        CompleteMultipartUploadResult ret = new CompleteMultipartUploadResult(handler.getBucketName(),
                handler.getObjectKey(), handler.getEtag(), handler.getLocation(),
                this.getObjectUrl(handler.getBucketName(), handler.getObjectKey()));
        setResponseHeaders(ret, this.cleanResponseHeaders(response));
        setStatusCode(ret, response.code());
        return ret;
    }

    protected MultipartUploadListing listMultipartUploadsImpl(ListMultipartUploadsRequest request)
            throws ServiceException {
        Map<String, String> requestParameters = new HashMap<String, String>();
        requestParameters.put(SpecialParamEnum.UPLOADS.getOriginalStringCode(), "");
        if (request.getPrefix() != null) {
            requestParameters.put(WosRequestParams.PREFIX, request.getPrefix());
        }
        if (request.getDelimiter() != null) {
            requestParameters.put(WosRequestParams.DELIMITER, request.getDelimiter());
        }
        if (request.getMaxUploads() != null) {
            requestParameters.put(WosRequestParams.MAX_UPLOADS, request.getMaxUploads().toString());
        }
        if (request.getKeyMarker() != null) {
            requestParameters.put(WosRequestParams.KEY_MARKER, request.getKeyMarker());
        }
        if (request.getUploadIdMarker() != null) {
            requestParameters.put(WosRequestParams.UPLOAD_ID_MARKER, request.getUploadIdMarker());
        }

        Response httpResponse = performRestGet(request.getBucketName(), null, requestParameters,
                null);

        this.verifyResponseContentType(httpResponse);

        XmlResponsesSaxParser.ListMultipartUploadsHandler handler = getXmlResponseSaxParser().parse(
                new HttpMethodReleaseInputStream(httpResponse), XmlResponsesSaxParser.ListMultipartUploadsHandler.class,
                true);

        MultipartUploadListing listResult = new MultipartUploadListing(
                handler.getBucketName() == null ? request.getBucketName() : handler.getBucketName(),
                handler.getKeyMarker() == null ? request.getKeyMarker() : handler.getKeyMarker(),
                handler.getUploadIdMarker() == null ? request.getUploadIdMarker() : handler.getUploadIdMarker(),
                handler.getNextKeyMarker(), handler.getNextUploadIdMarker(),
                handler.getPrefix() == null ? request.getPrefix() : handler.getPrefix(), handler.getMaxUploads(),
                handler.isTruncated(), handler.getMultipartUploadList(),
                handler.getDelimiter() == null ? request.getDelimiter() : handler.getDelimiter(),
                handler.getCommonPrefixes().toArray(new String[handler.getCommonPrefixes().size()]));
        setResponseHeaders(listResult, this.cleanResponseHeaders(httpResponse));
        setStatusCode(listResult, httpResponse.code());
        return listResult;
    }

    protected ListPartsResult listPartsImpl(ListPartsRequest request) throws ServiceException {
        Map<String, String> requestParameters = new HashMap<String, String>();
        requestParameters.put(WosRequestParams.UPLOAD_ID, request.getUploadId());
        if (null != request.getMaxParts()) {
            requestParameters.put(WosRequestParams.MAX_PARTS, request.getMaxParts().toString());
        }
        if (null != request.getPartNumberMarker()) {
            requestParameters.put(WosRequestParams.PART_NUMBER_MARKER, request.getPartNumberMarker().toString());
        }

        Response httpResponse = performRestGet(request.getBucketName(), request.getKey(), requestParameters,
                null);

        this.verifyResponseContentType(httpResponse);

        XmlResponsesSaxParser.ListPartsHandler handler = getXmlResponseSaxParser().parse(
                new HttpMethodReleaseInputStream(httpResponse), XmlResponsesSaxParser.ListPartsHandler.class, true);

        ListPartsResult result = new ListPartsResult(
                handler.getBucketName() == null ? request.getBucketName() : handler.getBucketName(),
                handler.getObjectKey() == null ? request.getKey() : handler.getObjectKey(),
                handler.getUploadId() == null ? request.getUploadId() : handler.getUploadId(),
                handler.getInitiator(), handler
                        .getOwner(),
                StorageClassEnum.getValueFromCode(handler.getStorageClass()), handler.getMultiPartList(),
                handler.getMaxParts(), handler.isTruncated(),
                handler.getPartNumberMarker() == null
                        ? (request.getPartNumberMarker() == null ? null : request.getPartNumberMarker().toString())
                        : handler.getPartNumberMarker(),
                handler.getNextPartNumberMarker());
        setResponseHeaders(result, this.cleanResponseHeaders(httpResponse));
        setStatusCode(result, httpResponse.code());
        return result;
    }

    protected LifecycleConfiguration getBucketLifecycleConfigurationImpl(BaseBucketRequest request)
            throws ServiceException {
        Map<String, String> requestParameters = new HashMap<String, String>();
        requestParameters.put(SpecialParamEnum.LIFECYCLE.getOriginalStringCode(), "");

        Response response = performRestGet(request.getBucketName(), null, requestParameters,
                null);

        this.verifyResponseContentType(response);

        LifecycleConfiguration ret = getXmlResponseSaxParser().parse(new HttpMethodReleaseInputStream(response),
                XmlResponsesSaxParser.BucketLifecycleConfigurationHandler.class, false).getLifecycleConfig();
        setResponseHeaders(ret, this.cleanResponseHeaders(response));
        setStatusCode(ret, response.code());
        return ret;
    }

    protected HeaderResponse setBucketLifecycleConfigurationImpl(SetBucketLifecycleRequest request)
            throws ServiceException {
        Map<String, String> requestParameters = new HashMap<String, String>();
        requestParameters.put(SpecialParamEnum.LIFECYCLE.getOriginalStringCode(), "");

        Map<String, String> metadata = new HashMap<String, String>();
        String xml = this.getIConvertor().transLifecycleConfiguration(request.getLifecycleConfig());
        metadata.put(CommonHeaders.CONTENT_MD5, ServiceUtils.computeMD5(xml));
        metadata.put(CommonHeaders.CONTENT_TYPE, Mimetypes.MIMETYPE_XML);

        Response response = performRestPut(request.getBucketName(), null, metadata, requestParameters,
                createRequestBody(Mimetypes.MIMETYPE_XML, xml), true);

        HeaderResponse ret = build(this.cleanResponseHeaders(response));
        setStatusCode(ret, response.code());
        return ret;
    }

    protected HeaderResponse deleteBucketLifecycleConfigurationImpl(BaseBucketRequest request) throws ServiceException {
        Map<String, String> requestParameters = new HashMap<String, String>();
        requestParameters.put(SpecialParamEnum.LIFECYCLE.getOriginalStringCode(), "");
        Response response = performRestDelete(request.getBucketName(), null, requestParameters,
                null);
        HeaderResponse ret = build(this.cleanResponseHeaders(response));
        setStatusCode(ret, response.code());
        return ret;
    }

    protected DeleteObjectsResult deleteObjectsImpl(DeleteObjectsRequest deleteObjectsRequest) throws ServiceException {
        if (deleteObjectsRequest.getKeyList() != null && deleteObjectsRequest.getKeyList().size() > 1000) {
            throw new ServiceException("delete object over 1000");
        }
        String xml = this.getIConvertor().transKey(deleteObjectsRequest.getObjectKeys(),
                deleteObjectsRequest.isQuiet());
        Map<String, String> metadata = new HashMap<String, String>();
        metadata.put(CommonHeaders.CONTENT_MD5, ServiceUtils.computeMD5(xml));
        metadata.put(CommonHeaders.CONTENT_TYPE, Mimetypes.MIMETYPE_XML);
        Map<String, String> requestParameters = new HashMap<String, String>();
        requestParameters.put(SpecialParamEnum.DELETE.getOriginalStringCode(), "");

        Response httpResponse = performRestPost(deleteObjectsRequest.getBucketName(), null, metadata, requestParameters,
                createRequestBody(Mimetypes.MIMETYPE_XML, xml), false);
        this.verifyResponseContentType(httpResponse);

        DeleteObjectsResult ret = getXmlResponseSaxParser().parse(new HttpMethodReleaseInputStream(httpResponse),
                XmlResponsesSaxParser.DeleteObjectsHandler.class, true).getMultipleDeleteResult();
        setResponseHeaders(ret, this.cleanResponseHeaders(httpResponse));
        setStatusCode(ret, httpResponse.code());
        return ret;
    }

    protected boolean headBucketImpl(BaseBucketRequest request) throws ServiceException {
        try {
            performRestHead(request.getBucketName(), null, null,
                    null);
            return true;
        } catch (ServiceException e) {
            if (e.getResponseCode() == 404) {
                return false;
            }
            throw e;
        }
    }

    protected RestoreObjectStatus restoreObjectImpl(RestoreObjectRequest restoreObjectRequest) throws ServiceException {
        RestoreObjectResult restoreObjectResult = restoreObjectV2Impl(restoreObjectRequest);
        return transRestoreObjectResultToRestoreObjectStatus(restoreObjectResult);
    }

    protected RestoreObjectResult restoreObjectV2Impl(RestoreObjectRequest restoreObjectRequest)
            throws ServiceException {
        Map<String, String> requestParameters = new HashMap<String, String>();
        requestParameters.put(SpecialParamEnum.RESTORE.getOriginalStringCode(), "");
        Map<String, String> metadata = new HashMap<String, String>();
        String requestXmlElement = this.getIConvertor().transRestoreObjectRequest(restoreObjectRequest);
        metadata.put(CommonHeaders.CONTENT_MD5, ServiceUtils.computeMD5(requestXmlElement));
        metadata.put(CommonHeaders.CONTENT_TYPE, Mimetypes.MIMETYPE_XML);

        Response response = this.performRestPost(restoreObjectRequest.getBucketName(),
                restoreObjectRequest.getObjectKey(), metadata, requestParameters,
                createRequestBody(Mimetypes.MIMETYPE_XML, requestXmlElement), true);
        RestoreObjectResult ret = new RestoreObjectResult(restoreObjectRequest.getBucketName(),
                restoreObjectRequest.getObjectKey());
        setResponseHeaders(ret, this.cleanResponseHeaders(response));
        setStatusCode(ret, response.code());
        return ret;
    }

    protected PostSignatureResponse createPostSignatureResponse(PostSignatureRequest request)
            throws Exception {
        BasicSecurityKey securityKey = this.getProviderCredentials().getSecurityKey();
        String accessKey = securityKey.getAccessKey();
        String secretKey = securityKey.getSecretKey();
        Date requestDate = request.getRequestDate() != null ? request.getRequestDate() : new Date();
        SimpleDateFormat expirationDateFormat = ServiceUtils.getExpirationDateFormat();
        Date expiryDate = request.getExpiryDate() == null ? new Date(requestDate.getTime()
                + (request.getExpires() <= 0 ? WosConstraint.DEFAULT_EXPIRE_SECONEDS : request.getExpires()) * 1000)
                : request.getExpiryDate();

        String expiration = expirationDateFormat.format(expiryDate);

        StringBuilder originPolicy = new StringBuilder();
        originPolicy.append("{\"expiration\":").append("\"").append(expiration).append("\",")
                .append("\"conditions\":[");

        String shortDate = ServiceUtils.getShortDateFormat().format(requestDate);
        String longDate = ServiceUtils.getLongDateFormat().format(requestDate);
        String credential = this.getCredential(shortDate, accessKey);
        if (request.getConditions() != null && !request.getConditions().isEmpty()) {
            originPolicy.append(ServiceUtils.join(request.getConditions(), ",")).append(",");
        } else {
            Map<String, Object> params = new TreeMap<String, Object>();

            params.put(Constants.WOS_HEADER_PREFIX_CAMEL + "Algorithm", Constants.V4_ALGORITHM);
            params.put(Constants.WOS_HEADER_PREFIX_CAMEL + "Date", longDate);
            params.put(Constants.WOS_HEADER_PREFIX_CAMEL + "Credential", credential);

            params.putAll(request.getFormParams());

            if (ServiceUtils.isValid(request.getBucketName())) {
                params.put("bucket", request.getBucketName());
            }

            if (ServiceUtils.isValid(request.getObjectKey())) {
                params.put("key", request.getObjectKey());
            }

            boolean matchAnyBucket = true;
            boolean matchAnyKey = true;

            for (Map.Entry<String, Object> entry : params.entrySet()) {
                if (ServiceUtils.isValid(entry.getKey())) {
                    String key = entry.getKey().toLowerCase().trim();

                    if (key.equals("bucket")) {
                        matchAnyBucket = false;
                    } else if (key.equals("key")) {
                        matchAnyKey = false;
                    }

                    if (!Constants.ALLOWED_REQUEST_HTTP_HEADER_METADATA_NAMES.contains(key)
                            && !key.startsWith(this.getRestHeaderPrefix())
                            && !key.startsWith(Constants.WOS_HEADER_PREFIX) && !key.equals("acl")
                            && !key.equals("bucket") && !key.equals("key") && !key.equals("success_action_redirect")
                            && !key.equals("redirect") && !key.equals("success_action_status")) {
                        continue;
                    }
                    String value = entry.getValue() == null ? "" : entry.getValue().toString();
                    originPolicy.append("{\"").append(key).append("\":").append("\"").append(value).append("\"},");
                }
            }

            if (matchAnyBucket) {
                originPolicy.append("[\"starts-with\", \"$bucket\", \"\"],");
            }

            if (matchAnyKey) {
                originPolicy.append("[\"starts-with\", \"$key\", \"\"],");
            }

        }

        originPolicy.append("]}");
        String policy = ServiceUtils.toBase64(originPolicy.toString().getBytes(Constants.DEFAULT_ENCODING));

        String signature = V4Authentication.caculateSignature(policy, shortDate, secretKey);
        return new PostSignatureResponse(policy, originPolicy.toString(), Constants.V4_ALGORITHM, credential,
                longDate, signature, expiration);
    }

    protected TemporarySignatureResponse createTemporarySignature(TemporarySignatureRequest request)
            throws Exception {
        StringBuilder canonicalUri = new StringBuilder();
        String bucketName = request.getBucketName();
        String endpoint = this.getEndpoint();
        String objectKey = request.getObjectKey();

        if (!this.isCname()) {
            if (ServiceUtils.isValid(bucketName)) {
                if (this.isPathStyle() || !ServiceUtils.isBucketNameValidDNSName(bucketName)) {
                    canonicalUri.append("/").append(bucketName.trim());
                } else {
                    endpoint = bucketName.trim() + "." + endpoint;
                }
                if (ServiceUtils.isValid(objectKey)) {
                    canonicalUri.append("/").append(RestUtils.uriEncode(objectKey, false));
                }
            }
        } else {
            if (ServiceUtils.isValid(objectKey)) {
                canonicalUri.append("/").append(RestUtils.uriEncode(objectKey, false));
            }
        }

        if (this.isCname()) {
            endpoint = this.getEndpoint();
        }

        Map<String, String> headers = new TreeMap<String, String>();
        headers.putAll(request.getHeaders());
        Map<String, Object> queryParams = new TreeMap<String, Object>();
        queryParams.putAll(request.getQueryParams());

        Date requestDate = request.getRequestDate();
        if (requestDate == null) {
            requestDate = new Date();
        }
        if ((this.getHttpsOnly() && this.getHttpsPort() == 443) || (!this.getHttpsOnly() && this.getHttpPort() == 80)) {
            headers.put(CommonHeaders.HOST, endpoint);
        } else {
            headers.put(CommonHeaders.HOST,
                    endpoint + ":" + (this.getHttpsOnly() ? this.getHttpsPort() : this.getHttpPort()));
        }

        BasicSecurityKey securityKey = this.getProviderCredentials().getSecurityKey();
        String accessKey = securityKey.getAccessKey();
        String secretKey = securityKey.getSecretKey();

        String requestMethod = request.getMethod() != null ? request.getMethod().getOperationType() : "GET";

        StringBuilder signedHeaders = new StringBuilder();
        StringBuilder canonicalHeaders = new StringBuilder();
        int index = 0;
        Map<String, String> actualSignedRequestHeaders = new TreeMap<String, String>();
        for (Map.Entry<String, String> entry : headers.entrySet()) {
            if (ServiceUtils.isValid(entry.getKey())) {
                String key = entry.getKey().toLowerCase().trim();
                boolean validKey = false;
                if (Constants.ALLOWED_REQUEST_HTTP_HEADER_METADATA_NAMES.contains(key)
                        || key.startsWith(this.getRestHeaderPrefix()) || key.startsWith(Constants.WOS_HEADER_PREFIX)) {
                    validKey = true;
                } else if (requestMethod.equals("PUT") || requestMethod.equals("POST")) {
                    key = this.getRestMetadataPrefix() + key;
                    validKey = true;
                }
                if (validKey) {
                    String value = entry.getValue() == null ? "" : entry.getValue().trim();
                    if (key.startsWith(this.getRestMetadataPrefix())) {
                        value = RestUtils.uriEncode(value, true);
                    }
                    signedHeaders.append(key);
                    canonicalHeaders.append(key).append(":").append(value).append("\n");
                    if (index++ != headers.size() - 1) {
                        signedHeaders.append(";");
                    }
                    actualSignedRequestHeaders.put(entry.getKey().trim(), value);
                }
            }
        }

        String shortDate = ServiceUtils.getShortDateFormat().format(requestDate);
        String longDate = ServiceUtils.getLongDateFormat().format(requestDate);

        queryParams.put(Constants.WOS_HEADER_PREFIX_CAMEL + "Algorithm", Constants.V4_ALGORITHM);
        queryParams.put(Constants.WOS_HEADER_PREFIX_CAMEL + "Credential", this.getCredential(shortDate, accessKey));
        queryParams.put(Constants.WOS_HEADER_PREFIX_CAMEL + "Date", longDate);
        queryParams.put(Constants.WOS_HEADER_PREFIX_CAMEL + "Expires",
                request.getExpires() <= 0 ? WosConstraint.DEFAULT_EXPIRE_SECONEDS : request.getExpires());
        queryParams.put(Constants.WOS_HEADER_PREFIX_CAMEL + "SignedHeaders", signedHeaders.toString());

        StringBuilder canonicalQueryString = new StringBuilder();

        StringBuilder signedUrl = new StringBuilder();
        if (this.getHttpsOnly()) {
            String securePortStr = this.getHttpsPort() == 443 ? "" : ":" + this.getHttpsPort();
            signedUrl.append("https://").append(endpoint).append(securePortStr);
        } else {
            String insecurePortStr = this.getHttpPort() == 80 ? "" : ":" + this.getHttpPort();
            signedUrl.append("http://").append(endpoint).append(insecurePortStr);
        }
        signedUrl.append(canonicalUri).append("?");

        if (request.getSpecialParam() != null) {
            queryParams.put(request.getSpecialParam().getOriginalStringCode(), null);
        }

        index = 0;
        for (Map.Entry<String, Object> entry : queryParams.entrySet()) {
            if (ServiceUtils.isValid(entry.getKey())) {
                String key = RestUtils.uriEncode(entry.getKey(), false);

                canonicalQueryString.append(key).append("=");
                signedUrl.append(key);
                if (entry.getValue() != null) {
                    String value = RestUtils.uriEncode(entry.getValue().toString(), false);
                    canonicalQueryString.append(value);
                    signedUrl.append("=").append(value);
                } else {
                    canonicalQueryString.append("");
                }
                if (index++ != queryParams.size() - 1) {
                    canonicalQueryString.append("&");
                    signedUrl.append("&");
                }
            }
        }

        StringBuilder canonicalRequest = new StringBuilder(requestMethod).append("\n")
                .append(canonicalUri.length() == 0 ? "/" : canonicalUri).append("\n").append(canonicalQueryString)
                .append("\n").append(canonicalHeaders).append("\n").append(signedHeaders).append("\n")
                .append("UNSIGNED-PAYLOAD");

        StringBuilder stringToSign = new StringBuilder(Constants.V4_ALGORITHM).append("\n").append(longDate)
                .append("\n").append(shortDate).append("/").append(WosConstraint.DEFAULT_BUCKET_LOCATION_VALUE)
                .append("/").append(Constants.SERVICE).append("/").append(Constants.REQUEST_TAG).append("\n")
                .append(V4Authentication.byteToHex((V4Authentication.sha256encode(canonicalRequest.toString()))));
        signedUrl.append("&").append(Constants.WOS_HEADER_PREFIX_CAMEL).append("Signature=")
                .append(V4Authentication.caculateSignature(stringToSign.toString(), shortDate, secretKey));
        TemporarySignatureResponse response = new TemporarySignatureResponse(signedUrl.toString());
        response.getActualSignedRequestHeaders().putAll(actualSignedRequestHeaders);
        return response;
    }

    protected PutObjectResult putObjectImpl(PutObjectRequest request) throws ServiceException {

        TransResult result = null;
        Response response;
        try {
            result = this.transPutObjectRequest(request);

            response = performRestPut(request.getBucketName(), request.getObjectKey(), result.getHeaders(), null,
                    result.getBody(), true);
        } finally {
            if (result != null && result.getBody() != null && request.isAutoClose()) {
                if (result.getBody() instanceof Closeable) {
                    ServiceUtils.closeStream((Closeable) result.getBody());
                }
            }
        }

        PutObjectResult ret = new PutObjectResult(request.getBucketName(), request.getObjectKey(),
                response.header(CommonHeaders.ETAG),
                StorageClassEnum.getValueFromCode(response.header(this.getIHeaders().storageClassHeader())),
                this.getObjectUrl(request.getBucketName(), request.getObjectKey()));
        Map<String, Object> map = this.cleanResponseHeaders(response);
        setResponseHeaders(ret, map);
        setStatusCode(ret, response.code());
        return ret;
    }

    protected CopyObjectResult copyObjectImpl(CopyObjectRequest request) throws ServiceException {

        TransResult result = this.transCopyObjectRequest(request);

        Response response = performRestPut(request.getDestinationBucketName(), request.getDestinationObjectKey(),
                result.getHeaders(), null, null, false);

        this.verifyResponseContentType(response);

        XmlResponsesSaxParser.CopyObjectResultHandler handler = getXmlResponseSaxParser().parse(
                new HttpMethodReleaseInputStream(response), XmlResponsesSaxParser.CopyObjectResultHandler.class, false);
        CopyObjectResult copyRet = new CopyObjectResult(handler.getETag(), handler.getLastModified(),
                StorageClassEnum.getValueFromCode(response.header(this.getIHeaders().storageClassHeader())));
        Map<String, Object> map = this.cleanResponseHeaders(response);
        setResponseHeaders(copyRet, map);
        setStatusCode(copyRet, response.code());

        return copyRet;
    }

    protected WosFSAttribute getObjectMetadataImpl(GetObjectMetadataRequest request) throws ServiceException {
        Map<String, String> headers = new HashMap<String, String>();

        Map<String, String> params = new HashMap<String, String>();
        return (WosFSAttribute) this.getObjectImpl(true, request.getBucketName(), request.getObjectKey(), headers,
                params, null, -1);
    }

    protected boolean doesObjectExistImpl(GetObjectMetadataRequest request) throws ServiceException {
        Map<String, String> headers = new HashMap<String, String>();

        Map<String, String> params = new HashMap<String, String>();
        boolean doesObjectExist = false;
        try {
            Response response = performRestHead(request.getBucketName(), request.getObjectKey(), params, headers);
            if (200 == response.code()) {
                doesObjectExist = true;
            }
        } catch (ServiceException ex) {
            if (404 == ex.getResponseCode()) {
                doesObjectExist = false;
            } else {
                throw ex;
            }
        }
        return doesObjectExist;
    }

    protected ObjectMetadata setObjectMetadataImpl(SetObjectMetadataRequest request) {
        TransResult result = this.transSetObjectMetadataRequest(request);
        Response response = performRestPut(request.getBucketName(), request.getObjectKey(), result.getHeaders(),
                result.getParams(), result.getBody(), true);
        return this.getWosFSAttributeFromResponse(response);
    }

    protected WosObject getObjectImpl(GetObjectRequest request) throws ServiceException {
        TransResult result = this.transGetObjectRequest(request);
        if (request.getRequestParameters() != null) {
            result.getParams().putAll(request.getRequestParameters());
        }
        return (WosObject) this.getObjectImpl(false, request.getBucketName(), request.getObjectKey(),
                result.getHeaders(), result.getParams(), request.getProgressListener(), request.getProgressInterval());
    }

    private WosFSAttribute getWosFSAttributeFromResponse(Response response) {
        Date lastModifiedDate = null;
        String lastModified = response.header(CommonHeaders.LAST_MODIFIED);
        if (lastModified != null) {
            try {
                lastModifiedDate = ServiceUtils.parseRfc822Date(lastModified);
            } catch (ParseException e) {
                if (log.isWarnEnabled()) {
                    log.warn("Response last-modified is not well-format", e);
                }
            }
        }
        WosFSAttribute objMetadata = new WosFSAttribute();
        objMetadata.setLastModified(lastModifiedDate);
        objMetadata.setContentEncoding(response.header(CommonHeaders.CONTENT_ENCODING));
        objMetadata.setContentType(response.header(CommonHeaders.CONTENT_TYPE));
        String contentLength = response.header(CommonHeaders.CONTENT_LENGTH);
        if (contentLength != null) {
            try {
                objMetadata.setContentLength(Long.parseLong(contentLength));
            } catch (NumberFormatException e) {
                if (log.isWarnEnabled()) {
                    log.warn("Response content-length is not well-format", e);
                }
            }
        }
        objMetadata.setObjectStorageClass(
                StorageClassEnum.getValueFromCode(response.header(this.getIHeaders().storageClassHeader())));

        String etag = response.header(CommonHeaders.ETAG);
        objMetadata.setEtag(etag);
        if (etag != null && !etag.contains("-")) {
            String md5 = etag;
            if (md5.startsWith("\"")) {
                md5 = md5.substring(1);
            }
            if (md5.endsWith("\"")) {
                md5 = md5.substring(0, md5.length() - 1);
            }
            try {
                objMetadata.setContentMd5(ServiceUtils.toBase64(ServiceUtils.fromHex(md5)));
            } catch (Exception e) {
                if (log.isDebugEnabled()) {
                    log.debug(e.getMessage(), e);
                }
            }
        }

        objMetadata.getMetadata().putAll(this.cleanResponseHeaders(response));
        setStatusCode(objMetadata, response.code());
        return objMetadata;
    }

    protected Object getObjectImpl(boolean headOnly, String bucketName, String objectKey, Map<String, String> headers,
            Map<String, String> params, ProgressListener progressListener, long progressInterval)
                    throws ServiceException {
        Response response;
        if (headOnly) {
            response = performRestHead(bucketName, objectKey, params, headers);
        } else {
            response = performRestGet(bucketName, objectKey, params, headers);
        }

        WosFSAttribute objMetadata = this.getWosFSAttributeFromResponse(response);

        if (headOnly) {
            response.close();
            return objMetadata;
        }
        ReadFileResult wosObject = new ReadFileResult();
        wosObject.setObjectKey(objectKey);
        wosObject.setBucketName(bucketName);
        wosObject.setMetadata(objMetadata);
        // pmd error message: CloseResource - Ensure that resources like this
        // InputStream object are closed after use
        // 该接口是下载对象，需要将流返回给客户（调用方），我们不能关闭这个流
        InputStream input = response.body().byteStream(); // NOPMD
        if (progressListener != null) {
            ProgressManager progressManager = new SimpleProgressManager(objMetadata.getContentLength(), 0,
                    progressListener,
                    progressInterval > 0 ? progressInterval : WosConstraint.DEFAULT_PROGRESS_INTERVAL);
            input = new ProgressInputStream(input, progressManager);
        }

        int readBufferSize = wosProperties.getIntProperty(WosConstraint.READ_BUFFER_SIZE,
                WosConstraint.DEFAULT_READ_BUFFER_STREAM);
        if (readBufferSize > 0) {
            input = new BufferedInputStream(input, readBufferSize);
        }

        wosObject.setObjectContent(input);
        return wosObject;
    }

    protected AccessControlList getBucketAclImpl(BaseBucketRequest request) throws ServiceException {
        Map<String, String> requestParameters = new HashMap<String, String>();
        requestParameters.put(SpecialParamEnum.ACL.getOriginalStringCode(), "");

        Response httpResponse = performRestGet(request.getBucketName(), null, requestParameters,
                null);

        this.verifyResponseContentType(httpResponse);

        AccessControlList ret = getXmlResponseSaxParser().parse(new HttpMethodReleaseInputStream(httpResponse),
                XmlResponsesSaxParser.AccessControlListHandler.class, false).getAccessControlList();
        setResponseHeaders(ret, this.cleanResponseHeaders(httpResponse));
        setStatusCode(ret, httpResponse.code());
        return ret;
    }

    protected AccessControlList getObjectAclImpl(GetObjectAclRequest getObjectAclRequest) throws ServiceException {
        Map<String, String> requestParameters = new HashMap<String, String>();
        requestParameters.put(SpecialParamEnum.ACL.getOriginalStringCode(), "");

        Response httpResponse = performRestGet(getObjectAclRequest.getBucketName(), getObjectAclRequest.getObjectKey(),
                requestParameters, null);

        this.verifyResponseContentType(httpResponse);

        AccessControlList ret = getXmlResponseSaxParser().parse(new HttpMethodReleaseInputStream(httpResponse),
                XmlResponsesSaxParser.AccessControlListHandler.class, false).getAccessControlList();
        setResponseHeaders(ret, this.cleanResponseHeaders(httpResponse));
        setStatusCode(ret, httpResponse.code());
        return ret;
    }

    protected String getObjectAvinfoImpl(GetObjectAvinfoRequest getObjectAvinfoRequest) throws ServiceException {
        Map<String, String> requestParameters = new HashMap<String, String>();
        requestParameters.put(SpecialParamEnum.AVINFO.getOriginalStringCode(), "");

        Response httpResponse = performRestGet(getObjectAvinfoRequest.getBucketName(), getObjectAvinfoRequest.getObjectKey(),
                requestParameters, null);

        this.verifyResponseContentType(httpResponse);

        /*AccessControlList ret = getXmlResponseSaxParser().parse(new HttpMethodReleaseInputStream(httpResponse),
                XmlResponsesSaxParser.AccessControlListHandler.class, false).getAccessControlList();
        setResponseHeaders(ret, this.cleanResponseHeaders(httpResponse));
        setStatusCode(ret, httpResponse.code());*/
        try {
            return httpResponse.body().string();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    protected DeleteObjectResult deleteObjectImpl(DeleteObjectRequest request) throws ServiceException {
        Map<String, String> requestParameters = new HashMap<String, String>();

        Response response = performRestDelete(request.getBucketName(), request.getObjectKey(), requestParameters,
                null);

        DeleteObjectResult result = new DeleteObjectResult(
                Boolean.valueOf(response.header(this.getIHeaders().deleteMarkerHeader())), request.getObjectKey());
        Map<String, Object> map = this.cleanResponseHeaders(response);
        setResponseHeaders(result, map);
        setStatusCode(result, response.code());
        return result;
    }

    protected ObjectListing listObjectsImpl(ListObjectsRequest listObjectsRequest) throws ServiceException {

        TransResult result = this.transListObjectsRequest(listObjectsRequest);

        Response httpResponse = performRestGet(listObjectsRequest.getBucketName(), null, result.getParams(),
                result.getHeaders());

        this.verifyResponseContentType(httpResponse);

        XmlResponsesSaxParser.ListObjectsHandler listObjectsHandler = getXmlResponseSaxParser().parse(
                new HttpMethodReleaseInputStream(httpResponse), XmlResponsesSaxParser.ListObjectsHandler.class, true);

        ObjectListing objList = new ObjectListing(listObjectsHandler.getObjects(),
                listObjectsHandler.getCommonPrefixes(),
                listObjectsHandler.getBucketName() == null ? listObjectsRequest.getBucketName()
                        : listObjectsHandler.getBucketName(),
                listObjectsHandler.isListingTruncated(),
                listObjectsHandler.getRequestPrefix() == null ? listObjectsRequest.getPrefix()
                        : listObjectsHandler.getRequestPrefix(),
                listObjectsHandler.getRequestMarker() == null ? listObjectsRequest.getMarker()
                        : listObjectsHandler.getRequestMarker(),
                listObjectsHandler.getRequestMaxKeys(),
                listObjectsHandler.getRequestDelimiter() == null ? listObjectsRequest.getDelimiter()
                        : listObjectsHandler.getRequestDelimiter(),
                listObjectsHandler.getMarkerForNextListing(),
                httpResponse.header(this.getIHeaders().bucketRegionHeader()),
                listObjectsHandler.getExtenedCommonPrefixes());
        setResponseHeaders(objList, this.cleanResponseHeaders(httpResponse));
        setStatusCode(objList, httpResponse.code());
        return objList;
    }

    protected ObjectV2Listing listObjectsV2Impl(ListObjectsV2Request listObjectsV2Request) throws ServiceException {

        TransResult result = this.transListObjectsV2Request(listObjectsV2Request);

        Response httpResponse = performRestGet(listObjectsV2Request.getBucketName(), null, result.getParams(),
                result.getHeaders());

        this.verifyResponseContentType(httpResponse);

        XmlResponsesSaxParser.ListObjectsV2Handler listObjectsV2Handler = getXmlResponseSaxParser().parse(
                new HttpMethodReleaseInputStream(httpResponse), XmlResponsesSaxParser.ListObjectsV2Handler.class, true);

        ObjectV2Listing objV2List = new ObjectV2Listing(listObjectsV2Handler.getObjects(),
                listObjectsV2Handler.getCommonPrefixes(),
                listObjectsV2Handler.getBucketName() == null ? listObjectsV2Request.getBucketName()
                        : listObjectsV2Handler.getBucketName(),
                listObjectsV2Handler.isListingTruncated(),
                listObjectsV2Handler.getRequestPrefix() == null ? listObjectsV2Request.getPrefix()
                        : listObjectsV2Handler.getRequestPrefix(),
                listObjectsV2Handler.getNextContinuationToken() == null ? listObjectsV2Request.getContinuationToken()
                        : listObjectsV2Handler.getNextContinuationToken(),
                listObjectsV2Handler.getStartAfter() == null ? listObjectsV2Request.getStartAfter()
                        : listObjectsV2Handler.getStartAfter(),
                listObjectsV2Handler.getKeyCount(),
                listObjectsV2Handler.getRequestMaxKeys(),
                listObjectsV2Handler.getRequestDelimiter() == null ? listObjectsV2Request.getDelimiter()
                        : listObjectsV2Handler.getRequestDelimiter(),
                listObjectsV2Handler.getEncodingType() == null ? listObjectsV2Request.getEncodingType()
                        : listObjectsV2Handler.getEncodingType(),
                listObjectsV2Handler.getContinuationToken() == null ? listObjectsV2Request.getContinuationToken()
                        : listObjectsV2Handler.getContinuationToken(),
                /*httpResponse.header(this.getIHeaders().bucketRegionHeader()),*/
                listObjectsV2Handler.getExtenedCommonPrefixes());
        setResponseHeaders(objV2List, this.cleanResponseHeaders(httpResponse));
        setStatusCode(objV2List, httpResponse.code());
        return objV2List;
    }

    protected ListBucketsResult listAllBucketsImpl() throws ServiceException {
        Map<String, String> headers = new HashMap<String, String>();
        Response httpResponse = performRestGetForListBuckets("", null, null, headers);

        this.verifyResponseContentType(httpResponse);

        XmlResponsesSaxParser.ListBucketsHandler handler = getXmlResponseSaxParser().parse(
                new HttpMethodReleaseInputStream(httpResponse), XmlResponsesSaxParser.ListBucketsHandler.class, true);

        Map<String, Object> responseHeaders = this.cleanResponseHeaders(httpResponse);

        ListBucketsResult result = new ListBucketsResult(handler.getBuckets(), handler.getOwner());
        setResponseHeaders(result, responseHeaders);
        setStatusCode(result, httpResponse.code());

        return result;
    }

    protected UploadPartResult uploadPartImpl(UploadPartRequest request) throws ServiceException {
        TransResult result = null;
        Response response;
        try {
            result = this.transUploadPartRequest(request);
            response = performRestPut(request.getBucketName(), request.getObjectKey(), result.getHeaders(),
                    result.getParams(), result.getBody(), true);
        } finally {
            if (result != null && result.getBody() != null && request.isAutoClose()) {
                RepeatableRequestEntity entity = (RepeatableRequestEntity) result.getBody();
                ServiceUtils.closeStream(entity);
            }
        }
        UploadPartResult ret = new UploadPartResult();
        ret.setEtag(response.header(CommonHeaders.ETAG));
        ret.setPartNumber(request.getPartNumber());
        setResponseHeaders(ret, this.cleanResponseHeaders(response));
        setStatusCode(ret, response.code());
        return ret;
    }

    protected CopyPartResult copyPartImpl(CopyPartRequest request) throws ServiceException {

        TransResult result = this.transCopyPartRequest(request);
        Response response = this.performRestPut(request.getDestinationBucketName(), request.getDestinationObjectKey(),
                result.getHeaders(), result.getParams(), null, false);
        this.verifyResponseContentType(response);

        CopyPartResult ret = getXmlResponseSaxParser().parse(new HttpMethodReleaseInputStream(response),
                XmlResponsesSaxParser.CopyPartResultHandler.class, true).getCopyPartResult(request.getPartNumber());
        setResponseHeaders(ret, this.cleanResponseHeaders(response));
        setStatusCode(ret, response.code());
        return ret;
    }

    private String getObjectUrl(String bucketName, String objectKey) {
        boolean pathStyle = this.isPathStyle();
        boolean https = this.getHttpsOnly();
        boolean isCname = this.isCname();
        return new StringBuilder().append(https ? "https://" : "http://")
                .append(pathStyle || isCname ? "" : bucketName + ".").append(this.getEndpoint()).append(":")
                .append(https ? this.getHttpsPort() : this.getHttpPort()).append("/")
                .append(pathStyle ? bucketName + "/" : "").append(RestUtils.uriEncode(objectKey, false)).toString();
    }
}
