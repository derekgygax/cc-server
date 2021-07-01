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

@WebServlet("/create-account")
public class CreateAccount extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    public CreateAccount() {
        super();
    }
    protected String getTemplate() {
    	return "<!DOCTYPE html><html xmlns='http://www.w3.org/1999/xhtml' xmlns:v='urn:schemas-microsoft-com:vml' xmlns:o='urn:schemas-microsoft-com:office:office'><head>  <title></title>  <!--[if !mso]><!-- -->  <meta http-equiv='X-UA-Compatible' content='IE=edge'>  <!--<![endif]--><meta http-equiv='Content-Type' content='text/html; charset=UTF-8'><meta name='viewport' content='width=device-width, initial-scale=1.0'><style type='text/css'>  #outlook a { padding: 0; }  .ReadMsgBody { width: 100%; }  .ExternalClass { width: 100%; }  .ExternalClass * { line-height:100%; }  body { margin: 0; padding: 0; -webkit-text-size-adjust: 100%; -ms-text-size-adjust: 100%; }  table, td { border-collapse:collapse; mso-table-lspace: 0pt; mso-table-rspace: 0pt; }  img { border: 0; height: auto; line-height: 100%; outline: none; text-decoration: none; -ms-interpolation-mode: bicubic; }  p { display: block; margin: 13px 0; }</style><!--[if !mso]><!--><style type='text/css'>  @media only screen and (max-width:480px) {    @-ms-viewport { width:320px; }    @viewport { width:320px; }  }</style><!--<![endif]--><!--[if mso]><xml>  <o:OfficeDocumentSettings>    <o:AllowPNG/>    <o:PixelsPerInch>96</o:PixelsPerInch>  </o:OfficeDocumentSettings></xml><![endif]--><!--[if lte mso 11]><style type='text/css'>  .outlook-group-fix {    width:100% !important;  }</style><![endif]--><!--[if !mso]><!-->    <link href='https://fonts.googleapis.com/css?family=Ubuntu:300,400,500,700' rel='stylesheet' type='text/css'>    <style type='text/css'>        @import url(https://fonts.googleapis.com/css?family=Ubuntu:300,400,500,700);    </style>  <!--<![endif]--><style type='text/css'>  @media only screen and (min-width:480px) {    .mj-column-per-100 { width:100%!important; }  }</style></head><body style='background: #FFFFFF;'>    <div class='mj-container' style='background-color:#FFFFFF;'><!--[if mso | IE]>      <table role='presentation' border='0' cellpadding='0' cellspacing='0' width='600' align='center' style='width:600px;'>        <tr>          <td style='line-height:0px;font-size:0px;mso-line-height-rule:exactly;'>      <![endif]--><div style='margin:0px auto;max-width:600px;'><table role='presentation' cellpadding='0' cellspacing='0' style='font-size:0px;width:100%;' align='center' border='0'><tbody><tr><td style='text-align:center;vertical-align:top;direction:ltr;font-size:0px;padding:9px 0px 9px 0px;'><!--[if mso | IE]>      <table role='presentation' border='0' cellpadding='0' cellspacing='0'>        <tr>          <td style='vertical-align:top;width:600px;'>      <![endif]--><div class='mj-column-per-100 outlook-group-fix' style='vertical-align:top;display:inline-block;direction:ltr;font-size:13px;text-align:left;width:100%;'><table role='presentation' cellpadding='0' cellspacing='0' width='100%' border='0'><tbody><tr><td style='word-wrap:break-word;font-size:0px;padding:0px 0px 0px 0px;' align='center'><table role='presentation' cellpadding='0' cellspacing='0' style='border-collapse:collapse;border-spacing:0px;' align='center' border='0'><tbody><tr><td style='width:600px;'><img alt='2COUPLESCONNECT' title='' height='auto' src='https://2couplesconnect.com/img/email-banner.png' style='border:none;border-radius:0px;display:block;font-size:13px;outline:none;text-decoration:none;width:100%;height:auto;' width='600'></td></tr></tbody></table></td></tr><tr><td style='word-wrap:break-word;font-size:0px;padding:0px 20px 0px 20px;' align='center'><div style='cursor:auto;color:#000000;font-family:Ubuntu, Helvetica, Arial, sans-serif;font-size:11px;line-height:22px;text-align:center;'><p>Thank you for registering with 2COUPLESCONNECT.com.&#xA0;We want to help couples going through new chapters in their life (moving homes, getting married, having kids) not have to go through it on their own.</p><p></p><p>In order to verify your account, please click on the following link:</p><p><a href='{{ONLINELINK}}'>{{ONLINELINK}}</a></p></div></td></tr><tr><td style='word-wrap:break-word;font-size:0px;padding:0px 20px 0px 20px;' align='center'><div style='cursor:auto;color:#000000;font-family:Ubuntu, Helvetica, Arial, sans-serif;font-size:11px;line-height:22px;text-align:center;'><p>After verifying your email, you will be asked to fill out additional requirements&#xA0;to help match your couple, with other couples!&#xA0; What happens after that, is up to you! Please enjoy 2COUPLESCONNECT and if you have any issues, please contact us at&#xA0;<a href='mailto:{{SUPPORTEMAIL}}'>{{SUPPORTEMAIL}}</a></p><p><span style='font-size:10px;'>2018 &#xA9; 2COUPLESCONNECT&#xA0;</span></p></div></td></tr></tbody></table></div><!--[if mso | IE]>      </td></tr></table>      <![endif]--></td></tr></tbody></table></div><!--[if mso | IE]>      </td></tr></table>      <![endif]--></div></body></html>";
    }
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		return;
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {
			String jsonString = Utils.getRequestContent(request);
			ObjectMapper objectMapper = new ObjectMapper();
			HashMap<String, Partner> newPartners = objectMapper.readValue(jsonString, new TypeReference<HashMap<String, Partner>>(){});
			ArrayList<String> ids = new ArrayList<String>();
			// Create both partners
			Partner partnerHigher = newPartners.get("higher");
			createPartner(partnerHigher, partnerHigher.getEmailAddress());
			Partner partnerLower = newPartners.get("lower");
			createPartner(partnerLower, partnerHigher.getEmailAddress());

			// get partner ids
			String partnerHigherId = partnerHigher.getPartnerId();
			String partnerLowerId = partnerLower.getPartnerId();
			
			// Create couple
			DB.createCouple(partnerHigherId, partnerLowerId);
			String coupleId = DB.getCoupleId(partnerHigherId);
			if (coupleId == null){
				throw new Exception("The couple couldn't be created between "+partnerHigherId+" and "+partnerLowerId);
			}
			
			// Get login token and email token for the first partner, the one that created the account
			String loginToken = DB.generateLoginToken(partnerHigherId);
			if (loginToken == null){
				throw new Exception("The login token for "+partnerHigherId+" couldn't be created while creating the account");
			}
			String emailToken = DB.createEmailVerificationToken(partnerHigherId);
			if (emailToken == null){
				throw new Exception("The email token for "+partnerHigherId+" couldn't be created while creating the account");
			}
			
			// Send the email verification
			SendEmail.verifyEmail(partnerHigherId, emailToken);
			
			// Write output stream
			ServletOutputStream o = response.getOutputStream();
			JsonFactory f = new JsonFactory();
			JsonGenerator g = f.createGenerator(o);
			g.writeStartObject();
			g.writeStringField("token", loginToken);
			g.writeStringField("coupleId", coupleId);
			g.writeStringField("partnerHigherId", partnerHigherId);
			g.writeStringField("partnerLowerId", partnerLowerId);
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
