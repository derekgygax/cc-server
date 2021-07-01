package com.couplecon.servlets;

import java.io.IOException;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.couplecon.data.SurveyQuestion;
import com.couplecon.util.DB;
import com.couplecon.util.Utils;
import com.couplecon.data.Couple;
import com.couplecon.data.JsonMatch;
import com.couplecon.data.Match;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebServlet("/matches/*")
public class GetMatchStatus extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    public GetMatchStatus() {
        super();
    }
    
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {
			String partnerId = (String) request.getAttribute("partnerId");
			String homeCoupleId = DB.getCoupleId(partnerId);
			String awayCoupleId = request.getPathInfo().split("/")[1];
			Match match = null;
			if (!DB.getBlocks(homeCoupleId).contains(awayCoupleId)) {
				match = DB.getMatch(homeCoupleId, awayCoupleId);
			}
			if (match != null) {
				JsonMatch jsonMatch = match.getJsonMatch(partnerId);
				ObjectMapper objectMapper = new ObjectMapper();
				response.getWriter().write(objectMapper.writeValueAsString(jsonMatch));
			} else {
				response.setStatus(HttpServletResponse.SC_NOT_FOUND);
			}
		} catch (Exception e){
	    	e.printStackTrace();
	    	throw new ServletException(e);
	    }
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {
			String partnerId = (String) request.getAttribute("partnerId");
			String awayCoupleId = request.getPathInfo().split("/")[1];
			if (DB.getBlocks(DB.getCoupleId(partnerId)).contains(awayCoupleId)) {
				response.setStatus(HttpServletResponse.SC_NOT_FOUND);
				return;
			}
			String jsonString = Utils.getRequestContent(request);
			ObjectMapper objectMapper = new ObjectMapper();
			JsonNode json = objectMapper.readTree(jsonString);
			String approve = json.get("approve").asText();
			DB.approveProfile(partnerId, awayCoupleId, approve);
			doGet(request, response);
		} catch (Exception e){
	    	e.printStackTrace();
	    	throw new ServletException(e);
	    }
	}

}
