package com.wos.services.model;

import com.wos.services.internal.WosConstraint;
import com.wos.services.internal.utils.ServiceUtils;

import java.util.Date;
import java.util.Map;

/**
 * Parameters in an object download request
 */
public class GetObjectRequest extends GenericRequest {
    private String bucketName;

    private String objectKey;

    private Long rangeStart;

    private Long rangeEnd;

    private ObjectRepleaceMetadata replaceMetadata;

    private Date ifModifiedSince;

    private Date ifUnmodifiedSince;

    private String ifMatchTag;

    private String ifNoneMatchTag;

    private String imageProcess;

    private ProgressListener progressListener;

    private long progressInterval = WosConstraint.DEFAULT_PROGRESS_INTERVAL;

    private long ttl;

    private Map<String, String> requestParameters;

    private boolean autoUnzipResponse = false;
    
    public GetObjectRequest() {

    }

    /**
     * Constructor
     * 
     * @param bucketName
     *            Bucket name
     * @param objectKey
     *            Object name
     */
    public GetObjectRequest(String bucketName, String objectKey) {
        this.bucketName = bucketName;
        this.objectKey = objectKey;
    }


    /**
     * Obtain the request headers that need to be rewritten during object
     * download.
     * 
     * @return Rewritten response headers
     */
    public ObjectRepleaceMetadata getReplaceMetadata() {
        return replaceMetadata;
    }

    /**
     * Set the request headers that need to be rewritten during object download.
     * 
     * @param replaceMetadata
     *            Rewritten response headers
     */
    public void setReplaceMetadata(ObjectRepleaceMetadata replaceMetadata) {
        this.replaceMetadata = replaceMetadata;
    }

    /**
     * Obtain the start position for object download.
     * 
     * @return Start position for object download
     */
    public Long getRangeStart() {
        return rangeStart;
    }

    /**
     * Set the start position for object download.
     * 
     * @param rangeStart
     *            Start position for object download
     */
    public void setRangeStart(Long rangeStart) {
        this.rangeStart = rangeStart;
    }

    /**
     * Obtain the end position for object download.
     * 
     * @return End position for object download
     */
    public Long getRangeEnd() {
        return rangeEnd;
    }

    /**
     * Set the end position for object download.
     * 
     * @param rangeEnd
     *            End position for object download
     * 
     */
    public void setRangeEnd(Long rangeEnd) {
        this.rangeEnd = rangeEnd;
    }

    /**
     * Obtain the bucket name.
     * 
     * @return Bucket name
     */
    public String getBucketName() {
        return bucketName;
    }

    /**
     * Set the bucket name.
     * 
     * @param bucketName
     *            Bucket name
     * 
     */
    public void setBucketName(String bucketName) {
        this.bucketName = bucketName;
    }

    /**
     * Obtain the object name.
     * 
     * @return Object name
     */
    public String getObjectKey() {
        return objectKey;
    }

    /**
     * Set the object name.
     * 
     * @param objectKey
     *            Object name
     * 
     */
    public void setObjectKey(String objectKey) {
        this.objectKey = objectKey;
    }


    /**
     * Obtain the time condition set for downloading the object. Only when the
     * object is modified after the point in time specified by this parameter,
     * it will be downloaded. Otherwise, "304 Not Modified" will be returned.
     * 
     * @return Time condition set for downloading the object
     */
    public Date getIfModifiedSince() {
        return ServiceUtils.cloneDateIgnoreNull(this.ifModifiedSince);
    }

    /**
     * Set the time condition set for downloading the object. Only when the
     * object is modified after the point in time specified by this parameter,
     * it will be downloaded. Otherwise, "304 Not Modified" will be returned.
     * 
     * @param ifModifiedSince
     *            Time condition set for downloading the object
     */
    public void setIfModifiedSince(Date ifModifiedSince) {
        this.ifModifiedSince = ServiceUtils.cloneDateIgnoreNull(ifModifiedSince);
    }

    /**
     * Obtain the time condition for downloading the object. Only when the
     * object remains unchanged after the point in time specified by this
     * parameter, it will be downloaded; otherwise, "412 Precondition Failed"
     * will be returned.
     * 
     * @return Time condition set for downloading the object
     */
    public Date getIfUnmodifiedSince() {
        return ServiceUtils.cloneDateIgnoreNull(this.ifUnmodifiedSince);
    }

    /**
     * Set the time condition for downloading the object. Only when the object
     * remains unchanged after the point in time specified by this parameter, it
     * will be downloaded; otherwise, "412 Precondition Failed" will be
     * returned.
     * 
     * @param ifUnmodifiedSince
     *            Time condition set for downloading the object
     */
    public void setIfUnmodifiedSince(Date ifUnmodifiedSince) {
        this.ifUnmodifiedSince = ServiceUtils.cloneDateIgnoreNull(ifUnmodifiedSince);
    }

    /**
     * Obtain the ETag verification condition for downloading the object. Only
     * when the ETag of the object is the same as that specified by this
     * parameter, the object will be downloaded. Otherwise,
     * "412 Precondition Failed" will be returned.
     * 
     * @return ETag verification condition set for downloading the object
     */
    public String getIfMatchTag() {
        return ifMatchTag;
    }

