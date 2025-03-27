package tests;

import config.ApiConfig;
import io.qameta.allure.Step;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;

public class BaseOrderTest extends ApiConfig {

    @Step("Создание тестового заказа")
    protected int createTestOrder(String[] colors) {
        String requestBody = String.format(
                "{\"firstName\": \"Name\", \"lastName\": \"User\", \"address\": \"Moscow\", " +
                        "\"metroStation\": \"Novokuznetskaya\", \"phone\": \"+79998887766\", \"rentTime\": 1, " +
                        "\"deliveryDate\": \"2025-12-31\", \"comment\": \"Test order\", " +
                        "\"color\": %s}",
                colors != null ? "[\"" + String.join("\",\"", colors) + "\"]" : "[]");

        Response response = given()
                .header("Content-type", "application/json")
                .body(requestBody)
                .when()
                .post("/api/v1/orders")
                .then()
                .statusCode(201)
                .extract().response();

        return response.jsonPath().getInt("track");
    }
}