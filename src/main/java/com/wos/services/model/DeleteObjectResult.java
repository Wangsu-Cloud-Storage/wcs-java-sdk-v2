package com.wos.services.model;

/**
 * Response to an object deletion request
 */
public class DeleteObjectResult extends HeaderResponse {
    private boolean deleteMarker;

    private String objectKey;


    public DeleteObjectResult(boolean deleteMarker, String objectKey) {
        this.deleteMarker = deleteMarker;
        this.objectKey = objectKey;
    }

    /**
     * Check whether a versioning object has been deleted.
     * 
     * @return Identifier indicating whether the versioning object has been
     *         deleted
     */
    public boolean isDeleteMarker() {
        return deleteMarker;
    }


    /**
     * Obtain the name of the deleted object.
     * 
     * @return Object name
     */
    public String getObjectKey() {
        return objectKey;
    }

    @Override
    public String toString() {
        return "DeleteObjectResult [deleteMarker=" + deleteMarker + ", objectKey=" + objectKey + "]";
    }
}
