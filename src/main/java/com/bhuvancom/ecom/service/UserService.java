package com.bhuvancom.ecom.service;

import com.bhuvancom.ecom.model.Order;
import com.bhuvancom.ecom.model.User;
import com.bhuvancom.ecom.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.constraints.Null;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class UserService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private OrderService mOrderService;

    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByUserEmail(email);
    }

    public User upsertUser(User user) {
        return userRepository.save(user);
    }

    public Optional<User> login(String email, String password) {
        Optional<User> byUserEmail = userRepository.findByUserEmail(email);
        if (byUserEmail.isPresent()) {
            boolean equals = byUserEmail.get().getUserPassword().equals(password);
            if (equals) return byUserEmail;
        }
        return Optional.empty();
    }

    public List<Order> findOrdersOfThisUser(int id) {
        List<Order> orders = mOrderService.getOrderByUserId(id);
        return orders;
    }
}
