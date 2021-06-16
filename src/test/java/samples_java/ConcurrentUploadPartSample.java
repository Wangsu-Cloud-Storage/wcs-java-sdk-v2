package samples_java;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import com.wos.services.WosClient;
import com.wos.services.WosConfiguration;
import com.wos.services.exception.WosException;
import com.wos.services.model.CompleteMultipartUploadRequest;
import com.wos.services.model.InitiateMultipartUploadRequest;
import com.wos.services.model.InitiateMultipartUploadResult;
import com.wos.services.model.ListPartsRequest;
import com.wos.services.model.ListPartsResult;
import com.wos.services.model.Multipart;
import com.wos.services.model.PartEtag;
import com.wos.services.model.UploadPartRequest;
import com.wos.services.model.UploadPartResult;

/**
 * This sample demonstrates how to multipart upload an object concurrently 
 * from WOS using the WOS SDK for Java.
 */
public class ConcurrentUploadPartSample
{
    private static final String endPoint = "https://your-endpoint";

    private static final String ak = "*** Provide your Access Key ***";

    private static final String sk = "*** Provide your Secret Key ***";

    private static WosClient wosClient;

    private static String bucketName = "my-wos-bucket-demo";

    private static String objectKey = "my-wos-object-key-demo";
    
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
            wosClient = new WosClient(ak, sk, config);
            
            /*
             * Claim a upload id firstly
             */
            String uploadId = claimUploadId();
            System.out.println("Claiming a new upload id " + uploadId + "\n");
            
            long partSize = 5 * 1024 * 1024l;// 5MB
            File sampleFile = createSampleFile();
            long fileLength = sampleFile.length();
            
            long partCount = fileLength % partSize == 0 ? fileLength / partSize : fileLength / partSize + 1;
            
            if (partCount > 10000)
            {
                throw new RuntimeException("Total parts count should not exceed 10000");
            }
            else
            {
                System.out.println("Total parts count " + partCount + "\n");
            }
            
            /*
             * Upload multiparts to your bucket
             */
            System.out.println("Begin to upload multiparts to WOS from a file\n");
            for (int i = 0; i < partCount; i++)
            {
                long offset = i * partSize;
                long currPartSize = (i + 1 == partCount) ? fileLength - offset : partSize;
                executorService.execute(new PartUploader(sampleFile, offset, currPartSize, i + 1, uploadId));
            }
            
            /*
             * Wait for all tasks to finish
             */
            executorService.shutdown();
            while (!executorService.isTerminated())
            {
                try
                {
                    executorService.awaitTermination(5, TimeUnit.SECONDS);
                }
                catch (InterruptedException e)
                {
                    e.printStackTrace();
                }
            }
            
            /*
             * Verify whether all tasks are finished
             */
            if (partETags.size() != partCount)
            {
                throw new IllegalStateException("Some parts are not finished");
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
            
        } catch (WosException e)
        {
            System.out.println("Response Code: " + e.getResponseCode());
            System.out.println("Error Message: " + e.getErrorMessage());
            System.out.println("Error Code:       " + e.getErrorCode());
            System.out.println("Request ID:      " + e.getErrorRequestId());
            System.out.println("Host ID:           " + e.getErrorHostId());
        }
        finally
        {
            if (wosClient != null)
            {
                try
                {
                    /*
                     * Close wos client
                     */
                    wosClient.close();
                }
                catch (IOException e)
                {
                }
            }
        }
        
    }
    
    private static class PartUploader implements Runnable
    {
        
        private File sampleFile;
        
        private long offset;
        
        private long partSize;
        
        private int partNumber;
        
        private String uploadId;
        
        public PartUploader(File sampleFile, long offset, long partSize, int partNumber, String uploadId)
        {
            this.sampleFile = sampleFile;
            this.offset = offset;
            this.partSize = partSize;
            this.partNumber = partNumber;
            this.uploadId = uploadId;
        }
        
        @Override
        public void run()
        {
            try
            {
                UploadPartRequest uploadPartRequest = new UploadPartRequest();
                uploadPartRequest.setBucketName(bucketName);
                uploadPartRequest.setObjectKey(objectKey);
                uploadPartRequest.setUploadId(this.uploadId);
                uploadPartRequest.setFile(this.sampleFile);
                uploadPartRequest.setPartSize(this.partSize);
                uploadPartRequest.setOffset(this.offset);
                uploadPartRequest.setPartNumber(this.partNumber);
                
                UploadPartResult uploadPartResult = wosClient.uploadPart(uploadPartRequest);
                System.out.println("Part#" + this.partNumber + " done\n");
                partETags.add(new PartEtag(uploadPartResult.getEtag(), uploadPartResult.getPartNumber()));
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
    
    private static File createSampleFile()
        throws IOException
    {
        File file = File.createTempFile("wos-java-sdk-", ".txt");
        file.deleteOnExit();
        
        Writer writer = new OutputStreamWriter(new FileOutputStream(file));
        for (int i = 0; i < 1000000; i++)
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
