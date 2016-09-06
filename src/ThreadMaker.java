import java.util.ArrayList;
import java.util.HashMap;

import Data.Document;
import Storage.FileHandler;
import Web.Crawler.CrawlerForIlbe;
import Web.Crawler.CrawlerForMegal;
import Web.Crawler.CrawlerForOu;

public class ThreadMaker implements Runnable{
	final String Ilbe = "Ilbe", Megal = "Megal", Today = "Ou";
	String object, target;
	int mode = 3;
	
	public ThreadMaker(String object, String target, int mode){
		this.object = object;
		this.target = target;
		this.mode = mode;
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		if(object.equals(Ilbe))
		{
			if(target.equals(Megal))
			{
				CrawlerForIlbe IlbeWeb = new CrawlerForIlbe();
				ArrayList<Document> documentsFromIlbe = IlbeWeb.phaseWebSite(Megal, mode);
				FileHandler dfhForIlbe = new FileHandler("D:/정우영/JAVA/WebPhasing/FileData/Ilbe", Megal, ".doc");
				dfhForIlbe.saveDocumentList(documentsFromIlbe);
			}
			else if(target.equals(Today))
			{
				CrawlerForIlbe IlbeWeb2 = new CrawlerForIlbe();
				ArrayList<Document> documentsFromIlbe = IlbeWeb2.phaseWebSite(Today, mode);
				FileHandler dfhForIlbe = new FileHandler("D:/정우영/JAVA/WebPhasing/FileData/Ilbe", Today, ".doc");
				dfhForIlbe.saveDocumentList(documentsFromIlbe);
			}
		}
		else if(object.equals(Megal))
		{
			if(target.equals(Ilbe))
			{
				CrawlerForMegal MegalWeb = new CrawlerForMegal();
				ArrayList<Document> documentsFromMegal = MegalWeb.phaseWebSite(Ilbe, mode);
				FileHandler dfhForMegal = new FileHandler("D:/정우영/JAVA/WebPhasing/FileData/Megal", Ilbe, ".doc");
				dfhForMegal.saveDocumentList(documentsFromMegal);
			}
			else if(target.equals(Today))
			{
				CrawlerForMegal MegalWeb2 = new CrawlerForMegal();
				ArrayList<Document> documentsFromMegal = MegalWeb2.phaseWebSite(Today, mode);
				FileHandler dfhForMegal = new FileHandler("D:/정우영/JAVA/WebPhasing/FileData/Megal", Today, ".doc");
				dfhForMegal.saveDocumentList(documentsFromMegal);
			}
		}
		else if(object.equals(Today))
		{
			if(target.equals(Ilbe))
			{
				CrawlerForOu OuWeb = new CrawlerForOu();
				ArrayList<Document> documentsFromOu = OuWeb.phaseWebSite(Ilbe, mode);
				FileHandler dfhForOu = new FileHandler("D:/정우영/JAVA/WebPhasing/FileData/Ou", Ilbe, ".doc");
				dfhForOu.saveDocumentList(documentsFromOu);
			}
			else if(target.equals(Megal))
			{
				CrawlerForOu OuWeb2 = new CrawlerForOu();
				ArrayList<Document> documentsFromOu = OuWeb2.phaseWebSite(Megal, mode);
				FileHandler dfhForOu = new FileHandler("D:/정우영/JAVA/WebPhasing/FileData/Ou", Megal, ".doc");
				dfhForOu.saveDocumentList(documentsFromOu);
			}
		}
		System.out.println(object + " page complete to " + target);
	}
}
