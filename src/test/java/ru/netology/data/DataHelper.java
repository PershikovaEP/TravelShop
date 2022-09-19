package ru.netology.data;

import com.github.javafaker.Faker;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Value;

import java.sql.Date;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import static java.lang.String.valueOf;

public class DataHelper {
    private static Faker faker = new Faker(new Locale("en"));

    private DataHelper() {}

    @Value
    public static class CardNumber {
        private String number;
    }

    public static CardNumber getCardNumber(String status) {
        CardNumber[] cards = {new CardNumber("1111 2222 3333 4444"), new CardNumber("5555 6666 7777 8888")};
        CardNumber result = cards[0];
        if (status.equals("APPROVED")) {
            result = cards[0];
        }
        if (status.equals("DECLINED")) {
            result = cards[1];
        }
        return result;
    }

//    public static CardNumber getCardNumberApproved() {
//        return new CardNumber("1111 2222 3333 4444");
//    }
//
//    public static CardNumber getCardNumberDeclined() {
//        return new CardNumber("5555 6666 7777 8888");
//    }

    public static CardNumber getCardNumberInvalid() {
        return new CardNumber(valueOf(faker.number().numberBetween(1, 15)));
    }

    @Value
    public static class ExpirationDate {
        private String month;
        private String year;
    }

    public static ExpirationDate generateValidDate() {
        return new ExpirationDate(LocalDate.now().plusMonths(faker.number().numberBetween(1, 12)).format(DateTimeFormatter.ofPattern("MM")),
                LocalDate.now().plusYears(faker.number().numberBetween(1, 5)).format(DateTimeFormatter.ofPattern("YY"))); //максимальный срок действия карты возьмем 5 лет
    }

    public static ExpirationDate generateDateMore5Year() {
        return new ExpirationDate(LocalDate.now().plusMonths(faker.number().numberBetween(1, 12)).format(DateTimeFormatter.ofPattern("MM")),
                LocalDate.now().plusYears(faker.number().numberBetween(6, 60)).format(DateTimeFormatter.ofPattern("YY"))); //максимальный срок действия карты возьмем 5 лет
    }

    public static ExpirationDate generateCurrentDate() {
        return new ExpirationDate(LocalDate.now().format(DateTimeFormatter.ofPattern("MM")),
                LocalDate.now().format(DateTimeFormatter.ofPattern("YY")));
    }

    public static ExpirationDate generatePastMonth() {
        String currentMonth = LocalDate.now().minusMonths(1).format(DateTimeFormatter.ofPattern("MM"));
        int year = 0;
        if (Integer.parseInt(currentMonth) > Integer.parseInt(LocalDate.now().format(DateTimeFormatter.ofPattern("MM")))) {
          year = 1;
        }
        return new ExpirationDate(currentMonth,
                LocalDate.now().minusYears(year).format(DateTimeFormatter.ofPattern("YY")));
    }

    public static ExpirationDate generatePastYear() {
        return new ExpirationDate(LocalDate.now().format(DateTimeFormatter.ofPattern("MM")),
                LocalDate.now().minusYears(1).format(DateTimeFormatter.ofPattern("YY")));
    }

    public static ExpirationDate generateInvalidDateFrom1Dijit() {
        return new ExpirationDate(faker.numerify("#"),
                faker.numerify("#"));
    }

    public static String invalidMonth() {
        int month = faker.number().numberBetween(13, 99);
        return valueOf(month);
    }

    @Value
    public static class CvcCode {
        private String code;
    }

    public static CvcCode generateCvcCode() {
        return new CvcCode(faker.numerify("###"));
    }

    public static CvcCode generateInvalidCvcCode() {
        return new CvcCode(faker.numerify("#"));
    }

    @Value
    public static class Name {
        private String name;
    }

    public static Name generateValidName() {
        return new Name(faker.name().fullName());
    }

    public static Name generateNameCyrillic() {
        Faker faker = new Faker(new Locale("ru"));
        return new Name(faker.name().fullName());
    }

    public static Name generateNameNumbers() {
        return new Name(faker.name().fullName() + 123);
    }

    public static Name generateNameSymbol() {
        return new Name(faker.name().fullName() + "%?");
    }

    public static Name generateName1Letter() {
        return new Name("j");
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class PaymentEntity {
        String id;
        int amount;
        Date created;
        String status;
        String transaction_id;
    }



}
