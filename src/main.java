import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class main {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
//		String Ilbe = "%EC%9D%BC%EB%B2%A0", Megal = "%EB%A9%94%EA%B0%88", Today = "%EC%98%A4%EC%9C%A0";
//		HashMap<String, String> targetList = new HashMap<String, String>();
//		targetList.put(Ilbe, "ilbe");
//		targetList.put(Megal, "megalian");
//		targetList.put(Today, "todayhumor");
//		String dir = "D:/정우영/JAVA/WebCrawler/WebSource/Document";
		// 일간 베스트
//		CrawlerForIlbe IlbeWeb = new CrawlerForIlbe();
////		mode-0 : web phasing with collecting web sources from web site on online.
////		mode-1 : only phasing web sources without collecting web sources from web site on online.
////		mode-2 : only phasing web sources without collecting web sources from offline such as file, DB.
////		mode-3 : only collecting web sources from online.
//		ArrayList<Document> documentsFromIlbe = IlbeWeb.phaseWebSite(targetList.get(Megal), 2);
//		FileHandler dfhForIlbe = new FileHandler(dir + "/" + targetList.get(Ilbe), targetList.get(Megal), ".doc");
//		dfhForIlbe.saveDocumentList(documentsFromIlbe);

		// 오늘의 유머
//		CrawlerForOu OuWeb = new CrawlerForOu();
////		mode-0 : web phasing with collecting web sources from web site on online.
////		mode-1 : only phasing web sources without collecting web sources from web site on online.
////		mode-2 : only phasing web sources without collecting web sources from offline such as file, DB.
////		mode-3 : only collecting web sources from online.
//		ArrayList<Document> documentsFromOu = OuWeb.phaseWebSite(targetList.get(Megal), 2);
//		FileHandler dfhForOu = new FileHandler(dir + "/" + targetList.get(Today), targetList.get(Ilbe), ".doc");
//		dfhForOu.saveDocumentList(documentsFromOu);
		
		// 메갈리안
//		CrawlerForMegal MegalWeb = new CrawlerForMegal();
////		mode-0 : web phasing with collecting web sources from web site on online.
////		mode-1 : only phasing web sources without collecting web sources from web site on online.
////		mode-2 : only phasing web sources without collecting web sources from offline such as file, DB.
////		mode-3 : only collecting web sources from online.
//		ArrayList<Document> documentsFromMegal = MegalWeb.phaseWebSite(targetList.get(Ilbe), 2);
//		FileHandler dfhForMegal = new FileHandler(dir + "/" + targetList.get(Megal), targetList.get(Ilbe), ".doc");
//		dfhForMegal.saveDocumentList(documentsFromMegal);

//		FileHandler dfh = new FileHandler("D:/정우영/JAVA/WebPhasing/FileData/Ilbe", "Megal", ".doc");
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

		String Ilbe = "ilbe", Megal = "megalian", Today = "todayhumor";
		String[] objects = {Ilbe, Megal, Today};
		String[][] target = {{Megal, Today}, {Ilbe, Today}, {Ilbe, Megal}};
		
		ExecutorService es = Executors.newFixedThreadPool(
				Runtime.getRuntime().availableProcessors());
		for(int i = 0; i < objects.length; i++)
			for(int j = 0; j < target[i].length; j++)
//				mode-0 : web phasing with collecting web sources from web site on online.
//				mode-1 : only phasing web sources without collecting web sources from web site on online.
//				mode-2 : only phasing web sources without collecting web sources from offline such as file, DB.
//				mode-3 : only collecting web sources from online.
				es.execute(new ThreadMaker(objects[i], target[i][j], 2));
		es.shutdown();
		
//		WebSourceDAO wsdao = new WebSourceDAO();
//		System.out.println(wsdao.getSource("http://www.megalian.com/free/375666").getSource());
				
		System.out.println("Complete End Process!!");
	}

}
