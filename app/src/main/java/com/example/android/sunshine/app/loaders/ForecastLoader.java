package com.example.android.sunshine.app.loaders;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;
import android.text.format.Time;
import android.util.Log;

import com.example.android.sunshine.app.api.retrofit.IForecastApi;
import com.example.android.sunshine.app.model.ArrayListForecast;
import com.example.android.sunshine.app.model.ForecastModel;
import com.example.android.sunshine.app.utils.DependencyInjector;

import org.json.JSONException;

import java.text.SimpleDateFormat;

/**
 * Created by livnatavikasis on 11/12/2016.
 */

public class ForecastLoader extends AsyncTaskLoader< String[]> {

    int mCityId ;
    IForecastApi mIForecastApi;
    private final String TAG = "ForecastLoader";
    String[] mForecastList = null;
    long mLastUpdateTime;


    public ForecastLoader(Context context, int cityId) {

        super(context);
        mCityId= cityId;
    }

    @Override
    public  String[] loadInBackground() {
        ForecastModel mForecastModel = null;
        String[] forecastList = null;
        try {
//            if (mIForecastApi == null)
//                mIForecastApi = ProtocolRetrofit.createInstance("http:////api.openweathermap.org//", "", getContext());
//            else
                mIForecastApi = DependencyInjector.getProtocol();


            mForecastModel = mIForecastApi.get5DaysForecast(mCityId, "metric", 7, "30bf6f95b393edde7a5ae63bc68db92b");
            ArrayListForecast listForecast = mForecastModel.getList();
            try {
                forecastList = getWeatherDataFromJson(listForecast, 5);
            } catch (JSONException t) {
                Log.d(TAG, "problem during pars and String array creation " + t.getMessage());
            }
            Log.d(TAG, "got response : " + mForecastModel.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }


        return forecastList;
    }




    @Override
    public void deliverResult( String[] data) {
        mForecastList = data;
        super.deliverResult(data);
    }

    @Override
    protected void onStartLoading() {
        super.onStartLoading();
        long currentTime = System.currentTimeMillis();
        if ( mForecastList == null ) {
            updateData(currentTime);
        }else if ( (currentTime+5*60000) >  mLastUpdateTime ) {
            // if it has been more then 5 min since last updated and we want to update
            updateData(currentTime);
        }else{
            // In case that we inside the gap time and we don't want to get data from server we will return the data from cash ...
            deliverResult(mForecastList);
        }
    }

    /**
     * update the current time + the forecast data from server
     * @param currentTime
     */
    private void updateData(long currentTime) {
        mLastUpdateTime = currentTime;
        forceLoad();
    }

    @Override
    public void forceLoad() {
        super.forceLoad();

    }

    /* The date/time conversion code is going to be moved outside the asynctask later,
       * so for convenience we're breaking it out into its own method now.
       */
    private String getReadableDateString(long time) {
        // Because the API returns a unix timestamp (measured in seconds),
        // it must be converted to milliseconds in order to be converted to valid date.
        SimpleDateFormat shortenedDateFormat = new SimpleDateFormat("EEE MMM dd");
        return shortenedDateFormat.format(time);
    }

    /**
     * Prepare the weather high/lows for presentation.
     */
    private String formatHighLows(double high, double low) {
        // For presentation, assume the user doesn't care about tenths of a degree.
        long roundedHigh = Math.round(high);
        long roundedLow = Math.round(low);

        String highLowStr = roundedHigh + "/" + roundedLow;
        return highLowStr;
    }

    /**
     * Take the String representing the complete forecast in JSON Format and
     * pull out the data we need to construct the Strings needed for the wireframes.
     * <p>
     * Fortunately parsing is easy:  constructor takes the JSON string and converts it
     * into an Object hierarchy for us.
     */
    private String[] getWeatherDataFromJson(ArrayListForecast listForecast, int numDays)
            throws JSONException {

        // These are the names of the JSON objects that need to be extracted.
        final String OWM_LIST = "list";
        final String OWM_WEATHER = "weather";
        final String OWM_TEMPERATURE = "temp";
        final String OWM_MAX = "max";
        final String OWM_MIN = "min";
        final String OWM_DESCRIPTION = "main";


        // OWM returns daily forecasts based upon the local time of the city that is being
        // asked for, which means that we need to know the GMT offset to translate this data
        // properly.

        // Since this data is also sent in-order and the first day is always the
        // current day, we're going to take advantage of that to get a nice
        // normalized UTC date for all of our weather.

        Time dayTime = new Time();
        dayTime.setToNow();

        // we start at the day returned by local time. Otherwise this is a mess.
        int julianStartDay = Time.getJulianDay(System.currentTimeMillis(), dayTime.gmtoff);

        // now we work exclusively in UTC
        dayTime = new Time();

        String[] resultStrs = new String[numDays];
        for (int i = 0; i < resultStrs.length; i++) {
            // For now, using the format "Day, description, hi/low"
            String day;
            String description;
            String highAndLow;

            // Get the JSON object representing the day

            // The date/time is returned as a long.  We need to convert that
            // into something human-readable, since most people won't read "1400356800" as
            // "this saturday".
            long dateTime;
            // Cheating to convert this to UTC time, which is what we want anyhow
            dateTime = dayTime.setJulianDay(julianStartDay + i);
            day = getReadableDateString(dateTime);

            // description is in a child array called "weather", which is 1 element long.
            description = listForecast.get(i).getWeather().get(0).getDescription();//weatherObject.getString(OWM_DESCRIPTION);

            // Temperatures are in a child object called "temp".  Try not to name variables
            // "temp" when working with temperature.  It confuses everybody.
            double high = listForecast.get(i).getMain().getTempMax();// temperatureObject.getDouble(OWM_MAX);
            double low = listForecast.get(i).getMain().getTempMin();//temperatureObject.getDouble(OWM_MIN);

            highAndLow = formatHighLows(high, low);
            resultStrs[i] = day + " - " + description + " - " + highAndLow;
        }

        for (String s : resultStrs) {
            Log.v(TAG, "Forecast entry: " + s);
        }
        return resultStrs;

    }
}
