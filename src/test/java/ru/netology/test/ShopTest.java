package ru.netology.test;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import com.codeborne.selenide.logevents.SelenideLogger;
import org.junit.jupiter.api.*;
import io.qameta.allure.selenide.AllureSelenide;
import ru.netology.data.DataHelper;
import ru.netology.data.SQLHelper;
import ru.netology.pageObject.BuyTour;

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

    //1. Успешный сценарий покупки тура при оплате по карте со статусом одобрено с проверкой записи в БД
    @Test
    public void shouldBuyTourCardApproved() {
        BuyTour buyTour = new BuyTour();
        buyTour.pressButtonBuy();
        buyTour.fillNumberCard(DataHelper.getCardNumber("APPROVED").getNumber());
        buyTour.fillMonth(DataHelper.generateValidDate().getMonth());
        buyTour.fillYear(DataHelper.generateValidDate().getYear());
        buyTour.fillHolder(DataHelper.generateValidName().getName());
        buyTour.fillCvc(DataHelper.generateCvcCode().getCode());
        buyTour.pressButtonProceed();
        buyTour.success();
        String payment_id = SQLHelper.paymentOrder();
        DataHelper.PaymentEntity payment_entity = SQLHelper.payment();
        assertEquals(payment_id, payment_entity.getId());
        assertEquals("45000", valueOf(payment_entity.getAmount()));
        assertEquals("APPROVED", payment_entity.getStatus());

    }

    //2. Успешный сценарий покупки тура при оплате по карте со статусом отклонено с проверкой записи в БД*
    @Test
    public void shouldErrorBuyTourCardDeclined() {
        BuyTour buyTour = new BuyTour();
        buyTour.pressButtonBuy();
        buyTour.fillNumberCard(DataHelper.getCardNumber("DECLINED").getNumber());
        buyTour.fillMonth(DataHelper.generateValidDate().getMonth());
        buyTour.fillYear(DataHelper.generateValidDate().getYear());
        buyTour.fillHolder(DataHelper.generateValidName().getName());
        buyTour.fillCvc(DataHelper.generateCvcCode().getCode());
        buyTour.pressButtonProceed();
        buyTour.error();
        String payment_id = SQLHelper.paymentOrder();
        DataHelper.PaymentEntity payment_entity = SQLHelper.payment();
        assertEquals(payment_id, payment_entity.getId());
        assertEquals("45000", valueOf(payment_entity.getAmount()));
        assertEquals("DECLINED", payment_entity.getStatus());
    }

    //3 Покупка тура по карте с прошлым месяцем
    @Test
    public void shouldErrorWhenPastMonth() {
        BuyTour buyTour = new BuyTour();
        buyTour.pressButtonBuy();
        buyTour.fillNumberCard(DataHelper.getCardNumber("DECLINED").getNumber());
        buyTour.fillMonth(DataHelper.generatePastMonth().getMonth());
        buyTour.fillYear(DataHelper.generatePastMonth().getYear());
        buyTour.fillHolder(DataHelper.generateValidName().getName());
        buyTour.fillCvc(DataHelper.generateCvcCode().getCode());
        buyTour.pressButtonProceed();
        buyTour.errorCardExpired();
    }

    //4. Покупка тура по карте с прошлым годом
    @Test
    public void shouldErrorWhenPastYear() {
        BuyTour buyTour = new BuyTour();
        buyTour.pressButtonBuy();
        buyTour.fillNumberCard(DataHelper.getCardNumber("DECLINED").getNumber());
        buyTour.fillMonth(DataHelper.generatePastYear().getMonth());
        buyTour.fillYear(DataHelper.generatePastYear().getYear());
        buyTour.fillHolder(DataHelper.generateValidName().getName());
        buyTour.fillCvc(DataHelper.generateCvcCode().getCode());
        buyTour.pressButtonProceed();
        buyTour.errorCardExpired();
    }

//    5. Покупка тура по карте со сроком действия более 5 лет
    @Test
    public void shouldErrorWhenDateForMoreThan5Years() {
        BuyTour buyTour = new BuyTour();
        buyTour.pressButtonBuy();
        buyTour.fillNumberCard(DataHelper.getCardNumber("DECLINED").getNumber());
        buyTour.fillMonth(DataHelper.generateDateMore5Year().getMonth());
        buyTour.fillYear(DataHelper.generateDateMore5Year().getYear());
        buyTour.fillHolder(DataHelper.generateValidName().getName());
        buyTour.fillCvc(DataHelper.generateCvcCode().getCode());
        buyTour.pressButtonProceed();
        buyTour.errorTimeLimit();
    }

    //6. Покупка тура по карте с текущим месяцем и годом с проверкой записи в бд
    @Test
    public void shouldBuyTourDateCurrent() {
        BuyTour buyTour = new BuyTour();
        buyTour.pressButtonBuy();
        buyTour.fillNumberCard(DataHelper.getCardNumber("APPROVED").getNumber());
        buyTour.fillMonth(DataHelper.generateCurrentDate().getMonth());
        buyTour.fillYear(DataHelper.generateCurrentDate().getYear());
        buyTour.fillHolder(DataHelper.generateValidName().getName());
        buyTour.fillCvc(DataHelper.generateCvcCode().getCode());
        buyTour.pressButtonProceed();
        buyTour.success();
        String payment_id = SQLHelper.paymentOrder();
        DataHelper.PaymentEntity payment_entity = SQLHelper.payment();
        assertEquals(payment_id, payment_entity.getId());
        assertEquals("45000", valueOf(payment_entity.getAmount()));
        assertEquals("APPROVED", payment_entity.getStatus());
    }

