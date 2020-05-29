package com.boubalos.mycalculator.retrofit;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

import static com.boubalos.mycalculator.Utils.Constants.CURRENCIES_ENDPOINT;
import static com.boubalos.mycalculator.Utils.Constants.LATEST_RATES_ENDPOINT;

public interface ApiInterface {
    @GET(CURRENCIES_ENDPOINT)
    Call<Object> GetAllAvailableCurrencies();

    @GET(LATEST_RATES_ENDPOINT)
    Call<Object> GetLatestRates(@Query("symbols") String symbols);

}
