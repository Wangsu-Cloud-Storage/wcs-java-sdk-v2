/**
 * Copyright 2019 Huawei Technologies Co.,Ltd.
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use
 * this file except in compliance with the License.  You may obtain a copy of the
 * License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed
 * under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
 * CONDITIONS OF ANY KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations under the License.
 */
package samples_java;

import com.wos.services.WosClient;
import com.wos.services.WosConfiguration;
import com.wos.services.exception.WosException;
import com.wos.services.model.HttpMethodEnum;
import com.wos.services.model.TemporarySignatureRequest;
import com.wos.services.model.TemporarySignatureResponse;
import okhttp3.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


public class TemporarySignatureSample {

	private static final String endPoint = "https://your-endpoint";

	private static final String ak = "*** Provide your Access Key ***";

	private static final String sk = "*** Provide your Secret Key ***";

	private static WosClient wosClient;

	private static String bucketName = "my-wos-bucket-demo";

	private static String regionName = "my-wos-region-demo";

	private static String objectKey = "my-wos-object-key-demo";


	private static OkHttpClient httpClient = new OkHttpClient.Builder().followRedirects(false)
			.retryOnConnectionFailure(false).cache(null).build();


	public static void main(String[] args) {
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
			 * Create object
			 */
			doCreateObject();

			/*
			 * Get object
			 */
			doGetObject();

			/*
			 * Delete object
			 */
			doDeleteObject();

		} catch (WosException e) {
			System.out.println("Response Code: " + e.getResponseCode());
			System.out.println("Error Message: " + e.getErrorMessage());
			System.out.println("Error Code:       " + e.getErrorCode());
			System.out.println("Request ID:      " + e.getErrorRequestId());
			System.out.println("Host ID:           " + e.getErrorHostId());
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally
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

	private static void getResponse(Request request) throws IOException {
		Call c = httpClient.newCall(request);
		Response res = c.execute();
		System.out.println("\tStatus:" + res.code());
		System.out.println("\theaders:" + res.headers().toString());
		if (res.body() != null) {
			String content = res.body().string();
			if (content == null || content.trim().equals("")) {
				System.out.println("\n");
			} else {
				System.out.println("\tContent:" + content + "\n\n");
			}
		} else {
			System.out.println("\n");
		}
		res.close();
	}

	private static Request.Builder getBuilder(TemporarySignatureResponse res) {
		Request.Builder builder = new Request.Builder();
		for (Map.Entry<String, String> entry : res.getActualSignedRequestHeaders().entrySet()) {
			builder.header(entry.getKey(), entry.getValue());
		}
		return builder.url(res.getSignedUrl());
	}

	private static void doDeleteObject() throws IOException {
		TemporarySignatureRequest req = new TemporarySignatureRequest(HttpMethodEnum.DELETE, 300);
		req.setBucketName(bucketName);
		req.setObjectKey(objectKey);
		TemporarySignatureResponse res = wosClient.createTemporarySignature(req);
		System.out.println("Deleting object using temporary signature url:");
		System.out.println("\t" + res.getSignedUrl());

		getResponse(getBuilder(res).delete().build());
	}

	private static void doGetObject() throws WosException, IOException {
		TemporarySignatureRequest req = new TemporarySignatureRequest(HttpMethodEnum.GET, 300);
		req.setBucketName(bucketName);
		req.setObjectKey(objectKey);
		TemporarySignatureResponse res = wosClient.createTemporarySignature(req);
		System.out.println("Getting object using temporary signature url:");
		System.out.println("\t" + res.getSignedUrl());
		getResponse(getBuilder(res).get().build());
	}

	private static void doCreateObject() throws WosException, IOException {
		Map<String, String> headers = new HashMap<String, String>();
		String contentType = "text/plain";
		headers.put("Content-Type", contentType);

		TemporarySignatureRequest req = new TemporarySignatureRequest(HttpMethodEnum.PUT, 300);
		req.setBucketName(bucketName);
		req.setObjectKey(objectKey);
		req.setHeaders(headers);

		TemporarySignatureResponse res = wosClient.createTemporarySignature(req);

		System.out.println("Createing object using temporary signature url:");
		System.out.println("\t" + res.getSignedUrl());
		Request.Builder builder = getBuilder(res);
		builder.put(RequestBody.create(MediaType.parse(contentType), "Hello WOS".getBytes("UTF-8")));
		getResponse(builder.build());
	}


}
