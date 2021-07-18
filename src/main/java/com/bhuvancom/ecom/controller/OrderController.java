package com.bhuvancom.ecom.controller;

import com.bhuvancom.ecom.dto.OrderProductDto;
import com.bhuvancom.ecom.exception.EcomError;
import com.bhuvancom.ecom.exception.model.ErrorResponse;
import com.bhuvancom.ecom.model.Order;
import com.bhuvancom.ecom.model.OrderForm;
import com.bhuvancom.ecom.model.OrderProduct;
import com.bhuvancom.ecom.model.OrderStatus;
import com.bhuvancom.ecom.service.OrderService;
import com.bhuvancom.ecom.service.ProductService;
import com.bhuvancom.ecom.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    @Autowired
    private OrderService mOrderService;
    @Autowired
    private ProductService productService;

    @Autowired
    private UserService userService;

    @PostMapping()
    public ResponseEntity<Order> upsertOrder(@RequestBody OrderForm orderForm) {
        List<OrderProductDto> productOrders = orderForm.getProductOrders();
        validateIfProductsExists(productOrders);
        Order order = new Order();
        order.setUser(orderForm.getUser());
        order.setStatus(OrderStatus.PAID.name());
        order = mOrderService.upsertOrder(order);
        List<OrderProduct> orderProducts = new ArrayList<>();
        for (OrderProductDto dto : productOrders) {
            OrderProduct orderProduct = new OrderProduct(order,
                    productService.getProduct(dto.getProduct().getId()),
                    dto.getQuantity()
            );
            orderProducts.add(mOrderService.upsertOrderProduct(orderProduct));
        }

        order.setOrderProducts(orderProducts);
        mOrderService.upsertOrder(order);
        //Order order1 = mOrderService.upsertOrder(order);
        //return new ResponseEntity<>(order1, HttpStatus.CREATED);
        String uri = ServletUriComponentsBuilder
                .fromCurrentServletMapping()
                .path("/orders/{id}")
                .buildAndExpand(order.getId())
                .toString();

        HttpHeaders headers = new HttpHeaders();
        headers.add("Location", uri);
        return new ResponseEntity<>(order, headers, HttpStatus.CREATED);
    }

    private void validateIfProductsExists(List<OrderProductDto> productOrders) {
        List<OrderProductDto> collect = productOrders.stream()
                .filter(product -> Objects
                        .isNull(productService.getProduct(
                                product.getProduct().getId())))
                .collect(Collectors.toList());

        if (!collect.isEmpty()) { //filter those which are not present, if it is not
            // empty means we have some non existing product
            throw new EcomError(new ErrorResponse(HttpStatus.NOT_FOUND, HttpStatus.NOT_FOUND.value(), "Product Not found"));
        }
    }

    @GetMapping(value = {"", "/"})
    @ResponseStatus(HttpStatus.OK)
    public Iterable<Order> getAllOrders() {
        return mOrderService.getAllOrders();
    }


}
