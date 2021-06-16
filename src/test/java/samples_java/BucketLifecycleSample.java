package samples_java;

import com.wos.services.WosClient;
import com.wos.services.WosConfiguration;
import com.wos.services.exception.WosException;
import com.wos.services.model.*;

import java.io.IOException;

public class BucketLifecycleSample {

    private static final String endPoint = "https://your-endpoint";

    private static final String ak = "*** Provide your Access Key ***";

    private static final String sk = "*** Provide your Secret Key ***";

    private static WosClient wosClient;

    private static String bucketName = "my-wos-bucket-demo";

    private static String objectKey = "my-wos-object-key-demo";

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
            doBucketLifecyclePrefix();
            doBucketLifecycleWholeBucket();

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

    private static void doBucketLifecyclePrefix()
            throws WosException {
        final String ruleId = "delete obsoleted files";
        final String matchPrefix = "obsoleted/";

        StringBuffer sb = new StringBuffer();

        LifecycleConfiguration lifecycleConfig = new LifecycleConfiguration();
        LifecycleConfiguration.Rule rule = lifecycleConfig.new Rule();
        rule.setEnabled(true);
        rule.setId(ruleId);
        rule.setPrefix(matchPrefix);
        LifecycleConfiguration.Expiration expiration = lifecycleConfig.new Expiration();
        expiration.setDays(10);

        rule.setExpiration(expiration);
        lifecycleConfig.addRule(rule);

        sb.append("Setting bucket lifecycle\n\n");
        HeaderResponse headerResponse = wosClient.setBucketLifecycleConfiguration(bucketName, lifecycleConfig);
        System.out.println(headerResponse);

        sb.append("Getting bucket lifecycle:");
        LifecycleConfiguration result = wosClient.getBucketLifecycleConfiguration(bucketName);
        for (LifecycleConfiguration.Rule r: result.getRules()) {
            sb.append("\tRule: Id=" + r.getId() + ", Prefix=" + r.getPrefix() + ", Status=" + r.getEnabled() + ", ExpirationDays="
                    + r.getExpiration().getDays());
        }
        sb.append("Deleting bucket lifecycle\n\n");
        System.out.println(sb.toString());
        wosClient.deleteBucketLifecycleConfiguration(bucketName);
    }

    private static void doBucketLifecycleWholeBucket()
            throws WosException {
        final String ruleId = "delete whole bucket files";

        StringBuffer sb = new StringBuffer();

        LifecycleConfiguration lifecycleConfig = new LifecycleConfiguration();
        LifecycleConfiguration.Rule rule = lifecycleConfig.new Rule();
        rule.setEnabled(true);
        rule.setId(ruleId);
        LifecycleConfiguration.Expiration expiration = lifecycleConfig.new Expiration();
        expiration.setDays(10);

        rule.setExpiration(expiration);
        lifecycleConfig.addRule(rule);

        sb.append("Setting bucket lifecycle\n\n");
        HeaderResponse headerResponse = wosClient.setBucketLifecycleConfiguration(bucketName, lifecycleConfig);
        System.out.println(headerResponse);

        sb.append("Getting bucket lifecycle:");
        LifecycleConfiguration result = wosClient.getBucketLifecycleConfiguration(bucketName);
        for (LifecycleConfiguration.Rule r: result.getRules()) {
            sb.append("\tRule: Id=" + r.getId() + ", Prefix=" + r.getPrefix() + ", Status=" + r.getEnabled() + ", ExpirationDays="
                    + r.getExpiration().getDays());
        }
        sb.append("Deleting bucket lifecycle\n\n");
        System.out.println(sb.toString());
        wosClient.deleteBucketLifecycleConfiguration(bucketName);
    }
}
