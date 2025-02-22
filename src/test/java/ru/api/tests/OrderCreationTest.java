package ru.api.tests;

import edu.methods.SetUp;
import io.qameta.allure.Description;
import io.restassured.response.Response;
import edu.methods.UserAndOrderMethods;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class OrderCreationTest extends UserAndOrderMethods {

    protected String accessToken;

    @After
    public void tearDown() {
            deleteUserByToken(accessToken);
    }

    @Test
    @Description("Создание заказа с авторизацией и с ингредиентами")
    public void createOrderWithAuthorization() {
        SetUp.setUp();
        String email = generateUniqueEmail();
        String password = generateUniquePassword();
        String name = generateUniqueName();
        Response createUserResponse = createUniqueUser(email, password, name);
        createUserResponse.then().statusCode(200);
        Response loginResponse = loginWithUser(email, password, name);
        loginResponse.then().statusCode(200);
        String accessToken = loginResponse.jsonPath().getString("accessToken");
        Response response = UserAndOrderMethods.getRequestToGetIngredients();
        List<String> allingredients = UserAndOrderMethods.getIngredients(response);
        List<String> ingredients = new ArrayList<>();
        ingredients.add(allingredients.get(0));
        ingredients.add(allingredients.get(1));
        OrderSerialization orderSerialization = new OrderSerialization(ingredients);
        Response orderResponse = UserAndOrderMethods.createOrder(orderSerialization, accessToken);
        orderResponse.then().log().all();
        UserAndOrderMethods.verifyOrderCreation(orderResponse);

    }

    @Test
    @Description("Создание заказа с авторизацией но без ингредиентов")
    public void createOrderWithAuthorizationAndNoIngredients() {
        SetUp.setUp();
        String email = generateUniqueEmail();
        String password = generateUniquePassword();
        String name = generateUniqueName();
        Response createUserResponse = createUniqueUser(email, password, name);
        createUserResponse.then().statusCode(200);
        Response loginResponse = loginWithUser(email, password, name);
        loginResponse.then().statusCode(200);
        String accessToken = loginResponse.jsonPath().getString("accessToken");
        List<String> ingredients = new ArrayList<>();
        OrderSerialization orderSerialization = new OrderSerialization(ingredients);
        Response orderResponse = UserAndOrderMethods.createOrder(orderSerialization, accessToken);
        orderResponse.then().log().all();
        UserAndOrderMethods.verifyOrderCreationNoIngredients(orderResponse);


    }

    @Test
    @Description("Создание заказа без авторизации, с ингредиентами")
    public void createOrderWithNoAuthorization() {
        SetUp.setUp();
        Response response = UserAndOrderMethods.getRequestToGetIngredients();
        List<String> allingredients = UserAndOrderMethods.getIngredients(response);
        List<String> ingredients = new ArrayList<>();
        ingredients.add(allingredients.get(0));
        ingredients.add(allingredients.get(1));
        OrderSerialization orderSerialization = new OrderSerialization(ingredients);
        Response orderResponse = UserAndOrderMethods.createOrderWithoutAuthorization(orderSerialization);
        orderResponse.then().log().all();
        UserAndOrderMethods.verifyOrderCreationUnauthorized(orderResponse);
    }

    @Test
    @Description("Создание заказа без авторизации и без ингредиентов")
    public void createOrderWithNoAuthorizationAndIngredients() {
        SetUp.setUp();
        List<String> ingredients = new ArrayList<>();
        OrderSerialization orderSerialization = new OrderSerialization(ingredients);
        Response orderResponseWithoutAuthorization = UserAndOrderMethods.createOrderWithoutAuthorizationAndIngredients(orderSerialization);
        orderResponseWithoutAuthorization.then().log().all();
        UserAndOrderMethods.verifyOrderCreationNoIngredientsUnauthorized(orderResponseWithoutAuthorization);

    }

    @Test
    @Description("Создание заказа с неверным хешем ингредиентов")
    public void createOrderWithInvalidIngredientsHash() {
        SetUp.setUp();
        String email = generateUniqueEmail();
        String password = generateUniquePassword();
        String name = generateUniqueName();
        Response createUserResponse = createUniqueUser(email, password, name);
        createUserResponse.then().statusCode(200);
        Response loginResponse = loginWithUser(email, password, name);
        loginResponse.then().statusCode(200);
        String accessToken = loginResponse.jsonPath().getString("accessToken");
        Response response = UserAndOrderMethods.getRequestToGetIngredients();
        List<String> allingredients = UserAndOrderMethods.getIngredients(response);
        List<String> ingredients = new ArrayList<>();
        ingredients.add("invalid1");
        ingredients.add("invalid2");
        OrderSerialization orderSerialization = new OrderSerialization(ingredients);
        Response orderResponse = UserAndOrderMethods.createOrder(orderSerialization, accessToken);
        Assert.assertEquals("Код ответа должен быть 500",500, orderResponse.getStatusCode());
    }


}
