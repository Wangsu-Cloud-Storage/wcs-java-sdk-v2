package com.wos.services.model.avOperation;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;


@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = {"outputBucket", "outputKey"})
public class OutPutFileInfo {
    /**
     * The space name of the output file
     */
    @XmlElement(name = "OutputBucket")
    private String outputBucket;

    /**
     * The name of the output file, UrlEncode is required
     */
    @XmlElement(name = "OutputKey")
    private String outputKey;

    public OutPutFileInfo() {
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
}
