package com.github;

import org.junit.jupiter.api.Test;
import utils.Config;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

public class GithubRepositoryApiTest extends GithubBaseTest{

    @Test
    public void createRepositoryTest() {
        String repositoryName = "Repository-create-test-" + Math.random();
        String requestBody = "{\n" +
                "\"name\": \"" + repositoryName + "\"\n" +
                "}";

        verifyCreateRepositoryBadRequest(REPOS_API_URL);
        verifyCreateRepositoryNotAuthenticatedRequest(REPOS_API_URL);

        try {
            verifyCreateRepositoryGoodRequest(REPOS_API_URL, requestBody, repositoryName);
        } finally {
            delete(REPOS_OWNER_API_URL_PARTLY + Config.getProperty("owner-name") + "/" + repositoryName,
                    204);
        }
    }

    @Test
    public void deleteRepositoryTest(){
        String repositoryName = "Repository-create-test-" + Math.random();
        String requestBody = "{\n" +
                "\"name\": \"" + repositoryName + "\"\n" +
                "}";

        getBasicRequest(REPOS_API_URL).body(requestBody).post();
        String repositoryUrl = REPOS_OWNER_API_URL_PARTLY + Config.getProperty("owner-name") + "/" + repositoryName;
        verifyDeleteRepositoryRequest(repositoryName, 204);
        verifyRepositoryWasDeleted(repositoryUrl);
        verifyDeleteRepositoryRequest(repositoryName, 404); // verify 404 status code for DELETE
                                                                                // request in case repository not exists
    }

    private void verifyDeleteRepositoryRequest(String repositoryName, int expectedStatusCode){
        delete(REPOS_OWNER_API_URL_PARTLY + Config.getProperty("owner-name") + "/" + repositoryName,
                expectedStatusCode);
    }

    private void verifyRepositoryWasDeleted(String url){
        get(url, 404);
    }

    private void verifyCreateRepositoryBadRequest(String url){
        getBasicRequest(url)
                .post()
                .then()
                .assertThat()
                .statusCode(400);
    }

    private void verifyCreateRepositoryNotAuthenticatedRequest(String url){
        given()
                .baseUri(url)
                .header("accept", "application/vnd.github+json")
                .post()
                .then()
                .assertThat()
                .statusCode(401);
    }

    private void verifyCreateRepositoryGoodRequest(String url, String requestBody, String repositoryName){
        post(url, requestBody, 201).body("name", equalTo(repositoryName));
    }
}
