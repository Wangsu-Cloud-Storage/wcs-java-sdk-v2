package samples_java;

import com.wos.services.WosClient;
import com.wos.services.WosConfiguration;
import com.wos.services.exception.WosException;
import com.wos.services.internal.utils.ServiceUtils;
import com.wos.services.model.AccessControlList;
import com.wos.services.model.AuthTypeEnum;
import com.wos.services.model.ObjectMetadata;
import com.wos.services.model.WosObject;

import java.io.ByteArrayInputStream;
import java.io.IOException;

/**
 * This sample demonstrates how to do object-related operations
 * (such as create/delete/get/copy object, do object ACL/OPTIONS) 
 * on WOS using the WOS SDK for Java.
 */
public class ObjectOperationsSample
{
    private static final String endPoint = "https://your-endpoint";

    private static final String ak = "*** Provide your Access Key ***";

    private static final String sk = "*** Provide your Secret Key ***";

    private static WosClient wosClient;

    private static String bucketName = "my-wos-bucket-demo";

    private static String regionName = "my-wos-region-demo";

    private static String objectKey = "my-wos-object-key-demo";
    
    public static void main(String[] args)
        throws IOException
    {
        WosConfiguration config = new WosConfiguration();
        config.setSocketTimeout(30000);
        config.setConnectionTimeout(10000);
        config.setEndPoint(endPoint);
        config.setAuthType(AuthTypeEnum.V4);
        config.setRegionName(regionName);

        try
        {
            /*
             * Constructs a wos client instance with your account for accessing WOS
             */
            wosClient = new WosClient(ak, sk, config);
//
            /*
             * Create object
             */
            String content = "Hello WOS";
            ObjectMetadata objectMetadata = new ObjectMetadata();
            objectMetadata.addUserMetadata("aa", "abc");
            wosClient.putObject(bucketName, objectKey, new ByteArrayInputStream(content.getBytes("UTF-8")), objectMetadata);
            System.out.println("Create object:" + objectKey + " successfully!\n");


            /*
             * Get object metadata
             */
            System.out.println("Getting object metadata");
            ObjectMetadata metadata = wosClient.getObjectMetadata(bucketName, objectKey);
            System.out.println("\t" + metadata);

            /*
             * Get object
             */
            System.out.println("Getting object content");
            WosObject wosObject = wosClient.getObject(bucketName, objectKey);
            System.out.println("\tobject content:" + ServiceUtils.toString(wosObject.getObjectContent()));
            System.out.println(wosObject.getMetadata());
//
            AccessControlList objectAcl = wosClient.getObjectAcl(bucketName, objectKey);
            System.out.println(objectAcl);

            /*
             * Copy object
             */
            String sourceBucketName = bucketName;
            String destBucketName = bucketName;
            String sourceObjectKey = objectKey;
            String destObjectKey = objectKey + "-back";
            System.out.println("Copying object\n");
            wosClient.copyObject(sourceBucketName, sourceObjectKey, destBucketName, destObjectKey);

            /*
             * Delete object
             */
            System.out.println("Deleting objects\n");
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
                } catch (IOException e) {
                }
            }
        }
        
    }
}
