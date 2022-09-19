package ru.netology.data;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import lombok.Value;

public class APIHelper {

    private APIHelper() {
    }

    public static RequestSpecification requestSpec() {
        RequestSpecification requestSpec = new RequestSpecBuilder()
                .setBaseUri("http://localhost")
                .setPort(8080)
                .setAccept(ContentType.JSON)
                .setContentType(ContentType.JSON)
                .log(LogDetail.ALL)
                .build();
        return requestSpec;
    }

    @Value
    public static class Card {
        String number;
        String month;
        String year;
        String holder;
        int cvc;
    }

    public static Card getCard(String status) {
        Card card = new Card(DataHelper.getCardNumber(status).getNumber(), DataHelper.generateValidDate().getMonth(),
                DataHelper.generateValidDate().getYear(), DataHelper.generateValidName().getName(),
                Integer.parseInt(DataHelper.generateCvcCode().getCode()));
        return card;
    }

}
