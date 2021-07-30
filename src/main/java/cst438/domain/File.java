package cst438.domain;



import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import java.text.ParseException;  
import java.text.SimpleDateFormat;  
import java.util.Date;  
import java.util.Locale;  

@Entity
@Table(name = "File")
public class File {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String type;
    private String boardId;
//    @Lob
//    private byte[] data;
    
    @Transient
    private String base64;
    
    private String description;
    
    private Date date;
    
    private int numComment;
    
    private String owner;
    
    public File() { }
    
	public File(Long id, String name, String type, String boardId, String description, Date date, int numComment, String owner) {
		super();
		this.id = id;
		this.name = name;
		this.type = type;
		this.boardId = boardId;
//		this.data = data;
		this.description = description;
		this.date = date;
		this.numComment = numComment;
		this.owner = owner;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getBoardId() {
		return boardId;
	}

	public void setBoardId(String boardId) {
		this.boardId = boardId;
	}

//	public byte[] getData() {
//		return data;
//	}
//
//	public void setData(byte[] data) {
//		this.data = data;
//	}

	public String getBase64() {
		return base64;
	}

	public void setBase64(String base64) {
		this.base64 = base64;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getDate() {
		SimpleDateFormat formatter = new SimpleDateFormat("E MMM dd h:mm aa");  
        String strDate = formatter.format(date);  
		return strDate;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public int getNumComment() {
		return numComment;
	}

	public void setNumComment(int numComment) {
		this.numComment = numComment;
	}

	public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}
}
