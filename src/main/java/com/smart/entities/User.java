package com.smart.entities;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;



@Entity
@Table(name="USER")
public class User 
{
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(nullable = false)
	private int id;
	
	@NotBlank(message = "Name field is required!!")
	@Size(min = 2,max = 15,message = "Name must inbetween 2-15 characters")
	@Column(nullable = false)
	private String name;
	
	@Email(regexp = "[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?")
	@Column(unique = true,nullable = false)
	private String email;
	
	@NotBlank(message = "Password field is required!!")
	@Column(nullable = false)
	private String password;
	
	@Column(nullable = false)
	private String role;
	
	@Column(nullable = false)
	private boolean enabled;
	
	@Column(nullable = false)
	private String imageUrl;
	
	@NotBlank(message = "Write something yourself!!")
	@Column(length = 500,nullable = false)
	private String about;
	
	
	//cascade all will do all the things automatic delete contacts by deleting user etc.
	@OneToMany(cascade = CascadeType.ALL,fetch = FetchType.LAZY,mappedBy = "user",orphanRemoval = true)
	
	private List<Contact> contacts = new ArrayList<Contact>();
	
	
	public User() {
		super();
		// TODO Auto-generated constructor stub
	}//End of default constructor


	public User(int id, String name, String email, String password, String role, boolean enabled, String imageUrl,
			String about, ArrayList<Contact> contacts) {
		super();
		this.id = id;
		this.name = name;
		this.email = email;
		this.password = password;
		this.role = role;
		this.enabled = enabled;
		this.imageUrl = imageUrl;
		this.about = about;
		this.contacts = contacts;
	}//End of parameterized constructor


	@Override
	public String toString() {
		return "User [id=" + id + ", name=" + name + ", email=" + email + ", password=" + password + ", role=" + role
				+ ", enabled=" + enabled + ", imageUrl=" + imageUrl + ", about=" + about + ", contacts=" + contacts
				+ "]";
	}//End of toString() method


	


	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}


	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}


	public String getEmail() {
		return email;
	}


	public void setEmail(String email) {
		this.email = email;
	}


	public String getPassword() {
		return password;
	}


	public void setPassword(String password) {
		this.password = password;
	}


	public String getRole() {
		return role;
	}


	public void setRole(String role) {
		this.role = role;
	}


	public boolean isEnabled() {
		return enabled;
	}


	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}


	public String getImageUrl() {
		return imageUrl;
	}


	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}


	public String getAbout() {
		return about;
	}


	public void setAbout(String about) {
		this.about = about;
	}


	public List<Contact> getContacts() {
		return contacts;
	}


	public void setContacts(List<Contact> contacts) {
		this.contacts = contacts;
	}
	
	
	
	//End of all getters and setters
	
	
	
	
	
	
	
}//End of the User class
