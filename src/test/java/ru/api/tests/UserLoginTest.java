package ru.api.tests;

import edu.methods.SetUp;
import io.qameta.allure.Description;
import io.restassured.response.Response;
import edu.methods.UserAndOrderMethods;
import org.junit.After;
import org.junit.Test;

public class UserLoginTest extends UserAndOrderMethods {

    private UserAndOrderMethods methodsUserLogin = new UserAndOrderMethods();
    protected String accessToken;


    @After
    public void tearDown() {
        deleteUserByToken(accessToken);
    }

    @Test
    @Description("Логин под существующим пользователем с проверкой успешного ответа")
    public void loginWithExistingUserTest() {
        SetUp.setUp();
        String email = generateUniqueEmail();
        String password = generateUniquePassword();
        String name = generateUniqueName();
        Response response = createUniqueUser(email, password, name);
        verifyUserCreation(response, email, name);
        Response loginResponse = methodsUserLogin.loginWithUser(email, password, name);
        String accessToken = verifyLoginSuccess(loginResponse);
    }

    @Test
    @Description("Логин с верным именем, но неверным email и паролем")
    public void loginWithInvalidCredentials() {
        SetUp.setUp();
        String email = generateUniqueEmail();
        String password = generateUniquePassword();
        String name = generateUniqueName();
        Response createUserResponse = createUniqueUser(email, password, name);
        verifyUserCreation(createUserResponse, email, name);
        String invalidEmail = "invalid" + email;
        String invalidPassword = "wrongPassword";
        Response loginResponse = methodsUserLogin.loginWithUser(invalidEmail, invalidPassword, name);
        UserAndOrderMethods.verifyLoginWithInvalidCredentials(loginResponse);
    }

    @Test
    @Description("Логин с неверным именем, пароль верный")
    public void loginWithInvalidLogin() {
        SetUp.setUp();
        String email = generateUniqueEmail();
        String password = generateUniquePassword();
        String name = generateUniqueName();
        Response createUserResponse = createUniqueUser(email, password, name);
        verifyUserCreation(createUserResponse, email, name);
        String invalidName = "invalid" + name;
        String invalidEmail = "invalid" + email;
        Response loginResponse = methodsUserLogin.loginWithUser(invalidEmail, password, invalidName);
        UserAndOrderMethods.verifyLoginWithInvalidCredentials(loginResponse);
    }
}
