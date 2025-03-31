package tests;

import io.qameta.allure.*;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import net.datafaker.Faker;
import org.junit.After;
import org.junit.Test;

import static org.apache.http.HttpStatus.*;
import static org.junit.Assert.*;

//почти все аннотации используются для структуризации Allure-отчета
@Epic("API тесты для сервиса доставки") //раздел системы.
@Feature("Создание курьера") //компоненты
public class CreateCourierTest extends BaseTest {

    private final Faker faker = new Faker();
    private String testLogin;
    private String testPassword;
    private String anotherPassword;
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
    @Story("Позитивные тесты создания курьера") //Тип сценариев
    @Description("Проверка успешного создания курьера")
    @DisplayName("Курьера можно создать")
    public void testCreateCourierSuccess() {
        testLogin = faker.letterify("??????");
        testPassword = faker.numerify("####");

        Response response = createTestCourier(testLogin, testPassword, testFirstName);

        assertEquals(SC_CREATED, response.statusCode());
        assertTrue(response.jsonPath().getBoolean("ok"));

        JsonPath jsonPath = response.jsonPath();
        assertTrue("Поле 'ok' отсутствует в ответе", jsonPath.get("ok") != null);
        assertTrue(jsonPath.getBoolean("ok"));
    }

    @Test
    @Story("Негативные тесты создания курьера") //Тип сценариев
    @Description("Попытка создания курьера с уже существующим логином")
    @DisplayName("Ошибка при создании курьера с существующим логином: код состояния")
    public void testCreateDuplicateCourierCheckStatusCode() {
        testLogin = faker.letterify("??????");
        testPassword = faker.numerify("####");
        anotherPassword = faker.numerify("####");

        createTestCourier(testLogin, testPassword, testFirstName); //создаем тестового курьера
        Response response = createTestCourier(testLogin, anotherPassword, testFirstName);

        assertEquals(SC_CONFLICT, response.statusCode());
    }

    @Test
    @Story("Негативные тесты создания курьера") //Тип сценариев
    @Description("Попытка создания курьера с уже существующим логином")
    @DisplayName("Ошибка при создании курьера с существующим логином: тело ответа")
    public void testCreateDuplicateCourierCheckBody() {
        testLogin = faker.letterify("??????");
        testPassword = faker.numerify("####");
        anotherPassword = faker.numerify("####");

        createTestCourier(testLogin, testPassword, testFirstName); //создаем тестового курьера
        Response response = createTestCourier(testLogin, anotherPassword, testFirstName);

        JsonPath jsonPath = response.jsonPath();
        assertTrue("Поле 'message' отсутствует в ответе", jsonPath.get("message") != null);
        assertEquals("Этот логин уже используется", jsonPath.getString("message")); //ошибка в тесте сообщения, поэтому всегда будет ошибка
    }

    @Test
    @Story("Негативные тесты создания курьера") //Тип сценариев
    @Description("Попытка создания курьера без обязательных полей")
    @DisplayName("Ошибка при создании курьера с пустым полем логин: код состояния")
    public void testCreateCourierWithoutRequiredFieldsLoginCheckStatusCode() {
        // Без логина
        testPassword = faker.numerify("####");

        Response response = createTestCourierWithoutLogin(testPassword, testFirstName); //создаем тестового курьера

        assertEquals(SC_BAD_REQUEST, response.statusCode());
    }

    @Test
    @Story("Негативные тесты создания курьера") //Тип сценариев
    @Description("Попытка создания курьера без обязательных полей")
    @DisplayName("Ошибка при создании курьера с пустым полем логин: тело ответа")
    public void testCreateCourierWithoutRequiredFieldsLoginCheckBody() {
        // Без логина
        testPassword = faker.numerify("####");

        Response response = createTestCourierWithoutLogin(testPassword, testFirstName); //создаем тестового курьера

        JsonPath jsonPath = response.jsonPath();
        assertTrue("Поле 'message' отсутствует в ответе", jsonPath.get("message") != null);
        assertEquals("Недостаточно данных для создания учетной записи", jsonPath.getString("message"));
    }

    @Test
    @Story("Негативные тесты создания курьера") //Тип сценариев
    @Description("Попытка создания курьера без обязательных полей")
    @DisplayName("Ошибка при создании курьера с пустым полем пароль: код состояния")
    public void testCreateCourierWithoutRequiredFieldsPasswordCheckStatusCode() {
        // Без пароля
        testLogin = faker.letterify("??????");

        Response response = createTestCourierWithoutPassword(testLogin, testFirstName); //создаем тестового курьера

        assertEquals(SC_BAD_REQUEST, response.statusCode());
    }

    @Test
    @Story("Негативные тесты создания курьера") //Тип сценариев
    @Description("Попытка создания курьера без обязательных полей")
    @DisplayName("Ошибка при создании курьера с пустым полем пароль: тело ответа")
    public void testCreateCourierWithoutRequiredFieldsPasswordCheckBody() {
        // Без пароля
        testLogin = faker.letterify("??????");

        Response response = createTestCourierWithoutPassword(testLogin, testFirstName); //создаем тестового курьера

        JsonPath jsonPath = response.jsonPath();
        assertTrue("Поле 'message' отсутствует в ответе", jsonPath.get("message") != null);
        assertEquals("Недостаточно данных для создания учетной записи", jsonPath.getString("message"));
    }
}