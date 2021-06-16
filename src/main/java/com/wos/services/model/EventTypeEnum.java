package com.wos.services.model;

import com.wos.services.internal.utils.ServiceUtils;

/**
 * Event type
 *
 */
public enum EventTypeEnum {

    /**
     * All events for creating objects
     */
    OBJECT_CREATED_ALL,

    /**
     * PUT Object events
     */
    OBJECT_CREATED_PUT,

    /**
     * POST Object events
     */
    OBJECT_CREATED_POST,

    /**
     * Events for copying objects
     */
    OBJECT_CREATED_COPY,

    /**
     * Events for combining parts
     */
    OBJECT_CREATED_COMPLETE_MULTIPART_UPLOAD,

    /**
     * All events for deleting objects
     */
    OBJECT_REMOVED_ALL,

    /**
     * Events for deleting objects by specifying object version IDs
     */
    OBJECT_REMOVED_DELETE,

    /**
     * Events for deleting objects without specifying version IDs after
     * versioning is enabled
     */
    OBJECT_REMOVED_DELETE_MARKER_CREATED;

    public static EventTypeEnum getValueFromCode(String code) {
        if (ServiceUtils.isValid(code)) {
            if (code.indexOf("ObjectCreated:*") >= 0) {
                return OBJECT_CREATED_ALL;
            } else if (code.indexOf("ObjectCreated:Put") >= 0) {
                return OBJECT_CREATED_PUT;
            } else if (code.indexOf("ObjectCreated:Post") >= 0) {
                return OBJECT_CREATED_POST;
            } else if (code.indexOf("ObjectCreated:Copy") >= 0) {
                return OBJECT_CREATED_COPY;
            } else if (code.indexOf("ObjectCreated:CompleteMultipartUpload") >= 0) {
                return OBJECT_CREATED_COMPLETE_MULTIPART_UPLOAD;
            } else if (code.indexOf("ObjectRemoved:*") >= 0) {
                return OBJECT_REMOVED_ALL;
            } else if (code.indexOf("ObjectRemoved:Delete") >= 0) {
                return OBJECT_REMOVED_DELETE;
            } else if (code.indexOf("ObjectRemoved:DeleteMarkerCreated") >= 0) {
                return OBJECT_REMOVED_DELETE_MARKER_CREATED;
            }
        }
        return null;
    }

}
