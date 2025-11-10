package org.example.core.strategy;

import java.util.Deque;
import java.util.List;

/**
 * Strategy interface for defining different calculation algorithms.
 */
public interface CalculationStrategy {
    double compute(List<String> rpn, Deque<Double> stack);
}
