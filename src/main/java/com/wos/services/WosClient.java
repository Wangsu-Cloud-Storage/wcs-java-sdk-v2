package com.wos.services;

import com.wos.log.ILogger;
import com.wos.log.InterfaceLogBean;
import com.wos.log.LoggerBuilder;
import com.wos.services.exception.WosException;
import com.wos.services.internal.Constants;
import com.wos.services.internal.Constants.CommonHeaders;
import com.wos.services.internal.ServiceException;
import com.wos.services.internal.WosConstraint;
import com.wos.services.internal.WosProperties;
import com.wos.services.internal.WosService;
import com.wos.services.internal.security.BasicSecurityKey;
import com.wos.services.internal.security.ProviderCredentials;
import com.wos.services.internal.utils.AccessLoggerUtils;
import com.wos.services.internal.utils.RestUtils;
import com.wos.services.internal.utils.ServiceUtils;
import com.wos.services.internal.utils.UrlCodecUtil;
import com.wos.services.internal.utils.V4Authentication;
import com.wos.services.model.AbortMultipartUploadRequest;
import com.wos.services.model.AccessControlList;
import com.wos.services.model.avOperation.AudioAndVideoTaskDetailResult;
import com.wos.services.model.avOperation.AudioAndVideoTaskRequestResult;
import com.wos.services.model.AuthTypeEnum;
import com.wos.services.model.avOperation.AvOperationTypeEnum;
import com.wos.services.model.BaseBucketRequest;
import com.wos.services.model.CompleteMultipartUploadRequest;
import com.wos.services.model.CompleteMultipartUploadResult;
import com.wos.services.model.CopyObjectRequest;
import com.wos.services.model.CopyObjectResult;
import com.wos.services.model.CopyPartRequest;
import com.wos.services.model.CopyPartResult;
import com.wos.services.model.avOperation.CreateAudioAndVideoTaskRequest;
import com.wos.services.model.avOperation.CreateDecompressTaskRequest;
import com.wos.services.model.DeleteObjectRequest;
import com.wos.services.model.DeleteObjectResult;
import com.wos.services.model.DeleteObjectsRequest;
import com.wos.services.model.DeleteObjectsResult;
import com.wos.services.model.avOperation.GetAudioAndVideoTaskRequest;
import com.wos.services.model.GetObjectAclRequest;
import com.wos.services.model.GetObjectAvinfoRequest;
import com.wos.services.model.GetObjectMetadataRequest;
import com.wos.services.model.GetObjectRequest;
import com.wos.services.model.HeaderResponse;
import com.wos.services.model.HttpMethodEnum;
import com.wos.services.model.InitiateMultipartUploadRequest;
import com.wos.services.model.InitiateMultipartUploadResult;
import com.wos.services.model.LifecycleConfiguration;
import com.wos.services.model.ListBucketsResult;
import com.wos.services.model.ListMultipartUploadsRequest;
import com.wos.services.model.ListObjectsRequest;
import com.wos.services.model.ListObjectsV2Request;
import com.wos.services.model.ListPartsRequest;
import com.wos.services.model.ListPartsResult;
import com.wos.services.model.MultipartUploadListing;
import com.wos.services.model.ObjectListing;
import com.wos.services.model.ObjectMetadata;
import com.wos.services.model.ObjectV2Listing;
import com.wos.services.model.PostSignatureRequest;
import com.wos.services.model.PostSignatureResponse;
import com.wos.services.model.PutObjectRequest;
import com.wos.services.model.PutObjectResult;
import com.wos.services.model.avOperation.QueryDecompressResult;
import com.wos.services.model.RestoreObjectRequest;
import com.wos.services.model.RestoreObjectResult;
import com.wos.services.model.SetBucketLifecycleRequest;
import com.wos.services.model.SetObjectMetadataRequest;
import com.wos.services.model.SpecialParamEnum;
import com.wos.services.model.TemporarySignatureRequest;
import com.wos.services.model.TemporarySignatureResponse;
import com.wos.services.model.UploadPartRequest;
import com.wos.services.model.UploadPartResult;
import com.wos.services.model.WosBucket;
import com.wos.services.model.WosObject;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * WosClient
 */
public class WosClient extends WosService implements Closeable, IWosClient {

    private static final ILogger ILOG = LoggerBuilder.getLogger(WosClient.class);

    private void init(String accessKey, String secretKey, String securityToken, WosConfiguration config) {
        InterfaceLogBean reqBean = new InterfaceLogBean("WosClient", config.getEndPoint(), "");
        ProviderCredentials credentials = new ProviderCredentials(accessKey, secretKey, securityToken);
        WosProperties wosProperties = ServiceUtils.changeFromWosConfiguration(config);
        credentials.setAuthType(config.getAuthType());
        credentials.setRegionName(config.getRegionName());
        this.wosProperties = wosProperties;
        this.credentials = credentials;
        this.keyManagerFactory = config.getKeyManagerFactory();
        this.trustManagerFactory = config.getTrustManagerFactory();
        if (this.isAuthTypeNegotiation()) {
            this.getProviderCredentials().initThreadLocalAuthType();
        }
        this.initHttpClient(config.getHttpDispatcher());
        reqBean.setRespTime(new Date());
        reqBean.setResultCode(Constants.RESULTCODE_SUCCESS);
        if (ILOG.isInfoEnabled()) {
            ILOG.info(reqBean);
        }

        if (ILOG.isWarnEnabled()) {
            StringBuilder sb = new StringBuilder("[WOS SDK Version=");
            sb.append(Constants.WOS_SDK_VERSION);
            sb.append("];");
            sb.append("[Endpoint=");
            String ep = "";
            if (this.getHttpsOnly()) {
                ep = "https://" + this.getEndpoint() + ":" + this.getHttpsPort() + "/";
            } else {
                ep = "http://" + this.getEndpoint() + ":" + this.getHttpPort() + "/";
            }
            sb.append(ep);
            sb.append("];");
            sb.append("[Access Mode=");
            sb.append(this.isPathStyle() ? "Path" : "Virtul Hosting");
            sb.append("]");
            ILOG.warn(sb);
        }
    }

    /**
     * Constructor
     *
     * @param endPoint WOS endpoint
     */
    public WosClient(String endPoint) {
        WosConfiguration config = new WosConfiguration();
        config.setEndPoint(endPoint);
        this.init("", "", null, config);
    }

    /**
     * Constructor
     *
     * @param endPoint   WOS endpoint
     * @param regionName the region name
     */
    public WosClient(String endPoint, String regionName) {
        WosConfiguration config = new WosConfiguration();
        config.setEndPoint(endPoint);
        config.setRegionName(regionName);
        this.init("", "", null, config);
    }

