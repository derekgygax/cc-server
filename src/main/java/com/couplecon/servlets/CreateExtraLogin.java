package com.couplecon.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;


import com.couplecon.data.Partner;
import com.couplecon.util.DB;
import com.couplecon.util.SendEmail;
import com.couplecon.util.Utils;
import com.couplecon.util.Config;

@WebServlet("/create-extra-login")
public class CreateExtraLogin extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    public CreateExtraLogin() {
        super();
    }

   
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		return;
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {
			String json = Utils.getRequestContent(request);
			System.out.println("Hit api post");
			
			String partnerId = request.getParameter("partnerId");
			String emailAddress = request.getParameter("emailAddress");
			
			boolean created = DB.createExtraLogin(
				partnerId,
				emailAddress,
				request.getParameter("firstName"),
				request.getParameter("lastName"),
				request.getParameter("password")
			);
			
			if(created == true) {
				response.setStatus(200);
				String emailToken = DB.createEmailVerificationToken(partnerId);
				if (emailToken == null){
					throw new Exception("The email token for "+partnerId+" couldn't be created while creating the account");
				}
				
				// Send the email verification
				SendEmail.verifyEmailWithProvided(emailAddress, emailToken);
				
				// Erase the old login token
				String loginToken = (String) request.getAttribute("loginToken");
				int rowsDeleted = DB.deleteLoginToken(loginToken);
				
				// Create new login token for other user
				String newToken = DB.generateLoginToken(partnerId);
				
				ServletOutputStream o = response.getOutputStream();
				JsonFactory f = new JsonFactory();
				JsonGenerator g = f.createGenerator(o);
				g.writeStartObject();
				g.writeStringField("token", newToken);
				g.writeStringField("partnerId", partnerId);
				g.writeEndObject();
				g.close();
				o.close();
			} else {
				response.setStatus(400);
			}
			
			System.out.println(created);

		} catch (Exception e){
			e.printStackTrace();
			throw new ServletException(e);
		}
	}

}
