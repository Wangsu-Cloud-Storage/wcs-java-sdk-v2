package samples_java;

import com.wos.services.WosClient;
import com.wos.services.WosConfiguration;
import com.wos.services.exception.WosException;
import com.wos.services.model.WosObject;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;

/**
 * This sample demonstrates how to download an object
 * from WOS in different ways using the WOS SDK for Java.
 */
public class DownloadSample {
    private static final String endPoint = "https://your-endpoint";

    private static final String ak = "*** Provide your Access Key ***";

    private static final String sk = "*** Provide your Secret Key ***";

    private static WosClient wosClient;

    private static String bucketName = "my-wos-bucket-demo";

    private static String regionName = "my-wos-region-demo";

    private static String objectKey = "my-wos-object-key-demo";

    private static String localFilePath = "/temp/" + objectKey;

    public static void main(String[] args)
            throws IOException {
        WosConfiguration config = new WosConfiguration();
        config.setSocketTimeout(300000);
        config.setConnectionTimeout(100000);
        config.setEndPoint(endPoint);
        config.setRegionName(regionName);
        try {
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
             * Upload an object to your bucket
             */
            System.out.println("Uploading a new object to WOS from a file\n");
            wosClient.putObject(bucketName, objectKey, createSampleFile());

            System.out.println("Downloading an object\n");

            /*
             * Download the object as an inputstream and display it directly
             */
            simpleDownload();

            File localFile = new File(localFilePath);
            if (!localFile.getParentFile().exists()) {
                localFile.getParentFile().mkdirs();
            }

            System.out.println("Downloading an object to file:" + localFilePath + "\n");
            /*
             * Download the object to a file
             */
            downloadToLocalFile();

            System.out.println("Deleting object  " + objectKey + "\n");
            wosClient.deleteObject(bucketName, objectKey);

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

    private static void downloadToLocalFile()
            throws WosException, IOException {
        WosObject wosObject = wosClient.getObject(bucketName, objectKey);
        ReadableByteChannel rchannel = Channels.newChannel(wosObject.getObjectContent());

        ByteBuffer buffer = ByteBuffer.allocate(4096);
        WritableByteChannel wchannel = Channels.newChannel(new FileOutputStream(new File(localFilePath)));

        while (rchannel.read(buffer) != -1) {
            buffer.flip();
            wchannel.write(buffer);
            buffer.clear();
        }
        rchannel.close();
        wchannel.close();
    }

    private static void simpleDownload()
            throws WosException, IOException {
        WosObject wosObject = wosClient.getObject(bucketName, objectKey);
        displayTextInputStream(wosObject.getObjectContent());
    }

    private static void displayTextInputStream(InputStream input)
            throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(input));
        while (true) {
            String line = reader.readLine();
            if (line == null)
                break;

            System.out.println("\t" + line);
        }
        System.out.println();

        reader.close();
    }

    private static File createSampleFile()
            throws IOException {
        File file = File.createTempFile("wos-java-sdk-", ".txt");
        file.deleteOnExit();
        Writer writer = new OutputStreamWriter(new FileOutputStream(file));
        writer.write("abcdefghijklmnopqrstuvwxyz\n");
        writer.write("0123456789011234567890\n");
        writer.close();

        return file;
    }

}
