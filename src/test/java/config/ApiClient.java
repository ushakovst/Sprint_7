package config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.BeforeClass;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static config.Endpoints.*;
import static io.restassured.RestAssured.given;
import static org.junit.Assume.assumeTrue;

public class ApiClient{

    @BeforeClass
    public static void checkApi() {
        ApiConfig.init();
        assumeTrue("API недоступен", ApiConfig.checkApiAvailable());
    }

    protected Response loginTestCourier(String login, String password) {
        return given()
                .header("Content-type", "application/json")
                .body(Map.of("login", login, "password", password))
                .when()
                .post(COURIER_LOGIN);
    }

    protected Response createTestCourier(String login, String password, String name) {
        return given()
                .header("Content-type", "application/json")
                .body(Map.of("login", login, "password", password, "firstName", name))
                .when()
                .post(COURIER_CREATE);
    }

    protected Response createTestCourierWithoutLogin(String password, String name) {
        return given()
                .header("Content-type", "application/json")
                .body(Map.of("login", "", "password", password, "firstName", name))
                .when()
                .post(COURIER_CREATE);
    }

    protected Response createTestCourierWithoutPassword(String login, String name) {
        return given()
                .header("Content-type", "application/json")
                .body(Map.of("login", login, "password", "", "firstName", name))
                .when()
                .post(COURIER_CREATE);
    }

    protected Response deleteTestCourier(String login, String password) {
        // Получаем ID курьера для удаления, а для этого сначала надо залогиниться
        Response loginResponse = loginTestCourier(login,password);
        String courierId = loginResponse.jsonPath().getString("id");
        return given()
                    .header("Content-type", "application/json")
                    .pathParam("id", courierId)
                    .when()
                    .delete(COURIER_DELETE + "{id}");
    }

    protected int createTestOrder(String[] colors) {
        Gson gson = new GsonBuilder().create();
        List<String> colorsList = colors != null ? Arrays.asList(colors) : null;
        OrderPOJO order = new OrderPOJO(
                "Name",
                "User",
                "Moscow",
                "Novokuznetskaya",
                "+79998887766",
                1,
                "2025-12-31",
                "Test order",
                colorsList
        );
        String requestBody = gson.toJson(order);

        Response response = given()
                .header("Content-type", "application/json")
                .body(requestBody)
                .when()
                .post(CREATING_AN_ORDER);
        return response.jsonPath().getInt("track");
    }

    protected Response getTestOrder(int trackNumber) {
        return given()
                .header("Content-type", "application/json")
                .queryParam("t", trackNumber)
                .when()
                .get(RECEICE_AN_ORDER);
    }

    protected Response cancelTestOrder(int track) {
        return given()
                .contentType(ContentType.JSON)
                .pathParam("track", track)
                .when()
                .put(CANCEL_AN_ORDER + "?track=" + "{track}"); // не получилось найти более "изящного" способа
    }

    protected Response getListOfOrders() {
        return given()
                .header("Content-type", "application/json")
                .when()
                .get(CREATING_AN_ORDER);
    }
}