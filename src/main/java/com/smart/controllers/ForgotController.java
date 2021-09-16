package com.smart.controllers;

import java.util.Random;

import javax.mail.Session;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.smart.dao.UserRepository;
import com.smart.entities.User;
import com.smart.helper.Message;
import com.smart.services.EmailService;

@Controller
public class ForgotController {
	
	@Autowired
	private EmailService emailService;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;

	
	
	//Email id form open handler
	@GetMapping("/forgot")
	public String openEmailForm(Model model) {
		
		model.addAttribute("title","Forgot password");
		
		return "forgot_email_form";
	}//End of the openEmailForm handler
	
	
	//OTP send handler
	@PostMapping("/send-otp")
	public String sendOTP(@RequestParam("email") String email,Model model,HttpSession session) {
		model.addAttribute("title","Verify OTP");
		
		System.out.println("Email :- "+email);
		
		//generating otp of 4 digit
		
		
//		Random random = new Random(1000);
//		
//		int otp = random.nextInt(999999);
		
		Random r = new Random( System.currentTimeMillis() );
	    int otp =  ((1 + r.nextInt(2)) * 100000 + r.nextInt(100000));
		System.out.println("OTP :-" +otp);
		
		//Write code for send otp to email Address
		String subject = "OTP from SCM";
		String message = "<div style='border:1px solid #e2e2e2; padding:20px;'>"
							+"<h1  class='text-center'>"
							+" OTP = " 
							+ otp 
							+ "</h1>"
							+"</div>";
		String to = email;
		
		boolean flag = this.emailService.sendEmail(subject, message, to);
		
		if (flag) {
			
			session.setAttribute("myotp", otp);
			session.setAttribute("email", email);
			
			return "verify_otp";
		}//End of if
		else {
			session.setAttribute("message",new Message("Check Your email !!","danger"));
			
			return "forgot_email_form";
		}//End of else
		
		
	}//End of sendOTP handler
	
	
	//Verifying OTP
	@PostMapping("/verify-otp")
	public String verifyOTP(@RequestParam("otp") int otp,HttpSession session,Model model) {
		
		
		
		
		int myOTP = (int) session.getAttribute("myotp");
		
		String email = (String) session.getAttribute("email");
		if(myOTP == otp) {
			//password change form
			User user = this.userRepository.getUserByUserName(email);
			
			if(user == null) {
				//send error message
				session.setAttribute("message",new Message("Check your email,User does not exists with this email !!","alert-danger"));
				return "redirect:/forgot";
			}
			else {
				//send change password form
				model.addAttribute("title","Change Your Password");
				return "password_change_form";
			}
			
			
		}else {
			session.setAttribute("message",new Message("You have entered wrong OTP !!","alert-danger"));
			return "verify_otp";
		}
	}//End of the varifyOTP() handler
	
	
	//change password
	@PostMapping("/change-password")
	public String changeStringPassword(@RequestParam("newpassword")String newpassword,HttpSession session) {
		String email = (String) session.getAttribute("email");
		User user = this.userRepository.getUserByUserName(email);
		user.setPassword(this.bCryptPasswordEncoder.encode(newpassword));
		this.userRepository.save(user);
		
		return "redirect:/login?Change = Password  changed successfully !!";
	}//End of change password handler
	
}//End of the controller
