package com.example.android.sunshine.app.api.retrofit;

import android.content.Context;

import com.example.android.sunshine.app.api.ResponseTemplate;
import com.example.android.sunshine.app.api.ServiceApi;
import com.example.android.sunshine.app.logs.Loggers;
import com.example.android.sunshine.app.model.ForecastModel;
import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.lang.ref.WeakReference;

import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Converter;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


/**
 * Created by livnatavikasis on 27/11/2016.
 */
public class ProtocolRetrofit implements IForecastApi {

    private ServiceApi mServiceApi;
    private Gson mGson;// for covertor factory
    private String mGTBaseUrl;
    private String mRegionName;
    private WeakReference<Context> mWeakContext;
    private IForecastApi mIForecastApi;
    private LoggingInterceptor mLoggingInterceptor;
    private static volatile ProtocolRetrofit sProtocolRetrofit;// one instance


    public ProtocolRetrofit( String GTBaseUrl, String regionName, Context context) {
//        mGson = gson;
        mGTBaseUrl = GTBaseUrl;
        mRegionName = regionName;
        mWeakContext = new WeakReference<>(context);
        mGson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .create();
        initServiceApi();

    }

    public synchronized static ProtocolRetrofit getInstance() {
        return sProtocolRetrofit;
    }

    public synchronized static ProtocolRetrofit createInstance( String baseUrl, String regionName, Context context) {
        if (sProtocolRetrofit != null) {
            Loggers.Network.error("ProtocolRetrofit can be created only once");
            throw new IllegalStateException("ProtocolRetrofit can be created only once");
        }

        sProtocolRetrofit = new ProtocolRetrofit( baseUrl, regionName,context);

        return sProtocolRetrofit;
    }

    public void initServiceApi(){
//        LoggingInterceptor logging = new LoggingInterceptor();
        // set your desired log level
//        logging.setLevel(LoggingInterceptor.Level.BODY);
        mLoggingInterceptor = new LoggingInterceptor();

        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        // add your other interceptors …
        // add logging as last interceptor
        httpClient.addInterceptor(mLoggingInterceptor);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(mGTBaseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .client(httpClient.build())
                .build();

        mServiceApi = retrofit.create(ServiceApi.class);
    }

    private <T extends ResponseTemplate> T execute(final Call<T> call, final Class<T> clazz) {
        T response;
        try {
            response = clazz.newInstance();
        } catch (InstantiationException e) {
            Loggers.Network.error("Unable to instantiate Class {}", clazz, e);
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            Loggers.Network.error("Unable to instantiate Class {}", clazz, e);
            throw new RuntimeException(e);
        }

        try {

            Response<T> retrofitResponse = call.execute();

            if (retrofitResponse.isSuccessful()) {
                if (retrofitResponse.body() != null) {
                    response = retrofitResponse.body();
                }

                response.setHttpCode(retrofitResponse.code());
            } else {
                //TODO: Check whether retrofit guys came up with better solution for error
                // converters : https://github.com/square/retrofit/issues/1564
                T tempResponse = parseErrorBody(retrofitResponse.errorBody(), clazz);
                response = tempResponse != null ? tempResponse : response;
                response.setHttpCode(retrofitResponse.code());

                // check for 401 - this is handled as an application level event
//                handleUnauthorized(response, retrofitResponse);
            }
        } catch (IOException e) {
            response.setThrowable(e);

            Loggers.Network.error(e.toString());
        } catch (Exception e) {
            response.setThrowable(e);
        }

        return response;
    }

    private <T extends ResponseTemplate> T parseErrorBody(ResponseBody errorBody,
                                                          final Class<T> clazz) throws IOException {
        T response = null;
        if (errorBody != null) {
            Converter<ResponseBody, T> errorConverter =
                    new GTResponseGsonConverter<>(mGson.getAdapter(clazz));

            response = errorConverter.convert(errorBody);
        }

        return response;
    }


    /**
     * Active order.
     *
     * @return the order response
     */
    @Override
    public ForecastModel get5DaysForecast(int cityId,String metric,int cnt,String AppId) {
        retrofit2.Call<ForecastModel> call = mServiceApi.get5DaysForecast( cityId, metric, cnt, AppId);
        return execute(call, ForecastModel.class);
    }


}
