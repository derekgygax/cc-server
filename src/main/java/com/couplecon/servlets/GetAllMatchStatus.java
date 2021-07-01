package com.couplecon.servlets;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.couplecon.data.JsonMatch;
import com.couplecon.data.Match;
import com.couplecon.data.MatchSearchParameters;
import com.couplecon.util.BlockedIds;
import com.couplecon.util.DB;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebServlet("/matches")
public class GetAllMatchStatus extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    public GetAllMatchStatus() {
        super();
    }
    
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {
			String partnerId = (String) request.getAttribute("partnerId");
			Map<String,String[]> paramsMap = request.getParameterMap();
			MatchSearchParameters matchParams = new MatchSearchParameters(paramsMap);
			String homeCoupleId = DB.getCoupleId(partnerId);
			ArrayList<Match> allMatches = DB.getAllMatches(homeCoupleId, partnerId, matchParams);
			BlockedIds blocks = DB.getBlocks(homeCoupleId);
			ArrayList<Match> unblockedMatches = new ArrayList<Match>();
			for (Match match : allMatches) {
				if (!blocks.contains(match.awayCoupleId)) {
					unblockedMatches.add(match);;
				}
			}
			ArrayList<JsonMatch> jsonMatches = new ArrayList<JsonMatch>();
			for (Match match : unblockedMatches) {
				jsonMatches.add(match.getJsonMatch(partnerId));
			}
			ObjectMapper objectMapper = new ObjectMapper();
			response.getWriter().write(objectMapper.writeValueAsString(jsonMatches));
		} catch (Exception e){
	    	e.printStackTrace();
	    	throw new ServletException(e);
	    }
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {

		} catch (Exception e){
	    	e.printStackTrace();
	    	throw new ServletException(e);
	    }
	}

}
