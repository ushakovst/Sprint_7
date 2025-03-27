package tests;

import io.qameta.allure.*;
import org.junit.After;
import org.junit.Test;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

//почти все аннотации используются для структуризации Allure-отчета
@Epic("API тесты для сервиса доставки") //раздел системы.
@Feature("Создание курьера") //компоненты
public class CreateCourierTest extends BaseCourierTest {

    private String testLogin;
    private final String testPassword = "7658";
    private final String testFirstName = "lays";

    @After
    public void tearDown() {
        if (testLogin != null) {
            deleteTestCourier(testLogin, testPassword);
        }
    }

    @Test
    @Story("Позитивные тесты создания курьера") //Тип сценариев
    @Description("Проверка успешного создания курьера")
    public void testCreateCourierSuccess() {
        testLogin = generateUniqueLogin();

        given()
                .header("Content-type", "application/json")
                .body(String.format("{\"login\": \"%s\", \"password\": \"%s\", \"firstName\": \"%s\"}",
                        testLogin, testPassword, testFirstName))
                .when()
                .post("/api/v1/courier")
                .then()
                .statusCode(201)
                .body("ok", equalTo(true));
    }

    @Test
    @Story("Негативные тесты создания курьера") //Тип сценариев
    @Description("Попытка создания курьера с уже существующим логином")
    public void testCreateDuplicateCourier() {
        testLogin = generateUniqueLogin();
        createTestCourier(testLogin, testPassword, testFirstName);

        given()
                .header("Content-type", "application/json")
                .body(String.format("{\"login\": \"%s\", \"password\": \"%s\", \"firstName\": \"%s\"}",
                        testLogin, "differentPass", "Different Name"))
                .when()
                .post("/api/v1/courier")
                .then()
                .statusCode(409);
    }

    @Test
    @Story("Негативные тесты создания курьера") //Тип сценариев
    @Description("Попытка создания курьера без обязательных полей")
    public void testCreateCourierWithoutRequiredFields() {
        // Без логина
        given()
                .header("Content-type", "application/json")
                .body(String.format("{\"password\": \"%s\", \"firstName\": \"%s\"}",
                        testPassword, testFirstName))
                .when()
                .post("/api/v1/courier")
                .then()
                .statusCode(400);

        // Без пароля
        testLogin = generateUniqueLogin();
        given()
                .header("Content-type", "application/json")
                .body(String.format("{\"login\": \"%s\", \"firstName\": \"%s\"}",
                        testLogin, testFirstName))
                .when()
                .post("/api/v1/courier")
                .then()
                .statusCode(400);
    }
}