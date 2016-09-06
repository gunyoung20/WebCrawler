package Web.Crawler;

import java.util.ArrayList;

import Data.Comment;
import Data.Date;
import Data.Document;
import Web.Scraper.ScraperForOu;

public class CrawlerForOu extends Crawler {
	
	public CrawlerForOu(){ super("http://www.todayhumor.co.kr/board/list.php?kind=search&keyfield=subject&keyword="); }

	public ArrayList<Document> phaseWebSite(String target, int mode)
	{
		scraper = new ScraperForOu(scraper.getSearchUrl(), target);
		scraper.setPage(1);
		
		ArrayList<Document> documentList = new ArrayList<>();
		ArrayList<String> sourcesOfDocumentList = new ArrayList<>();
		ArrayList<ArrayList<String>> sourcesOfCommentsList = new ArrayList<>();
		
		// Phase web sites from each web addresses
		String nowPages = scraper.getPageToString();
		
		String tempOfWeb;
		
		while(!nowPages.contains("단축키"))
		{
			scraper.setPage(nowPages);
			// search next page number
			tempOfWeb = scraper.readWebSite(mode);
			
			System.out.println("URL : " + scraper.getSearchUrl());
			System.out.println("----------------- Todayhumor " + nowPages + " Pages About " + target + " Collect Start ----------------------");
			// collect sources of web pages and split sector between documents and each comments
			if(!collectSourcesOfWebPage(tempOfWeb, sourcesOfDocumentList, sourcesOfCommentsList, mode))
				continue;
			System.out.println("----------------- Todayhumor " + nowPages + " Pages About " + target + " Collect Complete ----------------------");

			nowPages = phaser.phase(tempOfWeb.substring(tempOfWeb.indexOf("<font size=3 color=red>")), "color=#5151FD>", "</a>", true, true);
			if(nowPages.contains("다음10개"))
				nowPages = String.valueOf(scraper.getPage()+1);
		}
		
		System.out.println("----------------- Todayhumor Pages About " + target + " Phase Start ----------------------");
		if(mode != 3)
		{
			// phasing web page
			documentList = phasePage(sourcesOfDocumentList, sourcesOfCommentsList);
		}
		System.out.println("----------------- Todayhumor Pages About " + target + " Phase Complete ----------------------");
		return documentList;
	}
	private boolean collectSourcesOfWebPage(String sourceOfPages, ArrayList<String> sourcesOfDocumentList, ArrayList<ArrayList<String>> sourcesOfCommentsList, int mode)
	{
		ArrayList<String> token = phaser.split(sourceOfPages, "<tr class=\"view list_tr_");
		ArrayList<String> documentUrlListOfPage = phaseDocumentUrlList(token, "<a href=", "target=", true);

		// Bring web pages about each document
		String sourceOfDocumentPage, sourceOfCommentsPage;
		String table, tableId, memoUrl, urlOfPage;
		for (int i = 0; i < documentUrlListOfPage.size(); i++) {
			// split part of document
			urlOfPage = documentUrlListOfPage.get(i);
			sourceOfDocumentPage = scraper.readWebSite(urlOfPage, mode);
			if(sourceOfDocumentPage.contains(scraper.DeletedDocumentCode))
			{
				System.err.println("Deleted Document!! - " + urlOfPage);
				documentUrlListOfPage.remove(i--);
				continue;
			}
						
			// Extract Comment Page Size
			if(mode != 2){
				System.out.println("Todayhumor Page Url : " + urlOfPage);
				System.out.println("----------------- Todayhumor Comments About " + scraper.getTarget() + " Collect Start ----------------------");
			}
			// Phase All Comments
			ArrayList<String> sourceOfComments = new ArrayList<String>();
			table = urlOfPage.substring(urlOfPage.indexOf("table=")+"table=".length(), urlOfPage.indexOf("&no="));
			tableId = urlOfPage.substring(urlOfPage.indexOf("&no=")+"&no=".length(), urlOfPage.indexOf("&s_no="));
			memoUrl = "http://www.todayhumor.co.kr/board/ajax_memo_list.php?parent_table=" + table + "&parent_id="+ tableId +"&last_memo_no=0";
			sourceOfCommentsPage = scraper.readWebSite(memoUrl, mode);

			// split parts of comments
			ArrayList<String> tempOfSources = phaser.phaseSourcesOfComments(sourceOfCommentsPage, "{", "}", "[");
			sourceOfComments.addAll(tempOfSources);

			if(mode != 2)
				System.out.println("----------------- Todayhumor Comments About " + scraper.getTarget() + " Collect Complete ----------------------");
			
			sourcesOfCommentsList.add(sourceOfComments);
			sourcesOfDocumentList.add(sourceOfDocumentPage);
		}
		if(mode != 2)
			System.out.println("----------------- Todayhumor Documents About " + scraper.getTarget() + " Collect Complete ----------------------");
		return true;
	}
	private ArrayList<Document> phasePage(ArrayList<String> sourcesOfDocumentList, ArrayList<ArrayList<String>> sourcesOfCommentsList){

		ArrayList<Document> documentList = new ArrayList<>();
		// extract document
		String ID, title, author, date, time, story, sourceOfDate, sourceOfDocument;
		for (int i = 0; i < sourcesOfDocumentList.size(); i++) {
			sourceOfDocument = sourcesOfDocumentList.get(i);
			Document document = new Document();

			// phasing document
			sourceOfDocument = sourceOfDocument.substring(
					sourceOfDocument.indexOf("writerInfoContainer") == -1 ? 0 : sourceOfDocument.indexOf("writerInfoContainer")
							, sourceOfDocument.indexOf("add_source_container"));

			// ID phasing
			ID = phaser.phase(sourceOfDocument.substring(sourceOfDocument.indexOf("writerInfoContents") == -1 ? 0 : sourceOfDocument.indexOf("writerInfoContents")+"writerInfoContents".length()),
					"게시물ID : ", "</div>", true, true);
			// author phasing
			author = phaser.phase(sourceOfDocument.substring(
					sourceOfDocument.indexOf("작성자 : ") == -1 ? 0 : sourceOfDocument.indexOf("작성자 : ")),
					"<b", "</b>", true, true);
			// date phasing
			sourceOfDate = sourceOfDocument.substring(
					sourceOfDocument.indexOf("등록시간 : ") == -1 ? 0 : sourceOfDocument.indexOf("등록시간 : "));
			date = phaser.phase(sourceOfDate, "등록시간 : ", " ", true, true);
			time = phaser.phase(sourceOfDate, " ", "</div>", true, true);
			// Date
			Date d = new Date();
			String[] tempD = date.split("/");
			d.setYear(Integer.parseInt(tempD[0]));
			d.setMonth(Integer.parseInt(tempD[1]));
			d.setDate(Integer.parseInt(tempD[2]));
			// Time
			String[] tempT = time.split(":");
			d.setHours(Integer.parseInt(tempT[0]));
			d.setMinutes(Integer.parseInt(tempT[1]));
			d.setSeconds(Integer.parseInt(tempT[2]));

			// title phasing
			title = phaser.phase(sourceOfDocument, "<!--EAP_SUBJECT-->", " <!--/EAP_SUBJECT-->", false, true);
			
			// document story phasing
			story = phaser.phase(sourceOfDocument, "<div class=\"viewContent\">", "<!--viewContent-->", true, true);
			
			story = phaser.storyFilter(story);
			
			// phasing comments
			String comID, comAuthor, comStory, associated, comDate, comTime;
			ArrayList<Comment> commentList = new ArrayList<Comment>();
			ArrayList<String> sourceList = sourcesOfCommentsList.get(i);
			for (int j = 0; j < sourceList.size(); j++) {
				String sourceOfComment = sourceList.get(j).replace("\"", "");

				// Comment ID
				comID = phaser.phase(sourceOfComment, "no:", ",", true, true);

				// Comment Author
				comAuthor = phaser.phase(sourceOfComment, "name:", ",", true, true);

				// Date
				comDate = phaser.phase(sourceOfComment, "date:", "\n", true, true);

				// Time
				comTime = phaser.phase(sourceOfComment, "\n", ",", true, true);

				// Date
				Date cd = new Date();
				String[] tempCD = comDate.split("-");
				cd.setYear(Integer.parseInt(tempCD[0]));
				cd.setMonth(Integer.parseInt(tempCD[1]));
				cd.setDate(Integer.parseInt(tempCD[2]));
				// Time
				String[] tempCT = comTime.split(":");
				cd.setHours(Integer.parseInt(tempCT[0]));
				cd.setMinutes(Integer.parseInt(tempCT[1]));
				cd.setSeconds(Integer.parseInt(tempCT[2]));

				// Comment Story
				comStory = phaser.phase(sourceOfComment, "memo:", ",", false, true);

				// Comment Associated Index(new)
				associated = phaser.phase(sourceOfComment, "parent_memo_no:", ",", false, true);

				Comment comment = new Comment(comID, comAuthor, cd, comStory, associated);
				commentList.add(comment);
			}
			// insert document data
			document.setTitleNum(ID);
			document.setTitle(title);
			document.setAuthor(author);
			document.setDate(d);
			document.setStory(story);
			document.setCommentList(commentList);

			documentList.add(document);
		}
		
		return documentList;
	}

	private ArrayList<String> phaseDocumentUrlList(ArrayList<String> originToken, String startCon, String endCon, boolean filter) {
		ArrayList<String> phasedTokenList = new ArrayList<String>();
		String token, temp;
		for (int i = 1; i < originToken.size(); i++) {
			token = originToken.get(i);
			if (token.contains(endCon)) {

				if (token.contains(startCon)) {
					temp = token.substring(token.indexOf(startCon) + startCon.length());
					temp = temp.substring(0, temp.indexOf(endCon));
				} else
					temp = token.substring(0, token.indexOf(endCon));

				if (filter)
					temp = phaser.removeTag(temp);
				
				temp = scraper.getUrl() + temp.substring(temp.contains("//") ? 
						temp.substring(temp.indexOf("//")+2).indexOf("/")
						+ temp.substring(0, temp.indexOf("//")+2).length() : temp.indexOf("/"));
				phasedTokenList.add(temp);
			}
		}

		return phasedTokenList;
	}
}
