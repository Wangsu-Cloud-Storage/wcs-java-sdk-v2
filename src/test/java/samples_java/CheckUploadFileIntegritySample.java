package samples_java;

import com.wos.services.WosClient;
import com.wos.services.WosConfiguration;
import com.wos.services.internal.utils.EtagUtils;
import com.wos.services.model.DeleteObjectResult;
import com.wos.services.model.PutObjectResult;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.UUID;

/**
 * 普通文件上传Etag校验，分片上传校验见{@link ConcurrentCopyPartSample}
 * @author luosh
 * @since 2023/6/5 16:17
 */
public class CheckUploadFileIntegritySample {
    private static final String endPoint = "https://s3-cn-east-5.wcsapi.com";

    private static final String ak = "*** Provide your Access Key ***";

    private static final String sk = "*** Provide your Secret Key ***";

    private static String bucketName = "my-wos-bucket-demo";

    private static String regionName = "my-wos-region-demo";

    private static String objectKey = "my-wos-object-key-demo";

    private static WosClient wosClient;

    public static void main(String[] args) throws IOException {
        try {
            WosConfiguration config = new WosConfiguration();
            config.setSocketTimeout(300000);
            config.setConnectionTimeout(100000);
            config.setEndPoint(endPoint);
            config.setRegionName(regionName);
            wosClient = new WosClient(ak, sk, config);
            simpleUploadCheck();
        } catch ( Exception e) {
            e.printStackTrace();
        } finally {
            wosClient.close();
        }
    }



    private static void simpleUploadCheck() throws IOException {
        File sampleFile = createSampleFile(1000);
        // 生成本地文件 Etag
        String localEtag = EtagUtils.getFileEtag(bucketName, objectKey, sampleFile);
        // 上传文件
        PutObjectResult putObjectResult = wosClient.putObject(bucketName, objectKey, sampleFile);
        System.out.println("local etag value:" + localEtag + " server etag value:" + putObjectResult.getEtag());
        // 删除文件
        DeleteObjectResult deleteObjectResult = wosClient.deleteObject(bucketName, objectKey);
        System.out.println("delete object result:{}" + deleteObjectResult.toString());
    }


    private static File createSampleFile(int size)
            throws IOException {
        File file = File.createTempFile("wos-java-sdk-", ".txt");
        file.deleteOnExit();
        Writer writer = new OutputStreamWriter(new FileOutputStream(file));
        for (int i = 0; i < size; i++) {
            writer.write(UUID.randomUUID() + "\n");
            writer.write(UUID.randomUUID() + "\n");
        }
        writer.flush();
        writer.close();
        return file;
    }
}