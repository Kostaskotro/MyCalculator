package com.boubalos.mycalculator.views.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.boubalos.mycalculator.views.fragments.CalculatorFragment;
import com.boubalos.mycalculator.views.fragments.CurrecnyFragment;

import static com.boubalos.mycalculator.Utils.Constants.CALCULATOR_FRAGMENT;
import static com.boubalos.mycalculator.Utils.Constants.CURRENCY_FRAGMENT;

public class MyPagerAdapter extends FragmentStatePagerAdapter {

    public static String[] FRAGMENTS = {
            CALCULATOR_FRAGMENT,
            CURRENCY_FRAGMENT
    };

    public MyPagerAdapter(@NonNull FragmentManager fm, int behavior) {
        super(fm, behavior);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position) {                                                                         //Select the appropriate fragment
            case 0:
                return new CalculatorFragment();
            case 1:
                return new CurrecnyFragment();
        }
        return null;
    }

    @Override
    public int getCount() {
        return FRAGMENTS.length;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return FRAGMENTS[position];
    }
}
