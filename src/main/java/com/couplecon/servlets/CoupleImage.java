package com.couplecon.servlets;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.couplecon.util.DB;
import com.couplecon.util.Utils;
import com.couplecon.util.ResizableImage;

import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

@WebServlet("/couple/us/img")
@MultipartConfig(fileSizeThreshold=1024*1024*2, // 2MB
maxFileSize=1024*1024*10,      // 10MB
maxRequestSize=1024*1024*50)   // 50MB
public class CoupleImage extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    public CoupleImage() {
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
			String partnerId = (String) request.getAttribute("partnerId");
			String coupleId = DB.getCoupleId(partnerId);

	        AmazonS3 s3 = AmazonS3Client.builder()
	        	    .withRegion(System.getenv("AWS_REGION"))
	        	    .withCredentials(new DefaultAWSCredentialsProviderChain())
	        	    .build();
	        
	        String bucketName = "coupleconn-static";
	        for (int i=0; i<10; i++) {
	        	String partName = String.valueOf(i);
	        	Part part = request.getPart(partName);
	        	if (part != null) {
	        		BufferedInputStream partContent = new BufferedInputStream(part.getInputStream());
	        		ResizableImage resizableImage = new ResizableImage(partContent);
	        		HashMap<String,Integer> sizes = new HashMap<String,Integer>();
	        		sizes.put("large",1000);
	        		sizes.put("medium",500);
	        		sizes.put("small",150);
	        		HashMap<String,BufferedImage> images = new HashMap<String,BufferedImage>();
	        		images.put("native", resizableImage.getNative());
	        		for (String size : sizes.keySet()) {
	        			images.put(size, resizableImage.resizeByMaxSide(sizes.get(size)));
	        		}

	        		String filename = part.getSubmittedFileName();
	        		String[] fileToks = filename.split("\\.");
	        		String filetype;
	        		if (fileToks.length > 0) {
	        			filetype = fileToks[fileToks.length-1];
	        		} else {
	        			filetype = "jpg";
	        		}
	        		for (String size : images.keySet()) {
	        			BufferedImage image = images.get(size);
	        			ByteArrayOutputStream os = new ByteArrayOutputStream();
	        			ImageIO.write(image, filetype, os);
	        			InputStream imageData = new ByteArrayInputStream(os.toByteArray());
	        			ObjectMetadata metadata = new ObjectMetadata();
	        			if (filetype.equals("jpg")) {
	        				metadata.setContentType("image/jpeg");
	        			} else if (filetype.equals("png")) {
	        				metadata.setContentType("image/png");
	        			} else {
	        				metadata.setContentType("image/jpeg");
	        			}
	        			String s3Key;
	        			boolean uploaded = false;
	        			int attempts = 0;
	        			
	        			URL objectUrl;
	        			while (!(uploaded) && attempts < 10) {
	        				attempts += 1;
	        				s3Key = Utils.generateS3Key();
	        				if (!(s3.doesObjectExist(bucketName, s3Key))) {
	        					s3.putObject(bucketName, s3Key, imageData, metadata);
	        					uploaded = true;
	        					objectUrl = s3.getUrl(bucketName, s3Key);
	        					String link = objectUrl.toString();
	        					DB.setCoupleImage(coupleId,i,size,bucketName,s3Key,link);
	        				}
	        			}
	        		}
	        	}
	        }
		} catch (Exception e){
	    	e.printStackTrace();
	    	throw new ServletException(e);
	    }
	}
	
	
	protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {
			Map<String,String[]> queryParams = request.getParameterMap();
			String[] imageNumVals = queryParams.get("imgnums");
			HashSet<Integer> imgNums = new HashSet<Integer>();
			for (String numsStr : imageNumVals) {
				for (String numStr : numsStr.split(",")) {
					imgNums.add(Integer.valueOf(numStr));
				}
			}
			String partnerId = (String) request.getAttribute("partnerId");
			String coupleId = DB.getCoupleId(partnerId);
			DB.deleteCoupleImages(coupleId, imgNums);
		} catch (Exception e){
	    	e.printStackTrace();
	    	throw new ServletException(e);
	    }
		
	}
}
