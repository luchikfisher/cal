package org.example.core.operators.factory;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.example.core.operators.base.Operator;
import org.example.core.operators.math.*;
import org.example.util.Constants;

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
    private static final Map<String, Operator> registry = new HashMap<>();

    static {
        // ===== DEFAULT OPERATORS =====
        addOperator(Constants.PLUS_OPERATOR, new AdditionOperator());
        addOperator(Constants.MINUS_OPERATOR, new SubtractionOperator());
        addOperator(Constants.MULTIPLY_OPERATOR, new MultiplicationOperator());
        addOperator(Constants.DIVIDE_OPERATOR, new DivisionOperator());
        addOperator(Constants.SIN_OPERATOR, new SinOperator());
        addOperator(Constants.COS_OPERATOR, new CosOperator());
    }

    // ===== PUBLIC API =====

    /**
     * Registers a new Operator at runtime.
     * If the symbol already exists, it will be replaced.
     */
    public static void addOperator(String symbol, Operator operator) {
        if (symbol == null || operator == null)
            throw new IllegalArgumentException("Symbol and operator must not be null");
        registry.put(symbol, operator);
    }

    /**
     * Returns an Operator by symbol, wrapped in Optional.
     */
    public static Optional<Operator> get(String symbol) {
        return Optional.ofNullable(registry.get(symbol));
    }

    /**
     * Returns an unmodifiable view of the full operator registry.
     */
    public static Map<String, Operator> getRegistry() {
        return Collections.unmodifiableMap(registry);
    }
}
