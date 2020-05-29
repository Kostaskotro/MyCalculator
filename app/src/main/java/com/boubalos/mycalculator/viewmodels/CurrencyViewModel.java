package com.boubalos.mycalculator.viewmodels;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.boubalos.mycalculator.R;
import com.boubalos.mycalculator.Utils.SharedPrefsUtils;
import com.boubalos.mycalculator.Utils.Utils;
import com.boubalos.mycalculator.models.Currency;
import com.boubalos.mycalculator.retrofit.ApiClient;
import com.boubalos.mycalculator.retrofit.ApiInterface;
import com.google.gson.Gson;
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
import static com.boubalos.mycalculator.Utils.Constants.LAST_UPDATE;
import static com.boubalos.mycalculator.Utils.Constants.MY_PREFS;
import static com.boubalos.mycalculator.Utils.Constants.RATES;
import static com.boubalos.mycalculator.Utils.Constants.SELECTED_CURRENCIES;

public class CurrencyViewModel extends ParentViewModel {
    public static final String LOADING = "loading";
    public static final String CANT_CONNECT = "cant connect";
    public static final String INIT_COMPLETE = "init complete";
    public static final String SERVICE_ERROR = "service error";
    public static final String READY = "ready";
    public static final int CURRENCY_FIELDS = 5;

    private MutableLiveData<String> userMessage = new MutableLiveData<>();
    private MutableLiveData<Boolean> connectionError = new MutableLiveData<>();
    private MutableLiveData<Integer> active = new MutableLiveData<>();
    private MutableLiveData<Double> activeAmount = new MutableLiveData<>();

    private HashMap<String, Double> rates = new HashMap<>();
    private MutableLiveData<HashMap<String, Double>> ratesData = new MutableLiveData<>(); // data observed by each item to know if the currency rates are available
    private int[] liveCurrecies = new int[CURRENCY_FIELDS];                                //currencies the user have chosen
    private MutableLiveData<String> state = new MutableLiveData<>();
    private ArrayList<Currency> currencies = new ArrayList<>();                             //all currencies retrieved by server
    private String text = "";
    private Context context;

    public CurrencyViewModel(Application application) {
        super(application);
        context = application.getApplicationContext();
        connectionError.setValue(false);
        state.setValue(LOADING);
        LoadSelectedCurrencies();
        getListFromServer();
    }

    @Override
    public void setInput(char c) { //we get input from numpad here
        if (Character.isDigit(c)) {
            text = text + c;
            refreshAmount();
        } else
            switch (c) {
                case 'C': {
                    text = "";
                    refreshAmount();
                    break;
                }
                case 'b': {
                    if (text.length() > 0)
                        text = text.substring(0, text.length() - 1);
                    refreshAmount();
                    break;
                }
                case '.': {
                    if (Utils.canPutPoint(text))
                        text = text + '.';
                    refreshAmount();
                    break;
                }
            }

    }

    private void refreshAmount() {
        if (text.equals("")) activeAmount.setValue(1d);
        else activeAmount.setValue(Double.parseDouble(text));
    }

    public void setLiveCurrency(int position, int i) {      // if rate for the Currency chosen is not in memory do an ApiCall for the rates
        liveCurrecies[i] = position;
        ratesData.setValue(rates);                  //update rates in case the rates are already available
        saveSelectedCurrencies();
    }


    public void setActiveCurrency(Integer active)      { //changes witch currency item(row), is active
        if (this.active.getValue() != active) {
            this.active.setValue(active);
            Log.d("Active Currency : ", currencies.get(liveCurrecies[active]).toString());
            text = "";
            activeAmount.setValue(1d);
        }
    }

    public MutableLiveData<String> getState() {
        return state;
    }

    public ArrayList<Currency> getCurrencies() {
        return currencies;
    }

    public int[] getLiveCurrecies() {
        return liveCurrecies;
    }

    public MutableLiveData<Integer> getActive() {
        return active;
    }

    public MutableLiveData<Double> getActiveAmount() {
        return activeAmount;
    }

    public HashMap<String, Double> getRates() {
        return rates;
    }

    public void setActiveAmount(Double activeAmount) {
        this.activeAmount.setValue(activeAmount);
    }

    public MutableLiveData<HashMap<String, Double>> getRatesData() {
        return ratesData;
    }

    public MutableLiveData<String> getUserMessage() {
        return userMessage;
    }

    public MutableLiveData<Boolean> getConnectionError() {
        return connectionError;
    }

    //////////////////API Calls/////////////////////////
    private void getListFromServer() {     //get currencies list from fixer
        ApiInterface apiInterface = ApiClient.getClient().create(ApiInterface.class);
        Call<Object> call = apiInterface.GetAllAvailableCurrencies();
        call.enqueue(new Callback<Object>() {
            @Override
            public void onResponse(Call<Object> call, Response<Object> response) {
                if (isSuccessfull((LinkedTreeMap) response.body())) {
                    connectionError.setValue(false);
                    parseCurrenciesList((LinkedTreeMap) response.body());
                    Log.d("CallResponse : ", currencies.get(0).getDescription());
                    getRatesFromServer();
                } else {
                    getErrorMessage((LinkedTreeMap) response.body());
                    state.setValue(SERVICE_ERROR);
                }
            }
            @Override
            public void onFailure(Call<Object> call, Throwable t) {
                checkPrefs();
            }
        });
    }

