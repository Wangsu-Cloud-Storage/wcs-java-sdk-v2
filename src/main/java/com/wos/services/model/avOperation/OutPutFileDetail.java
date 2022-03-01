package com.wos.services.model.avOperation;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Processed file information
 * Note: Some operations may not have partial values
 * @author luosh
 */
@XmlRootElement(name = "Output")
@XmlAccessorType(XmlAccessType.FIELD)
public class OutPutFileDetail {
    /**
     * The bucket name of the processed file
     */
    @XmlElement(name = "OutputBucket")
    private String outputBucket;

    /**
     * the file name of the processed file
     */
    @XmlElement(name = "OutputKey")
    private String outputKey;

    /**
     * the file name of the processed file
     */
    @XmlElement(name = "Size")
    private Long size;

    /**
     * the hash value of the processed file
     */
    @XmlElement(name = "Hash")
    private String hash;

    /**
     * the video duration of the processed file
     */
    @XmlElement(name = "Duration")
    private Double duration;

    /**
     * the bitrate of the processed file
     */
    @XmlElement(name = "Bit_Rate")
    private String bitRate;

    /**
     * the resolution of the processed file
     */
    @XmlElement(name = "Resolution")
    private String resolution;

    public OutPutFileDetail() {
    }

    public String getOutputBucket() {
        return outputBucket;
    }

    public void setOutputBucket(String outputBucket) {
        this.outputBucket = outputBucket;
    }

    public String getOutputKey() {
        return outputKey;
    }

    public void setOutputKey(String outputKey) {
        this.outputKey = outputKey;
    }

    public Long getSize() {
        return size;
    }

    public void setSize(Long size) {
        this.size = size;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public Double getDuration() {
        return duration;
    }

    public void setDuration(Double duration) {
        this.duration = duration;
    }

    public String getBitRate() {
        return bitRate;
    }

    public void setBitRate(String bitRate) {
        this.bitRate = bitRate;
    }

    public String getResolution() {
        return resolution;
    }

    public void setResolution(String resolution) {
        this.resolution = resolution;
    }
}



