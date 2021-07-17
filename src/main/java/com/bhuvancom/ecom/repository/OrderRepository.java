package com.bhuvancom.ecom.repository;

import com.bhuvancom.ecom.model.Order;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends PagingAndSortingRepository<Order, Long> {

    @Query("from Order where user_id = ?1")
    List<Order> findByUserId(int id);
}
