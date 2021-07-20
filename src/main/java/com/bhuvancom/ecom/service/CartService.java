package com.bhuvancom.ecom.service;

import com.bhuvancom.ecom.model.Cart;
import com.bhuvancom.ecom.model.CartItems;
import com.bhuvancom.ecom.repository.CartItemRepo;
import com.bhuvancom.ecom.repository.CartRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Created Using IntelliJ Idea
 *
 * @author Bhuvaneshvar
 * Date    7/18/2021
 * Time    1:31 PM
 * Project ecomNew
 */
@Service
@Transactional
public class CartService {
    @Autowired
    private CartRepository cartRepository;
    @Autowired
    private CartItemRepo cartItemRepo;

    public Cart upsertCart(Cart cart) {
        return cartRepository.save(cart);
    }

    public CartItems upsertCartItem(CartItems cartItems) {
        return cartItemRepo.save(cartItems);
    }

    public Optional<Cart> getThisUserIdCart(Long userId) {
        return cartRepository.findTopByUserId(userId);
    }

    public void cleanCartItemOfThisCartId(Long cartId) {
        cartRepository.findById(cartId).ifPresent(cart -> cartItemRepo.deleteByCartId(cart.getId()));
    }


}
