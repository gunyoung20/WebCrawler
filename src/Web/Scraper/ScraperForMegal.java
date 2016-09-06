package Web.Scraper;

public class ScraperForMegal extends Scraper{
	public ScraperForMegal(String u, String t){
		super(u, t); 
		searchUrl = u.substring(0, u.indexOf("/page/") + "/page/".length());
		page = Integer.valueOf(u.substring(u.indexOf("/page/")+"/page/".length(), u.indexOf("?")));
		}
	
	public void setSearchUrl(String searchUrl) { 
		this.searchUrl = searchUrl.substring(0, searchUrl.indexOf("/page/") + "/page/".length());
		url = (searchUrl.contains("//")?searchUrl.substring(0, searchUrl.substring(searchUrl.indexOf("//")+2).indexOf("/") + searchUrl.indexOf("//") + 2):"");
		page = Integer.valueOf(searchUrl.substring(searchUrl.indexOf("/page/")+"/page/".length(), searchUrl.indexOf("?"))); 
		}
		
	public String getSearchUrl(){
		return searchUrl+page+"?search="+getTargetName()+"&sf=all";
	}
	public String readWebSite(int mode) {
		return readWebSite(this.getSearchUrl(), mode);
	}
}