//    7. Покупка тура по карте с введением в поле месяц одной цифры
    @Test
    public void shouldErrorWhenMonthFrom1Dijit() {
        BuyTour buyTour = new BuyTour();
        buyTour.pressButtonBuy();
        buyTour.fillNumberCard(DataHelper.getCardNumber("DECLINED").getNumber());
        buyTour.fillMonth(DataHelper.generateInvalidDateFrom1Dijit().getMonth());
        buyTour.fillYear(DataHelper.generateValidDate().getYear());
        buyTour.fillHolder(DataHelper.generateValidName().getName());
        buyTour.fillCvc(DataHelper.generateCvcCode().getCode());
        buyTour.pressButtonProceed();
        buyTour.errorWrongFormat();

    }

//    8. Покупка тура по карте с введением в поле месяц несуществующего месяца
    @Test
    public void shouldErrorInvalidMonth() {
        BuyTour buyTour = new BuyTour();
        buyTour.pressButtonBuy();
        buyTour.fillNumberCard(DataHelper.getCardNumber("DECLINED").getNumber());
        buyTour.fillMonth(DataHelper.invalidMonth());
        buyTour.fillYear(DataHelper.generateValidDate().getYear());
        buyTour.fillHolder(DataHelper.generateValidName().getName());
        buyTour.fillCvc(DataHelper.generateCvcCode().getCode());
        buyTour.pressButtonProceed();
        buyTour.errorTimeLimit();
    }

    //9. Покупка тура по карте с введением в поле год значения из одной цифры
    @Test
    public void shouldErrorWhenYearFrom1Dijit() {
        BuyTour buyTour = new BuyTour();
        buyTour.pressButtonBuy();
        buyTour.fillNumberCard(DataHelper.getCardNumber("DECLINED").getNumber());
        buyTour.fillMonth(DataHelper.generateValidDate().getMonth());
        buyTour.fillYear(DataHelper.generateInvalidDateFrom1Dijit().getYear());
        buyTour.fillHolder(DataHelper.generateValidName().getName());
        buyTour.fillCvc(DataHelper.generateCvcCode().getCode());
        buyTour.pressButtonProceed();
        buyTour.errorWrongFormat();
    }

    //10. Покупка тура по карте с невалидным владельцем с использованием кириллицы
    @Test
    public void shouldErrorWhenHolderOfCyrillic() {
        BuyTour buyTour = new BuyTour();
        buyTour.pressButtonBuy();
        buyTour.fillNumberCard(DataHelper.getCardNumber("DECLINED").getNumber());
        buyTour.fillMonth(DataHelper.generateValidDate().getMonth());
        buyTour.fillYear(DataHelper.generateValidDate().getYear());
        buyTour.fillHolder(DataHelper.generateNameCyrillic().getName());
        buyTour.fillCvc(DataHelper.generateCvcCode().getCode());
        buyTour.pressButtonProceed();
        buyTour.errorWrongFormat();
    }

    //11. Покупка тура по карте с невалидным владельцем с использованием цифр
    @Test
    public void shouldErrorWhenHolderOfNumber() {
        BuyTour buyTour = new BuyTour();
        buyTour.pressButtonBuy();
        buyTour.fillNumberCard(DataHelper.getCardNumber("DECLINED").getNumber());
        buyTour.fillMonth(DataHelper.generateValidDate().getMonth());
        buyTour.fillYear(DataHelper.generateValidDate().getYear());
        buyTour.fillHolder(DataHelper.generateNameNumbers().getName());
        buyTour.fillCvc(DataHelper.generateCvcCode().getCode());
        buyTour.pressButtonProceed();
        buyTour.errorWrongFormat();
    }

    //12. Покупка тура по карте с невалидным владельцем с использованием символов
    @Test
    public void shouldErrorWhenHolderOfSymbol() {
        BuyTour buyTour = new BuyTour();
        buyTour.pressButtonBuy();
        buyTour.fillNumberCard(DataHelper.getCardNumber("DECLINED").getNumber());
        buyTour.fillMonth(DataHelper.generateValidDate().getMonth());
        buyTour.fillYear(DataHelper.generateValidDate().getYear());
        buyTour.fillHolder(DataHelper.generateNameSymbol().getName());
        buyTour.fillCvc(DataHelper.generateCvcCode().getCode());
        buyTour.pressButtonProceed();
        buyTour.errorWrongFormat();
    }

