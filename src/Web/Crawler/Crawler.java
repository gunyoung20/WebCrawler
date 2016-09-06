package Web.Crawler;

import java.util.ArrayList;

import Data.Document;
import Web.Phaser;
import Web.Scraper.Scraper;

public abstract class Crawler {
	public Crawler(){ this("", ""); }
	public Crawler(String u){ this(u, ""); }
	public Crawler(String u, String t){
		scraper = new Scraper(u, t);
		phaser = new Phaser();
	}
	
	abstract ArrayList<Document> phaseWebSite(String target, int mode);
	
	protected Scraper scraper;
	protected Phaser phaser;
}
