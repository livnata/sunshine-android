package com.example.android.sunshine.app.api.retrofit;

import com.example.android.sunshine.app.model.ForecastModel;


/**
 * Created by livnatavikasis on 15/11/2016.
 */
public interface IForecastApi {

    /**
     * Active order.
     *
     * @return the order response
     */
    ForecastModel get5DaysForecast(int cityId,String metric,int cnt,String AppId);


}
