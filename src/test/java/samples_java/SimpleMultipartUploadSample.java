package samples_java;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.UUID;

import com.wos.services.WosClient;
import com.wos.services.WosConfiguration;
import com.wos.services.exception.WosException;
import com.wos.services.model.*;


/**
 * This sample demonstrates how to upload multiparts to WOS
 * using the WOS SDK for Java.
 */
public class SimpleMultipartUploadSample {
    private static final String endPoint = "https://your-endpoint";

    private static final String ak = "*** Provide your Access Key ***";

    private static final String sk = "*** Provide your Secret Key ***";

    private static WosClient wosClient;

    private static String bucketName = "my-wos-bucket-demo";

    private static String regionName = "my-wos-region-demo";

    private static String objectKey = "my-wos-object-key-demo";

    public static void main(String[] args) throws IOException {
        WosConfiguration config = new WosConfiguration();
        config.setSocketTimeout(300000);
        config.setConnectionTimeout(100000);
        config.setEndPoint(endPoint);
        config.setRegionName(regionName);
        try {
            /*
             * Constructs a wos client instance with your account for accessing WOS
             */
            wosClient = new WosClient(ak, sk, config);

            /*
             * Step 1: initiate multipart upload
             */
            System.out.println("Step 1: initiate multipart upload \n");
            InitiateMultipartUploadRequest request = new InitiateMultipartUploadRequest();
            request.setBucketName(bucketName);
            request.setObjectKey(objectKey);
            InitiateMultipartUploadResult result = wosClient.initiateMultipartUpload(request);
            System.out.println(result);

            /*
             * Step 2: upload a part
             */
            System.out.println("Step 2: upload part \n");
            UploadPartResult uploadPartResult = wosClient.uploadPart(bucketName, objectKey, result.getUploadId(), 1, new FileInputStream(createSampleFile()));

            ListPartsRequest listPartsRequest = new ListPartsRequest();
            listPartsRequest.setBucketName(bucketName);
            listPartsRequest.setKey(objectKey);
            listPartsRequest.setUploadId(result.getUploadId());
            ListPartsResult listPartsResult = wosClient.listParts(listPartsRequest);
            System.out.println(listPartsResult);

            AbortMultipartUploadRequest abortMultipartUploadRequest = new AbortMultipartUploadRequest();
            abortMultipartUploadRequest.setBucketName(bucketName);
            abortMultipartUploadRequest.setObjectKey(objectKey);
            abortMultipartUploadRequest.setUploadId(result.getUploadId());
            wosClient.abortMultipartUpload(abortMultipartUploadRequest);

            /*
             * Step 3: complete multipart upload
             */
            System.out.println("Step 3: complete multipart upload \n");
            CompleteMultipartUploadRequest completeMultipartUploadRequest = new CompleteMultipartUploadRequest();
            completeMultipartUploadRequest.setBucketName(bucketName);
            completeMultipartUploadRequest.setObjectKey(objectKey);
            completeMultipartUploadRequest.setUploadId(result.getUploadId());
            PartEtag partEtag = new PartEtag();
            partEtag.setPartNumber(uploadPartResult.getPartNumber());
            partEtag.seteTag(uploadPartResult.getEtag());
            completeMultipartUploadRequest.getPartEtag().add(partEtag);
            wosClient.completeMultipartUpload(completeMultipartUploadRequest);
        } catch (WosException e) {
            System.out.println("Response Code: " + e.getResponseCode());
            System.out.println("Error Message: " + e.getErrorMessage());
            System.out.println("Error Code:       " + e.getErrorCode());
            System.out.println("Request ID:      " + e.getErrorRequestId());
            System.out.println("Host ID:           " + e.getErrorHostId());
        } finally {
            if (wosClient != null) {
                try {
                    /*
                     * Close wos client
                     */
                    wosClient.close();
                } catch (IOException e) {
                }
            }
        }

    }

    private static File createSampleFile()
            throws IOException {
        File file = File.createTempFile("wos-java-sdk-", ".txt");
        file.deleteOnExit();

        Writer writer = new OutputStreamWriter(new FileOutputStream(file));
        for (int i = 0; i < 1; i++) {
            writer.write(UUID.randomUUID() + "\n");
        }
        writer.flush();
        writer.close();

        return file;
    }
}
