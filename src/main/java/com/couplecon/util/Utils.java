package com.couplecon.util;

import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Random;
import java.util.Base64.Decoder;
import java.util.List;

import com.couplecon.data.Couple;
import com.couplecon.data.Partner;
import com.couplecon.data.RequestFailureJson;
import com.couplecon.util.DB;

public class Utils {

	public static boolean verifyUser(HttpServletRequest request) throws Exception{
		String authPayload = request.getHeader("authorization");
		String authData = authPayload.split(" ")[1];
		Decoder decoder = Base64.getDecoder();
		byte[] authBytes = decoder.decode(authData);
		String authString = new String(authBytes, StandardCharsets.UTF_8);
		String[] authArray = authString.split(":");
		String email = authArray[0];
		String password = authArray[1];
		return DB.verifyUser(email, password);
	}
	
	public static String verifyUserByToken(HttpServletRequest request) throws Exception{
		String authPayload = request.getHeader("authorization");
		String authData = authPayload.split(" ")[1];
		Decoder decoder = Base64.getDecoder();
		byte[] authBytes = decoder.decode(authData);
		
		String token = new String(authBytes, StandardCharsets.UTF_8);
		
		request.setAttribute("loginToken", token);
		
		return DB.verifyUserByToken(token);
	}
	
	public static String getPartnerId(HttpServletRequest request) throws Exception {
		String authPayload = request.getHeader("authorization");
		String authData = authPayload.split(" ")[1];
		Decoder decoder = Base64.getDecoder();
		byte[] authBytes = decoder.decode(authData);
		String authString = new String(authBytes, StandardCharsets.UTF_8);
		String[] authArray = authString.split(":");
		String email = authArray[0];
		String partnerId = DB.getPartnerId(email);
		return partnerId;
	}
	
	public static String getRequestContent(HttpServletRequest request) throws IOException {
		BufferedReader requestReader = request.getReader();
    	StringBuilder result = new StringBuilder();
    	String line;
        while ((line = requestReader.readLine()) != null) {
            result.append(line);
        }
		return result.toString();
	}
	
	public static void changeCoupleVisibility(String coupleId) throws Exception {
		Couple couple = DB.getCouple(coupleId);
		boolean visibility = true;
		visibility = visibility && (couple.getRelationshipType() != null);
		visibility = visibility && (couple.getTimeTogether() != null);
		Partner[] partners = {DB.getPartner(couple.getPartnerIdLower()), DB.getPartner(couple.getPartnerIdHigher())};
		for (Partner partner : partners) {
			visibility = visibility && (partner.getFirstName() != null);
			visibility = visibility && (partner.getLastName() != null);
			visibility = visibility && (partner.getZipcode() != null);
			visibility = visibility && (partner.getAge() > 0);
		}
		DB.setCoupleVisibility(coupleId, visibility);
	}
	
	public static String undercaseToCamelCase(String under) {
		String[] toks = under.split("_");
		String camel = "";
		for (String tok : toks) {
			if (camel.length() > 0) {
				camel += tok.substring(0,1).toUpperCase() + tok.substring(1);
			} else {
				camel += tok;
			}
		}
		return camel;
	}
	
	public static String getRandomHexString(int numchars){
		Random r = new Random();
		StringBuffer sb = new StringBuffer();
		while(sb.length() < numchars){
			sb.append(Integer.toHexString(r.nextInt()));
		}
		return sb.toString().substring(0, numchars);
	}
	
	public static String sha256(String base) {
	    try{
	        MessageDigest digest = MessageDigest.getInstance("SHA-256");
	        byte[] hash = digest.digest(base.getBytes("UTF-8"));
	        StringBuffer hexString = new StringBuffer();

	        for (int i = 0; i < hash.length; i++) {
	            String hex = Integer.toHexString(0xff & hash[i]);
	            if(hex.length() == 1) hexString.append('0');
	            hexString.append(hex);
	        }

	        return hexString.toString();
	    } catch(Exception ex){
	       throw new RuntimeException(ex);
	    }
	}
	
	public static long partnerIdNum(String partnerId) {
		return Long.valueOf(partnerId, 16);
	}
	
	public static String getOtherPartnerId(String coupleId, String partnerId) {
		String[] partnerIds = coupleId.split("-");
		if (partnerId.equals(partnerIds[0])) {
			return partnerIds[1];
		} else if (partnerId.equals(partnerIds[1])) {
			return partnerIds[0];
		} else {
			return null;
		}
	}
	
	public static ArrayList<String> orderPartnerIds (String partner1Id, String partner2Id) {
		ArrayList<String> ordered = new ArrayList<String>();
		if (partnerIdNum(partner1Id) < partnerIdNum(partner2Id)) {
			ordered.add(partner1Id);
			ordered.add(partner2Id);
		} else {
			ordered.add(partner2Id);
			ordered.add(partner1Id);
		}
		return ordered;
	}
	
	public static String getCoupleId (String partner1Id, String partner2Id) {
		return String.join("-", orderPartnerIds(partner1Id, partner2Id));
	}
	
	public static ArrayList<String> orderCoupleIds (String couple1Id, String couple2Id) {
		String p1Id = couple1Id.split("-")[0];
		String p3Id = couple2Id.split("-")[0];
		ArrayList<String> ordered = new ArrayList<String>();
		if (partnerIdNum(p1Id) < partnerIdNum(p3Id)) {
			ordered.add(couple1Id);
			ordered.add(couple2Id);
		} else {
			ordered.add(couple2Id);
			ordered.add(couple1Id);
		}
		return ordered;
	}
	
	public static String getMatchId(String couple1Id, String couple2Id) {
		return String.join("-", orderCoupleIds(couple1Id, couple2Id));
	}
	
	public static String[] getPartners(String coupleId) {
		return coupleId.split("-");
	}
	
	public static String getCoupleApproveCode(String p1a, String p2a) {
		if (p1a.equals("none")) {
			if (p2a.equals("none")) {
				return "none";
			} else if (p2a.equals("approve")) {
				return "plus";
			} else {
				return "minus";
			}
		} else if (p1a.equals("approve")) {
			if (p2a.equals("none")) {
				return "plus";
			} else if (p2a.equals("approve")) {
				return "approve";
			} else {
				return "mixed";
			}
		} else {
			if (p2a.equals("none")) {
				return "minus";
			} else if (p2a.equals("approve")) {
				return "mixed";
			} else {
				return "decline";
			}
		}
	}
	
	public static int partnerIndex(String partnerId, String coupleId) {
		List<String> partners = Arrays.asList(getPartners(coupleId));
		return partners.indexOf(partnerId);
		
	}
	
	public static void sendRequestFailureJson(ServletResponse response, Integer internalErrorCode) throws IOException {
		HttpServletResponse httpResponse = (HttpServletResponse) response;
		httpResponse.setStatus(HttpServletResponse.SC_BAD_REQUEST);
		httpResponse.resetBuffer();
		response.getWriter().write(RequestFailureJson.getJsonString(internalErrorCode));
	}
	
	public static String generateS3Key() {
		return getRandomHexString(32);
	}
	
	public static String mysqlString(Object o) {
		if (o == null) {
			return "null";
		} else if (o instanceof Number) {
			return String.valueOf(o);
		} else {
			return "'"+String.valueOf(o)+"'";
		}
	}
	
	public static String randomString(int len) {
		String chars = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
		SecureRandom rnd = new SecureRandom();
		StringBuilder sb = new StringBuilder( len );
		   for( int i = 0; i < len; i++ ) 
		      sb.append( chars.charAt( rnd.nextInt(chars.length()) ) );
		   return sb.toString();
	}
}
