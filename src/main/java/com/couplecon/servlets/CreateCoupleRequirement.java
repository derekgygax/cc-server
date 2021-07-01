package com.couplecon.servlets;

import java.io.IOException;
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

@WebServlet("/create-couple-requirement")
public class CreateCoupleRequirement extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    public CreateCoupleRequirement() {
        super();
    }

   
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		return;
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {
			String json = Utils.getRequestContent(request);
			ObjectMapper objectMapper = new ObjectMapper();
			Partner partner = objectMapper.readValue(json, Partner.class);

			Partner homePartner = DB.getPartner(Utils.verifyUserByToken(request));
			
			// Create a new partner
			createPartner(partner, homePartner.getEmailAddress());

			// get partner ids
			String partnerId = partner.getPartnerId();
			String homePartnerId = homePartner.getPartnerId();

			// Create couple
			DB.createCouple(partnerId, homePartnerId);
			String coupleId = DB.getCoupleId(partnerId);

			// Write output stream
			ServletOutputStream o = response.getOutputStream();
			JsonFactory f = new JsonFactory();
			JsonGenerator g = f.createGenerator(o);
			g.writeStartObject();
			g.writeStringField("coupleId", coupleId);
			g.writeEndObject();
			g.close();
			o.close();
		} catch (Exception e){
			e.printStackTrace();
			throw new ServletException(e);
		}
	}
	
	private void createPartner(Partner partner, String emailAddress) throws Exception {
		String partnerId = DB.createPartner(partner);
		if (partnerId == null){
			throw new Exception("The partner "+partner.getFirstName()+" "+partner.getLastName()+" "
					+ "for the email address "+emailAddress + " "
							+ "could not be created because the partnerId came back null.");
		}
		partner.setPartnerId(partnerId);
	}

}