    private void getRatesFromServer() {        //get conversion rates from fixer
        ApiInterface apiInterface;
        apiInterface = ApiClient.getClient().create(ApiInterface.class);
        String symbols = "";
        for (Currency c : currencies) {
            if (!symbols.equals("")) symbols += ",";
            symbols += c.getName();
        }
        Call<Object> call = apiInterface.GetLatestRates(symbols);
        call.enqueue(new Callback<Object>() {
            @Override
            public void onResponse(Call<Object> call, Response<Object> response) {
                if (isSuccessfull((LinkedTreeMap) response.body())) {
                    connectionError.setValue(false);
                    parseRates((LinkedTreeMap) response.body());
                    String date =Utils.getDate();
                    SharedPrefsUtils.writeToSharedPreferences(context,MY_PREFS,LAST_UPDATE,date);
                    userMessage.setValue("Currencies updated : "+ date);
                } else {
                    getErrorMessage((LinkedTreeMap) response.body());
                    state.setValue(SERVICE_ERROR);
                }
            }
            @Override
            public void onFailure(Call<Object> call, Throwable t) {
                checkPrefs();
            }
        });
    }

    private boolean isSuccessfull(LinkedTreeMap response) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            return jsonObject.getBoolean("success");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return false;
    }

    private void getErrorMessage(LinkedTreeMap response) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            jsonObject = jsonObject.getJSONObject("error");
            userMessage.setValue(jsonObject.getString("info"));
            connectionError.setValue(true);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void parseRates(LinkedTreeMap response) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            jsonObject = jsonObject.getJSONObject("rates");
            Iterator<String> keys = jsonObject.keys();
            rates = new HashMap<>();
            while (keys.hasNext()) {
                String key = keys.next();
                rates.put(key, (Double) jsonObject.get(key));
            }
            Log.d("Rates received : ", rates.toString());
            state.setValue(READY);
            ratesData.setValue(rates);
            Gson gson = new Gson();
            String hashMapString = gson.toJson(rates);
            SharedPrefsUtils.writeToSharedPreferences(context, MY_PREFS, RATES, hashMapString);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void parseCurrenciesList(LinkedTreeMap response) {
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
            SharedPrefsUtils.writeToSharedPreferences(context, MY_PREFS, CURRENCIES, listString);
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    //////////////// Retrieve/Save data from Shared prefs //////////////////
    private void checkPrefs() {
        String savedCurrencies = SharedPrefsUtils.readValueFromSharedPreferences(context, MY_PREFS, CURRENCIES);
        String savedRates = SharedPrefsUtils.readValueFromSharedPreferences(context, MY_PREFS, RATES);
        if (savedCurrencies.equals("") || savedRates.equals("")) {
            state.setValue(CANT_CONNECT);
            userMessage.setValue(context.getString(R.string.no_connection));
            connectionError.setValue(true);
        }
        else {
            currencies = parseCurrenciesJSON(savedCurrencies);
            Gson gson = new Gson();
            rates = gson.fromJson(savedRates, HashMap.class);

            if (rates.size() > 0 && currencies.size() > 0) {
                state.setValue(INIT_COMPLETE);
                active.setValue(0);
                activeAmount.setValue(1d);
                ratesData.setValue(rates);
                state.setValue(READY);
                userMessage.setValue("Currencies updated : "+ SharedPrefsUtils.readValueFromSharedPreferences(context,MY_PREFS,LAST_UPDATE));
            }
        }
    }

    private ArrayList<Currency> parseCurrenciesJSON(String listString) {
        ArrayList<Currency> list = new ArrayList<>();
        try {
            JSONArray jsonArray = new JSONArray(listString);         //parse JsonString saved ad SharedPrefs
            for (int i = 0; i < jsonArray.length(); i++) {
                Gson gson = new Gson();
                Currency currency = gson.fromJson(jsonArray.get(i).toString(), Currency.class);
                list.add(currency);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return list;
    }

    private void saveSelectedCurrencies() {
        ArrayList<Currency> saveCurrencies = new ArrayList<>();
        for (int x : liveCurrecies)
            saveCurrencies.add(currencies.get(x));
        ArrayList<Integer> intList = new ArrayList<>();
        for (int i : liveCurrecies)
            intList.add(i);
        Gson gson = new Gson();
        String listString = gson.toJson(intList);
        SharedPrefsUtils.writeToSharedPreferences(context, MY_PREFS, SELECTED_CURRENCIES, listString);
    }

    private void LoadSelectedCurrencies() {
        try {
            String savedArray = SharedPrefsUtils.readValueFromSharedPreferences(context, MY_PREFS, SELECTED_CURRENCIES);
            if (!savedArray.equals("")) {
                JSONArray jsonArray = new JSONArray(savedArray);
                for (int i = 0; i < CURRENCY_FIELDS; i++) {
                    liveCurrecies[i] = jsonArray.getInt(i);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}
