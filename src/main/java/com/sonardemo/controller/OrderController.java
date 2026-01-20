package com.sonardemo.controller;

import com.sonardemo.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    @GetMapping("/customer/{customerId}")
    public ResponseEntity<List<Map<String, Object>>> getCustomerOrders(@PathVariable String customerId) {
        return ResponseEntity.ok(orderService.getCustomerOrders(customerId));
    }

    @GetMapping("/{orderId}")
    public ResponseEntity<Map<String, Object>> getOrderDetails(@PathVariable String orderId) {
        return ResponseEntity.ok(orderService.getOrderDetails(orderId));
    }

    @GetMapping("/search")
    public ResponseEntity<List<Map<String, Object>>> searchOrders(
            @RequestParam(required = false) String customerId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate) {
        
        if (startDate != null && endDate != null) {
            return ResponseEntity.ok(orderService.getOrdersByDateRange(startDate, endDate, status));
        }
        return ResponseEntity.ok(orderService.filterOrders(customerId, status, null, startDate));
    }

    @GetMapping("/products")
    public ResponseEntity<List<Map<String, Object>>> searchProducts(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(required = false) String sortBy) {
        return ResponseEntity.ok(orderService.searchProducts(keyword, minPrice, maxPrice, sortBy));
    }

    @GetMapping("/reports/sales")
    public ResponseEntity<List<Map<String, Object>>> getSalesReport(
            @RequestParam String groupBy,
            @RequestParam String startDate,
            @RequestParam String endDate) {
        return ResponseEntity.ok(orderService.generateSalesReport(groupBy, startDate, endDate));
    }

    @GetMapping("/reports/products")
    public ResponseEntity<List<Map<String, Object>>> getProductReport(
            @RequestParam String groupBy,
            @RequestParam String startDate,
            @RequestParam String endDate) {
        return ResponseEntity.ok(orderService.generateProductReport(groupBy, startDate, endDate));
    }

    @PutMapping("/{orderId}/status")
    public ResponseEntity<Void> updateOrderStatus(
            @PathVariable String orderId,
            @RequestParam String status,
            @RequestHeader("X-User-Id") String userId) {
        orderService.updateStatus(orderId, status, userId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{orderId}")
    public ResponseEntity<Void> cancelOrder(@PathVariable String orderId) {
        orderService.cancelOrder(orderId);
        return ResponseEntity.noContent().build();
    }
}

