package com.bhuvancom.ecom.service;

import com.bhuvancom.ecom.model.Order;
import com.bhuvancom.ecom.model.OrderProduct;
import com.bhuvancom.ecom.repository.OrderProductRepository;
import com.bhuvancom.ecom.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class OrderService {

    @Autowired
    private OrderRepository mOrderRespository;

    @Autowired
    private OrderProductRepository orderProductRepository;

    public Iterable<Order> getAllOrders() {
        return mOrderRespository.findAll();
    }

    public Order upsertOrder(Order orders) {
        return mOrderRespository.save(orders);
    }

    public OrderProduct upsertOrderProduct(OrderProduct orderProduct) {
        return orderProductRepository.save(orderProduct);
    }

    public void deleteOrder(Order orders) {
        mOrderRespository.delete(orders);
    }

    public List<Order> getOrderByUserId(int id) {
        return mOrderRespository.findByUserId(id);
    }
}
