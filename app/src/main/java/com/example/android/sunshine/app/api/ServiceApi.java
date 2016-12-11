package com.example.android.sunshine.app.api;

import com.example.android.sunshine.app.model.ForecastModel;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ServiceApi {

  String CONTENT_TYPE_JSON = "Content-Type: application/json";
  String CONTENT_TYPE_TEXT_PLAIN = "Content-Type: text/plain";

    /**
     * get 5 Days Forecast .
     *
     * @return the order response
     */
    @GET("data/2.5/forecast/city")
    Call<ForecastModel> get5DaysForecast(@Query("id") int cityId,
                                         @Query("units") String metric,
                                         @Query("cnt") int cnt,
                                         @Query("APPID") String AppId);


}
