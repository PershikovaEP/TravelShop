package ru.netology.test;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.logevents.SelenideLogger;
import org.junit.jupiter.api.*;
import io.qameta.allure.selenide.AllureSelenide;
import ru.netology.data.DataHelper;
import ru.netology.data.SQLHelper;

import java.time.Duration;

import static com.codeborne.selenide.Selenide.*;
import static java.lang.String.valueOf;
import static org.junit.jupiter.api.Assertions.*;

public class ShopTest {
    @BeforeAll
    static void setUpAll() {
        SelenideLogger.addListener("allure", new AllureSelenide());
    }

    @AfterAll
    static void tearDownAll() {
        SelenideLogger.removeListener("allure");
    }

    @BeforeEach
    void setup() {
        open("http://localhost:8080");
    }

    SelenideElement numberCard = $x("//*[text()='Номер карты']/following-sibling::span/input");
    SelenideElement month = $x("//*[text()='Месяц']/following-sibling::span/input");
    SelenideElement year = $x("//*[text()='Год']/following-sibling::span/input");
    SelenideElement holder = $x("//*[text()='Владелец']/following-sibling::span/input");
    SelenideElement cvc = $x("//*[text()='CVC/CVV']/following-sibling::span/input");
    SelenideElement buttonBuy = $x("//*[text()='Купить']");
    SelenideElement buttonProceed = $x("//*[text()='Продолжить']");
    SelenideElement success = $x("//*[contains(text(),'Операция одобрена')]");
    SelenideElement error = $x("//*[contains(text(),'отказал в проведении операции')");


    //1. Успешный сценарий покупки тура при оплате по карте со статусом одобрено с проверкой записи в БД
    @Test
    public void shouldBuyTourCardApproved() {
        buttonBuy.click();
        numberCard.setValue(DataHelper.getCardNumber("APPROVED").getNumber());
        month.setValue(DataHelper.generateValidDate().getMonth());
        year.setValue(DataHelper.generateValidDate().getYear());
        holder.setValue(DataHelper.generateValidName().getName());
        cvc.setValue(DataHelper.generateCvcCode().getCode());
        buttonProceed.click();
        success.shouldBe(Condition.visible, Duration.ofSeconds(15)).shouldHave(Condition.exactText("Операция одобрена Банком."));
        String payment_id = SQLHelper.paymentOrder();
        DataHelper.Payment_entity payment_entity = SQLHelper.payment();
        assertEquals(payment_id, payment_entity.getId());
        assertEquals("45000", valueOf(payment_entity.getAmount()));
        assertEquals("APPROVED", payment_entity.getStatus());

    }

    //2. Успешный сценарий покупки тура при оплате по карте со статусом отклонено с проверкой записи в БД*
    @Test
    public void shouldErrorBuyTourCardDeclined() {
        buttonBuy.click();
        numberCard.setValue(DataHelper.getCardNumber("DECLINED").getNumber());
        month.setValue(DataHelper.generateValidDate().getMonth());
        year.setValue(DataHelper.generateValidDate().getYear());
        holder.setValue(DataHelper.generateValidName().getName());
        cvc.setValue(DataHelper.generateCvcCode().getCode());
        buttonProceed.click();
        error.shouldBe(Condition.visible, Duration.ofSeconds(15)).shouldHave(Condition.exactText("Банк отказал в проведении операции."));
        String payment_id = SQLHelper.paymentOrder();
        DataHelper.Payment_entity payment_entity = SQLHelper.payment();
        assertEquals(payment_id, payment_entity.getId());
        assertEquals("45000", valueOf(payment_entity.getAmount()));
        assertEquals("DECLINED", payment_entity.getStatus());
    }

    //3 Покупка тура по карте с прошлым месяцем
    @Test
    public void shouldErrorWhenPastMonth() {
        buttonBuy.click();
        numberCard.setValue(DataHelper.getCardNumber("DECLINED").getNumber());
        month.setValue(DataHelper.generatePastMonth().getMonth());
        year.setValue(DataHelper.generatePastMonth().getYear());
        holder.setValue(DataHelper.generateValidName().getName());
        cvc.setValue(DataHelper.generateCvcCode().getCode());
        buttonProceed.click();
        $x("//*[contains(text(),'Неверно указан срок')]").shouldBe(Condition.visible, Duration.ofSeconds(15)).
                shouldHave(Condition.exactText("Неверно указан срок действия карты"));
    }

