package com.wos.services.internal;

import com.wos.log.ILogger;
import com.wos.log.LoggerBuilder;
import com.wos.services.internal.Constants.CommonHeaders;
import com.wos.services.internal.Constants.WosRequestParams;
import com.wos.services.internal.io.ProgressInputStream;
import com.wos.services.internal.utils.Mimetypes;
import com.wos.services.internal.utils.RestUtils;
import com.wos.services.internal.utils.ServiceUtils;
import com.wos.services.model.*;
import com.wos.services.model.RestoreObjectRequest.RestoreObjectStatus;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.Response;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class RequestConvertor extends RestStorageService {
    private static final ILogger log = LoggerBuilder.getLogger(RequestConvertor.class);

    protected static class TransResult {
        private Map<String, String> headers;

        private Map<String, String> params;

        private RequestBody body;

        TransResult(Map<String, String> headers) {
            this(headers, null, null);
        }

        TransResult(Map<String, String> headers, RequestBody body) {
            this(headers, null, body);
        }

        TransResult(Map<String, String> headers, Map<String, String> params, RequestBody body) {
            this.headers = headers;
            this.params = params;
            this.body = body;
        }

        Map<String, String> getHeaders() {
            if (this.headers == null) {
                headers = new HashMap<String, String>();
            }
            return this.headers;
        }

        Map<String, String> getParams() {
            if (this.params == null) {
                params = new HashMap<String, String>();
            }
            return this.params;
        }

        public void setParams(Map<String, String> params) {
            this.params = params;
        }

        public void setBody(RequestBody body) {
            this.body = body;
        }

        public RequestBody getBody() {
            return body;
        }
    }

    TransResult transInitiateMultipartUploadRequest(InitiateMultipartUploadRequest request) throws ServiceException {
        Map<String, String> headers = new HashMap<String, String>();
        IHeaders iheaders = this.getIHeaders();
        IConvertor iconvertor = this.getIConvertor();

        ObjectMetadata objectMetadata = request.getMetadata() == null ? new ObjectMetadata() : request.getMetadata();

        for (Entry<String, Object> entry : objectMetadata.getMetadata().entrySet()) {
            String key = entry.getKey();
            if (!ServiceUtils.isValid(key)) {
                continue;
            }
            key = key.trim();
            if ((CAN_USE_STANDARD_HTTP_HEADERS.get() == null || (CAN_USE_STANDARD_HTTP_HEADERS.get() != null
                    && !CAN_USE_STANDARD_HTTP_HEADERS.get().booleanValue()))
                    && Constants.ALLOWED_REQUEST_HTTP_HEADER_METADATA_NAMES.contains(key.toLowerCase())) {
                continue;
            }
            headers.put(key, entry.getValue() == null ? "" : entry.getValue().toString());
        }

        if (objectMetadata.getObjectStorageClass() != null) {
            putHeader(headers, iheaders.storageClassHeader(),
                    iconvertor.transStorageClass(objectMetadata.getObjectStorageClass()));
        }

        if (request.getExpires() > 0) {
            putHeader(headers, iheaders.expiresHeader(), String.valueOf(request.getExpires()));
        }

        if (ServiceUtils.isValid(objectMetadata.getContentEncoding())) {
            headers.put(CommonHeaders.CONTENT_ENCODING, objectMetadata.getContentEncoding().trim());
        }

        Object contentType = objectMetadata.getContentType() == null
                ? objectMetadata.getValue(CommonHeaders.CONTENT_TYPE) : objectMetadata.getContentType();
        if (contentType == null) {
            contentType = Mimetypes.getInstance().getMimetype(request.getObjectKey());
        }

        String contentTypeStr = contentType.toString().trim();
        headers.put(CommonHeaders.CONTENT_TYPE, contentTypeStr);

        Map<String, String> params = new HashMap<String, String>();
        params.put(SpecialParamEnum.UPLOADS.getOriginalStringCode(), "");

        return new TransResult(headers, params, null);
    }

    RestoreObjectStatus transRestoreObjectResultToRestoreObjectStatus(RestoreObjectResult result) {
        RestoreObjectStatus ret = RestoreObjectStatus.valueOf(result.getStatusCode());
        ret.setResponseHeaders(result.getResponseHeaders());
        ret.setStatusCode(result.getStatusCode());

        return ret;
    }

    TransResult transPutObjectRequest(PutObjectRequest request) throws ServiceException {
        Map<String, String> headers = new HashMap<String, String>();
        IConvertor iconvertor = this.getIConvertor();
        IHeaders iheaders = this.getIHeaders();

        ObjectMetadata objectMetadata = request.getMetadata() == null ? new ObjectMetadata() : request.getMetadata();

        for (Entry<String, Object> entry : objectMetadata.getMetadata().entrySet()) {
            String key = entry.getKey();
            if (!ServiceUtils.isValid(key)) {
                continue;
            }
            key = key.trim();
            if ((CAN_USE_STANDARD_HTTP_HEADERS.get() == null || (CAN_USE_STANDARD_HTTP_HEADERS.get() != null
                    && !CAN_USE_STANDARD_HTTP_HEADERS.get().booleanValue()))
                    && Constants.ALLOWED_REQUEST_HTTP_HEADER_METADATA_NAMES.contains(key.toLowerCase())) {
                continue;
            }
            headers.put(key, entry.getValue() == null ? "" : entry.getValue().toString());
        }

        if (ServiceUtils.isValid(objectMetadata.getContentMd5())) {
            headers.put(CommonHeaders.CONTENT_MD5, objectMetadata.getContentMd5().trim());
        }

        if (ServiceUtils.isValid(objectMetadata.getContentEncoding())) {
            headers.put(CommonHeaders.CONTENT_ENCODING, objectMetadata.getContentEncoding().trim());
        }

        if (objectMetadata.getObjectStorageClass() != null) {
            putHeader(headers, iheaders.storageClassHeader(),
                    iconvertor.transStorageClass(objectMetadata.getObjectStorageClass()));
        }

        if (request.getExpires() >= 0) {
            putHeader(headers, iheaders.expiresHeader(), String.valueOf(request.getExpires()));
        }

        Object contentType = objectMetadata.getContentType() == null
                ? objectMetadata.getValue(CommonHeaders.CONTENT_TYPE) : objectMetadata.getContentType();
        if (contentType == null) {
            contentType = Mimetypes.getInstance().getMimetype(request.getObjectKey());
        }
        Object contentLength = objectMetadata.getContentLength();

        if (contentLength == null) {
            contentLength = objectMetadata.getValue(CommonHeaders.CONTENT_LENGTH);
        }

        long contentLengthValue = contentLength == null ? -1L : Long.parseLong(contentLength.toString());

        if (request.getFile() != null) {
            if (Mimetypes.MIMETYPE_OCTET_STREAM.equals(contentType)) {
                contentType = Mimetypes.getInstance().getMimetype(request.getFile());
            }

            long fileSize = request.getFile().length();
            
            try {
                request.setInput(new FileInputStream(request.getFile()));
            } catch (FileNotFoundException e) {
                throw new IllegalArgumentException("File doesnot exist");
            }
            
            if (request.getOffset() > 0 && request.getOffset() < fileSize) {
                contentLengthValue = (contentLengthValue > 0 && contentLengthValue <= fileSize - request.getOffset())
                        ? contentLengthValue : fileSize - request.getOffset();
                try {
                    long skipByte = request.getInput().skip(request.getOffset());
                    if (log.isDebugEnabled()) {
                        log.debug("Skip " + skipByte + " bytes; offset : " + request.getOffset());
                    }
                } catch (IOException e) {
                    ServiceUtils.closeStream(request.getInput());
                    throw new ServiceException(e);
                }
            } else if (contentLengthValue < 0 || contentLengthValue > fileSize) {
                contentLengthValue = fileSize;
            }
        }

        String contentTypeStr = contentType.toString().trim();
        headers.put(CommonHeaders.CONTENT_TYPE, contentTypeStr);

        if (contentLengthValue > -1) {
            this.putHeader(headers, CommonHeaders.CONTENT_LENGTH, String.valueOf(contentLengthValue));
        }

        if (request.getInput() != null && request.getProgressListener() != null) {
            ProgressManager progressManager = new SimpleProgressManager(contentLengthValue, 0,
                    request.getProgressListener(), request.getProgressInterval() > 0 ? request.getProgressInterval()
                    : WosConstraint.DEFAULT_PROGRESS_INTERVAL);
            request.setInput(new ProgressInputStream(request.getInput(), progressManager));
        }

        RequestBody body = request.getInput() == null ? null
                : new RepeatableRequestEntity(request.getInput(), contentTypeStr, contentLengthValue,
                this.wosProperties);

        return new TransResult(headers, body);
    }

    TransResult transCopyObjectRequest(CopyObjectRequest request) throws ServiceException {
        Map<String, String> headers = new HashMap<String, String>();
        IConvertor iconvertor = this.getIConvertor();
        IHeaders iheaders = this.getIHeaders();

        ObjectMetadata objectMetadata = request.getNewObjectMetadata() == null ? new ObjectMetadata()
                : request.getNewObjectMetadata();

        putHeader(headers, iheaders.metadataDirectiveHeader(),
                request.isReplaceMetadata() ? Constants.DERECTIVE_REPLACE : Constants.DERECTIVE_COPY);
        if (request.isReplaceMetadata()) {
            objectMetadata.getMetadata().remove(iheaders.requestIdHeader());
            for (Entry<String, Object> entry : objectMetadata.getMetadata().entrySet()) {
                String key = entry.getKey();
                if (!ServiceUtils.isValid(key)) {
                    continue;
                }
                key = key.trim();
                if (Constants.ALLOWED_REQUEST_HTTP_HEADER_METADATA_NAMES.contains(key.toLowerCase())) {
                    continue;
                }
                headers.put(key, entry.getValue() == null ? "" : entry.getValue().toString());
            }
        }

        if (objectMetadata.getContentType() != null) {
            headers.put(CommonHeaders.CONTENT_TYPE, objectMetadata.getContentType().trim());
        }

        if (objectMetadata.getContentEncoding() != null) {
            headers.put(CommonHeaders.CONTENT_ENCODING, objectMetadata.getContentEncoding().trim());
        }

        if (objectMetadata.getObjectStorageClass() != null) {
            putHeader(headers, iheaders.storageClassHeader(),
                    iconvertor.transStorageClass(objectMetadata.getObjectStorageClass()));
        }

        transConditionCopyHeaders(request, headers, iheaders);

        String sourceKey = RestUtils.encodeUrlString(request.getSourceBucketName()) + "/"
                + RestUtils.encodeUrlString(request.getSourceObjectKey());
        putHeader(headers, iheaders.copySourceHeader(), sourceKey);

        return new TransResult(headers);
    }


    void transConditionCopyHeaders(CopyObjectRequest request, Map<String, String> headers, IHeaders iheaders) {
        if (request.getIfModifiedSince() != null) {
            putHeader(headers, iheaders.copySourceIfModifiedSinceHeader(),
                    ServiceUtils.formatRfc822Date(request.getIfModifiedSince()));
        }
        if (request.getIfUnmodifiedSince() != null) {
            putHeader(headers, iheaders.copySourceIfUnmodifiedSinceHeader(),
                    ServiceUtils.formatRfc822Date(request.getIfUnmodifiedSince()));
        }
        if (ServiceUtils.isValid(request.getIfMatchTag())) {
            putHeader(headers, iheaders.copySourceIfMatchHeader(), request.getIfMatchTag().trim());
        }
        if (ServiceUtils.isValid(request.getIfNoneMatchTag())) {
            putHeader(headers, iheaders.copySourceIfNoneMatchHeader(), request.getIfNoneMatchTag().trim());
        }
    }

    TransResult transGetObjectRequest(GetObjectRequest request) throws ServiceException {
        Map<String, String> headers = new HashMap<String, String>();
        this.transConditionGetObjectHeaders(request, headers);

        transRangeHeader(request, headers);

        Map<String, String> params = new HashMap<String, String>();
        this.transGetObjectParams(request, params);

        return new TransResult(headers, params, null);
    }

    /**
     *
     * @param request
     * @param headers
     */
    void transRangeHeader(GetObjectRequest request, Map<String, String> headers) {
        String start = "";
        String end = "";

        if (null != request.getRangeStart()) {
            ServiceUtils.assertParameterNotNegative(request.getRangeStart().longValue(),
                    "start range should not be negative.");
            start = String.valueOf(request.getRangeStart());
        }

        if (null != request.getRangeEnd()) {
            ServiceUtils.assertParameterNotNegative(request.getRangeEnd().longValue(),
                    "end range should not be negative.");
            end = String.valueOf(request.getRangeEnd());
        }

        if (null != request.getRangeStart() && null != request.getRangeEnd()) {
            if (request.getRangeStart().longValue() > request.getRangeEnd().longValue()) {
                throw new IllegalArgumentException("start must be less than end.");
            }
        }

        if (!"".equals(start) || !"".equals(end)) {
            String range = String.format("bytes=%s-%s", start, end);
            headers.put(CommonHeaders.RANGE, range);
        }
    }

    void transGetObjectParams(GetObjectRequest request, Map<String, String> params) {
        if (null != request.getReplaceMetadata()) {
            if (ServiceUtils.isValid(request.getReplaceMetadata().getCacheControl())) {
                params.put(WosRequestParams.RESPONSE_CACHE_CONTROL, request.getReplaceMetadata().getCacheControl());
            }
            if (ServiceUtils.isValid(request.getReplaceMetadata().getContentDisposition())) {
                params.put(WosRequestParams.RESPONSE_CONTENT_DISPOSITION,
                        request.getReplaceMetadata().getContentDisposition());
            }
            if (ServiceUtils.isValid(request.getReplaceMetadata().getContentEncoding())) {
                params.put(WosRequestParams.RESPONSE_CONTENT_ENCODING,
                        request.getReplaceMetadata().getContentEncoding());
            }
            if (ServiceUtils.isValid(request.getReplaceMetadata().getContentLanguage())) {
                params.put(WosRequestParams.RESPONSE_CONTENT_LANGUAGE,
                        request.getReplaceMetadata().getContentLanguage());
            }
            if (ServiceUtils.isValid(request.getReplaceMetadata().getContentType())) {
                params.put(WosRequestParams.RESPONSE_CONTENT_TYPE, request.getReplaceMetadata().getContentType());
            }
            if (ServiceUtils.isValid(request.getReplaceMetadata().getExpires())) {
                params.put(WosRequestParams.RESPONSE_EXPIRES, request.getReplaceMetadata().getExpires());
            }
        }
        if (ServiceUtils.isValid(request.getImageProcess())) {
            params.put(WosRequestParams.X_IMAGE_PROCESS, request.getImageProcess());
        }

    }

    void transConditionGetObjectHeaders(GetObjectRequest request, Map<String, String> headers) {
        if (request.getIfModifiedSince() != null) {
            headers.put(CommonHeaders.IF_MODIFIED_SINCE, ServiceUtils.formatRfc822Date(request.getIfModifiedSince()));
        }
        if (request.getIfUnmodifiedSince() != null) {
            headers.put(CommonHeaders.IF_UNMODIFIED_SINCE,
                    ServiceUtils.formatRfc822Date(request.getIfUnmodifiedSince()));
        }
        if (ServiceUtils.isValid(request.getIfMatchTag())) {
            headers.put(CommonHeaders.IF_MATCH, request.getIfMatchTag().trim());
        }
        if (ServiceUtils.isValid(request.getIfNoneMatchTag())) {
            headers.put(CommonHeaders.IF_NONE_MATCH, request.getIfNoneMatchTag().trim());
        }
        if (!request.isAutoUnzipResponse()) {
            headers.put(CommonHeaders.ACCETP_ENCODING, "identity");
        }
    }

    TransResult transSetObjectMetadataRequest(SetObjectMetadataRequest request) throws ServiceException {
        Map<String, String> headers = new HashMap<String, String>();
        IHeaders iheaders = this.getIHeaders();
        IConvertor iconvertor = this.getIConvertor();

        for (Entry<String, String> entry : request.getMetadata().entrySet()) {
            String key = entry.getKey();
            if (!ServiceUtils.isValid(key)) {
                continue;
            }
            key = key.trim();
            headers.put(key, entry.getValue() == null ? "" : entry.getValue());
        }

        if (request.getObjectStorageClass() != null) {
            putHeader(headers, iheaders.storageClassHeader(),
                    iconvertor.transStorageClass(request.getObjectStorageClass()));
        }

        if (request.getContentDisposition() != null) {
            putHeader(headers, CommonHeaders.CONTENT_DISPOSITION, request.getContentDisposition());
        }

        if (request.getContentEncoding() != null) {
            putHeader(headers, CommonHeaders.CONTENT_ENCODING, request.getContentEncoding());
        }

        if (request.getContentLanguage() != null) {
            putHeader(headers, CommonHeaders.CONTENT_LANGUAGE, request.getContentLanguage());
        }

        if (request.getContentType() != null) {
            putHeader(headers, CommonHeaders.CONTENT_TYPE, request.getContentType());
        }

        if (request.getCacheControl() != null) {
            putHeader(headers, CommonHeaders.CACHE_CONTROL, request.getCacheControl());
        }

        if (request.getExpires() != null) {
            putHeader(headers, CommonHeaders.EXPIRES, request.getExpires());
        }

        putHeader(headers, iheaders.metadataDirectiveHeader(),
                request.isRemoveUnset() ? Constants.DERECTIVE_REPLACE : Constants.DERECTIVE_REPLACE_NEW);

        Map<String, String> params = new HashMap<String, String>();
        params.put(SpecialParamEnum.METADATA.getOriginalStringCode(), "");
        return new TransResult(headers, params, null);
    }

    TransResult transCopyPartRequest(CopyPartRequest request) throws ServiceException {
        Map<String, String> params = new HashMap<String, String>();
        params.put(WosRequestParams.PART_NUMBER, String.valueOf(request.getPartNumber()));
        params.put(WosRequestParams.UPLOAD_ID, request.getUploadId());

        Map<String, String> headers = new HashMap<String, String>();
        IHeaders iheaders = this.getIHeaders();

        String sourceKey = RestUtils.encodeUrlString(request.getSourceBucketName()) + "/"
                + RestUtils.encodeUrlString(request.getSourceObjectKey());
        putHeader(headers, iheaders.copySourceHeader(), sourceKey);

        if (request.getByteRangeStart() != null) {
            String rangeEnd = request.getByteRangeEnd() != null ? String.valueOf(request.getByteRangeEnd()) : "";
            String range = String.format("bytes=%s-%s", request.getByteRangeStart(), rangeEnd);
            putHeader(headers, iheaders.copySourceRangeHeader(), range);
        }

        return new TransResult(headers, params, null);
    }

    TransResult transListObjectsRequest(ListObjectsRequest listObjectsRequest) {
        Map<String, String> params = new HashMap<String, String>();
        if (listObjectsRequest.getPrefix() != null) {
            params.put(WosRequestParams.PREFIX, listObjectsRequest.getPrefix());
        }
        if (listObjectsRequest.getDelimiter() != null) {
            params.put(WosRequestParams.DELIMITER, listObjectsRequest.getDelimiter());
        }
        if (listObjectsRequest.getMaxKeys() > 0) {
            params.put(WosRequestParams.MAX_KEYS, String.valueOf(listObjectsRequest.getMaxKeys()));
        }

        if (listObjectsRequest.getMarker() != null) {
            params.put(WosRequestParams.MARKER, listObjectsRequest.getMarker());
        }

        Map<String, String> headers = new HashMap<String, String>();

        return new TransResult(headers, params, null);
    }

    TransResult transListObjectsV2Request(ListObjectsV2Request listObjectsV2Request) {
        Map<String, String> params = new HashMap<String, String>();
        if (listObjectsV2Request.getPrefix() != null) {
            params.put(WosRequestParams.PREFIX, listObjectsV2Request.getPrefix());
        }
        if (listObjectsV2Request.getDelimiter() != null) {
            params.put(WosRequestParams.DELIMITER, listObjectsV2Request.getDelimiter());
        }
        if (listObjectsV2Request.getMaxKeys() > 0) {
            params.put(WosRequestParams.MAX_KEYS, String.valueOf(listObjectsV2Request.getMaxKeys()));
        }

        if (listObjectsV2Request.getStartAfter() != null) {
            params.put(WosRequestParams.START_AFTER, listObjectsV2Request.getStartAfter());
        }

        if (listObjectsV2Request.getFetchOwner()) {
            params.put(WosRequestParams.FETCH_OWNER, String.valueOf(listObjectsV2Request.getFetchOwner()));
        }

        if (listObjectsV2Request.getEncodingType() != null) {
            params.put(WosRequestParams.ENCODING_TYPE, listObjectsV2Request.getEncodingType());
        }

        if (listObjectsV2Request.getContinuationToken() != null) {
            params.put(WosRequestParams.CONTINUATION_TOKEN, listObjectsV2Request.getContinuationToken());
        }

        Map<String, String> headers = new HashMap<String, String>();
        params.put(WosRequestParams.LIST_TYPE, "2");

        return new TransResult(headers, params, null);
    }

    TransResult transUploadPartRequest(UploadPartRequest request) throws ServiceException {
        Map<String, String> params = new HashMap<String, String>();
        params.put(WosRequestParams.PART_NUMBER, String.valueOf(request.getPartNumber()));
        params.put(WosRequestParams.UPLOAD_ID, request.getUploadId());

        Map<String, String> headers = new HashMap<String, String>();
        IHeaders iheaders = this.getIHeaders();

        if (ServiceUtils.isValid(request.getContentMd5())) {
            headers.put(CommonHeaders.CONTENT_MD5, request.getContentMd5().trim());
        }

        long contentLength = -1L;
        if (null != request.getFile()) {
            long fileSize = request.getFile().length();
            long offset = (request.getOffset() >= 0 && request.getOffset() < fileSize) ? request.getOffset() : 0;
            long partSize = (request.getPartSize() != null && request.getPartSize() > 0
                    && request.getPartSize() <= (fileSize - offset)) ? request.getPartSize() : fileSize - offset;
            contentLength = partSize;

            try {
                if (request.isAttachMd5() && !ServiceUtils.isValid(request.getContentMd5())) {
                    headers.put(CommonHeaders.CONTENT_MD5, ServiceUtils.toBase64(
                            ServiceUtils.computeMD5Hash(new FileInputStream(request.getFile()), partSize, offset)));
                }
                request.setInput(new FileInputStream(request.getFile()));
                long skipByte = request.getInput().skip(offset);
                if (log.isDebugEnabled()) {
                    log.debug("Skip " + skipByte + " bytes; offset : " + offset);
                }
            } catch (Exception e) {
                ServiceUtils.closeStream(request.getInput());
                throw new ServiceException(e);
            }
        } else if (null != request.getInput()) {
            if (request.getPartSize() != null && request.getPartSize() > 0) {
                contentLength = request.getPartSize();
            }
        }

        if (request.getInput() != null && request.getProgressListener() != null) {
            ProgressManager progressManager = new SimpleProgressManager(contentLength, 0, request.getProgressListener(),
                    request.getProgressInterval() > 0 ? request.getProgressInterval()
                            : WosConstraint.DEFAULT_PROGRESS_INTERVAL);
            request.setInput(new ProgressInputStream(request.getInput(), progressManager));
        }

        String contentType = Mimetypes.getInstance().getMimetype(request.getObjectKey());
        headers.put(CommonHeaders.CONTENT_TYPE, contentType);

        if (contentLength > -1) {
            this.putHeader(headers, CommonHeaders.CONTENT_LENGTH, String.valueOf(contentLength));
        }
        RequestBody body = request.getInput() == null ? null
                : new RepeatableRequestEntity(request.getInput(), contentType, contentLength, this.wosProperties);
        return new TransResult(headers, params, body);
    }

    RequestBody createRequestBody(String mimeType, String content) throws ServiceException {
        try {
            if (log.isTraceEnabled()) {
                try {
                    log.trace("Entity Content:" + content);
                } catch (Exception e) {
                }
            }
            return RequestBody.create(MediaType.parse(mimeType), content.getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            throw new ServiceException(e);
        }
    }

    void putHeader(Map<String, String> headers, String key, String value) {
        if (ServiceUtils.isValid(key)) {
            headers.put(key, value);
        }
    }
    
    HeaderResponse build(Response res) {
        HeaderResponse response = new HeaderResponse();
        setResponseHeaders(response, this.cleanResponseHeaders(res));
        setStatusCode(response, res.code());
        return response;
    }
    
    static HeaderResponse build(Map<String, Object> responseHeaders) {
        HeaderResponse response = new HeaderResponse();
        setResponseHeaders(response, responseHeaders);
        return response;
    }
    
    Map<String, Object> cleanResponseHeaders(Response response) {
        Map<String, List<String>> map = response.headers().toMultimap();
        return ServiceUtils.cleanRestMetadataMap(map, this.getIHeaders().headerPrefix(),
                this.getIHeaders().headerMetaPrefix());
    }
    
    static void setStatusCode(HeaderResponse response, int statusCode) {
        response.setStatusCode(statusCode);
    }

    protected String getCredential(String shortDate, String accessKey) {
        return new StringBuilder(accessKey).append("/").append(shortDate).append("/")
                .append(WosConstraint.DEFAULT_BUCKET_LOCATION_VALUE).append("/").append(Constants.SERVICE).append("/")
                .append(Constants.REQUEST_TAG).toString();
    }


    protected static void setResponseHeaders(HeaderResponse response, Map<String, Object> responseHeaders) {
        response.setResponseHeaders(responseHeaders);
    }
}
