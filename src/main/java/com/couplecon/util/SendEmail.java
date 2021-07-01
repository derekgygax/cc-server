package com.couplecon.util;

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

import com.couplecon.data.Partner;

public class SendEmail {
	
	private static String getVerifyEmailTemplate(){
    	return "<!DOCTYPE html><html xmlns='http://www.w3.org/1999/xhtml' xmlns:v='urn:schemas-microsoft-com:vml' xmlns:o='urn:schemas-microsoft-com:office:office'><head>  <title></title>  <!--[if !mso]><!-- -->  <meta http-equiv='X-UA-Compatible' content='IE=edge'>  <!--<![endif]--><meta http-equiv='Content-Type' content='text/html; charset=UTF-8'><meta name='viewport' content='width=device-width, initial-scale=1.0'><style type='text/css'>  #outlook a { padding: 0; }  .ReadMsgBody { width: 100%; }  .ExternalClass { width: 100%; }  .ExternalClass * { line-height:100%; }  body { margin: 0; padding: 0; -webkit-text-size-adjust: 100%; -ms-text-size-adjust: 100%; }  table, td { border-collapse:collapse; mso-table-lspace: 0pt; mso-table-rspace: 0pt; }  img { border: 0; height: auto; line-height: 100%; outline: none; text-decoration: none; -ms-interpolation-mode: bicubic; }  p { display: block; margin: 13px 0; }</style><!--[if !mso]><!--><style type='text/css'>  @media only screen and (max-width:480px) {    @-ms-viewport { width:320px; }    @viewport { width:320px; }  }</style><!--<![endif]--><!--[if mso]><xml>  <o:OfficeDocumentSettings>    <o:AllowPNG/>    <o:PixelsPerInch>96</o:PixelsPerInch>  </o:OfficeDocumentSettings></xml><![endif]--><!--[if lte mso 11]><style type='text/css'>  .outlook-group-fix {    width:100% !important;  }</style><![endif]--><!--[if !mso]><!-->    <link href='https://fonts.googleapis.com/css?family=Ubuntu:300,400,500,700' rel='stylesheet' type='text/css'>    <style type='text/css'>        @import url(https://fonts.googleapis.com/css?family=Ubuntu:300,400,500,700);    </style>  <!--<![endif]--><style type='text/css'>  @media only screen and (min-width:480px) {    .mj-column-per-100 { width:100%!important; }  }</style></head><body style='background: #FFFFFF;'>    <div class='mj-container' style='background-color:#FFFFFF;'><!--[if mso | IE]>      <table role='presentation' border='0' cellpadding='0' cellspacing='0' width='600' align='center' style='width:600px;'>        <tr>          <td style='line-height:0px;font-size:0px;mso-line-height-rule:exactly;'>      <![endif]--><div style='margin:0px auto;max-width:600px;'><table role='presentation' cellpadding='0' cellspacing='0' style='font-size:0px;width:100%;' align='center' border='0'><tbody><tr><td style='text-align:center;vertical-align:top;direction:ltr;font-size:0px;padding:9px 0px 9px 0px;'><!--[if mso | IE]>      <table role='presentation' border='0' cellpadding='0' cellspacing='0'>        <tr>          <td style='vertical-align:top;width:600px;'>      <![endif]--><div class='mj-column-per-100 outlook-group-fix' style='vertical-align:top;display:inline-block;direction:ltr;font-size:13px;text-align:left;width:100%;'><table role='presentation' cellpadding='0' cellspacing='0' width='100%' border='0'><tbody><tr><td style='word-wrap:break-word;font-size:0px;padding:0px 0px 0px 0px;' align='center'><table role='presentation' cellpadding='0' cellspacing='0' style='border-collapse:collapse;border-spacing:0px;' align='center' border='0'><tbody><tr><td style='width:600px;'><img alt='2COUPLESCONNECT' title='' height='auto' src='https://2couplesconnect.com/img/email-banner.png' style='border:none;border-radius:0px;display:block;font-size:13px;outline:none;text-decoration:none;width:100%;height:auto;' width='600'></td></tr></tbody></table></td></tr><tr><td style='word-wrap:break-word;font-size:0px;padding:0px 20px 0px 20px;' align='center'><div style='cursor:auto;color:#000000;font-family:Ubuntu, Helvetica, Arial, sans-serif;font-size:11px;line-height:22px;text-align:center;'><p>Welcome to 2CouplesConnect!  Please verify your account:</p><p><a href='{{ONLINELINK}}'>{{ONLINELINK}}</a></p></div></td></tr><tr><td style='word-wrap:break-word;font-size:0px;padding:0px 20px 0px 20px;' align='center'><div style='cursor:auto;color:#000000;font-family:Ubuntu, Helvetica, Arial, sans-serif;font-size:11px;line-height:22px;text-align:center;'><p>It's a pleasure to have you on board our website and don't forget to tell your SO/boo/bae to sign up too! You won't be able to do much until you are both coupled up and fill out some basic requirements.</p><p></p><p>It's simple and should take you less than 10 minutes. So, get comfortable and be honest and we will match you with your perfect double date.</p><p></p><p>I'll be sending you another email soon with details on our promo to celebrate our website and Android app launch! You won't want to miss out on a chance for a $300 dinner for 4 (you, your SO and your new double date)!</p><p>Best</p><p>The 2CouplesConnect Team</p></div></td></tr></tbody></table></div><!--[if mso | IE]>      </td></tr></table>      <![endif]--></td></tr></tbody></table></div><!--[if mso | IE]>      </td></tr></table>      <![endif]--></div></body></html>";
	}
	
