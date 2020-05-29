package com.boubalos.mycalculator.viewmodels;

import android.app.Application;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.boubalos.mycalculator.Utils.Utils;

import org.mozilla.javascript.EcmaError;
import org.mozilla.javascript.EvaluatorException;

public class CalculatorViewModel extends ParentViewModel {
    private MutableLiveData<String> expression = new MutableLiveData<>();
    private MutableLiveData<String> history = new MutableLiveData<>();
    private char lastChar;
    private String text = "0";

    public CalculatorViewModel(@NonNull Application application) {
        super(application);
        refreshInput();
        expression.setValue(text);
    }

    public void setHistory(String history) {
        this.history.setValue(history);
    }

    public MutableLiveData<String> getHistory() {
        return history;
    }

    @Override
    public void setInput(char c) {
        if (Character.isDigit(c)) {
            if (text.equals("0")) text = "";
            if (lastChar == ')')
                text = text + "*";
            text = text + c;
            refreshInput();
        } else
            switch (c) {
                case '+':
                case '-':
                case 'x':
                case '/': {
                    if (text.equals("")) text = "0";
                    if (text.length() > 0)
                        if (lastChar == '.' || (!Character.isDigit(lastChar) && lastChar != ')' && lastChar != '('))
                            text = text.substring(0, text.length() - 1);
                    text = text + c;
                    refreshInput();
                    break;
                }
                case '(': {
                    if (text.length() > 0)
                        if (Character.isDigit(lastChar))
                            text = text + "x";
                    text = text + "(";
                    refreshInput();
                    break;
                }
                case ')': {
                    if (getOpenBrackets() > 0) {
                        text = text + ")";
                    }
                    refreshInput();
                    break;
                }
                case 'C': {
                    text = "0";
                    history.setValue("");
                    refreshInput();
                    break;
                }
                case 'b': {
                    if (text.length() > 0)
                        text = text.substring(0, text.length() - 1);
                    refreshInput();
                    break;
                }
                case '=': {
                    int count = getOpenBrackets();
                    for (int i = 0; i < count; i++) { //Close brackets
                        text = text + ")";
                    }
                    history.setValue(text + "=");
                    text=text.replace('x','*');
                    try{
                        text = Utils.Calculate(text) + "";
                    }catch (EvaluatorException | EcmaError e){
                        text="Error";
                    }
                    refreshInput();
                    break;
                }
                case '.': {
                    if (Utils.canPutPoint(text)) {
                        if (text.equals("") || !Character.isDigit(lastChar)) {
                            text = text + "0";
                        }
                        text = text + '.';
                    }
                    refreshInput();
                    break;
                }
            }
    }

    private void refreshInput() {
        if (text.equals("")) text = "0";
        expression.setValue(text);
            lastChar = text.charAt(text.length() - 1);
    }

    private int getOpenBrackets() {
        int brackets = 0;
        if (text.length() > 0)
            for (char c : text.toCharArray()) {
                if (!Character.isDigit(c))
                    if (c == '(')
                        brackets++;
                    else if (c == ')')
                        brackets--;
            }
        return brackets;
    }

    public MutableLiveData<String> getExpression() {
        return expression;
    }
}
