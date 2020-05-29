package com.boubalos.mycalculator.Utils;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.EcmaError;
import org.mozilla.javascript.EvaluatorException;
import org.mozilla.javascript.Scriptable;

import java.util.HashMap;

public class Utils {


    public static double Calculate(String expression) {
        Context context = Context.enter(); //
        context.setOptimizationLevel(-1); // this is required[2]
        Scriptable scope = context.initStandardObjects();
        try {
            Object result = context.evaluateString(scope, expression, "<cmd>", 1, null);
            return (double) result;
        } catch (EvaluatorException | EcmaError e) {
            e.printStackTrace();
        }
        return 0;
    }


    public static double ConvertCurrency(double amount, HashMap<String, Double> rates, String from, String to) throws NullPointerException {
        return (amount / rates.get(from)) * rates.get(to);
    }


}
