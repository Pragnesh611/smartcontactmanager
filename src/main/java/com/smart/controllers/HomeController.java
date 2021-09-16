package com.smart.controllers;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;


import com.smart.dao.UserRepository;
import com.smart.entities.User;
import com.smart.helper.Message;

@Controller
public class HomeController {

	@Autowired
	private BCryptPasswordEncoder passwordEncoder;
	
	
	@Autowired
	private UserRepository userRepository;
	
	
	//Main page Handler
	@GetMapping("/")	
	public String home(Model model) {
		
		
		model.addAttribute("title","Home - Smart Contact Manager");
		return "home";
	}//End of home() method
	
	
	//About Handler
	@GetMapping("/about")	
	public String about(Model model) {
		
		
		model.addAttribute("title","About - Smart Contact Manager");
		return "about";     
	}//End of home() method
	
	
	
	//SignUp Handler
	@GetMapping("/signup")	
	public String signup(Model model) 
	{	
		//For sending the title to the base page
		model.addAttribute("title","Register - Smart Contact Manager");
		
		//For sending content on signup page
		model.addAttribute("user",new User());
		return "signup";     
	}//End of home() method
	
	//This handler for registering user
	@PostMapping("do_register")
	public String registerUser(@Validated @ModelAttribute("user") User user,BindingResult result1,
			@RequestParam(value = "agreement",defaultValue = "false") boolean agreement,
			Model model,HttpSession session) 
	{
		try {
			
			if(!agreement) {
				System.out.println("Agree terms and condition");
				throw new Exception("Agree terms and condition");
			}//End of agreement if()
			
			if (result1.hasErrors()) 
			{
				System.out.println("ERROR : " +result1.toString());
				model.addAttribute("user",user);
				return "signup";
			}//End of result if()
			
			else {
				user.setRole("ROLE_USER");
				user.setEnabled(true);
				user.setImageUrl("default.png");
				user.setPassword(passwordEncoder.encode(user.getPassword()));
				
				System.out.println("Agreement := "+agreement);
				System.out.println("User"+user);
				
				User result = userRepository.save(user);				
				
				model.addAttribute("user",new User());
				
				session.setAttribute("message", new Message("Successfully Register!!","alert-success"));
						
				return "signup";
			}//End of else
			
		} catch (Exception e) {
			// TODO: handle exception
			//user side error
			model.addAttribute("user",user);
			session.setAttribute("message", new Message(e.getMessage(),"alert-danger"));
			
			//Our side errors
			e.printStackTrace();
			
			return "signup";
		}		
	}//End of registerUser()
	
	
	//handler for custom login
	@GetMapping("/login")
	public String customLogin(Model model) {
		//For sending the title to the base page
		model.addAttribute("title","Login - Smart Contact Manager");
		
		return "login";
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
//	@Autowired
//	private UserRepository userRepository;
//	
//	
//	@GetMapping("/test")
//	@ResponseBody
//	public String test(User user) {
//		
//		
//		
//		user.setName("Christian");
//		user.setEmail("Chris@gmail.com");
//		
//		userRepository.save(user);
//		
//		
//		return "Working";
//	}//End of test() method

	
	
}//End of controller
