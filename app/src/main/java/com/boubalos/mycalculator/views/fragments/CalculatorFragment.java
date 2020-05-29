package com.boubalos.mycalculator.views.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.boubalos.mycalculator.R;
import com.boubalos.mycalculator.databinding.CalculatorFragmentLayoutBinding;
import com.boubalos.mycalculator.viewmodels.CalculatorViewModel;

public class CalculatorFragment extends Fragment {

    private CalculatorFragmentLayoutBinding inputBinding;
    private CalculatorViewModel calculatorViewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        inputBinding = DataBindingUtil.inflate(inflater, R.layout.calculator_fragment_layout, container, false);
        View root = inputBinding.getRoot();
        calculatorViewModel = new ViewModelProvider(getActivity()).get(CalculatorViewModel.class);
        inputBinding.setLifecycleOwner(this);
        inputBinding.setViewModel(calculatorViewModel);
        return root;
    }


}
