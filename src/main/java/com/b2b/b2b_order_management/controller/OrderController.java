package com.b2b.b2b_order_management.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.b2b.b2b_order_management.dto.OrderCreateRequest;
import com.b2b.b2b_order_management.dto.OrderResponse;
import com.b2b.b2b_order_management.service.OrderService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(@Valid @RequestBody OrderCreateRequest request) {
        OrderResponse response = orderService.createOrder(request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderResponse> getOrderById(@PathVariable Long id) {
        return ResponseEntity.ok(orderService.getOrderById(id));
    }

    @GetMapping("/code/{orderCode}")
    public ResponseEntity<OrderResponse> getOrderByOrderCode(@PathVariable String orderCode) {
        return ResponseEntity.ok(orderService.getOrderByOrderCode(orderCode));
    }

    @GetMapping("/company/{companyId}")
    public ResponseEntity<List<OrderResponse>> getOrdersByCompanyId(@PathVariable Long companyId) {
        return ResponseEntity.ok(orderService.getOrdersByCompanyId(companyId));
    }

    @GetMapping
    public ResponseEntity<List<OrderResponse>> getAllOrders() {
        return ResponseEntity.ok(orderService.getAllOrders());
    }

    @PutMapping("/{id}/cancel")
    public ResponseEntity<OrderResponse> cancelOrder(@PathVariable Long id) {
        return ResponseEntity.ok(orderService.cancelOrder(id));
    }

    @PutMapping("/{id}/complete")
    public ResponseEntity<OrderResponse> completeOrder(@PathVariable Long id) {
        return ResponseEntity.ok(orderService.completeOrder(id));
    }
}
