package com.bhuvancom.ecom.controller;

import com.bhuvancom.ecom.model.Product;
import com.bhuvancom.ecom.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/products")
public class ProductController {
    @Autowired
    private ProductService productService;

    @GetMapping(value = {"", "/"})
    public Page<Product> getProducts(@RequestParam(name = "page", defaultValue = "1") int page,
                                     @RequestParam(name = "page_size", defaultValue = "5") int pageSize,
                                     @RequestParam(name = "query", defaultValue = "") String query) {
        return productService.getAllProduct(pageSize, page,query);
    }

}
