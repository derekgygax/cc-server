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
import com.fasterxml.jackson.databind.ObjectMapper;

@WebServlet("/categorical/*")
public class Categorical extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    public Categorical() {
        super();
    }
    
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {
			String level = request.getPathInfo().split("/")[1];

			ArrayList<SurveyQuestion> questions = DB.getCategoricalQuestions(level);
			ObjectMapper objectMapper = new ObjectMapper();
			response.getWriter().write(objectMapper.writeValueAsString(questions));
		} catch (Exception e){
	    	e.printStackTrace();
	    	throw new ServletException(e);
	    }
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}
