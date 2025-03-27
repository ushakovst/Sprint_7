package tests;

import io.qameta.allure.*;
import org.junit.Test;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@Epic("API тесты для сервиса доставки") //раздел системы.
@Feature("Список заказов") //компоненты
public class ListOfOrdersTest extends BaseOrderTest {

    @Test
    @Story("Получение списка заказов") //Тип сценариев
    @Description("Проверка, что возвращается список заказов")
    public void testGetListOfOrders() {
        given()
                .header("Content-type", "application/json")
                .when()
                .get("/api/v1/orders")
                .then()
                .statusCode(200)
                .body("orders", notNullValue())
                .body("orders", not(empty()))
                .body("pageInfo", notNullValue());
    }
}