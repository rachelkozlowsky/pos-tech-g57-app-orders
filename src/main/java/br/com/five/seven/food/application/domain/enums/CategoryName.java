package br.com.five.seven.food.application.domain.enums;

public enum CategoryName {
    SNACK("SNACK"),
    GARNISH("GARNISH"),
    DRINK("DRINK"),
    DESSERT("DESSERT"),;

    private final String name;

    CategoryName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
