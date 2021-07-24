package com.bhuvancom.ecom.repository;

import com.bhuvancom.ecom.model.CartItems;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created Using IntelliJ Idea
 *
 * @author Bhuvaneshvar
 * Date    7/18/2021
 * Time    1:32 PM
 * Project ecomNew
 */
@Repository
public interface CartItemRepo extends JpaRepository<CartItems, Long> {
    @Modifying
    @Transactional
    @Query("DELETE FROM CartItems where cart_id = ?1")
    void deleteByCartId(Long id);
}
