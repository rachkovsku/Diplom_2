package ru.api.tests;

import io.qameta.allure.Description;
import io.restassured.response.Response;
import edu.methods.UserCreationMethods;
import org.junit.Test;

public class UserLoginTest extends UserCreationMethods {

    private UserCreationMethods methodsUserLogin = new UserCreationMethods();

    @Test
    @Description("Логин под существующим пользователем с проверкой успешного ответа")
    public void loginWithExistingUserTest() {
        String email = generateUniqueEmail();
        String password = generateUniquePassword();
        String name = generateUniqueName();
        Response response = createUniqueUser(email, password, name);
        verifyUserCreation(response, email, name);
        Response loginResponse = methodsUserLogin.loginWithUser(email, password, name);
        String accessToken = verifyLoginSuccess(loginResponse);
        deleteUserByToken(accessToken);
    }

    @Test
    @Description("Логин с верным именем, но неверным email и паролем")
    public void loginWithInvalidCredentials() {
        String email = generateUniqueEmail();
        String password = generateUniquePassword();
        String name = generateUniqueName();
        Response createUserResponse = createUniqueUser(email, password, name);
        verifyUserCreation(createUserResponse, email, name);
        String accessToken = createUserResponse.jsonPath().getString("accessToken");
        String invalidEmail = "invalid" + email;
        String invalidPassword = "wrongPassword";
        Response loginResponse = methodsUserLogin.loginWithUser(invalidEmail, invalidPassword, name);
        UserCreationMethods.verifyLoginWithInvalidCredentials(loginResponse);
        deleteUserByToken(accessToken);
    }

    @Test
    @Description("Логин с неверным именем, пароль верный")
    public void loginWithInvalidLogin() {
        String email = generateUniqueEmail();
        String password = generateUniquePassword();
        String name = generateUniqueName();
        Response createUserResponse = createUniqueUser(email, password, name);
        verifyUserCreation(createUserResponse, email, name);
        String accessToken = createUserResponse.jsonPath().getString("accessToken");
        String invalidName = "invalid" + name;
        Response loginResponse = methodsUserLogin.loginWithUser(email, password, invalidName);
        UserCreationMethods.verifyLoginWithInvalidCredentials(loginResponse);
        deleteUserByToken(accessToken);
    }
}
