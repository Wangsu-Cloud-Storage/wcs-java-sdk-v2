package com.wos.services.model.avOperation;

import com.wos.services.model.GenericRequest;

import javax.xml.bind.annotation.*;
import java.util.List;

/**
 * Parameters in a request for creating audio and video task
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = {"configList", "notifyUrl", "force", "separate"})
public class CreateAudioAndVideoTaskRequest extends GenericRequest {
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
     * A list of operations to process. If processing requests includes multiple operations,
     * the ConfigList contains multiple information Config
     */
    @XmlElementWrapper(name = "ConfigList")
    @XmlElement(name = "Config")
    private List<AudioAndVideoOperationConfig> configList;

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

    /**
     * Whether to notify separately.  You can set the following values:
     *  0: notifyURL is notified once all screenshot commands are executed
     *  1: notifyURL is notified after each screenshot command is executed
     * The default value is 0
     */
    @XmlElement(name = "Separate")
    private Integer separate;


    public CreateAudioAndVideoTaskRequest() {
    }

    public List<AudioAndVideoOperationConfig> getConfigList() {
        return configList;
    }

    public void setConfigList(List<AudioAndVideoOperationConfig> configList) {
        this.configList = configList;
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

    public Integer getSeparate() {
        return separate;
    }

    public void setSeparate(Integer separate) {
        this.separate = separate;
    }

    public AvOperationTypeEnum getOperationType() {
        return operationType;
    }

    public void setOperationType(AvOperationTypeEnum operationType) {
        this.operationType = operationType;
    }

    public String getBucketName() {
        return bucketName;
    }

    public void setBucketName(String bucketName) {
        this.bucketName = bucketName;
    }

    public String getSourceFileName() {
        return sourceFileName;
    }

    public void setSourceFileName(String sourceFileName) {
        this.sourceFileName = sourceFileName;
    }

}
