package samples_java;

import com.wos.services.WosClient;
import com.wos.services.WosConfiguration;
import com.wos.services.exception.WosException;
import com.wos.services.model.GetObjectRequest;
import com.wos.services.model.ObjectMetadata;
import com.wos.services.model.WosObject;

import java.io.*;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * This sample demonstrates how to download an object concurrently 
 * from WOS using the WOS SDK for Java.
 */
public class ConcurrentDownloadObjectSample
{
    private static final String endPoint = "https://your-endpoint";
    
    private static final String ak = "*** Provide your Access Key ***";
    
    private static final String sk = "*** Provide your Secret Key ***";


    private static WosClient wosClient;

    private static String bucketName = "my-wos-bucket-demo";

    private static String objectKey = "my-wos-object-key-demo";
    
    private static String localFilePath = "/temp/" + objectKey;
    
    private static ExecutorService executorService = Executors.newFixedThreadPool(5);
    
    private static AtomicInteger blocks = new AtomicInteger(0);
    
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
             * Create bucket 
             */
            System.out.println("Create a new bucket to upload file\n");
            //wosClient.createBucket(bucketName);
            
            /*
             * Upload an object to your bucket
             */
            System.out.println("Uploading a new object to WOS from a file\n");
            wosClient.putObject(bucketName, objectKey, createSampleFile());
            
            /*
             * Get size of the object and pre-create a random access file to hold object data
             */
            ObjectMetadata metadata = wosClient.getObjectMetadata(bucketName, objectKey);
            long objectSize = metadata.getContentLength();
            
            System.out.println("Object size from metadata:" + objectSize + "\n");
            
            File localFile = new File(localFilePath);
            if (!localFile.getParentFile().exists())
            {
                localFile.getParentFile().mkdirs();
            }
            RandomAccessFile raf = new RandomAccessFile(localFile, "rw");
            raf.setLength(objectSize);
            raf.close();
            
            /*
             * Calculate how many blocks to be divided
             */
            final long blockSize = 5 * 1024 * 1024L; // 5MB
            int blockCount = (int)(objectSize / blockSize);
            if (objectSize % blockSize != 0)
            {
                blockCount++;
            }
            System.out.println("Total blocks count " + blockCount + "\n");
            
            /*
             * Download the object concurrently
             */
            System.out.println("Start to download " + objectKey + "\n");
            for (int i = 0; i < blockCount;)
            {
                long rangeStart = i++ * blockSize;
                long rangeEnd = (i == blockCount) ? objectSize - 1 : i * blockSize - 1;
                executorService.execute(new BlockFetcher(rangeStart, rangeEnd, i));
            }
            
            /*
             * Waiting for all blocks finished
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
             * Verify whether all blocks are finished
             */
            if (blocks.intValue() != blockCount)
            {
                throw new IllegalStateException("Some blocks are not finished");
            }
            else
            {
                System.out.println("Succeed to download object " + objectKey);
            }
            
            System.out.println("Deleting object  " + objectKey + "\n");
            wosClient.deleteObject(bucketName, objectKey);
            
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
    
    private static class BlockFetcher implements Runnable
    {
        private long rangeStart;
        
        private long rangeEnd;
        
        private int blockNumber;
        
        public BlockFetcher(long rangeStart, long rangeEnd, int blockNumber)
        {
            this.rangeStart = rangeStart;
            this.rangeEnd = rangeEnd;
            this.blockNumber = blockNumber;
        }
        
        @Override
        public void run()
        {
            RandomAccessFile raf = null;
            try
            {
                raf = new RandomAccessFile(localFilePath, "rw");
                raf.seek(rangeStart);
                
                GetObjectRequest getObjectRequest = new GetObjectRequest(bucketName, objectKey);
                getObjectRequest.setRangeStart(rangeStart);
                getObjectRequest.setRangeEnd(rangeEnd);
                WosObject object = wosClient.getObject(getObjectRequest);
                
                InputStream content = object.getObjectContent();
                try
                {
                    byte[] buf = new byte[8196];
                    int bytes = 0;
                    while ((bytes = content.read(buf)) != -1)
                    {
                        raf.write(buf, 0, bytes);
                    }
                    blocks.incrementAndGet();
                    System.out.println("Block : " + blockNumber + " Finish \n");
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
                finally
                {
                    content.close();
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
            finally
            {
                if (raf != null)
                {
                    try
                    {
                        raf.close();
                    }
                    catch (IOException e)
                    {
                        e.printStackTrace();
                    }
                }
            }
        }
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
    
}
