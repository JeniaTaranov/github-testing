package com.github;

import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import utils.Config;

import static io.restassured.RestAssured.given;

@Execution(ExecutionMode.CONCURRENT)
public abstract class GithubBaseTest {
    public final static String ACCESS_TOKEN = Config.getProperty("access-token");
    public final static String BASIC_API_URL = "https://api.github.com/";
    public final static String REPOS_API_URL = BASIC_API_URL + "user/repos";
    public final static String REPOS_OWNER_API_URL_PARTLY = BASIC_API_URL + "repos/";
    public final static String repositoryName = "Commit-tests";

    public static RequestSpecification getBasicRequest(String url){
        return given()
                .baseUri(url)
                .header("Authorization", "Bearer " + ACCESS_TOKEN)
                .header("accept", "application/vnd.github+json");
    }

    public static ValidatableResponse get(String url, int expectedStatusCode){
        return getBasicRequest(url)
                .get()
                .then()
                .assertThat()
                .statusCode(expectedStatusCode);
    }

    public static ValidatableResponse post(String url, String requestBody, int expectedStatusCode){
        return getBasicRequest(url)
                .body(requestBody)
                .post()
                .then()
                .assertThat()
                .statusCode(expectedStatusCode);
    }

    public static ValidatableResponse patch(String url, String requestBody, int expectedStatusCode){
        return getBasicRequest(url)
                .body(requestBody)
                .patch()
                .then()
                .assertThat()
                .statusCode(expectedStatusCode);
    }

    public static ValidatableResponse delete(String url, int expectedStatusCode){
        return getBasicRequest(url)
                .delete()
                .then()
                .statusCode(expectedStatusCode);
    }
}
