package Data;

import java.util.ArrayList;

public class Morpheme {
private String sentence;
private ArrayList<String> original;
private ArrayList<String> essential;
private ArrayList<String> pos;
private double sentimentScore;

Morpheme(){ sentence = ""; original = null; essential = null; pos = null; sentimentScore=0.0; }
Morpheme(String sentence, ArrayList<String> origin, ArrayList<String> essen, ArrayList<String> pos, double score){ this.sentence = sentence; original = origin; essential=essen; this.pos=pos; sentimentScore=score; }

public void setSentence(String sentence){ this.sentence = sentence; }
public void setOriginal(ArrayList<String> origin){ original = origin; }
public void setEssential(ArrayList<String> essen){ essential = essen; }
public void setPos(ArrayList<String> pos){ this.pos = pos; }
public void setSentimentScore(Double score){ sentimentScore = score; }

public String getSentence(){ return sentence; }
public ArrayList<String> getOriginal(){ return original; }
public ArrayList<String> getEssential(){ return essential; }
public ArrayList<String> getPos(){ return pos; }
public double getSentimentScore(){ return sentimentScore; }
}
