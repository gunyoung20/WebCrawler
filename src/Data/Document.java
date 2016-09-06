package Data;

import java.util.ArrayList;
import Data.Date;

public class Document {

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
	
	public Document(){ author=""; title = ""; story = ""; commentList = null; sentenceFromStory = null; morphemeOfTitle = null; morphemeOfSentence = null; sentimentScore = 0.0; }
	public Document(String a, String t, String s){ author=a; title = t; story = s; commentList = null; sentenceFromStory = null; morphemeOfTitle = null; morphemeOfSentence = null; sentimentScore = 0.0; }
	public Document(String a, String t, String s, ArrayList<Comment> comment){ author=a; title = t; story = s; commentList = comment; sentenceFromStory = null; morphemeOfTitle = null; morphemeOfSentence = null; sentimentScore = 0.0; }
	public Document(String a, String t, String s, ArrayList<Comment> comment, ArrayList<String> sSentence){ author=a; title = t; story = s; commentList = comment; sentenceFromStory = sSentence; morphemeOfTitle = null; morphemeOfSentence = null; sentimentScore = 0.0; }
	public Document(String a, String t, String s, ArrayList<Comment> comment, ArrayList<String> sSentence, Morpheme tMorpheme, ArrayList<Morpheme> sMorpheme)
	{ author=a; title = t; story = s; commentList = comment; sentenceFromStory = sSentence; morphemeOfTitle = tMorpheme; morphemeOfSentence = sMorpheme; sentimentScore = 0.0; }
	public Document(String a, String t, String s, ArrayList<Comment> comment, ArrayList<String> sSentence, Morpheme tMorpheme, ArrayList<Morpheme> sMorpheme, double score)
	{ author=a; title = t; story = s; commentList = comment; sentenceFromStory = sSentence; morphemeOfTitle = tMorpheme; morphemeOfSentence = sMorpheme; sentimentScore = score; }

	public void setAuthor(String a){ author = a; }
	public void setTitle(String t){ title = t; }
	public void setStory(String s){ story = s; }
	public void setDate(Date d){ date = d; }
	public void setTitleNum(String tn){ titleNum = tn; }
	
	public void setCommentList(ArrayList<Comment> cList){ commentList = cList; }
	public void addComment(Comment comment){ commentList.add(comment); }
	
	public void setSentenceFromStory(ArrayList<String> sSentence){ sentenceFromStory = sSentence; }
	
	public void setMorphemeOfTitle(Morpheme tMorpheme){ morphemeOfTitle = tMorpheme; }
	public void setMorphemeOfSentence(ArrayList<Morpheme> sMorpheme){ morphemeOfSentence = sMorpheme; }
	
	public void setSentimentScore(double score){ sentimentScore = score; }
	
	
	public String getAuthor(){ return author; }
	public String getTitle(){ return title; }
	public String getStory(){ return story; }
	public ArrayList<Comment> getCommentList(){ return commentList; }
	public Date getDate(){ return date; }
	public String getTitleNum(){ return titleNum; }
	
	public ArrayList<String> getSentenceFromStory(){ return sentenceFromStory; }
	public Morpheme getMorphemeOfTitle(){ return morphemeOfTitle; }
	public ArrayList<Morpheme> getMorphemeOfSentence(){ return morphemeOfSentence; }
	public double getSentimentScore(){ return sentimentScore; }
}
