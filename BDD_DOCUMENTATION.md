# BDD Test Documentation

## Overview

This document describes the Behavior-Driven Development (BDD) test scenarios implemented for the Order Management System. All tests follow the **Given-When-Then** pattern to provide clear, readable specifications of the system's behavior.

## Test Structure

- **Given**: Describes the initial context and preconditions
- **When**: Describes the action or event that triggers the behavior
- **Then**: Describes the expected outcome

## Testing Approach

### Controller Layer Testing Strategy

**Simple Unit Tests with Mockito** (Not MockMvc/Integration Tests)

All controller tests use **simple unit testing** with Mockito instead of MockMvc. This approach:
- **Eliminates HTTP overhead** - No web server or HTTP layer involved
- **Faster execution** - Direct method invocation instead of HTTP requests
- **Focuses on business logic** - Tests controller methods directly
- **Better isolation** - Pure unit tests without Spring context dependencies

---

## Controller Layer Tests

### Order Controller Tests

**Test Class**: `OrderControllerTest`  
**Dependencies Mocked**: `OrderServiceIn`, `OrderMapper`

#### RETRIEVE ORDERS

**Scenario 1: Successfully retrieve all orders with pagination**  
- **Given**: Multiple orders exist in the system
- **When**: Controller method `getAllOrders(pageable)` is called
- **Then**: 
  - Returns ResponseEntity with HTTP 200 OK
  - Response body contains Page with orders
  - OrderService.findAll() called once with pageable

**Scenario 2: Successfully retrieve orders filtered by status**  
- **Given**: Orders with specific status exist
- **When**: Controller method `getAllOrdersByStatus(status, pageable)` is called
- **Then**:
  - Returns ResponseEntity with HTTP 200 OK
  - Response body contains only matching orders
  - OrderService.findAllByOrderStatus() called once

**Scenario 3: Successfully retrieve order by ID**  
- **Given**: An existing order with ID 1
- **When**: Controller method `getOrderById(1L)` is called
- **Then**:
  - Returns ResponseEntity with HTTP 200 OK
  - Response body contains order details
  - OrderService.findById(1L) called once

**Scenario 4: Return 404 when order not found by ID**  
- **Given**: A non-existing order ID (999)
- **When**: Controller method `getOrderById(999L)` is called and service throws exception
- **Then**:
  - Returns ResponseEntity with HTTP 404 Not Found
  - OrderService.findById(999L) called once

**Scenario 5: Return empty page when no orders exist**  
- **Given**: No orders exist in the system
- **When**: Controller method `getAllOrders(pageable)` is called
- **Then**:
  - Returns ResponseEntity with HTTP 200 OK
  - Response body contains empty page

**Scenario 6: Return empty page when filtering by non-matching status**  
- **Given**: No orders with specified status
- **When**: Controller method `getAllOrdersByStatus(status, pageable)` is called
- **Then**:
  - Returns ResponseEntity with HTTP 200 OK
  - Response body contains empty page

#### CREATE ORDER

**Scenario 7: Successfully create order with valid items**  
- **Given**: A valid CreateOrderRequest with items
- **When**: Controller method `createOrder(request)` is called
- **Then**:
  - Returns ResponseEntity with HTTP 201 Created
  - Response body contains created order with ID
  - OrderService.create() called once

**Scenario 8: Successfully create order with multiple items**  
- **Given**: CreateOrderRequest with 2+ items
- **When**: Controller method `createOrder(request)` is called
- **Then**:
  - Returns ResponseEntity with HTTP 201 Created
  - Response includes all items

**Scenario 9: Successfully create order with client CPF**  
- **Given**: CreateOrderRequest with client CPF
- **When**: Controller method `createOrder(request)` is called
- **Then**:
  - Returns ResponseEntity with HTTP 201 Created
  - Response includes cpfClient field

#### UPDATE ORDER

**Scenario 10: Successfully update order**  
- **Given**: An existing order and UpdateOrderRequest
- **When**: Controller method `updateOrder(id, request)` is called
- **Then**:
  - Returns ResponseEntity with HTTP 200 OK
  - Order updated successfully
  - OrderService.update() called once

**Scenario 11: Successfully update order items**  
- **Given**: An existing order and UpdateOrderItemsRequest
- **When**: Controller method `updateOrderItems(id, request)` is called
- **Then**:
  - Returns ResponseEntity with HTTP 200 OK
  - Items updated successfully
  - OrderService.updateOrderItems() called once

#### UPDATE ORDER STATUS

**Scenario 12: Successfully update order status**  
- **Given**: A valid UpdateOrderStatusRequest
- **When**: Controller method `updateOrderStatus(request)` is called
- **Then**:
  - Returns ResponseEntity with HTTP 200 OK
  - Response body contains new status
  - OrderService.updateStatusOrder() called once

**Scenario 13: Successfully advance order status**  
- **Given**: An existing order
- **When**: Controller method `advanceStatus(id)` is called
- **Then**:
  - Returns ResponseEntity with HTTP 200 OK
  - Response body contains success message
  - OrderService.advanceOrderStatus() called once

