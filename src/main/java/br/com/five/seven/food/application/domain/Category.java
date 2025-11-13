package br.com.five.seven.food.application.domain;

import br.com.five.seven.food.adapter.in.payload.category.CategoryResponse;

public class Category {

    private Long id;
    private String name;
    private boolean active;

    public Category(Long id, String name, boolean active) {
        this.id = id;
        this.name = name;
        this.active = active;
    }

    public Category() {

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public CategoryResponse toResponse() {
        CategoryResponse response = new CategoryResponse();
        response.setId(this.id);
        response.setName(this.name);
        response.setActive(this.active);
        return response;
    }
}
