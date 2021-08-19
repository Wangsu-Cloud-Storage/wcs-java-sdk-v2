package com.wos.services.internal;

import com.wos.services.model.AuthTypeEnum;

import java.util.*;

public class Constants {
    public static class CommonHeaders {

        public static final String CONTENT_LENGTH = "Content-Length";

        public static final String CONTENT_TYPE = "Content-Type";

        public static final String HOST = "Host";

        public static final String ETAG = "ETag";

        public static final String CONTENT_MD5 = "Content-MD5";

        public static final String ORIGIN = "Origin";

        public static final String USER_AGENT = "User-Agent";

        public static final String PROXY_AUTHORIZATION = "Proxy-Authorization";

        public static final String LOCATION = "Location";

        public static final String ACCESS_CONTROL_REQUEST_HEADERS = "Access-Control-Request-Headers";
        public static final String ACCESS_CONTROL_REQUEST_METHOD = "Access-Control-Request-Method";

        public static final String ACCESS_CONTROL_ALLOW_HEADERS = "Access-Control-Allow-Headers";
        public static final String ACCESS_CONTROL_MAX_AGE = "Access-Control-Max-Age";
        public static final String ACCESS_CONTROL_ALLOW_ORIGIN = "Access-Control-Allow-Origin";
        public static final String ACCESS_CONTROL_ALLOW_METHODS = "Access-Control-Allow-Methods";
        public static final String ACCESS_CONTROL_EXPOSE_HEADERS = "Access-Control-Expose-Headers";

        public static final String CACHE_CONTROL = "Cache-Control";

        public static final String CONTENT_DISPOSITION = "Content-Disposition";

        public static final String CONTENT_ENCODING = "Content-Encoding";

        public static final String CONTENT_LANGUAGE = "Content-Language";

        public static final String EXPIRES = "Expires";

        public static final String DATE = "Date";

        public static final String LAST_MODIFIED = "Last-Modified";

        public static final String CONNECTION = "Connection";

        public static final String AUTHORIZATION = "Authorization";

        public static final String RANGE = "Range";

        public static final String IF_MODIFIED_SINCE = "If-Modified-Since";

        public static final String IF_UNMODIFIED_SINCE = "If-Unmodified-Since";

        public static final String IF_MATCH = "If-Match";

        public static final String IF_NONE_MATCH = "If-None-Match";

        public static final String X_RESERVED_INDICATOR = "x-reserved-indicator";
        
        public static final String ACCETP_ENCODING = "Accept-Encoding";
    }

    public static class WosRequestParams {
        public static final String UPLOAD_ID = "uploadId";
        public static final String VERSION_ID = "versionId";
        public static final String PREFIX = "prefix";
        public static final String MARKER = "marker";
        public static final String MAX_KEYS = "max-keys";
        public static final String MAX_UPLOADS = "max-uploads";
        public static final String DELIMITER = "delimiter";
        public static final String KEY_MARKER = "key-marker";
        public static final String UPLOAD_ID_MARKER = "upload-id-marker";
        public static final String VERSION_ID_MARKER = "version-id-marker";
        public static final String RESPONSE_CONTENT_TYPE = "response-content-type";
        public static final String RESPONSE_CONTENT_LANGUAGE = "response-content-language";
        public static final String RESPONSE_EXPIRES = "response-expires";
        public static final String RESPONSE_CACHE_CONTROL = "response-cache-control";
        public static final String RESPONSE_CONTENT_DISPOSITION = "response-content-disposition";
        public static final String RESPONSE_CONTENT_ENCODING = "response-content-encoding";
        public static final String X_IMAGE_PROCESS = "x-image-process";
        public static final String POSITION = "position";

        public static final String MAX_PARTS = "max-parts";
        public static final String PART_NUMBER_MARKER = "part-number-marker";
        public static final String PART_NUMBER = "partNumber";

        public static final String NAME = "name";
        public static final String LENGTH = "length";

        public static final String READAHEAD = "readAhead";
        public static final String X_CACHE_CONTROL = "x-cache-control";
        public static final String TASKID = "taskID";
        public static final String LIST_TYPE = "list-type";
        public static final String FETCH_OWNER = "fetch-owner";
        public static final String START_AFTER = "start-after";
        public static final String ENCODING_TYPE = "encoding-type";
        public static final String CONTINUATION_TOKEN = "continuation-token";
    }

    public static final Map<AuthTypeEnum, IHeaders> HEADERS_MAP;
    public static final Map<AuthTypeEnum, IConvertor> CONVERTOR_MAP;

    static {
        Map<AuthTypeEnum, IHeaders> headersMap = new HashMap<AuthTypeEnum, IHeaders>();
        headersMap.put(AuthTypeEnum.V4, WosHeaders.getInstance());
        HEADERS_MAP = Collections.unmodifiableMap(headersMap);

        Map<AuthTypeEnum, IConvertor> convertorMap = new HashMap<AuthTypeEnum, IConvertor>();
        convertorMap.put(AuthTypeEnum.V4, WosConvertor.getInstance());
        CONVERTOR_MAP = Collections.unmodifiableMap(convertorMap);
    }

    public static final String ALL_USERS_URI = "http://acs.amazonaws.com/groups/global/AllUsers";

    public static final String AUTHENTICATED_USERS_URI = "http://acs.amazonaws.com/groups/global/AuthenticatedUsers";

