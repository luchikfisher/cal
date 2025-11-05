package org.example.util;

import lombok.experimental.UtilityClass;
import org.example.constants.Constants;

import java.util.Set;


@UtilityClass
public class Validator {

    private static final Set<String> ALLOWED_OPERATORS = Set.of(
            Constants.PLUS_OPERATOR,
            Constants.MINUS_OPERATOR,
            Constants.MULTIPLY_OPERATOR,
            Constants.DIVIDE_OPERATOR,
            Constants.SIN_OPERATOR,
            Constants.COS_OPERATOR
    );

    public boolean isValidNumber(String input) {
        if (isBlank(input)) return false;
        try { Double.parseDouble(input.trim()); return true; }
        catch (NumberFormatException e) { return false; }
    }


    public boolean isValidOperator(String input) {
        return !isBlank(input) && ALLOWED_OPERATORS.contains(input.trim());
    }

    private boolean isBlank(String s) {
        return s == null || s.isBlank();
    }
}
