package com.wos.services.model.avOperation;

import com.wos.services.model.HeaderResponse;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;


/**
 * Response to an audio or video decompress request
 */
@XmlRootElement(name = "QueryDecompressionResult")
@XmlAccessorType(XmlAccessType.FIELD)
public class QueryDecompressResult extends HeaderResponse {

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
     * Source file information
     */
    @XmlElement(name = "Input")
    private InputBucketInfo input;

    /**
     * State information for each operation. If the processing request includes multiple operations,
     * the ItemList contains multiple information items
     */
    @XmlElement(name = "Item")
    private AvOperationItem item;


    public QueryDecompressResult() {
    }

    public String getOperationType() {
        return operationType;
    }

    public void setOperationType(String operationType) {
        this.operationType = operationType;
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

    public InputBucketInfo getInput() {
        return input;
    }

    public void setInput(InputBucketInfo input) {
        this.input = input;
    }

    public AvOperationItem getItem() {
        return item;
    }

    public void setItem(AvOperationItem item) {
        this.item = item;
    }
}
