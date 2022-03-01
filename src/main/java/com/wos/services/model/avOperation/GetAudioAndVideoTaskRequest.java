package com.wos.services.model.avOperation;

public class GetAudioAndVideoTaskRequest {
    private String bucketName;

    private String persistentId;

    private AvOperationTypeEnum operationType;

    public GetAudioAndVideoTaskRequest(String bucketName, String persistentId, AvOperationTypeEnum operationType) {
        this.bucketName = bucketName;
        this.persistentId = persistentId;
        this.operationType = operationType;
    }


    public String getBucketName() {
        return bucketName;
    }

    public void setBucketName(String bucketName) {
        this.bucketName = bucketName;
    }

    public String getPersistentId() {
        return persistentId;
    }

    public void setPersistentId(String persistentId) {
        this.persistentId = persistentId;
    }

    public AvOperationTypeEnum getOperationType() {
        return operationType;
    }

    public void setOperationType(AvOperationTypeEnum operationType) {
        this.operationType = operationType;
    }
}
