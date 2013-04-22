package com.ruyicai.dataanalysis.timer.news;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.ruyicai.dataanalysis.domain.news.News;
import com.ruyicai.dataanalysis.util.DateUtil;
import com.ruyicai.dataanalysis.util.HttpUtil;
import com.ruyicai.dataanalysis.util.JingCaiUtil;
import com.ruyicai.dataanalysis.util.StringUtil;

/**
 * 抓取新闻的定时任务
 * @author Administrator
 *
 */
@Service
public class FetchNewsService {

	private Logger logger = LoggerFactory.getLogger(FetchNewsService.class);
	
	@Value("${jingCaiNewsUrl}")
	private String jingCaiNewsUrl;
	
	@Value("${jingCaiZuQiuUrl}")
	private String jingCaiZuQiuUrl;
	
	public void process() {
		logger.info("抓取新闻开始");
		long startmillis = System.currentTimeMillis();
		try {
			//竞彩独家(http://info.sporttery.cn/roll/fb_list.php?&s=fb&c=%BE%BA%B2%CA%B6%C0%BC%D2)
			String urlDuJia = jingCaiNewsUrl + "?&s=fb&c="+URLEncoder.encode("竞彩独家", "gbk");
			doProcess(urlDuJia);
			
			//竞彩专家(http://info.sporttery.cn/roll/fb_list.php?&s=fb&c=%BE%BA%B2%CA%D7%A8%BC%D2)
			String urlZhuanJia = jingCaiNewsUrl + "?&s=fb&c="+URLEncoder.encode("竞彩专家", "gbk");
			doProcess(urlZhuanJia);
			
			//竞彩足球(http://www.sporttery.cn/football/index.html)
			doProcessJcZq(jingCaiZuQiuUrl);
		} catch (Exception e) {
			logger.error("抓取新闻异常", e);
		}
		long endmillis = System.currentTimeMillis();
		logger.info("抓取新闻结束, 共用时 " + (endmillis - startmillis));
	}
	
	private void doProcess(String url) throws IOException {
		Document listDocument = Jsoup.connect(url).timeout(5000).get();
		Elements uls = listDocument.select("div.MainLeftBox > ul");
		for (Element ul : uls) {
			Elements lis = ul.select("> li");
			for (Element li : lis) {
				parseLiElement(li);
			}
		}
	}
	
	private Date getPublishTime(Document contentDocument) {
		Date publishTime = null; //发布时间
		Element dateBox = contentDocument.select("div.DateBox > div").first();
		String dateBoxString = dateBox.text();
		String[] dateBoxStrings = dateBoxString.split(" ");
		if (dateBoxStrings!=null&&dateBoxStrings.length>=2) {
			String publishTimeString = dateBoxStrings[0] + " " + dateBoxStrings[1] + ":00";
			publishTime = DateUtil.parse(publishTimeString);
		}
		return publishTime;
	}
	
	private String getNewsContent(Document contentDocument) {
		Elements contents = contentDocument.select("div.Con");
		Elements ps = contents.select("p");
		StringBuilder builder = new StringBuilder();
		for (Element p : ps) {
			if (!StringUtil.isEmpty(p.text())&&!p.html().equals("&nbsp;")) {
				builder.append(p.text());
			}
		}
		return builder.toString();
	}
	
	private boolean containsWeek(String title) {
		boolean contains = false;
		if (title!=null&&(title.indexOf("周一")>-1||title.indexOf("周二")>-1||title.indexOf("周三")>-1
				||title.indexOf("周四")>-1||title.indexOf("周五")>-1||title.indexOf("周六")>-1||title.indexOf("周日")>-1)) {
			contains = true;
		}
		return contains;
	}
	
	private String getEvent(String title, Date publishTime) {
		String event = ""; //赛事信息
		if (containsWeek(title)&&publishTime!=null) {
			String weekStr = title.substring(title.indexOf("周"), title.indexOf("周")+2);
			BigDecimal weekIdBig = JingCaiUtil.WEEKID.get(weekStr);
			int weekIdInt = JingCaiUtil.WEEK.get(weekIdBig.intValue()) ;
			String teamId = title.substring(title.indexOf("周")+2, title.indexOf("周")+5);
			if (StringUtil.isInt(teamId)) {
				String type = "";
				if (Integer.parseInt(teamId)<=300) { //足球
					type = "1";
				} else { //篮球
					type = "0";
				}
				String day = "";
				Calendar calendar = Calendar.getInstance();
				for (int i = 0; i < 4; i++) {
					calendar.setTime(publishTime);
					calendar.add(Calendar.DATE, i);
					int weekId = JingCaiUtil.getWeekid(calendar.getTime());
					if (weekIdInt==weekId) {
						day = DateUtil.format("yyyyMMdd", calendar.getTime());
						break;
					}
				}
				event = StringUtil.join("_", type, day, weekIdBig.toString(), teamId);
			}
		}
		return event;
	}
	
