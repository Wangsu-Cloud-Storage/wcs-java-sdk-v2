package com.wos.services;

import com.wos.services.exception.WosException;
import com.wos.services.model.*;
import com.wos.services.model.RestoreObjectRequest.RestoreObjectStatus;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * Basic WOS interface
 */
public interface IWosClient {

    /**
     *
     * Refresh the temporary access key.
     *
     * @param accessKey
     *            AK in the temporary access key
     * @param secretKey
     *            SK in the temporary access key
     * @param securityToken
     *            Security token
     *
     */
    void refresh(String accessKey, String secretKey, String securityToken);

    /**
     * Generate parameters for browser-based authorized access.
     *
     * @param request
     *            Request parameters for V4 browser-based authorized access
     * @return Response to the V4 browser-based authorized access
     * @throws WosException
     *             WOS SDK self-defined exception, thrown when the interface
     *             fails to be called or access to WOS fails
     */
    PostSignatureResponse createPostSignature(PostSignatureRequest request) throws WosException;

    /**
     * Obtain the bucket list.
     *
     * @return Bucket list
     * @throws WosException
     *             WOS SDK self-defined exception, thrown when the interface fails to be called or access to WOS fails
     */
    List<WosBucket> listBuckets() throws WosException;

    /**
     * List objects in the bucket.
     *
     * @param request
     *            Request parameters for listing objects in a bucket
     * @return Response to the request for listing objects in the bucket
     * @throws WosException
     *             WOS SDK self-defined exception, thrown when the interface
     *             fails to be called or access to WOS fails
     */
    ObjectListing listObjects(ListObjectsRequest request) throws WosException;

    /**
     * List objects in the bucket.
     *
     * @param bucketName
     *            Bucket name
     * @return Response to the request for listing objects in the bucket
     * @throws WosException
     *             WOS SDK self-defined exception, thrown when the interface
     *             fails to be called or access to WOS fails
     */
    ObjectListing listObjects(String bucketName) throws WosException;


    /**
     * List objects in the bucket.
     *
     * @param request
     *            Request parameters for listing objects in a bucket
     * @return Response to the request for listing objects in the bucket
     * @throws WosException
     *             WOS SDK self-defined exception, thrown when the interface
     *             fails to be called or access to WOS fails
     */
    ObjectV2Listing listObjectsV2(ListObjectsV2Request request) throws WosException;

    /**
     * List objects in the bucket.
     *
     * @param bucketName
     *            Bucket name
     * @return Response to the request for listing objects in the bucket
     * @throws WosException
     *             WOS SDK self-defined exception, thrown when the interface
     *             fails to be called or access to WOS fails
     */
    ObjectV2Listing listObjectsV2(String bucketName) throws WosException;

    /**
     * Identify whether a bucket exists.
     *
     * @param bucketName
     *            Bucket name
     * @return Identifier indicating whether the bucket exists
     * @throws WosException
     *             WOS SDK self-defined exception, thrown when the interface
     *             fails to be called or access to WOS fails
     */
    boolean headBucket(String bucketName) throws WosException;

    /**
     * Identify whether a bucket exists.
     * 
     * @param request
     *            Request parameters
     * @return Identifier indicating whether the bucket exists
     * @throws WosException
     *             WOS SDK self-defined exception, thrown when the interface fails to be called or access to WOS fails
     *
     */
    boolean headBucket(BaseBucketRequest request) throws WosException;

    /**
     * Obtain a bucket ACL.
     *
     * @param bucketName
     *            Bucket name
     * @return Bucket ACL
     * @throws WosException
     *             WOS SDK self-defined exception, thrown when the interface
     *             fails to be called or access to WOS fails
     */
    AccessControlList getBucketAcl(String bucketName) throws WosException;

    /**
     * Obtain a bucket ACL.
     * 
     * @param request
     *            Request parameters for obtaining the bucket ACL
     * @return Response to a request for obtaining the bucket ACL
     * @throws WosException
     *             WOS SDK self-defined exception, thrown when the interface fails to be called or access to WOS fails
     *
     */
    AccessControlList getBucketAcl(BaseBucketRequest request) throws WosException;

    /**
     * Obtain the bucket lifecycle rules.
     *
     * @param bucketName
     *            Bucket name
     * @return Bucket lifecycle rules
     * @throws WosException
     *             WOS SDK self-defined exception, thrown when the interface
     *             fails to be called or access to WOS fails
     */
    LifecycleConfiguration getBucketLifecycle(String bucketName) throws WosException;

    /**
     * Obtain the bucket lifecycle rules.
     * 
     * @param request
     *            Request parameters
     * @return Bucket lifecycle rules
     * @throws WosException
     *             WOS SDK self-defined exception, thrown when the interface fails to be called or access to WOS fails
     *
     */
    LifecycleConfiguration getBucketLifecycle(BaseBucketRequest request) throws WosException;

