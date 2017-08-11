package com.example.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class loginController {
	@RequestMapping(value = "/login", method = RequestMethod.GET)
	String add(Model model, HttpServletRequest request) {
    		return "login";
    }
	@ResponseBody
	@RequestMapping(value = "/login", method = RequestMethod.POST)
	String login(HttpServletRequest request, @RequestParam("email") String email, @RequestParam("password") String password) {
		
		if(email.equalsIgnoreCase("gemal@gmail.com") && password.equalsIgnoreCase("gemal")) {
			return "login success";
		}else {
			return "login fail";
		}
    }
}
