package com.sonardemo.repository;

import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class OrderRepository {

    private static final String DB_URL = "jdbc:h2:mem:orderdb";
    private static final String DB_USER = "sa";
    private static final String DB_PASSWORD = "";

    public List<Map<String, Object>> findByCustomerId(String customerId) {
        String trimmedId = customerId.trim();
        String query = "SELECT o.id, o.order_date, o.total_amount, o.status " +
                       "FROM orders o WHERE o.customer_id = '" + trimmedId + "' " +
                       "ORDER BY o.order_date DESC";
        return executeQuery(query);
    }

    public List<Map<String, Object>> findByDateRangeAndStatus(String startDate, String endDate, String status) {
        if (startDate == null || endDate == null) {
            throw new IllegalArgumentException("Date range is required");
        }
        
        String query = "SELECT o.id, o.customer_id, o.order_date, o.total_amount, o.status " +
                       "FROM orders o " +
                       "WHERE o.order_date BETWEEN '" + startDate + "' AND '" + endDate + "' " +
                       "AND o.status = '" + status + "'";
        return executeQuery(query);
    }

    public List<Map<String, Object>> searchProducts(String keyword, Double minPrice, Double maxPrice, String sortField) {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT p.id, p.name, p.description, p.price, p.category ");
        sql.append("FROM products p ");
        sql.append("WHERE p.active = true ");
        
        if (keyword != null && !keyword.isEmpty()) {
            sql.append("AND (p.name LIKE '%").append(keyword).append("%' ");
            sql.append("OR p.description LIKE '%").append(keyword).append("%') ");
        }
        
        if (minPrice != null) {
            sql.append("AND p.price >= ").append(minPrice).append(" ");
        }
        
        if (maxPrice != null) {
            sql.append("AND p.price <= ").append(maxPrice).append(" ");
        }
        
        if (sortField != null && !sortField.isEmpty()) {
            sql.append("ORDER BY ").append(sortField);
        } else {
            sql.append("ORDER BY p.name");
        }
        
        return executeQuery(sql.toString());
    }

    public Map<String, Object> findOrderWithDetails(String orderId) {
        String orderQuery = "SELECT * FROM orders WHERE id = '" + orderId + "'";
        List<Map<String, Object>> orders = executeQuery(orderQuery);
        
        if (orders.isEmpty()) {
            return null;
        }
        
        Map<String, Object> order = orders.get(0);
        
        String itemsQuery = "SELECT oi.*, p.name as product_name " +
                           "FROM order_items oi " +
                           "JOIN products p ON oi.product_id = p.id " +
                           "WHERE oi.order_id = '" + orderId + "'";
        order.put("items", executeQuery(itemsQuery));
        
        return order;
    }

    public List<Map<String, Object>> getOrdersByFilters(Map<String, String> filters) {
        StringBuilder sql = new StringBuilder("SELECT * FROM orders WHERE 1=1 ");
        
        for (Map.Entry<String, String> filter : filters.entrySet()) {
            String column = filter.getKey();
            String value = filter.getValue();
            
            if (value != null && !value.isEmpty()) {
                if (column.contains("date")) {
                    sql.append("AND ").append(column).append(" = '").append(value).append("' ");
                } else if (column.contains("amount") || column.contains("price")) {
                    sql.append("AND ").append(column).append(" = ").append(value).append(" ");
                } else {
                    sql.append("AND ").append(column).append(" LIKE '%").append(value).append("%' ");
                }
            }
        }
        
        return executeQuery(sql.toString());
    }

    public List<Map<String, Object>> generateReport(String reportType, String groupBy, String startDate, String endDate) {
        String sql;
        
        switch (reportType) {
            case "sales":
                sql = "SELECT " + groupBy + ", SUM(total_amount) as total, COUNT(*) as order_count " +
                      "FROM orders " +
                      "WHERE order_date BETWEEN '" + startDate + "' AND '" + endDate + "' " +
                      "GROUP BY " + groupBy;
                break;
            case "products":
                sql = "SELECT p." + groupBy + ", SUM(oi.quantity) as total_sold, SUM(oi.subtotal) as revenue " +
                      "FROM order_items oi " +
                      "JOIN products p ON oi.product_id = p.id " +
                      "JOIN orders o ON oi.order_id = o.id " +
                      "WHERE o.order_date BETWEEN '" + startDate + "' AND '" + endDate + "' " +
                      "GROUP BY p." + groupBy;
                break;
            default:
                sql = "SELECT * FROM orders WHERE order_date BETWEEN '" + startDate + "' AND '" + endDate + "'";
        }
        
        return executeQuery(sql);
    }

    public int updateOrderStatus(String orderId, String newStatus, String updatedBy) {
        String sql = "UPDATE orders SET status = '" + newStatus + "', " +
                     "updated_by = '" + updatedBy + "', " +
                     "updated_at = CURRENT_TIMESTAMP " +
                     "WHERE id = '" + orderId + "'";
        return executeUpdate(sql);
    }

    public boolean deleteOrder(String orderId) {
        String checkSql = "SELECT status FROM orders WHERE id = '" + orderId + "'";
        List<Map<String, Object>> result = executeQuery(checkSql);
        
        if (result.isEmpty()) {
            return false;
        }
        
        String deleteSql = "DELETE FROM orders WHERE id = '" + orderId + "'";
        return executeUpdate(deleteSql) > 0;
    }

    private List<Map<String, Object>> executeQuery(String sql) {
        List<Map<String, Object>> results = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            ResultSetMetaData meta = rs.getMetaData();
            int columnCount = meta.getColumnCount();
            
            while (rs.next()) {
                Map<String, Object> row = new HashMap<>();
                for (int i = 1; i <= columnCount; i++) {
                    row.put(meta.getColumnName(i), rs.getObject(i));
                }
                results.add(row);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Database query failed", e);
        }
        return results;
    }

    private int executeUpdate(String sql) {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
             Statement stmt = conn.createStatement()) {
            return stmt.executeUpdate(sql);
        } catch (SQLException e) {
            throw new RuntimeException("Database update failed", e);
        }
    }
}