    /**
     * Set the bucket lifecycle rules.
     *
     * @param bucketName
     *            Bucket name
     * @param lifecycleConfig
     *            Bucket lifecycle rules
     * @return Common response headers
     * @throws WosException
     *             WOS SDK self-defined exception, thrown when the interface
     *             fails to be called or access to WOS fails
     */
    HeaderResponse setBucketLifecycle(String bucketName, LifecycleConfiguration lifecycleConfig) throws WosException;

    /**
     * Configure lifecycle rules for a bucket.
     * 
     * @param request
     *            Request parameters
     * @return Common response headers
     * @throws WosException
     *             WOS SDK self-defined exception, thrown when the interface fails to be called or access to WOS fails
     *
     */
    HeaderResponse setBucketLifecycle(SetBucketLifecycleRequest request) throws WosException;

    /**
     * Delete the bucket lifecycle rules from a bucket.
     *
     * @param bucketName
     *            Bucket name
     * @return Common response headers
     * @throws WosException
     *             WOS SDK self-defined exception, thrown when the interface
     *             fails to be called or access to WOS fails
     */
    HeaderResponse deleteBucketLifecycle(String bucketName) throws WosException;

    /**
     * Delete the bucket lifecycle rules from a bucket.
     * 
     * @param request
     *            Request parameters
     * @return Common response headers
     * @throws WosException
     *             WOS SDK self-defined exception, thrown when the interface fails to be called or access to WOS fails
     *
     */
    HeaderResponse deleteBucketLifecycle(BaseBucketRequest request) throws WosException;

    /**
     * Upload an object.
     *
     * @param bucketName
     *            Bucket name
     * @param objectKey
     *            Object name
     * @param input
     *            Data stream to be uploaded
     * @param metadata
     *            Object properties
     * @return Response to an object upload request
     * @throws WosException
     *             WOS SDK self-defined exception, thrown when the interface
     *             fails to be called or access to WOS fails
     */
    PutObjectResult putObject(String bucketName, String objectKey, InputStream input, ObjectMetadata metadata)
            throws WosException;

    /**
     * Upload an object.
     *
     * @param bucketName
     *            Bucket name
     * @param objectKey
     *            Object name
     * @param input
     *            Data stream to be uploaded
     * @return Response to an object upload request
     * @throws WosException
     *             WOS SDK self-defined exception, thrown when the interface
     *             fails to be called or access to WOS fails
     */
    PutObjectResult putObject(String bucketName, String objectKey, InputStream input) throws WosException;

    /**
     * Upload an object.
     *
     * @param request
     *            Parameters in an object upload request
     * @return Response to an object upload request
     * @throws WosException
     *             WOS SDK self-defined exception, thrown when the interface
     *             fails to be called or access to WOS fails
     */
    PutObjectResult putObject(PutObjectRequest request) throws WosException;

    /**
     * Upload an object.
     *
     * @param bucketName
     *            Bucket name
     * @param objectKey
     *            Object name
     * @param file
     *            File to be uploaded
     * @return Response to an object upload request
     * @throws WosException
     *             WOS SDK self-defined exception, thrown when the interface
     *             fails to be called or access to WOS fails
     */
    PutObjectResult putObject(String bucketName, String objectKey, File file) throws WosException;

    /**
     * Upload an object.
     *
     * @param bucketName
     *            Bucket name
     * @param objectKey
     *            Object name
     * @param file
     *            File to be uploaded
     * @param metadata
     *            Object properties
     * @return Response to an object upload request
     * @throws WosException
     *             WOS SDK self-defined exception, thrown when the interface
     *             fails to be called or access to WOS fails
     */
    PutObjectResult putObject(String bucketName, String objectKey, File file, ObjectMetadata metadata)
            throws WosException;


    /**
     * Check whether an object exists.
     * 
     * @param buckeName
     *            Bucket name
     * @param objectKey
     *            Object name
     * @return Whether an object exists
     * @throws WosException
     *             WOS SDK self-defined exception, thrown when the interface fails to be called or access to WOS fails
     */
    boolean doesObjectExist(String buckeName, String objectKey) throws WosException;

    /**
     * Check whether an object exists.
     * 
     * @param request
     *            Request parameters for obtaining the properties of an object
     * @return Whether an object exists
     * @throws WosException
     *             WOS SDK self-defined exception, thrown when the interface fails to be called or access to WOS fails
     */
    boolean doesObjectExist(GetObjectMetadataRequest request) throws WosException;


    /**
     * Download an object.
     *
     * @param request
     *            Parameters in an object download request
     * @return Object information, including the object data stream
     * @throws WosException
     *             WOS SDK self-defined exception, thrown when the interface
     *             fails to be called or access to WOS fails
     */
    WosObject getObject(GetObjectRequest request) throws WosException;