    //4. Покупка тура по карте с прошлым годом
    @Test
    public void shouldErrorWhenPastYear() {
        buttonBuy.click();
        numberCard.setValue(DataHelper.getCardNumber("DECLINED").getNumber());
        month.setValue(DataHelper.generatePastYear().getMonth());
        year.setValue(DataHelper.generatePastYear().getYear());
        holder.setValue(DataHelper.generateValidName().getName());
        cvc.setValue(DataHelper.generateCvcCode().getCode());
        buttonProceed.click();
        $x("//*[contains(text(),'Истёк срок')]").shouldBe(Condition.visible, Duration.ofSeconds(15)).
                shouldHave(Condition.exactText("Истёк срок действия карты"));
    }

//    5. Покупка тура по карте со сроком действия более 5 лет
    @Test
    public void shouldErrorWhenDateForMoreThan5Years() {
        buttonBuy.click();
        numberCard.setValue(DataHelper.getCardNumber("DECLINED").getNumber());
        month.setValue(DataHelper.generateDateMore5Year().getMonth());
        year.setValue(DataHelper.generateDateMore5Year().getYear());
        holder.setValue(DataHelper.generateValidName().getName());
        cvc.setValue(DataHelper.generateCvcCode().getCode());
        buttonProceed.click();
        $x("//*[contains(text(),'Неверно указан срок')]").shouldBe(Condition.visible, Duration.ofSeconds(15)).
                shouldHave(Condition.exactText("Неверно указан срок действия карты"));
    }

    //6. Покупка тура по карте с текущим месяцем и годом с проверкой записи в бд
    @Test
    public void shouldBuyTourDateCurrent() {
        buttonBuy.click();
        numberCard.setValue(DataHelper.getCardNumber("APPROVED").getNumber());
        month.setValue(DataHelper.generateCurrentDate().getMonth());
        year.setValue(DataHelper.generateCurrentDate().getYear());
        holder.setValue(DataHelper.generateValidName().getName());
        cvc.setValue(DataHelper.generateCvcCode().getCode());
        buttonProceed.click();
        success.shouldBe(Condition.visible, Duration.ofSeconds(15)).shouldHave(Condition.exactText("Операция одобрена Банком."));
        String payment_id = SQLHelper.paymentOrder();
        DataHelper.Payment_entity payment_entity = SQLHelper.payment();
        assertEquals(payment_id, payment_entity.getId());
        assertEquals("45000", valueOf(payment_entity.getAmount()));
        assertEquals("APPROVED", payment_entity.getStatus());
    }

//    7. Покупка тура по карте с введением в поле месяц одной цифры
    @Test
    public void shouldErrorWhenMonthFrom1Dijit() {
        buttonBuy.click();
        numberCard.setValue(DataHelper.getCardNumber("DECLINED").getNumber());
        month.setValue(DataHelper.generateInvalidDateFrom1Dijit().getMonth());
        year.setValue(DataHelper.generateValidDate().getYear());
        holder.setValue(DataHelper.generateValidName().getName());
        cvc.setValue(DataHelper.generateCvcCode().getCode());
        buttonProceed.click();
        $x("//*[contains(text(),'Неверный формат')]").shouldBe(Condition.visible, Duration.ofSeconds(15)).
                shouldHave(Condition.exactText("Неверный формат"));
    }

//    8. Покупка тура по карте с введением в поле месяц несуществующего месяца
    @Test
    public void shouldErrorInvalidMonth() {
        buttonBuy.click();
        numberCard.setValue(DataHelper.getCardNumber("DECLINED").getNumber());
        month.setValue(DataHelper.invalidMonth());
        year.setValue(DataHelper.generateValidDate().getYear());
        holder.setValue(DataHelper.generateValidName().getName());
        cvc.setValue(DataHelper.generateCvcCode().getCode());
        buttonProceed.click();
        $x("//*[contains(text(),'Неверно указан срок')]").shouldBe(Condition.visible, Duration.ofSeconds(15)).
                shouldHave(Condition.exactText("Неверно указан срок действия карты"));
    }

