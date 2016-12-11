package com.example.android.sunshine.app.model;

import com.example.android.sunshine.app.api.ResponseTemplate;
import com.google.gson.annotations.SerializedName;

/**
 * Created by livnatavikasis on 15/11/2016.
 */
public class ForecastModel extends ResponseTemplate {

    @SerializedName("city")
    City city;

    @SerializedName("list")
    ArrayListForecast list;


    public City getCity() {
        return city;
    }

    public void setCity(City city) {
        this.city = city;
    }


    public void setList(ArrayListForecast list) {
        this.list = list;
    }

    public ArrayListForecast getList() {
        return list;
    }
}
