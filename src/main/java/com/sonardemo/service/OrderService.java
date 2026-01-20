package com.sonardemo.service;

import com.sonardemo.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    public List<Map<String, Object>> getCustomerOrders(String customerId) {
        if (customerId == null || customerId.isEmpty()) {
            throw new IllegalArgumentException("Customer ID is required");
        }
        return orderRepository.findByCustomerId(customerId);
    }

    public List<Map<String, Object>> getOrdersByDateRange(String startDate, String endDate, String status) {
        return orderRepository.findByDateRangeAndStatus(startDate, endDate, status);
    }

    public List<Map<String, Object>> searchProducts(String keyword, Double minPrice, Double maxPrice, String sortBy) {
        return orderRepository.searchProducts(keyword, minPrice, maxPrice, sortBy);
    }

    public Map<String, Object> getOrderDetails(String orderId) {
        Map<String, Object> order = orderRepository.findOrderWithDetails(orderId);
        if (order == null) {
            throw new RuntimeException("Order not found: " + orderId);
        }
        return order;
    }

    public List<Map<String, Object>> filterOrders(String customerId, String status, String productName, String dateFrom) {
        Map<String, String> filters = new HashMap<>();
        if (customerId != null) filters.put("customer_id", customerId);
        if (status != null) filters.put("status", status);
        if (productName != null) filters.put("product_name", productName);
        if (dateFrom != null) filters.put("order_date", dateFrom);
        
        return orderRepository.getOrdersByFilters(filters);
    }

    public List<Map<String, Object>> generateSalesReport(String groupBy, String startDate, String endDate) {
        return orderRepository.generateReport("sales", groupBy, startDate, endDate);
    }

    public List<Map<String, Object>> generateProductReport(String groupBy, String startDate, String endDate) {
        return orderRepository.generateReport("products", groupBy, startDate, endDate);
    }

    public void updateStatus(String orderId, String status, String userId) {
        int updated = orderRepository.updateOrderStatus(orderId, status, userId);
        if (updated == 0) {
            throw new RuntimeException("Failed to update order: " + orderId);
        }
    }

    public void cancelOrder(String orderId) {
        boolean deleted = orderRepository.deleteOrder(orderId);
        if (!deleted) {
            throw new RuntimeException("Failed to cancel order: " + orderId);
        }
    }
}
