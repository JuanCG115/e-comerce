package com.system.e_comerce.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.system.e_comerce.dto.ProductRequest;
import com.system.e_comerce.dto.ProductResponse;
import com.system.e_comerce.service.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
public class ProductControllerTest {

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @MockitoBean
    private ProductService productService;

    @BeforeEach
    void setUp() {
        this.objectMapper = new ObjectMapper();
        this.mockMvc = MockMvcBuilders.standaloneSetup(new ProductController(productService)).build();
    }

    @Test
    @DisplayName("Should create a product and return HTTP 201 Created")
    void createProduct_ShouldReturnCreatedAndProductResponse() throws Exception {
        // 1. arrange
        ProductRequest request = new ProductRequest("Laptop Gamer", "Laptop de alta gama", new BigDecimal("1200.00"), 10);
        ProductResponse expectedResponse = new ProductResponse(
                1L,
                "Laptop Gamer",
                "Laptop de alta gama",
                new BigDecimal("1200.00"),
                10,
                4.5,
                LocalDateTime.now()
        );

        when(productService.createProduct(any(ProductRequest.class))).thenReturn(expectedResponse);

        // 2. act and assert
        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Laptop Gamer"))
                .andExpect(jsonPath("$.price").value(1200.00));

        // 3. verification
        verify(productService, times(1)).createProduct(any(ProductRequest.class));
    }

    @Test
    @DisplayName("Should return a list of all products and HTTP 200 OK")
    void getAllProducts_ShouldReturnListAnd200Ok() throws Exception {
        // 1. arrange
        ProductResponse p1 = new ProductResponse(1L, "Laptop", "Gamer", new BigDecimal("1200.00"), 10, 4.5, LocalDateTime.now());
        ProductResponse p2 = new ProductResponse(2L, "Mouse", "Inalámbrico", new BigDecimal("25.00"), 50, 4.8, LocalDateTime.now());

        when(productService.getAllProducts()).thenReturn(List.of(p1, p2));

        // 2. act and assert
        mockMvc.perform(get("/api/products"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.size()").value(2))
                .andExpect(jsonPath("$[0].name").value("Laptop"))
                .andExpect(jsonPath("$[1].name").value("Mouse"));

        // 3. verification
        verify(productService, times(1)).getAllProducts();
    }

    @Test
    @DisplayName("Should return a single product by ID and HTTP 200 OK")
    void getProductById_ShouldReturnProductAnd200Ok() throws Exception {
        // 1. arrange
        Long productId = 1L;
        ProductResponse expectedResponse = new ProductResponse(productId, "Laptop Gamer", "Laptop de alta gama", new BigDecimal("1200.00"), 10, 4.5, LocalDateTime.now());

        when(productService.getProductById(productId)).thenReturn(expectedResponse);

        // 2. act and assert
        mockMvc.perform(get("/api/products/{id}", productId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(productId))
                .andExpect(jsonPath("$.name").value("Laptop Gamer"));

        // 3. verification
        verify(productService, times(1)).getProductById(productId);
    }

    @Test
    @DisplayName("Should search products by name using request param and return HTTP 200 OK")
    void searchProducts_ShouldReturnFilteredListAnd200Ok() throws Exception {
        // 1. arrange
        String searchName = "Laptop";
        ProductResponse p1 = new ProductResponse(1L, "Laptop Gamer", "Laptop de alta gama", new BigDecimal("1200.00"), 10, 4.5, LocalDateTime.now());

        when(productService.searchProductsByName(searchName)).thenReturn(List.of(p1));

        // 2. act and assert
        mockMvc.perform(get("/api/products/search")
                        .param("name", searchName))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$[0].name").value("Laptop Gamer"));

        // 3. verification
        verify(productService, times(1)).searchProductsByName(searchName);
    }
}