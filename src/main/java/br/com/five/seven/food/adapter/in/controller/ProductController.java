package br.com.five.seven.food.adapter.in.controller;

import br.com.five.seven.food.adapter.in.payload.products.ProductRequest;
import br.com.five.seven.food.adapter.in.payload.products.ProductResponse;
import br.com.five.seven.food.application.domain.Category;
import br.com.five.seven.food.application.domain.Product;
import br.com.five.seven.food.application.ports.in.CategoryServiceIn;
import br.com.five.seven.food.application.ports.in.ProductServiceIn;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.xml.bind.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Tag(name = "Product", description = "Operations related to product management")
@RestController
@RequestMapping("/v1/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductServiceIn productService;
    private final CategoryServiceIn categoryService;

    @Operation(summary = "Create a new product", description = "Create a new product and return the created product details.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Product successfully created"),
            @ApiResponse(responseCode = "400", description = "Invalid input data")
    })
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<ProductResponse> createProduct(@Valid @RequestBody ProductRequest productRequest) throws ValidationException {
        try {
            Category category = categoryService.getCategoryByName(productRequest.getCategory());
            Product product = productService.createProduct(productRequest.toDomain(category));
            return ResponseEntity.ok(ProductResponse.fromDomain(product));
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @Operation(summary = "Get product by ID", description = "Retrieve a specific product by its ID.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Product successfully retrieved"),
            @ApiResponse(responseCode = "404", description = "Product not found")
    })
    @GetMapping("/{productId}")
    public ResponseEntity<ProductResponse> getProductById(@PathVariable Long productId) {
        Product product = productService.getProductById(productId);
        if (product == null) {
            return ResponseEntity.notFound().build();
        }
        ProductResponse response = product.toResponse();
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get all products", description = "Retrieve a list of all products.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Products successfully retrieved")
    })
    @GetMapping
    public ResponseEntity<List<ProductResponse>> getAllProducts() {
        List<ProductResponse> responseList = productService.getAllProducts().stream()
                .map(ProductResponse::fromDomain)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responseList);
    }

    @Operation(summary = "Get products by category", description = "Retrieve a list of products by category.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Products successfully retrieved")
    })
    @GetMapping("/categories/{category}")
    public ResponseEntity<List<ProductResponse>> getProductsByCategory(@PathVariable String category) {
        List<ProductResponse> responseList = productService.getProductsByCategory(category).stream()
                .map(ProductResponse::fromDomain)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responseList);
    }

    @Operation(summary = "Update a product", description = "Update an existing product by its ID.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Product successfully updated"),
            @ApiResponse(responseCode = "404", description = "Product not found")
    })
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{productId}")
    public ResponseEntity<ProductResponse> updateProduct(@PathVariable Long productId, @Valid @RequestBody ProductRequest productRequest) throws ValidationException {

        Category category = categoryService.getCategoryByName(productRequest.getCategory());

        Product updatedProduct = productService.updateProduct(productId, productRequest.toDomain(category));
        return ResponseEntity.ok(updatedProduct.toResponse());
    }

    @Operation(summary = "Delete a product", description = "Delete a product by its ID.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Product successfully deleted"),
            @ApiResponse(responseCode = "404", description = "Product not found")
    })
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{productId}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long productId) {
        productService.deleteProduct(productId);
        return ResponseEntity.noContent().build();
    }
}
