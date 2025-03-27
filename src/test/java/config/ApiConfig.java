package config;

import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;
import org.junit.Before;
import org.junit.BeforeClass;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.anyOf;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assume.assumeTrue;

public class ApiConfig {
    @Before
    public void setUp() {
        RestAssured.baseURI = "https://qa-scooter.praktikum-services.ru";
        RestAssured.filters(new AllureRestAssured());
    }

    @BeforeClass
    public static void checkApiAvailable() {
        try {
            given()
                    .baseUri("https://qa-scooter.praktikum-services.ru")
                    .when().get("/")
                    .then().statusCode(anyOf(is(200), is(404), is(401)));
        } catch (Exception e) {
            assumeTrue("API недоступен: " + e.getMessage(), false);
        }
    }
}