package com.wos.services.model.avOperation;

import com.wos.services.model.HeaderResponse;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlTransient;
import java.util.List;

/**
 * Audio and video task processing result
 */
@XmlAccessorType(XmlAccessType.FIELD)
public class AudioAndVideoTaskDetailResult extends HeaderResponse {


    /**
     * operation type
     * @see AvOperationTypeEnum
     */
    @XmlTransient
    private String operationType;

    /**
     * PersistentId returned from the audio and video processing interface
     */
    @XmlElement(name = "Id")
    private String id;

    /**
     * Task creation time
     * The format is YYYY-MM-DDTHh: MM :ssZ
     */
    @XmlElement(name = "CreationTime")
    private String createTime = "";

    /**
     * The format is YYYY-MM-DDTHh: MM :ssZ
     * the processing status 0 -- pending, 1 -- Processed, 3 -- completed,
     * 4 -- notifying, 5 -- notified failed, and 6 -- notified successfully
     */
    @XmlElement(name = "Code")
    private Integer code;

    /**
     * Detailed description corresponding to the status code
     */
    @XmlElement(name = "Desc")
    private String desc;

    /**
     * Separate notification option. 0 indicates one-time notification and 1 indicates separate notification
     */
    @XmlElement(name = "Separate")
    private Integer separate;

    /**
     * Source file information
     */
    @XmlElement(name = "Input")
    private InputBucketInfo input;

    /**
     * State information for each operation. If the processing request includes multiple operations,
     * the ItemList contains multiple information items
     */
    @XmlElementWrapper(name = "ItemList")
    @XmlElement(name = "Item")
    private List<AvOperationItem> avOperationItemList;

    public AudioAndVideoTaskDetailResult() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public Integer getSeparate() {
        return separate;
    }

    public void setSeparate(Integer separate) {
        this.separate = separate;
    }

    public InputBucketInfo getInput() {
        return input;
    }

    public void setInput(InputBucketInfo input) {
        this.input = input;
    }

    public List<AvOperationItem> getAvOperationItemList() {
        return avOperationItemList;
    }

    public void setAvOperationItemList(List<AvOperationItem> avOperationItemList) {
        this.avOperationItemList = avOperationItemList;
    }

    public String getOperationType() {
        return operationType;
    }

    public void setOperationType(String operationType) {
        this.operationType = operationType;
    }
}
