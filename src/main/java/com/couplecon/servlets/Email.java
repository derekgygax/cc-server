package com.couplecon.servlets;

import java.io.IOException;
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
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.couplecon.util.Config;

@WebServlet("/email")
public class Email extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    public Email() {
        super();
    }
    
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {

		} catch (Exception e){
	    	e.printStackTrace();
	    	throw new ServletException(e);
	    }
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {
			String server = Config.getProperty("mail_smtp_server");
			String sender = Config.getProperty("mail_smtp_address");
			String pw = Config.getProperty("mail_smtp_pw");
			Properties props = new Properties();
			props.put("mail.smtp.socketFactory.port", "465");
		    props.put("mail.smtp.socketFactory.class",
		            "javax.net.ssl.SSLSocketFactory");
		    props.put("mail.smtp.auth", true);
		    props.put("mail.smtp.host", server);
		    props.put("mail.smtp.port", "465");
		    props.put("mail.smtp.ssl.trust", server);
			Session session = Session.getInstance(props, new Authenticator() {
			    @Override
			    protected PasswordAuthentication getPasswordAuthentication() {
			        return new PasswordAuthentication(sender, pw);
			    }
			});
			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress(sender));
			message.setRecipients(
			  Message.RecipientType.TO, InternetAddress.parse("kyle.moad@gmail.com"));
			message.setSubject("Test 2couplesconnect message");
			String msg = "This is my first email using JavaMailer";
			MimeBodyPart mimeBodyPart = new MimeBodyPart();
			mimeBodyPart.setContent(msg, "text/html");
			MimeMultipart multipart = new MimeMultipart();
			multipart.addBodyPart(mimeBodyPart);
			message.setContent(multipart);
			Transport.send(message);
		} catch (Exception e){
	    	e.printStackTrace();
	    	throw new ServletException(e);
	    }
	}

}
