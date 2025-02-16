package ru.api.tests;

import java.util.List;

public class OrderSerialization {
    private List<String> ingredients;

    public OrderSerialization(List<String> ingredients) {
        this.ingredients = ingredients;
    }

    public List<String> getIngredients() {
        return ingredients;
    }

    public void setIngredients(List<String> ingredients) {
        this.ingredients = ingredients;
    }
}
