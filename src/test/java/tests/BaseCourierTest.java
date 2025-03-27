package tests;

import config.ApiConfig;
import io.qameta.allure.Step;
import io.restassured.response.Response;

import java.util.UUID;

import static io.restassured.RestAssured.given;

public class BaseCourierTest extends ApiConfig {

    @Step("Создание тестового курьера")
    protected String createTestCourier(String login, String password, String firstName) {
        given()
                .header("Content-type", "application/json")
                .body(String.format("{\"login\": \"%s\", \"password\": \"%s\", \"firstName\": \"%s\"}",
                        login, password, firstName))
                .when()
                .post("/api/v1/courier")
                .then()
                .statusCode(201);
        return login;
    }

    @Step("Удаление тестового курьера")
    protected void deleteTestCourier(String login, String password) {
        // Получаем ID курьера для удаления, а для этого сначала надо залогиниться
        Response loginResponse = given()
                .header("Content-type", "application/json")
                .body(String.format("{\"login\": \"%s\", \"password\": \"%s\"}", login, password))
                .when()
                .post("/api/v1/courier/login");

        if (loginResponse.statusCode() == 200) {
            //парсим json-ответ и извлекаем значение поля id из ответа
            String courierId = loginResponse.jsonPath().getString("id");

            given()
                    .header("Content-type", "application/json")
                    .pathParam("id", courierId)
                    .when()
                    .delete("/api/v1/courier/{id}")
                    .then()
                    .statusCode(200);
        }
    }

    //необходимо, чтобы исключать конфликты при повторных запусках
    @Step("Генерация уникального 6-символьного буквенного логина")
    protected String generateUniqueLogin() {
        String uuid = UUID.randomUUID()
                .toString()
                .replace("-", "")
                .replaceAll("[^a-zA-Z]", "");
        return uuid.substring(0, 6).toLowerCase();
    }
}