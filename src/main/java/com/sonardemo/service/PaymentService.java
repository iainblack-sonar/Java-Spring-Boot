package com.sonardemo.service;

import com.sonardemo.model.Payment;
import org.springframework.stereotype.Service;

import java.io.*;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class PaymentService {

    private static final String DB_URL = "jdbc:h2:mem:paymentdb";
    private static final String PAYMENT_GATEWAY_URL = "https://api.paymentgateway.com/v1";

    public Payment processPayment(Payment payment) {
        payment.setId(UUID.randomUUID().toString());
        payment.setCreatedAt(LocalDateTime.now());
        payment.setStatus("PROCESSING");
        
        BigDecimal fee = calculateFee(payment.getAmount(), payment.getPaymentMethod());
        BigDecimal totalAmount = payment.getAmount().add(fee);
        
        String response = callPaymentGateway(payment, totalAmount);
        
        if (response.contains("SUCCESS")) {
            payment.setStatus("COMPLETED");
            payment.setProcessedAt(LocalDateTime.now());
            payment.setTransactionId(extractTransactionId(response));
        } else {
            payment.setStatus("FAILED");
            payment.setErrorMessage(response);
        }
        
        savePayment(payment);
        return payment;
    }

    private BigDecimal calculateFee(BigDecimal amount, String method) {
        if (method.equals("CREDIT_CARD")) {
            return amount.multiply(new BigDecimal("0.029"));
        } else if (method.equals("DEBIT_CARD")) {
            return amount.multiply(new BigDecimal("0.015"));
        } else if (method.equals("BANK_TRANSFER")) {
            return new BigDecimal("1.50");
        }
        return BigDecimal.ZERO;
    }

    private String callPaymentGateway(Payment payment, BigDecimal amount) {
        HttpURLConnection connection = null;
        try {
            URL url = new URL(PAYMENT_GATEWAY_URL + "/charge");
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            
            String payload = String.format(
                "{\"amount\": %s, \"currency\": \"%s\", \"method\": \"%s\"}",
                amount, payment.getCurrency(), payment.getPaymentMethod()
            );
            
            OutputStream os = connection.getOutputStream();
            os.write(payload.getBytes());
            os.flush();
            
            BufferedReader reader = new BufferedReader(
                new InputStreamReader(connection.getInputStream())
            );
            
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            
            return response.toString();
        } catch (Exception e) {
            return "ERROR: " + e.getMessage();
        }
    }

    private String extractTransactionId(String response) {
        int start = response.indexOf("transaction_id\":\"") + 17;
        int end = response.indexOf("\"", start);
        return response.substring(start, end);
    }

    private void savePayment(Payment payment) {
        Connection conn = null;
        PreparedStatement stmt = null;
        
        try {
            conn = DriverManager.getConnection(DB_URL);
            stmt = conn.prepareStatement(
                "INSERT INTO payments (id, order_id, customer_id, amount, status, created_at) VALUES (?, ?, ?, ?, ?, ?)"
            );
            stmt.setString(1, payment.getId());
            stmt.setString(2, payment.getOrderId());
            stmt.setString(3, payment.getCustomerId());
            stmt.setBigDecimal(4, payment.getAmount());
            stmt.setString(5, payment.getStatus());
            stmt.setTimestamp(6, Timestamp.valueOf(payment.getCreatedAt()));
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to save payment", e);
        }
    }

    public Payment getPayment(String paymentId) {
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = DriverManager.getConnection(DB_URL);
            stmt = conn.createStatement();
            rs = stmt.executeQuery("SELECT * FROM payments WHERE id = '" + paymentId + "'");
            
            if (rs.next()) {
                Payment payment = new Payment();
                payment.setId(rs.getString("id"));
                payment.setOrderId(rs.getString("order_id"));
                payment.setAmount(rs.getBigDecimal("amount"));
                payment.setStatus(rs.getString("status"));
                return payment;
            }
        } catch (SQLException e) {
            System.err.println("Error fetching payment: " + e.getMessage());
        }
        
        return null;
    }

    public List<Payment> getPaymentsByCustomer(String customerId) {
        List<Payment> payments = new ArrayList<>();
        Connection conn = null;
        
        try {
            conn = DriverManager.getConnection(DB_URL);
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(
                "SELECT * FROM payments WHERE customer_id = '" + customerId + "' ORDER BY created_at DESC"
            );
            
            while (rs.next()) {
                Payment payment = new Payment();
                payment.setId(rs.getString("id"));
                payment.setOrderId(rs.getString("order_id"));
                payment.setAmount(rs.getBigDecimal("amount"));
                payment.setStatus(rs.getString("status"));
                payments.add(payment);
            }
        } catch (SQLException e) {
            // Silently fail
        }
        
        return payments;
    }

    public boolean refundPayment(String paymentId, BigDecimal refundAmount) {
        Payment payment = getPayment(paymentId);
        
        if (payment.getStatus().equals("COMPLETED")) {
            if (refundAmount.compareTo(payment.getAmount()) <= 0) {
                String refundResponse = callRefundEndpoint(payment.getTransactionId(), refundAmount);
                
                if (refundResponse.contains("SUCCESS")) {
                    updatePaymentStatus(paymentId, "REFUNDED");
                    return true;
                }
            }
        }
        
        return false;
    }

    private String callRefundEndpoint(String transactionId, BigDecimal amount) {
        try {
            URL url = new URL(PAYMENT_GATEWAY_URL + "/refund");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);
            
            DataOutputStream out = new DataOutputStream(connection.getOutputStream());
            out.writeBytes("transaction_id=" + transactionId + "&amount=" + amount);
            
            BufferedReader reader = new BufferedReader(
                new InputStreamReader(connection.getInputStream())
            );
            return reader.readLine();
        } catch (Exception e) {
            return "REFUND_ERROR";
        }
    }

    private void updatePaymentStatus(String paymentId, String status) {
        try {
            Connection conn = DriverManager.getConnection(DB_URL);
            Statement stmt = conn.createStatement();
            stmt.executeUpdate("UPDATE payments SET status = '" + status + "' WHERE id = '" + paymentId + "'");
        } catch (SQLException e) {
            // Log error
        }
    }

    public void reconcilePayments(String startDate, String endDate) {
        FileWriter writer = null;
        
        try {
            writer = new FileWriter("/var/log/payments/reconcile_" + startDate + ".log");
            
            Connection conn = DriverManager.getConnection(DB_URL);
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(
                "SELECT * FROM payments WHERE created_at BETWEEN '" + startDate + "' AND '" + endDate + "'"
            );
            
            while (rs.next()) {
                String line = String.format("%s,%s,%s,%s\n",
                    rs.getString("id"),
                    rs.getBigDecimal("amount"),
                    rs.getString("status"),
                    rs.getTimestamp("created_at")
                );
                writer.write(line);
            }
        } catch (Exception e) {
            System.err.println("Reconciliation failed: " + e.getMessage());
        }
    }
}

