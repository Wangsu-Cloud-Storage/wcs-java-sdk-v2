package samples_java;

import com.wos.services.WosClient;
import com.wos.services.WosConfiguration;
import com.wos.services.exception.WosException;
import com.wos.services.internal.handler.XmlRequestConstructHandler;
import com.wos.services.internal.utils.JSONChange;
import com.wos.services.internal.utils.UrlCodecUtil;
import com.wos.services.model.avOperation.AudioAndVideoOperationConfig;
import com.wos.services.model.avOperation.AudioAndVideoTaskDetailResult;
import com.wos.services.model.avOperation.AudioAndVideoTaskRequestResult;
import com.wos.services.model.avOperation.AvOperationTypeEnum;
import com.wos.services.model.avOperation.CreateAudioAndVideoTaskRequest;
import com.wos.services.model.avOperation.OutPutFileInfo;

import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

public class AudioAndVideoOperationSample {
    private static final String endPoint = "https://your-endpoint";

    private static final String ak = "*** Provide your Access Key ***";

    private static final String sk = "*** Provide your Secret Key ***";


    /**
     * the source bucket name
     */
    private static String bucketName = "my-wos-bucket-demo";

    private static String regionName = "my-wos-region-demo";

    /**
     * the source file name
     */
    private static String objectKey = "test-sdk.mp4";

    /**
     * the output bucket name
     */
    private static String outputBucketName = "my-wos-bucket-demo";

    /**
     * the output file name -- video snapshot
     */
    private static String outputSnapshotFileName = "test.jpg";

    /**
     * the output file name -- video snapshot
     */
    private static String videoConcatOutputFileName = "test.mp4";

    private static String inputSideFile1 = "side_file_1.mp4";

    private static String inputSideFile2 = "side_file_2.mp4";

    /**
     * the album name
     */
    private static String albumFileName = "getApic.mp3";

    /**
     * the albumCover file name
     */
    private static String albumCoverFileName = "getApic.jpg";

    /**
     * the transcoded file name
     */
    private static String transcodedFileName = "thumb.mp4";

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
        // create video snapshot task test
        String persistentId = CreateVideoSnapshotTask();

        // create video concat test
        // String persistentId = CreateVideoConcatTask();

        // create get album cover task test
        //String persistentId = CreateGetAlbumCoverTask();

        // create video transcode task test
        //String persistentId = createTranscodeTask();
        // get task info detail
        AudioAndVideoTaskDetailResult taskInfoDetail = getTaskInfoDetail(persistentId);

