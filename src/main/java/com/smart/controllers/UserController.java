package com.smart.controllers;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;

import java.util.Optional;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;


import com.smart.dao.ContactRepository;
import com.smart.dao.UserRepository;
import com.smart.entities.Contact;
import com.smart.entities.User;
import com.smart.helper.Message;

@Controller
@RequestMapping("/user")
public class UserController {
	
	
	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private ContactRepository contactRepository;
	
	//Method for adding common data to all the response
	@ModelAttribute
	public void addCommonData(Model model,Principal principal) {
		String userName = principal.getName();
		
		System.out.println("UserName = "+userName);
		
		User user = this.userRepository.getUserByUserName(userName);
		
		System.out.println("USER = " + user);
		
		model.addAttribute(user);
		
	}//End of addCommonData method
	
	

	@GetMapping("/index")
	public String dashboard(Model model,Principal principal) {
		model.addAttribute("title","Home - Smart Contact Manager");
		
		return "normal/user_dashboard";
	}//End of dashboard() method
	
	
	//Open Add form handler
	@GetMapping("/add-contact")
	public String openAddContactForm(Model model) 
	{
		model.addAttribute("title","Add-contact - Smart Contact Manager");
		model.addAttribute("contact",new Contact());		
		
		return "normal/add_contact_form";
	}//End of openAddContactForm() method
	
	
	//Processing add contact form
	@PostMapping("/process-contact")
	public String processContact(@ModelAttribute("contact") Contact contact, 
								 @RequestParam("profileImage")MultipartFile file,
								 Principal principal,HttpSession session) {
		
		try {
			
			String name = principal.getName();
			User user = this.userRepository.getUserByUserName(name);
			
			//processing and uploading file
			if(file.isEmpty())
			{
				//if the file is empty then try our message
				System.out.println("File empty");
				contact.setImage("contact.png");
				
			}else {
				//upload the file to folder and update the name to contact 
				contact.setImage(file.getOriginalFilename());
				
				File saveFile = new ClassPathResource("static/img").getFile();
				
				Path path = Paths.get(saveFile.getAbsolutePath()+File.separator+file.getOriginalFilename());
				
				Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
				System.out.println("Image uploaded");
			}
			
			contact.setUser(user);
			
			user.getContacts().add(contact);
			this.userRepository.save(user);

			System.out.println("Data := "+contact);
			System.out.println("Added to db");
		
			//Message Success send to user
			session.setAttribute("message", new Message("Your contact is added !! Add more", "success"));
			
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("ERROR : "+e.getMessage());
			e.printStackTrace();
			//Message Error send to user
			session.setAttribute("message", new Message("Something went wrong !!Try Again", "danger"));
		}
			
		
		return "redirect:/user/add-contact";
	}//End of the processContact() method
	
	
	//Show contacts handler
	//Per page = 5[n]
	//current page = 0[page]
	@GetMapping("/show-contacts/{page}")
	public String showContacts(@PathVariable("page") Integer page,Model model,Principal principal) {
		model.addAttribute("title","Show user contact - Smart Contact Manager");
		
		String userName = principal.getName();
		User user = this.userRepository.getUserByUserName(userName);
		
		Pageable pageable = PageRequest.of(page, 5);
		
		Page<Contact> contacts = this.contactRepository.findContactsByUser(user.getId(),pageable);
		
		model.addAttribute("contacts",contacts);
		model.addAttribute("currentPage",page);
		
		model.addAttribute("totalPages",contacts.getTotalPages());
		
		return "normal/show_contacts";
	}//End of showContacts() method

	
	//Showing particular contact details
	@GetMapping("/{cId}/contact")
	public String showContactDetail(@PathVariable("cId")Integer cId,Model model,Principal principal) 
	{
		System.out.println("cId " + cId);
		
		//returning contact details
		Optional<Contact> contactOptional = this.contactRepository.findById(cId);
		Contact contact = contactOptional.get();
		
		
		//
		String userName = principal.getName();
		User user = this.userRepository.getUserByUserName(userName);
		
		if(user.getId() == contact.getUser().getId())
		{
			model.addAttribute("title",contact.getName());
			model.addAttribute("contact",contact);
		}
		
		
		
		return "normal/contact_detail";
	}//End of the method
	
	
	//Delete contact handler
	@GetMapping("/delete/{cId}")
	public String deleteContact(@PathVariable("cId")Integer cId,Model model,Principal principal,HttpSession session) {
		
		Optional<Contact> contactOptional = this.contactRepository.findById(cId);
		
		Contact contact = contactOptional.get();
		
		//deleting contact(Using checking users )
//		String userName = principal.getName();						(Convert this two lines into one as follows)
//		User user = this.userRepository.getUserByUserName(userName);
		
		User user = this.userRepository.getUserByUserName(principal.getName());
		
		if(user.getId() == contact.getUser().getId())
		{
			//remove photo
			
				
			//remove user from contact
			
			//remove contact
			//this.contactRepository.delete(contact);   (Bug in this removel)
			user.getContacts().remove(contact);
			
			this.userRepository.save(user);		//User changes save by this line
			
			session.setAttribute("message",new Message("Contact deleted successfully","success"));
		}
		else {
			session.setAttribute("message",new Message("Error Accurs","danger"));
		}
		
		
		return "redirect:/user/show-contacts/0";
	}//End of deleteContact() method
	
	
	//open update form handler
	@PostMapping("/update-contact/{cId}")
	public String updateForm(@PathVariable("cId")Integer cId,Model model) {
		
		model.addAttribute("title","Update Contact");
		
		
		Contact contact = this.contactRepository.findById(cId).get();
		model.addAttribute("contact",contact);
		return "normal/update_form";
	}//End of updateForm() method
	
	
	//update contact handler
	@PostMapping("/process-update")
	public String updateHandler(@ModelAttribute Contact contact,@RequestParam("profileImage") MultipartFile file,
					Model model,HttpSession session,Principal principal)
	{

		try {
			
			//old contact details
			Contact oldcontactDetail = this.contactRepository.findById(contact.getcId()).get();
			
			if(!file.isEmpty())
			{
				//file work
				//rewrite
				//delete old photo	
				File deleteFile = new ClassPathResource("static/img").getFile();
				
				File file1 = new File(deleteFile, oldcontactDetail.getImage());
				file1.delete();
				
				
				
				//update new photo
				File saveFile = new ClassPathResource("static/img").getFile();
				
				Path path = Paths.get(saveFile.getAbsolutePath()+File.separator+file.getOriginalFilename());
				
				Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
				
				contact.setImage(file.getOriginalFilename());
				
				
			}//End of if case
			
			else {
				contact.setImage(oldcontactDetail.getImage());
				
			}//End of else statement
			
			User user = this.userRepository.getUserByUserName(principal.getName());
			
			contact.setUser(user);
			
			this.contactRepository.save(contact);
			
			session.setAttribute("message", new Message("Your contact is updated","success"));
			
		} catch (Exception e) {
			e.printStackTrace();
			session.setAttribute("message", new Message("Something went wrong , Your contact isn't updated","danger"));
		}
		
		System.out.println("Name := "+contact.getName());
		System.out.println("Id := "+contact.getcId());
		//this will redirect perticular contact
		return "redirect:/user/"+contact.getcId()+"/contact";
		
		
		//this will direct show all contacts
//		return "redirect:/user/show-contacts/0";
	}//End of the method
	
	
	
