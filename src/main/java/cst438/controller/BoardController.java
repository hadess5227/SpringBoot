package cst438.controller;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLConnection;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import cst438.config.AzureBlobAdapter;
import cst438.domain.File;
import cst438.domain.Link;
import cst438.domain.Comment;
import cst438.repository.CommentRepository;
import cst438.repository.FileRepository;
import cst438.repository.LinkRepository;
import cst438.service.CustomUserDetailsService;

import java.text.ParseException;  
import java.text.SimpleDateFormat;  
import java.util.Date;  
import java.util.Locale;  

@Controller
public class BoardController {
	
	@Autowired
    private FileRepository fileRepository;
	
    @Autowired
    private AzureBlobAdapter azureBlobAdapter;
    
    @Autowired
	private CustomUserDetailsService userDetailsService;
    
	@Autowired
	private LinkRepository linkRepository;
	
	@Autowired
	private CommentRepository commentRepository;
    
    @GetMapping("/board")
    public String index(HttpServletRequest request, @RequestParam String id, Model model) throws Exception {
        
    	String username = (String) request.getSession().getAttribute("username");    
        String boardId = (String) request.getSession().getAttribute("id");
        
        if (boardId == null || boardId.isEmpty() || (!boardId.equals(id))) {
        	request.getSession().setAttribute("id", id);
        	boardId = (String) request.getSession().getAttribute("id");
        }
        
        if (username == null || username.isEmpty()) {
            return "redirect:/board/login?id=" + boardId;
        }
 
        model.addAttribute("username", username);
        model.addAttribute("id", id);
        
        Long userId = userDetailsService.getLoggedUserId(); 
        model.addAttribute("userId", userId);
        
        List<File> files = fileRepository.findByBoardId(boardId);      
        files.forEach(file ->
        {
            // SimpleDateFormat formatter = new SimpleDateFormat("E MMM dd hh:mm aa");  
            // String strDate = formatter.format(file.getDate());  
            // System.out.println("Date Format with dd-M-yyyy hh:mm:ss : "+strDate);
            // try{
            // file.setDate(new SimpleDateFormat("E MMM dd hh:mm aa").parse(strDate));
                
            // }
            // catch(Exception e){

            // }
            file.setNumComment(commentRepository.findByFileId(file.getId()).size());
//        	byte[] bytes = azureBlobAdapter.downloadFile("cst499capstoneproject", files.get(i).getName());
//        	files.get(i).setBase64("data:" + files.get(i).getType() + ";base64,"+ Base64.getMimeEncoder().encodeToString(bytes));	 
        }); 
        
//        files.forEach(file->{  
//        	file.setBase64("data:video/mp4;base64,"+ Base64.getMimeEncoder().encodeToString(file.getData()));	 
//        });
        
        model.addAttribute("files", files);
        
        Optional<Link> links = linkRepository.findById(id);
        Link link = links.get();
       
        
        model.addAttribute("background", link.getBackcolor());
        model.addAttribute("title", link.getTitle());
        model.addAttribute("emoji1", link.getEmoji1());
        model.addAttribute("emoji2", link.getEmoji2());
        model.addAttribute("titlefont", link.getTitlefont());
        model.addAttribute("titlecolor", link.getTitlecolor());
        model.addAttribute("titlename", link.getName());
        
        return "board";
    }	
	
	@GetMapping("/board/login")
    public String showLoginPage(@RequestParam String id, Model model) {
		model.addAttribute("id", id);
        return "board_login";
    }
 
    @PostMapping("/board/login")
    public String doLogin(HttpServletRequest request, @RequestParam("username") String username, Model model) {
        username = username.trim();
        
        if (username == null || username.isEmpty()) {	
        	return "redirect:/board/login?id=" + (String) request.getSession().getAttribute("id");
        }
        
        request.getSession().setAttribute("username", username);
        model.addAttribute("username", username);
       
        return "redirect:/board?id=" + (String) request.getSession().getAttribute("id");
    }
    
	@GetMapping("/board/upload")
    public String prepareUpload(HttpServletRequest request, Model model) {
		model.addAttribute("id", (String) request.getSession().getAttribute("id"));
        return "board_upload";
    }
    
    
	
    @PostMapping("/files/upload")
    public String uploadFile(HttpServletRequest request, @RequestParam("file") MultipartFile multipartFile, 
    		@RequestParam("description") String description) throws IOException {
        System.out.println("File Size in Bytes - " + multipartFile.getBytes().length);
        
        if ((multipartFile != null) && !multipartFile.isEmpty()) { 
	        File file = new File();
	        file.setName(multipartFile.getOriginalFilename());
	        file.setType(multipartFile.getContentType());
	//        file.setData(multipartFile.getBytes());        
	        file.setBoardId((String) request.getSession().getAttribute("id"));
	        file.setDescription(description);
	        file.setDate(new java.util.Date());
	        file.setOwner((String) request.getSession().getAttribute("username"));
	        file.setNumComment(0);
	        fileRepository.save(file);
	        
	//        URI url = azureBlobAdapter.upload(multipartFile);
	        azureBlobAdapter.upload(multipartFile);
        }
                       
        return "redirect:/board?id=" + (String) request.getSession().getAttribute("id");
    }
    
