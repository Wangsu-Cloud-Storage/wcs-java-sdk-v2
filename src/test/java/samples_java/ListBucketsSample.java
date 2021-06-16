package samples_java;

import com.wos.services.WosClient;
import com.wos.services.WosConfiguration;
import com.wos.services.exception.WosException;
import com.wos.services.model.WosBucket;

import java.io.IOException;
import java.util.List;

public class ListBucketsSample {

    private static final String endPoint = "https://your-endpoint";

    private static final String ak = "*** Provide your Access Key ***";

    private static final String sk = "*** Provide your Secret Key ***";

    private static WosClient wosClient;

    public static void main(String[] args)
            throws IOException {
        WosConfiguration config = new WosConfiguration();
        config.setSocketTimeout(30000);
        config.setConnectionTimeout(10000);
        config.setEndPoint(endPoint);
        try {
            /*
             * Constructs a wos client instance with your account for accessing WOS
             */
            wosClient = new WosClient(ak, sk, config);

            List<WosBucket> buckets = wosClient.listBuckets();
            for(WosBucket bucket : buckets){
                System.out.println("BucketName:" + bucket.getBucketName());
                System.out.println("CreationDate:" + bucket.getCreationDate());
                System.out.println("Endpoint:" + bucket.getEndpoint());
                System.out.println("----------------------------");
            }

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
}
