package tests;

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
public class CourierLoginTest extends BaseTest {

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
        testLogin = faker.letterify("??????"); // по непонятной мне причине строки не работают в аннотации @Before
        testPassword = faker.numerify("####");

        createTestCourier(testLogin, testPassword, testFirstName);
        Response response = loginTestCourier(testLogin, testPassword);

        // Assert
        assertEquals(SC_OK, response.statusCode());
        assertNotNull(response.jsonPath().getInt("id"));
    }

    @Test
    @Story("Негативные тесты авторизации") //Тип сценариев
    @Description("Попытка авторизации с неверными данными")
    @DisplayName("Попытка логина с неверным паролем: код состояния")
    public void testLoginWithInvalidPasswordCheckStatusCode() {
        testLogin = faker.letterify("??????");
        testPassword = faker.numerify("####");
        invalidPassword = faker.numerify("#####");

        createTestCourier(testLogin, testPassword, testFirstName);
        Response response = loginTestCourier(testLogin, invalidPassword);

        assertEquals(SC_NOT_FOUND, response.statusCode());
    }

    @Test
    @Story("Негативные тесты авторизации") //Тип сценариев
    @Description("Попытка авторизации с неверными данными")
    @DisplayName("Попытка логина с неверным паролем: тело ответа")
    public void testLoginWithInvalidPasswordCheckBody() {
        testLogin = faker.letterify("??????");
        testPassword = faker.numerify("####");
        invalidPassword = faker.numerify("#####");

        createTestCourier(testLogin, testPassword, testFirstName);
        Response response = loginTestCourier(testLogin, invalidPassword);

        JsonPath jsonPath = response.jsonPath();
        assertTrue("Поле 'message' отсутствует в ответе", jsonPath.get("message") != null);
        assertEquals("Учетная запись не найдена", jsonPath.getString("message"));
    }

    @Test
    @Story("Негативные тесты авторизации") //Тип сценариев
    @Description("Попытка авторизации с неверными данными")
    @DisplayName("Попытка логина с неверным пользователем: код состояния")
    public void testLoginWithInvalidUserCheckStatusCode() {
        testLogin = faker.letterify("??????");
        testPassword = faker.numerify("####");
        invalidLogin = faker.numerify("?????");

        createTestCourier(testLogin, testPassword, testFirstName);
        Response response = loginTestCourier(invalidLogin, testPassword);

        assertEquals(SC_NOT_FOUND, response.statusCode());
    }

    @Test
    @Story("Негативные тесты авторизации") //Тип сценариев
    @Description("Попытка авторизации с неверными данными")
    @DisplayName("Попытка логина с неверным пользователем: тело ответа")
    public void testLoginWithInvalidUserCheckBody() {
        testLogin = faker.letterify("??????");
        testPassword = faker.numerify("####");
        invalidLogin = faker.numerify("?????");

        createTestCourier(testLogin, testPassword, testFirstName);
        Response response = loginTestCourier(invalidLogin, testPassword);

        JsonPath jsonPath = response.jsonPath();
        assertTrue("Поле 'message' отсутствует в ответе", jsonPath.get("message") != null);
        assertEquals("Учетная запись не найдена", jsonPath.getString("message"));
    }

    @Test
    @Story("Негативные тесты авторизации") //Тип сценариев
    @Description("Попытка авторизации без обязательных полей")
    @DisplayName("Попытка авторизации без пароля: код состояния")
    public void testLoginWithoutRequiredFieldsPasswordCheckStatusCode() {
        testLogin = faker.letterify("??????");
        testPassword = faker.numerify("####");
        invalidPassword = "";

        createTestCourier(testLogin, testPassword, testFirstName);
        Response response = loginTestCourier(testLogin, invalidPassword);

        assertEquals(SC_BAD_REQUEST, response.statusCode());
    }

    @Test
    @Story("Негативные тесты авторизации") //Тип сценариев
    @Description("Попытка авторизации без обязательных полей")
    @DisplayName("Попытка авторизации без пароля: тело ответа")
    public void testLoginWithoutRequiredFieldsPasswordCheckBody() {
        testLogin = faker.letterify("??????");
        testPassword = faker.numerify("####");
        invalidPassword = "";

        createTestCourier(testLogin, testPassword, testFirstName);
        Response response = loginTestCourier(testLogin, invalidPassword);
        // Без пароля
        JsonPath jsonPath = response.jsonPath();
        assertTrue("Поле 'message' отсутствует в ответе", jsonPath.get("message") != null);
        assertEquals("Недостаточно данных для входа", jsonPath.getString("message"));
    }

        @Test
        @Story("Негативные тесты авторизации") //Тип сценариев
        @Description("Попытка авторизации без обязательных полей")
        @DisplayName("Попытка авторизации без логина: код состояния")
        public void testLoginWithoutRequiredFieldsLoginCheckStatusCode() {
            testLogin = faker.letterify("??????");
            testPassword = faker.numerify("####");
            invalidLogin = "";

            createTestCourier(testLogin, testPassword, testFirstName);
            Response response = loginTestCourier(invalidLogin, testPassword);

            assertEquals(SC_BAD_REQUEST, response.statusCode());
    }

    @Test
    @Story("Негативные тесты авторизации") //Тип сценариев
    @Description("Попытка авторизации без обязательных полей: тело запроса")
    @DisplayName("Попытка авторизации без логина")
    public void testLoginWithoutRequiredFieldsLoginCheckBody() {
        testLogin = faker.letterify("??????");
        testPassword = faker.numerify("####");
        invalidLogin = "";

        createTestCourier(testLogin, testPassword, testFirstName);
        Response response = loginTestCourier(invalidLogin, testPassword);

        JsonPath jsonPath = response.jsonPath();
        assertTrue("Поле 'message' отсутствует в ответе", jsonPath.get("message") != null);
        assertEquals("Недостаточно данных для входа", jsonPath.getString("message"));
    }
}