package com.github;

import io.restassured.response.Response;
import org.apache.http.HttpStatus;
import org.json.JSONArray;
import org.json.JSONObject;

public class GithubGitBaseTest extends GithubBaseTest {

    protected String createBranch(String branchName, String shaToRef){
        String url = BASIC_API_URL + String.format("repos/%s/%s/git/refs",
                OWNER_NAME,
                repositoryName);
        JSONObject body = new JSONObject();
        body.put("ref", "refs/heads/" + branchName);
        body.put("sha", shaToRef);

        Response response = post(url, body.toString(), HttpStatus.SC_CREATED).extract().response();

        return new JSONObject(response.asPrettyString()).getString("ref");
    }

    protected void deleteBranch(String branchRef){
        String url = BASIC_API_URL + String.format("repos/%s/%s/git/%s",
                OWNER_NAME,
                repositoryName,
                branchRef);

        delete(url, HttpStatus.SC_NO_CONTENT);
    }

    protected static String getLatestCommitSha(String branch){
        String url = BASIC_API_URL + String.format("repos/%s/%s/git/refs/heads/%s",
                OWNER_NAME,
                repositoryName,
                branch);

        Response response = get(url, HttpStatus.SC_OK).extract().response();

        return new JSONObject(response.asPrettyString()).getJSONObject("object").getString("sha");
    }

    protected static String createTree(String filePath, String baseTreeSha, String blobSha){
        String url =
                BASIC_API_URL + String.format("repos/%s/%s/git/trees",
                        OWNER_NAME,
                        repositoryName);

        JSONObject tree = new JSONObject();
        if (baseTreeSha != null) {
            tree.put("tree_base", baseTreeSha);
        }

        JSONArray treeArray = new JSONArray();
        JSONObject treeItem = new JSONObject();
        treeItem.put("path", filePath);
        treeItem.put("mode", "100644");
        treeItem.put("type", "blob");
        treeItem.put("sha", blobSha);
        treeArray.put(treeItem);

        tree.put("tree", treeArray);

        Response response = post(url, tree.toString(), HttpStatus.SC_CREATED).extract().response();

        return new JSONObject(response.asPrettyString()).getString("sha");
    }

    protected static String createBlob(){
        String newFileContent = "File content for new commit";
        String url =
                BASIC_API_URL + String.format("repos/%s/%s/git/blobs",
                        OWNER_NAME,
                        repositoryName);
        String contentBody = "{\n" +
                "\"content\": \"" + newFileContent + "\"\n" +
                "}";

        Response response = post(url, contentBody, HttpStatus.SC_CREATED).extract().response();

        return new JSONObject(response.asPrettyString()).getString("sha");
    }

    protected static String createCommit(String message, String treeSha, String parentCommitSha){
        String url =
                BASIC_API_URL + String.format("repos/%s/%s/git/commits",
                        OWNER_NAME,
                        repositoryName);
        JSONObject commit = new JSONObject();
        commit.put("message", message);
        commit.put("tree", treeSha);
        if (parentCommitSha != null) {
            commit.put("parents", new JSONArray().put(parentCommitSha));
        }

        Response response = post(url, commit.toString(), HttpStatus.SC_CREATED).extract().response();

        return new JSONObject(response.asPrettyString()).getString("sha");
    }

    protected static String getTreeSha(String latestCommitSha){
        String url = BASIC_API_URL + String.format("repos/%s/%s/git/commits/%s",
                OWNER_NAME,
                repositoryName,
                latestCommitSha);

        Response response = get(url, HttpStatus.SC_OK).extract().response();

        return new JSONObject(response.asPrettyString()).getJSONObject("tree").getString("sha");
    }

    protected static void updateBranchReference(String commitSha, String branch){
        String url = BASIC_API_URL + String.format("repos/%s/%s/git/refs/heads/%s",
                OWNER_NAME,
                repositoryName,
                branch);

        JSONObject updateRef = new JSONObject();
        updateRef.put("sha", commitSha);

        patch(url, updateRef.toString(), HttpStatus.SC_OK);
    }


    protected static String pushCommit(String filePath, String commitMessage, String branchCommitDoneOn){
        String latestCommitSha = getLatestCommitSha(branchCommitDoneOn);
        String latestTreeSha = getTreeSha(latestCommitSha);

        String blobSha = createBlob();
        String treeSha = createTree(filePath, latestTreeSha, blobSha);
        String newCommitSha = createCommit(commitMessage, treeSha, latestCommitSha);

        updateBranchReference(newCommitSha, branchCommitDoneOn);

        return newCommitSha;
    }
}
