package com.boubalos.mycalculator.views.bindings;

import android.view.View;
import android.widget.TextView;

import androidx.dynamicanimation.animation.DynamicAnimation;
import androidx.dynamicanimation.animation.FloatPropertyCompat;
import androidx.dynamicanimation.animation.SpringAnimation;
import androidx.dynamicanimation.animation.SpringForce;
import androidx.lifecycle.MutableLiveData;

import com.boubalos.mycalculator.Utils.Utils;
import com.boubalos.mycalculator.viewmodels.CalculatorViewModel;
import com.boubalos.mycalculator.viewmodels.ParentViewModel;

public class MyNumpad {
    private String input = "";
    private String history = "";
    private char lastChar;
    private ParentViewModel activeviewModel;

    private MutableLiveData<String> inputData = new MutableLiveData<>();
    private MutableLiveData<String> historyData = new MutableLiveData<>();
    private MutableLiveData<Integer> type = new MutableLiveData<>();
    private MutableLiveData<Boolean> onlyNums = new MutableLiveData<>();

    public void NumberClicked(View v) {
        if (input.equals("0")) input = "";
        if (lastChar == ')')
            input = input + "*";
        playAnim(v);
        input = input + ((TextView) v).getText();
        refreshInput();
    }

    public void PointClicked(View v) {
        playAnim(v);
        if (canPutPoint()) {
            if (input.equals("") || !Character.isDigit(lastChar)) {
                input = input + "0";
            }
            input = input + '.';
        }
        refreshInput();
    }

    private boolean canPutPoint() {
        boolean result = true;
        for (char c : input.toCharArray()) {
            if (!Character.isDigit(c))
                if (c == '.')
                    result = false;
                else result = true;
        }
        return result;
    }

    private int getOpenBrackets() {
        int brackets = 0;
        if (input.length() > 0)
            for (char c : input.toCharArray()) {
                if (!Character.isDigit(c))
                    if (c == '(')
                        brackets++;
                    else if (c == ')')
                        brackets--;
            }
        return brackets;
    }

    public void ResultClicked(View v) {
        playAnim(v);
        int count = getOpenBrackets();
        for (int i = 0; i < count; i++) {
            input = input +")";
        }
        history = input + "=";
        input = Utils.Calculate(input) + "";

        refreshInput();
        refreshHistory();
    }

    public void ResetClicked(View v) {
        playAnim(v);
        input = "0";
        history = "";
        refreshHistory();
        refreshInput();
    }


    public void BackspaceClicked(View v) {
        playAnim(v);
        if (input.length() > 0)
            input = input.substring(0, input.length() - 1);
        refreshInput();
    }

    public void OperatorClicked(View v) {
        playAnim(v);
        if (input.equals("")) input = "0";
        if (input.length() > 0)
            if (input.charAt(input.length() - 1) == '.' || (!Character.isDigit(lastChar) && lastChar != ')' && lastChar != '('))
                input = input.substring(0, input.length() - 1);
        input = input + ((TextView) v).getText();
        refreshInput();
    }

    public void BracketOpened(View v) {
        playAnim(v);
        if (input.length() > 0)
            if (Character.isDigit(lastChar))
                input = input + "*";
        input = input + "(";
        refreshInput();
    }

    public void BracketClosed(View v) {
        playAnim(v);
        if (getOpenBrackets() > 0) {
            input = input + ")";
        }
        refreshInput();
    }

    private void refreshInput() {
        if (input.length() > 0)
            lastChar = input.charAt(input.length() - 1);
        inputData.setValue(input);
        if (activeviewModel != null)
           activeviewModel.setInput(input);
    }

    private void refreshHistory() {
        historyData.setValue(history);
        if (activeviewModel instanceof CalculatorViewModel) {
            ((CalculatorViewModel) activeviewModel).setHistory(history);
        }
    }

    private void onButtonPress(View v){
        playAnim(v);
        CharSequence input=((TextView) v).getText();
        activeviewModel.setInput(input.charAt(0));
    }

    public void playAnim(View v) {
        FloatPropertyCompat<View> scale = new FloatPropertyCompat<View>("") {
            @Override
            public float getValue(View view) {
                return view.getScaleX();
            }

            @Override
            public void setValue(View view, float value) {
                view.setScaleX(value);
                view.setScaleY(value);
            }
        };
        SpringAnimation anim = new SpringAnimation(v, scale, 1f);
        anim.getSpring().setStiffness(SpringForce.STIFFNESS_LOW).setDampingRatio(0.35f);
        anim.setMinimumVisibleChange(DynamicAnimation.MIN_VISIBLE_CHANGE_SCALE)
                .setStartVelocity(3f)
                .start();
    }

    //todo on Main
//    private void PlayStartingAnimation() {
//        final Animation anim = AnimationUtils.loadAnimation(context, R.anim.bounce_from_bottom);
//        numPadView.startAnimation(anim);
//        int offset=1000;
//
//        int count = numPadView.getChildCount();
//        for (int i = 0; i < count; i++) {
//            TextView button = (TextView) numPadView.getChildAt(count-i-1);
//            final Animation myAnim = AnimationUtils.loadAnimation(context, R.anim.bounce_from_right);
//            myAnim.setStartOffset(i * 50+offset);
//            button.startAnimation(myAnim);
//        }
//        final Animation myAnim = AnimationUtils.loadAnimation(context, R.anim.bounce_from_top);
//        inputLayout.startAnimation(myAnim);
//    }

    public MutableLiveData<String> getInputData() {
        return inputData;
    }

    public MutableLiveData<Integer> getType() {
        return type;
    }

    public MutableLiveData<String> getHistoryData() {
        return historyData;
    }

    public MutableLiveData<Boolean> getOnlyNums() {
        return onlyNums;
    }

    public void setActiveviewModel(ParentViewModel activeviewModel) {
        this.activeviewModel = activeviewModel;
        input = activeviewModel.getInput().getValue();
        if (input == null || input.equals(""))
            input = "0";
    }
}
