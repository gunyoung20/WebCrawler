package Web.Crawler;

import java.util.ArrayList;

import Data.Comment;
import Data.Date;
import Data.Document;
import Data.WebSource;
import Storage.DAO.WebSourceDAO;
import Web.Scraper.ScraperForMegal;

public class CrawlerForMegal extends Crawler{

	public CrawlerForMegal(){ super("http://www.megalian.com/search/page/1?search=%EC%9D%BC%EB%B2%A0&sf=all"); }

	public ArrayList<Document> phaseWebSite(String target, int mode)
	{
		ArrayList<Document> documentList = new ArrayList<>();

		scraper = new ScraperForMegal(scraper.getSearchUrl(), target);
		scraper.setPage(1);

		if(mode != 2)
		{
			// Phase web sites from each web addresses
			String nowPages = scraper.getPageToString();

			WebSource tempOfWeb;
//			while (!nowPages.equals("")) {
			while (!nowPages.equals("11")) {
				scraper.setPage(nowPages);
				// search next page number
				tempOfWeb = scraper.readWebPage(mode);

				System.out.println("URL : " + scraper.getSearchUrl());
				System.out.println("----------------- Megalian " + nowPages + " Pages About " + target + " Phase Start ----------------------");
				// collect sources of web pages and split sector between
				// documents and each comments
				if (!phaseSourcesOfWebPage(tempOfWeb, documentList, mode))
					continue;
				System.out.println("----------------- Megalian " + nowPages + " Pages About " + target + " Phase Complete ----------------------");

				nowPages = phaser.phase(tempOfWeb.getSource().contains("◀ 이전")
						? tempOfWeb.getSource().substring(tempOfWeb.getSource().indexOf("◀ 이전"))
						: tempOfWeb.getSource(), "/search/page/", "?", true, true);
			}
		}
		else
		{
			WebSourceDAO wsdao = new WebSourceDAO();
			ArrayList<String> urlList = wsdao.getUrlList(scraper.getWebName(), target, "Document");
			
			System.out.println("URL : " + scraper.getSearchUrl());
			System.out.println("----------------- Megalian About " + target + " Phase Start ----------------------");
			// collect sources of web pages and split sector between documents and each comments
			phaseSourcesOfWebPage(urlList, documentList, mode);
			System.out.println("----------------- Megalian About " + target + " Phase Complete ----------------------");			
		}
		
		return documentList;
	}
	private boolean phaseSourcesOfWebPage(WebSource sourceOfPages, ArrayList<Document> documentList, int mode)
	{
		ArrayList<String> token = phaser.split(sourceOfPages.getSource(), "<div class=\"id\">");
		ArrayList<String> documentUrlListOfPage = phaseDocumentUrlList(token, "<a href=\"", "\"", true);
		
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
				System.out.println("Megalian Page Url : " + url);
				System.out.println("----------------- Megalian Comments About " + scraper.getTarget() + " Collect Start ----------------------");
			}
			// Phase All Comments
			table = url.substring(url.indexOf(scraper.getUrl())+scraper.getUrl().length()+1);
			tableId = table.substring(table.indexOf("/")+1);
			table = table.substring(0, table.indexOf("/"));
			memoUrl = "http://www.megalian.com/json_replies?thread_id="+tableId+"&forumid="+table+"&start=0";
			sourceOfCommentsPage = scraper.readWebSite(memoUrl, "Comment", mode);

			if(mode != 2)
				System.out.println("----------------- Megalian Comments About " + scraper.getTarget() + " Collect Complete ----------------------");
						
			if(mode != 3)
			{
				System.out.println("----------------- Megalian Pages About " + url + " Phase Start ----------------------");
				// phasing web page
				Document document = phaseDocument(url, sourceOfDocumentPage, sourceOfCommentsPage);
				documentList.add(document);

				System.out.println("----------------- Megalian Pages About " + url + " Phase Complete ----------------------");
			}
		}
		if(mode != 2)
			System.out.println("----------------- Megalian Documents About " + scraper.getTarget() + " Collect Complete ----------------------");
		
		return true;
	}
	private Document phaseDocument(String url, WebSource sourceOfDocument, WebSource sourceOfComments){
		// extract document
		Document document = phaseBasicDocument(url, sourceOfDocument.getSource());

		// phasing comments
		ArrayList<Comment> commentList = new ArrayList<Comment>();
		// split parts of comments
		ArrayList<String> sourceList = phaser.phase(sourceOfComments.getSource()
										, "<article class=", "</article>", "<article class=", false, false);
		for (int i = 1; i < sourceList.size(); i++) 
		{
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
				sourceOfDocument.indexOf("class=\"date\">") == -1 ? 0 : sourceOfDocument.indexOf("class=\"date\">"));
		date = phaser.phase(sourceOfDate, "class=\"date\">", " ", true, true);
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
		story = phaser.phase(sourceOfDocument, "<div class=\"txt\">", "</div>", false, true);
		
//		story = phaser.storyFilter(story);

		return new Document(url, ID, "megalian-"+title, author, d, story);
	}
	private Comment phaseComment(String sourceOfComment)
	{
		// phasing comments
		String comID, comAuthor, comStory, associated, comDate, comTime, sourceOfDate;

		// Comment ID
		comID = phaser.phase(sourceOfComment, "id=\\\"", "\\\">", true, true);

		// Comment Author
		comAuthor = phaser.phase(sourceOfComment, "s nickname>", "</a>", true, true);

		// date phasing
		sourceOfDate = sourceOfComment.substring(sourceOfComment.indexOf("class=\\\"date\\\">") == -1 ?
				0 : sourceOfComment.indexOf("class=\\\"date\\\">") + "class=\\\"date\\\">".length(), sourceOfComment.indexOf("</span>"));
//		// Date
//		comDate = phaser.phase(sourceOfDate, "class=date>", " ", true, true);
//
//		// Time
//		comTime = phaser.phase(sourceOfDate, " ", "</span>", true, true);

		// Date
		Date cd = new Date(sourceOfDate);
//		String[] tempCD = comDate.split("-");
//		cd.setYear(Integer.parseInt(tempCD[0]));
//		cd.setMonth(Integer.parseInt(tempCD[1]));
//		cd.setDate(Integer.parseInt(tempCD[2]));
//		// Time
//		String[] tempCT = comTime.split(":");
//		cd.setHours(Integer.parseInt(tempCT[0]));
//		cd.setMinutes(Integer.parseInt(tempCT[1]));
//		cd.setSeconds(Integer.parseInt(tempCT[2]));

		// Comment Story
		comStory = phaser.phase(sourceOfComment, "class=\\\"txt\\\">", "<!--", false, true);

		// Comment Associated Index(new)
		associated = "0";

		return new Comment(comID, comAuthor, cd, comStory, associated);
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
