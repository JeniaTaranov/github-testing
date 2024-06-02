package com.github;

import io.restassured.response.Response;
import org.apache.commons.lang3.RandomUtils;
import org.apache.http.HttpStatus;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import utils.Config;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class GithubGitApiTest extends GithubGitBaseTest {
    private static String branchName = "main";

    @Test
    public void pushCommitTest(){
        String newBranch = "feature/random-" + RandomUtils.nextInt();
        String latestSha = getLatestCommitSha(branchName);
        createBranch(newBranch, latestSha);

        String filePath = "file-" + RandomUtils.nextInt() + ".txt";
        String commitMessage = "A message which describes the commit";

        String newCommitSha = pushCommit(filePath, commitMessage, newBranch);
        verifyCommitExists(newCommitSha);
        assertTrue(branchReferenceIsUpdated(newCommitSha, newBranch));
    }

    @Test
    public void createBranchTest(){
        String latestCommitSha = getLatestCommitSha(branchName);
        branchName = "feature-" + RandomUtils.nextInt();

        createBranchWithoutSha(branchName);
        createBranchWithoutName(latestCommitSha);
        createBranch(branchName, latestCommitSha);
        assertTrue(branchIsCreated(branchName, latestCommitSha));
    }

    private boolean branchIsCreated(String branchName, String expectedSha){
        return getLatestCommitSha(branchName).equals(expectedSha);
    }

    private void createBranchWithoutSha(String branchName){
        String url = BASIC_API_URL + String.format("repos/%s/%s/git/refs",
                Config.getProperty("owner-name"),
                repositoryName);
        JSONObject body = new JSONObject();
        body.put("ref", "refs/heads/" + branchName);

        post(url, body.toString(), HttpStatus.SC_UNPROCESSABLE_ENTITY);
    }

    private void createBranchWithoutName(String shaToRef){
        String url = BASIC_API_URL + String.format("repos/%s/%s/git/refs",
                Config.getProperty("owner-name"),
                repositoryName);
        JSONObject body = new JSONObject();
        body.put("sha", shaToRef);

        post(url, body.toString(), HttpStatus.SC_UNPROCESSABLE_ENTITY);
    }

    private void verifyCommitExists(String commitSha){
        String url =
                BASIC_API_URL + String.format("repos/%s/%s/git/commits/%s",
                        Config.getProperty("owner-name"),
                        repositoryName,
                        commitSha);

        get(url, HttpStatus.SC_OK);
    }

    private boolean branchReferenceIsUpdated(String commitSha, String branch){
        String url = BASIC_API_URL + String.format("repos/%s/%s/git/refs/heads/%s",
                Config.getProperty("owner-name"),
                repositoryName,
                branch);

            Response response = get(url, HttpStatus.SC_OK).extract().response();

        return new JSONObject(response.asPrettyString()).getJSONObject("object").getString("sha").equals(commitSha);
    }
}
