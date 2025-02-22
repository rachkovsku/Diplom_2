package edu.methods;

import io.restassured.RestAssured;

public class SetUp {
    public static void setUp() {
        RestAssured.baseURI = "https://stellarburgers.nomoreparties.site";
    }
}
