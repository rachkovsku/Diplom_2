package edu.methods;

import com.google.gson.Gson;
import io.qameta.allure.Step;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import ru.api.tests.OrderSerialization;
import ru.api.tests.UserSerialization;
import ru.api.tests.UserSerializationForNewData;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

// Общие методы для тестовых классов
public class UserAndOrderMethods {


    @Step("Удалить пользователя по токену")
    public void deleteUserByToken(String accessToken) {

        if (accessToken != null) {
            Response deleteResponse = given().header("Authorization", accessToken).when().delete("/api/auth/user");
            System.out.println("Delete Response Code = " + deleteResponse.getStatusCode());
            System.out.println("Пользователь успешно удален");
        }
    }

    @Step("Создать уникального пользователя")
    public Response createUniqueUser(String email, String password, String name) {
        UserSerialization body = new UserSerialization(name, email, password);
        Response response = given().contentType(ContentType.JSON).body(body).when().post("/api/auth/register");
        System.out.println("Пользователь успешно создан: " + email);
        return response;
    }

    @Step("Проверить ответ на создание уникального пользователя")
    public String verifyUserCreation(Response response, String email, String name) {
        System.out.println("Response Code: " + response.getStatusCode());
        System.out.println("Response body:\n" + response.getBody().prettyPrint());
        assertThat(response.getStatusCode(), is(200));
        assertThat(response.jsonPath().getBoolean("success"), is(true));
        assertThat(response.jsonPath().getString("user.email"), is(email));
        assertThat(response.jsonPath().getString("user.name"), is(name));
        String accessToken = "Bearer " + response.jsonPath().getString("accessToken");
        assertThat(accessToken, not(isEmptyOrNullString()));
        assertThat(response.jsonPath().getString("refreshToken"), not(isEmptyOrNullString()));
        return accessToken;
    }

    @Step("Проверка успешного создания пользователя")
    public void verifyUserCreationSuccess(Response response) {
        System.out.println("Response Code: " + response.getStatusCode());
        System.out.println("Response body:\n" + response.getBody().prettyPrint());
        assertThat(response.getStatusCode(), is(200));
        assertThat(response.jsonPath().getBoolean("success"), is(true));
    }

    @Step("Проверка ошибки при повторной регистрации пользователя")
    public void verifyDuplicateUserError(Response response) {
        System.out.println("Response Code: " + response.getStatusCode());
        System.out.println("Response body:\n" + response.getBody().prettyPrint());
        assertThat(response.getStatusCode(), is(403));
        assertThat(response.jsonPath().getString("message"), equalTo("User already exists"));
    }

    @Step("Создать уникального пользователя без password")
    public Response createUniqueUserWithoutPassword(String email, String name) {
        UserSerialization body = new UserSerialization(name, email);
        return given().contentType(ContentType.JSON).body(body).when().post("/api/auth/register");
    }


    @Step("Создать уникального пользователя без email")
    public Response createUniqueUserWithoutEmail(String password, String name) {
        UserSerialization body = new UserSerialization(password, name);
        return given().contentType(ContentType.JSON).body(body).when().post("/api/auth/register");
    }

    @Step("Проверка ответа на неудачное создание пользователя без email")
    public void verifyUserCreationFailureEmail(Response response, String expectedMessage) {
        System.out.println("Response Code: " + response.getStatusCode());
        System.out.println("Response body:\n" + response.getBody().prettyPrint());
        assertThat(response.getStatusCode(), is(403));
        assertThat(response.jsonPath().getBoolean("success"), is(false));
        assertThat(response.jsonPath().getString("message"), is(expectedMessage));
    }

    @Step("Создать уникального пользователя без name")
    public Response createUniqueUserWithoutName(String password, String email) {
        UserSerialization body = new UserSerialization(password, email);
        return given().contentType(ContentType.JSON).body(body).when().post("/api/auth/register");
    }


    public String generateUniqueEmail() {
        String uniqueId = UUID.randomUUID().toString().substring(0, 6);
        return "user" + uniqueId + "@yandex.ru";
    }

    public String generateUniquePassword() {
        return "pass" + UUID.randomUUID().toString().substring(0, 6);
    }

