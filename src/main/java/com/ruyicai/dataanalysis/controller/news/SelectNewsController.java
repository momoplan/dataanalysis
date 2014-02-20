package com.ruyicai.dataanalysis.controller.news;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import com.ruyicai.dataanalysis.consts.ErrorCode;
import com.ruyicai.dataanalysis.controller.ResponseData;
import com.ruyicai.dataanalysis.domain.news.News;
import com.ruyicai.dataanalysis.exception.RuyicaiException;
import com.ruyicai.dataanalysis.service.news.SelectNewsService;
import com.ruyicai.dataanalysis.util.Page;

@RequestMapping("/selectNews")
@Controller
public class SelectNewsController {

	private Logger logger = LoggerFactory.getLogger(SelectNewsController.class);
	
	@Autowired
	private SelectNewsService selectNewsService;
	
	@RequestMapping(value = "/getNews", method = RequestMethod.POST)
	public @ResponseBody
	ResponseData getInfo(@RequestParam("event") String event,
			@RequestParam(value = "startLine" ) int startLine,
			@RequestParam(value = "endLine", required = false, defaultValue = "10") int endLine) {
		ResponseData rd = new ResponseData();
		try {
			long startMillis = System.currentTimeMillis();
			Page<News> page = new Page<News>(startLine, endLine);
			selectNewsService.findNews(event, page);
			rd.setErrorCode(ErrorCode.OK.value);
			rd.setValue(page);
			long endMillis = System.currentTimeMillis();
			logger.info("新闻查询,用时:"+(endMillis-startMillis));
		} catch(RuyicaiException e){
			rd.setErrorCode(e.getErrorCode().value);
		} catch(Exception e) {
			logger.error(e.getMessage(), e);
			rd.setErrorCode(ErrorCode.ERROR.value);
		}
		return rd;
	}
	
}
