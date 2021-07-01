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
import com.couplecon.data.Partner;
import com.couplecon.data.JsonViews;

@WebServlet("/partner/*")
public class GetPartner extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    public GetPartner() {
        super();
    }
    
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {
			String pathPartnerId = request.getPathInfo().split("/")[1];
			String userPartnerId = (String) request.getAttribute("partnerId");
			String userCoupleId = DB.getCoupleId(userPartnerId);
			String awayPartnerId = (userCoupleId!=null) ? Utils.getOtherPartnerId(userCoupleId, userPartnerId) : null;
			String partnerId = null;
			if (pathPartnerId.equals("me")) {
				partnerId = (String) request.getAttribute("partnerId");
			} else if (pathPartnerId.equals("you")) {
				partnerId = awayPartnerId;
			} else {
				partnerId = pathPartnerId;
			}
			Partner partner = DB.getPartner(partnerId);
			if (partner != null && userCoupleId != null && DB.getBlocks(userCoupleId).contains(partner.getPartnerId())) {
				partner = null;
			}
			if (partner != null) {
				ObjectMapper objectMapper = new ObjectMapper();
				ObjectWriter partnerWriter;
				if (partnerId.equals(userPartnerId)) {
					partnerWriter = objectMapper.writerWithView(JsonViews.General.Private.class);
				} else if (partnerId.equals(awayPartnerId)) {
					partnerWriter = objectMapper.writerWithView(JsonViews.General.CoupleMember.class);
				} else {
					partnerWriter = objectMapper.writerWithView(JsonViews.General.Public.class);
				}
				response.getWriter().write(partnerWriter.writeValueAsString(partner));
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
			String json = Utils.getRequestContent(request);
			ObjectMapper objectMapper = new ObjectMapper();
			Partner partner = objectMapper.readValue(json, Partner.class);
			String homePartnerId = (String) request.getAttribute("partnerId");
			String awayPartnerId = DB.getOtherPartnersId(homePartnerId);

			// You can update your own or your partners info
			if (!homePartnerId.equals(partner.getPartnerId()) && !awayPartnerId.equals(partner.getPartnerId())) {
				response.setStatus(HttpServletResponse.SC_FORBIDDEN);
				return;
			}
			
			DB.updatePartner(partner);
			String coupleId = DB.getCoupleId(homePartnerId);
			if (coupleId != null) {
				Utils.changeCoupleVisibility(coupleId);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServletException(e);
		}
	}

}
