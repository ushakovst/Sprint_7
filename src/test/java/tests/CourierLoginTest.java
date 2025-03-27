package tests;

import io.qameta.allure.*;
import org.junit.After;
import org.junit.Test;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@Epic("API тесты для сервиса доставки") //раздел системы.
@Feature("Логин курьера") //компоненты
public class CourierLoginTest extends BaseCourierTest {

    private String testLogin;
    private final String testPassword = "7658";
    private final String testFirstName = "lays";

    @After
    public void tearDown() {
        if (testLogin != null) {
            try {
                deleteTestCourier(testLogin, testPassword);
            } catch (Exception e) {
                System.err.println("Ошибка при удалении курьера: " + e.getMessage());
            }
        }
    }

    @Test
    @Story("Позитивные тесты авторизации") //Тип сценариев
    @Description("Проверка успешной авторизации с валидными данными")
    public void testLoginSuccess() {
        testLogin = generateUniqueLogin(); // по непонятной мне причине строки 32 и 33 не работают в аннотации @Before
        createTestCourier(testLogin, testPassword, testFirstName);
        given()
                .header("Content-type", "application/json")
                .body(String.format("{\"login\": \"%s\", \"password\": \"%s\"}",
                        testLogin, testPassword))
                .when()
                .post("/api/v1/courier/login")
                .then()
                .statusCode(200)
                .body("id", notNullValue());
    }

    @Test
    @Story("Негативные тесты авторизации") //Тип сценариев
    @Description("Попытка авторизации с неверными данными")
    public void testLoginWithInvalidCredentials() {
        testLogin = generateUniqueLogin();
        createTestCourier(testLogin, testPassword, testFirstName);
        // Неверный пароль
        given()
                .header("Content-type", "application/json")
                .body(String.format("{\"login\": \"%s\", \"password\": \"7777\"}",
                        testLogin))
                .when()
                .post("/api/v1/courier/login")
                .then()
                .statusCode(404);
        //.body("message", equalTo("Учетная запись не найдена"));

        // Несуществующий пользователь
        given()
                .header("Content-type", "application/json")
                .body("{\"login\": \"nonex\", \"password\": \"1234\"}")
                .when()
                .post("/api/v1/courier/login")
                .then()
                .statusCode(404);
    }

    @Test
    @Story("Негативные тесты авторизации") //Тип сценариев
    @Description("Попытка авторизации без обязательных полей")
    public void testLoginWithoutRequiredFields() {
        testLogin = generateUniqueLogin();
        createTestCourier(testLogin, testPassword, testFirstName);
        // Без пароля
        given()
                .header("Content-type", "application/json")
                .body(String.format("{\"login\": \"%s\", \"password\": \"\"}", testLogin))
                .when()
                .post("/api/v1/courier/login")
                .then()
                .statusCode(400);

        // Без логина
        given()
                .header("Content-type", "application/json")
                .body(String.format("{\"login\": \"\", \"password\": \"%s\"}", testPassword))
                .when()
                .post("/api/v1/courier/login")
                .then()
                .statusCode(400);
    }
}