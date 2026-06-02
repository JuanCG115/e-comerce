package com.system.e_comerce.service.impl;

import com.system.e_comerce.dto.ProductRequest;
import com.system.e_comerce.dto.ProductResponse;
import com.system.e_comerce.model.Product;
import com.system.e_comerce.repository.ProductRepository;
import com.system.e_comerce.repository.ReviewRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProductServiceImplTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ReviewRepository reviewRepository;

    private ProductServiceImpl productService;

    @BeforeEach
    void setUp() {
        productService = new ProductServiceImpl(productRepository, reviewRepository);
    }

    @Test
    @DisplayName("Should create a product successfully and default rating to 0.0 when no reviews exist")
    void createProduct_ShouldSaveProductAndReturnResponse() {
        // 1. arrange
        ProductRequest request = new ProductRequest("Mechanic keyboard", "RGB Switch Blue", BigDecimal.valueOf(45000.0), 15);

        Product savedProduct = Product.builder()
                .id(1L)
                .name(request.name())
                .description(request.description())
                .price(request.price())
                .stock(request.stock())
                .createdAt(LocalDateTime.now())
                .build();

        when(productRepository.save(any(Product.class))).thenReturn(savedProduct);
        when(reviewRepository.getAverageRatingByProductId(1L)).thenReturn(null);

        // 2. act
        ProductResponse response = productService.createProduct(request);

        // 3. assert
        assertNotNull(response);
        assertEquals(1L, response.id());
        assertEquals("Mechanic keyboard", response.name());
        assertEquals(45000.0, response.price().doubleValue());
        assertEquals(15, response.stock());

        // verifications
        assertEquals(0.0, response.averageRating());

        ArgumentCaptor<Product> productCaptor = ArgumentCaptor.forClass(Product.class);
        verify(productRepository, times(1)).save(productCaptor.capture());

        Product productSend = productCaptor.getValue();
        assertEquals("Mechanic keyboard", productSend.getName());
        assertEquals(45000.0, productSend.getPrice().doubleValue());
        assertEquals(15, productSend.getStock());
    }

    @Test
    @DisplayName("Should return a list of all products")
    void getAllProducts_ShouldReturnList() {
        // 1. arrange
        Product p1 = Product.builder().id(1L).name("Mouse").price(BigDecimal.valueOf(15000.0)).stock(10).build();
        Product p2 = Product.builder().id(2L).name("Monitor").price(BigDecimal.valueOf(120000.0)).stock(5).build();

        when(productRepository.findAll()).thenReturn(List.of(p1, p2));

        when(reviewRepository.getAverageRatingByProductId(1L)).thenReturn(4.5);
        when(reviewRepository.getAverageRatingByProductId(2L)).thenReturn(null);

        // 2. act
        List<ProductResponse> result = productService.getAllProducts();

        // 3. assert
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Mouse", result.get(0).name());
        assertEquals(4.5, result.get(0).averageRating());
        assertEquals(0.0, result.get(1).averageRating());
    }

    @Test
    @DisplayName("Should return product when product exists by id")
    void getProductById_ShouldReturnProduct_WhenExists() {
        // 1. arrange
        Long productId = 1L;
        Product product = Product.builder()
                .id(productId)
                .name("Mouse")
                .price(BigDecimal.valueOf(15000.0)).stock(10)
                .build();

        when(productRepository.findById(productId)).thenReturn(java.util.Optional.of(product));
        when(reviewRepository.getAverageRatingByProductId(productId)).thenReturn(4.0);

        // 2. act
        ProductResponse response = productService.getProductById(productId);

        // 3. assert
        assertNotNull(response);
        assertEquals(productId, response.id());
        assertEquals("Mouse", response.name());
        assertEquals(4.0, response.averageRating());
    }

    @Test
    @DisplayName("Should throw IllegalArgumentException when product does not exist by id")
    void getProductById_ShouldThrowException_WhenNotFound() {
        // 1. arrange
        Long productId = 99L;
        when(productRepository.findById(productId)).thenReturn(java.util.Optional.empty());

        // 2. act and assert
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            productService.getProductById(productId);
        });

        // 3. verification
        assertEquals("Product not found with id: " + productId, exception.getMessage());
        verify(reviewRepository, never()).getAverageRatingByProductId(anyLong());
    }

    @Test
    @DisplayName("Should return matching products when searching by name")
    void searchProductsByName_ShouldReturnFilteredList() {
        // 1. arrange
        String query = "teclado";
        Product p = Product.builder()
                .id(1L)
                .name("Mechanic keyboard")
                .price(BigDecimal.valueOf(45000.0)).stock(5)
                .build();

        when(productRepository.findByNameContainingIgnoreCase(query)).thenReturn(List.of(p));
        when(reviewRepository.getAverageRatingByProductId(1L)).thenReturn(5.0);

        // 2. act
        List<ProductResponse> result = productService.searchProductsByName(query);

        // 3. assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Mechanic keyboard", result.get(0).name());
        assertEquals(5.0, result.get(0).averageRating());
    }
}
