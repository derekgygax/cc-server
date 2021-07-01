package com.couplecon.servlets;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.couplecon.data.CoupleRequests;
import com.couplecon.data.Partner;
import com.couplecon.data.RequestFailureJson;
import com.couplecon.util.DB;
import com.couplecon.util.SendEmail;
import com.couplecon.util.Utils;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebServlet("/create-couple")
public class CreateCouple extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    public CreateCouple() {
        super();
    }
    
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {
			String partnerId = (String) request.getAttribute("partnerId");
			CoupleRequests cr = DB.getAllCoupleRequests(partnerId);
			ObjectMapper objectMapper = new ObjectMapper();
			response.getWriter().write(objectMapper.writeValueAsString(cr.getJsonObject()));
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServletException(e);
		}
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {
			String reqPartnerId = (String) request.getAttribute("partnerId");
			String jsonString = Utils.getRequestContent(request);
			ObjectMapper objectMapper = new ObjectMapper();
			objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
			Partner awayPartner = objectMapper.readValue(jsonString, Partner.class);
			String resPartnerId = null;

			JsonNode json = objectMapper.readTree(jsonString);
			String action = json.get("action").asText().toLowerCase();
			
			if (awayPartner.getPartnerId() != null) {
				resPartnerId = awayPartner.getPartnerId();
			} else if (awayPartner.getEmailAddress() != null) {
				resPartnerId = DB.getPartnerId(awayPartner.getEmailAddress());
			}
			
			if ((resPartnerId == null || resPartnerId.equals("")) && action.equals("send")){
				// send them an email and lets make a token for this. web app has to account for CoupleRequests and CoupleRequestTokens sent out
				String toBeRegisteredEmail = awayPartner.getEmailAddress();
				if (toBeRegisteredEmail == null){
					toBeRegisteredEmail = awayPartner.getEmailAddress();
				}
				String token = DB.createCoupleRequestToken(reqPartnerId, toBeRegisteredEmail);
				SendEmail.coupleRequestEmail(reqPartnerId, token, toBeRegisteredEmail);
				return;
			} else if ((resPartnerId == null || resPartnerId.equals("")) && action.equals("revoke")){
				int tokensDeleted = DB.deleteCoupleRequestTokenByRequestingPartnerId(reqPartnerId);
				if (0 == tokensDeleted){
					Utils.sendRequestFailureJson(response, RequestFailureJson.SC_CR_NO_SUCH_CR);
				}
				return;
			}
			
			switch (action) {
			case "send":
				if (DB.partnerInCouple(resPartnerId)) {
					Utils.sendRequestFailureJson(response, RequestFailureJson.SC_CR_TO_MATCHED_PARTNER);
				} else {
					DB.createCoupleRequest(reqPartnerId, resPartnerId);
					response.getWriter().write(resPartnerId);
				}
				break;
			case "revoke":
				if (DB.coupleRequestExists(reqPartnerId, resPartnerId)) {
					DB.deleteCoupleRequest(reqPartnerId, resPartnerId);
				} else {
					Utils.sendRequestFailureJson(response, RequestFailureJson.SC_CR_NO_SUCH_CR);
				}
				break;
			case "accept":
				if (DB.coupleRequestExists(resPartnerId, reqPartnerId)) {
					DB.createCouple(reqPartnerId, resPartnerId);
					DB.deleteCoupleRequest(reqPartnerId, resPartnerId);
					DB.deleteCoupleRequest(resPartnerId, reqPartnerId);
					response.getWriter().write(Utils.getCoupleId(reqPartnerId, resPartnerId));
				} else {
					Utils.sendRequestFailureJson(response, RequestFailureJson.SC_CR_NO_SUCH_CR);
				}
				break;
			case "decline":
				if (DB.coupleRequestExists(resPartnerId, reqPartnerId)) {
					DB.deleteCoupleRequest(resPartnerId, reqPartnerId);
				} else {
					Utils.sendRequestFailureJson(response, RequestFailureJson.SC_CR_NO_SUCH_CR);
				}
				break;
			default:
				Utils.sendRequestFailureJson(response, RequestFailureJson.SC_NO_SUCH_ACTION);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServletException(e);
		}
	}

}
