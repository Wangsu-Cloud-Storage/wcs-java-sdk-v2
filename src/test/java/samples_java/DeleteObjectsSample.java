package samples_java;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import com.wos.services.WosClient;
import com.wos.services.WosConfiguration;
import com.wos.services.exception.WosException;
import com.wos.services.model.DeleteObjectsRequest;
import com.wos.services.model.DeleteObjectsResult;
import com.wos.services.model.DeleteObjectsResult.DeleteObjectResult;
import com.wos.services.model.DeleteObjectsResult.ErrorResult;

/**
 * This sample demonstrates how to delete objects under specified bucket 
 * from WOS using the WOS SDK for Java.
 */
public class DeleteObjectsSample
{
    private static final String endPoint = "https://your-endpoint";

    private static final String ak = "*** Provide your Access Key ***";

    private static final String sk = "*** Provide your Secret Key ***";

    private static WosClient wosClient;

    private static String bucketName = "my-wos-bucket-demo";
    
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
            System.out.println("Create a new bucket for demo\n");
            //wosClient.createBucket(bucketName);
            
            /*
             * Batch put objects into the bucket
             */
            final String content = "Thank you for using Object Storage Service";
            final String keyPrefix = "MyObjectKey";
            List<String> keys = new ArrayList<String>();
            for (int i = 0; i < 5; i++)
            {
                String key = keyPrefix + i;
                InputStream instream = new ByteArrayInputStream(content.getBytes());
                wosClient.putObject(bucketName, key, instream, null);
                System.out.println("Succeed to put object " + key);
                keys.add(key);
            }
            System.out.println();
            
            /*
             * Delete all objects uploaded recently under the bucket
             */
            System.out.println("\nDeleting all objects\n");
            
            DeleteObjectsRequest request = new DeleteObjectsRequest();
            request.setBucketName(bucketName);
            request.setQuiet(false);

            String[] kvs = new String[keys.size()];
            int index = 0;
            for (String key : keys)
            {
                kvs[index++] = key;
            }
            
            request.setObjectKeys(kvs);
            
            System.out.println("Delete results:");
            
            DeleteObjectsResult deleteObjectsResult = wosClient.deleteObjects(request);
            for (DeleteObjectResult object : deleteObjectsResult.getDeletedObjectResults())
            {
                System.out.println("\t" + object);
            }
            
            System.out.println("Error results:");
            
            for (ErrorResult error : deleteObjectsResult.getErrorResults())
            {
                System.out.println("\t" + error);
            }
            
            System.out.println();
            
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
