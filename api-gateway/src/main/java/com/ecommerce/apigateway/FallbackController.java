package com.ecommerce.apigateway;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/fallback")
public class FallbackController {

    @GetMapping("/auth")
    public Mono<ResponseEntity<String>> authServiceFallback() {
        return Mono.just(ResponseEntity
                .status(HttpStatus.SERVICE_UNAVAILABLE)
                .body("Auth service is currently unavailable. Please try again later."));
    }

    @GetMapping("/user")
    public Mono<ResponseEntity<String>> userServiceFallback() {
        return Mono.just(ResponseEntity
                .status(HttpStatus.SERVICE_UNAVAILABLE)
                .body("User service is currently unavailable. Please try again later."));
    }

    @GetMapping("/product")
    public Mono<ResponseEntity<String>> productServiceFallback() {
        return Mono.just(ResponseEntity
                .status(HttpStatus.SERVICE_UNAVAILABLE)
                .body("Product service is currently unavailable. Please try again later."));
    }

    @GetMapping("/cart")
    public Mono<ResponseEntity<String>> cartServiceFallback() {
        return Mono.just(ResponseEntity
                .status(HttpStatus.SERVICE_UNAVAILABLE)
                .body("Cart service is currently unavailable. Please try again later."));
    }

    @GetMapping("/order")
    public Mono<ResponseEntity<String>> orderServiceFallback() {
        return Mono.just(ResponseEntity
                .status(HttpStatus.SERVICE_UNAVAILABLE)
                .body("Order service is currently unavailable. Please try again later."));
    }

    @GetMapping("/payment")
    public Mono<ResponseEntity<String>> paymentServiceFallback() {
        return Mono.just(ResponseEntity
                .status(HttpStatus.SERVICE_UNAVAILABLE)
                .body("Payment Sevice is currently unavailable. Please try again later."));
    }
}
