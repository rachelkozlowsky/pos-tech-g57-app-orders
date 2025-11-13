package br.com.five.seven.food.adapter.out.relational.entity;

import java.util.List;

public class ComboEntity {
    private Long id;
    private List<ItemEntity> snack;
    private List<ItemEntity> garnish;
    private List<ItemEntity> drink;
    private List<ItemEntity> dessert;

    public ComboEntity(Long id, List<ItemEntity> snack, List<ItemEntity> garnish, List<ItemEntity> drink, List<ItemEntity> dessert) {
        this.id = id;
        this.snack = snack;
        this.garnish = garnish;
        this.drink = drink;
        this.dessert = dessert;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<ItemEntity> getSnack() {
        return snack;
    }

    public void setSnack(List<ItemEntity> snack) {
        this.snack = snack;
    }

    public List<ItemEntity> getGarnish() {
        return garnish;
    }

    public void setGarnish(List<ItemEntity> garnish) {
        this.garnish = garnish;
    }

    public List<ItemEntity> getDrink() {
        return drink;
    }

    public void setDrink(List<ItemEntity> drink) {
        this.drink = drink;
    }

    public List<ItemEntity> getDessert() {
        return dessert;
    }

    public void setDessert(List<ItemEntity> dessert) {
        this.dessert = dessert;
    }
}

