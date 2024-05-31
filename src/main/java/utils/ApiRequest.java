package utils;

import netscape.javascript.JSObject;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.io.IOException;

public abstract class ApiRequest implements AutoCloseable {
    private final String accessToken;
    private final CloseableHttpClient client = HttpClients.createDefault();

    public ApiRequest(String accessToken){
        this.accessToken = accessToken;
    }

    public ApiResponse get(String endpoint) throws IOException {
        HttpGet httpGet = new HttpGet(endpoint);
        httpGet.addHeader("Authorization", "Bearer " + accessToken);

        ApiResponse apiResponse;
        try (CloseableHttpResponse httpResponse = client.execute(httpGet)){
            apiResponse = createApiResponse(httpResponse);
        }

        return apiResponse;
    }

    public ApiResponse post(String endpoint, JSObject body) throws IOException {
        HttpPost httpPost = new HttpPost(endpoint);
        httpPost.addHeader("Authorization", "Bearer " + accessToken);
        httpPost.addHeader("accept", "application/vnd.github+json");
        httpPost.setEntity(new StringEntity(body.toString()));

        ApiResponse apiResponse;
        try (CloseableHttpResponse httpResponse = client.execute(httpPost)){
            apiResponse = createApiResponse(httpResponse);
        }

        return apiResponse;
    }

    public abstract ApiResponse createApiResponse(CloseableHttpResponse httpResponse);

    @Override
    public void close() throws Exception {
        client.close();
    }


}
