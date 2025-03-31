package tests;

import io.qameta.allure.*;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static org.apache.http.HttpStatus.*;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

@Epic("API тесты для сервиса доставки") //раздел системы.
@Feature("Создание заказа") //компоненты
@RunWith(Parameterized.class)
public class CreateOrderParametrizedTest extends BaseTest {

    private final String[] colors;
    private int trackNumber;

    @After
    public void tearDown() {
        if(trackNumber != 0) {
        Response response = cancelTestOrder(trackNumber);
            if (response.statusCode() != SC_OK) {
                fail("Не удалось отменить тестовый заказ: " + trackNumber);
            }
        }
    }

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
    @DisplayName("Заказ с разными вариантами цветов")
    public void testCreateOrderWithDifferentColors() {
        //создаем заказ и получаем трек-нмоер
        trackNumber = createTestOrder(colors);

        //запрашиваем данные заказа
        Response response = getTestOrder(trackNumber);

        //проверка статуса и трек-номера
        assertEquals(SC_OK, response.statusCode());
        JsonPath jsonPath = response.jsonPath();
        assertEquals(String.valueOf(trackNumber), jsonPath.getString("order.track"));

        //проверка цветов в ответе
        List<String> actualColors = jsonPath.getList("order.color", String.class);

            // Для случая, когда цвета не указаны
        if (colors == null) {
            assertTrue("Список цветов должен быть пустым или null",
                    actualColors == null || actualColors.isEmpty());

            // Для случаев с указанными цветами
        } else {
            assertNotNull("Список цветов не должен быть null", actualColors);
            assertEquals("Количество цветов не совпадает",
                    colors.length, actualColors.size());
            assertThat("Цвета не совпадают",
                    actualColors, containsInAnyOrder(colors));
        }
    }
}