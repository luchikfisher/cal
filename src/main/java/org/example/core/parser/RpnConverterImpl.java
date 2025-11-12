package org.example.core.parser;

import lombok.RequiredArgsConstructor;
import org.example.core.operators.base.Operator;
import org.example.core.operators.factory.OperatorFactory;
import org.example.util.Constants;
import org.example.core.validation.Validator;

import java.util.*;

/**RpnExpressionParser

 * RpnConverter — converts token sequence to Reverse Polish Notation.
 */
@RequiredArgsConstructor
public class RpnConverterImpl implements RpnConverter {

    private final Validator validator;

    @Override
    public List<String> toRpn(List<String> tokens) {
        if (tokens == null || tokens.isEmpty()) return List.of();

        List<String> output = new ArrayList<>();
        Deque<String> stack = new ArrayDeque<>();

        for (String token : tokens) processToken(token, output, stack);

        flushOperatorStack(stack, output);
        return output;
    }

    private void processToken(String token, List<String> out, Deque<String> stack) {
        if (validator.isValidNumber(token)) {
            out.add(token);
            return;
        }
        if (Constants.LEFT_PAREN.equals(token)) {
            stack.push(token);
            return;
        }
        if (Constants.RIGHT_PAREN.equals(token)) {
            flushParentheses(stack, out);
            return;
        }

        OperatorFactory.get(token)
                .ifPresent(op -> pushOperator(op, stack, out));
    }

    private void pushOperator(Operator op, Deque<String> stack, List<String> out) {
        while (!stack.isEmpty() && OperatorFactory.get(stack.peek()).isPresent()) {
            Operator o2 = OperatorFactory.get(stack.peek()).get();
            boolean left = op.isLeftAssociative() && op.getPrecedence() <= o2.getPrecedence();
            boolean right = !op.isLeftAssociative() && op.getPrecedence() < o2.getPrecedence();
            if (left || right) out.add(stack.pop());
            else break;
        }
        stack.push(op.getSymbol());
    }

    private void flushParentheses(Deque<String> stack, List<String> out) {
        while (!stack.isEmpty() && OperatorFactory.get(stack.peek()).isPresent())
            out.add(stack.pop());
        if (!stack.isEmpty() && Constants.LEFT_PAREN.equals(stack.peek())) stack.pop();
    }

    private void flushOperatorStack(Deque<String> stack, List<String> out) {
        while (!stack.isEmpty()) {
            out.add(stack.pop());
        }
    }
}