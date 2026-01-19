package com.sonardemo.service;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class DataProcessor {

    // MAINTAINABILITY: Magic numbers used throughout
    private static final int MAGIC_VALUE = 42;

    /**
     * CODE DUPLICATION: This method has duplicated logic with processOrderData
     */
    public List<String> processUserData(List<String> data) {
        List<String> result = new ArrayList<>();
        
        for (String item : data) {
            // Duplicated validation logic
            if (item == null || item.isEmpty()) {
                continue;
            }
            if (item.length() < 3) {
                continue;
            }
            if (item.length() > 100) {
                continue;
            }
            
            // Duplicated transformation logic
            String processed = item.trim();
            processed = processed.toLowerCase();
            processed = processed.replaceAll("[^a-z0-9]", "");
            
            // Duplicated filtering logic
            if (processed.startsWith("test")) {
                continue;
            }
            if (processed.contains("debug")) {
                continue;
            }
            
            result.add(processed);
        }
        
        return result;
    }

    /**
     * CODE DUPLICATION: This method has duplicated logic with processUserData
     */
    public List<String> processOrderData(List<String> data) {
        List<String> result = new ArrayList<>();
        
        for (String item : data) {
            // Duplicated validation logic (same as processUserData)
            if (item == null || item.isEmpty()) {
                continue;
            }
            if (item.length() < 3) {
                continue;
            }
            if (item.length() > 100) {
                continue;
            }
            
            // Duplicated transformation logic (same as processUserData)
            String processed = item.trim();
            processed = processed.toLowerCase();
            processed = processed.replaceAll("[^a-z0-9]", "");
            
            // Duplicated filtering logic (same as processUserData)
            if (processed.startsWith("test")) {
                continue;
            }
            if (processed.contains("debug")) {
                continue;
            }
            
            result.add(processed);
        }
        
        return result;
    }

    /**
     * CODE DUPLICATION: Third copy of the same logic
     */
    public List<String> processProductData(List<String> data) {
        List<String> result = new ArrayList<>();
        
        for (String item : data) {
            // Duplicated validation logic
            if (item == null || item.isEmpty()) {
                continue;
            }
            if (item.length() < 3) {
                continue;
            }
            if (item.length() > 100) {
                continue;
            }
            
            // Duplicated transformation logic
            String processed = item.trim();
            processed = processed.toLowerCase();
            processed = processed.replaceAll("[^a-z0-9]", "");
            
            // Duplicated filtering logic
            if (processed.startsWith("test")) {
                continue;
            }
            if (processed.contains("debug")) {
                continue;
            }
            
            result.add(processed);
        }
        
        return result;
    }

    /**
     * MAINTAINABILITY: Magic numbers and poor readability
     */
    public double calculateScore(int value1, int value2, int value3) {
        // MAINTAINABILITY: Magic numbers without explanation
        double result = value1 * 0.35 + value2 * 0.45 + value3 * 0.20;
        
        if (result > 85.5) {
            return result * 1.1;
        } else if (result > 70.0) {
            return result * 1.05;
        } else if (result < 30.0) {
            return result * 0.9;
        }
        
        return result + 5.5;
    }

    /**
     * MAINTAINABILITY: Deeply nested code
     */
    public String processNestedData(String input, boolean flag1, boolean flag2, boolean flag3) {
        String result = "";
        
        if (input != null) {
            if (!input.isEmpty()) {
                if (flag1) {
                    if (flag2) {
                        if (flag3) {
                            if (input.length() > 10) {
                                if (input.startsWith("A")) {
                                    result = "Category A Long";
                                } else {
                                    result = "Category Other Long";
                                }
                            } else {
                                result = "Category Short";
                            }
                        } else {
                            result = "Flag3 False";
                        }
                    } else {
                        result = "Flag2 False";
                    }
                } else {
                    result = "Flag1 False";
                }
            } else {
                result = "Empty Input";
            }
        } else {
            result = "Null Input";
        }
        
        return result;
    }

    /**
     * RELIABILITY: Unused private method (dead code)
     */
    private String unusedHelperMethod(String input) {
        return input.toUpperCase();
    }

    /**
     * RELIABILITY: Unused variable and unreachable code
     */
    public int processWithDeadCode(int value) {
        int unusedVariable = 100;  // MAINTAINABILITY: Unused local variable
        
        if (value > 0) {
            return value * 2;
        } else {
            return value * -1;
        }
        
        // RELIABILITY: Unreachable code (but Java compiler catches this, so commenting out)
        // System.out.println("This will never execute");
    }

    /**
     * MAINTAINABILITY: Method does too many things (low cohesion)
     */
    public String doEverything(String input, int mode) {
        // Validation
        if (input == null) return "";
        
        // Transformation
        String result = input.trim().toLowerCase();
        
        // Mode-specific processing
        switch (mode) {
            case 1:
                result = result.replaceAll(" ", "_");
                break;
            case 2:
                result = result.replaceAll(" ", "-");
                break;
            case 3:
                result = result.replaceAll(" ", "");
                break;
            case 4:  // MAINTAINABILITY: Missing break statement is intentional code smell
                result = result.toUpperCase();
            case 5:
                result = "prefix_" + result;
                break;
            default:
                break;
        }
        
        // Logging (mixed concerns)
        System.out.println("Processed: " + result);
        
        // Additional transformation
        if (result.length() > 50) {
            result = result.substring(0, 50);
        }
        
        return result;
    }
}

