package com.example.controller;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.model.Cart;
import com.example.model.Profile;

@Controller
public class loginController {
	
	 @Autowired
	  JdbcTemplate jdbcTemplate;
	
	@RequestMapping(value = "/login", method = RequestMethod.GET)
	String add(Model model, HttpServletRequest request) {
		HttpSession session = request.getSession(false);
		if(session == null) {
    			return "login"; //if not logged in
		}
		return "redirect:/"; //if already logged in, send to index
    }
	
	@RequestMapping(value = "/login", method = RequestMethod.POST)
	String login(Model model, HttpServletRequest request, @RequestParam("email") String email, @RequestParam("password") String password) {
		HttpSession session = request.getSession(false);
		if(session != null) { //If already logged in, forward to main page.
			return "redirect:/";
		}
		Profile profile = validateUser(email, password);
		if(profile != null) {
			session = request.getSession(); // Creating a session
			session.setAttribute("profile", profile);
			session.setAttribute("cart", new Cart());
			return "redirect:/";
		}else {
			String header = "Login failed";
			String inner = "Invalid username or password";
			model.addAttribute("header", header);
			model.addAttribute("body", inner);
			return "template";
		}
    }
	
	@RequestMapping(value = "/logout")
	String logout(Model model, HttpServletRequest request) {
		HttpSession session = request.getSession(false);
		if(session != null) {
    			session.invalidate();
		}
		return "redirect:/"; //send to main controller.
    }
	
    public Profile validateUser(String email, String password) {
    String sql = "select * from profile where email='" + email + "' and password='" + password
    + "'";
    List<Profile> users = jdbcTemplate.query(sql, new UserMapper());
    return users.size() > 0 ? users.get(0) : null;
    }
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
