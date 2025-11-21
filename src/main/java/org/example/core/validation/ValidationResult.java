package org.example.core.validation;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;

import java.util.Optional;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public final class ValidationResult {

    private final boolean valid;
    private final String message;

    public static ValidationResult ok() {
        return new ValidationResult(true, null);
    }

    public static ValidationResult fail(@NonNull String message) {
        return new ValidationResult(false, message);
    }

    public Optional<String> getMessage() {
        return Optional.ofNullable(message);
    }
}