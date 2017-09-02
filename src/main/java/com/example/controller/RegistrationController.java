package com.example.controller;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.SQL;
import com.example.model.Profile;

@Controller
public class RegistrationController {
	
	 @Autowired
	  JdbcTemplate jdbcTemplate;
	 
	 @Autowired
	 public SQL SQLService;
	
	@RequestMapping(value = "/registration", method = RequestMethod.POST)
	String register(Model model, HttpServletRequest request, @RequestParam("email") String email, @RequestParam("firstName") String firstName, @RequestParam("lastName") String lastName, @RequestParam("password") String password) {
		//first, check to see if email is taken
		String sql = "";
		String header = "";
		String inner = "";
		if(emailExists(email)) {
			header = "Registration Incomplete.";
			inner = "That email seems to have already been taken. Please try a new email.";
		}else {
		//Then, insert record in DB.
			header = "Registration Successful.";
			inner = "Your account has successfully been registered, and is ready for use.";
			sql = "insert into profile (firstName, lastName, email, password) values ('"+firstName+"', '"+lastName+"', '"+email+"', '"+password+"');";
			this.jdbcTemplate.update(sql);
		}
		model.addAttribute("header", header);
		model.addAttribute("body", inner);
		return "template";
	}
	
    public boolean emailExists(String email) {
    String sql = "select * from profile where email='" + email
    + "'";
    List<Profile> users = jdbcTemplate.query(sql, new UserMapper());
    return users.size() > 0 ? true : false;
    }
	
    class UserMapper implements RowMapper<Profile> {
    	  public Profile mapRow(ResultSet rs, int arg1) throws SQLException {
    	    Profile profile = new Profile();
    	    profile.setEmail(rs.getString("email"));
    	    profile.setFirstName(rs.getString("firstname"));
    	    profile.setLastName(rs.getString("lastname"));
    	    profile.setEmail(rs.getString("email"));
    	    return profile;
    	  }
    		
    		
    	}
}
