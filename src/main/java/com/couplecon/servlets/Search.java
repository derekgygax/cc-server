package com.couplecon.servlets;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.couplecon.data.Couple;
import com.couplecon.data.SearchParam;
import com.couplecon.util.BlockedIds;
import com.couplecon.util.DB;
import com.couplecon.util.SearchParamFactory;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebServlet("/search")
public class Search extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    public Search() {
        super();
    }
    
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {
			Map<String,String[]> urlParams = request.getParameterMap();
			String userId = (String) request.getAttribute("partnerId");
			String coupleId = DB.getCoupleId(userId);
			SearchParamFactory paramFactory = new SearchParamFactory(userId);
			ArrayList<SearchParam> searchParams = new ArrayList<SearchParam>();
			for (String paramName : urlParams.keySet()) {
				if (paramFactory.isValidName(paramName)) {
					String[] vals = urlParams.get(paramName)[0].split(",");
					SearchParam searchParam = paramFactory.makeSearchParam(paramName, vals);
					searchParams.add(searchParam);
				}
			}
			ArrayList<Couple> allCouples = DB.searchCouples(searchParams);
			BlockedIds blocks = DB.getBlocks(coupleId);
			ArrayList<Couple> unblockedCouples = new ArrayList<Couple>();
			for (Couple awayCouple : allCouples) {
				if (!blocks.contains(awayCouple.getCoupleId())) {
					unblockedCouples.add(awayCouple);
				}
			}
			ObjectMapper mapper = new ObjectMapper();
			
			response.getWriter().write(mapper.writeValueAsString(unblockedCouples));
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
