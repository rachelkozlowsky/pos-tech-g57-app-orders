package br.com.five.seven.food.adapter.in.payload.products;

import br.com.five.seven.food.application.domain.Category;
import br.com.five.seven.food.application.domain.Product;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductRequest {

    @NotNull(message = "Name é obrigatório")
    private String name;
    private String description;
    @NotNull(message = "Price é obrigatório")
    private BigDecimal price;
    private Boolean active = true;
    private List<ImageRequest> images;

    @NotNull(message = "Categoria é obrigatório")
    private String category;

    public Product toDomain(Category category) {
        Product product = new Product();
        product.setName(this.name);
        product.setDescription(this.description);
        product.setPrice(this.price);
        product.setActive(this.active);
        product.setImages(this.images.stream()
                .map(ImageRequest::toDomain)
                .collect(Collectors.toList()));
        product.setCategory(category);
        return product;
    }
}
