package com.boubalos.mycalculator.Utils;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.EcmaError;
import org.mozilla.javascript.EvaluatorException;
import org.mozilla.javascript.Scriptable;

import java.util.HashMap;

public class Utils {


    public static double Calculate(String expression) {
        Context context = Context.enter();
        context.setOptimizationLevel(-1);
        Scriptable scope = context.initStandardObjects();
        try {
            Object result = context.evaluateString(scope, expression, "<cmd>", 1, null);
            return (double) result;
        } catch (EvaluatorException | EcmaError e) {
          throw e;
        }
    }


    public static double ConvertCurrency(double amount, HashMap<String, Double> rates, String from, String to) throws NullPointerException {
        return (amount / rates.get(from)) * rates.get(to);
    }

    public static boolean canPutPoint(String text) {

        boolean result = true;
        for (char c : text.toCharArray()) {
            if (!Character.isDigit(c))
                if (c == '.')
                    result = false;
                else result = true;
        }
        return result;
    }


}
