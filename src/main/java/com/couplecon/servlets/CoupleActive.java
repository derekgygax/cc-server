package com.couplecon.servlets;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.couplecon.data.SurveyQuestion;
import com.couplecon.util.DB;
import com.couplecon.util.Utils;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebServlet("/couple-active/*")
public class CoupleActive extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    public CoupleActive() {
        super();
    }
    
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {
			String coupleId = request.getPathInfo().split("/")[1];
			Boolean active = DB.getCoupleActive(coupleId);
			if (active == null) {
				response.setStatus(HttpServletResponse.SC_NOT_FOUND);
			} else if ( active == true ) {
				response.getWriter().write("true");
			} else {
				response.getWriter().write("false");
			}
		} catch (Exception e){
	    	e.printStackTrace();
	    	throw new ServletException(e);
	    }
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {
			String coupleId = request.getPathInfo().split("/")[1];
			
			String json = Utils.getRequestContent(request);
			ObjectMapper om = new ObjectMapper();
			HashMap<String, String> activeMap = om.readValue(json, new TypeReference<HashMap<String, String>>(){});
			String activeString = activeMap.get("active");
			
			Boolean active;
			if ("true".equals(activeString)) {
				active = true;
			} else if ("false".equals(activeString)) {
				active = false;
			} else {
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				return;
			}
			DB.setCoupleActive(coupleId, active);
		} catch (Exception e){
	    	e.printStackTrace();
	    	throw new ServletException(e);
	    }
	}

}
