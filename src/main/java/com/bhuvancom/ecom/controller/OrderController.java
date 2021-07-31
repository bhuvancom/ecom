package com.bhuvancom.ecom.controller;

import com.bhuvancom.ecom.dto.OrderProductDto;
import com.bhuvancom.ecom.exception.EcomError;
import com.bhuvancom.ecom.exception.model.ErrorResponse;
import com.bhuvancom.ecom.model.*;
import com.bhuvancom.ecom.service.CartService;
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
import org.springframework.data.domain.jaxb.SpringDataJaxb;
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
    CartService cartService;

    @Autowired
    private UserService userService;

    @Value("${stripe.key}")
    String stripeKey;

    @PostMapping("/payment")
    @ResponseBody
    public Payment acceptPaymnet(HttpServletRequest request,
                                 HttpServletResponse response, @RequestBody User userBody) {
        LOGGER.info("entering payment");
        //if(1!=2)return  null;
        Optional<User> user = userService.findUserById(userBody.getId());
        if (!user.isPresent()) {
            LOGGER.info("user not found {} ", userBody);
            throw new EcomError(new ErrorResponse(HttpStatus.NOT_FOUND, HttpStatus.NOT_FOUND.value(), "User not found"));
        }

        Optional<Cart> thisUserIdCart = cartService.getThisUserIdCart(user.get().getId());
        if (!thisUserIdCart.isPresent()) {
            throw new EcomError(new ErrorResponse(HttpStatus.NOT_FOUND, HttpStatus.NOT_FOUND.value(),
                    "Cart step bypassed"));
        }

        response.setContentType("application/json");
        try {
            List<OrderProductDto> collect = thisUserIdCart.get().getCartItems().stream().map(cartItem -> {
                OrderProductDto dto = new OrderProductDto();
                dto.setProduct(cartItem.getProduct());
                dto.setQuantity(cartItem.getQuantity());
                return dto;
            }).collect(Collectors.toList());

            LOGGER.info("entering payment going to map price");
            validateIfProductsExists(collect);
            double sum = collect.stream().peek(orderProductDto -> {
                LOGGER.info("collect {}", orderProductDto);
            }).mapToDouble(orderProductDto -> {
                LOGGER.info("calculating {} , qty {}", orderProductDto.getProduct().getPrice(),
                        orderProductDto.getQuantity());

                return orderProductDto.getProduct().getPrice() * orderProductDto.getQuantity();
            }).sum();
            User userr = user.get();

            LOGGER.info("Amount is set to {}, for user {}", sum, user);

            Stripe.apiKey = stripeKey;
            CustomerCreateParams ccp = CustomerCreateParams.builder()
                    .setEmail(userr.getUserEmail())
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

            thisUserIdCart.get().setPaymentId(payment.getEphemeralKey());
            cartService.upsertCart(thisUserIdCart.get());

            return payment;

        } catch (StripeException e) {
            e.printStackTrace();
            LOGGER.error("Error occurred while payment {}", e.getMessage());
            throw new EcomError(new
                    ErrorResponse(HttpStatus.PAYMENT_REQUIRED,
                    HttpStatus.PAYMENT_REQUIRED.value(), "Payment failed"));
        }

    }

    /**
     * This method will convert cart into order based on previous payment.
     *
     * @param orderForm the order form, its needed that key is sent with this
     * @return Order updated order
     */
    @PostMapping()
    public ResponseEntity<Order> upsertOrder(@RequestBody OrderForm orderForm) {
        Optional<User> user = userService.findUserById(orderForm.getUser().getId());
        if (!user.isPresent()) {
            LOGGER.info("user not found {} ", orderForm.getUser());
            throw new EcomError(new ErrorResponse(HttpStatus.NOT_FOUND, HttpStatus.NOT_FOUND.value(),
                    "User not found"));
        }

        Optional<Cart> thisUserIdCart = cartService.getThisUserIdCart(user.get().getId());
        if (!thisUserIdCart.isPresent()) {
            throw new EcomError(new ErrorResponse(HttpStatus.NOT_FOUND, HttpStatus.NOT_FOUND.value(),
                    "Cart step bypassed"));
        }
        List<OrderProductDto> productOrders = thisUserIdCart.get().getCartItems().stream().map(cartItem -> {
            OrderProductDto dto = new OrderProductDto();
            dto.setProduct(cartItem.getProduct());
            dto.setQuantity(cartItem.getQuantity());
            return dto;
        }).collect(Collectors.toList());

        validateIfProductsExists(productOrders);
        Order order = new Order();
        order.setUser(user.get());

        if (!thisUserIdCart.get().getPaymentId().equals(orderForm.getPaymentId())) {
            throw new EcomError(new ErrorResponse(HttpStatus.BAD_REQUEST,
                    HttpStatus.BAD_REQUEST.value(),
                    "Payment id did not match"));
        }

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
        order.setPaymentId(thisUserIdCart.get().getPaymentId());
        order.setOrderProducts(orderProducts);
        mOrderService.upsertOrder(order);
        cartService.cleanCartItemOfThisCartId(thisUserIdCart.get().getId());
        thisUserIdCart.get().setPaymentId(null);
        cartService.upsertCart(thisUserIdCart.get());

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
