package ru.netology;

import org.junit.jupiter.api.*;
import ru.netology.DataGenerator.*;
import com.github.javafaker.Faker;
import com.google.gson.Gson;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Locale;

import static io.restassured.RestAssured.given;

import static com.codeborne.selenide.Selenide.*;
import static com.codeborne.selenide.Condition.*;

public class TestRegimTest {

    private static final Faker faker = new Faker(new Locale("ru"));
    private final DataGenerator.AuthData newActiveUser = getNewUser("active");
    private final DataGenerator.AuthData newBlockedUser = getNewUser("blocked");
    private final String invalidLogin = DataGenerator.getInvalidLogin();
    private final String invalidPassword = DataGenerator.getInvalidPassword();

    @BeforeEach
    void setUp() {
        open("http://localhost:9999");
    }

    private static final RequestSpecification requestSpec = new RequestSpecBuilder()
            .setBaseUri("http://localhost")
            .setPort(9999)
            .setAccept(ContentType.JSON)
            .setContentType(ContentType.JSON)
            .log(LogDetail.ALL)
            .build();

    public static AuthData getNewUser(String status) {
        Gson gson = new Gson();
        AuthData user = new AuthData(faker.name().username(), faker.internet().password(), status);

        given()
                .spec(requestSpec)
                .body(gson.toJson(user))
                .when()
                .post("/api/system/users")
                .then()
                .statusCode(200);
        return user;
    }


    //--- Проверка на заполнение

    // Форма не заполнена

    @Test
    void shouldGetErrorIfEmptyForm() {
        $("[data-test-id='action-login']").click();
        $("[data-test-id='login'] .input__sub").shouldBe(visible).shouldHave(text("Поле обязательно для заполнения"));
        $("[data-test-id='password'] .input__sub").shouldBe(visible).shouldHave(text("Поле обязательно для заполнения"));
    }

// Активный пользователь. Пароль и логин корректны

    @Test
    void shouldSuccessAuthIfActiveUser() {
        $("[data-test-id='login'] .input__control").setValue(newActiveUser.getLogin());
        $("[data-test-id='password'] .input__control").setValue(newActiveUser.getPassword());
        $("[data-test-id='action-login']").click();
        $("h2").shouldBe(visible).shouldHave(text("Личный кабинет"));
    }

    // Активный пользователь. Пароль некорректен
    @Test
    void shouldGetErrorIfActiveUserAndIncorrectPassword() {
        $("[data-test-id='login'] .input__control").setValue(newActiveUser.getLogin());
        $("[data-test-id='password'] .input__control").setValue(invalidPassword);
        $("[data-test-id='action-login']").click();
        $("[data-test-id=error-notification]").shouldBe(visible).shouldHave(text("Ошибка! Неверно указан логин или пароль"));
    }

    // Активный пользователь. Логин некорректен
    @Test
    void shouldGetErrorIfActiveUserAndIncorrectLogin() {
        $("[data-test-id='login'] .input__control").setValue(invalidLogin);
        $("[data-test-id='password'] .input__control").setValue(newActiveUser.getPassword());
        $("[data-test-id='action-login']").click();
        $("[data-test-id=error-notification]").shouldBe(visible).shouldHave(text("Ошибка! Неверно указан логин или пароль"));
    }

    // Активный пользователь. Нет логина
    @Test
    void shouldGetErrorIfActiveUserAndNoneLogin() {
        $("[data-test-id='password'] .input__control").setValue(newActiveUser.getPassword());
        $("[data-test-id='action-login']").click();
        $("[data-test-id='login'] .input__sub").shouldBe(visible).shouldHave(text("Поле обязательно для заполнения"));
    }

    // Активный пользователь. Нет пароля
    @Test
    void shouldGetErrorIfActiveUserAndNonePassword() {
        $("[data-test-id='login'] .input__control").setValue(newActiveUser.getLogin());
        $("[data-test-id='action-login']").click();
        $("[data-test-id='password'] .input__sub").shouldBe(visible).shouldHave(text("Поле обязательно для заполнения"));
    }

    // --- проверки на заблокированного пользователя

// Пользователь заблокирован. Логин и пароль корректны.

    @Test
    void shouldGetErrorIfBlockedUser() {
        $("[data-test-id='login'] .input__control").setValue(newBlockedUser.getLogin());
        $("[data-test-id='password'] .input__control").setValue(newBlockedUser.getPassword());
        $("[data-test-id='action-login']").click();
        $("[data-test-id=error-notification]").shouldBe(visible).shouldHave(text("Ошибка! Пользователь заблокирован"));
    }

    // Пользователь заблокирован. Неверный логин
    @Test
    void shouldGetErrorIfBlockedUserAndIncorrectLogin() {
        $("[data-test-id='login'] .input__control").setValue(invalidLogin);
        $("[data-test-id='password'] .input__control").setValue(newBlockedUser.getPassword());
        $("[data-test-id='action-login']").click();
        $("[data-test-id=error-notification]").shouldBe(visible).shouldHave(text("Ошибка! Неверно указан логин или пароль"));
    }

    // Пользователь заблокирован. Неверный пароль
    @Test
    void shouldGetErrorIfBlockedUserAndIncorrectPassword() {
        $("[data-test-id='login'] .input__control").setValue(newBlockedUser.getLogin());
        $("[data-test-id='password'] .input__control").setValue(invalidPassword);
        $("[data-test-id='action-login']").click();
        $("[data-test-id=error-notification]").shouldBe(visible).shouldHave(text("Ошибка! Неверно указан логин или пароль"));
    }

    // Пользователь заблокирован. Нет логина
    @Test
    void shouldGetErrorIfBlockedUserAndNoneLogin() {
        $("[data-test-id='password'] .input__control").setValue(newBlockedUser.getPassword());
        $("[data-test-id='action-login']").click();
        $("[data-test-id='login'] .input__sub").shouldBe(visible).shouldHave(text("Поле обязательно для заполнения"));
    }

    // Пользователь заблокирован. Нет пароля
    @Test
    void shouldGetErrorIfBlockedUserAndNonePassword() {
        $("[data-test-id='login'] .input__control").setValue(newBlockedUser.getLogin());
        $("[data-test-id='action-login']").click();
        $("[data-test-id='password'] .input__sub").shouldBe(visible).shouldHave(text("Поле обязательно для заполнения"));
    }
}