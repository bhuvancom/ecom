package com.bhuvancom.ecom.model;

import com.bhuvancom.ecom.dto.OrderProductDto;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

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
@ToString
@NoArgsConstructor
public class OrderForm {
    private User user;
    private List<OrderProductDto> productOrders;
    
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String paymentId;
}
