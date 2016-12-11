package com.example.android.sunshine.app.api;

import com.google.gson.annotations.SerializedName;

/**
 */
public class ResponseTemplate {
  // ------------------------------ FIELDS ------------------------------

  @SerializedName("httpCode") private int httpCode;

  @SerializedName("code") private int code;

  @SerializedName("throwable") private Throwable throwable;

  @SerializedName("user_message") private String userMessage;

  @SerializedName("error_message") private String errorMessage;

  // -------------------------- OTHER METHODS --------------------------

  public String getErrorMessage() {
    return errorMessage;
  }

  public void setErrorMessage(String errorMessage) {
    this.errorMessage = errorMessage;
  }

  public String getUserMessage() {
    return userMessage;
  }

  public void setUserMessage(String userMessage) {
    this.userMessage = userMessage;
  }

  public Throwable getThrowable() {
    return throwable;
  }

  public void setThrowable(Throwable throwable) {
    this.throwable = throwable;
  }

  public boolean ok() {
    return throwable == null && (getHttpCode() == 200 || getHttpCode() == 204);
  }

  public int getHttpCode() {
    return httpCode;
  }

  public void setHttpCode(int httpCode) {
    this.httpCode = httpCode;
  }

  public int getCode() {
    return code;
  }

  public void setCode(int code) {
    this.code = code;
  }
}
