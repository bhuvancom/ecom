package com.bhuvancom.ecom.model;

import lombok.Data;
import lombok.ToString;

/**
 * Created Using IntelliJ Idea
 *
 * @author Bhuvaneshvar
 * Date    7/27/2021
 * Time    9:56 PM
 * Project ecomNew
 */
@Data
@ToString
public class Payment {
    private String paymentIntent;
    private String customer;
    private String ephemeralKey;
}
