package ru.api.tests;

import io.qameta.allure.Description;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import edu.methods.UserAndOrderMethods;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class UserOrdersTest extends UserAndOrderMethods {

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
    @Description("Получение заказов авторизованного пользователя")
    public void getUserOrdersWithAuthorization() {
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
        UserAndOrderMethods.getUserOrders(accessToken);
    }




    @Test
    @Description("Проверка создания заказа и получения списка заказов без авторизации")
    public void createOrderAndGetUserOrdersWithoutAuthorization() {
        Response ordersResponse = UserAndOrderMethods.getUserOrdersWithoutAuthorization();
        UserAndOrderMethods.verifyUnauthorizedResponse(ordersResponse);
    }
}
