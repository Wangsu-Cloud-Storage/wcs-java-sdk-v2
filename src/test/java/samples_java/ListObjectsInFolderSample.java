package samples_java;

import java.io.ByteArrayInputStream;
import java.io.IOException;
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
 * This sample demonstrates how to list objects under a specified folder of a bucket 
 * from WOS using the WOS SDK for Java.
 */
public class ListObjectsInFolderSample
{
    private static final String endPoint = "https://your-endpoint";

    private static final String ak = "*** Provide your Access Key ***";

    private static final String sk = "*** Provide your Secret Key ***";

    private static WosClient wosClient;

    private static String bucketName = "my-wos-bucket-demo";

    private static String regionName = "my-wos-region-demo";
    
    public static void main(String[] args) throws UnsupportedEncodingException
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

            final String content = "Hello WOS";
            final String keyPrefix = "MyObjectKey";
            final String folderPrefix = "src";
            final String subFolderPrefix = "test";
            ObjectListing objectListing = null;
            ListObjectsRequest listObjectsRequest = null;
            List<String> keys = new ArrayList<String>();
            List<String> keyFolders = new ArrayList<String>();
            /*
             * First prepare folders and sub folders
             */

            for (int i = 0; i < 5; i++)
            {
                String key = folderPrefix + i + "/";
                wosClient.putObject(bucketName, key, new ByteArrayInputStream(new byte[0]), null);
                keyFolders.add(key);
                
                for (int j = 0; j < 3; j++)
                {
                    String subKey = key + subFolderPrefix + j + "/";
                    wosClient.putObject(bucketName, subKey, new ByteArrayInputStream(new byte[0]));
                    keyFolders.add(subKey);
                }
            }
            
            /*
             * Insert 2 objects in each folder
             */
            objectListing  = wosClient.listObjects(bucketName);
            for (WosObject object : objectListing.getObjects()) {
                for(int i=0;i<2;i++){
                    String objectKey = object.getObjectKey() + keyPrefix + i;
                    wosClient.putObject(bucketName, objectKey, new ByteArrayInputStream(content.getBytes("UTF-8")), null);
                    keys.add(objectKey);
                }
            }
          /*
          * Insert 2 objects in root path
          */
            wosClient.putObject(bucketName, keyPrefix+0, new ByteArrayInputStream(content.getBytes("UTF-8")), null);
            wosClient.putObject(bucketName, keyPrefix+1, new ByteArrayInputStream(content.getBytes("UTF-8")), null);
            keys.add(keyPrefix+0);
            keys.add(keyPrefix+1);
            System.out.println("Put " + keys.size() + " objects completed.");
            System.out.println();
           
            /*
             * List all objects in folder src0/
             */
            System.out.println("List all objects in folder src0/ \n");
            listObjectsRequest = new ListObjectsRequest(bucketName);
            listObjectsRequest.setPrefix("src0/");
            objectListing  = wosClient.listObjects(listObjectsRequest);
            for (WosObject object : objectListing.getObjects()) {
                System.out.println("\t" + object.getObjectKey() + " etag[" + object.getMetadata().getEtag()+ "]");
            }
            System.out.println();
            
            /*
             * List all objects in sub folder src0/test0/
             */
            System.out.println("List all objects in folder src0/test0/ \n");
            listObjectsRequest = new ListObjectsRequest(bucketName);
            listObjectsRequest.setPrefix("src0/test0/");
            objectListing  = wosClient.listObjects(listObjectsRequest);
            for (WosObject object : objectListing.getObjects()) {
                System.out.println("\t" + object.getObjectKey() + " etag[" + object.getMetadata().getEtag()+ "]");
            }
            System.out.println();
            
            /*
             * List all objects group by folder
             */
            System.out.println("List all objects group by folder \n");
            listObjectsRequest = new ListObjectsRequest(bucketName);
            listObjectsRequest.setDelimiter("/");
            
            objectListing  = wosClient.listObjects(listObjectsRequest);
            System.out.println("Root path:");
            for (WosObject object : objectListing.getObjects()) {
                System.out.println("\t" + object.getObjectKey() + " etag[" + object.getMetadata().getEtag()+ "]");
            }
            listObjectsByPrefix(listObjectsRequest, objectListing);
            
            System.out.println();
            
            
            /*
             * Delete all the objects created
             */
            DeleteObjectsRequest request = new DeleteObjectsRequest();
            request.setBucketName(bucketName);
            request.setQuiet(false);

            System.out.println("Delete results:");
            String[] kvs = new String[keys.size()];
            int index = 0;
            for (String key : keys) {
                kvs[index++] = key;
            }
            request.setObjectKeys(kvs);
            deleteObjects(request);

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

    private static void listObjectsByPrefix(ListObjectsRequest listObjectsRequest, ObjectListing objectListing) throws WosException
    {
        for(String prefix : objectListing.getCommonPrefixes()){
            System.out.println("Folder " + prefix + ":");
            listObjectsRequest.setPrefix(prefix);
            objectListing  = wosClient.listObjects(listObjectsRequest);
            for (WosObject object : objectListing.getObjects()) {
                System.out.println("\t" + object.getObjectKey() + " etag[" + object.getMetadata().getEtag()+ "]");
            }
            listObjectsByPrefix(listObjectsRequest, objectListing);
        }
    }

    private static void deleteObjects(DeleteObjectsRequest deleteObjectsRequest) {
        DeleteObjectsResult deleteObjectsResult = wosClient.deleteObjects(deleteObjectsRequest);
        for (DeleteObjectResult object : deleteObjectsResult.getDeletedObjectResults()) {
            System.out.println("\t" + object);
        }
    }

    private static void deleteFolderOrObject(List<String> objectKeyFolders) {
        for (String objectKey: objectKeyFolders) {
            com.wos.services.model.DeleteObjectResult deleteObjectsResult = wosClient.deleteObject(bucketName, objectKey);
            System.out.println("\t" + deleteObjectsResult.getObjectKey());
        }
    }
}
