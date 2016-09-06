package Web.Crawler;

import java.util.ArrayList;

import Data.Comment;
import Data.Date;
import Data.Document;
import Web.Scraper.ScraperForMegal;

public class CrawlerForMegal extends Crawler{

	public CrawlerForMegal(){ super("http://www.megalian.com/search/page/1?search=%EC%9D%BC%EB%B2%A0&sf=all"); }

	public ArrayList<Document> phaseWebSite(String target, int mode)
	{
		scraper = new ScraperForMegal(scraper.getSearchUrl(), target);
		scraper.setPage(1);
		
		ArrayList<Document> documentList = new ArrayList<>();
		ArrayList<String> sourcesOfDocumentList = new ArrayList<>();
		ArrayList<ArrayList<String>> sourcesOfCommentsList = new ArrayList<>();
		
		// Phase web sites from each web addresses
		String nowPages = scraper.getPageToString();
		
		String tempOfWeb;
		while(!nowPages.equals(""))
		{
			scraper.setPage(nowPages);
			// search next page number
			tempOfWeb = scraper.readWebSite(mode);
			
			System.out.println("URL : " + scraper.getSearchUrl());
			System.out.println("----------------- Megalian " + nowPages + " Pages About " + target + " Collect Start ----------------------");
			// collect sources of web pages and split sector between documents and each comments
			if(!collectSourcesOfWebPage(tempOfWeb, sourcesOfDocumentList, sourcesOfCommentsList, mode))
				continue;
			System.out.println("----------------- Megalian " + nowPages + " Pages About " + target + " Collect Complete ----------------------");

			nowPages = phaser.phase(tempOfWeb.contains("◀ 이전")?tempOfWeb.substring(tempOfWeb.indexOf("◀ 이전")):tempOfWeb, "/search/page/", "?", true, true);
		}
		
		System.out.println("----------------- Megalian Pages About " + target + " Phase Start ----------------------");
		if(mode != 3)
		{
			// phasing web page
			documentList = phasePage(sourcesOfDocumentList, sourcesOfCommentsList);
		}
		System.out.println("----------------- Megalian Pages About " + target + " Phase Complete ----------------------");
		return documentList;
	}
	private boolean collectSourcesOfWebPage(String sourceOfPages, ArrayList<String> sourcesOfDocumentList, ArrayList<ArrayList<String>> sourcesOfCommentsList, int mode)
	{
		ArrayList<String> token = phaser.split(sourceOfPages, "<div class=\"id\">");
		ArrayList<String> documentUrlListOfPage = phaseDocumentUrlList(token, "<a href=\"", "\"", true);

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
				System.out.println("Megalian Page Url : " + urlOfPage);
				System.out.println("----------------- Megalian Comments About " + scraper.getTarget() + " Collect Start ----------------------");
			}
			// Phase All Comments
			table = urlOfPage.substring(urlOfPage.indexOf(scraper.getUrl())+scraper.getUrl().length()+1);
			tableId = table.substring(table.indexOf("/")+1);
			table = table.substring(0, table.indexOf("/"));
			memoUrl = "http://www.megalian.com/json_replies?thread_id="+tableId+"&forumid="+table+"&start=0";
			sourceOfCommentsPage = scraper.readWebSite(memoUrl, mode);

			// split parts of comments
			ArrayList<String> tempOfSources = phaser.phase(sourceOfCommentsPage, "<article class=", "</article>", "<article class=", true, false);

			if(mode != 2)
				System.out.println("----------------- Megalian Comments About " + scraper.getTarget() + " Collect Complete ----------------------");
			
			sourcesOfCommentsList.add(tempOfSources);
			sourcesOfDocumentList.add(sourceOfDocumentPage);
		}
		if(mode != 2)
			System.out.println("----------------- Megalian Documents About " + scraper.getTarget() + " Collect Complete ----------------------");
		return true;
	}
	private ArrayList<Document> phasePage(ArrayList<String> sourcesOfDocumentList, ArrayList<ArrayList<String>> sourcesOfCommentsList){

		ArrayList<Document> documentList = new ArrayList<>();
		// extract document
		String ID, title, author, date, time, story, sourceOfDate, sourceOfDocument;
		for (int i = 0; i < sourcesOfDocumentList.size(); i++) {
			sourceOfDocument = sourcesOfDocumentList.get(i);
			Document document = new Document();

			// title phasing
			title = phaser.phase(sourceOfDocument, "제목</div><h1>", "</h1>", false, true);
			
			// ID phasing
			ID = phaser.phase(sourceOfDocument.substring(sourceOfDocument.indexOf("<article class=") == -1 ? 0 : sourceOfDocument.indexOf("<article class=")+"<article class=".length()),
					"id=\"", "\">", true, true);
			// author phasing
			author = phaser.phase(sourceOfDocument.substring(
					sourceOfDocument.indexOf("글쓴이") == -1 ? 0 : sourceOfDocument.indexOf("글쓴이")),
					"\">", "</a>", true, true);
			// date phasing
			sourceOfDate = sourceOfDocument.substring(
					sourceOfDocument.indexOf("<div class=\"date\">") == -1 ? 0 : sourceOfDocument.indexOf("<div class=\"date\">"));
			date = phaser.phase(sourceOfDate, "<div class=\"date\">", " ", true, true);
			time = phaser.phase(sourceOfDate, " ", "</div>", true, true);
			// Date
			Date d = new Date();
			String[] tempD = date.split("-");
			d.setYear(Integer.parseInt(tempD[0]));
			d.setMonth(Integer.parseInt(tempD[1]));
			d.setDate(Integer.parseInt(tempD[2]));
			// Time
			String[] tempT = time.split(":");
			d.setHours(Integer.parseInt(tempT[0]));
			d.setMinutes(Integer.parseInt(tempT[1]));
			d.setSeconds(Integer.parseInt(tempT[2]));

			// document story phasing
			story = phaser.phase(sourceOfDocument, "<div class=\"txt\">", "</div>", true, true);
			
			story = phaser.storyFilter(story);
			
			// phasing comments
			String comID, comAuthor, comStory, associated, comDate, comTime;
			ArrayList<Comment> commentList = new ArrayList<Comment>();
			ArrayList<String> sourceList = sourcesOfCommentsList.get(i);
			for (int j = 0; j < sourceList.size(); j++) {
				String sourceOfComment = sourceList.get(j).replace("\"", "");

				// Comment ID
				comID = phaser.phase(sourceOfComment, "id=", ">", true, true);

				// Comment Author
				comAuthor = phaser.phase(sourceOfComment, "s nickname>", "</a>", true, true);

				// date phasing
				sourceOfDate = sourceOfDocument.substring(
						sourceOfDocument.indexOf("class=date>") == -1 ? 0 : sourceOfDocument.indexOf("class=date>"));
				// Date
				comDate = phaser.phase(sourceOfComment, "class=date>", " ", true, true);

				// Time
				comTime = phaser.phase(sourceOfComment, " ", "</span>", true, true);

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
				comStory = phaser.phase(sourceOfComment, "class=txt>", "<!--", false, true);

				// Comment Associated Index(new)
				associated = "0";

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
		for (int i = 2; i < originToken.size(); i++) {
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
