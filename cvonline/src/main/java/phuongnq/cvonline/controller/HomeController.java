package phuongnq.cvonline.controller;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.itextpdf.text.Document;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.tool.xml.XMLWorkerHelper;

import phuongnq.cvonline.util.CommonUtil;

@Controller
public class HomeController {
	
	@Autowired
	ServletContext context; 
	
	@Autowired
	ResourceLoader resourceLoader;
	
	@RequestMapping(value="/")
	public ModelAndView homePage(Model model) throws IOException {
		ModelAndView mav = new ModelAndView("home");
		final Resource fileResource = resourceLoader.getResource("classpath:zzz/abc.properties");
		
		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(fileResource.getInputStream()));

		String line;

		// read from the urlconnection via the bufferedreader
      	try {
      		while ((line = bufferedReader.readLine()) != null)
      		{
      			String values[] = line.split("=");
      			System.out.println(values[1] + "/n");
      		}
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	    try {
			bufferedReader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return mav;
	}

	@RequestMapping(value="/export")
	public void exportPDF(HttpServletRequest request, HttpServletResponse response) {
		
		StringBuilder content = new StringBuilder();

	    // many of these calls can throw exceptions, so i've just
	    // wrapped them all in one try/catch statement.
	    try
	    {
	      // create a url object
	      URL url = new URL(CommonUtil.Config.domain + "/cvonline");

	      // create a urlconnection object
	      URLConnection urlConnection = url.openConnection();

	      // wrap the urlconnection in a bufferedreader
	      BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));

	      String line;

	      // read from the urlconnection via the bufferedreader
	      while ((line = bufferedReader.readLine()) != null)
	      {
	        content.append(line + "\n");
	      }
	      bufferedReader.close();
	    }
	    catch(Exception e)
	    {
	      e.printStackTrace();
	    }
	    String uploadPath = context.getRealPath("") + File.separator;

	    File filePDF = new File(uploadPath+"HTMLtoPDF.pdf");
	    System.out.println(filePDF.getAbsolutePath());
		try {
			
			OutputStream file = new FileOutputStream(filePDF);
			Document document = new Document();
			PdfWriter writer = PdfWriter.getInstance(document, file);
			document.open();
			InputStream is = new ByteArrayInputStream(content.toString().getBytes());
			XMLWorkerHelper.getInstance().parseXHtml(writer, document, is);
			document.close();
			file.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		try {
			FileInputStream fileInuptStream1 = new FileInputStream(filePDF);
	        BufferedInputStream bufferedInputStream1 = new BufferedInputStream(fileInuptStream1);
	        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
	        int start = 0;
	        int length = 1024;
	        int offset = -1;
	        byte[] buffer = new byte[length];

	        // Write BufferedInputStream
	        while ((offset = bufferedInputStream1.read(buffer, start, length)) != -1) {
	            byteArrayOutputStream.write(buffer, start, offset);
	        }

	        // Close inputStream & outputstream
	        bufferedInputStream1.close();
	        byteArrayOutputStream.flush();
	        byteArrayOutputStream.close();
	        
	        response.addHeader("Content-Disposition", "attachment;filename=\"hello.pdf\"");
	        response.setContentType("application/octet-stream ; charset=utf-8");
	        
	    	OutputStream out = response.getOutputStream();
	    	out.write(byteArrayOutputStream.toByteArray());
	        out.flush();
	        out.close();
	        filePDF.delete();
		} catch (Exception e1) {
			e1.printStackTrace();
		}
	}

}
