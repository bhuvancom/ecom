package com.bhuvancom.ecom.model;

import lombok.ToString;

@ToString
public enum OrderStatus {
    PAID,
    UNPAID,
    FAILED,
    CANCEL
}
