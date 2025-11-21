package org.example.core.operators.factory;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.example.config.ConfigurationManager;
import org.example.core.operators.base.Operator;
import org.example.core.operators.impl.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class OperatorFactory {

    private static final Map<String, Operator> registry = new HashMap<>();

    static {
        Map<String, Operator> defaults = Map.ofEntries(
                Map.entry("operator.plus", new AdditionOperator()),
                Map.entry("operator.minus", new SubtractionOperator()),
                Map.entry("operator.multiply", new MultiplicationOperator()),
                Map.entry("operator.divide", new DivisionOperator()),
                Map.entry("operator.sin", new SinOperator()),
                Map.entry("operator.cos", new CosOperator()),
                Map.entry("operator.unary.minus", new UnaryMinusOperator())
        );

        defaults.forEach((configKey, operatorInstance) -> {
            String symbol = ConfigurationManager.getOrThrow(configKey);
            if (symbol == null || symbol.isBlank()) {
                throw new IllegalStateException("Missing operator symbol for key: " + configKey);
            }
            addOperator(symbol, operatorInstance);
        });
    }

    public static void addOperator(String symbol, Operator operator) {
        if (symbol == null || operator == null)
            throw new IllegalArgumentException("Symbol and operator must not be null");
        registry.put(symbol, operator);
    }

    public static Optional<Operator> get(String symbol) {
        return Optional.ofNullable(registry.get(symbol));
    }

    public static Map<String, Operator> getRegistry() {
        return Collections.unmodifiableMap(registry);
    }
}
