package com.boubalos.mycalculator.views.bindings;

import android.widget.Spinner;
import android.widget.TextView;

import androidx.databinding.BindingAdapter;

import com.boubalos.mycalculator.views.adapters.SpinnerAdapter;

public class BindingAdapters {

    @BindingAdapter(value = "onItemSelected")
    public static void onSpinnerItemSelected(Spinner spinner, CurrencyItem currencyItem) {
        spinner.setOnItemSelectedListener(currencyItem);
        spinner.setAdapter(new SpinnerAdapter(spinner.getContext(), currencyItem.viewModel.getCurrencies()));
        spinner.setSelection(currencyItem.viewModel.getLiveCurrecies()[currencyItem.getIndex()]);
    }

    @BindingAdapter(value = "onSelect")
    public static void onFieldSelect(TextView editText, CurrencyItem currencyItem) {
        editText.setOnTouchListener(currencyItem);
        editText.addTextChangedListener(currencyItem);
        editText.setText("1");
    }

}
