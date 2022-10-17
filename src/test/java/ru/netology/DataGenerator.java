package ru.netology;

import lombok.Value;
import com.github.javafaker.Faker;

import java.util.Locale;

public class DataGenerator {

    private DataGenerator() {
    }

    private static final Faker faker = new Faker(new Locale("ru"));

    @Value
    public static class AuthData {
        String login;
        String password;
        String status;
    }

    public static String getInvalidLogin() {
        return faker.name().fullName();
    }

    public static String getInvalidPassword() {
        return faker.internet().password();
    }

}
