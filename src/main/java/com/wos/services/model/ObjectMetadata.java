package com.wos.services.model;

import com.wos.services.internal.Constants;
import com.wos.services.internal.utils.ServiceUtils;

import java.util.Date;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Object properties
 */
public class ObjectMetadata extends HeaderResponse {
    private Date lastModified;

    private Long contentLength;

    private String contentType;

    private String contentEncoding;

    private String etag;

    private String contentMd5;

    private StorageClassEnum storageClass;

    public ObjectMetadata() {

    }

    /**
     * Obtain object properties.
     * 
     * @return Object properties
     */
    public Map<String, Object> getMetadata() {
        return this.getResponseHeaders();
    }

    /**
     * Add customized metadata for an object.
     * 
     * @param key
     *            Keyword of the customized metadata
     * @param value
     *            Value of the customized metadata
     */
    public void addUserMetadata(String key, String value) {
        getMetadata().put(Constants.WOS_HEADER_META_PREFIX + key, value);
    }

    /**
     * Obtain the customized metadata of an object.
     * 
     * @param key
     *            Keyword of the customized metadata
     * @return Value of the customized metadata
     */
    public Object getUserMetadata(String key) {
        return getMetadata().get(key);
    }

    /**
     * Obtain the ETag of the object.
     * 
     * @return ETag of the object
     */
    public String getEtag() {
        return etag;
    }

    public void setEtag(String objEtag) {
        this.etag = objEtag;
    }

    /**
     * Set object properties.
     * 
     * @param metadata
     *            Object properties
     */
    public void setMetadata(Map<String, Object> metadata) {
        this.responseHeaders = metadata;
    }

    /**
     * Obtain the last modification time of the object.
     * 
     * @return Last modification time of the object
     */
    public Date getLastModified() {
        return ServiceUtils.cloneDateIgnoreNull(this.lastModified);
    }

    public void setLastModified(Date lastModified) {
        this.lastModified = ServiceUtils.cloneDateIgnoreNull(lastModified);
    }

    /**
     * Obtain the content encoding of the object.
     * 
     * @return Content encoding
     */
    public String getContentEncoding() {
        return contentEncoding;
    }

    /**
     * Set the content encoding of the object.
     * 
     * @param contentEncoding
     *            Content encoding
     */
    public void setContentEncoding(String contentEncoding) {
        this.contentEncoding = contentEncoding;
    }

    /**
     * Obtain the content length of an object.
     * 
     * @return Content length of the object
     */
    public Long getContentLength() {
        return contentLength;
    }

    /**
     * Set the content length of an object.
     * 
     * @param contentLength
     *            Content length of the object
     */
    public void setContentLength(Long contentLength) {
        this.contentLength = contentLength;
    }

    /**
     * Obtain the MIME type of an object.
     * 
     * @return MIME type of the object
     */
    public String getContentType() {
        return contentType;
    }

    /**
     * Set the MIME type for an object.
     * 
     * @param contentType
     *            MIME type of the object
     */
    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    /**
     * Obtain the object storage class.
     * 
     * @return Object storage class
     */

    public String getStorageClass() {
        return this.storageClass != null ? this.storageClass.getCode() : null;
    }

    /**
     * Set the object storage class.
     * 
     * @param storageClass
     *            Object storage class
     */

    public void setStorageClass(String storageClass) {
        this.storageClass = StorageClassEnum.getValueFromCode(storageClass);
    }

    /**
     * Obtain the object storage class.
     * 
     * @return Object storage class
     */
    public StorageClassEnum getObjectStorageClass() {
        return storageClass;
    }

    /**
     * Set the object storage class.
     * 
     * @param storageClass
     *            Object storage class
     */
    public void setObjectStorageClass(StorageClassEnum storageClass) {
        this.storageClass = storageClass;
    }

    public Object getValue(String name) {
        for (Entry<String, Object> entry : this.getMetadata().entrySet()) {
            if (isEqualsIgnoreCase(entry.getKey(), name)) {
                return entry.getValue();
            }
        }
        return null;
    }

    private boolean isEqualsIgnoreCase(String key1, String key2) {
        if (key1 == null && key2 == null) {
            return true;
        }
        if (key1 == null || key2 == null) {
            return false;
        }

        return key1.equalsIgnoreCase(key2);
    }

    /**
     * Obtain Base64-encoded MD5 value of an object.
     * 
     * @return Base64-encoded MD5 value of the object
     */
    public String getContentMd5() {
        return contentMd5;
    }

    /**
     * Set the Base64-encoded MD5 value for an object.
     * 
     * @param contentMd5
     *            Base64-encoded MD5 value of the object
     */
    public void setContentMd5(String contentMd5) {
        this.contentMd5 = contentMd5;
    }

    @Override
    public String toString() {
        return "ObjectMetadata [metadata=" + this.getMetadata() + ", lastModified=" + lastModified + ", contentLength="
                + contentLength + ", contentType=" + contentType + ", contentEncoding=" + contentEncoding + ", etag="
                + etag + ", contentMd5=" + contentMd5 + ", storageClass=" + storageClass + "]";
    }

}
