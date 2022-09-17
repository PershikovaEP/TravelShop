package ru.netology.data;

import com.github.javafaker.Faker;
import lombok.Value;

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

    public static CardNumber getCardNumberApproved() {
        return new CardNumber("1111 2222 3333 4444");
    }

    public static CardNumber getCardNumberDeclined() {
        return new CardNumber("5555 6666 7777 8888");
    }

    public static CardNumber getCardNumberInvalid() {
        return new CardNumber(valueOf(faker.number().numberBetween(1, 15)));
    }

    @Value
    public static class ExpirationDate {
        private String months;
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
        int currentMonth = Integer.parseInt(LocalDate.now().minusMonths(1).format(DateTimeFormatter.ofPattern("MM")));
        int year = 0;
        if (currentMonth > Integer.parseInt(LocalDate.now().format(DateTimeFormatter.ofPattern("MM")))) {
          year = 1;
        }
        return new ExpirationDate(valueOf(currentMonth),
                LocalDate.now().minusYears(year).format(DateTimeFormatter.ofPattern("YY")));
    }

    public static ExpirationDate generatePastYear() {
        return new ExpirationDate(LocalDate.now().format(DateTimeFormatter.ofPattern("MM")),
                LocalDate.now().minusYears(faker.number().numberBetween(1, 22)).format(DateTimeFormatter.ofPattern("YY")));
    }

    public static ExpirationDate generateInvalidDateFrom1Dijit() {
        return new ExpirationDate(faker.numerify("#"),
                faker.numerify("#"));
    }

    public static int invalidMonth() {
        int month = faker.number().numberBetween(13, 99);
        return month;
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

    @Value
    public static class Payment_entity {
        String amount;
        String created;
        String status;
        String transaction_id;
    }
}
