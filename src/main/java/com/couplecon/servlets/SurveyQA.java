package com.couplecon.servlets;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.couplecon.data.Partner;
import com.couplecon.data.SurveyAnswers;
import com.couplecon.data.SurveyChoice;
import com.couplecon.data.SurveyQuestion;
import com.couplecon.util.DB;
import com.couplecon.util.Utils;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebServlet("/survey/qa")
public class SurveyQA extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    public SurveyQA() {
        super();
    }
    
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {
			String partnerId = (String) request.getAttribute("partnerId");
			String groupStr = request.getParameter("group");
			ArrayList<String> allGroups = DB.getSurveyGroups();
			String[] groups;
			if (groupStr != null) {
				groups = groupStr.split(",");
				for (String group : groups) {
					if (!allGroups.contains(group)) {
						response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
						response.getWriter().write("{\"reason\":\"group "+group+" is not valid\"}");
						return;
					}
				}
			} else {
				groups = allGroups.toArray(new String[allGroups.size()]);
			}
			HashMap<String,ArrayList<SurveyQuestion>> allQuestions = DB.getSurveyGroupQuestions(groups, partnerId);
			ObjectMapper objectMapper = new ObjectMapper();
			response.getWriter().write(objectMapper.writeValueAsString(allQuestions));
		} catch (Exception e){
	    	e.printStackTrace();
	    	throw new ServletException(e);
	    }
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {
			String partnerId = (String) request.getAttribute("partnerId");
			String otherPartnerInCouplesId = DB.getOtherPartnersId(partnerId);
			String json = Utils.getRequestContent(request);
			ObjectMapper objectMapper = new ObjectMapper();
			SurveyAnswers surveyAnswers = objectMapper.readValue(json, SurveyAnswers.class);
			HashMap<String, SurveyChoice[]> groupChoices = surveyAnswers.answers;
			for (String groupId: groupChoices.keySet()){
				for (SurveyChoice choice: groupChoices.get(groupId)){
					DB.answerSurveyQuestion(partnerId, choice);
					if (groupId.equals("Essential") && surveyAnswers.applyToBothPartners && otherPartnerInCouplesId != null){
						// If you are looking at an essentials question then answer for the other partner as well
						DB.answerSurveyQuestion(otherPartnerInCouplesId, choice);	
					}
				}
			}
		} catch (Exception e){
	    	e.printStackTrace();
	    	throw new ServletException(e);
	    }
	}

}
