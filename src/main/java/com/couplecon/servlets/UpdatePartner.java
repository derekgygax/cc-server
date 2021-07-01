package com.couplecon.servlets;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.couplecon.data.Partner;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.couplecon.util.DB;
import com.couplecon.util.Utils;

@WebServlet("/update-partner")
public class UpdatePartner extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    public UpdatePartner() {
        super();
    }
    
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {
			String json = Utils.getRequestContent(request);
			ObjectMapper objectMapper = new ObjectMapper();
			Partner partner = objectMapper.readValue(json, Partner.class);
			String partnerId = (String) request.getAttribute("partnerId");
			partner.setPartnerId(partnerId);
			DB.updatePartner(partner);
			String coupleId = DB.getCoupleId(partnerId);
			if (coupleId != null) {
				Utils.changeCoupleVisibility(coupleId);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServletException(e);
		}
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}
