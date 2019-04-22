package com.ttsbco.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import junit.framework.TestResult;

public class NetworkStateReceiver extends BroadcastReceiver
{
    public NetworkStateReceiver()
    {
    }

    @Override
    public void onReceive(Context context, Intent intent)
    {
        CheckConnection.IsConnected = false;

        ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connectivity != null)
        {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null)
                for (NetworkInfo anInfo : info)
                    if (anInfo.getState() == NetworkInfo.State.CONNECTED)
                    {
                        CheckConnection.IsConnected = true;
                    }

        }
    }

}
