package samples_java;

import com.wos.services.WosClient;
import com.wos.services.WosConfiguration;
import com.wos.services.exception.WosException;
import com.wos.services.model.DeleteObjectResult;
import com.wos.services.model.PutObjectRequest;
import com.wos.services.model.WosObject;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * This sample demonstrates how to create an empty folder under 
 * specified bucket to WOS using the WOS SDK for Java.
 */
public class CreateFolderSample
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
            List<String> keyFolders = new ArrayList<String>();
            
            /*
             * Way 1:
             * Create an empty folder without request body, note that the key must be 
             * suffixed with a slash
             */
            final String keySuffixWithSlash1 = "MyObjectKey1/";
            wosClient.putObject(bucketName, keySuffixWithSlash1, new ByteArrayInputStream(new byte[0]));
            keyFolders.add(keySuffixWithSlash1);
            // Creating objects under the folder
            String objectKey1 = keySuffixWithSlash1 + "objectname.txt";
            keyFolders.add(objectKey1);
            wosClient.putObject(bucketName, objectKey1, new ByteArrayInputStream("Hello WOS".getBytes()));
            System.out.println("Creating an empty folder " + keySuffixWithSlash1 + "\n");
            
            /*
             * Verify whether the size of the empty folder is zero 
             */
            WosObject object = wosClient.getObject(bucketName, keySuffixWithSlash1);
            System.out.println("Size of the empty folder '" + object.getObjectKey() + "' is " + object.getMetadata().getContentLength());
            object.getObjectContent().close();
            
            /*
             * Way 2:
             * Create an empty folder without request body, note that the key must be 
             * suffixed with a slash
             */
            final String keySuffixWithSlash2 = "MyObjectKey2/";
            PutObjectRequest request = new PutObjectRequest();
            request.setBucketName(bucketName);
            request.setObjectKey(keySuffixWithSlash2);
            request.setInput(new ByteArrayInputStream(new byte[0]));
            wosClient.putObject(request);
            keyFolders.add(keySuffixWithSlash2);
            // Creating objects under the folder
            String objectKey2 = keySuffixWithSlash2 + "objectname.txt";
            keyFolders.add(objectKey2);
            wosClient.putObject(bucketName, objectKey2, new ByteArrayInputStream("Hello WOS".getBytes()));
            System.out.println("Creating an empty folder " + keySuffixWithSlash2 + "\n");
            
            /*
             * Verify whether the size of the empty folder is zero 
             */
            object = wosClient.getObject(bucketName, keySuffixWithSlash2);
            System.out.println("Size of the empty folder '" + object.getObjectKey() + "' is " + object.getMetadata().getContentLength());
            object.getObjectContent().close();

            deleteFolderOrObject(keyFolders);
            
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

    private static void deleteFolderOrObject(List<String> objectKeyFolders) {
        for (String objectKey: objectKeyFolders) {
            DeleteObjectResult deleteObjectsResult = wosClient.deleteObject(bucketName, objectKey);
            System.out.println("\t" + deleteObjectsResult.getObjectKey());
        }
    }
}
