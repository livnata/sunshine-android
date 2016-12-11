package com.example.android.sunshine.app.api;

import org.json.JSONObject;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.UUID;

/**
 * Contains all function names
 * 
 */
public class ApiFunction {

	public static final String api_ver = "2.5";
	public static final String forecast5Days = "data/2_9/forecast?q={city_name},{country_code}";//




//	" http://api.openweathermap.org";// base url
	private String _url;
	private int _method;
	private JSONObject _params = null;
	private UUID requestId;



	public ApiFunction(String baseUrl, String query, int method, JSONObject params) {
		this._url = baseUrl + query;
		this._url = this.appendLocale(this._url);
		this.requestId= UUID.randomUUID(); 
		// signature part
		
		String key = null;
//		if (!jsystem.utils.StringUtils.isEmpty(key)) {
//			this._url = this.appendSignature(this._url, key);
//			//this._url = this._url.replace("http://", "https://");
//		}
		if (key ==null){
			key="30bf6f95b393edde7a5ae63bc68db92b";
			this._url = this.appendSignature(this._url, key);
		}
		this.setMethod(method);
		this._params = params;
	}
	
	public String getUrl() {
		return _url;
	}
	
	public String getRequestId(){
		return requestId.toString();
	}

	public String getParams() {
		if (_params == null)
			return "";
		else
			return _params.toString();
	}

	public int getMethod() {
		return _method;
	}

	public void setMethod(int method) {
		_method = method;
	}

	private String appendLocale(String url) {
		if (url.contains("?")) {
			return url + "&lc=" + getLanguage();
		} else {
			return url + "?lc=" + getLanguage();
		}
	}

	private String appendSignature(String url, String key) {
		url = url + (url.contains("?") ? "&" : "?");
		// append salt
		long ts = toTimestamp(getCalendar().getTime());
		url = url + "salt=" + String.valueOf(ts);
		String sub_url = "";
		if(url.indexOf("/server/") > 0){
			sub_url = url.substring(url.indexOf("/server/"));
		}
		System.out.println("string for signature is: " + sub_url + key);
		String hash =sub_url + key;
		return url += "&APPID=" + hash;
	}
	
	public Calendar getCalendar() {
		TimeZone zone = TimeZone.getDefault();
		int offset = zone.inDaylightTime(Calendar.getInstance().getTime()) ? 60 * 60 * 1000 : 0;
//		zone.setRawOffset(Settings.getInstance().getServerTimeZone() * 1000 - offset);
		zone.setRawOffset(1 * 1000 - offset);
		return Calendar.getInstance(zone, Locale.US);
	}
	
	public long toTimestamp(Date date) {
		if (date == null) {
			return 0;
		}
		return date.getTime() / 1000;
	}
	
	public String getLanguage() {
		String language = Locale.getDefault().getLanguage().substring(0, 2);

		return language;
	}
}
