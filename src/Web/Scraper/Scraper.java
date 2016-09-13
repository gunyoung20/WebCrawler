package Web.Scraper;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

import Data.WebSource;
import Storage.FileHandler;
import Storage.DAO.WebSourceDAO;
import Web.Phaser;

public class Scraper {
	public Scraper(){ this("", ""); }
	public Scraper(String s){ this(s, ""); }
	public Scraper(String s, String t){ 
		searchUrl = s;
		url = (s.contains("//")?s.substring(0, s.substring(s.indexOf("//")+2).indexOf("/") + s.indexOf("//") + 2):"");
		target = t; 
		nowTimeForCollect = new SimpleDateFormat("YYYY-MM-dd HHmmss").format(Calendar.getInstance().getTime());
		targetList = new HashMap<String, String>();
		targetList.put("ilbe", "%EC%9D%BC%EB%B2%A0");
		targetList.put("megalian", "%EB%A9%94%EA%B0%88");
		targetList.put("todayhumor", "%EC%98%A4%EC%9C%A0");
		}
	// URL이 지정하는 Page를 모두 String으로 저장
	//mode-0 : web phasing with collecting web sources from web site on online.
	//mode-1 : only phasing web sources without collecting web sources from web site on online.
	//mode-2 : only phasing web sources without collecting web sources from offline such as file, DB.
	//mode-3 : only collecting web sources from online.
	public WebSource readWebPage(int mode){
		String u = getSearchUrl();
		String sName = "Page";
		String webName = getWebName();
		
		WebSource source = new WebSource(u, url, webName, target, nowTimeForCollect);

		String buffer = "";
		String path = System.getProperty("user.dir") + "/temp/Page/" + webName + "/" + target;
		String dummy = webName + "-" + target + "-" + sName;
		String dummyExtension = "txt";
		
		FileHandler dfh = new FileHandler(dummy, dummyExtension);
		if(mode == 2)
			buffer = dfh.readWebFile(u, dummy + page);
		else
			buffer = scrapWebSource(u, path, dummy);
		if(mode == 0 || mode == 3)
			dfh.writeWebFile(path + "/" + nowTimeForCollect, dummy + page,	buffer);
		
		source.setSourceName(sName);
		source.setSource(buffer);
		
		return source;
	}
	public WebSource readWebSite(String u, int mode){ return readWebSite(getSearchUrl(), "Document", mode); }
	public WebSource readWebSite(String u, String sName, int mode) {
		
		WebSourceDAO wsdao = new WebSourceDAO();
		WebSource source = null;
		if(mode == 2)		
			source = wsdao.getSource(u, getWebName(), target, sName);
		else
		{
			Phaser phaser = new Phaser();
			String searchUrl=u, page; 
			if(u.contains("page"))
			{
				page = String.valueOf(phaser.searchDigit(u.substring(u.indexOf("page"))));
				int peIndex = page.equals("-1") ? u.length() - 1 : u.indexOf(page) + page.length();
				searchUrl = u.substring(0, u.indexOf("page")-1) + u.substring(peIndex);
			}
			
			String webName = u.substring(u.indexOf(".")+1).substring(0, u.substring(u.indexOf(".")+1).indexOf("."));
			
			String buffer = "";
			String path = System.getProperty("user.dir") + "/temp/" + webName + "/" + target;
			String dummy = target + "-" + sName;
			
			buffer = scrapWebSource(searchUrl, path, dummy);

			source = new WebSource(searchUrl, url, webName, target, nowTimeForCollect, sName, buffer);
			
			if(mode == 0 || mode == 3)
			{
				if(!wsdao.update(source))
					wsdao.insert(source);
			}
		}
		
		return source;
	}
	private String scrapWebSource(String u, String dir, String fileName){
		String buffer = "";

		String dummyExtension = "txt";
		FileHandler dfh = new FileHandler(fileName, dummyExtension);
		
		while(true)
		{
			try {
				if ((new File(dir)).mkdirs() == true) {
					System.out.println("Directories : " + dir + " created");
				}

				Runtime rt = Runtime.getRuntime();
				Process p = rt.exec(System.getProperty("user.dir") + "/dist/app.exe " + u + " " + fileName + " " + dir);
				p.waitFor();
			} catch (Exception e) {
				e.getStackTrace();
			}
			sleep(300);
			buffer = dfh.readWebFile(dir, fileName);
			if(checkErrorCode(buffer))
			{
				System.err.println("Access Error path : " + dir + " target : " + fileName);
				continue;
			}
			break;
		}
		
		return buffer;
	}
	public boolean checkErrorCode(String source)
	{
		String[] ErrorCode = {WebAccessDeniedCode, WebAccessErrorCode, DeletedDocumentCode, BlindedDocumentCode};
		if(source==null)
		{
			System.err.println("Access Error(No have data in file)");
			return true;
		}
		
		for(int i = 0; i < ErrorCode.length; i++)
			if(source.contains(ErrorCode[i]))
			{
				System.err.println("Access Error Source : " + source);
				return true;
			}
		
		return false;
	}
	
	protected void sleep(int time) {
		try {
			Thread.sleep(time);
		} catch (Exception e) {
			System.err.println(e.getStackTrace());
		}
	}	
	public String getUrl(){ return url; }
	public String getWebName(){ 
		return url.contains("www")?
				url.substring(url.indexOf(".")+1).substring(0, url.substring(url.indexOf(".")+1).indexOf("."))
				:url.substring(url.indexOf("//")+2).substring(0, url.substring(url.indexOf("//")+2).indexOf(".")); }
	public String getTarget() { return target; }
	public int getPage(){ return page; }
	public String getPageToString(){ return String.valueOf(page); }
	public String getTargetName(){ return targetList.get(target); }

	public void setTarget(String target) { this.target = target; }
	public void setPage(int page){ this.page = page; }
	public void setPage(String page){ this.page = Integer.valueOf(page); }

	protected String url;
	protected String searchUrl;

	protected String target;
	protected int page;
	
	private String nowTimeForCollect;
	private HashMap<String, String> targetList;

	public final String WebAccessDeniedCode= "Do not Accessed Website";
	public final String WebAccessErrorCode = "비정상적인 검색입니다.";
	public final String DeletedDocumentCode = "삭제된 글입니다.";
	public final String BlindedDocumentCode = "현재 블라인드 상태인 게시물입니다.";

	public String getSearchUrl() { return searchUrl; }
	public void setSearchUrl(String searchUrl) { 
		this.searchUrl = searchUrl; 
		url = (searchUrl.contains("//")?searchUrl.substring(0, searchUrl.substring(searchUrl.indexOf("//")+2).indexOf("/") + searchUrl.indexOf("//") + 2):"");}

}
