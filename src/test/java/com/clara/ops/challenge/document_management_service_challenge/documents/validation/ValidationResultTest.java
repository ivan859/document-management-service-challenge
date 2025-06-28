package com.clara.ops.challenge.document_management_service_challenge.documents.validation;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ValidationResultTest {

    @Test
    void valid_shouldCreateValidResult() {
        ValidationResult result = ValidationResult.valid();
        
        assertTrue(result.isValid());
        assertTrue(result.getErrors().isEmpty());
        assertNull(result.getFirstError());
        assertEquals("", result.getAllErrorsAsString());
    }

    @Test
    void invalid_withSingleError_shouldCreateInvalidResult() {
        String error = "Test error";
        ValidationResult result = ValidationResult.invalid(error);
        
        assertFalse(result.isValid());
        assertEquals(1, result.getErrors().size());
        assertEquals(error, result.getFirstError());
        assertEquals(error, result.getAllErrorsAsString());
        assertTrue(result.getErrors().contains(error));
    }

    @Test
    void invalid_withMultipleErrors_shouldCreateInvalidResult() {
        List<String> errors = Arrays.asList("Error 1", "Error 2", "Error 3");
        ValidationResult result = ValidationResult.invalid(errors);
        
        assertFalse(result.isValid());
        assertEquals(3, result.getErrors().size());
        assertEquals("Error 1", result.getFirstError());
        assertEquals("Error 1; Error 2; Error 3", result.getAllErrorsAsString());
        assertEquals(errors, result.getErrors());
    }

    @Test
    void addError_shouldThrowException_whenResultIsValid() {
        ValidationResult result = ValidationResult.valid();
        
        IllegalStateException exception = assertThrows(
            IllegalStateException.class,
            () -> result.addError("New error")
        );
        
        assertEquals("Cannot add error to valid result", exception.getMessage());
    }

    @Test
    void addError_shouldAddError_whenResultIsInvalid() {
        ValidationResult result = ValidationResult.invalid("Initial error");
        ValidationResult updatedResult = result.addError("Second error");
        
        assertSame(result, updatedResult); // Should return same instance
        assertFalse(result.isValid());
        assertEquals(2, result.getErrors().size());
        assertEquals("Initial error", result.getFirstError());
        assertEquals("Initial error; Second error", result.getAllErrorsAsString());
    }

    @Test
    void combine_bothValid_shouldReturnValid() {
        ValidationResult result1 = ValidationResult.valid();
        ValidationResult result2 = ValidationResult.valid();
        
        ValidationResult combined = result1.combine(result2);
        
        assertTrue(combined.isValid());
        assertTrue(combined.getErrors().isEmpty());
    }

    @Test
    void combine_oneInvalid_shouldReturnInvalid() {
        ValidationResult valid = ValidationResult.valid();
        ValidationResult invalid = ValidationResult.invalid("Error");
        
        ValidationResult combined1 = valid.combine(invalid);
        ValidationResult combined2 = invalid.combine(valid);
        
        assertFalse(combined1.isValid());
        assertEquals(1, combined1.getErrors().size());
        assertEquals("Error", combined1.getFirstError());
        
        assertFalse(combined2.isValid());
        assertEquals(1, combined2.getErrors().size());
        assertEquals("Error", combined2.getFirstError());
    }

    @Test
    void combine_bothInvalid_shouldCombineErrors() {
        ValidationResult result1 = ValidationResult.invalid("Error 1");
        ValidationResult result2 = ValidationResult.invalid(Arrays.asList("Error 2", "Error 3"));
        
        ValidationResult combined = result1.combine(result2);
        
        assertFalse(combined.isValid());
        assertEquals(3, combined.getErrors().size());
        assertEquals("Error 1", combined.getFirstError());
        assertEquals("Error 1; Error 2; Error 3", combined.getAllErrorsAsString());
        assertEquals(Arrays.asList("Error 1", "Error 2", "Error 3"), combined.getErrors());
    }

    @Test
    void getFirstError_shouldReturnNull_whenNoErrors() {
        ValidationResult result = ValidationResult.valid();
        assertNull(result.getFirstError());
    }

    @Test
    void getAllErrorsAsString_shouldReturnEmptyString_whenNoErrors() {
        ValidationResult result = ValidationResult.valid();
        assertEquals("", result.getAllErrorsAsString());
    }

    @Test
    void getAllErrorsAsString_shouldJoinWithSemicolon() {
        ValidationResult result = ValidationResult.invalid(Arrays.asList("Error 1", "Error 2"));
        assertEquals("Error 1; Error 2", result.getAllErrorsAsString());
    }
}