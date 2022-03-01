package com.wos.services.model.avOperation;

import com.wos.services.model.HeaderResponse;

/**
 * the class of audio and video operation result
 */
public class AudioAndVideoTaskRequestResult extends HeaderResponse {

    /**
     * Id of the process for audio and video operation
     */
    private String persistentId;

    /**
     * operation type
     * @see AvOperationTypeEnum
     */
    private String operationType;

    public AudioAndVideoTaskRequestResult() {
    }

    public AudioAndVideoTaskRequestResult(String  persistentId, String operationType) {
        this.persistentId = persistentId;
        this.operationType = operationType;
    }

    public String  getPersistentId() {
        return persistentId;
    }

    public void setPersistentId(String  persistentId) {
        this.persistentId = persistentId;
    }

    public String getOperationType() {
        return operationType;
    }

    public void setOperationType(String operationType) {
        this.operationType = operationType;
    }
}
