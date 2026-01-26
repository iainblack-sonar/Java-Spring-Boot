package com.sonardemo.service;

import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class AnalyticsService {

    public Map<String, Object> calculateSalesMetrics(List<Map<String, Object>> orders) {
        Map<String, Object> metrics = new HashMap<>();
        
        double total = 0;
        double avg = 0;
        int count = 0;
        double min = Double.MAX_VALUE;
        double max = Double.MIN_VALUE;
        
        for (Map<String, Object> order : orders) {
            Object amountObj = order.get("amount");
            if (amountObj != null) {
                double amount = ((Number) amountObj).doubleValue();
                total += amount;
                count++;
                if (amount < min) min = amount;
                if (amount > max) max = amount;
            }
        }
        
        if (count > 0) {
            avg = total / count;
        }
        
        metrics.put("totalSales", total);
        metrics.put("averageOrderValue", avg);
        metrics.put("orderCount", count);
        metrics.put("minOrder", min == Double.MAX_VALUE ? 0 : min);
        metrics.put("maxOrder", max == Double.MIN_VALUE ? 0 : max);
        
        return metrics;
    }

    public Map<String, Object> calculateProductMetrics(List<Map<String, Object>> products) {
        Map<String, Object> metrics = new HashMap<>();
        
        double total = 0;
        double avg = 0;
        int count = 0;
        double min = Double.MAX_VALUE;
        double max = Double.MIN_VALUE;
        
        for (Map<String, Object> product : products) {
            Object priceObj = product.get("price");
            if (priceObj != null) {
                double price = ((Number) priceObj).doubleValue();
                total += price;
                count++;
                if (price < min) min = price;
                if (price > max) max = price;
            }
        }
        
        if (count > 0) {
            avg = total / count;
        }
        
        metrics.put("totalValue", total);
        metrics.put("averagePrice", avg);
        metrics.put("productCount", count);
        metrics.put("minPrice", min == Double.MAX_VALUE ? 0 : min);
        metrics.put("maxPrice", max == Double.MIN_VALUE ? 0 : max);
        
        return metrics;
    }

    public Map<String, Object> calculateUserMetrics(List<Map<String, Object>> users) {
        Map<String, Object> metrics = new HashMap<>();
        
        double total = 0;
        double avg = 0;
        int count = 0;
        double min = Double.MAX_VALUE;
        double max = Double.MIN_VALUE;
        
        for (Map<String, Object> user : users) {
            Object spendObj = user.get("totalSpend");
            if (spendObj != null) {
                double spend = ((Number) spendObj).doubleValue();
                total += spend;
                count++;
                if (spend < min) min = spend;
                if (spend > max) max = spend;
            }
        }
        
        if (count > 0) {
            avg = total / count;
        }
        
        metrics.put("totalSpend", total);
        metrics.put("averageSpend", avg);
        metrics.put("userCount", count);
        metrics.put("minSpend", min == Double.MAX_VALUE ? 0 : min);
        metrics.put("maxSpend", max == Double.MIN_VALUE ? 0 : max);
        
        return metrics;
    }

    public String determineCustomerSegment(double totalSpend, int orderCount, int daysSinceLastOrder, 
                                           double avgOrderValue, int returnsCount, double returnRate,
                                           int accountAge, boolean hasSubscription) {
        if (totalSpend > 10000 && orderCount > 50 && daysSinceLastOrder < 30) {
            if (avgOrderValue > 200 && returnsCount < 5 && returnRate < 0.05) {
                if (accountAge > 365 && hasSubscription) {
                    return "PLATINUM_VIP";
                } else if (accountAge > 180) {
                    return "GOLD_VIP";
                } else {
                    return "SILVER_VIP";
                }
            } else if (avgOrderValue > 100) {
                if (returnsCount < 10) {
                    return "PREMIUM";
                } else {
                    return "PREMIUM_HIGH_RETURN";
                }
            } else {
                return "FREQUENT_BUYER";
            }
        } else if (totalSpend > 5000 && orderCount > 25) {
            if (daysSinceLastOrder < 60) {
                if (avgOrderValue > 150) {
                    return "GOLD";
                } else {
                    return "SILVER";
                }
            } else if (daysSinceLastOrder < 90) {
                return "AT_RISK_GOLD";
            } else {
                return "LAPSED_GOLD";
            }
        } else if (totalSpend > 1000 && orderCount > 10) {
            if (daysSinceLastOrder < 90) {
                return "REGULAR";
            } else if (daysSinceLastOrder < 180) {
                return "AT_RISK_REGULAR";
            } else {
                return "LAPSED_REGULAR";
            }
        } else if (orderCount > 0) {
            if (daysSinceLastOrder < 30) {
                return "NEW_ACTIVE";
            } else if (daysSinceLastOrder < 90) {
                return "NEW_DORMANT";
            } else {
                return "ONE_TIME";
            }
        } else {
            return "PROSPECT";
        }
    }

    public double calculateChurnProbability(Map<String, Object> customer) {
        double prob = 0.5;
        
        int daysSinceLastOrder = (int) customer.getOrDefault("daysSinceLastOrder", 0);
        int orderCount = (int) customer.getOrDefault("orderCount", 0);
        double avgOrderValue = (double) customer.getOrDefault("avgOrderValue", 0.0);
        int complaintCount = (int) customer.getOrDefault("complaintCount", 0);
        double satisfactionScore = (double) customer.getOrDefault("satisfactionScore", 3.0);
        
        if (daysSinceLastOrder > 180) {
            prob += 0.3;
        } else if (daysSinceLastOrder > 90) {
            prob += 0.15;
        } else if (daysSinceLastOrder > 60) {
            prob += 0.05;
        } else if (daysSinceLastOrder < 30) {
            prob -= 0.2;
        }
        
        if (orderCount < 2) {
            prob += 0.2;
        } else if (orderCount < 5) {
            prob += 0.1;
        } else if (orderCount > 20) {
            prob -= 0.15;
        } else if (orderCount > 10) {
            prob -= 0.1;
        }
        
        if (avgOrderValue < 50) {
            prob += 0.1;
        } else if (avgOrderValue > 200) {
            prob -= 0.1;
        }
        
        if (complaintCount > 3) {
            prob += 0.25;
        } else if (complaintCount > 1) {
            prob += 0.1;
        }
        
        if (satisfactionScore < 2) {
            prob += 0.3;
        } else if (satisfactionScore < 3) {
            prob += 0.15;
        } else if (satisfactionScore > 4) {
            prob -= 0.2;
        }
        
        return Math.max(0, Math.min(1, prob));
    }

    public Map<String, Double> forecastRevenue(List<Double> historicalData, int periodsAhead) {
        Map<String, Double> forecast = new HashMap<>();
        
        if (historicalData.size() < 3) {
            for (int i = 1; i <= periodsAhead; i++) {
                forecast.put("period_" + i, 0.0);
            }
            return forecast;
        }
        
        double sum = 0;
        for (Double value : historicalData) {
            sum += value;
        }
        double avg = sum / historicalData.size();
        
        double growthSum = 0;
        int growthCount = 0;
        for (int i = 1; i < historicalData.size(); i++) {
            if (historicalData.get(i - 1) != 0) {
                double growth = (historicalData.get(i) - historicalData.get(i - 1)) / historicalData.get(i - 1);
                growthSum += growth;
                growthCount++;
            }
        }
        double avgGrowth = growthCount > 0 ? growthSum / growthCount : 0;
        
        double lastValue = historicalData.get(historicalData.size() - 1);
        for (int i = 1; i <= periodsAhead; i++) {
            double forecastValue = lastValue * Math.pow(1 + avgGrowth, i);
            forecastValue = forecastValue * 0.9 + avg * 0.1;
            forecast.put("period_" + i, Math.round(forecastValue * 100.0) / 100.0);
        }
        
        return forecast;
    }

    public List<Map<String, Object>> identifyAnomalies(List<Map<String, Object>> data, String metricField) {
        List<Map<String, Object>> anomalies = new ArrayList<>();
        
        List<Double> values = new ArrayList<>();
        for (Map<String, Object> item : data) {
            Object val = item.get(metricField);
            if (val != null) {
                values.add(((Number) val).doubleValue());
            }
        }
        
        if (values.size() < 10) {
            return anomalies;
        }
        
        double sum = 0;
        for (Double v : values) {
            sum += v;
        }
        double mean = sum / values.size();
        
        double sqDiffSum = 0;
        for (Double v : values) {
            sqDiffSum += Math.pow(v - mean, 2);
        }
        double stdDev = Math.sqrt(sqDiffSum / values.size());
        
        double threshold = 2.5;
        
        for (int i = 0; i < data.size(); i++) {
            Object val = data.get(i).get(metricField);
            if (val != null) {
                double value = ((Number) val).doubleValue();
                double zScore = (value - mean) / stdDev;
                
                if (Math.abs(zScore) > threshold) {
                    Map<String, Object> anomaly = new HashMap<>(data.get(i));
                    anomaly.put("zScore", zScore);
                    anomaly.put("isAnomaly", true);
                    anomalies.add(anomaly);
                }
            }
        }
        
        return anomalies;
    }
}

