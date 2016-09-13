import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import Data.Document;
import Storage.FileHandler;
import Storage.DAO.DocumentDAO;
import Web.Crawler.CrawlerForIlbe;
import Web.Crawler.CrawlerForMegal;
import Web.Crawler.CrawlerForOu;

public class ThreadMaker implements Runnable{
	final String Ilbe = "ilbe", Megal = "megalian", Today = "todayhumor";
	String object, target, dir;
	int mode = 3, maxPage = 0;
	
	public ThreadMaker(String object, String target, int mode){
		this(object, target, 0, mode);
	}
	public ThreadMaker(String object, String target, int maxPage, int mode){
		this.object = object;
		this.target = target;
		this.mode = mode;
		this.maxPage = maxPage;
		this.dir = "D:/정우영/JAVA/WebCrawler/WebSource/Document";
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		DocumentDAO ddao = new DocumentDAO();
		if(object.equals(Ilbe))
		{
			if(target.equals(Megal))
			{
				CrawlerForIlbe IlbeWeb = new CrawlerForIlbe();
				ArrayList<Document> documentsFromIlbe = IlbeWeb.phaseWebSite(Megal, maxPage, mode);
				FileHandler dfhForIlbe = new FileHandler(dir + "/" + Ilbe, Megal, ".doc");
				dfhForIlbe.saveDocumentList(documentsFromIlbe);
				ddao.updateAll(documentsFromIlbe);
			}
			else if(target.equals(Today))
			{
				CrawlerForIlbe IlbeWeb2 = new CrawlerForIlbe();
				ArrayList<Document> documentsFromIlbe = IlbeWeb2.phaseWebSite(Today, maxPage, mode);
				FileHandler dfhForIlbe = new FileHandler(dir + "/" + Ilbe, Today, ".doc");
				dfhForIlbe.saveDocumentList(documentsFromIlbe);
				ddao.updateAll(documentsFromIlbe);
			}
		}
		else if(object.equals(Megal))
		{
			if(target.equals(Ilbe))
			{
				CrawlerForMegal MegalWeb = new CrawlerForMegal();
				ArrayList<Document> documentsFromMegal = MegalWeb.phaseWebSite(Ilbe, maxPage, mode);
				FileHandler dfhForMegal = new FileHandler(dir + "/" + Megal, Ilbe, ".doc");
				dfhForMegal.saveDocumentList(documentsFromMegal);
				ddao.updateAll(documentsFromMegal);
			}
			else if(target.equals(Today))
			{
				CrawlerForMegal MegalWeb2 = new CrawlerForMegal();
				ArrayList<Document> documentsFromMegal = MegalWeb2.phaseWebSite(Today, maxPage, mode);
				FileHandler dfhForMegal = new FileHandler(dir + "/" + Megal, Today, ".doc");
				dfhForMegal.saveDocumentList(documentsFromMegal);
				ddao.updateAll(documentsFromMegal);
			}
		}
		else if(object.equals(Today))
		{
			if(target.equals(Ilbe))
			{
				CrawlerForOu OuWeb = new CrawlerForOu();
				ArrayList<Document> documentsFromOu = OuWeb.phaseWebSite(Ilbe, maxPage, mode);
				FileHandler dfhForOu = new FileHandler(dir + "/" + Today, Ilbe, ".doc");
				dfhForOu.saveDocumentList(documentsFromOu);
				ddao.updateAll(documentsFromOu);
			}
			else if(target.equals(Megal))
			{
				CrawlerForOu OuWeb2 = new CrawlerForOu();
				ArrayList<Document> documentsFromOu = OuWeb2.phaseWebSite(Megal, maxPage, mode);
				FileHandler dfhForOu = new FileHandler(dir + "/" + Today, Megal, ".doc");
				dfhForOu.saveDocumentList(documentsFromOu);
				ddao.updateAll(documentsFromOu);
			}
		}
		System.out.println((new SimpleDateFormat("YYYY-MM-dd HHmmss").format(Calendar.getInstance().getTime())) + " Complete Thread Process!!");
	}
}
