package com.lakshika.facebookOAuth.facebookOAuth.Controller;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

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
    
	@RequestMapping("/")
	public String MainPage() {
		return "main";
	}
	
	@RequestMapping(value = "/redirect", method = RequestMethod.GET)
	public void getAuthCode(HttpServletResponse httpServletResponse) {
	    	  	   
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
}
