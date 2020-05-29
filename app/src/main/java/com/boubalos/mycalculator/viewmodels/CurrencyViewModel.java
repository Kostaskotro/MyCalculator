package com.boubalos.mycalculator.viewmodels;

import android.app.Application;
import android.content.Context;
import android.util.Log;
import android.view.View;

import androidx.lifecycle.MutableLiveData;

import com.boubalos.mycalculator.Utils.SharedPrefsUtils;
import com.boubalos.mycalculator.retrofit.ApiClient;
import com.boubalos.mycalculator.retrofit.ApiInterface;
import com.boubalos.mycalculator.models.Currency;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.internal.LinkedTreeMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.boubalos.mycalculator.Utils.Constants.CURRENCIES;
import static com.boubalos.mycalculator.Utils.Constants.MY_PREFS;
import static com.boubalos.mycalculator.Utils.Constants.RATES;

public class CurrencyViewModel extends ParentViewModel {
    public static final String LOADING = "loading";
    public static final String CANT_CONNECT = "cant connect";
    public static final String INIT_COMPLETE = "init complete";
    public static final String READY = "ready";
    public static final int CURRENCY_FIELDS = 3;


    private MutableLiveData<Integer> active = new MutableLiveData<>();
    private MutableLiveData<Double> activeAmount = new MutableLiveData<>();

    private HashMap<String, Double> rates = new HashMap<>();
    private MutableLiveData<HashMap<String, Double>> ratesData = new MutableLiveData<>(); // data observed by each item to know if the currency rates are available
    private int[] liveCurrecies = new int[CURRENCY_FIELDS];                                //currencies the user have chosen
    private MutableLiveData<String> state = new MutableLiveData<>();
    private ArrayList<Currency> currencies = new ArrayList<>();                             //all currencies retrieved by server

    public CurrencyViewModel(Application application) {
        super(application);
        state.setValue(LOADING);
            getList();
    }

    @Override
    public void setInput(String input) {
        if (input == null || input.equals("")) input = "0";
        activeAmount.setValue(Double.parseDouble(input));//Todo update input here
    }


    public void setLiveCurrency(int position, int i) {      // if rate for the Currency chosen is not in memory do an ApiCall for the rates
        liveCurrecies[i] = position;
        if (rates.get(currencies.get(position).getName()) == null) {
            state.setValue(LOADING);
            updateRates();
        } else ratesData.setValue(rates);                  //update rates in case the rates are already available
    }

    public void setActive(Integer active) {
        if (this.active.getValue() != active) {
            this.active.setValue(active);
            Log.d("Active Currency : ", currencies.get(liveCurrecies[active]).toString());
        }
    }

    public MutableLiveData<String> getState() {return state; }

    public ArrayList<Currency> getCurrencies() {return currencies; }

    public int[] getLiveCurrecies() { return liveCurrecies; }

    public MutableLiveData<Integer> getActive() { return active; }

    public MutableLiveData<Double> getActiveAmount() { return activeAmount; }

    public HashMap<String, Double> getRates() { return rates; }

    public void setActiveAmount(Double activeAmount) { this.activeAmount.setValue(activeAmount); }

    public MutableLiveData<HashMap<String, Double>> getRatesData() { return ratesData; }



    ///API Calls
    private void updateRates() {
        ApiInterface apiInterface;
        apiInterface = ApiClient.getClient().create(ApiInterface.class);
        /**
         GET List Resources
         **/
        String symbols = "";
        for (Currency c :currencies) {
            if (!symbols.equals("")) symbols += ",";
            symbols += c.getName();
        }
        Call<Object> call = apiInterface.GetLatestRates(symbols);
        call.enqueue(new Callback<Object>() {
            @Override
            public void onResponse(Call<Object> call, Response<Object> response) {
                parseRates((LinkedTreeMap) response.body());
            }

            @Override
            public void onFailure(Call<Object> call, Throwable t) {
                try {
                    checkPrefs();
                }catch (JSONException e){
                    e.printStackTrace();
                }
            }
        });
    }

    private void getList() {

        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        /**
         GET List Resources
         **/
        Call<Object> call = apiInterface.GetAllAvailableCurrencies();
        call.enqueue(new Callback<Object>() {
            @Override
            public void onResponse(Call<Object> call, Response<Object> response) {
                parseResponse((LinkedTreeMap) response.body());
                Log.d("CallResponse : ", currencies.get(0).getDescription());
            }
            @Override
            public void onFailure(Call<Object> call, Throwable t) {
               try {
                   checkPrefs();
               }catch (JSONException e){
                   e.printStackTrace();
               }
            }
        });
    }

    private void parseRates(LinkedTreeMap response) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            jsonObject = jsonObject.getJSONObject("rates");
            Iterator<String> keys = jsonObject.keys();
            rates =new HashMap<>();
            while (keys.hasNext()) {
                String key = keys.next();
                rates.put(key, (Double) jsonObject.get(key));
            }
            Log.d("Rates received : ", rates.toString());
            state.setValue(READY);
            ratesData.setValue(rates);
            Gson gson = new Gson();
            String hashMapString = gson.toJson(rates);
            SharedPrefsUtils.writeToSharedPreferences(getApplication().getApplicationContext(),MY_PREFS,RATES,hashMapString);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void parseResponse(LinkedTreeMap response) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            jsonObject = jsonObject.getJSONObject("symbols");
            Iterator<String> keys = jsonObject.keys();
            while (keys.hasNext()) {
                String key = keys.next();
                Currency currency = new Currency(key, (String) jsonObject.get(key));
                currencies.add(currency);
            }
            state.setValue(INIT_COMPLETE);
            active.setValue(0);
            activeAmount.setValue(1d);
            Gson gson = new Gson();
            String listString = gson.toJson(currencies);
            SharedPrefsUtils.writeToSharedPreferences(getApplication().getApplicationContext(),MY_PREFS,CURRENCIES,listString);
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }


    private void checkPrefs() throws JSONException{
        String savedCurrencies = SharedPrefsUtils.readValueFromSharedPreferences(getApplication().getApplicationContext(),MY_PREFS,CURRENCIES);
        if(savedCurrencies.equals("")) state.setValue(CANT_CONNECT);
        else {
            JSONArray jsonArray= new JSONArray(savedCurrencies);         //parse JsonString saved ad SharedPrefs
         for(int i=0;i<jsonArray.length();i++) {
               Gson gson= new Gson();
                Currency currency = gson.fromJson(jsonArray.get(i).toString(),Currency.class);
                currencies.add(currency);
            }
        }
          String savedRates = SharedPrefsUtils.readValueFromSharedPreferences(getApplication().getApplicationContext(),MY_PREFS,RATES);
        if(savedRates.equals("")) state.setValue(CANT_CONNECT);
        else{
            Gson gson = new Gson();
            rates = gson.fromJson(savedRates,HashMap.class);
        }
        if(rates.size()>0&&currencies.size()>0)
        {
            state.setValue(INIT_COMPLETE);
            active.setValue(0);
            activeAmount.setValue(1d);
            ratesData.setValue(rates);
            state.setValue(READY);
        }
    }
////////////////////////
}
