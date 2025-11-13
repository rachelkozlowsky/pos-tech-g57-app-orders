package br.com.five.seven.food.adapter.in.controller;

import br.com.five.seven.food.adapter.in.mappers.CategoryMapper;
import br.com.five.seven.food.adapter.in.payload.category.CategoryRequest;
import br.com.five.seven.food.adapter.in.payload.category.CategoryResponse;
import br.com.five.seven.food.application.domain.Category;
import br.com.five.seven.food.application.ports.in.CategoryServiceIn;
import br.com.five.seven.food.infra.annotations.category.SwaggerCreateCategory;
import br.com.five.seven.food.infra.annotations.category.SwaggerDeleteCategory;
import br.com.five.seven.food.infra.annotations.category.SwaggerGeCategoriaById;
import br.com.five.seven.food.infra.annotations.category.SwaggerGetAllCategory;
import br.com.five.seven.food.infra.annotations.category.SwaggerGetCategoryByName;
import br.com.five.seven.food.infra.annotations.category.SwaggerUpdateCategory;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.websocket.server.PathParam;
import jakarta.xml.bind.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@Tag(name = "Category", description = "Operations related to category management")
@RestController
@RequestMapping("/v1/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryServiceIn categoryService;

    private final CategoryMapper mapper;

    @SwaggerCreateCategory
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<CategoryResponse> createCategory(@RequestBody CategoryRequest categoryRequest) throws ValidationException {
        Category createdCategory = categoryService.createCategory(mapper.requestFromDomain(categoryRequest));
        CategoryResponse response = createdCategory.toResponse();
        return ResponseEntity.ok(response);
    }

    @SwaggerGeCategoriaById
    @GetMapping("/{categoryId}")
    public ResponseEntity<CategoryResponse> getCategoryById(@PathVariable Long categoryId) {
        Category category = categoryService.getCategoryById(categoryId);
        if (category == null) {
            return ResponseEntity.notFound().build();
        }
        CategoryResponse response = category.toResponse();
        return ResponseEntity.ok(response);
    }

    @SwaggerGetCategoryByName
    @GetMapping("/by-name")
    public ResponseEntity<CategoryResponse> getCategoryByName(@PathParam("categoryName") String categoryName) {
        Category category = categoryService.getCategoryByName(categoryName);
        if (category == null) {
            return ResponseEntity.notFound().build();
        }
        CategoryResponse response = category.toResponse();
        return ResponseEntity.ok(response);
    }

    @SwaggerGetAllCategory
    @GetMapping
    public ResponseEntity<List<CategoryResponse>> getAllCategory() {
        List<CategoryResponse> responseList = categoryService.getAllCategory().stream()
                .map(CategoryResponse::fromDomain)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responseList);
    }

    @SwaggerUpdateCategory
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{categoryId}")
    public ResponseEntity<CategoryResponse> updateCategory(@PathVariable Long categoryId, @RequestBody CategoryRequest categoryRequest) throws ValidationException {
        Category updatedProduct = categoryService.updateCategory(categoryId, mapper.requestToDomain(categoryRequest));
        CategoryResponse response = updatedProduct.toResponse();
        return ResponseEntity.ok(response);
    }

    @SwaggerDeleteCategory
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{categoryId}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long categoryId) {
        categoryService.deleteCategory(categoryId);
        return ResponseEntity.noContent().build();
    }
}
