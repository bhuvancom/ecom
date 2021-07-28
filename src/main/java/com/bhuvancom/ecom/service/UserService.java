package com.bhuvancom.ecom.service;

import com.bhuvancom.ecom.exception.EcomError;
import com.bhuvancom.ecom.exception.model.ErrorResponse;
import com.bhuvancom.ecom.model.Order;
import com.bhuvancom.ecom.model.User;
import com.bhuvancom.ecom.repository.UserRepository;
import com.bhuvancom.ecom.util.PasswordManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Base64;
import java.util.Optional;

@Service
@Transactional
public class UserService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private OrderService mOrderService;

    public static final Logger logger = LoggerFactory.getLogger(UserService.class.getName());

    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByUserEmail(email);
    }

    /**
     * Expecting non cipher password, i will encypt that.
     *
     * @param user User
     * @return updated user
     * @throws NoSuchPaddingException             error
     * @throws InvalidKeyException                error
     * @throws NoSuchAlgorithmException           error
     * @throws IllegalBlockSizeException          error
     * @throws BadPaddingException                error
     * @throws InvalidAlgorithmParameterException error
     */
    public User upsertUser(User user) throws Exception {
        user.setUserPassword(PasswordManager.encrypt(user.getUserPassword()));
        if (user.getId() == null || user.getId().equals(0L)) {
            Optional<User> isAlreadyPresent = getUserByEmail(user.getUserEmail());
            if (isAlreadyPresent.isPresent()) {
                throw new EcomError(new ErrorResponse(
                        HttpStatus.BAD_REQUEST,
                        HttpStatus.BAD_REQUEST.value(),
                        "Email address already in use"
                ));
            }
        }
        return userRepository.save(user);
    }

    /**
     * I want plain text as password, i will match with that.
     *
     * @param email    email
     * @param password password
     * @return user if found
     * @throws NoSuchPaddingException             error
     * @throws InvalidKeyException                error
     * @throws NoSuchAlgorithmException           error
     * @throws IllegalBlockSizeException          error
     * @throws BadPaddingException                error
     * @throws InvalidAlgorithmParameterException error
     */
    public Optional<User> login(String email, String password) throws Exception {
        Optional<User> byUserEmail = userRepository.findByUserEmail(email);
        logger.info("Logging user {}", byUserEmail);
        if (byUserEmail.isPresent()) {
            String savedPswd = byUserEmail.get().getUserPassword();
            String decrypt = PasswordManager.decrypt(savedPswd);
            logger.info("ps {},dec pass {}", password, decrypt);
            boolean equals = decrypt.equals(password);
            if (equals) return byUserEmail;
        }
        return Optional.empty();
    }

    public Optional<User> findUserById(Long userId) {
        return userRepository.findById(userId);
    }

    public Page<Order> findOrdersOfThisUser(int id, int page) {
        Pageable pageable = PageRequest.of(page - 1, 5);
        return mOrderService.getOrderByUserId(id, pageable);
    }

}
