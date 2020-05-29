package com.boubalos.mycalculator.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

public class ParentViewModel extends AndroidViewModel {
    MutableLiveData<Character> input = new MutableLiveData<>();

    public ParentViewModel(@NonNull Application application) {
        super(application);
    }


    public void setInput(char input) {
        this.input.setValue(input);
    }

    public MutableLiveData<Character> getInput() {
        return input;
    }
}