    /**
     * Constructor
     *
     * @param config Configuration parameters of WosClient
     */
    public WosClient(WosConfiguration config) {
        if (config == null) {
            config = new WosConfiguration();
        }
        this.init("", "", null, config);
    }

    /**
     * Constructor
     *
     * @param accessKey AK in the access key
     * @param secretKey SK in the access key
     * @param endPoint  WOS endpoint
     */
    public WosClient(String accessKey, String secretKey, String endPoint) {
        WosConfiguration config = new WosConfiguration();
        config.setEndPoint(endPoint);
        this.init(accessKey, secretKey, null, config);
    }

    /**
     * Constructor
     *
     * @param accessKey AK in the access key
     * @param secretKey SK in the access key
     * @param config    Configuration parameters of WosClient
     */
    public WosClient(String accessKey, String secretKey, WosConfiguration config) {
        if (config == null) {
            config = new WosConfiguration();
        }
        this.init(accessKey, secretKey, null, config);
    }

    /**
     * Constructor
     *
     * @param accessKey     AK in the temporary access key
     * @param secretKey     SK in the temporary access key
     * @param securityToken Security token
     * @param endPoint      WOS endpoint
     */
    public WosClient(String accessKey, String secretKey, String securityToken, String endPoint) {
        WosConfiguration config = new WosConfiguration();
        config.setEndPoint(endPoint);
        this.init(accessKey, secretKey, securityToken, config);
    }

    /**
     * Constructor
     *
     * @param accessKey     AK in the temporary access key
     * @param secretKey     SK in the temporary access key
     * @param securityToken Security token
     * @param endPoint      WOS endpoint
     * @param regionName    the region name
     */
    public WosClient(String accessKey, String secretKey, String securityToken, String endPoint, String regionName) {
        WosConfiguration config = new WosConfiguration();
        config.setEndPoint(endPoint);
        config.setRegionName(regionName);
        this.init(accessKey, secretKey, securityToken, config);
    }

    /**
     * Constructor
     *
     * @param accessKey     AK in the temporary access key
     * @param secretKey     SK in the temporary access key
     * @param securityToken Security token
     * @param config        Configuration parameters of WosClient
     */
    public WosClient(String accessKey, String secretKey, String securityToken, WosConfiguration config) {
        if (config == null) {
            config = new WosConfiguration();
        }
        this.init(accessKey, secretKey, securityToken, config);
    }

    public WosClient(IWosCredentialsProvider provider, String endPoint) {
        ServiceUtils.asserParameterNotNull(provider, "WosCredentialsProvider is null");
        WosConfiguration config = new WosConfiguration();
        config.setEndPoint(endPoint);
        this.init(provider.getSecurityKey().getAccessKey(), provider.getSecurityKey().getSecretKey(),
                provider.getSecurityKey().getSecurityToken(), config);
        this.credentials.setWosCredentialsProvider(provider);
    }

    public WosClient(IWosCredentialsProvider provider, WosConfiguration config) {
        ServiceUtils.asserParameterNotNull(provider, "WosCredentialsProvider is null");
        if (config == null) {
            config = new WosConfiguration();
        }
        this.init(provider.getSecurityKey().getAccessKey(), provider.getSecurityKey().getSecretKey(),
                provider.getSecurityKey().getSecurityToken(), config);
        this.credentials.setWosCredentialsProvider(provider);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.wos.services.IWosClient#refresh(java.lang.String,
     * java.lang.String, java.lang.String)
     */
    @Override
    public void refresh(String accessKey, String secretKey, String securityToken) {
        ProviderCredentials credentials = new ProviderCredentials(accessKey, secretKey, securityToken);
        credentials.setAuthType(this.credentials.getAuthType());
        this.setProviderCredentials(credentials);
    }

    /**
     * Create a temporarily authorized URL.
     *
     * @param method       HTTP request method
     * @param bucketName   Bucket name
     * @param objectKey    Object name
     * @param specialParam Special operator
     * @param expiryTime   Time when the temporary authentication expires
     * @param headers      Header information
     * @param queryParams  Query parameter information
     * @return Temporarily authorized URL
     * @throws WosException WOS SDK self-defined exception, thrown when the interface
     *                      fails to be called or access to WOS fails
     */

    public String createSignedUrl(HttpMethodEnum method, String bucketName, String objectKey,
                                  SpecialParamEnum specialParam, Date expiryTime, Map<String, String> headers,
                                  Map<String, Object> queryParams) throws WosException {
        return this.createSignedUrl(method, bucketName, objectKey, specialParam, expiryTime == null
                        ? WosConstraint.DEFAULT_EXPIRE_SECONEDS : (expiryTime.getTime() - System.currentTimeMillis()) / 1000,
                headers, queryParams);
    }

    /**
     * Create a temporarily authorized URL.
     *
     * @param method       HTTP request method
     * @param bucketName   Bucket name
     * @param objectKey    Object name
     * @param specialParam Special operator
     * @param expires      Time when the temporary authentication expires. The unit is
     *                     second and the default value is 300.
     * @param headers      Header information
     * @param queryParams  Query parameter information
     * @return Temporarily authorized URL
     * @throws WosException WOS SDK self-defined exception, thrown when the interface
     *                      fails to be called or access to WOS fails
     */
    public String createSignedUrl(HttpMethodEnum method, String bucketName, String objectKey,
                                  SpecialParamEnum specialParam, long expires, Map<String, String> headers, Map<String, Object> queryParams) {
        TemporarySignatureRequest request = new TemporarySignatureRequest();
        request.setMethod(method);
        request.setBucketName(bucketName);
        request.setObjectKey(objectKey);
        request.setSpecialParam(specialParam);
        request.setHeaders(headers);
        request.setQueryParams(queryParams);
        if (expires > 0) {
            request.setExpires(expires);
        }
        try {
            return this.createTemporarySignature(request).getSignedUrl();
        } catch (Exception e) {
            throw new WosException(e.getMessage(), e);
        }
    }

    @Override
    public TemporarySignatureResponse createTemporarySignature(TemporarySignatureRequest request) {
        ServiceUtils.asserParameterNotNull(request, "V4TemporarySignatureRequest is null");
        InterfaceLogBean reqBean = new InterfaceLogBean("createTemporarySignature", this.getEndpoint(), "");
        try {
            TemporarySignatureResponse response = this.createV4TemporarySignature(request);
            return response;
        } catch (Exception e) {
            reqBean.setRespTime(new Date());
            if (ILOG.isErrorEnabled()) {
                ILOG.error(reqBean);
            }
            throw new WosException(e.getMessage(), e);
        }
    }