	private static String getCoupleRequestEmailTemplate(){
    	return "<!DOCTYPE html><html xmlns='http://www.w3.org/1999/xhtml' xmlns:v='urn:schemas-microsoft-com:vml' xmlns:o='urn:schemas-microsoft-com:office:office'><head>  <title></title>  <!--[if !mso]><!-- -->  <meta http-equiv='X-UA-Compatible' content='IE=edge'>  <!--<![endif]--><meta http-equiv='Content-Type' content='text/html; charset=UTF-8'><meta name='viewport' content='width=device-width, initial-scale=1.0'><style type='text/css'>  #outlook a { padding: 0; }  .ReadMsgBody { width: 100%; }  .ExternalClass { width: 100%; }  .ExternalClass * { line-height:100%; }  body { margin: 0; padding: 0; -webkit-text-size-adjust: 100%; -ms-text-size-adjust: 100%; }  table, td { border-collapse:collapse; mso-table-lspace: 0pt; mso-table-rspace: 0pt; }  img { border: 0; height: auto; line-height: 100%; outline: none; text-decoration: none; -ms-interpolation-mode: bicubic; }  p { display: block; margin: 13px 0; }</style><!--[if !mso]><!--><style type='text/css'>  @media only screen and (max-width:480px) {    @-ms-viewport { width:320px; }    @viewport { width:320px; }  }</style><!--<![endif]--><!--[if mso]><xml>  <o:OfficeDocumentSettings>    <o:AllowPNG/>    <o:PixelsPerInch>96</o:PixelsPerInch>  </o:OfficeDocumentSettings></xml><![endif]--><!--[if lte mso 11]><style type='text/css'>  .outlook-group-fix {    width:100% !important;  }</style><![endif]--><!--[if !mso]><!-->    <link href='https://fonts.googleapis.com/css?family=Ubuntu:300,400,500,700' rel='stylesheet' type='text/css'>    <style type='text/css'>        @import url(https://fonts.googleapis.com/css?family=Ubuntu:300,400,500,700);    </style>  <!--<![endif]--><style type='text/css'>  @media only screen and (min-width:480px) {    .mj-column-per-100 { width:100%!important; }  }</style></head><body style='background: #FFFFFF;'>    <div class='mj-container' style='background-color:#FFFFFF;'><!--[if mso | IE]>      <table role='presentation' border='0' cellpadding='0' cellspacing='0' width='600' align='center' style='width:600px;'>        <tr>          <td style='line-height:0px;font-size:0px;mso-line-height-rule:exactly;'>      <![endif]--><div style='margin:0px auto;max-width:600px;'><table role='presentation' cellpadding='0' cellspacing='0' style='font-size:0px;width:100%;' align='center' border='0'><tbody><tr><td style='text-align:center;vertical-align:top;direction:ltr;font-size:0px;padding:9px 0px 9px 0px;'><!--[if mso | IE]>      <table role='presentation' border='0' cellpadding='0' cellspacing='0'>        <tr>          <td style='vertical-align:top;width:600px;'>      <![endif]--><div class='mj-column-per-100 outlook-group-fix' style='vertical-align:top;display:inline-block;direction:ltr;font-size:13px;text-align:left;width:100%;'><table role='presentation' cellpadding='0' cellspacing='0' width='100%' border='0'><tbody><tr><td style='word-wrap:break-word;font-size:0px;padding:0px 0px 0px 0px;' align='center'><table role='presentation' cellpadding='0' cellspacing='0' style='border-collapse:collapse;border-spacing:0px;' align='center' border='0'><tbody><tr><td style='width:600px;'><img alt='2COUPLESCONNECT' title='' height='auto' src='https://2couplesconnect.com/img/email-banner.png' style='border:none;border-radius:0px;display:block;font-size:13px;outline:none;text-decoration:none;width:100%;height:auto;' width='600'></td></tr></tbody></table></td></tr><tr><td style='word-wrap:break-word;font-size:0px;padding:0px 20px 0px 20px;' align='center'><div style='cursor:auto;color:#000000;font-family:Ubuntu, Helvetica, Arial, sans-serif;font-size:11px;line-height:22px;text-align:center;'><h2>You're invited to 2COUPLESCONNECT!</h2><p>You have been invited to make an account and create an couple with your partner, {{PARTNER_FIRST_NAME}}, at our website 2COUPLESCONNECT.com. Please visit the link below to create an account and automatically join a couple with you and your partner, {{PARTNER_FIRST_NAME}}</p><p>We want to help couples going through new chapters in their life (moving homes, getting married, having kids) not have to go through it on their own.</p><p></p><p>To create your account, please click on the following link:</p><p><a href='{{ONLINELINK}}'>{{ONLINELINK}}</a></p><p></p><p>If the person inviting you is unknown to you, or you believe this email is a mistake, please don't hesistate to <a href='https://2couplesconnect.com/help'>contact us</a> with any questions or concerns.</p></div></td></tr><tr><td style='word-wrap:break-word;font-size:0px;padding:0px 20px 0px 20px;' align='center'><div style='cursor:auto;color:#000000;font-family:Ubuntu, Helvetica, Arial, sans-serif;font-size:11px;line-height:22px;text-align:center;'><p>After verifying your email, you will be asked to fill out additional requirements&#xA0;to help match your couple, with other couples!&#xA0; What happens after that, is up to you! Please enjoy 2COUPLESCONNECT and if you have any issues, please contact us at&#xA0;<a href='mailto:{{SUPPORTEMAIL}}'>{{SUPPORTEMAIL}}</a></p><p><span style='font-size:10px;'>2018 &#xA9; 2COUPLESCONNECT&#xA0;</span></p></div></td></tr></tbody></table></div><!--[if mso | IE]>      </td></tr></table>      <![endif]--></td></tr></tbody></table></div><!--[if mso | IE]>      </td></tr></table>      <![endif]--></div></body></html>";
	}
	