    /**
     * Download an object.
     *
     * @param bucketName
     *            Bucket name
     * @param objectKey
     *            Object name
     * @return Object information, including the object data stream
     * @throws WosException
     *             WOS SDK self-defined exception, thrown when the interface
     *             fails to be called or access to WOS fails
     */
    WosObject getObject(String bucketName, String objectKey) throws WosException;

    /**
     * Obtain object properties.
     *
     * @param request
     *            Parameters in a request for obtaining the properties of an
     *            object
     * @return Object properties
     * @throws WosException
     *             WOS SDK self-defined exception, thrown when the interface
     *             fails to be called or access to WOS fails
     */
    ObjectMetadata getObjectMetadata(GetObjectMetadataRequest request) throws WosException;


    /**
     * Obtain object properties.
     *
     * @param bucketName
     *            Bucket name
     * @param objectKey
     *            Object name
     * @return Object properties
     * @throws WosException
     *             WOS SDK self-defined exception, thrown when the interface
     *             fails to be called or access to WOS fails
     */
    ObjectMetadata getObjectMetadata(String bucketName, String objectKey) throws WosException;

    /**
     * Set object properties.
     * 
     * @param request
     *            Parameters in the request for obtaining object properties
     * @return Object properties
     * @throws WosException
     *             WOS SDK self-defined exception, thrown when the interface
     *             fails to be called or access to WOS fails
     */
    ObjectMetadata setObjectMetadata(SetObjectMetadataRequest request) throws WosException;

    /**
     * Restore an Archive object.
     *
     * @param request
     *            Parameters in a request for restoring an Archive object
     * @return Status of the to-be-restored Archive object
     * @throws WosException
     *             WOS SDK self-defined exception, thrown when the interface
     *             fails to be called or access to WOS fails
     *
     */
    RestoreObjectStatus restoreObject(RestoreObjectRequest request) throws WosException;

    /**
     * Restore an Archive object.
     *
     * @param request
     *            Request parameters for restoring an Archive object
     * @return Result of restoring the Archive object
     * @throws WosException
     *             WOS SDK self-defined exception, thrown when the interface
     *             fails to be called or access to WOS fails
     *
     */
    RestoreObjectResult restoreObjectV2(RestoreObjectRequest request) throws WosException;

    /**
     * Delete an object.
     *
     * @param bucketName
     *            Bucket name
     * @param objectKey
     *            Object name
     * @return Common response headers
     * @throws WosException
     *             WOS SDK self-defined exception, thrown when the interface
     *             fails to be called or access to WOS fails
     */
    DeleteObjectResult deleteObject(String bucketName, String objectKey) throws WosException;

    /**
     * Delete an object.
     * 
     * @param request
     *            Request parameters for deleting an object
     * @return Common response headers
     * @throws WosException
     *             WOS SDK self-defined exception, thrown when the interface fails to be called or access to WOS fails
     *
     */
    DeleteObjectResult deleteObject(DeleteObjectRequest request) throws WosException;

    /**
     * Delete objects in a batch.
     *
     * @param deleteObjectsRequest
     *            Parameters in an object batch deletion request
     * @return Result of the object batch deletion request
     * @throws WosException
     *             WOS SDK self-defined exception, thrown when the interface
     *             fails to be called or access to WOS fails
     */
    DeleteObjectsResult deleteObjects(DeleteObjectsRequest deleteObjectsRequest) throws WosException;


    /**
     * Obtain an object ACL.
     *
     * @param bucketName
     *            Bucket name
     * @param objectKey
     *            Object name
     * @return Object ACL
     * @throws WosException
     *             WOS SDK self-defined exception, thrown when the interface
     *             fails to be called or access to WOS fails
     */
    AccessControlList getObjectAcl(String bucketName, String objectKey) throws WosException;

    /**
     * Obtain an object ACL.
     * 
     * @param request
     *            Request parameters for obtaining an object ACL
     * @return Object ACL
     * @throws WosException
     *             WOS SDK self-defined exception, thrown when the interface fails to be called or access to WOS fails
     *
     */
    AccessControlList getObjectAcl(GetObjectAclRequest request) throws WosException;

    /**
     * Obtain an String Avinfo.
     *
     * @param bucketName
     *            Bucket name
     * @param objectKey
     *            Object name
     * @return String Avinfo
     * @throws WosException
     *             WOS SDK self-defined exception, thrown when the interface
     *             fails to be called or access to WOS fails
     */
    String getObjectAvinfo(String bucketName, String objectKey) throws WosException;

    /**
     * Obtain an String Avinfo.
     *
     * @param request
     *            Request parameters for obtaining an String Avinfo
     * @return String Avinfo
     * @throws WosException
     *             WOS SDK self-defined exception, thrown when the interface fails to be called or access to WOS fails
     *
     */
    String getObjectAvinfo(GetObjectAvinfoRequest request) throws WosException;

