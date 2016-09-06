import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import Data.Document;
import Storage.FileHandler;
import Web.Crawler.CrawlerForIlbe;
import Web.Crawler.CrawlerForMegal;
import Web.Crawler.CrawlerForOu;

public class main {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
//		String Ilbe = "%EC%9D%BC%EB%B2%A0", Megal = "%EB%A9%94%EA%B0%88", Today = "%EC%98%A4%EC%9C%A0";
//		HashMap<String, String> targetList = new HashMap<String, String>();
//		targetList.put(Ilbe, "Ilbe");
//		targetList.put(Megal, "Megal");
//		targetList.put(Today, "Ou");
//		// 일간 베스트
//		CrawlerForIlbe IlbeWeb = new CrawlerForIlbe();
////		mode-0 : web phasing with collecting web sources from web site on online.
////		mode-1 : only phasing web sources without collecting web sources from web site on online.
////		mode-2 : only phasing web sources without collecting web sources from offline such as file, DB.
////		mode-3 : only collecting web sources from online.
//		ArrayList<Document> documentsFromIlbe = IlbeWeb.phaseWebSite(targetList.get(Megal), 3);
//		FileHandler dfhForIlbe = new FileHandler("D:/정우영/JAVA/WebPhasing/FileData/Ilbe", targetList.get(Megal), ".doc");
//		dfhForIlbe.saveDocumentList(documentsFromIlbe);
//
//		// 오늘의 유머
//		CrawlerForOu OuWeb = new CrawlerForOu();
////		mode-0 : web phasing with collecting web sources from web site on online.
////		mode-1 : only phasing web sources without collecting web sources from web site on online.
////		mode-2 : only phasing web sources without collecting web sources from offline such as file, DB.
////		mode-3 : only collecting web sources from online.
//		ArrayList<Document> documentsFromOu = OuWeb.phaseWebSite(targetList.get(Megal), 3);
//		FileHandler dfhForOu = new FileHandler("D:/정우영/JAVA/WebPhasing/FileData/Ou", targetList.get(Ilbe), ".doc");
//		dfhForOu.saveDocumentList(documentsFromOu);
//		
//		// 메갈리안
//		CrawlerForMegal MegalWeb = new CrawlerForMegal();
////		mode-0 : web phasing with collecting web sources from web site on online.
////		mode-1 : only phasing web sources without collecting web sources from web site on online.
////		mode-2 : only phasing web sources without collecting web sources from offline such as file, DB.
////		mode-3 : only collecting web sources from online.
//		ArrayList<Document> documentsFromMegal = MegalWeb.phaseWebSite(targetList.get(Ilbe), 3);
//		FileHandler dfhForMegal = new FileHandler("D:/정우영/JAVA/WebPhasing/FileData/Megal", targetList.get(Ilbe), ".doc");
//		dfhForMegal.saveDocumentList(documentsFromMegal);

//		DocumentFileHandler dfh = new DocumentFileHandler("D:/정우영/JAVA/WebPhasing/FileData/Ilbe", "Megal", ".doc");
//		ArrayList<Document> dlist = dfh.loadDocumentList();
//		
//		for(int i = 0; i < dlist.size(); i++)
//		{
//			Document dc = dlist.get(i);
//			System.out.println("Title Number : " + dc.getTitleNum() + ", Title : " + dc.getTitle()
//			+ " , Date : " + dc.getDate().toString() + ", Author : " + dc.getAuthor() + ", Story : " + dc.getStory());
//			System.out.println("--------- Comment Start ----------");
//			ArrayList<Comment> clist = dc.getCommentList();
//			for(int j = 0; j < clist.size(); j++)
//			{
//				Comment c = clist.get(j);
//				System.out.println((j+1) + " ID : " + c.getID() + ", Associated ID : " + c.getAsociatedComment()
//				+ ", Author : "+ c.getAuthor() + ", Date : " + c.getDate().toString() + ", Story : " + c.getSentence());
//			}
//			System.out.println("--------- Comment End ------------");
//		} 

		String Ilbe = "Ilbe", Megal = "Megal", Today = "Ou";
		String[] objects = {Ilbe, Megal, Today};
		String[][] targets = {{Megal, Today}, {Ilbe, Today}, {Ilbe, Megal}};
		
		ExecutorService es = Executors.newFixedThreadPool(
				Runtime.getRuntime().availableProcessors());
		for(int i = 0; i < objects.length; i++)
			for(int j = 0; j < targets[i].length; j++)
				es.execute(new ThreadMaker(objects[i], targets[i][j]));
		es.shutdown();
		
		System.out.println("Complete End Process!!");
	}

}