**Scenario 14: Fail to advance status of finished order**  
- **Given**: A finished order
- **When**: Controller method `advanceStatus(id)` is called and service throws exception
- **Then**:
  - Returns ResponseEntity with HTTP 400 Bad Request
  - Response body contains error message
  - OrderService.advanceOrderStatus() called once

#### DELETE ORDER

**Scenario 15: Successfully delete order**  
- **Given**: An existing order
- **When**: Controller method `deleteOrder(id)` is called
- **Then**:
  - Returns ResponseEntity with HTTP 204 No Content
  - OrderService.deleteById() called once

**Scenario 16: Return 404 when deleting non-existing order**  
- **Given**: A non-existing order
- **When**: Controller method `deleteOrder(id)` is called and service throws exception
- **Then**:
  - Returns ResponseEntity with HTTP 404 Not Found
  - OrderService.deleteById() called once

---

### Category Controller Tests

**Test Class**: `CategoryControllerTest`  
**Dependencies Mocked**: `CategoryServiceIn`, `CategoryMapper`

#### CREATE CATEGORY

**Scenario 1: Successfully create category**  
- **Given**: A valid CategoryRequest
- **When**: Controller method `createCategory(request)` is called
- **Then**:
  - Returns ResponseEntity with HTTP 200 OK
  - Response body contains created category
  - CategoryService.createCategory() called once

**Scenario 2: Fail to create category with invalid data**  
- **Given**: CategoryRequest with empty name
- **When**: Controller method `createCategory(request)` is called
- **Then**:
  - ValidationException thrown
  - CategoryService.createCategory() called but throws exception

**Scenario 3: Successfully create inactive category**  
- **Given**: CategoryRequest with active=false
- **When**: Controller method `createCategory(request)` is called
- **Then**:
  - Returns ResponseEntity with HTTP 200 OK
  - Response body shows active=false

**Scenario 4: Fail to create duplicate category**  
- **Given**: CategoryRequest with duplicate name
- **When**: Controller method `createCategory(request)` is called
- **Then**:
  - IllegalStateException thrown
  - CategoryService.createCategory() called but throws exception

#### RETRIEVE CATEGORY

**Scenario 5: Successfully retrieve category by ID**  
- **Given**: An existing category with ID 1
- **When**: Controller method `getCategoryById(1L)` is called
- **Then**:
  - Returns ResponseEntity with HTTP 200 OK
  - Response body contains category details
  - CategoryService.getCategoryById(1L) called once

**Scenario 6: Return 404 when category not found by ID**  
- **Given**: A non-existing category ID (999)
- **When**: Controller method `getCategoryById(999L)` is called
- **Then**:
  - Returns ResponseEntity with HTTP 404 Not Found
  - CategoryService.getCategoryById(999L) called once

**Scenario 7: Successfully retrieve category by name**  
- **Given**: An existing category with name "Lanches"
- **When**: Controller method `getCategoryByName("Lanches")` is called
- **Then**:
  - Returns ResponseEntity with HTTP 200 OK
  - Response body contains category
  - CategoryService.getCategoryByName() called once

**Scenario 8: Return 404 when category not found by name**  
- **Given**: A non-existing category name
- **When**: Controller method `getCategoryByName("NonExisting")` is called
- **Then**:
  - Returns ResponseEntity with HTTP 404 Not Found

**Scenario 9: Successfully retrieve all categories**  
- **Given**: Multiple categories exist
- **When**: Controller method `getAllCategories()` is called
- **Then**:
  - Returns ResponseEntity with HTTP 200 OK
  - Response body contains all categories
  - CategoryService.getAllCategory() called once

**Scenario 10: Return empty list when no categories exist**  
- **Given**: No categories exist
- **When**: Controller method `getAllCategories()` is called
- **Then**:
  - Returns ResponseEntity with HTTP 200 OK
  - Response body contains empty list

**Scenario 11: Successfully retrieve active and inactive categories**  
- **Given**: Mix of active and inactive categories
- **When**: Controller method `getAllCategories()` is called
- **Then**:
  - Returns ResponseEntity with HTTP 200 OK
  - Response body contains all categories

#### UPDATE CATEGORY

**Scenario 12: Successfully update category**  
- **Given**: An existing category and CategoryRequest
- **When**: Controller method `updateCategory(id, request)` is called
- **Then**:
  - Returns ResponseEntity with HTTP 200 OK
  - Category updated successfully
  - CategoryService.updateCategory() called once

**Scenario 13: Successfully toggle category active status**  
- **Given**: An existing category
- **When**: Controller method `updateCategory(id, request)` is called with active=false
- **Then**:
  - Returns ResponseEntity with HTTP 200 OK
  - Active status toggled

**Scenario 14: Fail to update non-existing category**  
- **Given**: A non-existing category ID
- **When**: Controller method `updateCategory(id, request)` is called
- **Then**:
  - RuntimeException thrown

#### DELETE CATEGORY

**Scenario 15: Successfully delete category**  
- **Given**: An existing category
- **When**: Controller method `deleteCategory(id)` is called
- **Then**:
  - Returns ResponseEntity with HTTP 204 No Content
  - CategoryService.deleteCategory() called once

