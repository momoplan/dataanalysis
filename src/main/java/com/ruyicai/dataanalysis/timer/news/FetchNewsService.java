package com.ruyicai.dataanalysis.timer.news;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import org.apache.commons.lang.StringUtils;
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
import com.ruyicai.dataanalysis.util.StringUtil;
import com.ruyicai.dataanalysis.util.jc.JingCaiUtil;

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
			//竞彩足球-竞彩独家(http://info.sporttery.cn/roll/fb_list.php?&s=fb&c=%BE%BA%B2%CA%B6%C0%BC%D2)
			String urlDuJia = jingCaiNewsUrl + "fb_list.php?&s=fb&c="+URLEncoder.encode("竞彩独家", "gbk");
			doProcess(urlDuJia);
			
			//竞彩足球-竞彩专家(http://info.sporttery.cn/roll/fb_list.php?&s=fb&c=%BE%BA%B2%CA%D7%A8%BC%D2)
			String urlZhuanJia = jingCaiNewsUrl + "fb_list.php?&s=fb&c="+URLEncoder.encode("竞彩专家", "gbk");
			doProcess(urlZhuanJia);
			
			//竞彩足球(http://www.sporttery.cn/football/index.html)
			doProcessJcZq(jingCaiZuQiuUrl);
			
			//竞彩篮球-分析推荐(http://info.sporttery.cn/roll/bk_list.php?s=bk&c=%B7%D6%CE%F6%CD%C6%BC%F6)
			String jcLqFxtjUrl = jingCaiNewsUrl + "bk_list.php?s=bk&c="+URLEncoder.encode("分析推荐", "gbk");
			doProcess(jcLqFxtjUrl);
			
			//竞彩篮球-竞彩前瞻(http://info.sporttery.cn/roll/bk_list.php?s=bk&c=%BE%BA%B2%CA%C7%B0%D5%B0)
			String jcLqJcqzUrl = jingCaiNewsUrl + "bk_list.php?s=bk&c="+URLEncoder.encode("竞彩前瞻", "gbk");
			doProcess(jcLqJcqzUrl);
		} catch (Exception e) {
			logger.error("抓取新闻异常", e);
		}
		long endmillis = System.currentTimeMillis();
		logger.info("抓取新闻结束, 共用时 " + (endmillis - startmillis));
	}
	
	private void doProcess(String url) {
		try {
			Document listDocument = Jsoup.connect(url).timeout(5000).get();
			Elements uls = listDocument.select("div.MainLeftBox > ul");
			for (Element ul : uls) {
				Elements lis = ul.select("> li");
				for (Element li : lis) {
					parseLiElement(li);
				}
			}
		} catch (Exception e) {
			logger.error("抓取新闻-doProcess发生异常", e);
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
			if (StringUtils.isNotBlank(p.text())&&!StringUtils.equals(p.html(), "&nbsp;")) {
				builder.append(p.text()).append("\n");
			}
		}
		return builder.toString();
	}
	
	private String getNewsTitle(Document contentDocument) {
		//http://www.sporttery.cn/football/jcdj/2013/1112/82872.html
		Element textTitle = contentDocument.select("div.TextTitle").first();
		if (textTitle==null) {
			//http://www.sporttery.cn/football/jcdj/2013/1114/83104.html
			textTitle = contentDocument.select("div.TextBox > h2").first();
		}
		return textTitle==null ? null : textTitle.text();
	}
	
	private boolean containsWeek(String title) {
		boolean contains = false;
		if (StringUtils.indexOf(title, "周一")>-1||StringUtils.indexOf(title, "周二")>-1||StringUtils.indexOf(title, "周三")>-1
				||StringUtils.indexOf(title, "周四")>-1||StringUtils.indexOf(title, "周五")>-1||StringUtils.indexOf(title, "周六")>-1
				||StringUtils.indexOf(title, "周日")>-1) {
			contains = true;
		}
		return contains;
	}
	
	private String getEvent(String title, Date publishTime) {
		String event = ""; //赛事信息
		try {
			if (containsWeek(title)&&publishTime!=null) {
				int zhouIndex = title.indexOf("周");
				String weekStr = title.substring(zhouIndex, zhouIndex+2);
				BigDecimal weekIdBig = JingCaiUtil.WEEKID.get(weekStr);
				int weekIdInt = JingCaiUtil.WEEK.get(weekIdBig.intValue()) ;
				if (title.length()<zhouIndex+5) {
					return "";
				}
				String teamId = title.substring(zhouIndex+2, zhouIndex+5);
				if (StringUtils.isNumeric(teamId)) {
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
					if (StringUtils.isNotBlank(type)&&StringUtils.isNotBlank(day)&&StringUtils.isNotBlank(weekIdBig.toString())
							&&StringUtils.isNotBlank(teamId)) {
						event = StringUtil.join("_", type, day, weekIdBig.toString(), teamId);
					}
				}
			}
		} catch (Exception e) {
			logger.error("抓取新闻-getEvent发生异常", e);
		}
		return event;
	}
	
	private void doProcessJcZq(String url) {
		try {
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
		} catch (Exception e) {
			logger.error("抓取新闻-doProcessJcZq发生异常", e);
		}
	}
	
	private void parseLiElement(Element li) {
		try {
			Element a = li.select("a").first();
			String href = a.attr("href"); //新闻地址(http://www.sporttery.cn/football/jczj/2013/0417/54063.html)
			String outId = ""; //外部id
			if (StringUtils.isNotBlank(href)&&StringUtils.indexOf(href, "/")>-1&&StringUtils.indexOf(href, ".")>-1) {
				outId = StringUtils.substring(href, StringUtils.lastIndexOf(href, "/")+1, StringUtils.lastIndexOf(href, "."));
			}
			//获取新闻内容
			fetchNewsContent(href, outId);
		} catch (IOException e) {
			logger.error("抓取新闻-parseLiElement发生异常", e);
		}
	}
	
	private void fetchNewsContent(String url, String outId) throws IOException {
		Document contentDocument = Jsoup.parse(new URL(url).openStream(), HttpUtil.GBK, url);
		Date publishTime = getPublishTime(contentDocument); //发布时间
		String content = getNewsContent(contentDocument); //新闻内容
		String title = getNewsTitle(contentDocument); //新闻标题
		if (StringUtils.isBlank(title)||StringUtils.isBlank(content)) {
			//System.out.println("aa");
			return ;
		}
		//防止重复
		List<News> newsList = getNewsListByTitle(title);
		if (newsList!=null&&newsList.size()>0) {
			return ;
		}
		String event = getEvent(title, publishTime); //赛事信息
		if (StringUtils.isBlank(event)) {
			return ;
		}
		
		News news = new News();
		news.setTitle(title);
		news.setContent(content);
		news.setPublishtime(publishTime);
		news.setOutid(outId);
		news.setEvent(event);
		news.setUrl(url);
		news.persist();
	}
	
	public void fetchNewsByUrl(String url) throws IOException {
		String outId = ""; //外部id
		if (StringUtils.isNotBlank(url)&&StringUtils.indexOf(url, "/")>-1&&StringUtils.indexOf(url, ".")>-1) {
			outId = StringUtils.substring(url, StringUtils.lastIndexOf(url, "/")+1, StringUtils.lastIndexOf(url, "."));
		}
		if (StringUtils.isBlank(outId)) {
			return ;
		}
		Document contentDocument = Jsoup.parse(new URL(url).openStream(), HttpUtil.GBK, url);
		Date publishTime = getPublishTime(contentDocument); //发布时间
		String content = getNewsContent(contentDocument); //新闻内容
		
		Element textTitle = contentDocument.select("div.TextTitle").first();
		String title = textTitle.text(); //新闻标题
		if (StringUtils.isBlank(title)||StringUtils.isBlank(content)) {
			return ;
		}
		String event = getEvent(title, publishTime); //赛事信息
		//防止重复
		List<News> newsList = getNewsListByTitle(title);
		if (newsList!=null&&newsList.size()>0) {
			return ;
		}
		if (newsList==null||newsList.size()<=0) {
			News news = new News();
			news.setTitle(title);
			news.setContent(content);
			news.setPublishtime(publishTime);
			news.setOutid(outId);
			news.setEvent(event);
			news.setUrl(url);
			news.persist();
		} else if (newsList!=null&&newsList.size()>=1) {
			News news = newsList.get(0);
			news.setTitle(title);
			news.setContent(content);
			news.setPublishtime(publishTime);
			news.setOutid(outId);
			news.setEvent(event);
			news.setUrl(url);
			news.merge();
		}
	}
	
	private List<News> getNewsListByTitle(String title) {
		StringBuilder builder = new StringBuilder(" where");
		List<Object> params = new ArrayList<Object>();
		
		builder.append(" o.title=? ");
		params.add(title);
		List<News> list = News.getList(builder.toString(), "order by o.publishtime asc", params);
		return list;
	}
	
}