    public static final String LOG_DELIVERY_URI = "http://acs.amazonaws.com/groups/s3/LogDelivery";

    public static final String PERMISSION_FULL_CONTROL = "FULL_CONTROL";

    public static final String PERMISSION_READ = "READ";

    public static final String PERMISSION_WRITE = "WRITE";

    public static final String PERMISSION_READ_ACP = "READ_ACP";

    public static final String PERMISSION_WRITE_ACP = "WRITE_ACP";

    public static final String PERMISSION_READ_OBJECT = "READ_OBJECT";

    public static final String PERMISSION_FULL_CONTROL_OBJECT = "FULL_CONTROL_OBJECT";

    public static final String DERECTIVE_COPY = "COPY";

    public static final String DERECTIVE_REPLACE = "REPLACE";

    public static final String DERECTIVE_REPLACE_NEW = "REPLACE_NEW";

    public static final String RESULTCODE_SUCCESS = "0";

    public static final String SERVICE = "wos";

    public static final String REQUEST_TAG = "wos_request";

    public static final String V4_ALGORITHM = "WOS-HMAC-SHA256";

    public static final String SHORT_DATE_FORMATTER = "yyyyMMdd";

    public static final String LONG_DATE_FORMATTER = "yyyyMMdd'T'HHmmss'Z'";

    public static final String HEADER_DATE_FORMATTER = "EEE, dd MMM yyyy HH:mm:ss z";

    public static final String EXPIRATION_DATE_FORMATTER = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";

    public static final TimeZone GMT_TIMEZONE = TimeZone.getTimeZone("GMT");

    public static final String WOS_SDK_VERSION = "1.0.3";

    public static final String USER_AGENT_VALUE = "wcs-java-sdk-v2/" + Constants.WOS_SDK_VERSION;

    public static final String DEFAULT_ENCODING = "UTF-8";

    public static final String ISO_8859_1_ENCOING = "ISO-8859-1";

    public static final String HMAC_SHA1_ALGORITHM = "HmacSHA1";

    public static final String HMAC_SHA256_ALGORITHM = "HmacSHA256";

    public static final String WOS_HEADER_PREFIX = "x-wos-";

    public static final String WOS_HEADER_PREFIX_CAMEL = "X-Wos-";

    public static final String WOS_HEADER_META_PREFIX = "x-wos-meta-";

    public static final String V2_HEADER_PREFIX = "x-amz-";

    public static final String V2_HEADER_META_PREFIX = "x-amz-meta-";

    public static final String V2_HEADER_PREFIX_CAMEL = "X-Amz-";

    public static final String REQUEST_ID_HEADER = "request-id";

    public static final String TRUE = "true";

    public static final String FALSE = "false";

    public static final String ENABLED = "Enabled";

    public static final String DISABLED = "Disabled";

    public static final String YES = "yes";

    public static final String OBJECT = "OBJECT";

    public static final String PFS = "PFS";

    public static final String POSIX = "POSIX";

    public static final List<String> ALLOWED_RESPONSE_HTTP_HEADER_METADATA_NAMES = Collections.unmodifiableList(
            Arrays.asList("content-type", "content-md5", "content-length", "content-language", "expires", "origin",
                    "cache-control", "content-disposition", "content-encoding", "x-default-storage-class", "location",
                    "date", "etag", "host", "last-modified", "content-range", "x-reserved", "x-reserved-indicator",
                    "access-control-allow-origin", "access-control-allow-headers", "access-control-max-age",
                    "access-control-allow-methods", "access-control-expose-headers", "connection", "pragma"));

    public static final List<String> ALLOWED_REQUEST_HTTP_HEADER_METADATA_NAMES = Collections.unmodifiableList(
            Arrays.asList("content-type", "content-md5", "content-length", "content-language", "expires", "origin",
                    "cache-control", "content-disposition", "content-encoding", "access-control-request-method",
                    "access-control-request-headers", "success-action-redirect", "x-default-storage-class", "location",
                    "date", "etag", "range", "host", "if-modified-since", "if-unmodified-since", "if-match",
                    "if-none-match", "last-modified", "content-range", "x-cache-control", "x-wos-bucket-type", "accept-encoding"));

    public static final List<String> ALLOWED_RESOURCE_PARAMTER_NAMES = Collections.unmodifiableList(
            Arrays.asList("acl", "backtosource", "policy", "torrent", "logging", "location", "storageinfo", "quota",
                    "storagepolicy", "storageclass", "requestpayment", "versions", "versioning", "versionid", "uploads",
                    "uploadid", "partnumber", "website", "notification", "lifecycle", "deletebucket", "delete", "cors",
                    "restore", "tagging", "replication", "metadata", "encryption", "directcoldaccess", "mirrorrefresh", "mirrorbacktosource",
                    /**
                     * File System API
                     */
                    "append", "position", "truncate", "modify", "rename", "length", "name", "fileinterface", "readahead",
                    "response-content-type", "response-content-language", "response-expires", "response-cache-control",
                    "response-content-disposition", "response-content-encoding", "x-image-save-bucket",
                    "x-image-save-object", "x-image-process", "x-wos-sse-kms-key-project-id", "x-oss-process",
                    "ignore-sign-in-query","listcontentsummary"));

}
