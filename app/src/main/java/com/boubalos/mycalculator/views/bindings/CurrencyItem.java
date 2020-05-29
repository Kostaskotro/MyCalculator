package com.boubalos.mycalculator.views.bindings;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.MutableLiveData;

import com.boubalos.mycalculator.Utils.Utils;
import com.boubalos.mycalculator.models.Currency;
import com.boubalos.mycalculator.viewmodels.CurrencyViewModel;

import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.InputMismatchException;

import static com.boubalos.mycalculator.viewmodels.CurrencyViewModel.READY;

public class CurrencyItem implements AdapterView.OnItemSelectedListener, View.OnTouchListener, TextWatcher , View.OnClickListener {


    CurrencyViewModel viewModel;
    private int index;
    private LifecycleOwner fragment;
    private MutableLiveData<Boolean> ready = new MutableLiveData<>();
    private MutableLiveData<String> value = new MutableLiveData<>();
    private MutableLiveData<Boolean> isActive = new MutableLiveData<>();
    private MutableLiveData<Currency> currency = new MutableLiveData<>();

    public CurrencyItem(int i, LifecycleOwner owner, CurrencyViewModel viewModel) {
        index = i;
        fragment = owner;
        this.viewModel = viewModel;
        if (index == 0) isActive.setValue(true);
        else isActive.setValue(false);
        currency.setValue(viewModel.getCurrencies().get(viewModel.getLiveCurrecies()[index]));
        ready.setValue(true);
        viewModel.getActive().observe(fragment, this::activeChanged);
        viewModel.getActiveAmount().observe(fragment, this::activeAmountChanged);
        viewModel.getRatesData().observe(fragment, this::ratesChanged);
    }

    private void activeChanged(int i) {//active currency changed
        if (i == index) {
            isActive.setValue(true);
            value.setValue("1");
        } else {
            isActive.setValue(false);
        }
    }

    private void ratesChanged(HashMap<String, Double> rates) {                                                 //rates updated
        try {
            Double rate1 = rates.get(currency.getValue().getName());                                                                            //rate of this currency
            Double rate2 = rates.get(viewModel.getCurrencies().get(viewModel.getLiveCurrecies()[viewModel.getActive().getValue()]).getName());  //rate of active currency
            if (rate1 != null && rate2 != null) {
                ready.setValue(true);
                activeAmountChanged(viewModel.getActiveAmount().getValue());
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    private void activeAmountChanged(double amount) { //called when active currency amount is changed
        if (viewModel.getState().getValue().equals(READY)) {
            String from = viewModel.getCurrencies().get(viewModel.getLiveCurrecies()[viewModel.getActive().getValue()]).getName();
            String to = viewModel.getCurrencies().get(viewModel.getLiveCurrecies()[index]).getName();
            DecimalFormat formater = new DecimalFormat("#.####");
            value.setValue(formater.format(Utils.ConvertCurrency(amount, viewModel.getRates(), from, to)));
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        viewModel.setLiveCurrency(position, index);
        currency.setValue(viewModel.getCurrencies().get(position));
        if (viewModel.getRates().get(currency.getValue().getName()) == null)
            ready.setValue(false);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        viewModel.setActiveCurrency(index);
        return false;
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        try {
            for (int i = 0; i < s.length(); i++) {
                if (!(Character.isDigit(s.charAt(i)) || s.charAt(i) == '.' || s.charAt(i) == ',' || s.charAt(i) == 'e' || s.charAt(i) == 'E'))
                    throw new InputMismatchException("Input Not a Number");
            }
        } catch (InputMismatchException e) {
            e.printStackTrace();
        }
    }

    public int getIndex() {
        return index;
    }

    public MutableLiveData<Currency> getCurrency() {
        return currency;
    }

    public MutableLiveData<Boolean> getIsActive() {
        return isActive;
    }

    public MutableLiveData<String> getValue() {
        return value;
    }

    public MutableLiveData<Boolean> getReady() {
        return ready;
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {}
    @Override
    public void afterTextChanged(Editable s) {}
    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

    @Override
    public void onClick(View v) {

    }
}

