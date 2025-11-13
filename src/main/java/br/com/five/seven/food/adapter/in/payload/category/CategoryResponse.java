package br.com.five.seven.food.adapter.in.payload.category;

import br.com.five.seven.food.application.domain.Category;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CategoryResponse {

    private Long id;
    private String name;
    private Boolean active;

    public static CategoryResponse fromDomain(Category category) {
        CategoryResponse response = new CategoryResponse();
        response.setId(category.getId());
        response.setName(category.getName());
        response.setActive(category.isActive());
        return response;
    }

}
