package Web.Scraper;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

import Storage.FileHandler;

public class Scraper {
	public Scraper(){ this("", ""); }
	public Scraper(String s){ this(s, ""); }
	public Scraper(String s, String t){ 
		searchUrl = s;
		url = (s.contains("//")?s.substring(0, s.substring(s.indexOf("//")+2).indexOf("/") + s.indexOf("//") + 2):"");
		target = t; 
		nowTimeForCollect = new SimpleDateFormat("YYYY.MM.dd HHmmss").format(Calendar.getInstance().getTime());
		targetList = new HashMap<String, String>();
		targetList.put("Ilbe", "%EC%9D%BC%EB%B2%A0");
		targetList.put("Megal", "%EB%A9%94%EA%B0%88");
		targetList.put("Ou", "%EC%98%A4%EC%9C%A0");
		}
	// URL이 지정하는 Page를 모두 String으로 저장
	//mode-0 : web phasing with collecting web sources from web site on online.
	//mode-1 : only phasing web sources without collecting web sources from web site on online.
	//mode-2 : only phasing web sources without collecting web sources from offline such as file, DB.
	//mode-3 : only collecting web sources from online.
	public String readWebSite(int mode){ return readWebSite(this.getSearchUrl(), mode); }
	public String readWebSite(String u, int mode) {
		String buffer = "";
		String path = System.getProperty("user.dir") + "/WebSource/"
		+ u.substring(u.indexOf(".")+1).substring(0, u.substring(u.indexOf(".")+1).indexOf(".")) + "/"
		+ target;
		String dummy = u.substring(u.indexOf("//")+2).substring(u.substring(u.indexOf("//")+2).indexOf("/")+1);
		dummy = dummy.replace("/", "-");
		dummy = dummy.replace("?", "#");
		String dummyExtension = "txt";
		FileHandler dfh = new FileHandler("", "txt");
		if(mode == 2)
			buffer = dfh.readWebFile(path, dummy);
		else
		{
			while(true)
			{
				try {
					if ((new File(path)).mkdirs() == true) {
						System.out.println("Directories : " + path + " created");
					}

					Runtime rt = Runtime.getRuntime();
					Process p = rt
							.exec(System.getProperty("user.dir") + "/dist/app.exe " + u + " " + dummy + " " + path);
					p.waitFor();
				} catch (Exception e) {
					e.getStackTrace();
				}
				sleep(1200);
				buffer = dfh.readWebFile(path, dummy, dummyExtension);
				if(buffer.contains(WebAccessErrorCode))
					continue;
				break;
			}
		}
		if(mode == 0 || mode == 3)
		{
			dfh.writeWebFile(path + "/" + nowTimeForCollect, dummy,	buffer);
		}
		return buffer;
	}
	
	protected void sleep(int time) {
		try {
			Thread.sleep(time);
		} catch (Exception e) {
			System.err.println(e.getStackTrace());
		}
	}	
	public String getUrl(){ return url; }
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

	public final String WebAccessErrorCode = "비정상적인 검색입니다.<br>잠시 후에 시도하세요!";
	public final String DeletedDocumentCode = "삭제된 글입니다.";

	public String getSearchUrl() { return searchUrl; }
	public void setSearchUrl(String searchUrl) { 
		this.searchUrl = searchUrl; 
		url = (searchUrl.contains("//")?searchUrl.substring(0, searchUrl.substring(searchUrl.indexOf("//")+2).indexOf("/") + searchUrl.indexOf("//") + 2):"");}

}