    public String generateUniqueName() {
        return "User" + UUID.randomUUID().toString().substring(0, 6);
    }


    @Step("Логин под существующим пользователем")
    public Response loginWithUser(String email, String password, String name) {
        System.out.println("Логин с данными - email: " + email + ", password: " + password + ", name: " + name);
        UserSerialization body = new UserSerialization(name, email, password);
        Response response = given().contentType(ContentType.JSON).body(body).when().post("/api/auth/login");
        System.out.println("Пользователь успешно вошел в систему");
        return response;
    }

    @Step("Проверка ответа успешного логина пользователя")
    public String verifyLoginSuccess(Response response) {
        System.out.println("Response Code: " + response.getStatusCode());
        System.out.println("Response ru.api.tests.Body:\n" + response.getBody().prettyPrint());
        assertThat(response.getStatusCode(), equalTo(200));
        assertThat(response.jsonPath().getBoolean("success"), is(true));
        return response.jsonPath().getString("accessToken");
    }

    @Step("Проверка ответа выполнения логина с неверными email и паролем")
    public static void verifyLoginWithInvalidCredentials(Response loginResponse) {
        loginResponse.then().statusCode(401).body("message", equalTo("email or password are incorrect"));
    }


    @Step("Проверка обновления email пользователя с авторизацией")
    public static Response updateUserEmail(String accessToken, String newEmail, String password, String name) {
        UserSerialization body = new UserSerialization(name, newEmail, password);
        return given().contentType(ContentType.JSON).header("Authorization", accessToken).body(body).when().patch("/api/auth/user");
    }

    @Step("Логирование данных запроса обновления email пользователя с авторизацией")
    public static void logRequest(String accessToken, String requestBody) {
        System.out.println("Запрос на обновление email:");
        System.out.println("Токен авторизации: " + accessToken);
        System.out.println("Тело запроса: " + requestBody);
    }

    @Step("Логирование данных ответа обновления email пользователя с авторизацией")
    public static void logResponse(Response response) {
        System.out.println("Ответ после обновления email:");
        System.out.println(response.prettyPrint());
    }

    @Step("Проверка что статус код 200 и данные email пользователя с авторизацией обновлены")
    public static void validateUpdateResponse(Response response, String newEmail, String name) {
        response.then().statusCode(200).body("success", equalTo(true)).body("user.email", equalTo(newEmail)).body("user.name", equalTo(name)); // Проверяеми что имя осталось прежним
    }


    @Step("Проверка обновления name пользователя с авторизацией")
    public static Response updateUserName(String accessToken, String email, String password, String newName) {
        UserSerialization body = new UserSerialization(newName, email, password);
        return given().contentType(ContentType.JSON).header("Authorization", accessToken).body(body).when().patch("/api/auth/user");
    }

    @Step("Логирование данных запроса обновления name пользователя с авторизацией")
    public static void logRequestName(String accessToken, String requestBody) {
        System.out.println("Запрос на обновление name:");
        System.out.println("Токен авторизации: " + accessToken);
        System.out.println("Тело запроса: " + requestBody);
    }

    @Step("Логирование данных ответа обновления name пользователя с авторизацией")
    public static void logResponseName(Response response) {
        System.out.println("Ответ после обновления name:");
        System.out.println(response.prettyPrint());
    }

    @Step("Проверка, что статус код 200 и name пользователя с авторизацией обновлено")
    public static void validateUpdateNameResponse(Response response, String newName, String email) {
        response.then().statusCode(200).body("success", equalTo(true)).body("user.name", equalTo(newName)) // Проверяем что name обновилось
                .body("user.email", equalTo(email)); // Проверяем что email остался прежним
    }


    @Step("Проверка обновления всех полей пользователя с авторизацией")
    public static Response updateUserAllFields(String accessToken, String newEmail, String newPassword, String newName) {
        UserSerializationForNewData body = new UserSerializationForNewData(newName, newEmail, newPassword);
        return given().contentType(ContentType.JSON).header("Authorization", accessToken).body(body).when().patch("/api/auth/user");
    }

    @Step("Логирование данных запроса на обновление всех полей пользователя с авторизацией")
    public static void logRequestAllFields(String accessToken, String requestBody) {
        System.out.println("Запрос на обновление всех полей:");
        System.out.println("Токен авторизации: " + accessToken);
        System.out.println("Тело запроса: " + requestBody);
    }

