package com.boubalos.mycalculator.views.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.boubalos.mycalculator.views.bindings.CurrencyItem;
import com.boubalos.mycalculator.R;
import com.boubalos.mycalculator.databinding.CurrencyFragmentLayoutBinding;
import com.boubalos.mycalculator.databinding.CurrencyItemBinding;
import com.boubalos.mycalculator.viewmodels.CurrencyViewModel;

import java.util.ArrayList;
import java.util.List;

import static com.boubalos.mycalculator.viewmodels.CurrencyViewModel.CANT_CONNECT;
import static com.boubalos.mycalculator.viewmodels.CurrencyViewModel.CURRENCY_FIELDS;
import static com.boubalos.mycalculator.viewmodels.CurrencyViewModel.INIT_COMPLETE;
import static com.boubalos.mycalculator.viewmodels.CurrencyViewModel.LOADING;

public class CurrecnyFragment extends Fragment {

    private List<CurrencyItemBinding> itemBindings = new ArrayList<>();
    private CurrencyViewModel viewModel;
    private CurrencyFragmentLayoutBinding currencyFragmentLayoutBinding;
    private LinearLayout parent;
    TextView noConnectionText;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        currencyFragmentLayoutBinding = DataBindingUtil.inflate(inflater, R.layout.currency_fragment_layout, container, false);
        currencyFragmentLayoutBinding.setLifecycleOwner(this);
        viewModel = new ViewModelProvider(getActivity()).get(CurrencyViewModel.class);
        View rootView = currencyFragmentLayoutBinding.getRoot();
        noConnectionText = rootView.findViewById(R.id.no_connection_message);
        noConnectionText.setVisibility(View.GONE);
        parent = rootView.findViewById(R.id.parent);
        if (viewModel.getCurrencies().size() > 0) addViews(); //in case of orientation change
        else viewModel.getState().observe(getViewLifecycleOwner(), this::stateChanged);
        return rootView;
    }

    private void stateChanged(String state) {
        switch (state) {
            case LOADING: {
                break;
            }
            case INIT_COMPLETE: {
                addViews();
                break;
            }
            case CANT_CONNECT: {
                noConnectionText.setVisibility(View.VISIBLE);
                break;
            }
        }
    }

    private void addViews() {
        for (int i = 0; i < CURRENCY_FIELDS; i++) {
            addView(i);
        }
    }

    private void addView(int i) {
        CurrencyItemBinding itemBinding = DataBindingUtil.inflate(getLayoutInflater(), R.layout.currency_item, parent, false);
        itemBinding.setCurrencyItem(new CurrencyItem(i, this, viewModel));
        itemBinding.setLifecycleOwner(this);
        itemBindings.add(itemBinding);
        parent.addView(itemBinding.getRoot());
    }


}
