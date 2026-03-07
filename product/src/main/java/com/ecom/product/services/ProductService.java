package com.ecom.product.services;

import com.ecom.product.dtos.ProductRequest;
import com.ecom.product.dtos.ProductResponse;
import com.ecom.product.models.Product;
import com.ecom.product.repositories.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;

    public ProductResponse addProduct(ProductRequest productRequest) {
        Product product = new Product();
        updateProductFromProductRequest(product, productRequest);
        Product savedProduct  = productRepository.save(product);
        return maptoProductResponse(savedProduct);
    }

    public List<ProductResponse> fetchAllProducts() {
        return productRepository.findByIsActiveTrue().stream()
                .map(this::maptoProductResponse)
                .collect(Collectors.toList());

    }

    public ProductResponse fetchProductById(Long id) {
        return productRepository.findByIdAndIsActiveTrue(id).map(this::maptoProductResponse).orElse(null);
    }

    public ProductResponse updateProduct(Long id, ProductRequest productRequest) {
        return productRepository.findById(id).
                map(existingProduct -> {
                            updateProductFromProductRequest(existingProduct, productRequest);
                    return maptoProductResponse(productRepository.save(existingProduct));
                }).orElse(null);
    }

    private void updateProductFromProductRequest(Product product, ProductRequest productRequest) {
        product.setName(productRequest.getName());
        product.setDescription(productRequest.getDescription());
        product.setPrice(productRequest.getPrice());
        product.setCategory(productRequest.getCategory());
        product.setStockQuantity(productRequest.getStockQuantity());
        product.setIsActive(productRequest.getIsActive());
        product.setImageUrl(productRequest.getImageUrl());
    }

    private ProductResponse maptoProductResponse(Product product) {
        ProductResponse productResponse = new ProductResponse();
        productResponse.setId(String.valueOf(product.getId()));
        productResponse.setName(product.getName());
        productResponse.setDescription(product.getDescription());
        productResponse.setPrice(product.getPrice());
        productResponse.setCategory(product.getCategory());
        productResponse.setStockQuantity(product.getStockQuantity());
        productResponse.setIsActive(product.getIsActive());
        productResponse.setImageUrl(product.getImageUrl());
        return productResponse;
    }

    public boolean deleteProduct(Long id) {
        return productRepository.findById(id)
                .map(product -> {
                    product.setIsActive(false);
                    productRepository.save(product);
                    return true;
                }).orElse(false);
    }

    public List<ProductResponse> searchProducts(String keyword) {
        return productRepository.searchProducts(keyword).stream()
                .map(this::maptoProductResponse).collect(Collectors.toList());
    }
}
