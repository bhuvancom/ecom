package com.bhuvancom.ecom.repository;

import com.bhuvancom.ecom.model.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends PagingAndSortingRepository<Order, Long> {

    @Query(value = "from Order where user_id = ?1",
            countQuery = "select count(*) from Order where user_id = ?1")
    Page<Order> findByUserId(int id, Pageable pageable);
}