	public static void verifyEmail(String partnerId, String token){
		try {
			Partner partner = DB.getPartner(partnerId);
			String email = partner.getEmailAddress();
			
			
			String server = Config.getProperty("MAIL_SMTP_SERVER");
			String sender = Config.getProperty("MAIL_SMTP_ADDRESS");
			String pw = Config.getProperty("MAIL_SMTP_PASSWORD");
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
			  Message.RecipientType.TO, InternetAddress.parse(email));
			message.setSubject("Welcome to 2COUPLESCONNECT, verify your account!");
			
			String msg = getVerifyEmailTemplate();
			String verifyEmailUrl = System.getenv("WEB_URL") + "verifyemail?token=" + token;
			String supportEmail = "support@2couplesconnect.com";
			
			msg = msg.replace("{{ONLINELINK}}", verifyEmailUrl);
			msg = msg.replace("{{SUPPORTEMAIL}}", supportEmail);
			MimeBodyPart mimeBodyPart = new MimeBodyPart();
			mimeBodyPart.setContent(msg, "text/html");
			MimeMultipart multipart = new MimeMultipart();
			multipart.addBodyPart(mimeBodyPart);
			message.setContent(multipart);
			Transport.send(message);
			
			
		} catch (Exception e){
	    	e.printStackTrace();
	    }		
		
	}
	public static void verifyEmailWithProvided(String email, String token){
		try {
			String server = Config.getProperty("MAIL_SMTP_SERVER");
			String sender = Config.getProperty("MAIL_SMTP_ADDRESS");
			String pw = Config.getProperty("MAIL_SMTP_PASSWORD");
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
			  Message.RecipientType.TO, InternetAddress.parse(email));
			message.setSubject("Welcome to 2COUPLESCONNECT, verify your account!");
			
			String msg = getVerifyEmailTemplate();
			String verifyEmailUrl = System.getenv("WEB_URL") + "verifyemail?token=" + token;
			String supportEmail = "support@2couplesconnect.com";
			
			msg = msg.replace("{{ONLINELINK}}", verifyEmailUrl);
			msg = msg.replace("{{SUPPORTEMAIL}}", supportEmail);
			MimeBodyPart mimeBodyPart = new MimeBodyPart();
			mimeBodyPart.setContent(msg, "text/html");
			MimeMultipart multipart = new MimeMultipart();
			multipart.addBodyPart(mimeBodyPart);
			message.setContent(multipart);
			Transport.send(message);
			
			
		} catch (Exception e){
	    	e.printStackTrace();
	    }		
		
	}
	
	public static void coupleRequestEmail(String partnerId, String token, String sendTo){
		try {
			Partner partner = DB.getPartner(partnerId);
			String email = partner.getEmailAddress();
			
			
			String server = Config.getProperty("MAIL_SMTP_SERVER");
			String sender = Config.getProperty("MAIL_SMTP_ADDRESS");
			String pw = Config.getProperty("MAIL_SMTP_PASSWORD");
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
			  Message.RecipientType.TO, InternetAddress.parse(sendTo));
			message.setSubject("You've been invited to join "+partner.getFirstName()+" at 2COUPLESCONNECT!");
			
			String msg = getCoupleRequestEmailTemplate();
			String verifyEmailUrl = System.getenv("WEB_URL") + "create-account?coupleRequest=" + token;
			String supportEmail = "support@2couplesconnect.com";
			
			msg = msg.replace("{{ONLINELINK}}", verifyEmailUrl);
			msg = msg.replace("{{SUPPORTEMAIL}}", supportEmail);
			msg = msg.replace("{{PARTNER_FIRST_NAME}}", partner.getFirstName());
			MimeBodyPart mimeBodyPart = new MimeBodyPart();
			mimeBodyPart.setContent(msg, "text/html");
			MimeMultipart multipart = new MimeMultipart();
			multipart.addBodyPart(mimeBodyPart);
			message.setContent(multipart);
			Transport.send(message);
			
			
		} catch (Exception e){
	    	e.printStackTrace();
	    }		
		
	}
}
