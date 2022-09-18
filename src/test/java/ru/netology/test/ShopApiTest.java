package ru.netology.test;

import com.codeborne.selenide.logevents.SelenideLogger;
import com.google.gson.Gson;
import io.qameta.allure.selenide.AllureSelenide;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.netology.data.DataHelper;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

public class ShopApiTest {

    private static final RequestSpecification requestSpec = new RequestSpecBuilder()
            .setBaseUri("http://localhost")
            .setPort(8080)
            .setAccept(ContentType.JSON)
            .setContentType(ContentType.JSON)
            .log(LogDetail.ALL)
            .build();

    Gson gson = new Gson();

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
        var card = DataHelper.getCard("APPROVED");
        given()
                .baseUri("http://localhost:8080")
//                .spec(requestSpec)
                .body(gson.toJson(card))
                .when()
                .post("/api/v1/pay")
                .then()
                .statusCode(200)
                .body("status", equalTo("APPROVED"));
    }

    //22. Тестирование API(post) покупки тура по карте со статусом DECLINED
    @Test
    public void shouldFailureCardDeclined() {
        var card = DataHelper.getCard("DECLINED");
        given()
                .spec(requestSpec)
                .body(gson.toJson(card))
                .when()
                .post("/api/v1/pay")
                .then()
                .statusCode(200)
                .body("status", equalTo("DECLINED"));
    }

    //23. Тестирование API(post) покупки тура при заполнение поля владелец на кириллице
    @Test
    public void shouldErrorWhenHolderCyrillic() {
        var card = new DataHelper.Card(DataHelper.getCardNumber("APPROVED").getNumber(), DataHelper.generateValidDate().getMonth(),
                DataHelper.generateValidDate().getYear(), DataHelper.generateNameCyrillic().getName(), DataHelper.generateCvcCode().getCode());
        given()
                .spec(requestSpec)
                .body(gson.toJson(card))
                .when()
                .post("/api/v1/pay")
                .then()
                .statusCode(500);
    }

    //24. Тестирование API(post) покупки тура при заполении номером карты, состоящим из менее 16 цифр
    @Test
    public void shouldErrorWhenInvalidNumberCard() {
        var card = new DataHelper.Card(DataHelper.getCardNumberInvalid().getNumber(), DataHelper.generateValidDate().getMonth(),
                DataHelper.generateValidDate().getYear(), DataHelper.generateValidName().getName(), DataHelper.generateCvcCode().getCode());
        given()
                .spec(requestSpec)
                .body(gson.toJson(card))
                .when()
                .post("/api/v1/pay")
                .then()
                .statusCode(500);
    }

    //25. Тестирование API(post) покупки тура при заполении формы прошлым месяцем
    @Test
    public void shouldErrorPastMonth() {
        var card = new DataHelper.Card(DataHelper.getCardNumber("APPROVED").getNumber(), DataHelper.generatePastMonth().getMonth(),
                DataHelper.generatePastMonth().getYear(), DataHelper.generateValidName().getName(), DataHelper.generateCvcCode().getCode());
        given()
                .spec(requestSpec)
                .body(gson.toJson(card))
                .when()
                .post("/api/v1/pay")
                .then()
                .statusCode(500);
    }

    //26. Тестирование API(post) покупки тура при заполении формы прошлым годом
    @Test
    public void shouldErrorPastYear() {
        var card = new DataHelper.Card(DataHelper.getCardNumber("APPROVED").getNumber(), DataHelper.generatePastYear().getMonth(),
                DataHelper.generatePastYear().getYear(), DataHelper.generateValidName().getName(), DataHelper.generateCvcCode().getCode());
        given()
                .spec(requestSpec)
                .body(gson.toJson(card))
                .when()
                .post("/api/v1/pay")
                .then()
                .statusCode(500);
    }

    //27. Тестирование API(post) покупки тура при заполении cvc-кода из 2 цифр
    @Test
    public void shouldErrorInvalidCvcCode() {
        var card = new DataHelper.Card(DataHelper.getCardNumber("APPROVED").getNumber(), DataHelper.generateValidDate().getMonth(),
                DataHelper.generateValidDate().getYear(), DataHelper.generateValidName().getName(), DataHelper.generateInvalidCvcCode().getCode());
        given()
               // .baseUri("http://localhost:8080")
                .spec(requestSpec)
                .body(gson.toJson(card))
                .when()
                .post("/api/v1/pay")
                .then()
                .statusCode(500);
    }


}
