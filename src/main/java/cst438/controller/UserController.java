package cst438.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import cst438.domain.User;
import cst438.repository.UserRepository;
import cst438.service.CustomUserDetailsService;

@Controller
public class UserController {
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private CustomUserDetailsService userDetailsService;
	
	@GetMapping("")
    public String viewHomePage(Model model) {
		Long id = userDetailsService.getLoggedUserId();
		model.addAttribute("id", id);
        return "index";
    }
	
	@GetMapping("/register")
	public String showRegistrationForm(Model model) {
		Long id = userDetailsService.getLoggedUserId();		
		model.addAttribute("id", id);
		model.addAttribute("user", new User());	     
	    return "signup_form";
	}
	
	@PostMapping("/process_register")
	public String processRegister(User user) {
	    BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
	    String encodedPassword = passwordEncoder.encode(user.getPassword());
	    user.setPassword(encodedPassword);     
	    userRepository.save(user);
	    return "register_success";
	}
	
	@GetMapping("/users")
	public String listUsers(Model model) {
	    List<User> listUsers = userRepository.findAll();
	    model.addAttribute("listUsers", listUsers);  
	    return "users";
	}
	
	@GetMapping("/step")
	public String Step(Model model) {	     
		Long id = userDetailsService.getLoggedUserId();
		model.addAttribute("id", id);
		return "step";
	}
	
	@GetMapping("/about")
	public String about(Model model) {	     
		Long id = userDetailsService.getLoggedUserId();		
		model.addAttribute("id", id);
		return "about";
	}
	
	@GetMapping("/contact")
	public String contact(Model model) {	     
		Long id = userDetailsService.getLoggedUserId();
		model.addAttribute("id", id);
		return "contact";
	}
}