    //9. Покупка тура по карте с введением в поле год значения из одной цифры
    @Test
    public void shouldErrorWhenYearFrom1Dijit() {
        buttonBuy.click();
        numberCard.setValue(DataHelper.getCardNumber("DECLINED").getNumber());
        month.setValue(DataHelper.invalidMonth());
        year.setValue(DataHelper.generateValidDate().getYear());
        holder.setValue(DataHelper.generateValidName().getName());
        cvc.setValue(DataHelper.generateCvcCode().getCode());
        buttonProceed.click();
        $x("//*[contains(text(),'Истёк срок')]").shouldBe(Condition.visible, Duration.ofSeconds(15)).
                shouldHave(Condition.exactText("Истёк срок действия карты"));
    }

    //10. Покупка тура по карте с невалидным владельцем с использованием кириллицы
    @Test
    public void shouldErrorWhenHolderOfCyrillic() {
        buttonBuy.click();
        numberCard.setValue(DataHelper.getCardNumber("DECLINED").getNumber());
        month.setValue(DataHelper.generateValidDate().getMonth());
        year.setValue(DataHelper.generateValidDate().getYear());
        holder.setValue(DataHelper.generateNameCyrillic().getName());
        cvc.setValue(DataHelper.generateCvcCode().getCode());
        buttonProceed.click();
        $x("//*[contains(text(),'Неверный формат')]").shouldBe(Condition.visible, Duration.ofSeconds(15)).
                shouldHave(Condition.exactText("Неверный формат"));
    }

    //11. Покупка тура по карте с невалидным владельцем с использованием цифр
    @Test
    public void shouldErrorWhenHolderOfNumber() {
        buttonBuy.click();
        numberCard.setValue(DataHelper.getCardNumber("DECLINED").getNumber());
        month.setValue(DataHelper.generateValidDate().getMonth());
        year.setValue(DataHelper.generateValidDate().getYear());
        holder.setValue(DataHelper.generateNameNumbers().getName());
        cvc.setValue(DataHelper.generateCvcCode().getCode());
        buttonProceed.click();
        $x("//*[contains(text(),'Неверный формат')]").shouldBe(Condition.visible, Duration.ofSeconds(15)).
                shouldHave(Condition.exactText("Неверный формат"));
    }

    //12. Покупка тура по карте с невалидным владельцем с использованием символов
    @Test
    public void shouldErrorWhenHolderOfSymbol() {
        buttonBuy.click();
        numberCard.setValue(DataHelper.getCardNumber("DECLINED").getNumber());
        month.setValue(DataHelper.generateValidDate().getMonth());
        year.setValue(DataHelper.generateValidDate().getYear());
        holder.setValue(DataHelper.generateNameSymbol().getName());
        cvc.setValue(DataHelper.generateCvcCode().getCode());
        buttonProceed.click();
        $x("//*[contains(text(),'Неверный формат')]").shouldBe(Condition.visible, Duration.ofSeconds(15)).
                shouldHave(Condition.exactText("Неверный формат"));
    }

//    13. Покупка тура по карте с внесением в поле владелец одной буквы на латинице

    @Test
    public void shouldErrorWhenHolderFor1Letter() {
        buttonBuy.click();
        numberCard.setValue(DataHelper.getCardNumber("DECLINED").getNumber());
        month.setValue(DataHelper.generateValidDate().getMonth());
        year.setValue(DataHelper.generateValidDate().getYear());
        holder.setValue(DataHelper.generateName1Letter().getName());
        cvc.setValue(DataHelper.generateCvcCode().getCode());
        buttonProceed.click();
        $x("//*[contains(text(),'Неверный формат')]").shouldBe(Condition.visible, Duration.ofSeconds(15)).
                shouldHave(Condition.exactText("Неверный формат"));
    }

    //14. Покупка тура по карте с cvc-кодом из 2 цифр
    @Test
    public void shouldErrorWhenCVCFor2Digit() {
        buttonBuy.click();
        numberCard.setValue(DataHelper.getCardNumber("DECLINED").getNumber());
        month.setValue(DataHelper.generateValidDate().getMonth());
        year.setValue(DataHelper.generateValidDate().getYear());
        holder.setValue(DataHelper.generateValidName().getName());
        cvc.setValue(DataHelper.generateInvalidCvcCode().getCode());
        buttonProceed.click();
        $x("//*[contains(text(),'Неверный формат')]").shouldBe(Condition.visible, Duration.ofSeconds(15)).
                shouldHave(Condition.exactText("Неверный формат"));
    }

