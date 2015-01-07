package com.ruyicai.dataanalysis.timer.zq;

import java.io.IOException;
import java.util.logging.Level;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.LogFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.DomElement;
import com.gargoylesoftware.htmlunit.html.DomNodeList;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.ruyicai.dataanalysis.domain.CupMatchRanking;
import com.ruyicai.dataanalysis.domain.CupMatchRankingPK;
import com.ruyicai.dataanalysis.util.NumberUtil;
import com.ruyicai.dataanalysis.util.zq.JmsZqUtil;

/**
 * @Description: 亚洲杯射手榜数据抓取
 * 
 * @author chenchuang
 * @date 2014年12月25日上午11:44:22
 * @version V1.0
 * 
 */
@Service
public class CupMatchRankingUpdateService {

	private Logger logger = LoggerFactory.getLogger(CupMatchRankingUpdateService.class);

	@Autowired
	JmsZqUtil jmsZqUtil;
	
	public void process() {
		logger.info("亚洲杯射手榜数据更新 start");
		long startmillis = System.currentTimeMillis();
		LogFactory.getFactory().setAttribute("org.apache.commons.logging.Log",
				"org.apache.commons.logging.impl.NoOpLog"); // 关闭运行时的日志
		java.util.logging.Logger.getLogger("com.gargoylesoftware").setLevel(Level.OFF);
		WebClient wc = new WebClient(BrowserVersion.CHROME);
		wc.getOptions().setUseInsecureSSL(true);
		wc.getOptions().setJavaScriptEnabled(true); // 启用JS解释器，默认为true
		wc.getOptions().setCssEnabled(false); // 禁用css支持
		wc.getOptions().setThrowExceptionOnScriptError(false); // js运行错误时，是否抛出异常
		wc.getOptions().setTimeout(100000); // 设置连接超时时间 ，这里是10S。如果为0，则无限期等待
		wc.getOptions().setDoNotTrackEnabled(false);
		HtmlPage page;
		try {
			page = wc.getPage("http://info.win007.com/cn/ArcherCup/2013-2015/95.html");
//			page = wc.getPage("http://info.win007.com/cn/ArcherCup/2009-2011/95.html");
			DomNodeList<DomElement> links = page.getElementsByTagName("td");
			int flag = -1;
			for (int i = 0; i < links.size(); i++) {
				if (links.get(i).asText().equals("排名")) {
					flag = i;
				}
			}
			if (flag > -1) {
				String league = "亚洲杯"; // 设定亚洲杯
				String season = "2013-2015"; // 设定亚洲杯赛季
//				String season = "2009-2011"; // 设定亚洲杯赛季
				int tempCount = 7;
				int totalCount = 8;
				CupMatchRanking cupMatch = null;
				CupMatchRankingPK pk = null;
				for (int j = 1; j <= 10; j++) { // 抓取前十名
					boolean ismod = false;
					Integer ranking = NumberUtil.parseInt(links.get(tempCount + 1).asText(), 0);
					String player = links.get(tempCount + 2).asText();
					String country = links.get(tempCount + 3).asText();
					String team = links.get(tempCount + 4).asText();
					String goals = links.get(tempCount + 5).asText();
					pk = new CupMatchRankingPK(ranking, league,season);
					cupMatch = CupMatchRanking.findCupMatchRanking(pk);
					if (cupMatch == null) {
						cupMatch = new CupMatchRanking();
						cupMatch.setId(pk);
						cupMatch.setPlayer(player);
						cupMatch.setCountry(country);
						cupMatch.setTeam(team);
						cupMatch.setGoals(goals);
						ismod = true;
					} else {
						if (StringUtils.isNotBlank(player) && !player.equals(cupMatch.getPlayer())) {
							ismod = true;
							cupMatch.setPlayer(player);
						}
						if (StringUtils.isNotBlank(country) && !country.equals(cupMatch.getCountry())) {
							ismod = true;
							cupMatch.setCountry(country);
						}
						if (StringUtils.isNotBlank(team) && !team.equals(cupMatch.getTeam())) {
							ismod = true;
							cupMatch.setTeam(team);
						}
						if (StringUtils.isNotBlank(goals) && !goals.equals(cupMatch.getGoals())) {
							ismod = true;
							cupMatch.setGoals(goals);
						}
					}
					if (ismod) {
						cupMatch.merge();
						jmsZqUtil.sendAsianCupJMS("AsianCup");
					}
					tempCount = tempCount + totalCount;
				}
			}
		} catch (IOException e) {
			logger.error("亚洲杯射手榜数据更新发生异常", e);
		}
		long endmillis = System.currentTimeMillis();
		logger.info("更新亚洲杯射手榜数据结束, 共用时 " + (endmillis - startmillis));
	}
	
}