	private void doProcessJcZq(String url) throws IOException {
		Document listDocument = Jsoup.connect(url).timeout(5000).get();
		Elements left360s = listDocument.select("div.left360");
		for (Element left360 : left360s) {
			Elements borders = left360.select("> div.border");
			for (Element border : borders) {
				Elements ul03s = border.select("> ul.ul03");
				for (Element ul03 : ul03s) {
					Elements lis = ul03.select("> li");
					for (Element li : lis) {
						parseLiElement(li);
					}
				}
			}
		}
	}
	
	private void parseLiElement(Element li) throws IOException {
		Element a = li.select("a").first();
		String href = a.attr("href"); //新闻地址(http://www.sporttery.cn/football/jczj/2013/0417/54063.html)
		String outId = ""; //外部id
		if (!StringUtil.isEmpty(href)&&href.indexOf("/")>-1&&href.indexOf(".")>-1) {
			outId = href.substring(href.lastIndexOf("/")+1, href.lastIndexOf("."));
		}
		if (StringUtil.isEmpty(outId)) {
			return ;
		}
		List<News> newsList = News.findByOutId(outId);
		if (newsList!=null&&newsList.size()>0) {
			return ;
		}
		//获取新闻内容
		fetchNewsContent(href, outId);
	}
	
	private void fetchNewsContent(String url, String outId) throws IOException {
		//Document contentDocument = Jsoup.connect(url).timeout(5000).get();
		Document contentDocument = Jsoup.parse(new URL(url).openStream(), HttpUtil.GBK, url);
		Date publishTime = getPublishTime(contentDocument); //发布时间
		String content = getNewsContent(contentDocument); //新闻内容
		
		Element textTitle = contentDocument.select("div.TextTitle").first();
		String title = textTitle.text(); //新闻标题
		if (StringUtil.isEmpty(title)||StringUtil.isEmpty(content)) {
			return ;
		}
		String event = getEvent(title, publishTime); //赛事信息
		
		News news = new News();
		news.setTitle(title);
		news.setContent(content);
		news.setPublishtime(publishTime);
		news.setOutid(outId);
		news.setEvent(event);
		news.persist();
	}
	
	public void fetchNewsByUrl(String url) throws IOException {
		String outId = ""; //外部id
		if (!StringUtil.isEmpty(url)&&url.indexOf("/")>-1&&url.indexOf(".")>-1) {
			outId = url.substring(url.lastIndexOf("/")+1, url.lastIndexOf("."));
		}
		if (StringUtil.isEmpty(outId)) {
			return ;
		}
		//Document contentDocument = Jsoup.connect(url).timeout(5000).get();
		Document contentDocument = Jsoup.parse(new URL(url).openStream(), HttpUtil.GBK, url);
		Date publishTime = getPublishTime(contentDocument); //发布时间
		String content = getNewsContent(contentDocument); //新闻内容
		
		Element textTitle = contentDocument.select("div.TextTitle").first();
		String title = textTitle.text(); //新闻标题
		if (StringUtil.isEmpty(title)||StringUtil.isEmpty(content)) {
			return ;
		}
		String event = getEvent(title, publishTime); //赛事信息
		
		List<News> newsList = News.findByOutId(outId);
		if (newsList==null||newsList.size()<=0) {
			News news = new News();
			news.setTitle(title);
			news.setContent(content);
			news.setPublishtime(publishTime);
			news.setOutid(outId);
			news.setEvent(event);
			news.persist();
		} else if (newsList!=null&&newsList.size()==1) {
			News news = newsList.get(0);
			news.setTitle(title);
			news.setContent(content);
			news.setPublishtime(publishTime);
			news.setOutid(outId);
			news.setEvent(event);
			news.merge();
		}
	}
	
	public static void main(String[] args) throws Exception {
		//获取新闻内容
		//Document contentDocument = Jsoup.connect("http://www.sporttery.cn/football/jcdj/2013/0416/53972.html").timeout(5000).get();
		/*String url = "http://www.sporttery.cn/football/jcdj/2013/0416/53972.html";
		Document contentDocument = Jsoup.parse(new URL(url).openStream(), HttpUtil.GBK, url);
		Date publishTime = new Date(); //发布时间
		Element dateBox = contentDocument.select("div.DateBox > div").first();
		String dateBoxString = dateBox.text();
		String[] dateBoxStrings = dateBoxString.split(" ");
		if (dateBoxStrings!=null&&dateBoxStrings.length>2) {
			String publishTimeString = dateBoxStrings[0] + " " + dateBoxStrings[1] + ":00";
			publishTime = DateUtil.parse(publishTimeString);
		}
		Elements contents = contentDocument.select("div.Con");
		Elements ps = contents.select("p");
		StringBuilder builder = new StringBuilder();
		for (Element p : ps) {
			if (!StringUtil.isEmpty(p.text())) {
				builder.append(p.text());
			}
		}
		String content = builder.toString(); //新闻内容
		System.out.println(content);*/
	}
	
}
