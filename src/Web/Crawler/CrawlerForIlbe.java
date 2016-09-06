package Web.Crawler;

import java.util.ArrayList;

import Data.Comment;
import Data.Date;
import Data.Document;
import Web.Scraper.ScraperForIlbe;

public class CrawlerForIlbe extends Crawler {
	public CrawlerForIlbe(){ 
		super("http://www.ilbe.com/?_filter=search&mid=ilbe&search_target=title&search_keyword="); }
	
	public ArrayList<Document> phaseWebSite(String target, int mode)
	{
		scraper = new ScraperForIlbe(scraper.getSearchUrl(), target);
		scraper.setPage(1);
		
		ArrayList<Document> documentList = new ArrayList<>();
		ArrayList<String> sourcesOfDocumentList = new ArrayList<>();
		ArrayList<ArrayList<String>> sourcesOfCommentsList = new ArrayList<>();
		
		// Phase web sites from each web addresses
		String nowPages = scraper.getPageToString();
		
		String tempOfWeb;
		
		while(!nowPages.contains("다음"))
		{
			scraper.setPage(nowPages);
			// search next page nowPages
			tempOfWeb = scraper.readWebSite(mode);
			
			System.out.println("URL : " + scraper.getSearchUrl());
			System.out.println("----------------- Ilbe " + nowPages + " Pages About " + target + " Collect Start ----------------------");
			// collect sources of web pages and split sector between documents and each comments
			if(!collectSourcesOfWebPage(tempOfWeb, sourcesOfDocumentList, sourcesOfCommentsList, mode))
				continue;
			System.out.println("----------------- Ilbe " + nowPages + " Pages About " + target + " Collect Complete ----------------------");

			nowPages = phaser.phase(tempOfWeb.substring(tempOfWeb.indexOf("pagination")), "</strong>", "</a>", true, true);
		}
		
		System.out.println("----------------- Ilbe Pages About " + target + " Phase Start ----------------------");
		if(mode != 3)
		{
			// phasing web page
			documentList = phasePage(sourcesOfDocumentList, sourcesOfCommentsList);
		}
		System.out.println("----------------- Ilbe Pages About " + target + " Phase Complete ----------------------");
		return documentList;
	}
	private boolean collectSourcesOfWebPage(String sourceOfPages, ArrayList<String> sourcesOfDocumentList, ArrayList<ArrayList<String>> sourcesOfCommentsList, int mode)
	{		
		ArrayList<String> token = phaser.split(sourceOfPages, "<td class=\"num\">");
		ArrayList<String> documentUrlListOfPage = phaseDocumentUrlList(token, "<a href=\"", "\"", true);

		// Bring web pages about each document

		for (int i = 0; i < documentUrlListOfPage.size(); i++) {
			// split part of document
			String sourceOfDocumentPage = scraper.readWebSite(documentUrlListOfPage.get(i), mode);
			if(sourceOfDocumentPage.contains(scraper.DeletedDocumentCode))
			{
				System.err.println("Deleted Document!! - " + documentUrlListOfPage.get(i));
				documentUrlListOfPage.remove(i--);
				continue;
			}
						
			// Extract Comment Page Size
			String sourceOfCommentPageSection = sourceOfDocumentPage.substring(sourceOfDocumentPage.indexOf("class=\"pagination") == -1 ? 0 : sourceOfDocumentPage.indexOf("class=\"pagination") + "class=\"pagination".length());
			String comPageSection = "";
			comPageSection = phaser.phase(sourceOfCommentPageSection, "</a>", "</div>", true);
			
			int comPageSize = 1;
			if (comPageSection.contains("</strong>"))
				comPageSize = Integer.valueOf(phaser.phase(comPageSection, "<strong>", "</strong>", true));

			if(mode != 2){
				System.out.println("Ilbe Page Url : " + documentUrlListOfPage.get(i));
				System.out.println("----------------- Ilbe Comments About " + scraper.getTarget() + " Collect Start ----------------------");
			}
			// Phase All Comments
			ArrayList<String> sourceOfComments = new ArrayList<String>();
			for (int j = 0; j < comPageSize; j++) {
				sourceOfDocumentPage = scraper.readWebSite(documentUrlListOfPage.get(i) + "&cpage=" + (j+1), mode);
				
				// split parts of comments
				ArrayList<String> tempOfSources = phaser.phase(sourceOfDocumentPage, "<div class=\"replyItem", "<div style=\"text-align", "<div class=\"replyItem", false);
				sourceOfComments.addAll(tempOfSources);
				if(mode != 2)
					System.out.println("Comment " + (j+1) + "/" + comPageSize + " Pages Collected");
			}
			if(mode != 2)
				System.out.println("----------------- Ilbe Comments About " + scraper.getTarget() + " Collect Complete ----------------------");
			
			sourcesOfCommentsList.add(sourceOfComments);
			sourcesOfDocumentList.add(phaser.phase(sourceOfDocumentPage.substring(sourceOfDocumentPage.indexOf("<div class=\"title\">") == -1 ? 0 : sourceOfDocumentPage.indexOf("<div class=\"title\">") + "<div class=\"title\">".length()), "<div class=\"title\">", "<div class=\"tRight\">", false));
		}
		if(mode != 2)
			System.out.println("----------------- Ilbe Documents About " + scraper.getTarget() + " Collect Complete ----------------------");
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
			sourceOfDocument = sourceOfDocument.substring(sourceOfDocument.indexOf("<div class=\"title\">") == -1 ? 0 : sourceOfDocument.indexOf("<div class=\"title\">"));
			// ID phasing
			ID = phaser.phase(sourceOfDocument.substring(sourceOfDocument.indexOf("//") == -1 ? 0 : sourceOfDocument.indexOf("//")+2),
					"/", "\"", true, true);
			// title phasing
			title = phaser.phase(sourceOfDocument, "-->", "<!--", true, true);
			// author phasing
			author = phaser.phase(sourceOfDocument.substring(
					sourceOfDocument.indexOf("class=\"userInfo\"") == -1 ? 0 : sourceOfDocument.indexOf("class=\"userInfo\"")),
					"<span", "</span>", true, true);
			// date phasing
			sourceOfDate = sourceOfDocument.substring(
					sourceOfDocument.indexOf("class=\"date\"") == -1 ? 0 : sourceOfDocument.indexOf("class=\"date\""));
			date = phaser.phase(sourceOfDate, "<strong>", "</strong>", true, true);
			time = phaser.phase(sourceOfDate, "</strong>", "</div>", true, true);
			// Date
			Date d = new Date();
			String[] tempD = date.split("\\.");
			d.setYear(Integer.parseInt(tempD[0]));
			d.setMonth(Integer.parseInt(tempD[1]));
			d.setDate(Integer.parseInt(tempD[2]));
			// Time
			String[] tempT = time.split(":");
			d.setHours(Integer.parseInt(tempT[0]));
			d.setMinutes(Integer.parseInt(tempT[1]));
			d.setSeconds(Integer.parseInt(tempT[2]));

			// document story phasing
			ArrayList<String> storyList = phaser.phase(
					sourceOfDocument.substring(sourceOfDocument.indexOf("<div class=\"contentBody\">") == -1 ? 0
							: sourceOfDocument.indexOf("<div class=\"contentBody\">") + "<div class=\"contentBody\">".length()),
					"<p", "</p>", "<p", false, true);
			story = "";
			for (int j = 0; j < storyList.size(); j++) {
				if (storyList.get(j).contains("/>"))
					continue;
				story = story + storyList.get(j);
			}
			story = phaser.storyFilter(story);
			
			// phasing comments
			String comID, comAuthor, comStory, associated, comDate, comTime, sourceOfCdate;
			ArrayList<Comment> commentList = new ArrayList<Comment>();
			ArrayList<String> sourceList = sourcesOfCommentsList.get(i);
			for (int j = 0; j < sourceList.size(); j++) {
				String sourceOfComment = sourceList.get(j);

				// Comment ID
				comID = phaser.phase(
						sourceOfComment.substring(sourceOfComment.indexOf("<a name=\"comment_") + "<a name=\"comment_".length()),
						"<a name=\"comment_", "\"", true, true);

				// Comment Author
				comAuthor = phaser.phase(
						sourceOfComment.substring(sourceOfComment.indexOf("<div class=\"author\">") == -1 ? 0
								: sourceOfComment.indexOf("<div class=\"author\">") + "<div class=\"author\">".length()),
						"/>", "</div>", true, true);

				// Comment Date
				sourceOfCdate = sourceOfComment.substring(sourceOfComment.indexOf("<div class=\"date\">") == -1 ? 0
						: sourceOfComment.indexOf("<div class=\"date\">") + "<div class=\"date\">".length());
				// Date
				comDate = phaser.phase(sourceOfCdate, "<strong>", "</strong>", true, true);
				// Time
				comTime = phaser.phase(sourceOfCdate, "</strong>", "</div>", true, true);

				// Date
				Date cd = new Date();
				String[] tempCD = comDate.split("\\.");
				cd.setYear(Integer.parseInt(tempCD[0]));
				cd.setMonth(Integer.parseInt(tempCD[1]));
				cd.setDate(Integer.parseInt(tempCD[2]));
				// Time
				String[] tempCT = comTime.split(":");
				cd.setHours(Integer.parseInt(tempCT[0]));
				cd.setMinutes(Integer.parseInt(tempCT[1]));
				cd.setSeconds(Integer.parseInt(tempCT[2]));

				// Comment Story
				comStory = phaser.phase(
						sourceOfComment.substring(sourceOfComment.indexOf("<div class=\"comment_") == -1 ? 0
								: sourceOfComment.indexOf("<div class=\"comment_") + "<div class=\"comment_".length()),
						"\">", "</div>", false, true);

				// Comment Associated Index(new)
				if (sourceOfComment.contains("replyIndent"))
					associated = phaser.phase(sourceOfComment, "parent_srl_", "reply", true, true);
				else
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
