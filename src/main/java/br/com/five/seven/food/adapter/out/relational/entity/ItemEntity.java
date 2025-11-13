package br.com.five.seven.food.adapter.out.relational.entity;

public class ItemEntity {
    private Long id;
    private ProductEntity product;
    private Integer quantity;
    private ComboEntity combo;

    public ItemEntity(Long id, ProductEntity product, Integer quantity, ComboEntity combo) {
        this.id = id;
        this.product = product;
        this.quantity = quantity;
        this.combo = combo;
    }

    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public ProductEntity getProduct() { return product; }
    public void setProduct(ProductEntity product) { this.product = product; }
    public Integer getQuantity() { return quantity; }
    public void setQuantity(Integer quantity) { this.quantity = quantity; }
    public ComboEntity getCombo() { return combo; }
    public void setCombo(ComboEntity combo) { this.combo = combo; }
}