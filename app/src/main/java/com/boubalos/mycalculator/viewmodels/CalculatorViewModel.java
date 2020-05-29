package com.boubalos.mycalculator.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

public class CalculatorViewModel extends ParentViewModel {
    MutableLiveData<String> input = new MutableLiveData<>();
    MutableLiveData<String> history = new MutableLiveData<>();

    public CalculatorViewModel(@NonNull Application application) {
        super(application);
    }

    public void setHistory(String history) {
        this.history.setValue(history);
    }

    public MutableLiveData<String> getHistory() {
        return history;
    }


    @Override
    public void setInput(String input) {



    }
}
