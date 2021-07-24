package com.bhuvancom.ecom.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.Valid;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created Using IntelliJ Idea
 *
 * @author Bhuvaneshvar
 * Date    7/18/2021
 * Time    1:21 PM
 * Project ecomNew
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Cart {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(optional = false)
    @JoinColumn(name = "user_id", unique = true, updatable = false)
    private User user;

    @JsonManagedReference
    @OneToMany(mappedBy = "pk.cart", cascade = CascadeType.ALL)
    @Valid
    Set<CartItems> cartItems = new HashSet<>();

    @Transient
    public Double getTotalCartPrice() {
        double sum = 0D;
        Set<CartItems> cartItems = getCartItems();
        for (CartItems op : cartItems) {
            sum += op.getTotalPrice();
        }
        return sum;
    }

    @Transient
    public int getNumberOfProducts() {
        return this.cartItems.size();
    }

}
