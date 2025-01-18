package testApi;

import io.qameta.allure.Description;
import io.restassured.response.Response;
import org.junit.Test;

public class UserOrdersTest extends UserCreationMethods {

    private UserCreationMethods methodsUserLogin = new UserCreationMethods();

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
        Response orderResponse = UserCreationMethods.createOrderWithIngredients(accessToken);
        orderResponse.then().log().all();
        UserCreationMethods.verifyOrderCreation(orderResponse);
        try {
            Response ordersResponse = UserCreationMethods.getUserOrders(accessToken);
            UserCreationMethods.verifyUserOrdersRetrieval(ordersResponse);
        } finally {
            deleteUserByToken(accessToken);
        }
    }


    @Test
    @Description("Проверка создания заказа и получения списка заказов без авторизации")
    public void createOrderAndGetUserOrdersWithoutAuthorization() {
        Response ordersResponse = UserCreationMethods.getUserOrdersWithoutAuthorization();
        UserCreationMethods.verifyUnauthorizedResponse(ordersResponse);
    }
}