        System.out.println("TaskDetail: \n" + JSONChange.objToJson(taskInfoDetail));

    }

    private static String createTranscodeTask() {
        List<AudioAndVideoOperationConfig> configList = new ArrayList<>();
        AudioAndVideoOperationConfig audioAndVideoOperationConfig = new AudioAndVideoOperationConfig();

        // param list
        LinkedHashMap<String, String> operationParams = new LinkedHashMap<>();
        operationParams.put("format", "mp4");
        operationParams.put("vb", "128k");
        audioAndVideoOperationConfig.setOperationParams(operationParams);

        // output info
        OutPutFileInfo outputFileInfo = new OutPutFileInfo();
        outputFileInfo.setOutputBucket(outputBucketName);
        outputFileInfo.setOutputKey(UrlCodecUtil.dataEncodeWithUtf8(transcodedFileName));
        audioAndVideoOperationConfig.setOutPutFileInfo(outputFileInfo);
        configList.add(audioAndVideoOperationConfig);

        CreateAudioAndVideoTaskRequest createAudioAndVideoTaskRequest = new CreateAudioAndVideoTaskRequest();
        createAudioAndVideoTaskRequest.setBucketName(bucketName);
        createAudioAndVideoTaskRequest.setSourceFileName(objectKey);
        createAudioAndVideoTaskRequest.setOperationType(AvOperationTypeEnum.Avthumb);
        createAudioAndVideoTaskRequest.setConfigList(configList);
        createAudioAndVideoTaskRequest.setNotifyUrl(UrlCodecUtil.dataEncodeWithUtf8(notificationUrl));
        createAudioAndVideoTaskRequest.setForce(1);
        createAudioAndVideoTaskRequest.setSeparate(0);

        String xmlBody = XmlRequestConstructHandler.convertToDifferentRootXml(createAudioAndVideoTaskRequest, AvOperationTypeEnum.Avthumb.getValue());
        System.out.println(xmlBody);
        AudioAndVideoTaskRequestResult audioAndVideoTask = new AudioAndVideoTaskRequestResult();
        try {
            audioAndVideoTask = wosClient.createAudioAndVideoTask(createAudioAndVideoTaskRequest);
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

    private static String CreateGetAlbumCoverTask() {
        List<AudioAndVideoOperationConfig> configList = new ArrayList<>();
        AudioAndVideoOperationConfig audioAndVideoOperationConfig = new AudioAndVideoOperationConfig();

        // param list
        LinkedHashMap<String, String> operationParams = new LinkedHashMap<>();
        operationParams.put("format", "jpg");
        audioAndVideoOperationConfig.setOperationParams(operationParams);

        // output info
        OutPutFileInfo outputFileInfo = new OutPutFileInfo();
        outputFileInfo.setOutputBucket(outputBucketName);
        outputFileInfo.setOutputKey(UrlCodecUtil.dataEncodeWithUtf8(albumCoverFileName));
        audioAndVideoOperationConfig.setOutPutFileInfo(outputFileInfo);
        configList.add(audioAndVideoOperationConfig);

        CreateAudioAndVideoTaskRequest createAudioAndVideoTaskRequest = new CreateAudioAndVideoTaskRequest();
        createAudioAndVideoTaskRequest.setBucketName(bucketName);
        createAudioAndVideoTaskRequest.setSourceFileName(albumFileName);
        createAudioAndVideoTaskRequest.setOperationType(AvOperationTypeEnum.Getapic);
        createAudioAndVideoTaskRequest.setConfigList(configList);
        createAudioAndVideoTaskRequest.setNotifyUrl(UrlCodecUtil.dataEncodeWithUtf8(notificationUrl));
        createAudioAndVideoTaskRequest.setForce(1);
        createAudioAndVideoTaskRequest.setSeparate(0);

        String xmlBody = XmlRequestConstructHandler.convertToDifferentRootXml(createAudioAndVideoTaskRequest, AvOperationTypeEnum.Getapic.getValue());
        System.out.println(xmlBody);
        AudioAndVideoTaskRequestResult audioAndVideoTask = new AudioAndVideoTaskRequestResult();
        try {
            audioAndVideoTask = wosClient.createAudioAndVideoTask(createAudioAndVideoTaskRequest);
            System.out.println("persistentId: " + audioAndVideoTask.getPersistentId());
        } catch (WosException e) {
            System.out.println("Response Code: " + e.getResponseCode());
            System.out.println("Error Message: " + e.getErrorMessage());
            System.out.println("Error Code: " + e.getErrorCode());
            System.out.println("Request ID: " + e.getErrorRequestId());
            System.out.println("Host ID: " + e.getErrorHostId());
        } finally {
            // If you create only one object, comment the following code in the method before the last method is executed
            // You have to close wos client this object in the application end
            // if (wosClient != null) {
            //     try {
            //         /*
            //          * Close wos client
            //          */
            //         wosClient.close();
            //     } catch (IOException e) {
            //     }
            // }
        }
        return audioAndVideoTask.getPersistentId();
    }


    private static String CreateVideoSnapshotTask() {
        List<AudioAndVideoOperationConfig> configList = new ArrayList<>();
        AudioAndVideoOperationConfig audioAndVideoOperationConfig = new AudioAndVideoOperationConfig();

        // param list
        LinkedHashMap<String, String> operationParams = new LinkedHashMap<>();
        operationParams.put("format", "jpg");
        operationParams.put("offset", "5");
        audioAndVideoOperationConfig.setOperationParams(operationParams);

        // output info
        OutPutFileInfo outputFileInfo = new OutPutFileInfo();
        outputFileInfo.setOutputBucket(outputBucketName);
        outputFileInfo.setOutputKey(UrlCodecUtil.dataEncodeWithUtf8(outputSnapshotFileName));
        audioAndVideoOperationConfig.setOutPutFileInfo(outputFileInfo);
        configList.add(audioAndVideoOperationConfig);

        CreateAudioAndVideoTaskRequest createAudioAndVideoTaskRequest = new CreateAudioAndVideoTaskRequest();
        createAudioAndVideoTaskRequest.setBucketName(bucketName);
        createAudioAndVideoTaskRequest.setSourceFileName(objectKey);
        createAudioAndVideoTaskRequest.setOperationType(AvOperationTypeEnum.Vframe);
        createAudioAndVideoTaskRequest.setConfigList(configList);
        createAudioAndVideoTaskRequest.setNotifyUrl(UrlCodecUtil.dataEncodeWithUtf8(notificationUrl));
        createAudioAndVideoTaskRequest.setForce(1);
        createAudioAndVideoTaskRequest.setSeparate(0);

        String xmlBody = XmlRequestConstructHandler.convertToDifferentRootXml(createAudioAndVideoTaskRequest, AvOperationTypeEnum.Vframe.getValue());
        System.out.println(xmlBody);
        AudioAndVideoTaskRequestResult audioAndVideoTask = new AudioAndVideoTaskRequestResult();
        try {
            audioAndVideoTask = wosClient.createAudioAndVideoTask(createAudioAndVideoTaskRequest);
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

    private static AudioAndVideoTaskDetailResult getTaskInfoDetail(String persistentId) {
        AudioAndVideoTaskDetailResult audioAndVideoTaskResult = null;
        try {
            audioAndVideoTaskResult = wosClient.getAudioAndVideoTask(bucketName, persistentId, AvOperationTypeEnum.Avthumb);
            System.out.println(audioAndVideoTaskResult);
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
        return audioAndVideoTaskResult;
    }


    private static String CreateVideoConcatTask() {
        List<AudioAndVideoOperationConfig> configList = new ArrayList<>();


        AudioAndVideoOperationConfig audioAndVideoOperationConfig = new AudioAndVideoOperationConfig();
        // param list
        LinkedHashMap<String, String> operationParams = new LinkedHashMap<>();
        operationParams.put("format", "mp4");
        audioAndVideoOperationConfig.setOperationParams(operationParams);
        // List of side files
        List<String> fileList = new ArrayList<>();
        fileList.add(UrlCodecUtil.dataEncodeWithUtf8(inputSideFile1));
        fileList.add(UrlCodecUtil.dataEncodeWithUtf8(inputSideFile2));
        audioAndVideoOperationConfig.setFileList(fileList);
        // output file info
        OutPutFileInfo outputFileInfo = new OutPutFileInfo();
        outputFileInfo.setOutputBucket(outputBucketName);
        outputFileInfo.setOutputKey(UrlCodecUtil.dataEncodeWithUtf8(videoConcatOutputFileName));
        audioAndVideoOperationConfig.setOutPutFileInfo(outputFileInfo);

        configList.add(audioAndVideoOperationConfig);


        CreateAudioAndVideoTaskRequest createAudioAndVideoTaskRequest = new CreateAudioAndVideoTaskRequest();
        createAudioAndVideoTaskRequest.setBucketName(bucketName);
        createAudioAndVideoTaskRequest.setSourceFileName(objectKey);
        createAudioAndVideoTaskRequest.setOperationType(AvOperationTypeEnum.Avconcat);
        createAudioAndVideoTaskRequest.setConfigList(configList);
        createAudioAndVideoTaskRequest.setNotifyUrl(UrlCodecUtil.dataEncodeWithUtf8(notificationUrl));
        createAudioAndVideoTaskRequest.setForce(1);
        createAudioAndVideoTaskRequest.setSeparate(0);

        String xmlBody = XmlRequestConstructHandler.convertToDifferentRootXml(createAudioAndVideoTaskRequest, AvOperationTypeEnum.Avconcat.getValue());
        System.out.println(xmlBody);
        AudioAndVideoTaskRequestResult audioAndVideoTask = new AudioAndVideoTaskRequestResult();
        try {
            audioAndVideoTask = wosClient.createAudioAndVideoTask(createAudioAndVideoTaskRequest);
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
}
