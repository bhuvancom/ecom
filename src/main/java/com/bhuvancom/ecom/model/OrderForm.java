package com.bhuvancom.ecom.model;

import com.bhuvancom.ecom.dto.OrderProductDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * Created Using IntelliJ Idea
 *
 * @author Bhuvaneshvar
 * Date    7/17/2021
 * Time    9:29 PM
 * Project ecomNew
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OrderForm {
    private User user;
    private List<OrderProductDto> productOrders;
}
