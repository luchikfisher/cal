package org.example.core.operators;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.example.constants.Constants;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * OperatorFactory — central registry & singleton factory for all Operators.
 * Implements Factory + Singleton Pattern.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class OperatorFactory {

    // ===== SINGLETON INSTANCE =====
    private static final Map<String, Operator> REGISTRY = new HashMap<>();

    static {
        // ===== DEFAULT OPERATORS =====
        register(Constants.PLUS_OPERATOR, new AdditionOperator());
        register(Constants.MINUS_OPERATOR, new SubtractionOperator());
        register(Constants.MULTIPLY_OPERATOR, new MultiplicationOperator());
        register(Constants.DIVIDE_OPERATOR, new DivisionOperator());
        register(Constants.SIN_OPERATOR, new SinOperator());
        register(Constants.COS_OPERATOR, new CosOperator());
    }

    // ===== PUBLIC API =====

    /**
     * Registers a new Operator at runtime.
     * If the symbol already exists, it will be replaced.
     */
    public static void register(String symbol, Operator operator) {
        if (symbol == null || operator == null)
            throw new IllegalArgumentException("Symbol and operator must not be null");
        REGISTRY.put(symbol, operator);
    }

    /**
     * Returns an Operator by symbol, wrapped in Optional.
     */
    public static Optional<Operator> get(String symbol) {
        return Optional.ofNullable(REGISTRY.get(symbol));
    }

    /**
     * Returns an unmodifiable view of the full operator registry.
     */
    public static Map<String, Operator> getRegistry() {
        return Collections.unmodifiableMap(REGISTRY);
    }

    /**
     * Clears all registered operators (used mainly for testing).
     */
    public static void clear() {
        REGISTRY.clear();
    }
}
