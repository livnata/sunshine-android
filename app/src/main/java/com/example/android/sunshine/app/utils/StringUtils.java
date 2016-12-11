package com.example.android.sunshine.app.utils;

import android.text.TextUtils;

import com.example.android.sunshine.app.logs.Loggers;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class StringUtils {

  public static boolean isValidPrice(String price) {
    if (StringUtils.isNullOrEmpty(price)) {
      return false;
    }

    price = price.trim();

    Pattern pattern = Pattern.compile("[0-9]{1,4}(\\.[0-9]{1,2})?");

    return pattern.matcher(price).matches();
  }

  public static boolean isNullOrEmpty(String string) {
    // if (string == null)
    // return true;
    //
    // return string.trim().length() == 0;
    return TextUtils.isEmpty(string);
  }

  public static String buildPriceWithCurrency(double price, String currency) {
    // formatter
    DecimalFormat formatter = new DecimalFormat("#0.##");
    formatter.setGroupingSize(3);
    formatter.setGroupingUsed(true);

    // init separator
    DecimalFormatSymbols symbols = formatter.getDecimalFormatSymbols();
    symbols.setGroupingSeparator(' ');
    formatter.setDecimalFormatSymbols(symbols);

    // format price
    String result = formatter.format(price);
    if (isCurrencyInLeft(currency)) {
      result = currency + result;
    } else {
      result = result + " " + currency;
    }
    return result;
  }

  public static boolean isCurrencyInLeft(String currency) {
    if (!StringUtils.isNullOrEmpty(currency)) {
      if (currency.trim().equalsIgnoreCase("руб.")) {
        return false;
      }
    }
    return true;
  }

  public static Double parseDouble(String value) {

    DecimalFormat formatter = (DecimalFormat) DecimalFormat.getInstance();
    formatter.setRoundingMode(RoundingMode.DOWN);

    if (formatter.getDecimalFormatSymbols().getDecimalSeparator() == ',') {
      DecimalFormatSymbols symbols = new DecimalFormatSymbols();
      symbols.setDecimalSeparator('.');
      formatter.setDecimalFormatSymbols(symbols);
    }

    try {
      return formatter.parse(value).doubleValue();
    } catch (ParseException e) {
      Loggers.AppEvent.error("Unable to format {}. error = {}", value, e);
      return null;
    }
  }

  public static String removeTrailingZerosFromDouble(double number) {
    // Format to no more 2 digits after the dot
    DecimalFormat df = new DecimalFormat("###.##");
    return df.format(number);
  }

  public static List<String> splitStringByLength(String source, int length) {
    final int numChunks =
        0 == (source.length() % length) ? source.length() / length : 1 + (source.length() / length);

    final List<String> chunks = new ArrayList<String>(numChunks);

    for (int startIndex = 0; startIndex < source.length(); startIndex += length) {
      final int endIndex = Math.min(source.length(), startIndex + length);
      chunks.add(source.substring(startIndex, endIndex));
    }

    return chunks;
  }

  public static String formatNumberByCurrency(double price, String currency) {
    // formatter
    DecimalFormat formatter = new DecimalFormat("###,###.##");
    formatter.setGroupingSize(3);
    formatter.setGroupingUsed(true);
    DecimalFormatSymbols symbols = formatter.getDecimalFormatSymbols();

    if (isCurrencyInLeft(currency)) {
      symbols.setGroupingSeparator(',');
      symbols.setDecimalSeparator('.');
    } else {
      symbols.setGroupingSeparator(' ');
      symbols.setDecimalSeparator(',');
    }

    formatter.setDecimalFormatSymbols(symbols);

    // format price
    return formatter.format(price);
  }



  public static double convertMeterToFeet(double meters) {
    return meters * 3.2808;
  }
}
