package com.github;

import org.apache.commons.lang3.RandomUtils;
import org.apache.http.HttpStatus;
import org.junit.jupiter.api.Test;
import utils.Config;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

public class GithubRepositoryApiTest extends GithubBaseTest{

    @Test
    public void createRepositoryTest() {
        String repositoryName = "Repository-create-test-" + RandomUtils.nextInt();
        String requestBody = "{\n" +
                "\"name\": \"" + repositoryName + "\"\n" +
                "}";

        verifyCreateRepositoryBadRequest(REPOS_API_URL);
        verifyCreateRepositoryNotAuthenticatedRequest(REPOS_API_URL);

        try {
            verifyCreateRepositoryGoodRequest(REPOS_API_URL, requestBody, repositoryName);
        } finally {
            delete(REPOS_OWNER_API_URL_PARTLY + Config.getProperty("owner-name") + "/" + repositoryName,
                    HttpStatus.SC_NO_CONTENT);
        }
    }

    @Test
    public void deleteRepositoryTest(){
        String repositoryName = "Repository-create-test-" + RandomUtils.nextInt();
        String requestBody = "{\n" +
                "\"name\": \"" + repositoryName + "\"\n" +
                "}";

        getBasicRequest(REPOS_API_URL).body(requestBody).post();
        String repositoryUrl = REPOS_OWNER_API_URL_PARTLY + Config.getProperty("owner-name") + "/" + repositoryName;
        verifyDeleteRepositoryRequest(repositoryName, HttpStatus.SC_NO_CONTENT);
        verifyRepositoryWasDeleted(repositoryUrl);
        verifyDeleteRepositoryRequest(repositoryName, HttpStatus.SC_NOT_FOUND); // verify 404 status code for DELETE
                                                                                // request in case repository not exists
    }

    private void verifyDeleteRepositoryRequest(String repositoryName, int expectedStatusCode){
        delete(REPOS_OWNER_API_URL_PARTLY + Config.getProperty("owner-name") + "/" + repositoryName,
                expectedStatusCode);
    }

    private void verifyRepositoryWasDeleted(String url){
        get(url, HttpStatus.SC_NOT_FOUND);
    }

    private void verifyCreateRepositoryBadRequest(String url){
        getBasicRequest(url)
                .post()
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_BAD_REQUEST);
    }

    private void verifyCreateRepositoryNotAuthenticatedRequest(String url){
        given()
                .baseUri(url)
                .header("accept", "application/vnd.github+json")
                .post()
                .then()
                .assertThat()
                .statusCode(HttpStatus.SC_UNAUTHORIZED);
    }

    private void verifyCreateRepositoryGoodRequest(String url, String requestBody, String repositoryName){
        post(url, requestBody, HttpStatus.SC_CREATED).body("name", equalTo(repositoryName));
    }
}
