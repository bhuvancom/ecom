package com.bhuvancom.ecom.controller;

import com.bhuvancom.ecom.exception.EcomError;
import com.bhuvancom.ecom.exception.model.ErrorResponse;
import com.bhuvancom.ecom.model.Order;
import com.bhuvancom.ecom.model.User;
import com.bhuvancom.ecom.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    @PostMapping("/update")
    public ResponseEntity<User> update(@RequestBody User user,
                                       @RequestParam(value = "old_password", defaultValue = "") String oldPass) throws Exception {
        Optional<User> login = mUserService.login(user.getUserEmail(), oldPass);
        if (!login.isPresent()) {
            throw new EcomError(new ErrorResponse(HttpStatus.BAD_REQUEST,
                    HttpStatus.BAD_REQUEST.value(),
                    "Please check your input"));
        } else {
            return upsertUser(user);
        }
    }

    @PostMapping()
    public ResponseEntity<User> upsertUser(@RequestBody User user) throws Exception {
        if (user.getUserEmail() == null || !isEmailCorrect(user.getUserEmail()) || user.getUserPassword().trim().isEmpty()) {
            throw new EcomError(new ErrorResponse(HttpStatus.BAD_REQUEST,
                    HttpStatus.BAD_REQUEST.value(), "Email address/password is not correct"));
        } else {
            return new ResponseEntity<>(mUserService.upsertUser(user), HttpStatus.CREATED);
        }
    }

    private boolean isEmailCorrect(String userEmail) {
        String regex = "^[\\w!#$%&’*+/=?`{|}~^-]+(?:\\.[\\w!#$%&’*+/=?`{|}~^-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,6}$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(userEmail);
        return matcher.matches();
    }

    @GetMapping("/orders/{user_id}")
    public Page<Order> getThisUserOrders(@PathVariable(name = "user_id") int id,
                                         @RequestParam(value = "page", defaultValue = "1") int page) {
        return mUserService.findOrdersOfThisUser(id, page);
    }

    @PostMapping("/login")
    public ResponseEntity<Object> loginUser(@RequestParam(name = "email") String email,
                                            @RequestParam(name = "password") String password) throws Exception {
        Optional<User> login = mUserService.login(email, password);
        if (login.isPresent()) {
            return new ResponseEntity<>(login.get(), HttpStatus.OK);
        }
        throw new EcomError(new ErrorResponse(HttpStatus.UNAUTHORIZED, HttpStatus.UNAUTHORIZED.value(), "Email or password is wrong"));
    }
}
