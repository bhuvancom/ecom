package com.bhuvancom.ecom.dto;

import com.bhuvancom.ecom.model.Product;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderProductDto {
    private Product product;
    private Integer quantity;
}
