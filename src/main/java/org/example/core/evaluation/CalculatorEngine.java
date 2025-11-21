package org.example.core.evaluation;

import lombok.RequiredArgsConstructor;
import org.example.core.parser.RpnParser;
import org.example.core.parser.ExpressionParser;

import java.util.*;

@RequiredArgsConstructor
public class CalculatorEngine implements EvaluationEngine {

    private final ExpressionParser expressionParser;
    private final RpnParser rpnConverter;
    private final EvaluationAlgorithm evaluationAlgorithm;

    @Override
    public double evaluate(String input) {

        List<String> tokens = expressionParser.parse(input);

        List<String> rpnTokens = rpnConverter.toRpn(tokens);

        return compute(rpnTokens);
    }


    private double compute(List<String> rpnTokens) {
        Deque<Double> stack = new ArrayDeque<>();
        return evaluationAlgorithm.execute(new TokenIterator(rpnTokens), stack);
    }
}