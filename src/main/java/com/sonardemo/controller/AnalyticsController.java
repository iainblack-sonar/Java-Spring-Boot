package com.sonardemo.controller;

import com.sonardemo.service.AnalyticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/analytics")
public class AnalyticsController {

    @Autowired
    private AnalyticsService analyticsService;

    @PostMapping("/sales/metrics")
    public ResponseEntity<Map<String, Object>> getSalesMetrics(@RequestBody List<Map<String, Object>> orders) {
        return ResponseEntity.ok(analyticsService.calculateSalesMetrics(orders));
    }

    @PostMapping("/products/metrics")
    public ResponseEntity<Map<String, Object>> getProductMetrics(@RequestBody List<Map<String, Object>> products) {
        return ResponseEntity.ok(analyticsService.calculateProductMetrics(products));
    }

    @PostMapping("/users/metrics")
    public ResponseEntity<Map<String, Object>> getUserMetrics(@RequestBody List<Map<String, Object>> users) {
        return ResponseEntity.ok(analyticsService.calculateUserMetrics(users));
    }

    @GetMapping("/customer/segment")
    public ResponseEntity<String> getCustomerSegment(
            @RequestParam double totalSpend,
            @RequestParam int orderCount,
            @RequestParam int daysSinceLastOrder,
            @RequestParam double avgOrderValue,
            @RequestParam(defaultValue = "0") int returnsCount,
            @RequestParam(defaultValue = "0.0") double returnRate,
            @RequestParam(defaultValue = "0") int accountAge,
            @RequestParam(defaultValue = "false") boolean hasSubscription) {
        
        String segment = analyticsService.determineCustomerSegment(
            totalSpend, orderCount, daysSinceLastOrder, avgOrderValue,
            returnsCount, returnRate, accountAge, hasSubscription
        );
        return ResponseEntity.ok(segment);
    }

    @PostMapping("/customer/churn-probability")
    public ResponseEntity<Double> getChurnProbability(@RequestBody Map<String, Object> customer) {
        return ResponseEntity.ok(analyticsService.calculateChurnProbability(customer));
    }

    @PostMapping("/forecast")
    public ResponseEntity<Map<String, Double>> forecastRevenue(
            @RequestBody List<Double> historicalData,
            @RequestParam(defaultValue = "6") int periodsAhead) {
        return ResponseEntity.ok(analyticsService.forecastRevenue(historicalData, periodsAhead));
    }

    @PostMapping("/anomalies")
    public ResponseEntity<List<Map<String, Object>>> detectAnomalies(
            @RequestBody List<Map<String, Object>> data,
            @RequestParam String metricField) {
        return ResponseEntity.ok(analyticsService.identifyAnomalies(data, metricField));
    }
}

