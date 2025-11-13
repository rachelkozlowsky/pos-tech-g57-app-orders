package br.com.five.seven.food.application.domain;

import br.com.five.seven.food.adapter.in.payload.category.CategoryResponse;
import br.com.five.seven.food.adapter.in.payload.products.ProductResponse;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Product {

    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private Boolean active = true;
    private List<Image> images = new ArrayList<>();
    private Category category;

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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public List<Image> getImages() {
        return images;
    }

    public void setImages(List<Image> images) {
        this.images = images;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public ProductResponse toResponse() {
        ProductResponse response = new ProductResponse();
        response.setId(this.id);
        response.setName(this.name);
        response.setDescription(this.description);
        response.setPrice(this.price);
        response.setActive(this.active);
        response.setImages(this.images.stream()
                .map(Image::toResponse)
                .collect(Collectors.toList()));
        response.setCategory(CategoryResponse.fromDomain(this.category));
        return response;
    }
}
