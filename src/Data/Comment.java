package Data;

import java.util.ArrayList;

public class Comment {

	private String ID;
	private String author;
	private Date date;
	private String asociatedComment;
	private String sentence;
	
	private ArrayList<Morpheme> morphemeOfSentence;
	private double sentimentScore;
	
	public Comment(){ this("", "", null, "", "", null, 0.0); }
	public Comment(String ID, String au, Date da, String sen){ this(ID, au, da, sen, "", null, 0.0); }
	public Comment(String ID, String au, Date da, String sen, String aso){ this(ID, au, da, sen, aso, null, 0.0); }
	public Comment(String ID, String au, String da, String sen, String aso){ this(ID, au, new Date(da), sen, aso, null, 0.0); }
	public Comment(String ID, String au, Date da, String sen, String aso, double score){ this(ID, au, da, sen, aso, null, score); }
	public Comment(String ID, String au, Date da, String sen, String aso, ArrayList<Morpheme> morephemeL){ this(ID, au, da, sen, aso, morephemeL, 0.0); }
	public Comment(String ID, String au, Date da, String sen, String aso, ArrayList<Morpheme> morephemeL, double score){ this.ID=ID; morphemeOfSentence = morephemeL; author=au; date=da; asociatedComment=aso; sentence = sen; sentimentScore = score;}

	public String getID() { return ID; }	
	public String getAuthor(){ return author; }
	public Date getDate(){ return date; }
	public String getAsociatedComment(){ return asociatedComment; }
	public String getSentence(){ return sentence; }
	public ArrayList<Morpheme> getMorpheme(){ return morphemeOfSentence; }
	public double getSentimentScore(){ return sentimentScore; }

	public void setID(String iD) { ID = iD; }
	public void setAuthor(String a){ author = a; }
	public void setDate(Date d){ date = d; }
	public void setAsociatedComment(String a){ asociatedComment = a; }
	public void setSentence(String sen){ sentence = sen; }
	public void setMorpheme(ArrayList<Morpheme> morphemeL){ morphemeOfSentence = morphemeL; }
	public void setSentimentScore(double score){ sentimentScore = score; }
}
