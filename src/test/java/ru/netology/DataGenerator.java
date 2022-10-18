package ru.netology;

import com.google.gson.Gson;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import lombok.Value;
import com.github.javafaker.Faker;
import io.restassured.specification.RequestSpecification;

import java.util.Locale;

import static com.codeborne.selenide.Selenide.open;
import static io.restassured.RestAssured.given;

public class DataGenerator {

    private DataGenerator() {
    }

    private static final Faker faker = new Faker(new Locale("ru"));

    public static AuthData getNewUser(String status) {
        Gson gson = new Gson();
        AuthData user = new AuthData(faker.name().username(), faker.internet().password(), status);

            open("http://localhost:9999");

        final RequestSpecification requestSpec = new RequestSpecBuilder()
                .setBaseUri("http://localhost")
                .setPort(9999)
                .setAccept(ContentType.JSON)
                .setContentType(ContentType.JSON)
                .log(LogDetail.ALL)
                .build();

       given()
               .spec(requestSpec)
               .body(gson.toJson(user))
               .when()
               .post("/api/system/users")
               .then()
               .statusCode(200);
       return user;
   }

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
