package com.github;

import io.restassured.response.Response;
import org.apache.commons.lang3.RandomUtils;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import utils.Config;

public class GithubPullRequestApiTest extends GithubGitBaseTest {
    private String branchToMergeTo = "main";
    private String newBranch = "";

    @BeforeEach
    public void initBranch(){
        newBranch = "Random-feature-" + RandomUtils.nextInt();
        String latestSha = getLatestCommitSha(branchToMergeTo);
        createBranch(newBranch, latestSha);
    }

    @Test
    public void createPullRequestTest(){
        String filePath = "file-for-pr-" + RandomUtils.nextInt() + ".txt";
        String commitMessage = "A commit message for PR";
        pushCommit(filePath, commitMessage, newBranch);
        int pullNumber = createPullRequest(newBranch, branchToMergeTo);
    }

    private void verifyPullRequestExists(int pullNumber){

    }
    
    private int createPullRequest(String newBranch, String branchToMergeTo){
        String url = BASIC_API_URL + String.format("repos/%s/%s/pulls",
                Config.getProperty("owner-name"),
                repositoryName);

        JSONObject pullRequestData = new JSONObject();
        pullRequestData.put("title", "Pull request " + RandomUtils.nextInt());
        pullRequestData.put("head", newBranch);
        pullRequestData.put("base", branchToMergeTo);

        Response response = post(url, pullRequestData.toString(), 201).extract().response();

        return new JSONObject(response.asPrettyString()).getString("id");
    }
}