//    13. Покупка тура по карте с внесением в поле владелец одной буквы на латинице
    @Test
    public void shouldErrorWhenHolderFor1Letter() {
        BuyTour buyTour = new BuyTour();
        buyTour.pressButtonBuy();
        buyTour.fillNumberCard(DataHelper.getCardNumber("DECLINED").getNumber());
        buyTour.fillMonth(DataHelper.generateValidDate().getMonth());
        buyTour.fillYear(DataHelper.generateValidDate().getYear());
        buyTour.fillHolder(DataHelper.generateName1Letter().getName());
        buyTour.fillCvc(DataHelper.generateCvcCode().getCode());
        buyTour.pressButtonProceed();
        buyTour.errorWrongFormat();
    }

    //14. Покупка тура по карте с cvc-кодом из 2 цифр
    @Test
    public void shouldErrorWhenCVCFor2Digit() {
        BuyTour buyTour = new BuyTour();
        buyTour.pressButtonBuy();
        buyTour.fillNumberCard(DataHelper.getCardNumber("DECLINED").getNumber());
        buyTour.fillMonth(DataHelper.generateValidDate().getMonth());
        buyTour.fillYear(DataHelper.generateValidDate().getYear());
        buyTour.fillHolder(DataHelper.generateValidName().getName());
        buyTour.fillCvc(DataHelper.generateInvalidCvcCode().getCode());
        buyTour.pressButtonProceed();
        buyTour.errorWrongFormat();
    }

    //15. Покупка тура по карте с незаполненным полем номер карты
    @Test
    public void shouldErrorWhenNotFilledNumberCard() {
        BuyTour buyTour = new BuyTour();
        buyTour.pressButtonBuy();
        buyTour.fillMonth(DataHelper.generateValidDate().getMonth());
        buyTour.fillYear(DataHelper.generateValidDate().getYear());
        buyTour.fillHolder(DataHelper.generateValidName().getName());
        buyTour.fillCvc(DataHelper.generateCvcCode().getCode());
        buyTour.pressButtonProceed();
        buyTour.errorRequiredField();
    }

//    16. Покупка тура по карте с незаполненным полем месяц
    @Test
    public void shouldErrorWhenNotFilledMonth() {
        BuyTour buyTour = new BuyTour();
        buyTour.pressButtonBuy();
        buyTour.fillNumberCard(DataHelper.getCardNumber("DECLINED").getNumber());
        buyTour.fillYear(DataHelper.generateValidDate().getYear());
        buyTour.fillHolder(DataHelper.generateValidName().getName());
        buyTour.fillCvc(DataHelper.generateCvcCode().getCode());
        buyTour.pressButtonProceed();
        buyTour.errorRequiredField();
    }


    //17. Покупка тура по карте с незаполенным полем год
    @Test
    public void shouldErrorWhenNotFilledYear() {
        BuyTour buyTour = new BuyTour();
        buyTour.pressButtonBuy();
        buyTour.fillNumberCard(DataHelper.getCardNumber("DECLINED").getNumber());
        buyTour.fillMonth(DataHelper.generateValidDate().getMonth());
        buyTour.fillHolder(DataHelper.generateValidName().getName());
        buyTour.fillCvc(DataHelper.generateCvcCode().getCode());
        buyTour.pressButtonProceed();
        buyTour.errorRequiredField();
    }

    //18. Покупка тура по карте с незаполенным полем владелец
    @Test
    public void shouldErrorWhenNotFilledHolder() {
        BuyTour buyTour = new BuyTour();
        buyTour.pressButtonBuy();
        buyTour.fillNumberCard(DataHelper.getCardNumber("DECLINED").getNumber());
        buyTour.fillMonth(DataHelper.generateValidDate().getMonth());
        buyTour.fillYear(DataHelper.generateValidDate().getYear());
        buyTour.fillCvc(DataHelper.generateCvcCode().getCode());
        buyTour.pressButtonProceed();
        buyTour.errorRequiredField();
    }

//    19. Покупка тура по карте с незаполненным полем cvc-код
    @Test
    public void shouldErrorWhenNotFilledCVC() {
        BuyTour buyTour = new BuyTour();
        buyTour.pressButtonBuy();
        buyTour.fillNumberCard(DataHelper.getCardNumber("DECLINED").getNumber());
        buyTour.fillMonth(DataHelper.generateValidDate().getMonth());
        buyTour.fillYear(DataHelper.generateValidDate().getYear());
        buyTour.fillHolder(DataHelper.generateValidName().getName());
        buyTour.pressButtonProceed();
        buyTour.errorRequiredField();
    }

//    20. Покупка тура по карте с номером, состоящим из менее 16 цифр
    @Test
    public void shouldErrorWhenNumberCardInvalid() {
        BuyTour buyTour = new BuyTour();
        buyTour.pressButtonBuy();
        buyTour.fillNumberCard(DataHelper.getCardNumberInvalid().getNumber());
        buyTour.fillMonth(DataHelper.generateValidDate().getMonth());
        buyTour.fillYear(DataHelper.generateValidDate().getYear());
        buyTour.fillHolder(DataHelper.generateValidName().getName());
        buyTour.fillCvc(DataHelper.generateCvcCode().getCode());
        buyTour.pressButtonProceed();
        buyTour.errorWrongFormat();
    }

}
