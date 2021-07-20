package com.bhuvancom.ecom.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Transient;
import java.io.Serializable;

/**
 * Created Using IntelliJ Idea
 *
 * @author Bhuvaneshvar
 * Date    7/18/2021
 * Time    1:24 PM
 * Project ecomNew
 */
@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class CartItems implements Serializable {
    @EmbeddedId
    @JsonIgnore
    private CartPK pk;

    @Column(nullable = false)
    private Integer quantity;

    public CartItems(Cart cart, Product product, Integer quantity) {
        pk = new CartPK(cart, product);
        this.quantity = quantity;
    }

    @Transient
    public Product getProduct() {
        return this.pk.getProduct();
    }

    @Transient
    public Double getTotalPrice() {
        return getProduct().getPrice() * getQuantity();
    }
}
