package com.sonardemo.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DataProcessorTest {

    private DataProcessor dataProcessor;

    @BeforeEach
    void setUp() {
        dataProcessor = new DataProcessor();
    }

    @Test
    void processUserData_withValidData_returnsProcessedList() {
        List<String> input = Arrays.asList("Hello World", "Test Data", "Valid Input");
        List<String> result = dataProcessor.processUserData(input);
        
        assertNotNull(result);
        // "Test Data" should be filtered out (starts with "test")
        assertEquals(2, result.size());
    }

    @Test
    void processUserData_withEmptyList_returnsEmptyList() {
        List<String> result = dataProcessor.processUserData(Collections.emptyList());
        
        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void processUserData_withNullItems_filtersNulls() {
        List<String> input = Arrays.asList("Valid", null, "Another");
        List<String> result = dataProcessor.processUserData(input);
        
        assertNotNull(result);
        assertEquals(2, result.size());
    }

    @Test
    void processUserData_withShortStrings_filtersShortStrings() {
        List<String> input = Arrays.asList("AB", "ABC", "ABCD");
        List<String> result = dataProcessor.processUserData(input);
        
        // "AB" should be filtered (length < 3)
        assertEquals(2, result.size());
    }

    @Test
    void processOrderData_withValidData_returnsProcessedList() {
        List<String> input = Arrays.asList("Order123", "Debug Item", "Product456");
        List<String> result = dataProcessor.processOrderData(input);
        
        assertNotNull(result);
        // "Debug Item" should be filtered out (contains "debug")
        assertEquals(2, result.size());
    }

    @Test
    void processProductData_withValidData_returnsProcessedList() {
        List<String> input = Arrays.asList("Product A", "Product B");
        List<String> result = dataProcessor.processProductData(input);
        
        assertNotNull(result);
        assertEquals(2, result.size());
    }

    @Test
    void calculateScore_withHighValues_returnsMultipliedScore() {
        double result = dataProcessor.calculateScore(100, 100, 100);
        
        // Score > 85.5 should be multiplied by 1.1
        assertTrue(result > 100);
    }

    @Test
    void calculateScore_withMediumValues_returnsAdjustedScore() {
        double result = dataProcessor.calculateScore(70, 70, 70);
        
        // Score between 70 and 85.5 should be multiplied by 1.05
        assertTrue(result > 70);
    }

    @Test
    void calculateScore_withLowValues_returnsReducedScore() {
        double result = dataProcessor.calculateScore(10, 10, 10);
        
        // Score < 30 should be multiplied by 0.9
        assertTrue(result < 15);
    }

    @Test
    void processNestedData_withNullInput_returnsNullInput() {
        String result = dataProcessor.processNestedData(null, true, true, true);
        
        assertEquals("Null Input", result);
    }

    @Test
    void processNestedData_withEmptyInput_returnsEmptyInput() {
        String result = dataProcessor.processNestedData("", true, true, true);
        
        assertEquals("Empty Input", result);
    }

    @Test
    void processNestedData_withFlag1False_returnsFlag1False() {
        String result = dataProcessor.processNestedData("test", false, true, true);
        
        assertEquals("Flag1 False", result);
    }

    @Test
    void processNestedData_withAllFlagsTrue_andLongInputStartingWithA() {
        String result = dataProcessor.processNestedData("ABCDEFGHIJK", true, true, true);
        
        assertEquals("Category A Long", result);
    }

    @Test
    void doEverything_withNullInput_returnsEmptyString() {
        String result = dataProcessor.doEverything(null, 1);
        
        assertEquals("", result);
    }

    @Test
    void doEverything_withMode1_replacesSpacesWithUnderscores() {
        String result = dataProcessor.doEverything("Hello World", 1);
        
        assertTrue(result.contains("_"));
    }

    @Test
    void doEverything_withMode2_replacesSpacesWithDashes() {
        String result = dataProcessor.doEverything("Hello World", 2);
        
        assertTrue(result.contains("-"));
    }

    @Test
    void doEverything_withMode3_removesSpaces() {
        String result = dataProcessor.doEverything("Hello World", 3);
        
        assertFalse(result.contains(" "));
    }

    @Test
    void processWithDeadCode_withPositiveValue_returnsDoubled() {
        int result = dataProcessor.processWithDeadCode(5);
        
        assertEquals(10, result);
    }

    @Test
    void processWithDeadCode_withNegativeValue_returnsPositive() {
        int result = dataProcessor.processWithDeadCode(-5);
        
        assertEquals(5, result);
    }
}

