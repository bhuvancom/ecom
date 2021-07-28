package com.bhuvancom.ecom.dto;

import com.bhuvancom.ecom.model.Product;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class OrderProductDto {
    private Product product;
    private Integer quantity;
}
