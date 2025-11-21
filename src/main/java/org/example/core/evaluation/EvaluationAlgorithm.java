package org.example.core.evaluation;

import java.util.Deque;

public interface EvaluationAlgorithm {
    double execute(TokenIterator tokens, Deque<Double> stack);
}
