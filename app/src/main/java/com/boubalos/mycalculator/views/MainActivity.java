package com.boubalos.mycalculator.views;

import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.GridLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager.widget.ViewPager;

import com.boubalos.mycalculator.databinding.MainActivityLayoutBinding;
import com.boubalos.mycalculator.views.bindings.MyNumpad;
import com.boubalos.mycalculator.views.adapters.MyPagerAdapter;
import com.boubalos.mycalculator.R;
import com.boubalos.mycalculator.databinding.CalculatorNumpadLayoutBinding;
import com.boubalos.mycalculator.viewmodels.CalculatorViewModel;
import com.boubalos.mycalculator.viewmodels.CurrencyViewModel;

import static androidx.fragment.app.FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT;

public class MainActivity extends AppCompatActivity implements ViewPager.OnPageChangeListener {

    ViewPager mainViewPager;
    MyPagerAdapter pagerAdapter;
    private GridLayout numPadView;
    MyNumpad numpad;
    CalculatorNumpadLayoutBinding numpadBinding;
    CurrencyViewModel currencyViewModel;
    CalculatorViewModel calculatorViewModel;
    MainActivityLayoutBinding mainActivityLayoutBinding;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainActivityLayoutBinding=DataBindingUtil.setContentView(this,R.layout.main_activity_layout);
        mainActivityLayoutBinding.setLifecycleOwner(this);
        mainViewPager = findViewById(R.id.viewPager);
        pagerAdapter = new MyPagerAdapter(getSupportFragmentManager(), BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        mainViewPager.setAdapter(pagerAdapter);
        mainViewPager.addOnPageChangeListener(this);
        numpadBinding = DataBindingUtil.bind(findViewById(R.id.numpad_layout));
        numPadView = mainActivityLayoutBinding.getRoot().findViewById(R.id.numpad_layout);
        numpad = new MyNumpad();
        calculatorViewModel = new ViewModelProvider(this).get(CalculatorViewModel.class);
        currencyViewModel = new ViewModelProvider(this).get(CurrencyViewModel.class);
        numpadBinding.setNumpad(numpad);
        numpad.setActiveviewModel(calculatorViewModel);
        PlayStartingAnimation();
    }


    private void PlayStartingAnimation() {
        final Animation anim = AnimationUtils.loadAnimation(this, R.anim.slide_from_bottom);
        numpadBinding.getRoot().startAnimation(anim);
        int offset = 1000;

        int count = numPadView.getChildCount();
        for (int i = 0; i < count; i++) {
            View button =  numPadView.getChildAt(count - i - 1);
            final Animation myAnim = AnimationUtils.loadAnimation(this, R.anim.bounce_from_top);
            myAnim.setStartOffset(i * 50 + offset);
            button.startAnimation(myAnim);
        }
        final Animation myAnim = AnimationUtils.loadAnimation(this, R.anim.slide_from_top);
        mainViewPager.startAnimation(myAnim);
    }

    private void HideOperatorsAnimation() {
        int count = numPadView.getChildCount();
        for (int i = 0; i < count; i++) {
            View button = numPadView.getChildAt(count - i - 1);
            char c='C';
            if(button instanceof TextView)
             c = ((TextView)button).getText().charAt(0);
            if (!(Character.isDigit(c) || c == 'C' || c == 'b' || c == '.')) {
                final Animation myAnim = AnimationUtils.loadAnimation(this, R.anim.disappear_from_bot);
                myAnim.setStartOffset(i * 10);
                myAnim.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        button.setVisibility(View.GONE);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {
                    }
                });
                button.startAnimation(myAnim);
            }
        }
    }

    private void ShowOperatorsAnimation() {
        int count = numPadView.getChildCount();
        for (int i = 0; i < count; i++) {
            View button = numPadView.getChildAt(count - i - 1);
            char c='C';
            if(button instanceof TextView)
                c = ((TextView)button).getText().charAt(0);
            if (!(Character.isDigit(c) || c == 'C' || c == 'b' || c == '.')) {
                final Animation myAnim = AnimationUtils.loadAnimation(this, R.anim.bounce_from_top);
                myAnim.setStartOffset(i * 10);
                myAnim.setStartOffset(i * 10);
                myAnim.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                        button.setVisibility(View.VISIBLE);
                    }
                    @Override
                    public void onAnimationEnd(Animation animation) {
                    }
                    @Override
                    public void onAnimationRepeat(Animation animation) {
                    }
                });
                button.startAnimation(myAnim);
            }
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}
    @Override
    public void onPageSelected(int position) {
        if (position == 1) {
            HideOperatorsAnimation();
            numpad.setActiveviewModel(currencyViewModel);
        }
        if (position == 0) {
            ShowOperatorsAnimation();
            numpad.setActiveviewModel(calculatorViewModel);
        }
    }
    @Override
    public void onPageScrollStateChanged(int state) {
    }
}
