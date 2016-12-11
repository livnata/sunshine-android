package com.example.android.sunshine.app.api.retrofit;


import com.example.android.sunshine.app.logs.Loggers;
import com.example.android.sunshine.app.utils.StringUtils;

import java.io.IOException;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.Buffer;

/**
 * Used to log requests and responses on the network layer.
 * will log the method, url and body.
 *
 * If request Body logging is not necessary call should use the "Logging-Level-Request: no-body"
 * header
 * If response Body logging is not necessary call should use the "Logging-Level-Response: no-body"
 * header
 */
public class LoggingInterceptor implements Interceptor {

  // TODO: Replace it with more appropriate mechanism after square guys make the following changes:
  // https://github.com/square/retrofit/issues/1496
  static final String LOGGING_LEVEL_REQUEST_WITHOUT_BODY = "Logging-Level-Request: no-body";

  static final String LOGGING_LEVEL_RESPONSE_WITHOUT_BODY = "Logging-Level-Response: no-body";

  @Override public Response intercept(Chain chain) throws IOException {
    String requestId = UUID.randomUUID().toString().substring(0, 5);

    Request request = chain.request();
    // TODO: In some point Retrofit guys might add a way to diffrenciate between calls in the interceptors (https://github.com/square/retrofit/issues/1496)
    // TODO: until then, check for the Logging-Level header to decide if need to print body.
    //
    String printRequestHeader = request.header("Logging-Level-Request");
    String printResponseHeader = request.header("Logging-Level-Response");
    request = request.newBuilder().removeHeader("Logging-Level-Request").
        removeHeader("Logging-Level-Response").build();

    Loggers.Network.info("---> {} {} | reqId : {}", request.method(), request.url(), requestId);

    boolean printRequestBody = printRequestHeader == null || !printRequestHeader.equals("no-body");

    if (request.body() != null && printRequestBody) {
      String bodyString = requestBodyToString(request.body());
      logBody(bodyString);
    }

    long startNs = System.nanoTime();
    Response response = chain.proceed(request);
    long tookMs = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startNs);

    Loggers.Network.info("<--- {} {} | reqId : {} | took ({}ms)", response.code(), request.url(),
        requestId, tookMs);

    boolean printResponseBody =
        printResponseHeader == null || !printResponseHeader.equals("no-body");
    // TODO: untill "Content-Length" header is not part of the response, read 8192 bytes, can also use Long.MAX_VALUE
    // TODO: after server guys pass the "Content-Length" uncomment the commented lines and delete the unnecessary ones.
    //if (printResponseBody && response.body().contentLength() > 0) {
    if (printResponseBody) {
      //String responseString = response.peekBody(response.body().contentLength()).string();
      String responseString = response.peekBody(8192).string();
      logBody(responseString);
    }
    return response;
  }

  String requestBodyToString(RequestBody body) throws IOException {
    Buffer buffer = new Buffer();
    body.writeTo(buffer);
    return buffer.readUtf8();
  }

  void logBody(String body) {
    List<String> chunks = StringUtils.splitStringByLength(body, 3000);

    for (int i = 0; i < chunks.size(); i++) {

      String chunk = chunks.get(i);

      if (i == 0) {
        Loggers.Network.info("body: {}", chunk);
      } else if (i < chunks.size()) {
        Loggers.Network.info(chunk);
      }
    }
  }
}
