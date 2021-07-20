package com.bhuvancom.ecom.controller;

import com.bhuvancom.ecom.exception.EcomError;
import com.bhuvancom.ecom.exception.MyApiExceptionHandler;
import com.bhuvancom.ecom.exception.model.ErrorResponse;
import com.bhuvancom.ecom.model.Cart;
import com.bhuvancom.ecom.model.Order;
import com.bhuvancom.ecom.model.User;
import com.bhuvancom.ecom.service.CartService;
import com.bhuvancom.ecom.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

/**
 * Created Using IntelliJ Idea
 *
 * @author Bhuvaneshvar
 * Date    7/17/2021
 * Time    10:57 PM
 * Project ecomNew
 */
@RestController()
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService mUserService;

    @PostMapping()
    public ResponseEntity<User> upsertUser(@RequestBody User user) {
        return new ResponseEntity<>(mUserService.upsertUser(user), HttpStatus.CREATED);
    }

    @GetMapping("/orders/{user_id}")
    public List<Order> getThisUserOrders(@PathVariable(name = "user_id") int id) {
        return mUserService.findOrdersOfThisUser(id);
    }

    @PostMapping("/login")
    public ResponseEntity<Object> loginUser(@RequestParam(name = "email") String email,
                                            @RequestParam(name = "password") String password) {
        Optional<User> login = mUserService.login(email, password);
        if (login.isPresent()) {
            return new ResponseEntity<>(login.get(), HttpStatus.OK);
        }
        throw new EcomError(new ErrorResponse(HttpStatus.UNAUTHORIZED, HttpStatus.UNAUTHORIZED.value(), "Email or password is wrong"));
    }
}