**Scenario 16: Fail to delete non-existing category**  
- **Given**: A non-existing category
- **When**: Controller method `deleteCategory(id)` is called
- **Then**:
  - RuntimeException thrown

---

### Product Controller Tests

**Test Class**: `ProductControllerTest`  
**Dependencies Mocked**: `ProductServiceIn`, `CategoryServiceIn`

#### CREATE PRODUCT

**Scenario 1: Successfully create product**  
- **Given**: A valid ProductRequest
- **When**: Controller method `createProduct(request)` is called
- **Then**:
  - Returns ResponseEntity with HTTP 200 OK
  - Response body contains created product
  - ProductService.createProduct() called once

**Scenario 2: Fail to create product with non-existing category**  
- **Given**: ProductRequest with invalid category
- **When**: Controller method `createProduct(request)` is called
- **Then**:
  - Returns ResponseEntity with HTTP 400 Bad Request
  - ProductService.createProduct() never called

**Scenario 3: Successfully create product with zero price**  
- **Given**: ProductRequest with price=0
- **When**: Controller method `createProduct(request)` is called
- **Then**:
  - Returns ResponseEntity with HTTP 200 OK
  - Product created with price=0

**Scenario 4: Successfully create inactive product**  
- **Given**: ProductRequest with active=false
- **When**: Controller method `createProduct(request)` is called
- **Then**:
  - Returns ResponseEntity with HTTP 200 OK
  - Product created with active=false

**Scenario 5: Fail to create product with negative price**  
- **Given**: ProductRequest with negative price
- **When**: Controller method `createProduct(request)` is called
- **Then**:
  - ValidationException thrown

#### RETRIEVE PRODUCT

**Scenario 6: Successfully retrieve product by ID**  
- **Given**: An existing product with ID 1
- **When**: Controller method `getProductById(1L)` is called
- **Then**:
  - Returns ResponseEntity with HTTP 200 OK
  - Response body contains product details
  - ProductService.getProductById(1L) called once

**Scenario 7: Return 404 when product not found by ID**  
- **Given**: A non-existing product ID (999)
- **When**: Controller method `getProductById(999L)` is called
- **Then**:
  - Returns ResponseEntity with HTTP 404 Not Found

**Scenario 8: Successfully retrieve all products**  
- **Given**: Multiple products exist
- **When**: Controller method `getAllProducts()` is called
- **Then**:
  - Returns ResponseEntity with HTTP 200 OK
  - Response body contains all products
  - ProductService.getAllProducts() called once

**Scenario 9: Return empty list when no products exist**  
- **Given**: No products exist
- **When**: Controller method `getAllProducts()` is called
- **Then**:
  - Returns ResponseEntity with HTTP 200 OK
  - Response body contains empty list

**Scenario 10: Successfully retrieve products by category**  
- **Given**: Products exist in category "Lanches"
- **When**: Controller method `getProductsByCategory("Lanches")` is called
- **Then**:
  - Returns ResponseEntity with HTTP 200 OK
  - Response body contains products from category
  - ProductService.getProductsByCategory() called once

**Scenario 11: Return empty list when no products in category**  
- **Given**: A category without products
- **When**: Controller method `getProductsByCategory("EmptyCategory")` is called
- **Then**:
  - Returns ResponseEntity with HTTP 200 OK
  - Response body contains empty list

**Scenario 12: Successfully retrieve products from multiple categories**  
- **Given**: Products in different categories
- **When**: Controller methods called for each category
- **Then**:
  - Returns correct products for each category

#### UPDATE PRODUCT

**Scenario 13: Successfully update product**  
- **Given**: An existing product and ProductRequest
- **When**: Controller method `updateProduct(id, request)` is called
- **Then**:
  - Returns ResponseEntity with HTTP 200 OK
  - Product updated successfully
  - ProductService.updateProduct() called once

**Scenario 14: Successfully update product category**  
- **Given**: A product being moved to different category
- **When**: Controller method `updateProduct(id, request)` is called
- **Then**:
  - Returns ResponseEntity with HTTP 200 OK
  - Product category changed

**Scenario 15: Fail to update non-existing product**  
- **Given**: A non-existing product ID
- **When**: Controller method `updateProduct(id, request)` is called
- **Then**:
  - RuntimeException thrown

#### DELETE PRODUCT

**Scenario 16: Successfully delete product**  
- **Given**: An existing product
- **When**: Controller method `deleteProduct(id)` is called
- **Then**:
  - Returns ResponseEntity with HTTP 204 No Content
  - ProductService.deleteProduct() called once

**Scenario 17: Fail to delete non-existing product**  
- **Given**: A non-existing product
- **When**: Controller method `deleteProduct(id)` is called
- **Then**:
  - RuntimeException thrown

---

## Running the Tests

### All Tests
```bash
# Run all tests
mvn test

# Run with coverage report
mvn clean test jacoco:report
```

### Controller Tests Only
```bash
# Run all controller tests
mvn test -Dtest=*ControllerTest
```

### Service Tests Only
```bash
# Run all service tests
mvn test -Dtest=*ServiceTest
```