	@GetMapping("/board/post")
    public String prepareLink(HttpServletRequest request, Model model) {	
		model.addAttribute("id", (String) request.getSession().getAttribute("id"));
        return "board_post";
    }

    @PostMapping("/comment/{id}")
	public String addComment(HttpServletRequest request, @PathVariable long id, @RequestParam("comment") String comment, @RequestParam("name") String name) throws Exception {
		System.out.println("hadess"+id);
        if(name != null && !name.isEmpty())
        {
            Comment _comment = new Comment();
            _comment.setName(name);
            _comment.setComment(comment);
            _comment.setFileId(id);
            commentRepository.save(_comment);
        }
		return "redirect:/detailPage/" + id;
	}

    @GetMapping("/detailPage/{id}")
    public String detailPage(HttpServletRequest request, Model model,  @PathVariable long id) {
    	model.addAttribute("id", (String) request.getSession().getAttribute("id"));
        Optional<File> file = fileRepository.findById(id);
		String fileName = file.get().getName();
        String description = file.get().getDescription();
        List<Comment> comments = commentRepository.findByFileId(id);
        System.out.println(comments);
        model.addAttribute("type", file.get().getType());
        model.addAttribute("fileid", id);
        model.addAttribute("filename", (String)fileName);
        model.addAttribute("description", (String)description);
        model.addAttribute("comments", comments);
        model.addAttribute("comment_count", comments.size());

        return "detailPage";
    }

    @PostMapping("/files/post")
    public String postLink(HttpServletRequest request, @RequestParam("link") String link, @RequestParam("description") String description) {
        if ((link!= null) && !link.isEmpty()) {
	        File file = new File();
	        String youtubeLink = "https://www.youtube.com/embed/" + getYoutubeId(link);	        
	        file.setName(youtubeLink);
	        file.setType("video:link");      
	        file.setBoardId((String) request.getSession().getAttribute("id"));
	        file.setDescription(description);
	        file.setDate(new java.util.Date());
	        file.setNumComment(0);
	        file.setOwner((String) request.getSession().getAttribute("username"));
	        fileRepository.save(file);
        }
        
        return "redirect:/board?id=" + (String) request.getSession().getAttribute("id");
    }
    
    @GetMapping("/files/{filename}")
    public void getImage(HttpServletRequest request, @PathVariable("filename") String filename, HttpServletResponse response) throws Exception {
        List<File> files = fileRepository.findByNameAndBoardId(filename, (String) request.getSession().getAttribute("id"));

        byte[] bytes2 = azureBlobAdapter.downloadFile("cst499capstoneproject", filename);
            InputStream is = new BufferedInputStream(new ByteArrayInputStream(bytes2));
            String mimeType = URLConnection.guessContentTypeFromStream(is);
            response.setContentType(mimeType);
            OutputStream outputStream = response.getOutputStream();
            outputStream.write(bytes2);
            outputStream.flush();
            outputStream.close();
            System.out.println("sdfsdfsdfsdfsdf:"+filename+":"+request.getSession().getAttribute("id"));
//         if(!files.isEmpty() && files.get(0).getType().contains("image")) {
// //            byte[] bytes = file.get().getData();
// System.out.println("sdfsdfsdfsdfsdf");
//             byte[] bytes2 = azureBlobAdapter.downloadFile("cst499capstoneproject", filename);
//             InputStream is = new BufferedInputStream(new ByteArrayInputStream(bytes2));
//             String mimeType = URLConnection.guessContentTypeFromStream(is);
//             response.setContentType(mimeType);
//             OutputStream outputStream = response.getOutputStream();
//             outputStream.write(bytes2);
//             outputStream.flush();
//             outputStream.close();
//         }
    }

    
    
    
	@GetMapping("/files/delete/{id}")
	public String updateReservaion(HttpServletRequest request, @PathVariable long id) throws Exception {
		Optional<File> file = fileRepository.findById(id);
		String fileName = file.get().getName();
		fileRepository.deleteById(id);
		azureBlobAdapter.deleteBlob("cst499capstoneproject", fileName);
		System.out.println("/files/delete/"+id);
		return "redirect:/board?id=" + (String) request.getSession().getAttribute("id");
	}
	
	public static String getYoutubeId(String url) {
        String pattern = "https?:\\/\\/(?:[0-9A-Z-]+\\.)?(?:youtu\\.be\\/|youtube\\.com\\S*[^\\w\\-\\s])([\\w\\-]{11})(?=[^\\w\\-]|$)(?![?=&+%\\w]*(?:['\"][^<>]*>|<\\/a>))[?=&+%\\w]*";

        Pattern compiledPattern = Pattern.compile(pattern,
                Pattern.CASE_INSENSITIVE);
        Matcher matcher = compiledPattern.matcher(url);
        if (matcher.find()) {
            return matcher.group(1);
        }/*from w  w  w.  j a  va  2 s .c om*/
        return null;
    }
}