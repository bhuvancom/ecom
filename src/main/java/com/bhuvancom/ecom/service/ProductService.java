package com.bhuvancom.ecom.service;

import com.bhuvancom.ecom.exception.EcomError;
import com.bhuvancom.ecom.exception.model.ErrorResponse;
import com.bhuvancom.ecom.model.Product;
import com.bhuvancom.ecom.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ProductService {
    @Autowired
    ProductRepository productRepository;

    public Page<Product> getAllProduct(int pageSize, int page) {
        Pageable pageable = PageRequest.of(page - 1, pageSize);
        return productRepository.findAll(pageable);
    }

    public Product getProduct(long id) {
        return productRepository
                .findById(id)
                .orElseThrow(() -> new EcomError(
                        new ErrorResponse(HttpStatus.NOT_FOUND, HttpStatus.NOT_FOUND.value(), "Product Not found")));
    }

    public Product save(Product product) {
        return productRepository.save(product);
    }

    public void delete(Product product) {
        productRepository.delete(product);
    }
}
