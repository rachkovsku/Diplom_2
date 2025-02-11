package ru.api.tests;

import io.qameta.allure.Description;
import io.restassured.response.Response;
import edu.methods.UserCreationMethods;
import org.junit.Test;

public class OrderCreationTest extends UserCreationMethods {

    private UserCreationMethods methodsUserLogin = new UserCreationMethods();

    @Test
    @Description("Создание заказа с авторизацией и с ингредиентами")
    public void createOrderWithAuthorization() {
        String email = generateUniqueEmail();
        String password = generateUniquePassword();
        String name = generateUniqueName();
        Response createUserResponse = createUniqueUser(email, password, name);
        createUserResponse.then().statusCode(200);
        Response loginResponse = loginWithUser(email, password, name);
        loginResponse.then().statusCode(200);
        String accessToken = loginResponse.jsonPath().getString("accessToken");
        Response orderResponse = UserCreationMethods.createOrderWithIngredients(accessToken);
        orderResponse.then().log().all();
        UserCreationMethods.verifyOrderCreation(orderResponse);
        deleteUserByToken(accessToken);
    }

    @Test
    @Description("Создание заказа с авторизацией но без ингредиентов")
    public void createOrderWithAuthorizationAndNoIngredients() {
        String email = generateUniqueEmail();
        String password = generateUniquePassword();
        String name = generateUniqueName();
        Response createUserResponse = createUniqueUser(email, password, name);
        Response loginResponse = loginWithUser(email, password, name);
        String accessToken = loginResponse.jsonPath().getString("accessToken");
        Response orderResponse = UserCreationMethods.createOrderWitNoIngredients(accessToken);
        orderResponse.then().log().all();
        UserCreationMethods.verifyOrderCreationNoIngredients(orderResponse);
        deleteUserByToken(accessToken);
    }

    @Test
    @Description("Создание заказа без авторизации, с ингредиентами")
    public void createOrderWithNoAuthorization() {
        Response orderResponse = UserCreationMethods.createOrderWithoutAuthorization();
        orderResponse.then().log().all();
        UserCreationMethods.verifyOrderCreationUnauthorized(orderResponse);
    }

    @Test
    @Description("Создание заказа без авторизации и без ингредиентов")
    public void createOrderWithNoAuthorizationAndIngredients() {
        Response orderResponseWithoutAuthorization = UserCreationMethods.createOrderWithoutAuthorizationAndIngredients();
        orderResponseWithoutAuthorization.then().log().all();
        UserCreationMethods.verifyOrderCreationNoIngredientsUnauthorized(orderResponseWithoutAuthorization);
        Response orderResponseWithoutIngredients = UserCreationMethods.createOrderWithoutAuthorizationAndIngredients();
        UserCreationMethods.verifyOrderCreationNoAuthorizedAndNoIngredients(orderResponseWithoutIngredients);
    }

    @Test
    @Description("Создание заказа с неверным хешем ингредиентов")
    public void createOrderWithInvalidIngredientsHash() {
        String email = generateUniqueEmail();
        String password = generateUniquePassword();
        String name = generateUniqueName();
        Response createUserResponse = createUniqueUser(email, password, name);
        createUserResponse.then().statusCode(200);
        Response loginResponse = loginWithUser(email, password, name);
        loginResponse.then().statusCode(200);
        String accessToken = loginResponse.jsonPath().getString("accessToken");
        try {
            Response orderResponse = UserCreationMethods.createOrderWithInvalidIngredientsHash(accessToken);
            orderResponse.then().log().all();
            UserCreationMethods.verifyOrderCreationInvalidIngredientsHash(orderResponse);
            orderResponse.then().statusCode(400);
        } catch (Exception e) {
            System.out.println("Ошибка при создании заказа: " + e.getMessage());
        } finally {
            deleteUserByToken(accessToken);
        }
    }
}