    /**
     * Set the ETag verification condition for downloading the object. Only when
     * the ETag of the object is the same as that specified by this parameter,
     * the object will be downloaded. Otherwise, "412 Precondition Failed" will
     * be returned.
     * 
     * @param ifMatchTag
     *            ETag verification condition set for downloading the object
     */
    public void setIfMatchTag(String ifMatchTag) {
        this.ifMatchTag = ifMatchTag;
    }

    /**
     * Obtain the ETag verification condition for downloading the object. Only
     * when the ETag of the object is different from that specified by this
     * parameter, the object will be downloaded. Otherwise, "304 Not Modified"
     * will be returned.
     * 
     * @return ETag verification condition set for downloading the object
     */
    public String getIfNoneMatchTag() {
        return ifNoneMatchTag;
    }

    /**
     * Set the ETag verification condition for downloading the object. Only when
     * the ETag of the object is different from that specified by this
     * parameter, the object will be downloaded. Otherwise, "304 Not Modified"
     * will be returned.
     * 
     * @param ifNoneMatchTag
     *            ETag verification condition set for downloading the object
     * 
     */
    public void setIfNoneMatchTag(String ifNoneMatchTag) {
        this.ifNoneMatchTag = ifNoneMatchTag;
    }

    /**
     * Obtain image processing parameters.
     * 
     * @return Image processing parameters
     */
    public String getImageProcess() {
        return imageProcess;
    }

    /**
     * Set image processing parameters.
     * 
     * @param imageProcess
     *            Image processing parameters
     */
    public void setImageProcess(String imageProcess) {
        this.imageProcess = imageProcess;
    }

    /**
     * Obtain the data transfer listener.
     * 
     * @return Data transfer listener
     */
    public ProgressListener getProgressListener() {
        return progressListener;
    }

    /**
     * Set the data transfer listener.
     * 
     * @param progressListener
     *            Data transfer listener
     */
    public void setProgressListener(ProgressListener progressListener) {
        this.progressListener = progressListener;
    }

    /**
     * Obtain the callback threshold of the data transfer listener. The default
     * value is 100 KB.
     * 
     * @return Callback threshold of the data transfer listener
     */
    public long getProgressInterval() {
        return progressInterval;
    }

    /**
     * Set the callback threshold of the data transfer listener. The default
     * value is 100 KB.
     * 
     * @param progressInterval
     *            Callback threshold of the data transfer listener
     */
    public void setProgressInterval(long progressInterval) {
        this.progressInterval = progressInterval;
    }

    public Map<String, String> getRequestParameters() {
        return this.requestParameters;
    }


    /**
     * Obtain the cache data expiration time.
     * 
     * @return Cache data expiration time
     */
    public long getTtl() {
        return ttl;
    }

    /**
     * Set the cache data expiration time.
     * 
     * @param ttl
     *            Cache data expiration time
     */
    public void setTtl(long ttl) {
        if (ttl < 0 || ttl > 259200) {
            ttl = 60 * 60 * 24L;
        }
        this.ttl = ttl;
    }

    public void setRequestParameters(Map<String, String> requestParameters) {
        this.requestParameters = requestParameters;
    }
    
    /**
     * Obtain the response to whether to automatically decompress the object compressed using gzip. The default response is false, indicating that the object is not automatically decompressed.<br>
     * Description: WOS SDK uses okhttp. If Accept-Encoding is not set in a request, okhttp automatically adds the Accept-Encoding:gzip header and decompresses the object after receiving the request.<br>
     * If the Accept-Encoding header is manually set in a request, the object will not be automatically decompressed.
     * @see okhttp3.internal.http.BridgeInterceptor
     * @return boolean
     * @since 3.20.5
     */
    public boolean isAutoUnzipResponse() {
        return autoUnzipResponse;
    }

    /**
     * Set the response to whether to automatically decompress the object compressed using gzip. The default response is false, indicating that the object is not automatically decompressed.<br>
     * Description: WOS SDK uses okhttp. If Accept-Encoding is not set in a request, okhttp automatically adds the Accept-Encoding:gzip header and decompresses the object after receiving the request.<br>
     * If the Accept-Encoding header is manually set in a request, the object will not be automatically decompressed.
     * @see okhttp3.internal.http.BridgeInterceptor
     * @param autoUnzipResponse boolean
     *  boolean value
     * @since 3.20.5
     */
    public void setAutoUnzipResponse(boolean autoUnzipResponse) {
        this.autoUnzipResponse = autoUnzipResponse;
    }

    @Override
    public String toString() {
        return "GetObjectRequest [bucketName=" + bucketName + ", objectKey=" + objectKey + ", rangeStart=" + rangeStart
                + ", rangeEnd=" + rangeEnd + ", replaceMetadata=" + replaceMetadata
                + ", ifModifiedSince=" + ifModifiedSince + ", ifUnmodifiedSince="
                + ifUnmodifiedSince + ", ifMatchTag=" + ifMatchTag + ", ifNoneMatchTag=" + ifNoneMatchTag
                + ", imageProcess=" + imageProcess + ", autoUnzipResponse=" + autoUnzipResponse + "]";
    }

}
