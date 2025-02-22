package ru.api.tests;

import edu.methods.SetUp;
import io.qameta.allure.Description;
import io.restassured.response.Response;
import edu.methods.UserAndOrderMethods;
import org.junit.After;
import org.junit.Test;

public class UserDataUpdateTest extends UserAndOrderMethods {

    protected String accessToken;


    @After
    public void tearDown() {
        deleteUserByToken(accessToken);
    }

    @Test
    @Description("Изменение email пользователя с авторизацией")
    public void updateUserWithAuthorizationEmail() {
        SetUp.setUp();
        String email = generateUniqueEmail();
        String password = generateUniquePassword();
        String name = generateUniqueName();
        Response createUserResponse = createUniqueUser(email, password, name);
        verifyUserCreation(createUserResponse, email, name);
        String accessToken = createUserResponse.jsonPath().getString("accessToken");
        String newEmail = generateUniqueEmail();
        UserSerialization body = new UserSerialization(newEmail, name, password);
        logRequest(accessToken, String.valueOf(body));
        Response updateResponse = updateUserEmail(accessToken, newEmail, password, name);
        logResponse(updateResponse);
        validateUpdateResponse(updateResponse, newEmail, name);

    }

    @Test
    @Description("Изменение имени пользователя с авторизацией")
    public void updateUserWithAuthorizationName() {
        SetUp.setUp();
        String email = generateUniqueEmail();
        String password = generateUniquePassword();
        String name = generateUniqueName();
        Response createUserResponse = createUniqueUser(email, password, name);
        verifyUserCreation(createUserResponse, email, name);
        String accessToken = createUserResponse.jsonPath().getString("accessToken");
        String newName = generateUniqueName();
        UserSerialization body = new UserSerialization(newName, email, password);
        logRequestName(accessToken, String.valueOf(body));
        Response updateResponse = updateUserName(accessToken, email, password, newName);
        logResponseName(updateResponse);
        validateUpdateNameResponse(updateResponse, newName, email);

    }


    @Test
    @Description("Изменение всех данных пользователя с авторизацией")
    public void updateUserWithAuthorizationAllFields() {
        SetUp.setUp();
        String email = generateUniqueEmail();
        String password = generateUniquePassword();
        String name = generateUniqueName();
        Response createUserResponse = createUniqueUser(email, password, name);
        verifyUserCreation(createUserResponse, email, name);
        String accessToken = createUserResponse.jsonPath().getString("accessToken");
        String newEmail = generateUniqueEmail();
        String newPassword = generateUniquePassword();
        String newName = generateUniqueName();
        UserSerializationForNewData body = new UserSerializationForNewData(newName, newEmail, newPassword);
        logRequestAllFields(accessToken, String.valueOf(body));
        Response updateResponse = updateUserAllFields(accessToken, newEmail, newPassword, newName);
        logResponseAll(updateResponse);

    }

    @Test
    @Description("Изменение email пользователя без авторизации")
    public void updateUserWithoutAuthorizationEmail() {
        SetUp.setUp();
        String email = generateUniqueEmail();
        String password = generateUniquePassword();
        String name = generateUniqueName();
        Response createUserResponse = createUniqueUser(email, password, name);
        verifyUserCreation(createUserResponse, email, name);
        String newEmail = generateUniqueEmail();
        UserSerialization body = new UserSerialization(name, newEmail, password);
        logRequestWithoutAuth(String.valueOf(body));
        Response updateResponse = updateUserEmailWithoutAuth(newEmail, password, name);
        logResponse(updateResponse);
        validateUnauthorizedResponse(updateResponse);


    }

    @Test
    @Description("Изменение имени пользователя без авторизации")
    public void updateUserWithoutAuthorizationName() {
        SetUp.setUp();
        String email = generateUniqueEmail();
        String password = generateUniquePassword();
        String name = generateUniqueName();
        Response createUserResponse = createUniqueUser(email, password, name);
        verifyUserCreation(createUserResponse, email, name);
        String newName = generateUniqueName();
        UserSerialization body = new UserSerialization(newName, email, password);
        logRequestWithoutAuthName(String.valueOf(body));
        Response updateResponse = updateUserNameWithoutAuth(email, password, newName);
        logResponse(updateResponse);
        validateUnauthorizedResponse(updateResponse);


    }

    @Test
    @Description("Изменение всех данных пользователя без авторизации")
    public void updateUserWithoutAuthorizationAllData() {
        SetUp.setUp();
        String email = generateUniqueEmail();
        String password = generateUniquePassword();
        String name = generateUniqueName();
        Response createUserResponse = createUniqueUser(email, password, name);
        verifyUserCreation(createUserResponse, email, name);
        String newEmail = generateUniqueEmail();
        String newPassword = generateUniquePassword();
        String newName = generateUniqueName();
        UserSerializationForNewData body = new UserSerializationForNewData(newName, newEmail, newPassword);
        logRequestWithoutAuthAll(String.valueOf(body));
        Response updateResponse = updateUserAllWithoutAuth(email, newPassword, name);
        logResponse(updateResponse);
        validateUnauthorizedResponse(updateResponse);


    }
}
