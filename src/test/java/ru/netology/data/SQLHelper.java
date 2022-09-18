package ru.netology.data;

import lombok.SneakyThrows;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;

import java.sql.DriverManager;

public class SQLHelper {
    private static QueryRunner runner = new QueryRunner();

    private SQLHelper() {
    }

    @SneakyThrows
    public static String paymentOrder() {
        var codeSQL = "SELECT payment_id FROM order_entity ORDER BY created DESC LIMIT 1";
        try (var conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/shop",
                "app", "pass")) {
            var result = runner.query(conn, codeSQL, new ScalarHandler<String>());
            return result;
        }
    }

    @SneakyThrows
    public static DataHelper.Payment_entity payment() {
        var codeSQL = "SELECT * FROM payment_entity ORDER BY created DESC LIMIT 1";
        try (var conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/shop",
                "app", "pass")) {
            var result = runner.query(conn, codeSQL, new BeanHandler<>(DataHelper.Payment_entity.class));
            return result;
        }
    }

}
