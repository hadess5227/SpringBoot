package cst438.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name = "link")
public class Link {
    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    private String id;
    
    @Column(name="user_id")
    private Long userId;
    
    @Column(name="date")
	private Date date;
    
    private String title;
    
    private int background;
	private String backcolor;
	private String emoji1;
	private String emoji2;
	private String titlefont;
	private String titlecolor;
	private String name;
    public Link() { };
    
	public Link(Long userId, Date date, String title, int background, String backcolor, String emoji1, String emoji2, String titlefont, String titlecolor, String name) {
		super();
		this.userId = userId;
		this.date = date;
		this.title = title;
		this.background = background;

		this.backcolor = backcolor;
		this.emoji1 = emoji1;
		this.emoji2 = emoji2;
		this.titlefont = titlefont;
		this.titlecolor = titlecolor;
		this.name = name;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}


	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}
	
	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public int getBackground() {
		return background;
	}

	public void setBackground(int background) {
		this.background = background;
	}

	public String getBackcolor() {
		return backcolor;
	}
	public void setBackcolor(String backcolor) {
		this.backcolor = backcolor;
	}

	public String getEmoji1() {
		return emoji1;
	}
	public void setEmoji1(String emoji1) {
		this.emoji1 = emoji1;
	}

	public String getEmoji2() {
		return emoji2;
	}
	public void setEmoji2(String emoji2) {
		this.emoji2 = emoji2;
	}


	public String getTitlefont() {
		return titlefont;
	}

	public void setTitlefont(String titlefont) {
		this.titlefont = titlefont;
	}
	public String getTitlecolor() {
		return titlecolor;
	}

	public void setTitlecolor(String titlecolor) {
		this.titlecolor = titlecolor;
	}
}