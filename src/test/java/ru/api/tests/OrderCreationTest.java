package ru.api.tests;

import io.qameta.allure.Description;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import edu.methods.UserAndOrderMethods;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class OrderCreationTest extends UserAndOrderMethods {

    private UserAndOrderMethods methodsUserLogin = new UserAndOrderMethods();
    protected String accessToken;

    @Before
    public void setUp() {
        RestAssured.baseURI = "https://stellarburgers.nomoreparties.site";
    }

    @After
    public void tearDown() {
            deleteUserByToken(accessToken);
    }

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
        Response orderResponse = UserAndOrderMethods.createOrderWithIngredients(accessToken);
        orderResponse.then().log().all();
        UserAndOrderMethods.verifyOrderCreation(orderResponse);

    }

    @Test
    @Description("Создание заказа с авторизацией но без ингредиентов")
    public void createOrderWithAuthorizationAndNoIngredients() {
        String email = generateUniqueEmail();
        String password = generateUniquePassword();
        String name = generateUniqueName();
        Response createUserResponse = createUniqueUser(email, password, name);
        createUserResponse.then().statusCode(200);
        Response loginResponse = loginWithUser(email, password, name);
        loginResponse.then().statusCode(200);
        String accessToken = loginResponse.jsonPath().getString("accessToken");
        Response orderResponse = UserAndOrderMethods.createOrderWitNoIngredients(accessToken);
        orderResponse.then().log().all();
        UserAndOrderMethods.verifyOrderCreationNoIngredients(orderResponse);


    }

    @Test
    @Description("Создание заказа без авторизации, с ингредиентами")
    public void createOrderWithNoAuthorization() {
        Response orderResponse = UserAndOrderMethods.createOrderWithoutAuthorization();
        orderResponse.then().log().all();
        UserAndOrderMethods.verifyOrderCreationUnauthorized(orderResponse);
    }

    @Test
    @Description("Создание заказа без авторизации и без ингредиентов")
    public void createOrderWithNoAuthorizationAndIngredients() {
        Response orderResponseWithoutAuthorization = UserAndOrderMethods.createOrderWithoutAuthorizationAndIngredients();
        orderResponseWithoutAuthorization.then().log().all();
        UserAndOrderMethods.verifyOrderCreationNoIngredientsUnauthorized(orderResponseWithoutAuthorization);
        Response orderResponseWithoutIngredients = UserAndOrderMethods.createOrderWithoutAuthorizationAndIngredients();
        UserAndOrderMethods.verifyOrderCreationNoAuthorizedAndNoIngredients(orderResponseWithoutIngredients);
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
            Response orderResponse = UserAndOrderMethods.createOrderWithInvalidIngredientsHash(accessToken);
            orderResponse.then().log().all();
            UserAndOrderMethods.verifyOrderCreationInvalidIngredientsHash(orderResponse);
            orderResponse.then().statusCode(500);
        } catch (Exception e) {
            System.out.println("Ошибка при создании заказа: " + e.getMessage());
        }
    }


}
