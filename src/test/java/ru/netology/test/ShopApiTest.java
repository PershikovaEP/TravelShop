package ru.netology.test;

import com.codeborne.selenide.logevents.SelenideLogger;
import com.google.gson.Gson;
import io.qameta.allure.selenide.AllureSelenide;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.netology.data.APIHelper;
import ru.netology.data.DataHelper;
import ru.netology.data.SQLHelper;

import static io.restassured.RestAssured.given;
import static java.lang.String.valueOf;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ShopApiTest {


    @BeforeAll
    static void setUpAll() {
        SelenideLogger.addListener("allure", new AllureSelenide());
    }

    @AfterAll
    static void tearDownAll() {
        SelenideLogger.removeListener("allure");
    }


   //21. Тестирование API(post) покупки тура по карте со статусом APPROVED
    @Test
    public void shouldBuyingTourCardApproved() {
        String status = "APPROVED";
        var card = DataHelper.getCard(status);
        APIHelper.buyTour(card, status);
        String payment_id = SQLHelper.paymentOrder();
        DataHelper.PaymentEntity payment_entity = SQLHelper.payment();
        assertEquals(payment_id, payment_entity.getTransaction_id());
        assertEquals("45000", valueOf(payment_entity.getAmount()));
        assertEquals(status, payment_entity.getStatus());
    }

    //22. Тестирование API(post) покупки тура по карте со статусом DECLINED
    @Test
    public void shouldFailureCardDeclined() {
        String status = "DECLINED";
        var card = DataHelper.getCard(status);
        APIHelper.buyTour(card,status);
        String payment_id = SQLHelper.paymentOrder();
        DataHelper.PaymentEntity payment_entity = SQLHelper.payment();
        assertEquals(payment_id, payment_entity.getTransaction_id());
        assertEquals("45000", valueOf(payment_entity.getAmount()));
        assertEquals("DECLINED", payment_entity.getStatus());
    }

    //23. Тестирование API(post) покупки тура при заполнение поля владелец на кириллице
    @Test
    public void shouldErrorWhenHolderCyrillic() {
        var card = DataHelper.getCardInvalidHolder("APPROVED", DataHelper.generateNameCyrillic().getName());
        APIHelper.errorBuyTour(card);
    }

    //24. Тестирование API(post) покупки тура при заполении номером карты, состоящим из менее 16 цифр
    @Test
    public void shouldErrorWhenInvalidNumberCard() {
        var card = DataHelper.getCardInvalidNumberCard(DataHelper.getCardNumberInvalid().getNumber());
        APIHelper.errorBuyTour(card);
    }

    //25. Тестирование API(post) покупки тура при заполении формы прошлым месяцем
    @Test
    public void shouldErrorPastMonth() {
        var card = DataHelper.getCardInvalidTime("APPROVED", DataHelper.generatePastMonth().getMonth(),
                DataHelper.generatePastMonth().getYear());
        APIHelper.errorBuyTour(card);
    }

    //26. Тестирование API(post) покупки тура при заполении формы прошлым годом
    @Test
    public void shouldErrorPastYear() {
        var card = DataHelper.getCardInvalidTime("APPROVED", DataHelper.generatePastYear().getMonth(),
                DataHelper.generatePastYear().getYear());
        APIHelper.errorBuyTour(card);
    }

    //27. Тестирование API(post) покупки тура при заполении cvc-кода из 2 цифр
    @Test
    public void shouldErrorInvalidCvcCode() {
        var card = DataHelper.getCardInvalidCvc("APPROVED", DataHelper.generateInvalidCvcCode().getCode());
        APIHelper.errorBuyTour(card);
    }

    //28. Тестирование API(post) покупки тура при заполнение поля владелец c использованием цифр
    @Test
    public void shouldErrorWhenHolderOfNumber() {
        var card = DataHelper.getCardInvalidHolder("APPROVED", DataHelper.generateNameNumbers().getName());
        APIHelper.errorBuyTour(card);
    }

    //29. Тестирование API(post) покупки тура при заполнение поля владелец c использованием спец.символов
    @Test
    public void shouldErrorWhenWhenHolderOfSymbol() {
        var card = DataHelper.getCardInvalidHolder("APPROVED", DataHelper.generateNameSymbol().getName());
        APIHelper.errorBuyTour(card);
    }

    //30. Тестирование API(post) покупки тура при незаполненным полем владелец
    @Test
    public void shouldErrorWhenNotFilledHolder() {
        var card = DataHelper.getCardInvalidHolder("APPROVED", "");
        APIHelper.errorBuyTour(card);
    }

    //31. Тестирование API(post) покупки тура при заполнении формы несуществующм месяцем
    @Test
    public void shouldErrorWhenInvalidMonth() {
        var card = DataHelper.getCardInvalidTime("APPROVED", DataHelper.invalidMonth(), DataHelper.generateValidDate().getYear());
        APIHelper.errorBuyTour(card);
    }

    //32. Тестирование API(post) покупки тура при заполнении поля месяц одной цифры
    @Test
    public void shouldErrorWhenMonthFrom1Dijit() {
        var card = DataHelper.getCardInvalidTime("APPROVED", DataHelper.generateInvalidDateFrom1Dijit().getMonth(), DataHelper.generateValidDate().getYear());
        APIHelper.errorBuyTour(card);
    }

    //33. Тестирование API(post) покупки тура по карте со сроком действия более 5 лет
    @Test
    public void shouldErrorWhenDateForMoreThan5Years() {
        var card = DataHelper.getCardInvalidTime("APPROVED", DataHelper.generateDateMore5Year().getMonth(), DataHelper.generateDateMore5Year().getYear());
        APIHelper.errorBuyTour(card);
    }

    //*34. Тестирование API(post) покупки тура по карте при заполнении поля год одной цифры
    @Test
    public void shouldErrorWhenYearFrom1Dijit() {
        var card = DataHelper.getCardInvalidTime("APPROVED", DataHelper.generateValidDate().getMonth(), DataHelper.generateInvalidDateFrom1Dijit().getYear());
        APIHelper.errorBuyTour(card);
    }

    //35. Тестирование API(post) покупки тура при незаполенным полем год
    @Test
    public void shouldErrorWhenNotFilledYear() {
        var card = DataHelper.getCardInvalidTime("APPROVED", DataHelper.generateValidDate().getMonth(), "");
        APIHelper.errorBuyTour(card);
    }

    //36. Тестирование API(post) покупки тура при незаполненнои поле месяц
    @Test
    public void shouldErrorWhenNotFilledMonth() {
        var card = DataHelper.getCardInvalidTime("APPROVED", "", DataHelper.generateValidDate().getYear());
        APIHelper.errorBuyTour(card);
    }

    //37. Тестирование API(post) покупки тура при незаполненном поле номер карты
    @Test
    public void shouldErrorWhenNotFilledNumberCard() {
        var card = DataHelper.getCardInvalidNumberCard("");
        APIHelper.errorBuyTour(card);
    }

    //38. Тестирование API(post) покупки тура при незаполненном поле CVC/CVV
    @Test
    public void shouldErrorWhenNotFilledCVC() {
        var card = DataHelper.getCardInvalidCvc("APPROVED", "");
        APIHelper.errorBuyTour(card);
    }

    //39. Тестирование API(post) покупки тура по карте с текущим месяцем и годом с проверкой записи в бд
    @Test
    public void shouldBuyTourDateCurrent() {
        String status = "APPROVED";
        var card = DataHelper.getCardInvalidTime(status, DataHelper.generateCurrentDate().getMonth(), DataHelper.generateCurrentDate().getYear());
        APIHelper.buyTour(card, status);
        String payment_id = SQLHelper.paymentOrder();
        DataHelper.PaymentEntity payment_entity = SQLHelper.payment();
        assertEquals(payment_id, payment_entity.getTransaction_id());
        assertEquals("45000", valueOf(payment_entity.getAmount()));
        assertEquals(status, payment_entity.getStatus());
    }

    //40. Тестирование API(post) покупки тура при заполнение поля владелец одной буквы на латинице
    @Test
    public void shouldErrorWhenHolderFor1Letter() {
        var card = DataHelper.getCardInvalidHolder("APPROVED", DataHelper.generateName1Letter().getName());
        APIHelper.errorBuyTour(card);
    }
}
