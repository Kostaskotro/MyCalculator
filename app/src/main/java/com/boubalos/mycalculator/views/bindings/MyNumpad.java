package com.boubalos.mycalculator.views.bindings;

import android.view.View;
import android.widget.TextView;

import androidx.dynamicanimation.animation.DynamicAnimation;
import androidx.dynamicanimation.animation.FloatPropertyCompat;
import androidx.dynamicanimation.animation.SpringAnimation;
import androidx.dynamicanimation.animation.SpringForce;

import com.boubalos.mycalculator.viewmodels.ParentViewModel;

public class MyNumpad {
    private String input = "";
    private ParentViewModel activeviewModel;

    public void onButtonPress(View v) {
        playAnim(v);
        CharSequence input = ((TextView) v).getText();
        activeviewModel.setInput(input.charAt(0));
    }
    public void onBackspacePress(View v){
        playAnim(v);
        activeviewModel.setInput('b');
    }

    private void playAnim(View v) {
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

    public void setActiveviewModel(ParentViewModel activeviewModel) {
        this.activeviewModel = activeviewModel;
        if (input == null || input.equals(""))
            input = "0";
    }
}