	//Your profile handler
	@GetMapping("/profile")
	public String yourProfile(Model model) 
	{
		model.addAttribute("title","Profile Page");
		return "normal/profile";
	}//End of the method
	
	
	//Open settings handler
	@GetMapping("/settings")
	public String openSettings(Model model) {
		
		model.addAttribute("title","Settings ");
		return "normal/settings";
	}//ENd of the openSettings() method
	
	
	//Change password handler
	@PostMapping("/change-password")
	public String changePassword(@RequestParam("oldPassword")String oldPassword,@RequestParam("newPassword")String newPassword,
								Model model,Principal principal,HttpSession session) {
		System.out.println("Old :=" +oldPassword);
		System.out.println("New :=" +newPassword);
		
		//checking old password matches or not
		String userName = principal.getName();
		User currenruser = this.userRepository.getUserByUserName(userName);
		
		System.out.println(currenruser.getPassword());
		
		if(this.bCryptPasswordEncoder.matches(oldPassword, currenruser.getPassword()))
		{
			//Change the password
			currenruser.setPassword(this.bCryptPasswordEncoder.encode(newPassword));
			this.userRepository.save(currenruser);
			session.setAttribute("message", new Message("Your password successfully changed !! Login with new password","success"));
			return "redirect:/logout";
		}//End of the if statement
		else {
			//Error . . .
			session.setAttribute("message", new Message("Something went wrong , Your contact isn't updated","danger"));
			return "redirect:/user/settings";
		}
		
		
	}//End of changePassword handler
	
	
	
	
	
	
}//End of UserController class