    protected TemporarySignatureResponse createV4TemporarySignature(TemporarySignatureRequest request)
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
        String securityToken = securityKey.getSecurityToken();
        if (!queryParams.containsKey(this.getIHeaders().securityTokenHeader())) {
            if (ServiceUtils.isValid(securityToken)) {
                queryParams.put(this.getIHeaders().securityTokenHeader(), securityToken);
            }
        }

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
                } else if ("PUT".equals(requestMethod) || "POST".equals(requestMethod)) {
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
        String regionName = credentials.getRegionName();
        queryParams.put(Constants.WOS_HEADER_PREFIX_CAMEL + "Algorithm", Constants.V4_ALGORITHM);
        queryParams.put(Constants.WOS_HEADER_PREFIX_CAMEL + "Credential", this.getCredential(shortDate, accessKey, regionName));
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
                .append("\n").append(shortDate).append("/").append(regionName)
                .append("/").append(Constants.SERVICE).append("/").append(Constants.REQUEST_TAG).append("\n")
                .append(V4Authentication.byteToHex((V4Authentication.sha256encode(canonicalRequest.toString()))));
        signedUrl.append("&").append(Constants.WOS_HEADER_PREFIX_CAMEL).append("Signature=")
                .append(V4Authentication.caculateSignature(stringToSign.toString(), shortDate, secretKey, regionName));
        TemporarySignatureResponse response = new TemporarySignatureResponse(signedUrl.toString());
        actualSignedRequestHeaders.put(CommonHeaders.USER_AGENT, Constants.USER_AGENT_VALUE);
        response.getActualSignedRequestHeaders().putAll(actualSignedRequestHeaders);
        return response;
    }

    /**
     * Generate parameters for browser-based authorized access.
     *
     * @param acl         Object ACL
     * @param contentType MIME type of the object
     * @param expires     Validity period (in seconds)
     * @param bucketName  Bucket name
     * @param objectKey   Object name
     * @return Response to the V4 browser-based authorized access
     * @throws WosException WOS SDK self-defined exception, thrown when the interface
     *                      fails to be called or access to WOS fails
     */
    public PostSignatureResponse createPostSignature(String acl, String contentType, long expires, String bucketName,
                                                     String objectKey) throws WosException {
        PostSignatureRequest request = new PostSignatureRequest(expires, new Date(), bucketName, objectKey);
        request.getFormParams().put(
                this.getProviderCredentials().getAuthType() == AuthTypeEnum.V4 ? "acl" : this.getIHeaders().aclHeader(),
                acl);
        request.getFormParams().put(Constants.CommonHeaders.CONTENT_TYPE, contentType);
        return this.createPostSignature(request);
    }

    /**
     * Generate parameters for browser-based authorized access.
     *
     * @param expires    Validity period (in seconds)
     * @param bucketName Bucket name
     * @param objectKey  Object name
     * @return Response to the V4 browser-based authorized access
     * @throws WosException WOS SDK self-defined exception, thrown when the interface
     *                      fails to be called or access to WOS fails
     */
    public PostSignatureResponse createPostSignature(long expires, String bucketName, String objectKey)
            throws WosException {
        PostSignatureRequest request = new PostSignatureRequest(expires, new Date(), bucketName, objectKey);
        return this.createPostSignature(request);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.wos.services.IWosClient#createPostSignature(com.wos.services.model.
     * PostSignatureRequest)
     */
    @Override
    public PostSignatureResponse createPostSignature(PostSignatureRequest request) throws WosException {
        ServiceUtils.asserParameterNotNull(request, "PostSignatureRequest is null");
        InterfaceLogBean reqBean = new InterfaceLogBean("createPostSignature", this.getEndpoint(), "");
        try {
            PostSignatureResponse response = this.createPostSignatureResponse(request);
            reqBean.setRespTime(new Date());
            reqBean.setResultCode(Constants.RESULTCODE_SUCCESS);
            if (ILOG.isInfoEnabled()) {
                ILOG.info(reqBean);
            }
            return response;
        } catch (Exception e) {
            reqBean.setRespTime(new Date());
            if (ILOG.isErrorEnabled()) {
                ILOG.error(reqBean);
            }
            throw new WosException(e.getMessage(), e);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see com.wos.services.IWosClient#listBuckets(com.wos.services.model.
     * ListBucketsRequest)_
     */
    @Override
    public List<WosBucket> listBuckets() throws WosException {
        return this.doActionWithResult("listBuckets", "All Buckets", new ActionCallbackWithResult<ListBucketsResult>() {
            @Override
            public ListBucketsResult action() throws ServiceException {
                if (isCname()) {
                    throw new ServiceException("listBuckets is not allowed in customdomain mode");
                }
                return WosClient.this.listAllBucketsImpl();
            }
        }).getBuckets();
    }

    /*
     * (non-Javadoc)
     *
     * @see com.wos.services.IWosClient#listObjects(com.wos.services.model.
     * ListObjectsRequest)
     */
    @Override
    public ObjectListing listObjects(final ListObjectsRequest request) throws WosException {
        ServiceUtils.asserParameterNotNull(request, "ListObjectsRequest is null");
        return this.doActionWithResult("listObjects", request.getBucketName(),
                new ActionCallbackWithResult<ObjectListing>() {
                    @Override
                    public ObjectListing action() throws ServiceException {
                        return WosClient.this.listObjectsImpl(request);
                    }

                });
    }

    /*
     * (non-Javadoc)
     *
     * @see com.wos.services.IWosClient#listObjects(java.lang.String)
     */
    @Override
    public ObjectListing listObjects(String bucketName) throws WosException {
        ListObjectsRequest listObjectsRequest = new ListObjectsRequest(bucketName);
        return this.listObjects(listObjectsRequest);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.wos.services.IWosClient#listObjects(com.wos.services.model.
     * ListObjectsRequest)
     */
    @Override
    public ObjectV2Listing listObjectsV2(final ListObjectsV2Request request) throws WosException {
        ServiceUtils.asserParameterNotNull(request, "ListObjectsRequest is null");
        return this.doActionWithResult("listObjects", request.getBucketName(),
                new ActionCallbackWithResult<ObjectV2Listing>() {
                    @Override
                    public ObjectV2Listing action() throws ServiceException {
                        return WosClient.this.listObjectsV2Impl(request);
                    }

                });
    }

    /*
     * (non-Javadoc)
     *
     * @see com.wos.services.IWosClient#listObjects(java.lang.String)
     */
    @Override
    public ObjectV2Listing listObjectsV2(String bucketName) throws WosException {
        ListObjectsV2Request listObjectsRequest = new ListObjectsV2Request(bucketName);
        return this.listObjectsV2(listObjectsRequest);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.wos.services.IWosClient#headBucket(java.lang.String)
     */
    @Override
    public boolean headBucket(final String bucketName) throws WosException {
        return headBucket(new BaseBucketRequest(bucketName));
    }

    /*
     * (non-Javadoc)
     *
     * @see com.wos.services.IWosClient#headBucket(java.lang.String)
     */
    @Override
    public boolean headBucket(final BaseBucketRequest request) throws WosException {
        ServiceUtils.asserParameterNotNull(request, "BaseBucketRequest is null");
        ServiceUtils.asserParameterNotNull(request.getBucketName(), "bucketName is null");
        return this.doActionWithResult("headBucket", request.getBucketName(), new ActionCallbackWithResult<Boolean>() {

            @Override
            public Boolean action() throws ServiceException {
                return WosClient.this.headBucketImpl(request);
            }
        });
    }


    /*
     * (non-Javadoc)
     *
     * @see com.wos.services.IWosClient#getBucketAcl(java.lang.String)
     */
    @Override
    public AccessControlList getBucketAcl(final String bucketName) throws WosException {
        return getBucketAcl(new BaseBucketRequest(bucketName));
    }

    /*
     * (non-Javadoc)
     *
     * @see com.wos.services.IWosClient#getBucketAcl(java.lang.String)
     */
    @Override
    public AccessControlList getBucketAcl(final BaseBucketRequest request) throws WosException {
        ServiceUtils.asserParameterNotNull(request, "BaseBucketRequest is null");
        ServiceUtils.asserParameterNotNull2(request.getBucketName(), "bucketName is null");
        return this.doActionWithResult("getBucketAcl", request.getBucketName(),
                new ActionCallbackWithResult<AccessControlList>() {

                    @Override
                    public AccessControlList action() throws ServiceException {
                        return WosClient.this.getBucketAclImpl(request);
                    }

                });
    }

    public LifecycleConfiguration getBucketLifecycleConfiguration(final String bucketName) throws WosException {
        return this.getBucketLifecycle(new BaseBucketRequest(bucketName));
    }

    /*
     * (non-Javadoc)
     *
     * @see com.wos.services.IWosClient#getBucketLifecycle(java.lang.String)
     */
    @Override
    public LifecycleConfiguration getBucketLifecycle(final String bucketName) throws WosException {
        return this.getBucketLifecycle(new BaseBucketRequest(bucketName));
    }

    /*
     * (non-Javadoc)
     *
     * @see com.wos.services.IWosClient#getBucketLifecycle(java.lang.String)
     */
    @Override
    public LifecycleConfiguration getBucketLifecycle(final BaseBucketRequest request) throws WosException {
        ServiceUtils.asserParameterNotNull(request, "BaseBucketRequest is null");
        ServiceUtils.asserParameterNotNull2(request.getBucketName(), "bucketName is null");
        return this.doActionWithResult("getBucketLifecycleConfiguration", request.getBucketName(),
                new ActionCallbackWithResult<LifecycleConfiguration>() {

                    @Override
                    public LifecycleConfiguration action() throws ServiceException {
                        return WosClient.this.getBucketLifecycleConfigurationImpl(request);
                    }
                });
    }

    public HeaderResponse setBucketLifecycleConfiguration(final String bucketName,
                                                          final LifecycleConfiguration lifecycleConfig) throws WosException {
        return this.setBucketLifecycle(new SetBucketLifecycleRequest(bucketName, lifecycleConfig));
    }

    /*
     * (non-Javadoc)
     *
     * @see com.wos.services.IWosClient#setBucketLifecycle(java.lang.String,
     * com.wos.services.model.LifecycleConfiguration)
     */
    @Override
    public HeaderResponse setBucketLifecycle(final String bucketName, final LifecycleConfiguration lifecycleConfig)
            throws WosException {
        return this.setBucketLifecycle(new SetBucketLifecycleRequest(bucketName, lifecycleConfig));
    }

    /*
     * (non-Javadoc)
     *
     * @see com.wos.services.IWosClient#setBucketLifecycle(java.lang.String,
     * com.wos.services.model.LifecycleConfiguration)
     */
    @Override
    public HeaderResponse setBucketLifecycle(final SetBucketLifecycleRequest request) throws WosException {
        ServiceUtils.asserParameterNotNull(request, "SetBucketLifecycleRequest is null");
        ServiceUtils.asserParameterNotNull(request.getLifecycleConfig(), "LifecycleConfiguration is null");
        ServiceUtils.asserParameterNotNull2(request.getBucketName(), "bucketName is null");
        return this.doActionWithResult("setBucketLifecycleConfiguration", request.getBucketName(),
                new ActionCallbackWithResult<HeaderResponse>() {

                    @Override
                    public HeaderResponse action() throws ServiceException {
                        return WosClient.this.setBucketLifecycleConfigurationImpl(request);
                    }
                });
    }

    public HeaderResponse deleteBucketLifecycleConfiguration(final String bucketName) throws WosException {
        return this.deleteBucketLifecycle(new BaseBucketRequest(bucketName));
    }

    /*
     * (non-Javadoc)
     *
     * @see com.wos.services.IWosClient#deleteBucketLifecycle(java.lang.String)
     */
    @Override
    public HeaderResponse deleteBucketLifecycle(final String bucketName) throws WosException {
        return this.deleteBucketLifecycle(new BaseBucketRequest(bucketName));
    }

    /*
     * (non-Javadoc)
     *
     * @see com.wos.services.IWosClient#deleteBucketLifecycle(java.lang.String)
     */
    @Override
    public HeaderResponse deleteBucketLifecycle(final BaseBucketRequest request) throws WosException {
        ServiceUtils.asserParameterNotNull(request, "BaseBucketRequest is null");
        ServiceUtils.asserParameterNotNull2(request.getBucketName(), "bucketName is null");
        return this.doActionWithResult("deleteBucketLifecycleConfiguration", request.getBucketName(),
                new ActionCallbackWithResult<HeaderResponse>() {
                    @Override
                    public HeaderResponse action() throws ServiceException {
                        return WosClient.this.deleteBucketLifecycleConfigurationImpl(request);
                    }
                });

    }

    /*
     * (non-Javadoc)
     *
     * @see com.wos.services.IWosClient#putObject(java.lang.String,
     * java.lang.String, java.io.InputStream,
     * com.wos.services.model.ObjectMetadata)
     */
    @Override
    public PutObjectResult putObject(String bucketName, String objectKey, InputStream input, ObjectMetadata metadata)
            throws WosException {
        PutObjectRequest request = new PutObjectRequest();
        request.setBucketName(bucketName);
        request.setInput(input);
        request.setMetadata(metadata);
        request.setObjectKey(objectKey);
        return this.putObject(request);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.wos.services.IWosClient#putObject(java.lang.String,
     * java.lang.String, java.io.InputStream)
     */
    @Override
    public PutObjectResult putObject(String bucketName, String objectKey, InputStream input) throws WosException {
        return this.putObject(bucketName, objectKey, input, null);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.wos.services.IWosClient#putObject(com.wos.services.model.
     * PutObjectRequest)
     */
    @Override
    public PutObjectResult putObject(final PutObjectRequest request) throws WosException {

        ServiceUtils.asserParameterNotNull(request, "PutObjectRequest is null");
        ServiceUtils.asserParameterNotNull2(request.getObjectKey(), "objectKey is null");

        return this.doActionWithResult("putObject", request.getBucketName(),
                new ActionCallbackWithResult<PutObjectResult>() {
                    @Override
                    public PutObjectResult action() throws ServiceException {
                        if (null != request.getInput() && null != request.getFile()) {
                            throw new ServiceException("Both input and file are set, only one is allowed");
                        }
                        return WosClient.this.putObjectImpl(request);
                    }
                });
    }

    /*
     * (non-Javadoc)
     *
     * @see com.wos.services.IWosClient#putObject(java.lang.String,
     * java.lang.String, java.io.File)
     */
    @Override
    public PutObjectResult putObject(String bucketName, String objectKey, File file) throws WosException {
        return this.putObject(bucketName, objectKey, file, null);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.wos.services.IWosClient#putObject(java.lang.String,
     * java.lang.String, java.io.File, com.wos.services.model.ObjectMetadata)
     */
    @Override
    public PutObjectResult putObject(String bucketName, String objectKey, File file, ObjectMetadata metadata)
            throws WosException {
        PutObjectRequest request = new PutObjectRequest();
        request.setBucketName(bucketName);
        request.setFile(file);
        request.setObjectKey(objectKey);
        request.setMetadata(metadata);
        return this.putObject(request);
    }


    /*
     * (non-Javadoc)
     *
     * @see com.wos.services.IWosClient#getObject(com.wos.services.model.
     * GetObjectRequest)
     */
    @Override
    public WosObject getObject(final GetObjectRequest request) throws WosException {

        ServiceUtils.asserParameterNotNull(request, "GetObjectRequest is null");
        ServiceUtils.asserParameterNotNull2(request.getObjectKey(), "objectKey is null");
        return this.doActionWithResult("getObject", request.getBucketName(), new ActionCallbackWithResult<WosObject>() {

            @Override
            public WosObject action() throws ServiceException {
                return WosClient.this.getObjectImpl(request);
            }
        });
    }


    /*
     * (non-Javadoc)
     *
     * @see com.wos.services.IWosClient#getObject(java.lang.String,
     * java.lang.String)
     */
    @Override
    public WosObject getObject(final String bucketName, final String objectKey) throws WosException {
        return this.getObject(new GetObjectRequest(bucketName, objectKey));
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.wos.services.IWosClient#getObjectMetadata(com.wos.services.model.
     * GetObjectMetadataRequest)
     */
    @Override
    public ObjectMetadata getObjectMetadata(final GetObjectMetadataRequest request) throws WosException {
        ServiceUtils.asserParameterNotNull(request, "GetObjectMetadataRequest is null");
        ServiceUtils.asserParameterNotNull2(request.getObjectKey(), "objectKey is null");
        return this.doActionWithResult("getObjectMetadata", request.getBucketName(),
                new ActionCallbackWithResult<ObjectMetadata>() {

                    @Override
                    public ObjectMetadata action() throws ServiceException {
                        return WosClient.this.getObjectMetadataImpl(request);
                    }
                });
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.wos.services.IWosClient#setObjectMetadata(com.wos.services.model.
     * SetObjectMetadataRequest)
     */
    @Override
    public ObjectMetadata setObjectMetadata(final SetObjectMetadataRequest request) throws WosException {
        ServiceUtils.asserParameterNotNull(request, "SetObjectMetadataRequest is null");
        return this.doActionWithResult("setObjectMetadata", request.getBucketName(),
                new ActionCallbackWithResult<ObjectMetadata>() {
                    @Override
                    public ObjectMetadata action() throws ServiceException {
                        return WosClient.this.setObjectMetadataImpl(request);
                    }
                });
    }


    /*
     * (non-Javadoc)
     *
     * @see com.wos.services.IWosClient#getObjectMetadata(java.lang.String,
     * java.lang.String)
     */
    @Override
    public ObjectMetadata getObjectMetadata(String bucketName, String objectKey) throws WosException {
        return this.getObjectMetadata(new GetObjectMetadataRequest(bucketName, objectKey));
    }

    /*
     * (non-Javadoc)
     *
     * @see com.wos.services.IWosClient#restoreObject(com.wos.services.model.
     * RestoreObjectRequest)
     */
    @Override
    public RestoreObjectRequest.RestoreObjectStatus restoreObject(final RestoreObjectRequest request) throws WosException {
        ServiceUtils.asserParameterNotNull(request, "RestoreObjectRequest is null");
        return this.doActionWithResult("restoreObject", request.getBucketName(),
                new ActionCallbackWithResult<RestoreObjectRequest.RestoreObjectStatus>() {

                    @Override
                    public RestoreObjectRequest.RestoreObjectStatus action() throws ServiceException {
                        ServiceUtils.asserParameterNotNull2(request.getObjectKey(), "objectKey is null");
                        return WosClient.this.restoreObjectImpl(request);
                    }
                });

    }

    /*
     * (non-Javadoc)
     *
     * @see com.wos.services.IWosClient#restoreObjectV2(com.wos.services.model.
     * RestoreObjectRequest)
     */
    @Override
    public RestoreObjectResult restoreObjectV2(final RestoreObjectRequest request) throws WosException {
        ServiceUtils.asserParameterNotNull(request, "RestoreObjectRequest is null");
        return this.doActionWithResult("restoreObjectV2", request.getBucketName(),
                new ActionCallbackWithResult<RestoreObjectResult>() {

                    @Override
                    public RestoreObjectResult action() throws ServiceException {
                        ServiceUtils.asserParameterNotNull2(request.getObjectKey(), "objectKey is null");
                        return WosClient.this.restoreObjectV2Impl(request);
                    }
                });
    }

    /*
     * (non-Javadoc)
     *
     * @see com.wos.services.IWosClient#deleteObject(java.lang.String,
     * java.lang.String)
     */
    @Override
    public DeleteObjectResult deleteObject(final String bucketName, final String objectKey) throws WosException {
        DeleteObjectRequest request = new DeleteObjectRequest(bucketName, UrlCodecUtil.dataEncode(objectKey, "UTF-8"));
        return this.deleteObject(request);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.wos.services.IWosClient#deleteObject(java.lang.String,
     * java.lang.String)
     */
    @Override
    public DeleteObjectResult deleteObject(final DeleteObjectRequest request) throws WosException {
        ServiceUtils.asserParameterNotNull(request, "DeleteObjectRequest is null");
        return this.doActionWithResult("deleteObject", request.getBucketName(),
                new ActionCallbackWithResult<DeleteObjectResult>() {

                    @Override
                    public DeleteObjectResult action() throws ServiceException {
                        ServiceUtils.asserParameterNotNull2(request.getObjectKey(), "objectKey is null");
                        return WosClient.this.deleteObjectImpl(request);
                    }
                });
    }

    /*
     * (non-Javadoc)
     *
     * @see com.wos.services.IWosClient#deleteObjects(com.wos.services.model.
     * DeleteObjectsRequest)
     */
    @Override
    public DeleteObjectsResult deleteObjects(final DeleteObjectsRequest deleteObjectsRequest) throws WosException {
        ServiceUtils.asserParameterNotNull(deleteObjectsRequest, "DeleteObjectsRequest is null");
        return this.doActionWithResult("deleteObjects", deleteObjectsRequest.getBucketName(),
                new ActionCallbackWithResult<DeleteObjectsResult>() {

                    @Override
                    public DeleteObjectsResult action() throws ServiceException {
                        return WosClient.this.deleteObjectsImpl(deleteObjectsRequest);
                    }
                });
    }


    /*
     * (non-Javadoc)
     *
     * @see com.wos.services.IWosClient#getObjectAcl(java.lang.String,
     * java.lang.String)
     */
    @Override
    public AccessControlList getObjectAcl(final String bucketName, final String objectKey) throws WosException {
        return getObjectAcl(new GetObjectAclRequest(bucketName, objectKey));
    }

    /*
     * (non-Javadoc)
     *
     * @see com.wos.services.IWosClient#getObjectAcl(java.lang.String,
     * java.lang.String)
     */
    @Override
    public AccessControlList getObjectAcl(final GetObjectAclRequest request) throws WosException {
        ServiceUtils.asserParameterNotNull(request, "GetObjectAclRequest is null");
        ServiceUtils.asserParameterNotNull2(request.getObjectKey(), "objectKey is null");
        return this.doActionWithResult("getObjectAcl", request.getBucketName(),
                new ActionCallbackWithResult<AccessControlList>() {
                    @Override
                    public AccessControlList action() throws ServiceException {
                        return WosClient.this.getObjectAclImpl(request);
                    }

                });
    }

    /*
     * (non-Javadoc)
     *
     * @see com.wos.services.IWosClient#getObjectAvinfo(java.lang.String,
     * java.lang.String)
     */
    @Override
    public String getObjectAvinfo(final String bucketName, final String objectKey)
            throws WosException {
        return getObjectAvinfo(new GetObjectAvinfoRequest(bucketName, objectKey));
    }

    /*
     * (non-Javadoc)
     *
     * @see com.wos.services.IWosClient#getObjectAvinfo(com.wos.services.model.GetObjectAvinfoRequest)
     */
    @Override
    public String getObjectAvinfo(final GetObjectAvinfoRequest request) throws WosException {
        ServiceUtils.asserParameterNotNull(request, "GetObjectAclRequest is null");
        ServiceUtils.asserParameterNotNull2(request.getObjectKey(), "objectKey is null");
        return this.doActionWithResult("getObjectAcl", request.getBucketName(),
                new ActionCallbackWithResult<String>() {
                    @Override
                    public String action() throws ServiceException {
                        return WosClient.this.getObjectAvinfoImpl(request);
                    }

                });
    }


    /*
     * (non-Javadoc)
     *
     * @see com.wos.services.IWosClient#copyObject(com.wos.services.model.
     * CopyObjectRequest)
     */
    @Override
    public CopyObjectResult copyObject(final CopyObjectRequest request) throws WosException {
        ServiceUtils.asserParameterNotNull(request, "CopyObjectRequest is null");
        ServiceUtils.asserParameterNotNull(request.getDestinationBucketName(), "destinationBucketName is null");
        ServiceUtils.asserParameterNotNull2(request.getSourceObjectKey(), "sourceObjectKey is null");
        ServiceUtils.asserParameterNotNull2(request.getDestinationObjectKey(), "destinationObjectKey is null");
        return this.doActionWithResult("copyObject", request.getSourceBucketName(),
                new ActionCallbackWithResult<CopyObjectResult>() {
                    @Override
                    public CopyObjectResult action() throws ServiceException {
                        return WosClient.this.copyObjectImpl(request);
                    }
                });

    }

    /*
     * (non-Javadoc)
     *
     * @see com.wos.services.IWosClient#copyObject(java.lang.String,
     * java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    public CopyObjectResult copyObject(String sourceBucketName, String sourceObjectKey, String destBucketName,
                                       String destObjectKey) throws WosException {
        return this.copyObject(new CopyObjectRequest(sourceBucketName, sourceObjectKey, destBucketName, destObjectKey));
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.wos.services.IWosClient#initiateMultipartUpload(com.wos.services.
     * model.InitiateMultipartUploadRequest)
     */
    @Override
    public InitiateMultipartUploadResult initiateMultipartUpload(final InitiateMultipartUploadRequest request)
            throws WosException {
        ServiceUtils.asserParameterNotNull(request, "InitiateMultipartUploadRequest is null");
        ServiceUtils.asserParameterNotNull2(request.getObjectKey(), "objectKey is null");
        return this.doActionWithResult("initiateMultipartUpload", request.getBucketName(),
                new ActionCallbackWithResult<InitiateMultipartUploadResult>() {
                    @Override
                    public InitiateMultipartUploadResult action() throws ServiceException {
                        return WosClient.this.initiateMultipartUploadImpl(request);
                    }
                });
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.wos.services.IWosClient#abortMultipartUpload(com.wos.services.model.
     * AbortMultipartUploadRequest)
     */
    @Override
    public HeaderResponse abortMultipartUpload(final AbortMultipartUploadRequest request) throws WosException {
        ServiceUtils.asserParameterNotNull(request, "AbortMultipartUploadRequest is null");
        ServiceUtils.asserParameterNotNull2(request.getObjectKey(), "objectKey is null");
        ServiceUtils.asserParameterNotNull(request.getUploadId(), "uploadId is null");
        return this.doActionWithResult("abortMultipartUpload", request.getBucketName(),
                new ActionCallbackWithResult<HeaderResponse>() {
                    @Override
                    public HeaderResponse action() throws ServiceException {
                        return WosClient.this.abortMultipartUploadImpl(request);
                    }
                });
    }

    /*
     * (non-Javadoc)
     *
     * @see com.wos.services.IWosClient#uploadPart(java.lang.String,
     * java.lang.String, java.lang.String, int, java.io.InputStream)
     */
    @Override
    public UploadPartResult uploadPart(String bucketName, String objectKey, String uploadId, int partNumber,
                                       InputStream input) throws WosException {
        UploadPartRequest request = new UploadPartRequest();
        request.setBucketName(bucketName);
        request.setObjectKey(objectKey);
        request.setUploadId(uploadId);
        request.setPartNumber(partNumber);
        request.setInput(input);
        return this.uploadPart(request);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.wos.services.IWosClient#uploadPart(java.lang.String,
     * java.lang.String, java.lang.String, int, java.io.File)
     */
    @Override
    public UploadPartResult uploadPart(String bucketName, String objectKey, String uploadId, int partNumber, File file)
            throws WosException {
        UploadPartRequest request = new UploadPartRequest();
        request.setBucketName(bucketName);
        request.setObjectKey(objectKey);
        request.setUploadId(uploadId);
        request.setPartNumber(partNumber);
        request.setFile(file);
        return this.uploadPart(request);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.wos.services.IWosClient#uploadPart(com.wos.services.model.
     * UploadPartRequest)
     */
    @Override
    public UploadPartResult uploadPart(final UploadPartRequest request) throws WosException {

        ServiceUtils.asserParameterNotNull(request, "UploadPartRequest is null");
        ServiceUtils.asserParameterNotNull2(request.getObjectKey(), "objectKey is null");
        ServiceUtils.asserParameterNotNull(request.getUploadId(), "uploadId is null");
        return this.doActionWithResult("uploadPart", request.getBucketName(),
                new ActionCallbackWithResult<UploadPartResult>() {

                    @Override
                    public UploadPartResult action() throws ServiceException {
                        if (null != request.getInput() && null != request.getFile()) {
                            throw new ServiceException("Both input and file are set, only one is allowed");
                        }
                        return WosClient.this.uploadPartImpl(request);
                    }
                });

    }

    /*
     * (non-Javadoc)
     *
     * @see com.wos.services.IWosClient#copyPart(com.wos.services.model.
     * CopyPartRequest)
     */
    @Override
    public CopyPartResult copyPart(final CopyPartRequest request) throws WosException {
        ServiceUtils.asserParameterNotNull(request, "CopyPartRequest is null");
        ServiceUtils.asserParameterNotNull2(request.getSourceObjectKey(), "sourceObjectKey is null");
        ServiceUtils.asserParameterNotNull(request.getDestinationBucketName(), "destinationBucketName is null");
        ServiceUtils.asserParameterNotNull2(request.getDestinationObjectKey(), "destinationObjectKey is null");
        ServiceUtils.asserParameterNotNull(request.getUploadId(), "uploadId is null");
        return this.doActionWithResult("copyPart", request.getSourceBucketName(),
                new ActionCallbackWithResult<CopyPartResult>() {

                    @Override
                    public CopyPartResult action() throws ServiceException {
                        return WosClient.this.copyPartImpl(request);
                    }
                });

    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.wos.services.IWosClient#completeMultipartUpload(com.wos.services.
     * model.CompleteMultipartUploadRequest)
     */
    @Override
    public CompleteMultipartUploadResult completeMultipartUpload(final CompleteMultipartUploadRequest request)
            throws WosException {
        ServiceUtils.asserParameterNotNull(request, "CompleteMultipartUploadRequest is null");
        ServiceUtils.asserParameterNotNull2(request.getObjectKey(), "objectKey is null");
        ServiceUtils.asserParameterNotNull(request.getUploadId(), "uploadId is null");
        return this.doActionWithResult("completeMultipartUpload", request.getBucketName(),
                new ActionCallbackWithResult<CompleteMultipartUploadResult>() {
                    @Override
                    public CompleteMultipartUploadResult action() throws ServiceException {
                        return WosClient.this.completeMultipartUploadImpl(request);
                    }
                });
    }

    /*
     * (non-Javadoc)
     *
     * @see com.wos.services.IWosClient#listParts(com.wos.services.model.
     * ListPartsRequest)
     */
    @Override
    public ListPartsResult listParts(final ListPartsRequest request) throws WosException {
        ServiceUtils.asserParameterNotNull(request, "ListPartsRequest is null");
        ServiceUtils.asserParameterNotNull2(request.getKey(), "objectKey is null");
        ServiceUtils.asserParameterNotNull(request.getUploadId(), "uploadId is null");
        return this.doActionWithResult("listParts", request.getBucketName(),
                new ActionCallbackWithResult<ListPartsResult>() {

                    @Override
                    public ListPartsResult action() throws ServiceException {
                        return WosClient.this.listPartsImpl(request);
                    }
                });

    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.wos.services.IWosClient#listMultipartUploads(com.wos.services.model.
     * ListMultipartUploadsRequest)
     */
    @Override
    public MultipartUploadListing listMultipartUploads(final ListMultipartUploadsRequest request) throws WosException {
        ServiceUtils.asserParameterNotNull(request, "ListMultipartUploadsRequest is null");
        return this.doActionWithResult("listMultipartUploads", request.getBucketName(),
                new ActionCallbackWithResult<MultipartUploadListing>() {

                    @Override
                    public MultipartUploadListing action() throws ServiceException {
                        return WosClient.this.listMultipartUploadsImpl(request);
                    }
                });

    }

    /**
     * method to create audio and video task
     * @param request create decompression request
     * @return task Id
     * @throws WosException exception
     */
    @Override
    public AudioAndVideoTaskRequestResult createAudioAndVideoTask(final CreateAudioAndVideoTaskRequest request) throws WosException {
        ServiceUtils.asserParameterNotNull(request, "CreateAudioAndVideoTaskRequest is null");
        ServiceUtils.asserParameterNotNull(request.getOperationType(), "operationType is null");
        ServiceUtils.asserParameterNotNull(request.getSourceFileName(), "sourceFileName is null");
        return this.doActionWithResult("createAudioAndVideoTask", request.getBucketName(),
                new ActionCallbackWithResult<AudioAndVideoTaskRequestResult>() {
                    @Override
                    public AudioAndVideoTaskRequestResult action() throws ServiceException {
                        return WosClient.this.createAudioAndVideoTaskImpl(request);
                    }
                });
    }


    /**
     *
     * @param bucketName source bucket name
     * @param persistentId Id of the process for audio and video operation
     * @param operationType AvOperationTypeEnum
     * @return {@link AudioAndVideoTaskDetailResult}
     */
    @Override
    public AudioAndVideoTaskDetailResult getAudioAndVideoTask(final String bucketName, final String persistentId, final AvOperationTypeEnum operationType)
            throws WosException {
        return getAudioAndVideoTaskDetail(new GetAudioAndVideoTaskRequest(bucketName, persistentId, operationType));
    }


    private AudioAndVideoTaskDetailResult getAudioAndVideoTaskDetail(final GetAudioAndVideoTaskRequest request) {
        ServiceUtils.asserParameterNotNull(request.getBucketName(), "bucketName is null");
        ServiceUtils.asserParameterNotNull(request.getOperationType(), "operationType is null");
        ServiceUtils.asserParameterNotNull(request.getPersistentId(), "persistentId is null");
        return this.doActionWithResult("getAudioAndVideoTaskDetail", request.getBucketName(),
                new ActionCallbackWithResult<AudioAndVideoTaskDetailResult>() {
                    @Override
                    public AudioAndVideoTaskDetailResult action() throws ServiceException {
                        return WosClient.this.getAudioAndVideoTaskDetailImpl(request);
                    }
                });
    }


    /**
     * Note: The decompression operation and the audio and video operation have some similarities, so they share some objects
     * @param request create decompression request
     * @return task Id
     * @throws WosException exception
     */
    @Override
    public AudioAndVideoTaskRequestResult createDecompressTask(final CreateDecompressTaskRequest request) throws WosException {
        ServiceUtils.asserParameterNotNull(request, "CreateAudioAndVideoTaskRequest is null");
        ServiceUtils.asserParameterNotNull(request.getSourceFileName(), "sourceFileName is null");
        return this.doActionWithResult("createAudioAndVideoTask", request.getBucketName(),
                new ActionCallbackWithResult<AudioAndVideoTaskRequestResult>() {
                    @Override
                    public AudioAndVideoTaskRequestResult action() throws ServiceException {
                        return WosClient.this.createDecompressTaskImpl(request);
                    }
                });
    }

    /**
     * Note: The decompression operation and the audio and video operation have some similarities, so they share some objects
     * @param bucketName source bucket name
     * @param persistentId Id of the process for audio and video operation
     * @return {@link QueryDecompressResult}
     * @throws WosException exception
     */
    @Override
    public QueryDecompressResult getDecompressTask(final String bucketName, final String persistentId)
            throws WosException {
        ServiceUtils.asserParameterNotNull(bucketName, "bucketName is null");
        ServiceUtils.asserParameterNotNull(persistentId, "persistentId is null");
        return this.doActionWithResult("getDecompressTask", bucketName,
                new ActionCallbackWithResult<QueryDecompressResult>() {
                    @Override
                    public QueryDecompressResult action() throws ServiceException {
                        return WosClient.this.getDecompressTaskDetailImpl(new GetAudioAndVideoTaskRequest(bucketName, persistentId, AvOperationTypeEnum.Decompression));
                    }
                });
    }


    @Override
    public boolean doesObjectExist(final String buckeName, final String objectKey) throws WosException {
        GetObjectMetadataRequest request = new GetObjectMetadataRequest(buckeName, objectKey);
        return this.doesObjectExist(request);
    }

    @Override
    public boolean doesObjectExist(final GetObjectMetadataRequest request) throws WosException {
        ServiceUtils.asserParameterNotNull(request.getBucketName(), "bucke is null");
        ServiceUtils.asserParameterNotNull2(request.getObjectKey(), "objectKey is null");
        return WosClient.this.doesObjectExistImpl(request);
    }

    private abstract class ActionCallbackWithResult<T> {

        abstract T action() throws ServiceException;
    }

    private <T> T doActionWithResult(String action, String bucketName, ActionCallbackWithResult<T> callback)
            throws WosException {
        if (!this.isCname()) {
            ServiceUtils.asserParameterNotNull(bucketName, "bucketName is null");
        }
        InterfaceLogBean reqBean = new InterfaceLogBean(action, this.getEndpoint(), "");
        try {
            long start = System.currentTimeMillis();
            T ret = callback.action();
            reqBean.setRespTime(new Date());
            reqBean.setResultCode(Constants.RESULTCODE_SUCCESS);
            if (ILOG.isInfoEnabled()) {
                ILOG.info(reqBean);
                ILOG.info("WosClient [" + action + "] cost " + (System.currentTimeMillis() - start) + " ms");
            }
            return ret;
        } catch (ServiceException e) {

            WosException ex = ServiceUtils.changeFromServiceException(e);
            if (ex.getResponseCode() >= 400 && ex.getResponseCode() < 500) {
                if (ILOG.isWarnEnabled()) {
                    reqBean.setRespTime(new Date());
                    reqBean.setResultCode(String.valueOf(e.getResponseCode()));
                    ILOG.warn(reqBean);
                }
            } else if (ILOG.isErrorEnabled()) {
                reqBean.setRespTime(new Date());
                reqBean.setResultCode(String.valueOf(ex.getResponseCode()));
                ILOG.error(reqBean);
            }
            throw ex;
        } finally {
            if (this.isAuthTypeNegotiation()) {
                this.getProviderCredentials().removeThreadLocalAuthType();
            }
            AccessLoggerUtils.printLog();
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see com.wos.services.IWosClient#close()
     */
    @Override
    public void close() throws IOException {
        this.shutdown();
    }

    public String base64Md5(InputStream is, long length, long offset) throws NoSuchAlgorithmException, IOException {
        return ServiceUtils.toBase64(ServiceUtils.computeMD5Hash(is, length, offset));
    }

    public String base64Md5(InputStream is) throws NoSuchAlgorithmException, IOException {
        return ServiceUtils.toBase64(ServiceUtils.computeMD5Hash(is));
    }
}
