package com.github;

import io.restassured.response.Response;
import org.json.JSONArray;
import org.json.JSONObject;
import utils.Config;

public class GithubGitBaseTest extends GithubBaseTest {

    protected void createBranch(String branchName, String shaToRef){
        String url = BASIC_API_URL + String.format("repos/%s/%s/git/refs",
                Config.getProperty("owner-name"),
                repositoryName);
        JSONObject body = new JSONObject();
        body.put("ref", "refs/heads/" + branchName);
        body.put("sha", shaToRef);

        post(url, body.toString(), 201);
    }


    protected String getLatestCommitSha(String branch){
        String url = BASIC_API_URL + String.format("repos/%s/%s/git/refs/heads/%s",
                Config.getProperty("owner-name"),
                repositoryName,
                branch);

        Response response = get(url, 200).extract().response();

        return new JSONObject(response.asPrettyString()).getJSONObject("object").getString("sha");
    }

    protected String createTree(String filePath, String baseTreeSha, String blobSha){
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

    protected String createBlob(){
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

    protected String createCommit(String message, String treeSha, String parentCommitSha){
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

    protected String getTreeSha(String latestCommitSha){
        String url = BASIC_API_URL + String.format("repos/%s/%s/git/commits/%s",
                Config.getProperty("owner-name"),
                repositoryName,
                latestCommitSha);

        Response response = get(url, 200).extract().response();

        return new JSONObject(response.asPrettyString()).getJSONObject("tree").getString("sha");
    }

    protected void updateBranchReference(String commitSha, String branch){
        String url = BASIC_API_URL + String.format("repos/%s/%s/git/refs/heads/%s",
                Config.getProperty("owner-name"),
                repositoryName,
                branch);

        JSONObject updateRef = new JSONObject();
        updateRef.put("sha", commitSha);

        patch(url, updateRef.toString(), 200);
    }

    protected String pushCommit(String filePath, String commitMessage, String branchCommitDoneOn){
        String latestCommitSha = getLatestCommitSha(branchCommitDoneOn);
        String latestTreeSha = getTreeSha(latestCommitSha);

        String blobSha = createBlob();
        String treeSha = createTree(filePath, latestTreeSha, blobSha);
        String newCommitSha = createCommit(commitMessage, treeSha, latestCommitSha);

        updateBranchReference(newCommitSha, branchCommitDoneOn);

        return newCommitSha;
    }
}
