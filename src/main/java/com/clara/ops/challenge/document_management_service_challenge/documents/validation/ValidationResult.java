package com.clara.ops.challenge.document_management_service_challenge.documents.validation;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ValidationResult {
    
    private final boolean valid;
    private final List<String> errors;

    public static ValidationResult valid() {
        return new ValidationResult(true, new ArrayList<>());
    }

    public static ValidationResult invalid(String error) {
        List<String> errors = new ArrayList<>();
        errors.add(error);
        return new ValidationResult(false, errors);
    }

    public static ValidationResult invalid(List<String> errors) {
        return new ValidationResult(false, new ArrayList<>(errors));
    }

    public ValidationResult addError(String error) {
        if (valid) {
            throw new IllegalStateException("Cannot add error to valid result");
        }
        errors.add(error);
        return this;
    }

    public String getFirstError() {
        return errors.isEmpty() ? null : errors.get(0);
    }

    public String getAllErrorsAsString() {
        return String.join("; ", errors);
    }

    public ValidationResult combine(ValidationResult other) {
        if (this.valid && other.valid) {
            return valid();
        }
        
        List<String> combinedErrors = new ArrayList<>(this.errors);
        combinedErrors.addAll(other.errors);
        return invalid(combinedErrors);
    }
}