    /**
     * Copy an object.
     *
     * @param request
     *            Parameters in a request for copying an object
     * @return Result of the object copy
     * @throws WosException
     *             WOS SDK self-defined exception, thrown when the interface
     *             fails to be called or access to WOS fails
     */
    CopyObjectResult copyObject(CopyObjectRequest request) throws WosException;

    /**
     * Copy an object.
     *
     * @param sourceBucketName
     *            Source bucket name
     * @param sourceObjectKey
     *            Source object name
     * @param destBucketName
     *            Destination bucket name
     * @param destObjectKey
     *            Destination object name
     * @return Result of the object copy
     * @throws WosException
     *             WOS SDK self-defined exception, thrown when the interface
     *             fails to be called or access to WOS fails
     */
    CopyObjectResult copyObject(String sourceBucketName, String sourceObjectKey, String destBucketName,
                                String destObjectKey) throws WosException;

    /**
     * Initialize a multipart upload.
     *
     * @param request
     *            Parameters in a request for initializing a multipart upload
     * @return Result of the multipart upload
     * @throws WosException
     *             WOS SDK self-defined exception, thrown when the interface
     *             fails to be called or access to WOS fails
     */
    InitiateMultipartUploadResult initiateMultipartUpload(InitiateMultipartUploadRequest request) throws WosException;

    /**
     * Abort a multipart upload.
     *
     * @param request
     *            Parameters in a request for aborting a multipart upload
     * @return Common response headers
     * @throws WosException
     *             WOS SDK self-defined exception, thrown when the interface
     *             fails to be called or access to WOS fails
     */
    HeaderResponse abortMultipartUpload(AbortMultipartUploadRequest request) throws WosException;

    /**
     * Upload a part.
     *
     * @param bucketName
     *            Bucket name
     * @param objectKey
     *            Object name
     * @param uploadId
     *            Multipart upload ID
     * @param partNumber
     *            Part number
     * @param input
     *            Data stream to be uploaded
     * @return Response to a part upload request
     * @throws WosException
     *             WOS SDK self-defined exception, thrown when the interface
     *             fails to be called or access to WOS fails
     */
    UploadPartResult uploadPart(String bucketName, String objectKey, String uploadId, int partNumber, InputStream input)
            throws WosException;

    /**
     * Upload a part.
     *
     * @param bucketName
     *            Bucket name
     * @param objectKey
     *            Object name
     * @param uploadId
     *            Multipart upload ID
     * @param partNumber
     *            Part number
     * @param file
     *            File to be uploaded
     * @return Response to a part upload request
     * @throws WosException
     *             WOS SDK self-defined exception, thrown when the interface
     *             fails to be called or access to WOS fails
     */
    UploadPartResult uploadPart(String bucketName, String objectKey, String uploadId, int partNumber, File file)
            throws WosException;

    /**
     * Upload a part.
     *
     * @param request
     *            Parameters in a part upload request
     * @return Response to a part upload request
     * @throws WosException
     *             WOS SDK self-defined exception, thrown when the interface
     *             fails to be called or access to WOS fails
     */
    UploadPartResult uploadPart(UploadPartRequest request) throws WosException;

    /**
     * Copy a part.
     *
     * @param request
     *            Parameters in the request for copying a part
     * @return Response to a part copy request
     * @throws WosException
     *             WOS SDK self-defined exception, thrown when the interface
     *             fails to be called or access to WOS fails
     */
    CopyPartResult copyPart(CopyPartRequest request) throws WosException;

    /**
     * Combine parts.
     *
     * @param request
     *            Parameters in a request for combining parts
     * @return Result of part combination
     * @throws WosException
     *             WOS SDK self-defined exception, thrown when the interface
     *             fails to be called or access to WOS fails
     */
    CompleteMultipartUploadResult completeMultipartUpload(CompleteMultipartUploadRequest request) throws WosException;

    /**
     * List uploaded parts.
     *
     * @param request
     *            Parameters in a request for listing uploaded parts
     * @return Response to a request for listing uploaded parts
     * @throws WosException
     *             WOS SDK self-defined exception, thrown when the interface
     *             fails to be called or access to WOS fails
     */
    ListPartsResult listParts(ListPartsRequest request) throws WosException;

    /**
     * List incomplete multipart uploads.
     *
     * @param request
     *            Parameters in a request for listing multipart uploads
     * @return List of multipart uploads
     * @throws WosException
     *             WOS SDK self-defined exception, thrown when the interface
     *             fails to be called or access to WOS fails
     */
    MultipartUploadListing listMultipartUploads(ListMultipartUploadsRequest request) throws WosException;

    /**
     * Close WosClient and release connection resources.
     * 
     * @throws IOException
     *             WosClient close exception
     */
    void close() throws IOException;

}
