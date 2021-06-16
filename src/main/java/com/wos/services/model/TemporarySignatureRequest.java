package com.wos.services.model;

import com.wos.services.internal.WosConstraint;
import com.wos.services.internal.utils.ServiceUtils;

import java.util.Date;

/**
 * Parameters in a request for temporarily authorized access
 *
 */
public class TemporarySignatureRequest extends AbstractTemporarySignatureRequest {

    private long expires = WosConstraint.DEFAULT_EXPIRE_SECONEDS;

    private Date requestDate;

    public TemporarySignatureRequest() {
    }

    /**
     * Constructor
     * 
     * @param method
     *            HTTP/HTTPS request method
     * @param expires
     *            Expiration time (in seconds)
     */
    public TemporarySignatureRequest(HttpMethodEnum method, long expires) {
        this(method, null, null, null, expires);
    }

    /**
     * Constructor
     * 
     * @param method
     *            HTTP/HTTPS request method
     * @param bucketName
     *            Bucket name
     * @param objectKey
     *            Object name
     * @param specialParam
     *            Special operator
     * @param expires
     *            Expiration time (in seconds)
     */
    public TemporarySignatureRequest(HttpMethodEnum method, String bucketName, String objectKey,
            SpecialParamEnum specialParam, long expires) {
        this(method, bucketName, objectKey, specialParam, expires, null);
    }

    /**
     * Constructor
     * 
     * @param method
     *            HTTP/HTTPS request method
     * @param bucketName
     *            Bucket name
     * @param objectKey
     *            Object name
     * @param specialParam
     *            Special operator
     * @param expires
     *            Expiration time (in seconds)
     * @param requestDate
     *            Request date
     */
    public TemporarySignatureRequest(HttpMethodEnum method, String bucketName, String objectKey,
            SpecialParamEnum specialParam, long expires, Date requestDate) {
        this.method = method;
        this.bucketName = bucketName;
        this.objectKey = objectKey;
        this.specialParam = specialParam;
        this.expires = expires;
        this.requestDate = ServiceUtils.cloneDateIgnoreNull(requestDate);
    }

    /**
     * Obtain the validity period of the temporary authorization (in seconds).
     * The devalue value is 5 minutes (value "300") and the maximum value is 7
     * days ("604800").
     * 
     * @return Validity period
     */
    public long getExpires() {
        return expires;
    }

    /**
     * Obtain the validity period of the temporary authorization (in seconds).
     * The devalue value is 5 minutes (value "300") and the maximum value is 7
     * days ("604800").
     * 
     * @param expires
     *            Validity period
     */
    public void setExpires(long expires) {
        this.expires = expires;
    }

    /**
     * Set the request time.
     * 
     * @return Request time
     */
    public Date getRequestDate() {
        return ServiceUtils.cloneDateIgnoreNull(requestDate);
    }

    /**
     * Set the request time.
     * 
     * @param requestDate
     *            Request date
     */
    public void setRequestDate(Date requestDate) {
        if (null != requestDate) {
            this.requestDate = (Date) requestDate.clone();
        } else {
            this.requestDate = null;
        }
    }

    @Override
    public String toString() {
        return "TemporarySignatureRequest [method=" + method + ", bucketName=" + bucketName + ", objectKey=" + objectKey
                + ", specialParam=" + specialParam + ", expires=" + expires + ", requestDate=" + requestDate
                + ", headers=" + getHeaders() + ", queryParams=" + getQueryParams() + "]";
    }

}
