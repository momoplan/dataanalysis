package com.ruyicai.dataanalysis.controller.news;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import com.ruyicai.dataanalysis.controller.ResponseData;
import com.ruyicai.dataanalysis.timer.news.FetchNewsService;

@RequestMapping("/systemNews")
@Controller
public class SystemNewsController {

	private Logger logger = LoggerFactory.getLogger(SystemNewsController.class);
	
	@Autowired
	private FetchNewsService fetchNewsService;
	
	@RequestMapping(value = "/fetchNewsAll", method = RequestMethod.POST)
	public @ResponseBody
	ResponseData updateNews() {
		ResponseData rd = new ResponseData();
		try {
			fetchNewsService.process();
		} catch(Exception e) {
			logger.error(e.getMessage(), e);
			rd.setValue(e.getMessage());
		}
		return rd;
	}
	
	@RequestMapping(value = "/fetchNewsByUrl", method = RequestMethod.POST)
	public @ResponseBody
	ResponseData updateNews(@RequestParam("url") String url) {
		ResponseData rd = new ResponseData();
		try {
			fetchNewsService.fetchNewsByUrl(url);
		} catch(Exception e) {
			logger.error(e.getMessage(), e);
			rd.setValue(e.getMessage());
		}
		return rd;
	}
	
}