    @Step("Логирование данных ответа обновления всех данных пользователя с авторизацией")
    public static void logResponseAll(Response response) {
        System.out.println("Ответ после обновления всех полей:");
        System.out.println(response.prettyPrint());
    }


    @Step("Логирование данных запроса на обновление email без авторизации")
    public static void logRequestWithoutAuth(String requestBody) {
        System.out.println("Запрос на обновление email без авторизации:");
        System.out.println("Тело запроса: " + requestBody);
    }

    @Step("Обновление email пользователя без авторизации")
    public static Response updateUserEmailWithoutAuth(String newEmail, String password, String name) {
        UserSerialization body = new UserSerialization(newEmail, name, password);
        return given().contentType(ContentType.JSON).body(body).when().patch("/api/auth/user");
    }

    @Step("Проверка ошибки доступа без авторизации")
    public static void validateUnauthorizedResponse(Response response) {
        response.then().statusCode(401).body("success", equalTo(false)).body("message", equalTo("You should be authorised"));
    }

    @Step("Обновление name пользователя без авторизации")
    public static Response updateUserNameWithoutAuth(String email, String password, String newName) {
        UserSerialization body = new UserSerialization(email, newName, password);
        return given().contentType(ContentType.JSON).body(body).when().patch("/api/auth/user");
    }

    @Step("Логирование данных запроса на обновление name без авторизации")
    public static void logRequestWithoutAuthName(String requestBody) {
        System.out.println("Запрос на обновление name без авторизации:");
        System.out.println("Тело запроса: " + requestBody);
    }

    @Step("Обновление password пользователя без авторизации")
    public static Response updateUserPasswordWithoutAuth(String email, String newPassword, String name) {
        UserSerialization body = new UserSerialization(email, name, newPassword);
        return given().contentType(ContentType.JSON).body(body).when().patch("/api/auth/user");
    }


    @Step("Обновление всех данных пользователя без авторизации")
    public static Response updateUserAllWithoutAuth(String newEmail, String newPassword, String newName) {
        UserSerializationForNewData body = new UserSerializationForNewData(newName, newEmail, newPassword);
        return given().contentType(ContentType.JSON).body(body).when().patch("/api/auth/user");
    }

    @Step("Логирование данных запроса на обновление всех данных пользователя без авторизации")
    public static void logRequestWithoutAuthAll(String requestBody) {
        System.out.println("Запрос на обновление всех данных без авторизации:");
        System.out.println("Тело запроса: " + requestBody);
    }


    @Step("Метод для запроса создания заказа с ингредиентами и авторизацией")
    public static Response createOrder(OrderSerialization orderSerialization, String accessToken) {
        System.out.println("Проверка успешного создания заказа...");
        Response response = given().contentType(ContentType.JSON).header("Authorization", accessToken).body(orderSerialization).when().post("/api/orders");
        return response;
    }


    @Step("Запрос на получение игредиентов")
    public static Response getRequestToGetIngredients(){
       Response response = given().contentType(ContentType.JSON).when().get("/api/ingredients");
       return response;
    }

    @Step("Получение ids ингредиентов")
    public static List<String> getIngredients(Response response) {
        return response.then().extract().jsonPath().getList("data._id");
    }

    @Step("Метод для проверки кода и тела ответа успешности создания заказа с ингредиентами и авторизацией")
    public static void verifyOrderCreation(Response orderResponse) {
        System.out.println("Проверка ответа успешного создания заказа...");
        orderResponse.then().statusCode(200).body("success", equalTo(true));
        System.out.println("Создание заказа проверено успешно");
    }


    @Step("Метод для запроса создания заказа с авторизацией но без ингредиентов")
    public static Response createOrderWitNoIngredients(String accessToken) {
        OrderSerialization ingredients = new OrderSerialization(List.of("", ""));
        Gson gson = new Gson();
        String jsonString = gson.toJson(ingredients);
        return given().header("accept", "application/json").header("Authorization", accessToken).body(jsonString).when().post("/api/orders");
    }

