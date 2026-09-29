package com.b2b.b2b_order_management.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.b2b.b2b_order_management.dto.OrderCreateRequest;
import com.b2b.b2b_order_management.dto.OrderItemRequest;
import com.b2b.b2b_order_management.dto.OrderItemResponse;
import com.b2b.b2b_order_management.dto.OrderResponse;
import com.b2b.b2b_order_management.entity.Company;
import com.b2b.b2b_order_management.entity.Inventory;
import com.b2b.b2b_order_management.entity.Order;
import com.b2b.b2b_order_management.entity.OrderItem;
import com.b2b.b2b_order_management.entity.OrderStatus;
import com.b2b.b2b_order_management.entity.Product;
import com.b2b.b2b_order_management.exception.InsufficientCreditException;
import com.b2b.b2b_order_management.exception.InsufficientStockException;
import com.b2b.b2b_order_management.exception.ResourceNotFoundException;
import com.b2b.b2b_order_management.repository.CompanyRepository;
import com.b2b.b2b_order_management.repository.InventoryRepository;
import com.b2b.b2b_order_management.repository.OrderRepository;
import com.b2b.b2b_order_management.repository.ProductRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final InventoryRepository inventoryRepository;
    private final CompanyRepository companyRepository;

    @Transactional
    public OrderResponse createOrder(OrderCreateRequest request) {
        Company company = companyRepository.findById(request.getCompanyId())
                .orElseThrow(() -> new ResourceNotFoundException("Company not found with id: " + request.getCompanyId()));

        if (request.getItems() == null || request.getItems().isEmpty()) {
            throw new IllegalArgumentException("Order must contain at least one item");
        }

        BigDecimal totalOrderAmount = BigDecimal.ZERO;
        List<OrderItem> orderItems = new ArrayList<>();

        Order order = Order.builder()
                .company(company)
                .status(OrderStatus.PENDING)
                .build();

        for (OrderItemRequest itemRequest : request.getItems()) {
            Product product = productRepository.findById(itemRequest.getProductId())
                    .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + itemRequest.getProductId()));

            Inventory inventory = inventoryRepository.findByProductId(product.getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Inventory record not found for product: " + product.getName()));

            int availableQuantity = inventory.getQuantity() - inventory.getReservedQuantity();
            if (availableQuantity < itemRequest.getQuantity()) {
                throw new InsufficientStockException("Insufficient available stock for product: " + product.getName()
                        + ". Available: " + availableQuantity + ", Requested: " + itemRequest.getQuantity());
            }

            // Reserve stock
            inventory.setReservedQuantity(inventory.getReservedQuantity() + itemRequest.getQuantity());
            inventoryRepository.save(inventory);

            BigDecimal lineTotal = product.getPrice().multiply(BigDecimal.valueOf(itemRequest.getQuantity()));
            totalOrderAmount = totalOrderAmount.add(lineTotal);

            OrderItem orderItem = OrderItem.builder()
                    .order(order)
                    .product(product)
                    .quantity(itemRequest.getQuantity())
                    .unitPrice(product.getPrice())
                    .totalPrice(lineTotal)
                    .build();

            orderItems.add(orderItem);
        }

        // B2B Credit Limit Verification
        BigDecimal currentBalance = company.getCurrentBalance() != null ? company.getCurrentBalance() : BigDecimal.ZERO;
        BigDecimal availableCredit = company.getCreditLimit().subtract(currentBalance);
        if (availableCredit.compareTo(totalOrderAmount) < 0) {
            throw new InsufficientCreditException("Insufficient credit limit for company: " + company.getName()
                    + ". Available credit: " + availableCredit + ", Order total: " + totalOrderAmount);
        }

        order.setTotalAmount(totalOrderAmount);
        order.setOrderItems(orderItems);

        Order savedOrder = orderRepository.save(order);
        return mapToResponse(savedOrder);
    }

    @Transactional(readOnly = true)
    public OrderResponse getOrderById(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + id));
        return mapToResponse(order);
    }

    @Transactional(readOnly = true)
    public OrderResponse getOrderByOrderCode(String orderCode) {
        Order order = orderRepository.findByOrderCode(orderCode)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with code: " + orderCode));
        return mapToResponse(order);
    }

    @Transactional(readOnly = true)
    public List<OrderResponse> getOrdersByCompanyId(Long companyId) {
        return orderRepository.findByCompanyId(companyId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<OrderResponse> getAllOrders() {
        return orderRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public OrderResponse cancelOrder(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + id));

        if (order.getStatus() == OrderStatus.COMPLETED || order.getStatus() == OrderStatus.CANCELLED) {
            throw new IllegalStateException("Cannot cancel order in status: " + order.getStatus());
        }

        // Release reserved stock
        for (OrderItem item : order.getOrderItems()) {
            Inventory inventory = inventoryRepository.findByProductId(item.getProduct().getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Inventory not found for product: " + item.getProduct().getName()));
            
            inventory.setReservedQuantity(Math.max(0, inventory.getReservedQuantity() - item.getQuantity()));
            inventoryRepository.save(inventory);
        }

        order.setStatus(OrderStatus.CANCELLED);
        Order updatedOrder = orderRepository.save(order);
        return mapToResponse(updatedOrder);
    }

    private OrderResponse mapToResponse(Order order) {
        List<OrderItemResponse> itemResponses = order.getOrderItems().stream()
                .map(item -> OrderItemResponse.builder()
                        .id(item.getId())
                        .productId(item.getProduct().getId())
                        .productName(item.getProduct().getName())
                        .productSku(item.getProduct().getSku())
                        .quantity(item.getQuantity())
                        .unitPrice(item.getUnitPrice())
                        .totalPrice(item.getTotalPrice())
                        .build())
                .collect(Collectors.toList());

        return OrderResponse.builder()
                .id(order.getId())
                .orderCode(order.getOrderCode())
                .companyId(order.getCompany().getId())
                .companyName(order.getCompany().getName())
                .status(order.getStatus())
                .totalAmount(order.getTotalAmount())
                .items(itemResponses)
                .createdAt(order.getCreatedAt())
                .build();
    }
}
