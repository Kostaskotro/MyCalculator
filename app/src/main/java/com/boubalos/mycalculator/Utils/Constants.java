package com.boubalos.mycalculator.Utils;

public class Constants {

    public static final String CALCULATOR_FRAGMENT = "Calculator";
    public static final String CURRENCY_FRAGMENT = "Currency";
    public static final String RATES = "rates";
    public static final String CURRENCIES = "currencies";
    public static final String MY_PREFS = "sharedprefs";
    public static final String SELECTED_CURRENCIES = "selected";

    final public static String API_KEY_FIXER = "4e5e33b1bedefa2e20ad79d3bf3496fc";
    final public static String BASE_URL = "http://data.fixer.io/api/";
    final public static String CURRENCIES_ENDPOINT = "symbols?access_key=" + API_KEY_FIXER;
    final public static String LATEST_RATES_ENDPOINT = "latest?access_key=" + API_KEY_FIXER + "&base=EUR&symbols=";


}
