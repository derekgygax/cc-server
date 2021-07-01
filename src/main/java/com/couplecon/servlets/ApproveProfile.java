package com.couplecon.servlets;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.couplecon.util.DB;
import com.couplecon.util.Utils;

@WebServlet("/approve-profile")
public class ApproveProfile extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    public ApproveProfile() {
        super();
    }
    
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {
			String homePartnerId = (String) request.getAttribute("partnerId");
			String jsonString = Utils.getRequestContent(request);
			ObjectMapper objectMapper = new ObjectMapper();
			JsonNode json = objectMapper.readTree(jsonString);
			String awayCoupleId = json.get("awayCoupleId").asText();
			String approve = json.get("approve").asText();
			DB.approveProfile(homePartnerId, awayCoupleId, approve);
			String matchStatus = DB.getMatchStatusByPartner(homePartnerId, awayCoupleId);
			response.getWriter().write(matchStatus);
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServletException(e);
		}
	}
}
