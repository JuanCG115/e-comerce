package com.system.e_comerce.service;

import com.system.e_comerce.dto.ProductRequest;
import com.system.e_comerce.dto.ProductResponse;

import java.util.List;

public interface ProductService {
    ProductResponse createProduct(ProductRequest request);
    List<ProductResponse> getAllProducts();
    ProductResponse getProductById(Long id);
    List<ProductResponse> searchProductsByName(String name);
}
