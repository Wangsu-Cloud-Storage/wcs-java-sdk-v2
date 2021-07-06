package samples_java;

import com.wos.services.WosClient;
import com.wos.services.WosConfiguration;
import com.wos.services.exception.WosException;
import com.wos.services.model.*;

import java.io.*;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * This sample demonstrates how to multipart upload an object concurrently by copy mode 
 * to WOS using the WOS SDK for Java.
 */
public class ConcurrentCopyPartSample
{
    private static final String endPoint = "https://your-endpoint";

    private static final String ak = "*** Provide your Access Key ***";

    private static final String sk = "*** Provide your Secret Key ***";

    private static WosClient wosClient;

    private static String bucketName = "my-wos-bucket-demo";

    private static String regionName = "my-wos-region-demo";

    private static String sourceBucketName = bucketName;

    private static String sourceObjectKey = "my-wos-object-key-demo";

    private static String objectKey = sourceObjectKey + "-back";
    
    private static ExecutorService executorService = Executors.newFixedThreadPool(5);
    
    private static List<PartEtag> partETags = Collections.synchronizedList(new ArrayList<PartEtag>());
    
    public static void main(String[] args)
        throws IOException
    {
        WosConfiguration config = new WosConfiguration();
        config.setSocketTimeout(30000);
        config.setConnectionTimeout(10000);
        config.setEndPoint(endPoint);
        try
        {
            /*
             * Constructs a wos client instance with your account for accessing WOS
             */
            wosClient = new WosClient(ak, sk, config, regionName);
            
            /*
             * Upload an object to your source bucket
             */
            System.out.println("Uploading a new object to WOS from a file\n");
            wosClient.putObject(new PutObjectRequest(sourceBucketName, sourceObjectKey, createSampleFile()));
            
            /*
             * Claim a upload id firstly
             */
            String uploadId = claimUploadId();
            System.out.println("Claiming a new upload id " + uploadId + "\n");
            
            long partSize = 5 * 1024 * 1024l;// 5MB
            ObjectMetadata metadata = wosClient.getObjectMetadata(sourceBucketName, sourceObjectKey);
            
            long objectSize = metadata.getContentLength();
            
            long partCount = objectSize % partSize == 0 ? objectSize / partSize : objectSize / partSize + 1;
            
            if (partCount > 10000)
            {
                throw new RuntimeException("Total parts count should not exceed 10000");
            }
            else
            {
                System.out.println("Total parts count " + partCount + "\n");
            }
            
            /*
             * Upload multiparts by copy mode
             */
            System.out.println("Begin to upload multiparts to WOS by copy mode \n");
            for (int i = 0; i < partCount; i++) {
                
                long rangeStart = i * partSize;
                long rangeEnd = (i + 1 == partCount) ? objectSize - 1 : rangeStart + partSize - 1;
                executorService.execute(new PartCopier(sourceBucketName, sourceObjectKey, rangeStart, rangeEnd, i + 1, uploadId));
            }
            
            /*
             * Waiting for all parts finished
             */
            executorService.shutdown();
            while (!executorService.isTerminated())
            {
                try
                {
                    executorService.awaitTermination(3, TimeUnit.SECONDS);
                }
                catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
            }
            
            /*
             * Verify whether all parts are finished
             */
            if (partETags.size() != partCount)
            {
                throw new IllegalStateException("Upload multiparts fail due to some parts are not finished yet");
            }
            else
            {
                System.out.println("Succeed to complete multiparts into an object named " + objectKey + "\n");
            }
            
            /*
             * View all parts uploaded recently
             */
            listAllParts(uploadId);
            
            /*
             * Complete to upload multiparts
             */
            completeMultipartUpload(uploadId);
            
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
    
    private static class PartCopier implements Runnable
    {
        
        private String sourceBucketName;
        
        private String sourceObjectKey;
        
        private long rangeStart;
        
        private long rangeEnd;
        
        private int partNumber;
        
        private String uploadId;
        
        public PartCopier(String sourceBucketName, String sourceObjectKey, long rangeStart, long rangeEnd, int partNumber, String uploadId)
        {
            this.sourceBucketName = sourceBucketName;
            this.sourceObjectKey = sourceObjectKey;
            this.rangeStart = rangeStart;
            this.rangeEnd = rangeEnd;
            this.partNumber = partNumber;
            this.uploadId = uploadId;
        }
        
        @Override
        public void run()
        {
            try
            {
                CopyPartRequest request = new CopyPartRequest();
                request.setUploadId(this.uploadId);
                request.setSourceBucketName(this.sourceBucketName);
                request.setSourceObjectKey(this.sourceObjectKey);
                request.setDestinationBucketName(bucketName);
                request.setDestinationObjectKey(objectKey);
                request.setByteRangeStart(this.rangeStart);
                request.setByteRangeEnd(this.rangeEnd);
                request.setPartNumber(this.partNumber);
                CopyPartResult result = wosClient.copyPart(request);
                System.out.println("Part#" + this.partNumber + " done\n");
                partETags.add(new PartEtag(result.getEtag(), result.getPartNumber()));
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }
    
    private static String claimUploadId()
            throws WosException
    {
        InitiateMultipartUploadRequest request = new InitiateMultipartUploadRequest(bucketName, objectKey);
        InitiateMultipartUploadResult result = wosClient.initiateMultipartUpload(request);
        return result.getUploadId();
    }
    
    private static File createSampleFile() throws IOException
    {
        File file = File.createTempFile("wos-java-sdk-", ".txt");
        file.deleteOnExit();
        
        Writer writer = new OutputStreamWriter(new FileOutputStream(file));
        for (int i = 0; i < 10; i++)
        {
            writer.write(UUID.randomUUID() + "\n");
            writer.write(UUID.randomUUID() + "\n");
        }
        writer.flush();
        writer.close();
        
        return file;
    }
    
    private static void completeMultipartUpload(String uploadId)
            throws WosException
    {
        // Make part numbers in ascending order
        Collections.sort(partETags, new Comparator<PartEtag>()
        {
            
            @Override
            public int compare(PartEtag o1, PartEtag o2)
            {
                return o1.getPartNumber() - o2.getPartNumber();
            }
        });
        
        System.out.println("Completing to upload multiparts\n");
        CompleteMultipartUploadRequest completeMultipartUploadRequest =
            new CompleteMultipartUploadRequest(bucketName, objectKey, uploadId, partETags);
        wosClient.completeMultipartUpload(completeMultipartUploadRequest);
    }
    
    private static void listAllParts(String uploadId)
            throws WosException
    {
        System.out.println("Listing all parts......");
        ListPartsRequest listPartsRequest = new ListPartsRequest(bucketName, objectKey, uploadId);
        ListPartsResult partListing = wosClient.listParts(listPartsRequest);
        
        for (Multipart part : partListing.getMultipartList())
        {
            System.out.println("\tPart#" + part.getPartNumber() + ", ETag=" + part.getEtag());
        }
        System.out.println();
    }
}
