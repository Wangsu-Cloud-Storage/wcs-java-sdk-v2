package com.wos.services.model;

/**
 * Special operator, which indicates the sub-resource to be operated
 */
public enum SpecialParamEnum {
    
    /**
     * Obtain the bucket location information.
     */
    LOCATION("location"),
    /**
     * Obtain bucket storage information.
     */
    STORAGEINFO("storageinfo"),
    /**
     * Obtain or set the bucket quota.
     */
    QUOTA("quota"),
    /**
     * Obtain or set the ACL of the bucket (object).
     */
    ACL("acl"),
    /**
     * Obtain or set the ACL of the bucket (object).
     */
    AVINFO("avinfo"),
    /**
     * Obtain the logging settings of or configure logging for a bucket.
     */
    LOGGING("logging"),
    /**
     * Obtain, set, or delete bucket policies.
     */
    POLICY("policy"),
    /**
     * Obtain, set, or delete bucket lifecycle rules.
     */
    LIFECYCLE("lifecycle"),
    /**
     * Obtain or delete website hosting settings of or configure website hosting for a bucket.
     */
    WEBSITE("website"),
    /**
     * Obtain or set the versioning status of a bucket.
     */
    VERSIONING("versioning"),
    /**
     * Obtain or set requester payment status of a bucket.
     */
    REQUEST_PAYMENT("requestPayment"),
    /**
     * Obtain or set bucket storage policies.
     */

    STORAGEPOLICY("storagePolicy"),
    /**
     * Obtain or set the bucket storage class. 
     */
    STORAGECLASS("storageClass"),
    /**
     * Obtain, set, or delete the CORS rules of a bucket.
     */
    CORS("cors"),
    /**
     * List or initialize multipart uploads.
     */
    UPLOADS("uploads"),
    /**
     * List versioning objects in a bucket.
     */
    VERSIONS("versions"),
    /**
     * Delete objects in a batch.
     */
    DELETE("delete"),
    
    /**
     * Restore an Archive object.
     */
    RESTORE("restore"),
    
    /**
     * Obtain, set, or delete bucket tags.
     */
    TAGGING("tagging"),
    
    /**
     * Configure bucket notification or obtain bucket notification configuration.
     */
    NOTIFICATION("notification"),
    /**
     * Set, obtain, or delete the cross-region replication configuration of a bucket.
     */
    REPLICATION("replication"),
    
    /**
     * Perform an appendable upload.
     */
    APPEND("append"),
    
    /**
     * Rename a file or folder.
     */
    RENAME("rename"),
    
    /**
     * Truncate a file.
     */
    TRUNCATE("truncate"),
    
    /**
     * Modify a file.
     */
    MODIFY("modify"),
    
    /**
     * Configure the file gateway feature.
     */
    FILEINTERFACE("fileinterface"),
    
    /**
     * Set or delete object properties.
     */
    METADATA("metadata"),
    
    /**
     * Set, obtain, or delete the encryption configuration of a bucket.
     */
    ENCRYPTION("encryption"),

    /**
     * obtain folder contentSummary
     */
    LISTCONTENTSUMMARY("listcontentsummary"),

    /**
     * Set, obtain, or delete the direct reading policy for Archive objects in a bucket.
     */
    DIRECTCOLDACCESS("directcoldaccess"); 
    
    /**
     * Specify the corresponding code in the database and the external code.
     */
    private String stringCode;
    
    private SpecialParamEnum(String stringCode) {
        if (stringCode == null) {
            throw new IllegalArgumentException("stringCode is null");
        }
        this.stringCode = stringCode;
    }
    
    public String getStringCode() {
        return this.stringCode.toLowerCase();
    }
    
    public String getOriginalStringCode() {
        return this.stringCode;
    }
    
    public static SpecialParamEnum getValueFromStringCode(String stringCode) {
        if (stringCode == null) {
            throw new IllegalArgumentException("string code is null");
        }
        
        for (SpecialParamEnum installMode : SpecialParamEnum.values()) {
            if (installMode.getStringCode().equals(stringCode.toLowerCase())) {
                return installMode;
            }
        }
        
        throw new IllegalArgumentException("string code is illegal");
    }
}
