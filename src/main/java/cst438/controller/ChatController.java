package cst438.controller;

import javax.servlet.http.HttpServletRequest;
 
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
 
@Controller
public class ChatController {
 
    @GetMapping("/chat")
    public String index(HttpServletRequest request, Model model) {
        String username = (String) request.getSession().getAttribute("username");
        String id = (String) request.getSession().getAttribute("id");
          
        if (username == null || username.isEmpty() || id == null || id.isEmpty()) {
            return "redirect:/chat_login";
        }
 
        model.addAttribute("username", username);
        model.addAttribute("id", id);
        
        return "chat";
    }
 
    @GetMapping("/chat_login")
    public String showLoginPage(Model model) {
        return "chat_login";
    }
 
    @PostMapping("/chat_login")
    public String doLogin(HttpServletRequest request, @RequestParam("username") String username, @RequestParam("id") String id) {
        username = username.trim();
 
        if (username.isEmpty() || id.isEmpty()) {
            return "chat_login";
        }
        
        request.getSession().setAttribute("username", username);
        request.getSession().setAttribute("id", id);

        return "redirect:/chat";
    }
 
    @GetMapping("/chat_logout")
    public String logout(HttpServletRequest request) {
    	
        request.getSession(true).invalidate();     
        return "redirect:/chat_login";
    } 
}