    @Step("Метод для проверки кода и тела ответа ошибки создания заказа без ингредиентов но с авторизацией")
    public static void verifyOrderCreationNoIngredients(Response orderResponse) {
        orderResponse.then().statusCode(400).body("success", equalTo(false)).body("message", equalTo("Ingredient ids must be provided"));
    }


    @Step("Метод для проверки ошибки запроса создания заказа с ингредиентами но без авторизации")
    public static Response createOrderWithoutAuthorization(OrderSerialization orderSerialization) {
        System.out.println("Выполняется метод для проверки ошибки запроса создания заказа без авторизации, с ингредиентами...");
        Response response = given().contentType(ContentType.JSON).body(orderSerialization).when().post("/api/orders");
        return response;
    }

    @Step("Метод для проверки кода и тела ответа при отсутствии авторизации создания заказа с ингредиентами")
    public static void verifyOrderCreationUnauthorized(Response orderResponse) {
        orderResponse.then().statusCode(200).body("success", equalTo(true));
        System.out.println("Метод для проверки ошибки кода и тела ответа при отсутствии авторизации создания заказа с ингредиентами отработал успешно");
    }


    @Step("Метод для запроса создания заказа без авторизации и без ингредиентов")
    public static Response createOrderWithoutAuthorizationAndIngredients(OrderSerialization orderSerialization) {
        Response response = given().contentType(ContentType.JSON).body(orderSerialization).when().post("/api/orders");
        return response;
    }

    @Step("Метод для проверки кода и тела ответа при отсутствии авторизации")
    public static void verifyOrderCreationNoIngredientsUnauthorized(Response orderResponse) {
        orderResponse.then().statusCode(400).body("success", equalTo(false)).body("message", equalTo("Ingredient ids must be provided"));
    }

    @Step("Метод для проверки кода и тела ответа при отсутствии ингредиентов")
    public static void verifyOrderCreationNoAuthorizedAndNoIngredients(Response orderResponse) {
        orderResponse.then().statusCode(400).body("success", equalTo(false)).body("message", equalTo("Ingredient ids must be provided"));
    }




    @Step("Метод для проверки кода и тела ответа при неверном хеше ингредиентов")
    public static void verifyOrderCreationInvalidIngredientsHash(Response orderResponse) {
        orderResponse.then().statusCode(500).body("success", equalTo(false));
    }


    @Step("Метод для получения списка заказов авторизованного пользователя")
    public static Response getUserOrders(String accessToken) {
        System.out.println("Выполняется метод для получения списка заказов авторизованного пользователя...");
        Response response = given().header("Authorization", accessToken).log().all() // Логирование запроса
                .when().get("/api/orders").then().log().all() // Логирование ответа
                .extract().response();
        System.out.println("Метод получения списка заказов пользователя отработал");
        return response;
    }

    @Step("Метод для проверки ответа успешного получения списка заказов")
    public static void verifyUserOrdersRetrieval(Response ordersResponse) {
        System.out.println("Проверка успешного получения списка заказов....");
        ordersResponse.then().statusCode(200).body("success", equalTo(true));
        List<Map<String, Object>> orders = ordersResponse.jsonPath().getList("orders");
        assertThat("Список заказов должен быть не пустым", orders, not(empty()));
        System.out.println("Список заказов проверен успешно");
    }


    @Step("Метод проверки ошибки для получения списка заказов без авторизации")
    public static Response getUserOrdersWithoutAuthorization() {
        System.out.println("Выполняется метод проверки ошибки для получения списка заказов без авторизации...");
        Response response = given().log().all() // Логирование запроса
                .when().get("/api/orders").then().log().all() // Логирование ответа
                .extract().response();
        System.out.println("Метод проверки ошибки для получения списка заказов без авторизации отработал");
        return response;
    }

    @Step("Метод для проверки ответа ошибки получения списка заказов пользователя без авторизации")
    public static void verifyUnauthorizedResponse(Response response) {
        System.out.println("Проверка ответа об ошибке получения списка заказов без авторизации....");
        response.then().statusCode(401).body("success", equalTo(false)).body("message", equalTo("You should be authorised"));
        System.out.println("Проверка ответа об ошибке получения списка заказов без авторизации прошла успешно");
    }
}
