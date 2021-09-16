package com.smart.services;

//import java.io.File;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.springframework.stereotype.Service;

@Service
public class EmailService {

	public boolean sendEmail(String subject,String message,String to) 
	{
		//rest of the code
		
		boolean f=false;
		
		String from = "pkprajapati6111999@gmail.com";
		
		String host = "smtp.gmail.com";
		
		//get the system properties
		Properties properties = System.getProperties();
		System.out.println("Properties := " +properties );
		
		
		//setting imp info to properties object
		properties.put("mail.smtp.host", host);
		properties.put("mail.smtp.port", "465");
		properties.put("mail.smtp.ssl.enable", "true");
		properties.put("mail.smtp.auth", "true");
		
		//Step:1 to get the session object
		Session session = Session.getInstance(properties, new Authenticator() {

			@Override
			protected javax.mail.PasswordAuthentication getPasswordAuthentication() {
				
				return new javax.mail.PasswordAuthentication(from, "Pragnesh@prk@8849513601");
			}
			
		});
		
		session.setDebug(true);
		//Step:2 compose the message[text,attachment,multimedia] {mime message send message}
		MimeMessage mimeMessage = new MimeMessage(session);
		
		try {
			
			//from email
			mimeMessage.setFrom(from);
			
			//adding recipient 
			mimeMessage.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
			
			//adding subject to message
			mimeMessage.setSubject(subject);
			
			//attachment
			//1) file path
//			String path = "C:\\Users\\Dell\\Desktop\\Icons\\ic_home.png";
			
			MimeMultipart mimeMultipart = new MimeMultipart();
			//text    				
			MimeBodyPart textMime = new MimeBodyPart(); 
			//file
//			MimeBodyPart fileMime = new MimeBodyPart();
		
			try {
				textMime.setContent(message, "text/html");
				
//				File file = new File(path);
//				fileMime.attachFile(file);
				
				mimeMultipart.addBodyPart(textMime);
//				mimeMultipart.addBodyPart(fileMime);
				
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			mimeMessage.setContent(mimeMultipart);    				
			
			
			//Step:3 send message using transport class
			Transport.send(mimeMessage);
			System.out.println("Sended successfully.....................");
			f= true;
			
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return f;
		
	}//End of the sendEmail() method
	
	
}//End of the class
