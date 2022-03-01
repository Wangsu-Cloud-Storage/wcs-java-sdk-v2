package com.wos.services.model.avOperation;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * the specific config of operation
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(propOrder = {"operationParams", "outPutFileInfo", "fileList"})
public class AudioAndVideoOperationConfig {
    /**
     * Define the detailed configuration of a audio and video processing
     * Map key -> operation Map value -> value
     * example: map.put("format", "jpg");
     * @For some parameters, The order of keys may be influence the operation result
     */
    @XmlJavaTypeAdapter(MapAdapter.class)
    @XmlElement(name = "Param")
    private LinkedHashMap<String, String> operationParams;

    /**
     * Defines information about an output file
     */
    @XmlElement(name = "Output")
    private OutPutFileInfo outPutFileInfo;

    /**
     * Specifies the list of secondary files to concatenate.
     * If there are more than one secondary files, FileList contains multiple names.
     * @this attribute only use in file concat
     */
    @XmlElementWrapper(name = "FileList")
    @XmlElement(name = "FileName")
    private List<String> fileList;

    public AudioAndVideoOperationConfig() {
    }

    public LinkedHashMap<String, String> getOperationParams() {
        return operationParams;
    }

    public void setOperationParams(LinkedHashMap<String, String> operationParams) {
        this.operationParams = operationParams;
    }

    public OutPutFileInfo getOutPutFileInfo() {
        return outPutFileInfo;
    }

    public void setOutPutFileInfo(OutPutFileInfo outPutFileInfo) {
        this.outPutFileInfo = outPutFileInfo;
    }

    public List<String> getFileList() {
        return fileList;
    }

    public void setFileList(List<String> fileList) {
        this.fileList = fileList;
    }
}
