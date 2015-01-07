package com.ruyicai.dataanalysis;

import java.io.IOException;
import java.util.logging.Level;

import org.apache.commons.logging.LogFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

public class AsianCupDataTest {
	
	private Logger logger = LoggerFactory.getLogger(AsianCupDataTest.class);

	public void sinaProcess(){
		logger.info("亚洲杯射手榜数据更新 start");
		LogFactory.getFactory().setAttribute("org.apache.commons.logging.Log",
				"org.apache.commons.logging.impl.NoOpLog"); // 关闭运行时的日志
		java.util.logging.Logger.getLogger("com.gargoylesoftware").setLevel(Level.OFF);
		WebClient wc = new WebClient(BrowserVersion.CHROME);
		wc.getOptions().setUseInsecureSSL(true);
		wc.getOptions().setJavaScriptEnabled(true); // 启用JS解释器，默认为true
		wc.getOptions().setCssEnabled(false); // 禁用css支持
		wc.getOptions().setThrowExceptionOnScriptError(false); // js运行错误时，是否抛出异常
		wc.getOptions().setTimeout(1000000); // 设置连接超时时间 ，这里是10S。如果为0，则无限期等待
		wc.getOptions().setDoNotTrackEnabled(false);
		wc.getOptions().setDoNotTrackEnabled(true);
		wc.getOptions().setRedirectEnabled(false);
		HtmlPage page;
		try {
			page = wc.getPage("http://app.gooooal.com/goalscorer.do?lid=318&sid=2011&pid=140&lang=cn");
//			page = wc.getPage("http://app.gooooal.com/goalscorer.do?lid=318&sid=2011&lang=cn&l=");
//			Document document = Jsoup.connect("http://app.gooooal.com/goalscorer.do?lid=318&sid=2011&pid=140&lang=cn").get();
//			System.out.println(document);
			System.out.println(page.asText());
//			DomNodeList<DomElement> links = page.getElementsByTagName("td");
//			for (int i = 0; i < links.size(); i++) {
//				System.out.println(links.get(i).asText());
//			}
		}catch (IOException e) {
			logger.error("亚洲杯射手榜数据更新发生异常", e);
		}
	}
	
	
	public static void main(String[] args) {
		new AsianCupDataTest().sinaProcess();
	}
	
}
