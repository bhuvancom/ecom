package com.bhuvancom.ecom.repository;

import com.bhuvancom.ecom.model.Cart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Created Using IntelliJ Idea
 *
 * @author Bhuvaneshvar
 * Date    7/18/2021
 * Time    1:29 PM
 * Project ecomNew
 */
@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {
    //@Query("from Cart where user_id =?1")
    Optional<Cart> findTopByUserId(Long userId);
}

