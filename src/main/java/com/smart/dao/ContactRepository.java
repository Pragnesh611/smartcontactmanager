package com.smart.dao;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.smart.entities.Contact;
import com.smart.entities.User;

public interface ContactRepository extends JpaRepository<Contact, Integer>{

	//pegination . . .
	//current page-page
	//contact per page - 5
	
	@Query("from Contact as c where c.user.id =:userId")
	public Page<Contact> findContactsByUser(@Param("userId")int userId,Pageable pePageable);
	
	//Searching
	public List<Contact> findByNameContainingAndUser(String name,User user);

	
	
}//End of ContactRepository
