package ru.netology.data;

import com.google.gson.Gson;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import lombok.Value;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

public class APIHelper {

    private APIHelper() {
    }

    static Gson gson = new Gson();

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

    public static void buyTour(DataHelper.Card card, String status) {
        given()
                .spec(requestSpec())
                .body(gson.toJson(card))
                .when()
                .post("/api/v1/pay")
                .then()
                .statusCode(200)
                .body("status", equalTo(status));
    }

    public static void errorBuyTour(DataHelper.Card card) {
        given()
                .spec(requestSpec())
                .body(gson.toJson(card))
                .when()
                .post("/api/v1/pay")
                .then()
                .statusCode(400);

    }


}
