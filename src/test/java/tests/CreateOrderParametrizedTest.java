package tests;

import io.qameta.allure.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import java.util.Arrays;
import java.util.Collection;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@Epic("API тесты для сервиса доставки") //раздел системы.
@Feature("Создание заказа") //компоненты
@RunWith(Parameterized.class)
public class CreateOrderParametrizedTest extends BaseOrderTest {

    private final String[] colors;
    private Integer trackNumber;

    //case name используется только для отображения в параметризованном тесте
    public CreateOrderParametrizedTest(String caseName, String[] colors) {
        this.colors = colors;
    }

    //name = "{0}" означает, что в отчете будет использовано первое значение из массива
    @Parameterized.Parameters(name = "{0}")
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] {
                {"Заказ с цветом BLACK", new String[]{"BLACK"}},
                {"Заказ с цветом GREY", new String[]{"GREY"}},
                {"Заказ с двумя цветами", new String[]{"BLACK", "GREY"}},
                {"Заказ без указания цвета", null}
        });
    }

    @Test
    @Story("Позитивные тесты создания заказа") //Тип сценариев
    @Description("Проверка создания заказа с разными вариантами цветов")
    public void testCreateOrderWithDifferentColors() {
        trackNumber = createTestOrder(colors);

        given()
                .header("Content-type", "application/json")
                .queryParam("t", trackNumber)
                .when()
                .get("/api/v1/orders/track")
                .then()
                .statusCode(200)
                .body("order.track", equalTo(trackNumber));
    }
}