package org.example.core.operators.factory;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.example.config.ConfigurationManager;
import org.example.core.operators.base.BinaryOperator;
import org.example.core.operators.base.UnaryOperator;
import org.example.core.operators.base.Operator;
import org.example.core.operators.impl.*;

import java.util.*;
import java.util.stream.Collectors;

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
                Map.entry("operator.unary.minus", new UnaryMinusOperator()),
                Map.entry("operator.unary.plus", new UnaryPlusOperator())
        );

        defaults.forEach((configKey, operatorInstance) -> {
            String symbol = ConfigurationManager.getOrThrow(configKey);
            if (Objects.isNull(symbol) || symbol.isBlank()) {
                throw new IllegalStateException(
                        "Missing operator symbol for key: " + configKey
                );
            }
            addOperator(symbol, operatorInstance);
        });
    }

    public static void addOperator(String symbol, Operator operator) {
        if (Objects.isNull(symbol) || Objects.isNull(operator)) {
            throw new IllegalArgumentException("Symbol and operator must not be null");
        }
        Operator existing = registry.putIfAbsent(symbol, operator);
        if (Objects.nonNull(existing)) {
            throw new IllegalStateException(
                    "Operator already registered for symbol: " + symbol + " ("
                            + existing.getClass().getSimpleName() + ")"
            );
        }
    }

    public static Set<String> getBinaryOperators() {
        return registry.entrySet().stream()
                .filter(e -> e.getValue() instanceof BinaryOperator)
                .map(Map.Entry::getKey)
                .collect(Collectors.toUnmodifiableSet());
    }

    public static Set<String> getUnaryOperators() {
        return registry.entrySet().stream()
                .filter(e -> e.getValue() instanceof UnaryOperator)
                .map(Map.Entry::getKey)
                .collect(Collectors.toUnmodifiableSet());
    }

    public static Set<String> getFunctionNames() {
        return registry.entrySet().stream()
                .filter(e -> e.getValue().isFunction())
                .map(Map.Entry::getKey)
                .collect(Collectors.toUnmodifiableSet());
    }

    public static Optional<Operator> get(String symbol) {
        return Optional.ofNullable(registry.get(symbol));
    }

    public static Map<String, Operator> getRegistry() {
        return Collections.unmodifiableMap(registry);
    }
}