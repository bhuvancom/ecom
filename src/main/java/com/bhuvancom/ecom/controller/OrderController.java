package com.bhuvancom.ecom.controller;

import com.bhuvancom.ecom.dto.OrderProductDto;
import com.bhuvancom.ecom.exception.EcomError;
import com.bhuvancom.ecom.exception.model.ErrorResponse;
import com.bhuvancom.ecom.model.*;
import com.bhuvancom.ecom.service.OrderService;
import com.bhuvancom.ecom.service.ProductService;
import com.bhuvancom.ecom.service.UserService;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Customer;
import com.stripe.model.EphemeralKey;
import com.stripe.model.PaymentIntent;
import com.stripe.net.RequestOptions;
import com.stripe.param.CustomerCreateParams;
import com.stripe.param.EphemeralKeyCreateParams;
import com.stripe.param.PaymentIntentCreateParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/orders")
public class OrderController {
    private static final Logger LOGGER = LoggerFactory.getLogger(OrderController.class.getName());

    @Autowired
    private OrderService mOrderService;
    @Autowired
    private ProductService productService;

    @Autowired
    private UserService userService;

    @Value("${stripe.key}")
    String stripeKey;

    @PostMapping("/payment")
    public Payment acceptPaymnet(HttpServletRequest request,
                                 HttpServletResponse response, @RequestBody OrderForm of) {
        LOGGER.info("entering payment");
        //if(1!=2)return  null;
        Optional<User> user = userService.findUserById(of.getUser().getId());
        if (!user.isPresent()) {
            LOGGER.info("user not found {} ", of.getUser());
            throw new EcomError(new ErrorResponse(HttpStatus.NOT_FOUND, HttpStatus.NOT_FOUND.value(), "User not found"));
        }
        response.setContentType("application/json");
        try {
            List<OrderProductDto> productOrders = of.getProductOrders();
            LOGGER.info("entering payment going to map price");
            validateIfProductsExists(productOrders);
            double sum = productOrders.stream().peek(orderProductDto ->
                    LOGGER.info("product {}", orderProductDto)).mapToDouble(orderProductDto -> {
                LOGGER.info("calculating {} , qty {}", orderProductDto.getProduct().getPrice(), orderProductDto.getQuantity());
                return orderProductDto.getProduct().getPrice() * orderProductDto.getQuantity();
            }).sum();
            User userr = user.get();

            LOGGER.info("Amount is set to {}, for user {}", sum, user);

            Stripe.apiKey = stripeKey;
            CustomerCreateParams ccp = CustomerCreateParams.builder()
                    .setEmail(userr.getUserEmail())
                    //add api key here some where
                    .build();
            Customer customer = Customer.create(ccp);
            LOGGER.info("Customer set to {}", customer);
            EphemeralKeyCreateParams ekcp = EphemeralKeyCreateParams.builder()
                    .setCustomer(customer.getId()).build();

            RequestOptions ro = new RequestOptions.RequestOptionsBuilder()
                    .setStripeVersionOverride("2020-08-27").build();
            EphemeralKey ek = EphemeralKey.create(ekcp, ro);

            PaymentIntentCreateParams picp = PaymentIntentCreateParams.builder()
                    .setAmount((long) sum)
                    .setCurrency("usd")
                    .setCustomer(customer.getId())
                    .build();

            PaymentIntent paymentIntent = PaymentIntent.create(picp);
            Payment payment = new Payment();
            payment.setCustomer(customer.getId());
            payment.setEphemeralKey(ek.getSecret());
            payment.setPaymentIntent(paymentIntent.getClientSecret());

            return payment;

        } catch (StripeException e) {
            e.printStackTrace();
            LOGGER.error("Error occurred while payment {}", e.getMessage());
            throw new EcomError(new
                    ErrorResponse(HttpStatus.PAYMENT_REQUIRED,
                    HttpStatus.PAYMENT_REQUIRED.value(), "Payment failed"));
        }

    }

    @PostMapping()
    public ResponseEntity<Order> upsertOrder(@RequestBody OrderForm orderForm) {
        List<OrderProductDto> productOrders = orderForm.getProductOrders();
        validateIfProductsExists(productOrders);
        Order order = new Order();
        order.setUser(orderForm.getUser());
        order.setStatus(OrderStatus.PAID.name());
        order = mOrderService.upsertOrder(order);
        List<OrderProduct> orderProducts = new ArrayList<>();
        for (OrderProductDto dto : productOrders) {
            OrderProduct orderProduct = new OrderProduct(order,
                    productService.getProduct(dto.getProduct().getId()),
                    dto.getQuantity()
            );
            orderProducts.add(mOrderService.upsertOrderProduct(orderProduct));
        }

        order.setOrderProducts(orderProducts);
        mOrderService.upsertOrder(order);
        //Order order1 = mOrderService.upsertOrder(order);
        //return new ResponseEntity<>(order1, HttpStatus.CREATED);
        String uri = ServletUriComponentsBuilder
                .fromCurrentServletMapping()
                .path("/orders/{id}")
                .buildAndExpand(order.getId())
                .toString();

        HttpHeaders headers = new HttpHeaders();
        headers.add("Location", uri);
        return new ResponseEntity<>(order, headers, HttpStatus.CREATED);
    }

    private void validateIfProductsExists(List<OrderProductDto> productOrders) {
        LOGGER.info("validating... ");

        List<OrderProductDto> collect = productOrders.stream()
                .filter(product -> {
                    Product product1 = productService.getProduct(product.getProduct().getId());
                    product.setProduct(product1);
                    return Objects.isNull(product.getProduct());
                })
                .collect(Collectors.toList());
        LOGGER.info("validated.. ");
        if (!collect.isEmpty()) { //filter those which are not present, if it is not
            // empty means we have some non existing product
            LOGGER.info("validation fail {} ", collect.toString());

            throw new EcomError(new ErrorResponse(HttpStatus.NOT_FOUND, HttpStatus.NOT_FOUND.value(), "Product Not found"));
        }
    }

    @GetMapping(value = {"", "/"})
    @ResponseStatus(HttpStatus.OK)
    public Iterable<Order> getAllOrders() {
        return mOrderService.getAllOrders();
    }


}
