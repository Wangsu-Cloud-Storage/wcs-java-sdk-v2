package com.wos.services.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Response to an object batch deletion request
 */
public class DeleteObjectsResult extends HeaderResponse {
    private List<DeleteObjectResult> deletedObjectResults;

    private List<ErrorResult> errorResults;

    public DeleteObjectsResult() {

    }

    public DeleteObjectsResult(List<DeleteObjectResult> deletedObjectResults, List<ErrorResult> errorResults) {
        this.deletedObjectResults = deletedObjectResults;
        this.errorResults = errorResults;
    }

    /**
     * Obtain the list of objects that have been deleted successfully.
     * 
     * @return List of successfully deleted objects
     */
    public List<DeleteObjectResult> getDeletedObjectResults() {
        if (this.deletedObjectResults == null) {
            this.deletedObjectResults = new ArrayList<DeleteObjectResult>();
        }
        return deletedObjectResults;
    }

    /**
     * Obtain the list of objects failed to be deleted.
     * 
     * @return List of objects failed to be deleted
     */
    public List<ErrorResult> getErrorResults() {
        if (this.errorResults == null) {
            this.errorResults = new ArrayList<ErrorResult>();
        }
        return errorResults;
    }

    /**
     * Results returned if the deletion succeeds
     */
    public static class DeleteObjectResult {
        private String objectKey;

        private boolean deleteMarker;

        public DeleteObjectResult(String objectKey, boolean deleteMarker) {
            super();
            this.objectKey = objectKey;
            this.deleteMarker = deleteMarker;
        }

        /**
         * Obtain the object name.
         * 
         * @return Object name
         */
        public String getObjectKey() {
            return objectKey;
        }

        /**
         * Check whether the deleted object is a delete marker
         * 
         * @return Identifier specifying whether the object is a delete marker
         */
        public boolean isDeleteMarker() {
            return deleteMarker;
        }

        @Override
        public String toString() {
            return "DeleteObjectResult [objectKey=" + objectKey + ", deleteMarker="
                    + deleteMarker + "]";
        }

    }

    /**
     * Results returned if the deletion fails
     */
    public static class ErrorResult {
        private String objectKey;

        private String errorCode;

        private String message;

        /**
         * Constructor
         *
         * @param objectKey
         *            Name of the object that fails to be deleted
         * @param errorCode
         *            Error code returned after a deletion failure
         * @param message
         *            Error information returned after a deletion failure
         */
        public ErrorResult(String objectKey, String errorCode, String message) {
            this.objectKey = objectKey;
            this.errorCode = errorCode;
            this.message = message;
        }

        /**
         * Obtain the name of the object that fails to be deleted.
         * 
         * @return Name of the object that fails to be deleted
         */
        public String getObjectKey() {
            return objectKey;
        }

        /**
         * Error code returned after a deletion failure
         * 
         * @return Error code returned after a deletion failure
         */
        public String getErrorCode() {
            return errorCode;
        }

        /**
         * Obtain the error description returned after a deletion failure.
         * 
         * @return Error information returned after a deletion failure
         */
        public String getMessage() {
            return message;
        }

        @Override
        public String toString() {
            return "ErrorResult [objectKey=" + objectKey + ", errorCode=" + errorCode
                    + ", message=" + message + "]";
        }

    }

    @Override
    public String toString() {
        return "DeleteObjectsResult [deletedObjectResults=" + deletedObjectResults + ", errorResults=" + errorResults
                + "]";
    }

}
