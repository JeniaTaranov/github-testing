package com.github;

import org.apache.http.client.methods.CloseableHttpResponse;
import utils.ApiRequest;
import utils.ApiResponse;

public class GithubApiRequest extends ApiRequest {

    public GithubApiRequest(String accessToken) {
        super(accessToken);
    }

    @Override
    public ApiResponse createApiResponse(CloseableHttpResponse httpResponse) {
        return null;
    }

}
