package ru.api.tests;

import io.qameta.allure.Description;
import io.restassured.response.Response;
import edu.methods.UserCreationMethods;
import org.junit.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class UserCreationTest extends UserCreationMethods {

    private final UserCreationMethods userActions = new UserCreationMethods();

    @Test
    @Description("Создание уникального пользователя и удаление после проверки")
    public void creationUniqueUser() {
        String email = generateUniqueEmail();
        String password = generateUniquePassword();
        String name = generateUniqueName();
        Response response = createUniqueUser(email, password, name);
        String accessToken = userActions.verifyUserCreation(response, email, name);

        deleteUserByToken(accessToken);
    }

    @Test
    @Description("Создание зарегистрированного пользователя и проверка на ошибку повторной регистрации")
    public void createExistingUser() {
        String email = generateUniqueEmail();
        String password = generateUniquePassword();
        String name = generateUniqueName();
        Response firstResponse = userActions.createUniqueUser(email, password, name);
        userActions.verifyUserCreationSuccess(firstResponse);
        accessToken = firstResponse.jsonPath().getString("accessToken");

        Response secondResponse = userActions.createUniqueUser(email, password, name);
        userActions.verifyDuplicateUserError(secondResponse);

        Response deleteResponse = given()
                .header("Authorization", "accessToken")
                .when()
                .delete("/api/auth/user");
        System.out.println("Delete Response Code: " + deleteResponse.getStatusCode());
        assertThat(deleteResponse.getStatusCode(), is(202));
    }

    @Test
    @Description("Создание пользователя без пароля и проверка на ошибку")
    public void createUserWithoutRequiredFieldPassword() {
        String email = generateUniqueEmail();
        String name = generateUniqueName();
        Response response = createUniqueUserWithoutPassword(email, name);
        userActions.verifyUserCreationFailurePassword(response);
    }

    @Test
    @Description("Создание пользователя без email и проверка на ошибку")
    public void createUserWithoutRequiredFieldEmail() {
        String password = generateUniquePassword();
        String name = generateUniqueName();
        Response response = createUniqueUserWithoutEmail(password, name);
        userActions.verifyUserCreationFailureEmail(response, "Email, password and name are required fields");
    }

    @Test
    @Description("Создание пользователя без имени и проверка на ошибку")
    public void createUserWithoutRequiredFieldName() {
        String password = generateUniquePassword();
        String email = generateUniqueEmail();
        Response response = createUniqueUserWithoutName(password, email);
        userActions.verifyUserCreationFailureName(response, "Email, password and name are required fields");
    }
}