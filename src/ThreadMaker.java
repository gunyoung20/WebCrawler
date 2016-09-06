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
	
	public ThreadMaker(String object, String target){
		this.object = object;
		this.target = target;
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		if(object.contains(Ilbe))
		{
			CrawlerForIlbe IlbeWeb = new CrawlerForIlbe();
			if(target.contains(Megal))
			{
				ArrayList<Document> documentsFromIlbe = IlbeWeb.phaseWebSite(Megal, mode);
				FileHandler dfhForIlbe = new FileHandler("D:/정우영/JAVA/WebPhasing/FileData/Ilbe", Megal, ".doc");
				dfhForIlbe.saveDocumentList(documentsFromIlbe);
			}
			else if(object.contains(Today))
			{
				ArrayList<Document> documentsFromIlbe = IlbeWeb.phaseWebSite(Today, mode);
				FileHandler dfhForIlbe = new FileHandler("D:/정우영/JAVA/WebPhasing/FileData/Ilbe", Today, ".doc");
				dfhForIlbe.saveDocumentList(documentsFromIlbe);
			}
		}
		else if(object.contains(Megal))
		{
			CrawlerForMegal MegalWeb = new CrawlerForMegal();
			if(target.contains(Ilbe))
			{
				ArrayList<Document> documentsFromMegal = MegalWeb.phaseWebSite(Ilbe, mode);
				FileHandler dfhForMegal = new FileHandler("D:/정우영/JAVA/WebPhasing/FileData/Megal", Ilbe, ".doc");
				dfhForMegal.saveDocumentList(documentsFromMegal);
			}
			else if(object.contains(Today))
			{
				ArrayList<Document> documentsFromMegal = MegalWeb.phaseWebSite(Today, mode);
				FileHandler dfhForMegal = new FileHandler("D:/정우영/JAVA/WebPhasing/FileData/Megal", Today, ".doc");
				dfhForMegal.saveDocumentList(documentsFromMegal);
			}
		}
		else if(object.contains(Today))
		{
			CrawlerForOu OuWeb = new CrawlerForOu();
			if(target.contains(Ilbe))
			{
				ArrayList<Document> documentsFromOu = OuWeb.phaseWebSite(Ilbe, mode);
				FileHandler dfhForOu = new FileHandler("D:/정우영/JAVA/WebPhasing/FileData/Ou", Ilbe, ".doc");
				dfhForOu.saveDocumentList(documentsFromOu);
			}
			else if(object.contains(Megal))
			{
				ArrayList<Document> documentsFromOu = OuWeb.phaseWebSite(Megal, mode);
				FileHandler dfhForOu = new FileHandler("D:/정우영/JAVA/WebPhasing/FileData/Ou", Megal, ".doc");
				dfhForOu.saveDocumentList(documentsFromOu);
			}
		}
		System.out.println(object + " page complete to " + target);
	}
}
