package com.couplecon.servlets;

import java.io.IOException;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.couplecon.data.SearchParam;
import com.couplecon.data.SearchParamViews;
import com.couplecon.util.SearchParamFactory;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;

@WebServlet("/search/parameters")
public class SearchParameters extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    public SearchParameters() {
        super();
    }
    
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {
			SearchParamFactory paramFactory = new SearchParamFactory();
			ArrayList<SearchParam> params = paramFactory.getAvailableParams();
			ObjectMapper mapper = new ObjectMapper();
			ArrayNode jsonArray = mapper.createArrayNode();
			for (SearchParam param : params) {
				JsonNode paramNode = null;
				switch (param.getType()) {
				case "range":
					paramNode = mapper.readTree(
											    mapper.writerWithView(SearchParamViews.Range.class)
											   						  .writeValueAsString(param)
											  	);
					break;
				case "multiselect":
					paramNode = mapper.readTree(
							   				    mapper.writerWithView(SearchParamViews.Multiselect.class)
							   						 				  .writeValueAsString(param)
							  );
					break;
				case "singleselect":
					paramNode = mapper.readTree(
		   				    					mapper.writerWithView(SearchParamViews.Singleselect.class)
		   						 				  					  .writeValueAsString(param)
							);
					break;
				}
				if (paramNode != null) {
					jsonArray.add(paramNode);
				}
			}
			response.getWriter().write(jsonArray.toString());
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
