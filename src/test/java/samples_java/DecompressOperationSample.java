package samples_java;

import com.wos.services.WosClient;
import com.wos.services.WosConfiguration;
import com.wos.services.exception.WosException;
import com.wos.services.internal.handler.XmlRequestConstructHandler;
import com.wos.services.internal.utils.JSONChange;
import com.wos.services.internal.utils.UrlCodecUtil;
import com.wos.services.model.avOperation.AudioAndVideoOperationConfig;
import com.wos.services.model.avOperation.AudioAndVideoTaskRequestResult;
import com.wos.services.model.avOperation.AvOperationTypeEnum;
import com.wos.services.model.avOperation.CreateDecompressTaskRequest;
import com.wos.services.model.avOperation.OutPutFileInfo;
import com.wos.services.model.avOperation.QueryDecompressResult;

import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.util.LinkedHashMap;

public class DecompressOperationSample {
    private static final String endPoint = "https://your-endpoint";

    private static final String ak = "*** Provide your Access Key ***";

    private static final String sk = "*** Provide your Secret Key ***";

    /**
     * the source bucket name
     */
    private static String bucketName = "my-wos-bucket-demo";

    private static String regionName = "my-wos-region-demo";

    private static String sourceFileName = "test-sdk.zip";

    /**
     * A file that stores a list of decompressed files name
     */
    private static String decompressListFileName = "test.list";

    /**
     * the notification url
     */
    private static String notificationUrl = "https://your-notification-url";

    private static WosClient wosClient;

    public static void main(String[] args) throws JAXBException {
        WosConfiguration config = new WosConfiguration();
        config.setSocketTimeout(300000);
        config.setConnectionTimeout(100000);
        config.setEndPoint(endPoint);
        config.setRegionName(regionName);

        wosClient = new WosClient(ak, sk, config);

        // create decompress task test
        String persistentId = CreateTask();

        // get Task Info detail
        QueryDecompressResult taskInfoDetail = getTaskInfoDetail(persistentId);
        System.out.println("TaskDetail: \n" + JSONChange.objToJson(taskInfoDetail));

    }



    private static String CreateTask() {
        AudioAndVideoOperationConfig audioAndVideoOperationConfig = new AudioAndVideoOperationConfig();

        LinkedHashMap<String, String> operationParams = new LinkedHashMap<>();
        operationParams.put("format", "zip");
        operationParams.put("crush", "0");
        audioAndVideoOperationConfig.setOperationParams(operationParams);

        OutPutFileInfo outputFileInfo = new OutPutFileInfo();
        outputFileInfo.setOutputBucket(bucketName);
        outputFileInfo.setOutputKey(UrlCodecUtil.dataEncodeWithUtf8(decompressListFileName));
        audioAndVideoOperationConfig.setOutPutFileInfo(outputFileInfo);

        CreateDecompressTaskRequest createAudioAndVideoTaskRequest = new CreateDecompressTaskRequest();
        createAudioAndVideoTaskRequest.setBucketName(bucketName);
        createAudioAndVideoTaskRequest.setSourceFileName(sourceFileName);
        createAudioAndVideoTaskRequest.setConfig(audioAndVideoOperationConfig);
        createAudioAndVideoTaskRequest.setNotifyUrl(UrlCodecUtil.dataEncodeWithUtf8(notificationUrl));
        createAudioAndVideoTaskRequest.setForce(1);

        String s = XmlRequestConstructHandler.convertToDifferentRootXml(createAudioAndVideoTaskRequest, AvOperationTypeEnum.Decompression.getValue());
        System.out.println(s);
        AudioAndVideoTaskRequestResult audioAndVideoTask = new AudioAndVideoTaskRequestResult();
        try {
            audioAndVideoTask = wosClient.createDecompressTask(createAudioAndVideoTaskRequest);
            System.out.println("persistentId: " + audioAndVideoTask.getPersistentId());
        } catch (WosException e) {
            System.out.println("Response Code: " + e.getResponseCode());
            System.out.println("Error Message: " + e.getErrorMessage());
            System.out.println("Error Code: " + e.getErrorCode());
            System.out.println("Request ID: " + e.getErrorRequestId());
            System.out.println("Host ID: " + e.getErrorHostId());
        } finally {
            //if (wosClient != null) {
            //    try {
            //        /*
            //         * Close wos client
            //         */
            //        wosClient.close();
            //    } catch (IOException e) {
            //    }
            //}
        }
        return audioAndVideoTask.getPersistentId();
    }

    private static QueryDecompressResult getTaskInfoDetail(String persistentId) {
        QueryDecompressResult queryDecompressResult = null;
        try {
            queryDecompressResult = wosClient.getDecompressTask(bucketName, persistentId);
            System.out.println(queryDecompressResult);
        } catch (WosException e) {
            System.out.println("Response Code: " + e.getResponseCode());
            System.out.println("Error Message: " + e.getErrorMessage());
            System.out.println("Error Code: " + e.getErrorCode());
            System.out.println("Request ID: " + e.getErrorRequestId());
            System.out.println("Host ID: " + e.getErrorHostId());
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
        return queryDecompressResult;
    }

}
