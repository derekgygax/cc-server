package com.couplecon.servlets;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.couplecon.data.Couple;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.couplecon.util.DB;
import com.couplecon.util.Utils;

@WebServlet("/update-couple")
public class UpdateCouple extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    public UpdateCouple() {
        super();
    }
    
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {
			String partnerId = (String) request.getAttribute("partnerId");
			String json = Utils.getRequestContent(request);
			ObjectMapper objectMapper = new ObjectMapper();
			Couple couple = objectMapper.readValue(json, Couple.class);
			DB.updateCouple(couple, partnerId);
			Utils.changeCoupleVisibility(DB.getCoupleId(partnerId));
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServletException(e);
		}
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}
