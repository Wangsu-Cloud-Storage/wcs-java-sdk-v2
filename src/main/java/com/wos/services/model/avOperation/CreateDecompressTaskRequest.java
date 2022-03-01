package com.wos.services.model.avOperation;

import com.wos.services.model.GenericRequest;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

/**
 * Parameters in a request for creating decompressed task
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = {"config", "notifyUrl", "force"})
public class CreateDecompressTaskRequest extends GenericRequest {
    /**
     * @see AvOperationTypeEnum
     * audio/video operation type
     */
    @XmlTransient
    private AvOperationTypeEnum operationType;

    /**
     * the name of source file
     */
    @XmlTransient
    private String sourceFileName;

    /**
     * the bucket name of source file
     */
    @XmlTransient
    private String bucketName;

    /**
     * define the decompression processing operation
     */
    @XmlElement(name = "Config")
    private AudioAndVideoOperationConfig config;

    /**
     * The URL for receiving the notification result requires UrlEncode encoding
     */
    @XmlElement(name = "NotifyURL")
    private String notifyUrl;

    /**
     * whether to enforce data processing
     * You can set the following values:
     *  0: If the specified data processing result exists, the system returns that the file already exists and does not process the file to avoid wasting resources
     *  1: forces data processing and overwrites existing files
     * The default value is 0
     */
    @XmlElement(name = "Force")
    private Integer force;

    public CreateDecompressTaskRequest() {
    }

    public AvOperationTypeEnum getOperationType() {
        return operationType;
    }

    public void setOperationType(AvOperationTypeEnum operationType) {
        this.operationType = operationType;
    }

    public String getSourceFileName() {
        return sourceFileName;
    }

    public void setSourceFileName(String sourceFileName) {
        this.sourceFileName = sourceFileName;
    }

    public String getBucketName() {
        return bucketName;
    }

    public void setBucketName(String bucketName) {
        this.bucketName = bucketName;
    }

    public AudioAndVideoOperationConfig getConfig() {
        return config;
    }

    public void setConfig(AudioAndVideoOperationConfig config) {
        this.config = config;
    }

    public String getNotifyUrl() {
        return notifyUrl;
    }

    public void setNotifyUrl(String notifyUrl) {
        this.notifyUrl = notifyUrl;
    }

    public Integer getForce() {
        return force;
    }

    public void setForce(Integer force) {
        this.force = force;
    }
}
