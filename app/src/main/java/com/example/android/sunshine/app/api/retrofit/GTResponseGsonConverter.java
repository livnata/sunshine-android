package com.example.android.sunshine.app.api.retrofit;

import com.example.android.sunshine.app.logs.Loggers;
import com.google.gson.TypeAdapter;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Converter;

public class GTResponseGsonConverter<T> implements Converter<ResponseBody, T> {

  private final TypeAdapter<T> adapter;

  GTResponseGsonConverter(TypeAdapter<T> adapter) {
    this.adapter = adapter;
  }

  @Override public T convert(ResponseBody value) throws IOException {
    String body = value.string();
    try {
      JSONObject responsePayload;
      responsePayload = new JSONObject(body);
      JSONObject dataJsonPart = responsePayload.optJSONObject("data");
      if (dataJsonPart != null && dataJsonPart.length() == 0) {
        responsePayload.put("data", null);
      }
      return adapter.fromJson(responsePayload.toString());
    } catch (JSONException e) {
      Loggers.Network.error("Unable to create json object from {}. error={}", body, e);
    } catch (Exception e) {
      Loggers.Network.error("got exception: {}", e);
    }

    return null;
  }
}
