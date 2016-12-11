package com.example.android.sunshine.app.utils;

import com.example.android.sunshine.app.api.retrofit.IForecastApi;
import com.example.android.sunshine.app.api.retrofit.ProtocolRetrofit;

/**
 * Created by livnatavikasis on 27/11/2016.
 */
public class DependencyInjector {



    private static IForecastApi sProtocol;


    private DependencyInjector() {
    }
    public static synchronized IForecastApi getProtocol() {
        sProtocol = ProtocolRetrofit.getInstance();
        if (sProtocol == null) {
            return null ;
        }
        return sProtocol = ProtocolRetrofit.getInstance();
    }
}
