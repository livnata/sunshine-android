package com.example.android.sunshine.app;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.Time;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.android.sunshine.app.api.retrofit.IForecastApi;
import com.example.android.sunshine.app.api.retrofit.ProtocolRetrofit;
import com.example.android.sunshine.app.loaders.ForecastLoader;
import com.example.android.sunshine.app.model.ArrayListForecast;
import com.example.android.sunshine.app.model.ForecastModel;
import com.example.android.sunshine.app.utils.DependencyInjector;

import org.json.JSONException;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

import retrofit2.Retrofit;

/**
 * A placeholder fragment containing a simple view.
 */
public class ForecastFragment extends Fragment implements
        LoaderManager.LoaderCallbacks<String[]> {

    private static final String TAG = "ForecastFragment";
    //    String Static TAG = Class.
    ArrayList<DateTimeItem> mArrayListDateTime;
    WeatherRecyclerViewAdapter mWheatherAdapter;
    RecyclerView mRecyclerView;
    IForecastApi mIForecastApi;
    private static final int FORECAST_LOADER_ID = 100;


    // API key 30bf6f95b393edde7a5ae63bc68db92b
//    &APPID={30bf6f95b393edde7a5ae63bc68db92b}

    //Katherine's solution: http://api.openweathermap.org/data/2.5/forecast/daily?q=94043&mode=json&units=metric&cnt=7


    public ForecastFragment() {


    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View viewResult;
        setHasOptionsMenu(true);
//        mIForecastApi = createProtocol();

        viewResult = inflater.inflate(R.layout.fragment_main, container, false);
        mArrayListDateTime = new ArrayList<>();


//        mArrayListDateTime.add(new DateTimeItem("Mon 6/23 - Sunny - 31/17"));
//        mArrayListDateTime.add(new DateTimeItem("Mon 6/23 - Sunny - 31/17"));
//        mArrayListDateTime.add(new DateTimeItem("Mon 6/23 - Sunny - 31/17"));
//        mArrayListDateTime.add(new DateTimeItem("Mon 6/23 - Sunny - 31/17"));


        mWheatherAdapter = new WeatherRecyclerViewAdapter(getActivity(), mArrayListDateTime);

//        RecyclerView mRecyclerView = new RecyclerView(mContext);
        mRecyclerView = (RecyclerView) viewResult.findViewById(R.id.recycler_view);
        mRecyclerView.setAdapter(mWheatherAdapter);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));// what is the perepes of the last parameter

        return viewResult;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        createProtocol();
        getLoaderManager().initLoader(FORECAST_LOADER_ID, null, this);

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.forecastfregment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement

        if (id == R.id.action_refresh) {
            getLoaderManager().restartLoader(FORECAST_LOADER_ID, null, this);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<String[]> onCreateLoader(int id, Bundle args) {
        return new ForecastLoader(getContext(), 524901);
    }

    @Override
    public void onLoadFinished(Loader<String[]> loader, String[] result) {
        if (result != null) {
            for (String dayForecastStr : result) {
                mArrayListDateTime.add(new DateTimeItem(dayForecastStr));
            }
            mWheatherAdapter = new WeatherRecyclerViewAdapter(getContext(), mArrayListDateTime);
            mRecyclerView.setAdapter(mWheatherAdapter);
        } else {
            Toast.makeText(getContext(), "Failed to fetch data!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onLoaderReset(Loader<String[]> loader) {
        // The use in this method is to clean data if I want to
        Log.d(TAG,"onLoaderReset");
    }


    protected IForecastApi createProtocol() {
        if (DependencyInjector.getProtocol() == null) {
            return   ProtocolRetrofit.createInstance("http:////api.openweathermap.org//", "", getContext());
        }else
            return DependencyInjector.getProtocol();
    }
    public class FetchWeatherTask extends AsyncTask<String, Void, String[]> {

        private final String TAG = "FetchWeatherTask";
        Retrofit retrofit;
        String[] mForecastList = null;
        ForecastModel mForecastModel = null;

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


        @Override
        protected String[] doInBackground(String... params) {

            try {

                if (params.length == 0) {
                    return null;
                }
                int cityId = Integer.valueOf(params[0]);


//
                mForecastModel = mIForecastApi.get5DaysForecast(cityId, "metric", 7, "30bf6f95b393edde7a5ae63bc68db92b");
                ArrayListForecast listForecast = mForecastModel.getList();
                try {
                    mForecastList = getWeatherDataFromJson(listForecast, 5);
                } catch (JSONException t) {
                    Log.d(TAG, "problem during pars and String array creation " + t.getMessage());
                }
                Log.d(TAG, "got response : " + mForecastModel.toString());
            } catch (Exception e) {
                e.printStackTrace();
            }


            return mForecastList;
        }


        @Override
        protected void onPostExecute(String[] result) {

            if (result != null) {
                for (String dayForecastStr : result) {
                    mArrayListDateTime.add(new DateTimeItem(dayForecastStr));
                }
                mWheatherAdapter = new WeatherRecyclerViewAdapter(getContext(), mArrayListDateTime);
                mRecyclerView.setAdapter(mWheatherAdapter);
            } else {
                Toast.makeText(getContext(), "Failed to fetch data!", Toast.LENGTH_SHORT).show();
            }
        }


    }
}