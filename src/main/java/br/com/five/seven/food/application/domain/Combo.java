package br.com.five.seven.food.application.domain;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class Combo {

    private Long id;
    private List<Item> snack = new ArrayList<>();
    private List<Item> garnish = new ArrayList<>();
    private List<Item> drink = new ArrayList<>();
    private List<Item> dessert = new ArrayList<>();

    public Combo(Long id, List<Item> snack, List<Item> garnish, List<Item> drink, List<Item> dessert) {
        this.id = id;
        this.snack = snack;
        this.garnish = garnish;
        this.drink = drink;
        this.dessert = dessert;
    }

    public Combo() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<Item> getSnack() {
        return snack;
    }

    public void setSnack(List<Item> snack) {
        this.snack = snack;
    }

    public List<Item> getGarnish() {
        return garnish;
    }

    public void setGarnish(List<Item> garnish) {
        this.garnish = garnish;
    }

    public List<Item> getDrink() {
        return drink;
    }

    public void setDrink(List<Item> drink) {
        this.drink = drink;
    }

    public List<Item> getDessert() {
        return dessert;
    }

    public void setDessert(List<Item> dessert) {
        this.dessert = dessert;
    }

    public List<Item> getAllItems() {
        return Stream.of(snack, garnish, drink, dessert)
                .flatMap(List::stream)
                .toList();
    }

    public BigDecimal getTotalPrice() {
        return getAllItems().stream()
                .map(Item::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
