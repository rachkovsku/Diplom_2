package ru.api.tests;

import edu.methods.SetUp;
import io.qameta.allure.Description;
import io.restassured.response.Response;
import edu.methods.UserAndOrderMethods;
import org.junit.After;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class UserOrdersTest extends UserAndOrderMethods {

    private UserAndOrderMethods methodsUserLogin = new UserAndOrderMethods();
    protected String accessToken;


    @After
    public void tearDown() {
        deleteUserByToken(accessToken);
    }

    @Test
    @Description("Получение заказов авторизованного пользователя")
    public void getUserOrdersWithAuthorization() {
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
        UserAndOrderMethods.getUserOrders(accessToken);
    }




    @Test
    @Description("Проверка получения списка заказов без авторизации")
    public void createOrderAndGetUserOrdersWithoutAuthorization() {
        SetUp.setUp();
        Response ordersResponse = UserAndOrderMethods.getUserOrdersWithoutAuthorization();
        UserAndOrderMethods.verifyUnauthorizedResponse(ordersResponse);
    }
}
