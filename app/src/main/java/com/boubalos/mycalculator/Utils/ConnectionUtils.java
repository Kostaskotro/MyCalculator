package com.boubalos.mycalculator.Utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class ConnectionUtils {


    private static boolean wifiConnected, mobileConnected, isAvailable;


    /**
     * Checks the network connection and sets the wifiConnected
     * and mobileConnectedvariables accordingly.
     */
    private static void updateConnectionFlags(Context context) {
        ConnectivityManager connMgr = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeInfo = connMgr.getActiveNetworkInfo();
        if (activeInfo != null && activeInfo.isConnected()) {
            wifiConnected = activeInfo.getType() == ConnectivityManager.TYPE_WIFI;
            mobileConnected = activeInfo.getType() == ConnectivityManager.TYPE_MOBILE;
        } else {
            wifiConnected = false;
            mobileConnected = false;
        }
    }

    public static boolean getWifiConnected(Context context) {
        updateConnectionFlags(context);
        return wifiConnected;
    }

    public static boolean getMobileConnected(Context context) {
        updateConnectionFlags(context);
        return mobileConnected;
    }

    public static boolean getInternetConnected(Context context) {
        updateConnectionFlags(context);
        return wifiConnected || mobileConnected;
    }


}
