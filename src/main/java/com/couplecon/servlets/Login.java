package com.couplecon.servlets;

import java.io.IOException;
import java.sql.Connection;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.couplecon.util.DB;
import com.couplecon.util.Utils;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;


@WebServlet("/login")
public class Login extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    public Login() {
        super();
    }
    
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {			
			String email = (String) request.getParameter("emailAddress");
			String password = (String) request.getParameter("password");
			
			if(email != null) {
				Boolean verified = DB.verifyUser(email, password);
				
				if(verified) {
					String partnerId = DB.getPartnerId(email);
					String coupleId = DB.getCoupleId(partnerId);
					
					String token = DB.generateLoginToken(partnerId);
	
					this.outputLoginDetails(response, token, partnerId, coupleId);
				} else {
					response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);					
				}
				
			} else {
				// authenticate by token
				String token = (String) request.getParameter("token");
				String partnerId = DB.verifyUserByToken(token);
				
				if(partnerId != null) {
					String coupleId = DB.getCoupleId(partnerId);
					
					this.outputLoginDetails(response, token, partnerId, coupleId);
				} else {
					response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServletException(e);
		}
	}
	
	protected void outputLoginDetails(HttpServletResponse response, String token, String partnerId, String coupleId) {
		try {
			ServletOutputStream o = response.getOutputStream();
			JsonFactory f = new JsonFactory();
			JsonGenerator g = f.createGenerator(o);
			g.writeStartObject();
			g.writeStringField("token", token);
			g.writeStringField("partnerId", partnerId);
			g.writeStringField("coupleId", coupleId);
			g.writeEndObject();
			g.close();
			o.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}