    //15. Покупка тура по карте с незаполненным полем номер карты
    @Test
    public void shouldErrorWhenNotFilledNumberCard() {
        buttonBuy.click();
        month.setValue(DataHelper.generateValidDate().getMonth());
        year.setValue(DataHelper.generateValidDate().getYear());
        holder.setValue(DataHelper.generateValidName().getName());
        cvc.setValue(DataHelper.generateCvcCode().getCode());
        buttonProceed.click();
        $x("//*[contains(text(),'Поле обязательно')]").shouldBe(Condition.visible, Duration.ofSeconds(15)).
                shouldHave(Condition.exactText("Поле обязательно для заполнения"));
    }

//    16. Покупка тура по карте с незаполненным полем месяц
    @Test
    public void shouldErrorWhenNotFilledMonth() {
        buttonBuy.click();
        numberCard.setValue(DataHelper.getCardNumber("DECLINED").getNumber());
        year.setValue(DataHelper.generateValidDate().getYear());
        holder.setValue(DataHelper.generateValidName().getName());
        cvc.setValue(DataHelper.generateCvcCode().getCode());
        buttonProceed.click();
        $x("//*[contains(text(),'Поле обязательно')]").shouldBe(Condition.visible, Duration.ofSeconds(15)).
                shouldHave(Condition.exactText("Поле обязательно для заполнения"));
    }


    //17. Покупка тура по карте с незаполенным полем год
    @Test
    public void shouldErrorWhenNotFilledYear() {
        buttonBuy.click();
        numberCard.setValue(DataHelper.getCardNumber("DECLINED").getNumber());
        month.setValue(DataHelper.generateValidDate().getMonth());
        holder.setValue(DataHelper.generateValidName().getName());
        cvc.setValue(DataHelper.generateCvcCode().getCode());
        buttonProceed.click();
        $x("//*[contains(text(),'Поле обязательно')]").shouldBe(Condition.visible, Duration.ofSeconds(15)).
                shouldHave(Condition.exactText("Поле обязательно для заполнения"));
    }

    //18. Покупка тура по карте с незаполенным полем владелец

    @Test
    public void shouldErrorWhenNotFilledHolder() {
        buttonBuy.click();
        numberCard.setValue(DataHelper.getCardNumber("DECLINED").getNumber());
        month.setValue(DataHelper.generateValidDate().getMonth());
        year.setValue(DataHelper.generateValidDate().getYear());
        cvc.setValue(DataHelper.generateCvcCode().getCode());
        buttonProceed.click();
        $x("//*[contains(text(),'Поле обязательно')]").shouldBe(Condition.visible, Duration.ofSeconds(15)).
                shouldHave(Condition.exactText("Поле обязательно для заполнения"));
    }

//    19. Покупка тура по карте с незаполненным полем cvc-код
    @Test
    public void shouldErrorWhenNotFilledCVC() {
        buttonBuy.click();
        numberCard.setValue(DataHelper.getCardNumber("DECLINED").getNumber());
        month.setValue(DataHelper.generateValidDate().getMonth());
        year.setValue(DataHelper.generateValidDate().getYear());
        holder.setValue(DataHelper.generateValidName().getName());
        buttonProceed.click();
        $x("//*[contains(text(),'Поле обязательно')]").shouldBe(Condition.visible, Duration.ofSeconds(15)).
                shouldHave(Condition.exactText("Поле обязательно для заполнения"));
    }

//    20. Покупка тура по карте с номером, состоящим из менее 16 цифр
    @Test
    public void shouldErrorWhenNumberCardInvalid() {
        buttonBuy.click();
        numberCard.setValue(DataHelper.getCardNumberInvalid().getNumber());
        month.setValue(DataHelper.generateValidDate().getMonth());
        year.setValue(DataHelper.generateValidDate().getYear());
        holder.setValue(DataHelper.generateValidName().getName());
        cvc.setValue(DataHelper.generateCvcCode().getCode());
        buttonProceed.click();
        $x("//*[contains(text(),'Неверный формат')]").shouldBe(Condition.visible, Duration.ofSeconds(15)).
                shouldHave(Condition.exactText("Неверный формат"));
    }







}
