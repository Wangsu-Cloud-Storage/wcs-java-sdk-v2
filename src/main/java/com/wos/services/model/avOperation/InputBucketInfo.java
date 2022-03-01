package com.wos.services.model.avOperation;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class InputBucketInfo {
    /**
     * The bucket name of the source file
     */
    @XmlElement(name = "Bucket")
    private String bucket;

    /**
     * The file name of the source file
     */
    @XmlElement(name = "Key")
    private String key;

    /**
     * the size of the source file
     */
    @XmlElement(name = "Size")
    private Long size;


    public InputBucketInfo() {
    }

    public String getBucket() {
        return bucket;
    }

    public void setBucket(String bucket) {
        this.bucket = bucket;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Long getSize() {
        return size;
    }

    public void setSize(Long size) {
        this.size = size;
    }
}
