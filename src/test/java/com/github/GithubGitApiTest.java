package com.github;

import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import utils.Config;

public class GithubGitApiTest extends GithubBaseTest{
    private final static String repositoryName = "Commit-tests";
    private final static String requestBody = "{\n" +
            "\"name\": \"" + repositoryName + "\"\n" +
            "}";


    @Test
    public void gitCommitTest(){
        String url =
                BASIC_API_URL + String.format("repos/%s/%s/git/commits",
                        Config.getProperty("owner-name"),
                        repositoryName);
        String commitMessage = "A message which describes the commit";
        String latestCommitSha = getLatestCommitSha();
        String treeSha = getTreeSha(latestCommitSha);

        verifyCreateCommit(url, commitMessage, treeSha);
    }

    private ValidatableResponse verifyCreateCommit(String url, String message, String treeSha){
        String requestBody = "{\n" +
                "\"message\": \"" + message + "\",\n" +
                "\"tree\": \"" + treeSha + "\"\n" +
                "}";
        return post(url, requestBody, 201);
    }

    private String getLatestCommitSha(){
        String url = BASIC_API_URL + String.format("repos/%s/%s/git/refs/heads/%s",
                Config.getProperty("owner-name"),
                repositoryName,
                "main");

        Response response = get(url, 200).extract().response();

        return new JSONObject(response.asPrettyString()).getJSONObject("object").getString("sha");
    }

    private String getTreeSha(String latestCommitSha){
        String url = BASIC_API_URL + String.format("repos/%s/%s/git/commits/%s",
                Config.getProperty("owner-name"),
                repositoryName,
                latestCommitSha);

        Response response = get(url, 200).extract().response();

        return new JSONObject(response.asPrettyString()).getJSONObject("tree").getString("sha");
    }
}
