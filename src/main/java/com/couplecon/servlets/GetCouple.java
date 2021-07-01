package com.couplecon.servlets;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.couplecon.util.DB;
import com.couplecon.util.Utils;
import com.couplecon.data.Couple;
import com.couplecon.data.JsonViews;

@WebServlet("/couple/*")
public class GetCouple extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    public GetCouple() {
        super();
    }
    
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {
			String pathCoupleId = request.getPathInfo().split("/")[1];
			String homePartnerId = (String) request.getAttribute("partnerId");
			String homeCoupleId = DB.getCoupleId(homePartnerId);
			String coupleId;
			if (pathCoupleId.equals("us")) {
				coupleId = homeCoupleId;
			} else {
				coupleId = pathCoupleId;
			}
			if (coupleId.equals(homeCoupleId)) {
				DB.updateLastActive(coupleId);
			}
			Couple couple = DB.getCouple(coupleId);
			if (couple != null && DB.getBlocks(homeCoupleId).contains(couple.getCoupleId())) {
				couple = null;
			}
			if (couple != null ) {
				ObjectMapper objectMapper = new ObjectMapper();
				ObjectWriter coupleWriter;
				if (coupleId.equals(homeCoupleId)) {
					coupleWriter = objectMapper.writerWithView(JsonViews.General.CoupleMember.class);
				} else {
					coupleWriter = objectMapper.writerWithView(JsonViews.General.Public.class);
				}
				String s = coupleWriter.writeValueAsString(couple);
				response.getWriter().write(s);
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
			String coupleId = DB.getCoupleId(partnerId);
			String pathCoupleId = request.getPathInfo().split("/")[1];
			if (!(pathCoupleId.equals(coupleId) || pathCoupleId.equals("us"))) {
				response.setStatus(HttpServletResponse.SC_FORBIDDEN);
				return;
			}
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

}
