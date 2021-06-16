package samples_java;

import com.wos.services.WosClient;
import com.wos.services.WosConfiguration;
import com.wos.services.exception.WosException;
import com.wos.services.internal.utils.ServiceUtils;
import com.wos.services.model.ObjectMetadata;
import com.wos.services.model.RestoreObjectRequest;
import com.wos.services.model.RestoreObjectRequest.RestoreObjectStatus;

import java.io.ByteArrayInputStream;
import java.io.IOException;

/**
 * This sample demonstrates how to download an cold object 
 * from WOS using the WOS SDK for Java.
 */
public class RestoreObjectSample
{
    private static final String endPoint = "https://your-endpoint";

    private static final String ak = "*** Provide your Access Key ***";

    private static final String sk = "*** Provide your Secret Key ***";

    private static WosClient wosClient;

    private static String bucketName = "my-wos-bucket-demo";

    private static String objectKey = "my-wos-object-key-demo";
    
    public static void main(String[] args) throws InterruptedException, IOException
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
             * Create a cold object
             */
            System.out.println("Create a new cold object for demo\n");
            String content = "Hello WOS";

            ObjectMetadata meta = new ObjectMetadata();
            /*
             * Setting self-defined metadata
             */
            meta.getMetadata().put("x-wos-storage-class", "archive");
            wosClient.putObject(bucketName, objectKey, new ByteArrayInputStream(content.getBytes("UTF-8")), meta);

            /*
             * Restore the cold object
             */
            System.out.println("Restore the cold object");
            RestoreObjectRequest restoreObjectRequest = new RestoreObjectRequest(bucketName, objectKey, 1);
            System.out.println("\t"+(wosClient.restoreObject(restoreObjectRequest) ==  RestoreObjectStatus.INPROGRESS));

            /*
             * Delete the cold object
             */
            wosClient.deleteObject(bucketName, objectKey);

            /*
             * Create a cold object
             */
            System.out.println("Create a new cold object for demo\n");
            wosClient.putObject(bucketName, objectKey, new ByteArrayInputStream(content.getBytes("UTF-8")), meta);
            
            /*
             * Get the cold object status
             */
            System.out.println("Get the cold object status");
            restoreObjectRequest = new RestoreObjectRequest(bucketName, objectKey, 1);
            System.out.println("\t"+(wosClient.restoreObject(restoreObjectRequest) ==  RestoreObjectStatus.AVALIABLE) + "\n");
            
            /*
             * Waiting for restored, Get the cold object
             */
            System.out.println("Get the cold object");
            System.out.println("\tcontent:" + ServiceUtils.toString(wosClient.getObject(bucketName, objectKey).getObjectContent()));
            
            /*
             * Delete the cold object
             */
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
}
