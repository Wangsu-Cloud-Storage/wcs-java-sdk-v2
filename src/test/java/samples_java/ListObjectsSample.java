package samples_java;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import com.wos.services.WosClient;
import com.wos.services.WosConfiguration;
import com.wos.services.exception.WosException;
import com.wos.services.model.*;
import com.wos.services.model.DeleteObjectsResult.DeleteObjectResult;
import com.wos.services.model.WosObject;

/**
 * This sample demonstrates how to list objects under specified bucket 
 * from WOS using the WOS SDK for Java.
 */
public class ListObjectsSample
{
    private static final String endPoint = "https://your-endpoint";

    private static final String ak = "*** Provide your Access Key ***";

    private static final String sk = "*** Provide your Secret Key ***";

    private static WosClient wosClient;

    private static String bucketName = "my-wos-bucket-demo";
    
    public static void main(String[] args)
        throws UnsupportedEncodingException
    {
        WosConfiguration config = new WosConfiguration();
        config.setSocketTimeout(300000);
        config.setConnectionTimeout(100000);
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
            //wosClient.createBucket(bucketName);

            final String content = "Hello WOS";
            final String keyPrefix = "MyObjectKey";
            ObjectListing objectListing = null;
            ListObjectsRequest listObjectsRequest = null;
            /*
             * First insert 100 objects for demo
             */
            List<String> keys = new ArrayList<String>();
            for (int i = 0; i < 100; i++)
            {
                String key = keyPrefix + i;
                InputStream instream = new ByteArrayInputStream(content.getBytes("UTF-8"));
                wosClient.putObject(bucketName, key, instream, null);
                keys.add(key);
            }
            System.out.println("Put " + keys.size() + " objects completed.");
            
            /*
             * List objects using default parameters, will return up to 1000 objects
             */
            System.out.println("List objects using default parameters:\n");
            objectListing = wosClient.listObjects(bucketName);
            for (WosObject object : objectListing.getObjects())
            {
                System.out.println("\t" + object.getObjectKey() + " etag[" + object.getMetadata().getEtag() + "]");
            }
            System.out.println();
            
            /*
             * List the first 10 objects 
             */
            System.out.println("List the first 10 objects :\n");
            listObjectsRequest = new ListObjectsRequest(bucketName);
            listObjectsRequest.setMaxKeys(10);
            objectListing = wosClient.listObjects(listObjectsRequest);
            for (WosObject object : objectListing.getObjects())
            {
                System.out.println("\t" + object.getObjectKey() + " etag[" + object.getMetadata().getEtag() + "]");
            }
            System.out.println();
            
            String theSecond10ObjectsMarker = objectListing.getNextMarker();
            
            /*
             * List the second 10 objects using marker 
             */
            System.out.println("List the second 10 objects using marker:\n");
            listObjectsRequest = new ListObjectsRequest(bucketName);
            listObjectsRequest.setMaxKeys(10);
            listObjectsRequest.setMarker(theSecond10ObjectsMarker);
            objectListing = wosClient.listObjects(listObjectsRequest);
            for (WosObject object : objectListing.getObjects())
            {
                System.out.println("\t" + object.getObjectKey() + " etag[" + object.getMetadata().getEtag() + "]");
            }
            System.out.println();
            
            /*
             * List objects with prefix and max keys
             */
            System.out.println("List objects with prefix and max keys:\n");
            listObjectsRequest = new ListObjectsRequest(bucketName);
            listObjectsRequest.setPrefix(keyPrefix + "2");
            listObjectsRequest.setMaxKeys(5);
            objectListing = wosClient.listObjects(listObjectsRequest);
            for (WosObject object : objectListing.getObjects())
            {
                System.out.println("\t" + object.getObjectKey() + " etag[" + object.getMetadata().getEtag() + "]");
            }
            System.out.println();
            
            /*
             * List all the objects in way of pagination
             */
            System.out.println("List all the objects in way of pagination:\n");
            listObjectsRequest = new ListObjectsRequest(bucketName);
            listObjectsRequest.setMaxKeys(10);
            String nextMarker = null;
            int index = 1;
            do
            {
                listObjectsRequest.setMarker(nextMarker);
                objectListing = wosClient.listObjects(listObjectsRequest);
                System.out.println("Page:" + index++ + "\n");
                for (WosObject object : objectListing.getObjects())
                {
                    System.out.println("\t" + object.getObjectKey() + " etag[" + object.getMetadata().getEtag() + "]");
                }
                nextMarker = objectListing.getNextMarker();
            } while (objectListing.isTruncated());
            System.out.println();
            
            /*
             * Delete all the objects created
             */
            DeleteObjectsRequest request = new DeleteObjectsRequest();
            request.setBucketName(bucketName);
            request.setQuiet(false);
            String[] kvs = new String[keys.size()];
            index = 0;
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
