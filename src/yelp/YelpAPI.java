package yelp;

import org.json.JSONArray;
import org.json.JSONObject;
import org.scribe.builder.ServiceBuilder;
import org.scribe.model.OAuthRequest;
import org.scribe.model.Response;
import org.scribe.model.Token;
import org.scribe.model.Verb;
import org.scribe.oauth.OAuthService;

public class YelpAPI {
	private static final String API_HOST = "api.yelp.com";
	private static final String DEFAULT_TERM = "dinner";
	private static final int SEARCH_LIMIT = 20;
	private static final String SEARCH_PATH = "/v2/search";
	private static final String CONSUMER_KEY = "m7DToyGC8fkEavGL9ZmJtA";
	private static final String CONSUMER_SECRET = "yuC5XfYbUjTLG_bXoUagk9d2e_o";
	private static final String TOKEN = "a2xNmgb8Z8zQRZNo5dxaDgBHFmB4vu8H";
	private static final String TOKEN_SECRET = "hkBgoqFVRrz_Sxvi6rRHINRT8pw";
	OAuthService service;
	Token accessToken;

	/**
	 * Setup the Yelp API OAuth credentials.
	 */
	public YelpAPI() {
		this.service = new ServiceBuilder().provider(TwoStepOAuth.class).apiKey(CONSUMER_KEY).apiSecret(CONSUMER_SECRET)
				.build();
		this.accessToken = new Token(TOKEN, TOKEN_SECRET);
	}

	/**
	 * Creates and sends a request to the Search API by term and location.
	 */
	public String searchForBusinessesByLocation(double lat, double lon) {
		OAuthRequest request = new OAuthRequest(Verb.GET, "http://" + API_HOST + SEARCH_PATH);
		request.addQuerystringParameter("term", DEFAULT_TERM);
		request.addQuerystringParameter("ll", lat + "," + lon);
		request.addQuerystringParameter("limit", String.valueOf(SEARCH_LIMIT));
		System.out.println(request);
		return sendRequestAndGetResponse(request);
	}

	/**
	 * Sends an {@link OAuthRequest} and returns the {@link Response} body.
	 */
	private String sendRequestAndGetResponse(OAuthRequest request) {
		System.out.println("Querying " + request.getCompleteUrl() + " ...");
		this.service.signRequest(this.accessToken, request);
		Response response = request.send();
		return response.getBody();
	}

	/**
	 * Queries the Search API based on the command line arguments and takes the
	 * first result to query the Business API.
	 */
	private static void queryAPI(YelpAPI yelpApi, double lat, double lon) {
		String searchResponseJSON = yelpApi.searchForBusinessesByLocation(lat, lon);
		JSONObject response = null;
		try {
			response = new JSONObject(searchResponseJSON);
			JSONArray businesses = (JSONArray) response.get("businesses");
			for (int i = 0; i < businesses.length(); i++) {
				JSONObject business = (JSONObject) businesses.get(i);
				System.out.println(business);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Main entry for sample Yelp API requests.
	 */
	public static void main(String[] args) {
		YelpAPI yelpApi = new YelpAPI();
		queryAPI(yelpApi, 37.38, -122.08);
	}
}

// Yelp API v2 was deprectaed on June 30, 2018, v3 is currently in use.

// https://www.yelp.com/developers/documentation/v3/authentication

// public String searchForBusinessesByLocation(double lat, double lon) throws Exception {
// 	OkHttpClient client = new OkHttpClient.Builder().build();
	
// 	HttpUrl.Builder urlBuilder = HttpUrl.parse("https://api.yelp.com/v3/businesses/search?term=restaurants").newBuilder();
// 	urlBuilder.addQueryParameter("latitude", lat + "");
// 	urlBuilder.addQueryParameter("longitude", lon + "");
// 	urlBuilder.addQueryParameter("limit", String.valueOf(SEARCH_LIMIT));
// 	String url = urlBuilder.build().toString();

// 	Request request= new Request.Builder().url(url).header("Authorization", "Bearer " + API_KEY).build();
// 	Call call = client.newCall(request);
// 	return call.execute().body().string();
// }

// Also the structure of JSON output was modified, so are the names of some attributes.
// Therefore some modifications are needed under /src/model/Restaurant/java
