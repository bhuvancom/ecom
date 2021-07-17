package com.bhuvancom.ecom.controller;

import com.bhuvancom.ecom.model.Product;
import com.bhuvancom.ecom.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/products")
public class ProductController {
    @Autowired
    private ProductService productService;

    @GetMapping(value = {"", "/"})
    public Iterable<Product> getProducts() {
        return productService.getAllProduct();
    }

}
