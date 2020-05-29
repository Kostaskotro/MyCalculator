package com.boubalos.mycalculator.views.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.boubalos.mycalculator.R;
import com.boubalos.mycalculator.models.Currency;

import java.util.ArrayList;

public class SpinnerAdapter extends ArrayAdapter<Currency> {

    public SpinnerAdapter(@NonNull Context context, ArrayList<Currency> currencies) {
        super(context, 0, currencies);
    }


    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null)
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.spinner_label, parent, false);

        TextView textView = convertView.findViewById(R.id.text);
        textView.setText(getItem(position).getName());
        return convertView;
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null)
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.spinner_item, parent, false);

        TextView textView = convertView.findViewById(R.id.text);
        textView.setText(getItem(position).toString());
        return convertView;
    }


}
