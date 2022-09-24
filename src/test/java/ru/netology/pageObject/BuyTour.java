package ru.netology.pageObject;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import ru.netology.data.DataHelper;

import java.time.Duration;

import static com.codeborne.selenide.Selenide.$x;

public class BuyTour {

    public void fillNumberCard(String cardNumber) {
        $x("//*[text()='Номер карты']/following-sibling::span/input").setValue(cardNumber);
    }

    public void fillMonth(String month) {
        $x("//*[text()='Месяц']/following-sibling::span/input").setValue(month);
    }

    public void fillYear(String year) {
        $x("//*[text()='Год']/following-sibling::span/input").setValue(year);
    }

    public void fillHolder(String name) {
        $x("//*[text()='Владелец']/following-sibling::span/input").setValue(name);
    }

    public void fillCvc(String cvc) {
        $x("//*[text()='CVC/CVV']/following-sibling::span/input").setValue(cvc);
    }

    public void pressButtonBuy() {
        $x("//*[text()='Купить']").click();
    }

    public void pressButtonProceed() {
        $x("//*[text()='Продолжить']").click();
    }

    public void success() {
        $x("//*[contains(text(),'Операция одобрена')]").shouldBe(Condition.visible,
                Duration.ofSeconds(15)).shouldHave(Condition.exactText("Операция одобрена Банком."));
    }

    public void error() {
        $x("//*[contains(text(),'отказал в проведении операции')]").shouldBe(Condition.visible,
                Duration.ofSeconds(15)).shouldHave(Condition.exactText("Банк отказал в проведении операции."));
    }

    public void errorTimeLimit() {
        $x("//*[contains(text(),'Неверно указан срок')]").shouldBe(Condition.visible, Duration.ofSeconds(15)).
                shouldHave(Condition.exactText("Неверно указан срок действия карты"));
    }

    public void errorWrongFormat() {
        $x("//*[contains(text(),'Неверный формат')]").shouldBe(Condition.visible, Duration.ofSeconds(15)).
                shouldHave(Condition.exactText("Неверный формат"));
    }

    public void errorCardExpired() {
        $x("//*[contains(text(),'Истёк срок')]").shouldBe(Condition.visible, Duration.ofSeconds(15)).
                shouldHave(Condition.exactText("Истёк срок действия карты"));
    }

    public void errorRequiredField() {
        $x("//*[contains(text(),'Поле обязательно')]").shouldBe(Condition.visible, Duration.ofSeconds(15)).
                shouldHave(Condition.exactText("Поле обязательно для заполнения"));
    }
}
