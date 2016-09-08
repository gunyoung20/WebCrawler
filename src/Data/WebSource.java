package Data;

public class WebSource {
	public WebSource(String url, String webUrl, String webName, String target, Date date){
		this(url, webUrl, webName, target, date, "", "");
	}
	public WebSource(String url, String webUrl, String webName, String target, String date){
		this(url, webUrl, webName, target, date, "", "");
	}
	public WebSource(String url, String webUrl, String webName, String target, Date date, String sourceName){
		this(url, webUrl, webName, target, date, "", "");
	}
	public WebSource(String url, String webUrl, String webName, String target, String date, String sourceName){
		this(url, webUrl, webName, target, date, "", "");
	}
	public WebSource(String url, String webUrl, String webName, String target, String date, String sourceName, String source){
		this.url = url; this.webUrl = webUrl; this.webName = webName; this.target = target; 
		this.date = new Date(date); this.source = source; this.sourceName = sourceName;
	}
	public WebSource(String url, String webUrl, String webName, String target, Date date, String sourceName, String source){
		this.url = url; this.webUrl = webUrl; this.webName = webName; this.target = target; 
		this.date = date; this.source = source; this.sourceName = sourceName;
	}
	
	private String url;
	private String webUrl;
	private String webName;
	private String target;
	private Date date;
	private String source;
	private String sourceName;

	public void setSource(String source) { this.source = source; }
	public void setSourceName(String sourceName) { this.sourceName = sourceName; }
	
	public String getSource() { return source; }
	public String getSourceName() { return sourceName; }
	public String getUrl() { return url; }
	public String getWebUrl() { return webUrl; }
	public String getWebName() { return webName; }
	public String getTarget() { return target; }
	public Date getDate(){ return date; }
}
