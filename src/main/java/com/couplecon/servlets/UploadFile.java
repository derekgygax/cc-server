package com.couplecon.servlets;

import java.io.File;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.ObjectMetadata;

import javax.servlet.annotation.WebServlet;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.Part;

/**
 * Servlet implementation class Upload
 */
@WebServlet("/uploadfile")
@MultipartConfig(fileSizeThreshold=1024*1024*2, // 2MB
maxFileSize=1024*1024*10,      // 10MB
maxRequestSize=1024*1024*50)   // 50MB
public class UploadFile extends HttpServlet {
	
	private static final String SAVE_DIR = "uploadFiles";
	
	private static final long serialVersionUID = 1L;

    /**
     * Default constructor. 
     */
    public UploadFile() {
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		AmazonS3 s3 = AmazonS3ClientBuilder.defaultClient();
        String path = "C:\\Users\\Kyle\\a\\france-temp\\upload.txt";
        File uploadFile = new File(path);
        s3.putObject("coupleconn-static","upload.txt",uploadFile);
        response.getWriter().append("Uploaded "+path);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		String appPath = request.getServletContext().getRealPath("");
        // constructs path of the directory to save uploaded file
        String savePath = appPath + File.separator + SAVE_DIR;
         
        // creates the save directory if it does not exists
        File fileSaveDir = new File(savePath);
        if (!fileSaveDir.exists()) {
            fileSaveDir.mkdir();
        }
         
        AmazonS3 s3 = AmazonS3ClientBuilder.defaultClient();
        for (Part part : request.getParts()) {
            String fileName = part.getName();
            // refines the fileName in case it is an absolute path
            fileName = new File(fileName).getName();
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentType("text/plain");
            s3.putObject("coupleconn-static",fileName, part.getInputStream(), metadata);
        }
        request.setAttribute("message", "Upload has been done successfully!");
//        getServletContext().getRequestDispatcher("/message.jsp").forward(
//                request, response);
	}

}
