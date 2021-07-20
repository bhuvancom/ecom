package com.bhuvancom.ecom.controller;

import com.bhuvancom.ecom.dto.OrderProductDto;
import com.bhuvancom.ecom.exception.EcomError;
import com.bhuvancom.ecom.exception.model.ErrorResponse;
import com.bhuvancom.ecom.model.*;
import com.bhuvancom.ecom.service.CartService;
import com.bhuvancom.ecom.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created Using IntelliJ Idea
 *
 * @author Bhuvaneshvar
 * Date    7/18/2021
 * Time    1:44 PM
 * Project ecomNew
 */
@RestController
@RequestMapping("/api/cart")
public class CartController {

    @Autowired
    private CartService cartService;

    @Autowired
    ProductService productService;

    @GetMapping("/user/{user_id}")
    public ResponseEntity<Cart> getThisUsersCart(@PathVariable(name = "user_id") Long userId) {
        Optional<Cart> thisUserIdCart = cartService.getThisUserIdCart(userId);
        if (thisUserIdCart.isPresent()) {
            return new ResponseEntity<>(thisUserIdCart.get(), HttpStatus.OK);
        }
        throw new EcomError(new ErrorResponse(HttpStatus.NOT_FOUND,
                HttpStatus.NOT_FOUND.value(), "Cart not yet created"));
    }

    @PostMapping()
    public ResponseEntity<Cart> upsertCart(@RequestBody OrderForm orderForm) {
        List<OrderProductDto> productOrders = orderForm.getProductOrders();
        validateIfProductsExists(productOrders);
        Cart cart = new Cart();
        cart.setUser(orderForm.getUser());
        Optional<Cart> thisUserIdCart = cartService.getThisUserIdCart(orderForm.getUser().getId());

        if (thisUserIdCart.isPresent()) { // set current cart id to existing cart id
            cartService.cleanCartItemOfThisCartId(thisUserIdCart.get().getId());
            cart.setId(thisUserIdCart.get().getId());
        }

        cart = cartService.upsertCart(cart);
        Cart finalCart = cart;

        Set<CartItems> collect = orderForm.getProductOrders().stream().map(item -> {
            CartItems cartItems = new CartItems();
            cartItems.setQuantity(item.getQuantity());
            cartItems.setPk(new CartPK(finalCart, productService.getProduct(item.getProduct().getId())));
            return cartService.upsertCartItem(cartItems);
        }).collect(Collectors.toSet());

        finalCart.setCartItems(collect);
        cartService.upsertCart(finalCart);

        String uri = ServletUriComponentsBuilder
                .fromCurrentServletMapping()
                .path("/cart/{id}")
                .buildAndExpand(finalCart.getId())
                .toString();

        HttpHeaders headers = new HttpHeaders();
        headers.add("Location", uri);
        return new ResponseEntity<>(finalCart, headers, HttpStatus.CREATED);
    }

    private void validateIfProductsExists(List<OrderProductDto> productOrders) {
        List<OrderProductDto> collect = productOrders.stream()
                .filter(product -> Objects
                        .isNull(productService.getProduct(
                                product.getProduct().getId())))
                .collect(Collectors.toList());

        if (!collect.isEmpty()) { //filter those which are not present, if it is not
            // empty means we have some non existing product
            throw new EcomError(new ErrorResponse(HttpStatus.NOT_FOUND, HttpStatus.NOT_FOUND.value(),
                    "Product Not found"));
        }
    }
}
