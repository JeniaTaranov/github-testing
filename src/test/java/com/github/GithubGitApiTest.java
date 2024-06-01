package com.github;

import io.restassured.response.Response;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import utils.Config;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class GithubGitApiTest extends GithubBaseTest{
    private final static String repositoryName = "Commit-tests";

    @Test
    public void pushCommitTest(){
        String filePath = "file" + Math.random() + ".txt";
        String commitMessage = "A message which describes the commit";
        String latestCommitSha = getLatestCommitSha();
        String latestTreeSha = getTreeSha(latestCommitSha);

        String blobSha = createBlob();
        String treeSha = createTree(filePath, latestTreeSha, blobSha);
        String newCommitSha = createCommit(commitMessage, treeSha, latestCommitSha);
        updateBranchReference(newCommitSha);
        verifyCommitExists(newCommitSha);
        assertTrue(BranchReferenceIsUpdated(newCommitSha));
    }

    private void verifyCommitExists(String commitSha){
        String url =
                BASIC_API_URL + String.format("repos/%s/%s/git/commits/%s",
                        Config.getProperty("owner-name"),
                        repositoryName,
                        commitSha);

        get(url, 200);
    }

    private String createTree(String filePath, String baseTreeSha, String blobSha){
        String url =
                BASIC_API_URL + String.format("repos/%s/%s/git/trees",
                        Config.getProperty("owner-name"),
                        repositoryName);

        JSONObject tree = new JSONObject();
        tree.put("tree_base", baseTreeSha);

        JSONArray treeArray = new JSONArray();
        JSONObject treeItem = new JSONObject();
        treeItem.put("path", filePath);
        treeItem.put("mode", "100644");
        treeItem.put("type", "blob");
        treeItem.put("sha", blobSha);
        treeArray.put(treeItem);

        tree.put("tree", treeArray);

        Response response = post(url, tree.toString(), 201).extract().response();

        return new JSONObject(response.asPrettyString()).getString("sha");
    }

    private String createBlob(){
        String newFileContent = "File content for new commit";
        String url =
                BASIC_API_URL + String.format("repos/%s/%s/git/blobs",
                        Config.getProperty("owner-name"),
                        repositoryName);
        String contentBody = "{\n" +
                "\"content\": \"" + newFileContent + "\"\n" +
                "}";

        Response response = post(url, contentBody, 201).extract().response();

        return new JSONObject(response.asPrettyString()).getString("sha");
    }

    private String createCommit(String message, String treeSha, String parentCommitSha){
        String url =
                BASIC_API_URL + String.format("repos/%s/%s/git/commits",
                        Config.getProperty("owner-name"),
                        repositoryName);
        JSONObject commit = new JSONObject();
        commit.put("message", message);
        commit.put("tree", treeSha);
        commit.put("parents", new JSONArray().put(parentCommitSha));

        Response response = post(url, commit.toString(), 201).extract().response();

        return new JSONObject(response.asPrettyString()).getString("sha");
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

    private void updateBranchReference(String commitSha){
        String url = BASIC_API_URL + String.format("repos/%s/%s/git/refs/heads/%s",
                Config.getProperty("owner-name"),
                repositoryName,
                "main");

        JSONObject updateRef = new JSONObject();
        updateRef.put("sha", commitSha);

        patch(url, updateRef.toString(), 200);
    }

    private boolean BranchReferenceIsUpdated(String commitSha){
        String url = BASIC_API_URL + String.format("repos/%s/%s/git/refs/heads/%s",
                Config.getProperty("owner-name"),
                repositoryName,
                "main");

        Response response = get(url, 200).extract().response();

        return new JSONObject(response.asPrettyString()).getJSONObject("object").getString("sha").equals(commitSha);
    }
}
