package tests;

import config.ApiClient;
import io.qameta.allure.*;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import net.datafaker.Faker;
import org.junit.After;

import static org.apache.http.HttpStatus.*;

import org.junit.Test;
import static org.junit.Assert.*;

@Epic("API тесты для сервиса доставки") //раздел системы.
@Feature("Логин курьера") //компоненты
public class CourierLoginTest extends ApiClient {

    private Faker faker = new Faker();
    private String testLogin;
    private String invalidLogin;
    private String testPassword;
    private String invalidPassword;
    private final String testFirstName = "lays";

    @After
    public void tearDown() {
        if (testLogin != null && testPassword != null) {
            Response response = deleteTestCourier(testLogin, testPassword);
            if (response.statusCode() != SC_OK) {
                fail("Не удалось удалить тестового курьера: " + testLogin);
            }
        }
    }

    @Test
    @Story("Позитивные тесты авторизации") //Тип сценариев
    @Description("Проверка успешной авторизации с валидными данными")
    @DisplayName("Курьер может успешно авторизоваться")
    public void testLoginNew() {
        //подготовка данных
        testLogin = faker.letterify("??????"); // по непонятной мне причине строки не работают в аннотации @Before
        testPassword = faker.numerify("####");

        createTestCourier(testLogin, testPassword, testFirstName);
        Response response = loginTestCourier(testLogin, testPassword);

        // проверка статуса и тела ответа
        assertEquals(SC_OK, response.statusCode());
        assertNotNull("Должен вернуться ID курьера",response.jsonPath().getInt("id"));
    }

    @Test
    @Story("Негативные тесты авторизации") //Тип сценариев
    @Description("Попытка авторизации с неверными данными")
    @DisplayName("Попытка логина с неверным паролем")
    public void testLoginWithInvalidPassword() {
        //подготовка данных
        testLogin = faker.letterify("??????");
        testPassword = faker.numerify("####");
        invalidPassword = faker.numerify("#####");

        createTestCourier(testLogin, testPassword, testFirstName);
        Response response = loginTestCourier(testLogin, invalidPassword);
        JsonPath jsonPath = response.jsonPath();

        //проверка статуса и тела овтета
        assertEquals(SC_NOT_FOUND, response.statusCode());
        assertEquals("Учетная запись не найдена", jsonPath.getString("message"));
    }

    @Test
    @Story("Негативные тесты авторизации") //Тип сценариев
    @Description("Попытка авторизации с неверными данными")
    @DisplayName("Попытка логина с неверным пользователем")
    public void testLoginWithInvalidUser() {
        //подготовка данных
        testLogin = faker.letterify("??????");
        testPassword = faker.numerify("####");
        invalidLogin = faker.numerify("?????");

        createTestCourier(testLogin, testPassword, testFirstName);
        Response response = loginTestCourier(invalidLogin, testPassword);
        JsonPath jsonPath = response.jsonPath();

        //проверка статуса и тела овтет
        assertEquals(SC_NOT_FOUND, response.statusCode());
        assertEquals("Учетная запись не найдена", jsonPath.getString("message"));
    }

    @Test
    @Story("Негативные тесты авторизации") //Тип сценариев
    @Description("Попытка авторизации без обязательных полей")
    @DisplayName("Попытка авторизации без пароля")
    public void testLoginWithoutRequiredFieldsPassword() {
        testLogin = faker.letterify("??????");
        testPassword = faker.numerify("####");
        invalidPassword = "";

        createTestCourier(testLogin, testPassword, testFirstName);
        Response response = loginTestCourier(testLogin, invalidPassword);
        JsonPath jsonPath = response.jsonPath();

        assertEquals(SC_BAD_REQUEST, response.statusCode());
        assertEquals("Недостаточно данных для входа", jsonPath.getString("message"));
    }

        @Test
        @Story("Негативные тесты авторизации") //Тип сценариев
        @Description("Попытка авторизации без обязательных полей")
        @DisplayName("Попытка авторизации без логина")
        public void testLoginWithoutRequiredFieldsLogin() {
            testLogin = faker.letterify("??????");
            testPassword = faker.numerify("####");
            invalidLogin = "";

            createTestCourier(testLogin, testPassword, testFirstName);
            Response response = loginTestCourier(invalidLogin, testPassword);
            JsonPath jsonPath = response.jsonPath();

            assertEquals(SC_BAD_REQUEST, response.statusCode());
            assertEquals("Недостаточно данных для входа", jsonPath.getString("message"));
    }
}