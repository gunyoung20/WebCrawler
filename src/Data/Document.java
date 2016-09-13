package Data;

import java.util.ArrayList;

import Storage.DAO.WebSourceDAO;

public class Document {

	private String webId;
	private String titleNum;
	private String title;
	private String author;
	private Date date;
	private String story;
	private ArrayList<Comment> commentList;
	
	private ArrayList<String> sentenceFromStory;
	
	private Morpheme morphemeOfTitle;
	private ArrayList<Morpheme> morphemeOfSentence;
	private double sentimentScore;

	public Document(){ this("", "", "", "", "", ""); }
	public Document(String webId, String titleNum, String title, String author, String date, String story)	{
		this(webId, titleNum, title, author, date.equals("")?null:new Date(date), story);	}
	public Document(String webId, String titleNum, String title, String author, Date date, String story)	{
		this.webId = webId; this.titleNum = titleNum; this.title = title; this.author = author; 
		this.date = date; this.story = story;
	}

	public void setWebId(String wi){ webId = wi; }
	public void setTitleNum(String tn){ titleNum = tn; }
	public void setTitle(String t){ title = t; }
	public void setAuthor(String a){ author = a; }
	public void setDate(Date d){ date = d; }
	public void setStory(String s){ story = s; }
	
	public void setCommentList(ArrayList<Comment> cList){ commentList = cList; }
	public void addComment(Comment comment){ commentList.add(comment); }
	
	public void setSentenceFromStory(ArrayList<String> sSentence){ sentenceFromStory = sSentence; }
	
	public void setMorphemeOfTitle(Morpheme tMorpheme){ morphemeOfTitle = tMorpheme; }
	public void setMorphemeOfSentence(ArrayList<Morpheme> sMorpheme){ morphemeOfSentence = sMorpheme; }
	
	public void setSentimentScore(double score){ sentimentScore = score; }
	
	public String getWebUrl(){ return (new WebSourceDAO()).get(webId).getWebUrl(); }
	public String getWebID(){ return webId; }
	public String getTitleNum(){ return titleNum; }
	public String getTitle(){ return title; }
	public String getAuthor(){ return author; }
	public Date getDate(){ return date; }
	public String getStory(){ return story; }
	public ArrayList<Comment> getCommentList(){ return commentList; }
	
	public ArrayList<String> getSentenceFromStory(){ return sentenceFromStory; }
	public Morpheme getMorphemeOfTitle(){ return morphemeOfTitle; }
	public ArrayList<Morpheme> getMorphemeOfSentence(){ return morphemeOfSentence; }
	public double getSentimentScore(){ return sentimentScore; }
}
