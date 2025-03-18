package com.api.client;

import io.restassured.response.Response;

import static io.restassured.RestAssured.*;

public class ApiClient {

    private final String baseUrl;

    public ApiClient(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public Response getRequest(String endpoint) {
        return given()
                .baseUri(baseUrl)
                .when()
                .get(endpoint)
                .then()
                .extract()
                .response();
    }

    public Response postRequest(String endpoint, Object body) {
        return given()
                .baseUri(baseUrl)
                .header("Content-Type", "application/json")
                .body(body)
                .when()
                .post(endpoint)
                .then()
                .extract()
                .response();
    }

    public Response putRequest(String endpoint, Object body) {
        return given()
                .baseUri(baseUrl)
                .header("Content-Type", "application/json")
                .body(body)
                .when()
                .put(endpoint)
                .then()
                .extract()
                .response();
    }

    public Response deleteRequest(String endpoint) {
        return given()
                .baseUri(baseUrl)
                .when()
                .delete(endpoint)
                .then()
                .extract()
                .response();
    }
}
