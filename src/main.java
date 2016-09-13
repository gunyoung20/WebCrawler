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
//		DocumentDAO ddao = new DocumentDAO();
		// 일간 베스트
//		CrawlerForIlbe IlbeWeb = new CrawlerForIlbe();
////		mode-0 : web phasing with collecting web sources from web site on online.
////		mode-1 : only phasing web sources without collecting web sources from web site on online.
////		mode-2 : only phasing web sources without collecting web sources from offline such as file, DB.
////		mode-3 : only collecting web sources from online.
//		ArrayList<Document> documentsFromIlbe = IlbeWeb.phaseWebSite(targetList.get(Megal), 0);
//		FileHandler dfhForIlbe = new FileHandler(dir + "/" + targetList.get(Ilbe), targetList.get(Megal), ".doc");
//		dfhForIlbe.saveDocumentList(documentsFromIlbe);
//		ddao.updateAll(documentsFromIlbe);

//		 오늘의 유머
//		CrawlerForOu OuWeb = new CrawlerForOu();
////		mode-0 : web phasing with collecting web sources from web site on online.
////		mode-1 : only phasing web sources without collecting web sources from web site on online.
////		mode-2 : only phasing web sources without collecting web sources from offline such as file, DB.
////		mode-3 : only collecting web sources from online.
//		ArrayList<Document> documentsFromOu = OuWeb.phaseWebSite(targetList.get(Megal), 0);
//		FileHandler dfhForOu = new FileHandler(dir + "/" + targetList.get(Today), targetList.get(Ilbe), ".doc");
//		dfhForOu.saveDocumentList(documentsFromOu);
//		ddao.updateAll(documentsFromOu);
		
		// 메갈리안
//		CrawlerForMegal MegalWeb = new CrawlerForMegal();
////		mode-0 : web phasing with collecting web sources from web site on online.
////		mode-1 : only phasing web sources without collecting web sources from web site on online.
////		mode-2 : only phasing web sources without collecting web sources from offline such as file, DB.
////		mode-3 : only collecting web sources from online.
//		ArrayList<Document> documentsFromMegal = MegalWeb.phaseWebSite(targetList.get(Ilbe), 0);
//		FileHandler dfhForMegal = new FileHandler(dir + "/" + targetList.get(Megal), targetList.get(Ilbe), ".doc");
//		dfhForMegal.saveDocumentList(documentsFromMegal);
//		ddao.updateAll(documentsFromMegal);

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
			{
//				mode-0 : web phasing with collecting web sources from web site on online.
//				mode-1 : only phasing web sources without collecting web sources from web site on online.
//				mode-2 : only phasing web sources without collecting web sources from offline such as file, DB.
//				mode-3 : only collecting web sources from online.
				es.execute(new ThreadMaker(objects[i], target[i][j], 10, 0));
//				Future future = es.submit(new ThreadMaker(objects[i], target[i][j], 2));
//				try{
//					future.get();
//					System.out.println(objects[i] + " page complete to " + target[i][j]);
//				}catch(Exception e)	{ e.printStackTrace(); }
			}
		es.shutdown();
		
//		WebSourceDAO wsdao = new WebSourceDAO();
//		System.out.println(wsdao.getSource("http://www.megalian.com/free/375666").getSource());
				
//		String u = "http://www.ilbe.com/index.php?_filter=search&mid=ilbe&search_target=title&search_keyword=%EB%A9%94%EA%B0%88&document_srl=6862497249";
//		CrawlerForIlbe IlbeWeb = new CrawlerForIlbe();
//		// mode-0 : web phasing with collecting web sources from web site on online.
//		// mode-1 : only phasing web sources without collecting web sources from web site on online.
//		// mode-2 : only phasing web sources without collecting web sources from offline such as file, DB.
//		// mode-3 : only collecting web sources from online.
//		Document document = IlbeWeb.phaseSourceOfDocument(u, 2);
//		DocumentDAO ddao = new DocumentDAO();
//		if(!ddao.update(document))
//			ddao.insert(document);
		
//		DocumentDAO ddao = new DocumentDAO();
//		ddao.delete(new Document("53194", "threadEntry375400", "", "", "", ""));
		
		System.out.println("Complete End Process!!");
	}

}
