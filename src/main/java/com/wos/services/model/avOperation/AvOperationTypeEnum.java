package com.wos.services.model.avOperation;

import org.apache.commons.lang3.StringUtils;

public enum AvOperationTypeEnum {

    /**
     * 转码
     * transcode
     */
    Avthumb("Avthumb", "avthumb"),

    /**
     * 截图
     * snapshot
     */
    Vframe("Vframe", "vframe"),

    /**
     * 音视频拼接
     * audio or video concat
     */
    Avconcat("Avconcat", "avconcat"),

    /**
     * 获取专辑图片
     * Get album Cover
     */
    Getapic("Getapic", "getapic"),

    /**
     * 解压缩
     */
    Decompression("Decompression", "decompression");

    private String value;

    private String code;

    AvOperationTypeEnum(String value, String code) {
        this.value = value;
        this.code = code;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public static String getTypeByValuePrefix(String name) {
        if (StringUtils.isEmpty(name)) {
            return null;
        }
        for (AvOperationTypeEnum operationType : values()) {
            if (name.startsWith(operationType.getValue())) {
                return operationType.getValue();
            }
        }
        return null;
    }

}