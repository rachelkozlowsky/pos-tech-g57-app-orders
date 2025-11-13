package br.com.five.seven.food.application.domain.enums;

public enum OrderStatus {

    CREATED("Created"),
    SENT("Sent"),
    RECEIVED("Received"),
    IN_PREPARATION("In Preparation"),
    READY("Ready"),
    FINISHED("Finished");

    private final String description;

    OrderStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

}
