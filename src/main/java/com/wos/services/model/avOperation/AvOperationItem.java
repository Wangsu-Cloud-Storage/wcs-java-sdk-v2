package com.wos.services.model.avOperation;


import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

/**
 * @author luosh
 */
@XmlRootElement(name = "Item")
@XmlAccessorType(XmlAccessType.FIELD)
public class AvOperationItem {

    /**
     * The operation commands that system actually executes (OPS)
     */
    @XmlElement(name = "Cmd")
    private String cmd;

    /**
     * Processing result status code, 0--to be processed, 1--processing, 2--processing failure,
     * 3--processing success, 4--processing success and notifying, 5--processing success and notification failure,
     * 6--processing success and notification success, 18--processing failure notification,
     * 19--processing failure notification failure, 20--processing failure notification success
     */
    @XmlElement(name = "Code")
    private Integer code;

    /**
     * Detailed description corresponding to the status code
     */
    @XmlElement(name = "Desc")
    private String desc;

    /**
     * Processing time, the default is 0 for non-special scenarios
     */
    @XmlElement(name = "CostTime")
    private Integer costTime;

    /**
     * If processing failed, this field lists the specific reason
     */
    @XmlElement(name = "Error")
    private String error;


    /**
     * When there are multiple output files, the specific information of each output file
     */
    @XmlElementWrapper(name = "OutputList")
    @XmlElement(name = "Output")
    private List<OutPutFileDetail> outPutFileInfoList;

    public AvOperationItem() {
    }


    public String getCmd() {
        return cmd;
    }

    public void setCmd(String cmd) {
        this.cmd = cmd;
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

    public Integer getCostTime() {
        return costTime;
    }

    public void setCostTime(Integer costTime) {
        this.costTime = costTime;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public List<OutPutFileDetail> getOutPutFileInfoList() {
        return outPutFileInfoList;
    }

    public void setOutPutFileInfoList(List<OutPutFileDetail> outPutFileInfoList) {
        this.outPutFileInfoList = outPutFileInfoList;
    }
}