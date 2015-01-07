package com.ruyicai.dataanalysis.timer.zq;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
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
import com.ruyicai.dataanalysis.domain.CupMatchJiFen;
import com.ruyicai.dataanalysis.domain.CupMatchJiFenPK;
import com.ruyicai.dataanalysis.util.NumberUtil;
import com.ruyicai.dataanalysis.util.zq.JmsZqUtil;

/**
 * @Description: 亚洲杯积分榜数据抓取
 * 
 * @author chenchuang
 * @date 2014年12月17日上午10:24:32
 * @version V1.0
 * 
 */
@Service
public class CupMatchJiFenUpdateService {

	private Logger logger = LoggerFactory.getLogger(CupMatchJiFenUpdateService.class);

	@Autowired
	JmsZqUtil jmsZqUtil;

	public void process() {
		logger.info("亚洲杯积分榜数据更新 start");
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
			page = wc.getPage("http://info.win007.com/cn/CupMatch/95.html");
			DomNodeList<DomElement> links = page.getElementsByTagName("td");

			int flagA = -1;
			int flagB = -1;
			int flagC = -1;
			int flagD = -1;
			for (int i = 0; i < links.size(); i++) {
				if (links.get(i).asText().equals("A组积分")) {
					flagA = i;
				} else if (links.get(i).asText().equals("B组积分")) {
					flagB = i;
				} else if (links.get(i).asText().equals("C组积分")) {
					flagC = i;
				} else if (links.get(i).asText().equals("D组积分")) {
					flagD = i;
				}
			}
			if (flagA > -1 && flagB > -1 && flagC > -1 && flagD > -1) {
				List<Integer> grouplist = new ArrayList<Integer>();
				grouplist.add(flagA);
				grouplist.add(flagB);
				grouplist.add(flagC);
				grouplist.add(flagD);
				CupMatchJiFen cupMatch = null;
				CupMatchJiFenPK pk = null;
				for (int i = 0; i < grouplist.size(); i++) { // ABCD四个小组
					int tempCount = 0;
					int totalCount = 10;
					String group = "";
					if (grouplist.get(i) == flagA) {
						group = "A";
					} else if (grouplist.get(i) == flagB) {
						group = "B";
					} else if (grouplist.get(i) == flagC) {
						group = "C";
					} else if (grouplist.get(i) == flagD) {
						group = "D";
					}
					for (int j = 1; j <= 4; j++) { // 排名1234
						boolean ismod = false;
						Integer ranking = NumberUtil.parseInt(links.get(grouplist.get(i) + tempCount + 1).asText(), 0);
						String total = links.get(grouplist.get(i) + tempCount + 3).asText();
						String win = links.get(grouplist.get(i) + tempCount + 4).asText();
						String ping = links.get(grouplist.get(i) + tempCount + 5).asText();
						String loss = links.get(grouplist.get(i) + tempCount + 6).asText();
						String get = links.get(grouplist.get(i) + tempCount + 7).asText();
						String miss = links.get(grouplist.get(i) + tempCount + 8).asText();
						String jing = links.get(grouplist.get(i) + tempCount + 9).asText();
						String score = links.get(grouplist.get(i) + tempCount + 10).asText();
						pk = new CupMatchJiFenPK(group, links.get(grouplist.get(i) + tempCount + 2).asText());
						cupMatch = CupMatchJiFen.findCupMatchJiFen(pk);
						if (cupMatch == null) {
							cupMatch = new CupMatchJiFen();
							cupMatch.setId(pk);
							cupMatch.setRanking(ranking);
							cupMatch.setTotal(total);
							cupMatch.setWin(win);
							cupMatch.setPing(ping);
							cupMatch.setLoss(loss);
							cupMatch.setGet(get);
							cupMatch.setMiss(miss);
							cupMatch.setJing(jing);
							cupMatch.setScore(score);
							ismod = true;
						} else {
							if (ranking != cupMatch.getRanking()) {
								ismod = true;
								cupMatch.setRanking(ranking);
							}
							if (StringUtils.isNotBlank(total) && !total.equals(cupMatch.getTotal())) {
								ismod = true;
								cupMatch.setTotal(total);
							}
							if (StringUtils.isNotBlank(win) && !win.equals(cupMatch.getWin())) {
								ismod = true;
								cupMatch.setWin(win);
							}
							if (StringUtils.isNotBlank(ping) && !ping.equals(cupMatch.getPing())) {
								ismod = true;
								cupMatch.setPing(ping);
							}
							if (StringUtils.isNotBlank(loss) && !loss.equals(cupMatch.getLoss())) {
								ismod = true;
								cupMatch.setLoss(loss);
							}
							if (StringUtils.isNotBlank(get) && !get.equals(cupMatch.getGet())) {
								ismod = true;
								cupMatch.setGet(get);
							}
							if (StringUtils.isNotBlank(miss) && !miss.equals(cupMatch.getMiss())) {
								ismod = true;
								cupMatch.setMiss(miss);
							}
							if (StringUtils.isNotBlank(jing) && !jing.equals(cupMatch.getJing())) {
								ismod = true;
								cupMatch.setJing(jing);
							}
							if (StringUtils.isNotBlank(score) && !score.equals(cupMatch.getScore())) {
								ismod = true;
								cupMatch.setScore(score);
							}
						}
						if (ismod) {
							cupMatch.merge();
							jmsZqUtil.sendAsianCupJMS("AsianCup");	//亚洲杯
						}
						tempCount = tempCount + totalCount;
					}
				}
				//=========================
				jmsZqUtil.sendAsianCupJMS("AsianCup");	//亚洲杯
			} else {
				logger.info("未抓取到亚洲杯分组赛的积分榜数据");
			}
		} catch (IOException e) {
			logger.error("亚洲杯积分榜数据更新发生异常", e);
		}
		long endmillis = System.currentTimeMillis();
		logger.info("更新亚洲杯积分榜数据结束, 共用时 " + (endmillis - startmillis));
	}

}
