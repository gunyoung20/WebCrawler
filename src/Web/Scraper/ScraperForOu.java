package Web.Scraper;

import Data.WebSource;

public class ScraperForOu extends Scraper {

	public ScraperForOu(String u, String t){
		super(u, t); 
		searchUrl = u.substring(0, u.indexOf("keyword=") + "keyword=".length());
		page = searchUrl.contains("page=")?Integer.valueOf(searchUrl.substring(searchUrl.indexOf("page=")+"page=".length())):1;
		}
	
	public void setSearchUrl(String searchUrl) { 
		this.searchUrl = searchUrl; 
		url = (searchUrl.contains("//")?searchUrl.substring(0, searchUrl.substring(searchUrl.indexOf("//")+2).indexOf("/") + searchUrl.indexOf("//") + 2):"");
		page = searchUrl.contains("page=")?Integer.valueOf(searchUrl.substring(searchUrl.indexOf("page=")+"page=".length())):1; 
		}
	
//	"&Submit.x=0&Submit.y=0&Submit=%EA%B2%80%EC%83%89&page=1");
	public String getSearchUrl(){
		return searchUrl+getTargetName()+"&Submit.x=0&Submit.y=0&Submit=%EA%B2%80%EC%83%89&page="+page;
	}
	public WebSource readWebSite(int mode) {
		return readWebSite(this.getSearchUrl(), mode);
	}
}
