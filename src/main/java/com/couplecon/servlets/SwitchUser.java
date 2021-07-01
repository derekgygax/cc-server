package com.couplecon.servlets;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.couplecon.util.DB;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;

/**
 * Servlet implementation class SwitchUser
 * This servlets switches a user in the couple from one to the other
 */
@WebServlet("/switch-user")
public class SwitchUser extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public SwitchUser() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.getWriter().append("Served at: ").append(request.getContextPath());
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try{
			String partnerId = (String) request.getAttribute("partnerId");
			String otherPartnerInCouplesId = DB.getOtherPartnersId(partnerId);
			
			// Erase the old login token
			String loginToken = (String) request.getAttribute("loginToken");
			int rowsDeleted = DB.deleteLoginToken(loginToken);
			if (rowsDeleted != 1) {
				response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			}
			
			// Create new login token for other user
			String newToken = DB.generateLoginToken(otherPartnerInCouplesId);
			
			ServletOutputStream o = response.getOutputStream();
			JsonFactory f = new JsonFactory();
			JsonGenerator g = f.createGenerator(o);
			g.writeStartObject();
			g.writeStringField("token", newToken);
			g.writeStringField("partnerId", otherPartnerInCouplesId);
			g.writeEndObject();
			g.close();
			o.close();
			
		} catch (Exception e){
			e.printStackTrace();
			throw new ServletException(e);
		}
	}

}
