package com.zapstitch;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.json.JSONArray;
import org.json.JSONObject;

import com.google.api.client.googleapis.apache.GoogleApacheHttpTransport;
import com.google.api.client.http.ByteArrayContent;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.HttpTransport;

public class OauthClient {

	public static final HttpRequestFactory requestFactory = getResourceFactory();

	private String accessToken;
	private String emailAddress;
	private static ConcurrentHashMap<String,Integer> completeEmailReport  = new ConcurrentHashMap<String,Integer>();

	public OauthClient(String accessToken) {
		this.accessToken = accessToken;		
		this.emailAddress = getEmailAddress();
	}

	public static synchronized void addTocompleteEmailReport(String finalemail, int i){
		completeEmailReport.put(finalemail,i);
	}

	public static ConcurrentHashMap<String, Integer> getCompleteEmailReport() {
		return completeEmailReport;
	}

	public static HttpRequestFactory getResourceFactory() {

		try {
			HttpTransport transport = GoogleApacheHttpTransport.newTrustedTransport();
			HttpRequestFactory requestFactory = transport.createRequestFactory();
			return requestFactory;
		} catch(Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static  String  getAccessToken(String authCode) {
		try {
			String postUrl = "https://www.googleapis.com/oauth2/v3/token";
			GenericUrl url = new GenericUrl(postUrl.replaceAll(" ", "%20"));
			String requestBody = "code=" + authCode + "&redirect_uri=http://localhost:8080/Zapstitch/success.jsp&client_id=364702885185-d1im0f76lp02olg88rlgcqdigqcs53qb.apps.googleusercontent.com&client_secret=trKASa5aBxWyGFHTrq88NSl0&grant_type=authorization_code";
			HttpRequest request = requestFactory.buildPostRequest(url, ByteArrayContent.fromString(null, requestBody));
			request.getHeaders().setContentType("application/x-www-form-urlencoded");
			com.google.api.client.http.HttpResponse response = request.execute();
			JSONObject myObject = getJsonFromResponse(response);
			String access_token = myObject.getString("access_token");
			return access_token;
		} catch(Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public String getEmailAddress() {
		try {
			String getUrl = "https://www.googleapis.com/gmail/v1/users/me/profile?access_token=" + this.accessToken;
			JSONObject json = getRequest(getUrl);
			if(null!=json)
				return json.getString("emailAddress");
		} catch(Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public void getEmails(String endDate, String startDate) {
		try {

			String getUrl = "https://www.googleapis.com/gmail/v1/users/me/messages?q=\"in:all%20after:" + startDate + "%20before:" + endDate + "\""+ "&access_token="+ this.accessToken;
			System.out.println(getUrl);
			String nextPageToken=null;
			do {
				if(null!=nextPageToken) { 
					getUrl = "https://www.googleapis.com/gmail/v1/users/me/messages?pageToken=" + nextPageToken +"&access_token="+ this.accessToken;
					nextPageToken = null;
				}
				JSONObject json = getRequest(getUrl);
				JSONArray messages = (JSONArray) json.get("messages");
				if(null!=messages) {
					for(int i=0;i<messages.length();i++) {
						System.out.print("=");
						JSONObject current = messages.getJSONObject(i);
						String id = current.getString("id");
						updateEmailCounters(id);
					}
				}
				if(json.has("nextPageToken"))
					nextPageToken = json.getString("nextPageToken");
			}
			while(null!=nextPageToken);
			//removing own email address counter from complete email report
			completeEmailReport.remove(this.emailAddress);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	public void updateEmailCounters(String messageId) {

		try {

			String url = "https://www.googleapis.com/gmail/v1/users/me/messages/" + messageId + "?format=metadata&access_token="+ this.accessToken; 	
			JSONObject json = getRequest(url);
			processEMail(json);
		}catch(Exception e) {
			System.out.println(e.getLocalizedMessage());
		}

	}

	public void processEMail(JSONObject json) {
		if(null!=json) {
			JSONObject payload =  (JSONObject) json.get("payload");
			if(null!=payload) {
				JSONArray headers = (JSONArray) payload.get("headers");
				if(null!=headers) {
					for(int i=0;i<headers.length();++i) {
						JSONObject jsonObject = headers.getJSONObject(i);
						String name = (String) jsonObject.getString("name");
						if(name.equals("From") || name.equals("To") || name.equals("Cc") || name.equals("Bcc")) {
							String value = jsonObject.getString("value");
							for(String emailFullName : value.split(",")) {
								String finalemail;
								int startIndex = emailFullName.indexOf('<');
								if(startIndex!=-1) {
									int endIndex = emailFullName.indexOf('>');
									finalemail = emailFullName.substring(startIndex+1,endIndex).trim();
								}
								else
									finalemail = emailFullName.trim();
								if(finalemail.contains("@")) {
									if(completeEmailReport.containsKey(finalemail)) {
										int counter = completeEmailReport.get(finalemail);
										addTocompleteEmailReport(finalemail, counter+1);
									}
									else
										addTocompleteEmailReport(finalemail, 1);
								}
							}


						}

					}
				}

			}
		}
	}

	public JSONObject getRequest(String getUrl) {
		try {
			GenericUrl url = new GenericUrl(getUrl.replaceAll(" ", "%20"));
			HttpRequest request = requestFactory.buildGetRequest(url);
			com.google.api.client.http.HttpResponse response = request.execute();
			JSONObject json =  getJsonFromResponse(response);
			if(response.getStatusCode()==401)
				System.out.println("Wrong url = " + url);
			response.disconnect();
			return json;
		}
		catch(Exception e) {
			e.printStackTrace();
			System.out.println("Wrong url = " + getUrl);
		}
		return null;
	}
	public static JSONObject getJsonFromResponse(HttpResponse response) {

		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(response.getContent(), "UTF-8"));
			StringBuilder builder = new StringBuilder();
			for (String line = null; (line = reader.readLine()) != null;) {
				builder.append(line).append("\n");
			}
			return new JSONObject(builder.toString());
		} catch(Exception e) {
			e.printStackTrace();
		}
		return null;

	}
	public void driverClass(String startDate, String endDate){
		getEmails(startDate,endDate);
	}
}
