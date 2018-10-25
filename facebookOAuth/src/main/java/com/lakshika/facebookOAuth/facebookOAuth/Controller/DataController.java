package com.lakshika.facebookOAuth.facebookOAuth.Controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.lakshika.facebookOAuth.facebookOAuth.Model.UserData;

@Controller
public class DataController {
	
	ArrayList<UserData> linkList = new ArrayList<>();
	
	@Value("${oauth.endpoint}") 
	String AUTH_ENDPOINT;
	
    @Value("${oauth.responseType}") 
    String RESPONSE_TYPE;
    
    @Value("${oauth.clientId}") 
    String CLIENT_ID;
    
    @Value("${oauth.redirectionUri}") 
    String REDIRECT_URI;
    
    @Value("${oauth.acope}") 
    String SCOPE;
    
    @Value("${oauth.tokenEndpoint}") 
    String TOKEN_ENDPOINT;
    
    @Value("${oauth.grantType}") 
    String GRANT_TYPE;
    
    @Value("${oauth.clientSecret}") 
    String CLIENT_SECRET;
    
	@RequestMapping("/")
	public String MainPage() {
		return "main";
	}
	
	@RequestMapping(value = "/redirect", method = RequestMethod.GET)
	public void getOAuthCode(HttpServletResponse httpServletResponse) {
	    	  	   
	    	   String requestEndpoint = null;
			try {
				requestEndpoint = AUTH_ENDPOINT + "?" +
				            "response_type=" + RESPONSE_TYPE + "&" +
				            "client_id=" + CLIENT_ID + "&" +
				            "redirect_uri=" + URLEncoder.encode(REDIRECT_URI,"UTF-8" ) + "&" +
				            "scope=" + URLEncoder.encode(SCOPE,"UTF-8" );
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}     
		
	        httpServletResponse.setHeader("Location", requestEndpoint);
	        httpServletResponse.setStatus(302);
	 }
	
	@RequestMapping(value="callback", method = RequestMethod.GET)
	public String getAccessToken(@RequestParam("code") String token,Model model) throws ClientProtocolException, IOException, UnsupportedOperationException, JSONException{
	    	    	
	    	
            HttpPost httpPost = new HttpPost(TOKEN_ENDPOINT +
            		"?grant_type=" + GRANT_TYPE +
                    "&code=" + token +
                    "&redirect_uri=" + URLEncoder.encode(REDIRECT_URI,"UTF-8") +
                    "&client_id=" + CLIENT_ID);
            
         
            String clientCredentials = CLIENT_ID + ":" + CLIENT_SECRET;
            String encodedClientCredentials = new String(Base64.encodeBase64(clientCredentials.getBytes()));
     
            httpPost.setHeader("Authorization", "Basic " +encodedClientCredentials);
            
            CloseableHttpClient httpClient = HttpClients.createDefault();
     
            HttpResponse httpResponse = httpClient.execute(httpPost);

   
            Reader reader = new InputStreamReader
                    (httpResponse.getEntity().getContent());
            BufferedReader bufferedReader = new BufferedReader(reader);
            String line = bufferedReader.readLine();
        
    
            final JSONObject obj = new JSONObject(line);
            String accessToken = obj.getString("access_token");
            
       
    		JSONObject albums = new JSONObject(getResourceData("https://graph.facebook.com/v2.8/me/albums",accessToken));
         
    		for (int i = 0; i < albums.getJSONArray("data").length(); i++) {
    	
    		    JSONObject albumobject = albums.getJSONArray("data").getJSONObject(i);
    		
    		    String id = albumobject.getString("id");
    	
    		    JSONObject photos = new JSONObject(getResourceData("https://graph.facebook.com/v2.8/"+id+"/photos",accessToken));
    		    
            	for (int p = 0; p < photos.getJSONArray("data").length(); p++) {
            
        		    JSONObject photos_object = photos.getJSONArray("data").getJSONObject(p);
        	
        		    String p_id = photos_object.getString("id");
        		   
        	
        		    JSONObject photo_object = new JSONObject(getResourceData("https://graph.facebook.com/"+p_id+"?fields=images",accessToken));	    
        		    
                    JSONObject image = photo_object.getJSONArray("images").getJSONObject(1);
        		    String link = image.getString("source");
        		    UserData ud = new UserData();
                    ud.setLinks(link);
               
                    linkList.add(ud);
            	}              
    		}
   
    	model.addAttribute("photos", linkList);

  	    return "userData";    
	    }
	
	public String getResourceData(String url,String accessToken) throws IOException{
    	
		URL urlobj = new URL(url);
		HttpURLConnection con = (HttpURLConnection) urlobj.openConnection();
		con.setRequestMethod("GET");

	
		con.setRequestProperty("Authorization", "Bearer " + accessToken);

		int responseCode = con.getResponseCode();
		System.out.println("\nSending 'GET' request to URL : " + url);
		System.out.println("Response Code : " + responseCode);

		BufferedReader in = new BufferedReader(
		        new InputStreamReader(con.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();

		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();
	
		return response.toString();
}	
}
