package tests;

import io.qameta.allure.*;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.junit.Test;

import static org.apache.http.HttpStatus.SC_OK;
import static org.junit.Assert.*;

@Epic("API тесты для сервиса доставки") //раздел системы.
@Feature("Список заказов") //компоненты
public class ListOfOrdersTest extends BaseTest {

    @Test
    @Story("Получение списка заказов") //Тип сценариев
    @Description("Проверка, что возвращается список заказов")
    @DisplayName("В тело ответа возвращается список заказов")
    public void testGetListOfOrders() {
        //получаем список заказов по умолчанию
        Response response = getListOfOrders();

        //проверка статуса
        assertEquals(SC_OK, response.statusCode());
        JsonPath jsonPath = response.jsonPath();

        //проверка обязательных полей в ответе
        assertNotNull("Поле 'orders' должно присутствовать", jsonPath.get("orders"));
        assertNotNull("Поле 'pageInfo' должно присутствовать", jsonPath.get("pageInfo"));
        assertNotNull("Поле 'availableStations' должно присутствовать", jsonPath.get("availableStations"));
    }
}