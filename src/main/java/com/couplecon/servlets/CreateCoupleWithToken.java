package com.couplecon.servlets;

import java.io.IOException;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.couplecon.data.CoupleRequests;
import com.couplecon.data.Partner;
import com.couplecon.data.RequestFailureJson;
import com.couplecon.util.DB;
import com.couplecon.util.Utils;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebServlet("/create-couple-with-token")
public class CreateCoupleWithToken extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    public CreateCoupleWithToken() {
        super();
    }
    
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {
			String secretPhrase = "a-secret-phrase";
			String isServer = (String) request.getParameter("secretPhrase");
			if(isServer.equals(secretPhrase)) {
				String coupleRequestToken = (String) request.getParameter("coupleRequestToken");
				
				HashMap<String, String> tokenData = DB.getCoupleRequestTokenData(coupleRequestToken);
				
				String awayPartnerId = tokenData.get("partnerId");
				String email_address = tokenData.get("email_address");
				
				Boolean inCouple = DB.partnerInCouple(awayPartnerId);
				String awayPartnerInCouple = null;
				if(inCouple == true) {
					DB.deleteCoupleRequestToken(coupleRequestToken);
					awayPartnerInCouple = "true";
				} else {
					awayPartnerInCouple = "false";
				}
				
				ServletOutputStream o = response.getOutputStream();
				JsonFactory f = new JsonFactory();
				JsonGenerator g = f.createGenerator(o);
				g.writeStartObject();
				g.writeStringField("partnerId", awayPartnerId);
				g.writeStringField("email_address", email_address);
				g.writeStringField("awayPartnerInCouple", awayPartnerInCouple);
				g.writeEndObject();
				g.close();
				o.close();
				
					
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServletException(e);
		}
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {
			String jsonString = request.getParameter("data");
			String coupleRequestToken = request.getParameter("coupleRequestToken");
			
			HashMap<String, String> tokenData = DB.getCoupleRequestTokenData(coupleRequestToken);
			
			String awayPartnerId = tokenData.get("partnerId");
			Boolean awayPartnerInCouple = DB.partnerInCouple(awayPartnerId);
			
			String toBeRegisteredEmail = tokenData.get("email_address");

			ObjectMapper objectMapper = new ObjectMapper();
			Partner partner = objectMapper.readValue(jsonString, Partner.class);
			
			// this sets their email address to the one sent via token
			partner.setEmailAddress(toBeRegisteredEmail);
			
			String partnerId = DB.createPartner(partner);
			if(partnerId == null) {
				// Error: Creating Account
			    response.setStatus(500);
				return;
			}
			
			// what if they didn't sign up with the same email address, we dont want to verify that one.
			// this should never happen, right now, we dont allow them to change email addresses
			if(toBeRegisteredEmail == partner.getEmailAddress()) {
				DB.setPartnerEmailIsVerified(partnerId, 1);
			}
			String loginToken = DB.generateLoginToken(partnerId);

			if(awayPartnerInCouple == false) {
				Boolean coupleCreated = DB.createCouple(partnerId, awayPartnerId);
				if(coupleCreated == false) {
					// Account Created, Error: Creating Couple
					response.setStatus(500);
					return;
				}			
				String coupleId = DB.getCoupleId(partnerId);
			}
			
			DB.deleteCoupleRequestToken(coupleRequestToken);
			
			ServletOutputStream o = response.getOutputStream();
			JsonFactory f = new JsonFactory();
			JsonGenerator g = f.createGenerator(o);
			g.writeStartObject();
			g.writeStringField("token", loginToken);
			g.writeStringField("partnerId", partnerId);
			g.writeEndObject();
			g.close();
			o.close();
			
//			System.out.println("--- EMAIL TOKEN DEBUG ---");
//			System.out.println("Existing Partner ID: "+awayPartnerId);
//			System.out.println("New Partner ID: "+partnerId);
//			System.out.println("New Couple ID: "+coupleId);
//			System.out.println("New Login Token: "+loginToken);
//			System.out.println("Couple Request Token: "+coupleRequestToken);
//			System.out.println("--- END EMAIL TOKEN ---");
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServletException(e);
		}
	}

}
