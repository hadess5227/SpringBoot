package cst438.controller;

import java.util.List;
import java.util.Optional;

import javax.mail.internet.MimeMessage;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLConnection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import cst438.config.AzureBlobAdapter;

import cst438.domain.Link;
import cst438.repository.LinkRepository;
import cst438.service.CustomUserDetailsService;

import cst438.domain.File;
import cst438.repository.FileRepository;

@Controller
public class LinkController {
	@Autowired
    private FileRepository fileRepository;

	@Autowired
	private LinkRepository linkRepository;

	@Autowired
    private AzureBlobAdapter azureBlobAdapter;
	
	@Autowired
	private CustomUserDetailsService userDetailsService;
	
	@Autowired
    private JavaMailSender mailSender;
	
    @GetMapping("/links/create")
    public String prepareBoard(Model model) {
    	return "board_create";
    }
    
    @PostMapping("/links/create")
    public String createBoard(HttpServletRequest request, @RequestParam("title") String title,  @RequestParam("titleselect") String titlefont,@RequestParam("titlecolor") String titlecolor,@RequestParam("emoji1") String emoji1,@RequestParam("emoji2") String emoji2,@RequestParam("backcolor") String backcolor,@RequestParam("file") MultipartFile multipartFile, Model model) throws IOException{
    	Long id = userDetailsService.getLoggedUserId();
		Link link = new Link();
		link.setUserId(id);
		link.setDate(new java.util.Date());
		link.setTitle(title);
		link.setEmoji1(emoji1);
		link.setEmoji2(emoji2);
		link.setTitlefont(titlefont);
		link.setTitlecolor(titlecolor);
		link.setBackcolor(backcolor);

		if(!multipartFile.isEmpty() && multipartFile != null)
		link.setName(multipartFile.getOriginalFilename());
		
		linkRepository.save(link);

		if(!multipartFile.isEmpty() && multipartFile != null)
		azureBlobAdapter.upload(multipartFile);
		
		return "redirect:/links";
    }	
	
	@GetMapping("/links/send")
	public String provideLink(Model model) {
		Long id = userDetailsService.getLoggedUserId();
		List<Link> links = linkRepository.findByUserId(id);
		model.addAttribute("links", links);
	    return "send_links";
	}
	
	@PostMapping("/links/send")
	public String sendLink(
			@RequestParam("email1") String email1,
			@RequestParam("email2") String email2,
			@RequestParam("email3") String email3,
			@RequestParam("email3") String email4,
			@RequestParam("email3") String email5,
			@RequestParam("linkId") String boardId,
			@RequestParam("subject") String subject,
			@RequestParam("message") String message,
			
			Model model) {
		
		try {
			if ((email1 != null) && (!email1.isEmpty()))
				sendEmail(email1, boardId, subject, message);
			if ((email2 != null) && (!email2.isEmpty()))
				sendEmail(email2, boardId, subject, message);
			if ((email3 != null) && (!email3.isEmpty()))
				sendEmail(email3, boardId, subject, message);
			if ((email4 != null) && (!email4.isEmpty()))
				sendEmail(email4, boardId, subject, message);
			if ((email5 != null) && (!email5.isEmpty()))
				sendEmail(email5, boardId, subject, message);		
		}
		catch (Exception ex) {
			return "Error in sending email: " + ex;
		}
				
	    return "redirect:/links/send";
	}
	
	@GetMapping("/links/delete/{id}")
	public String deleteLink(@PathVariable String id, Model model) throws Exception {
		linkRepository.deleteById(id);
		System.out.println("/link/delete/"+id);
		
	    return "redirect:/links";
	}
	
	@GetMapping("/links/update/{id}")
	public String updateLink(@PathVariable String id, Model model) throws Exception {		
		Optional<Link> links = linkRepository.findById(id);
		Link link = links.get();
		model.addAttribute("link", link);
		
	    return "board_update";
	}
	
	@PostMapping("/links/update/{id}")
	public String updateLink2(@PathVariable String id, @ModelAttribute("link") Link link, 
			@RequestParam("title") String title, @RequestParam("titleselect") String titlefont,@RequestParam("titlecolor") String titlecolor,@RequestParam("emoji1") String emoji1,@RequestParam("emoji2") String emoji2, @RequestParam("backcolor") String backcolor,		
			@RequestParam("file") MultipartFile multipartFile, Model model) throws Exception {
		
		link.setUserId(userDetailsService.getLoggedUserId());
		link.setDate(new java.util.Date());
		link.setTitle(title);
		link.setBackcolor(backcolor);

		link.setEmoji1(emoji1);
		link.setEmoji2(emoji2);
		link.setTitlefont(titlefont);
		link.setTitlecolor(titlecolor);
		
		if(!multipartFile.isEmpty() && multipartFile != null)
			link.setName(multipartFile.getOriginalFilename());
		linkRepository.save(link);

		if(!multipartFile.isEmpty() && multipartFile != null)
		azureBlobAdapter.upload(multipartFile);
	    return "redirect:/links";
	}
	
	@GetMapping("/links")
	public String getLinks(Model model) throws Exception {	
		Long id = userDetailsService.getLoggedUserId();
		List<Link> links = linkRepository.findByUserId(id);
		model.addAttribute("links", links);
		
	    return "links";
	}
	
	private void sendEmail(String email, String boardId, String subject, String textmessage) throws Exception{
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message);
         
        helper.setTo(email);
        helper.setSubject(subject);
        helper.setText(textmessage + "\n\n" + "Playboard Link: " + "https://cst499capstoneproject.herokuapp.com/board?id=" + boardId);
//        helper.addAttachment("img/playboard_logo.png", new ClassPathResource("img/playboard_logo.png"));
        mailSender.send(message);
    }
}