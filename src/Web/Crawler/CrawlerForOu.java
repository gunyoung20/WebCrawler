package Web.Crawler;

import java.util.ArrayList;

import Data.Comment;
import Data.Date;
import Data.Document;
import Data.WebSource;
import Storage.DAO.WebSourceDAO;
import Web.Scraper.ScraperForOu;

public class CrawlerForOu extends Crawler {
	
	public CrawlerForOu(){ super("http://www.todayhumor.co.kr/board/list.php?kind=search&keyfield=subject&keyword="); }

//	mode-0 : web phasing with collecting web sources from web site on online.
//	mode-1 : only phasing web sources without collecting web sources from web site on online.
//	mode-2 : only phasing web sources without collecting web sources from offline such as file, DB.
//	mode-3 : only collecting web sources from online.
	public ArrayList<Document> phaseWebSite(String target, int mode)
	{
		ArrayList<Document> documentList = new ArrayList<>();

		scraper = new ScraperForOu(scraper.getSearchUrl(), target);
		scraper.setPage(1);
		
		if(mode != 2)
		{
			// Phase web sites from each web addresses
			String nowPages = scraper.getPageToString();
			WebSource tempOfWeb;
//			while(!nowPages.contains("단축키"))
			while(!nowPages.equals("11"))
			{
				scraper.setPage(nowPages);
				// search next page number
				tempOfWeb = scraper.readWebPage(mode);
			
				System.out.println("URL : " + scraper.getSearchUrl());
				System.out.println("----------------- Todayhumor " + nowPages + " Pages About " + target + " Phase Start ----------------------");
				// collect sources of web pages and split sector between documents and each comments
				if(!phaseSourcesOfWebPage(tempOfWeb, documentList, mode))
					continue;
				System.out.println("----------------- Todayhumor " + nowPages + " Pages About " + target + " Phase Complete ----------------------");

				nowPages = phaser.phase(tempOfWeb.getSource().substring(tempOfWeb.getSource().indexOf("<font size=3 color=red>")), "color=#5151FD>", "</a>", true, true);
				if(nowPages.contains("다음10개"))
					nowPages = String.valueOf(scraper.getPage()+1);
			}
		}
		else
		{
			WebSourceDAO wsdao = new WebSourceDAO();
			ArrayList<String> urlList = wsdao.getUrlList(scraper.getWebName(), target, "Document");
			
			System.out.println("URL : " + scraper.getSearchUrl());
			System.out.println("----------------- Todayhumor About " + target + " Phase Start ----------------------");
			// collect sources of web pages and split sector between documents and each comments
			phaseSourcesOfWebPage(urlList, documentList, mode);
			System.out.println("----------------- Todayhumor About " + target + " Phase Complete ----------------------");			
		}
		
		return documentList;
	}
	private boolean phaseSourcesOfWebPage(WebSource sourceOfPages, ArrayList<Document> documentList, int mode)
	{
		ArrayList<String> token = phaser.split(sourceOfPages.getSource(), "<tr class=\"view list_tr_");
		ArrayList<String> documentUrlListOfPage = phaseDocumentUrlList(token, "<a href=", "target=", true);
		
		return phaseSourcesOfWebPage(documentUrlListOfPage, documentList, mode);
	}
	private boolean phaseSourcesOfWebPage(ArrayList<String> documentUrlListOfPage, ArrayList<Document> documentList, int mode)
	{
		// Bring web pages about each document
		WebSource sourceOfDocumentPage, sourceOfCommentsPage;
		String table, tableId, memoUrl, url;
		for (int i = 0; i < documentUrlListOfPage.size(); i++) {
			// split part of document
			url = documentUrlListOfPage.get(i);
			sourceOfDocumentPage = scraper.readWebSite(url, "Document", mode);
			if(sourceOfDocumentPage.getSource().contains(scraper.DeletedDocumentCode))
			{
				System.err.println("Deleted Document!! - " + url);
				documentUrlListOfPage.remove(i--);
				continue;
			}
						
			// Extract Comment Page Size
			if(mode != 2){
				System.out.println("Todayhumor Page Url : " + url);
				System.out.println("----------------- Todayhumor Comments About " + scraper.getTarget() + " Collect Start ----------------------");
			}
			// Phase All Comments
			table = url.substring(url.indexOf("table=")+"table=".length(), url.indexOf("&no="));
			tableId = url.substring(url.indexOf("&no=")+"&no=".length(), url.indexOf("&s_no="));
			memoUrl = "http://www.todayhumor.co.kr/board/ajax_memo_list.php?parent_table=" + table + "&parent_id="+ tableId +"&last_memo_no=0";
			sourceOfCommentsPage = scraper.readWebSite(memoUrl, "Comment", mode);

			if(mode != 3)
			{
				System.out.println("----------------- Todayhumor Pages About " + url + " Phase Start ----------------------");
				// phasing web page
				Document document = phaseDocument(url, sourceOfDocumentPage, sourceOfCommentsPage);
				documentList.add(document);
				
				System.out.println("----------------- Todayhumor Pages About " + url + " Phase Complete ----------------------");
			}
			if(mode != 2)
				System.out.println("----------------- Todayhumor Comments About " + scraper.getTarget() + " Collect Complete ----------------------");
		}
		if(mode != 2)
			System.out.println("----------------- Todayhumor Documents About " + scraper.getTarget() + " Collect Complete ----------------------");
		return true;
	}
	public Document phaseSourceOfDocument(String url, int mode)
	{
		Document doc = null;

		// split part of document
		WebSource sourceOfDocumentPage = scraper.readWebSite(url, "Document", mode);

		// Phase All Comments
		String table, tableId, memoUrl;
		table = url.substring(url.indexOf("table=")+"table=".length(), url.indexOf("&no="));
		tableId = url.substring(url.indexOf("&no=")+"&no=".length(), url.indexOf("&s_no="));
		memoUrl = "http://www.todayhumor.co.kr/board/ajax_memo_list.php?parent_table=" + table + "&parent_id="+ tableId +"&last_memo_no=0";
		WebSource sourceOfCommentsPage = scraper.readWebSite(memoUrl, "Comment", mode);
		
		if(mode != 3)
			// phasing web page
			doc = phaseDocument(url, sourceOfDocumentPage, sourceOfCommentsPage);
	
		return doc;
	}
	private Document phaseDocument(String url, WebSource sourceOfDocument, WebSource sourceOfComments){

		// extract document
		Document document = phaseBasicDocument(url, sourceOfDocument.getSource());

		// phasing comments
		ArrayList<Comment> commentList = new ArrayList<Comment>();
		ArrayList<String> sourceList = phaser.phaseSourcesOfComments(sourceOfComments.getSource(), "{", "}", "[");
		for (int i = 0; i < sourceList.size(); i++) {
			Comment comment = phaseComment(sourceList.get(i));
			comment.setID(document.getTitleNum() + "-" + comment.getID());
			commentList.add(comment);
		}
		// insert comments
		document.setCommentList(commentList);
		
		return document;
	}
	private Document phaseBasicDocument(String url, String sourceOfDocument)
	{
		// extract document
		String ID, title, author, date, time, story, sourceOfDate;

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
		time = phaser.phase(sourceOfDate, date+" ", "</div>", true, true);
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
		title = phaser.phase(sourceOfDocument, "<!--EAP_SUBJECT-->", "<!--/EAP_SUBJECT-->", false, true);
		
		// document story phasing
		story = phaser.phase(sourceOfDocument, "<div class=\"viewContent\">", "<!--viewContent-->", false, true);
		
		return new Document(url, "todayhumor-"+ID, title, author, d, story);
	}
	private Comment phaseComment(String sourceOfComment)
	{
		sourceOfComment = sourceOfComment.replace("\"", "");
		// phasing comments
		String comID, comAuthor, comStory, associated, comDate, comTime;

		// Comment ID
		comID = phaser.phase(sourceOfComment, "no:", ",", true, true);

		// Comment Author
		comAuthor = phaser.phase(sourceOfComment, "name:", ",", true, true);

		// Date
		comDate = phaser.phase(sourceOfComment, "date:", " ", true, true);

		// Time
		comTime = phaser.phase(sourceOfComment, " ", ",", true, true);

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


		return new Comment(comID, comAuthor, cd, comStory, associated);
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
