package samples_java;

import com.wos.services.WosClient;
import com.wos.services.WosConfiguration;
import com.wos.services.exception.WosException;
import com.wos.services.model.ObjectMetadata;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

/**
 * This sample demonstrates how to set/get self-defined metadata for object
 * on WOS using the WOS SDK for Java.
 */
public class ObjectMetaSample
{
    private static final String endPoint = "https://your-endpoint";
    
    private static final String ak = "*** Provide your Access Key ***";
    
    private static final String sk = "*** Provide your Secret Key ***";

    private static WosClient wosClient;

    private static String bucketName = "my-wos-bucket-demo";

    private static String regionName = "my-wos-region-demo";

    private static String objectKey = "my-wos-object-key-demo";
    
    public static void main(String[] args)
        throws UnsupportedEncodingException
    {
        WosConfiguration config = new WosConfiguration();
        config.setSocketTimeout(30000);
        config.setConnectionTimeout(10000);
        config.setEndPoint(endPoint);
        config.setRegionName(regionName);
        try
        {
            /*
             * Constructs a wos client instance with your account for accessing WOS
             */
            wosClient = new WosClient(ak, sk, config);
            
            /*
             * Create bucket 
             */
            System.out.println("Create a new bucket for demo\n");
            //wosClient.createBucket(bucketName);
            
            ObjectMetadata meta = new ObjectMetadata();
            /*
             * Setting object mime type
             */
            meta.setContentType("text/plain");
            /*
             * Setting self-defined metadata
             */
            meta.getMetadata().put("x-wos-meta-meta1", "value1");
            meta.addUserMetadata("meta2", "value2");
            String content = "Hello WOS";
            wosClient.putObject(bucketName, objectKey, new ByteArrayInputStream(content.getBytes("UTF-8")), meta);
            System.out.println("Create object " + objectKey + " successfully!\n");
            
            /*
             * Get object metadata
             */
            ObjectMetadata result = wosClient.getObjectMetadata(bucketName, objectKey);
            System.out.println("Getting object metadata:");
            System.out.println("\tContentType:" + result.getContentType());
            System.out.println("\tmeta1:" + result.getUserMetadata("meta1"));
            System.out.println("\tmeta2:" + result.getUserMetadata("meta2"));
            
            /*
             * Delete object
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
