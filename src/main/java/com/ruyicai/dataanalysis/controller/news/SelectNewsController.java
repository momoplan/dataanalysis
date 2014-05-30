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
	
	/**
	 * 查询新闻列表
	 * @param event
	 * @param type
	 * @param startLine
	 * @param endLine
	 * @return
	 */
	@RequestMapping(value = "/getNews", method = RequestMethod.POST)
	public @ResponseBody
	ResponseData getInfo(@RequestParam(value = "event", required = false, defaultValue = "") String event,
			@RequestParam(value = "type", required = false, defaultValue = "") String type,
			@RequestParam(value = "startLine" ) int startLine,
			@RequestParam(value = "endLine", required = false, defaultValue = "10") int endLine) {
		ResponseData rd = new ResponseData();
		ErrorCode result = ErrorCode.OK;
		try {
			long startMillis = System.currentTimeMillis();
			Page<News> page = new Page<News>(startLine, endLine);
			selectNewsService.findNews(event, type, page);
			rd.setValue(page);
			long endMillis = System.currentTimeMillis();
			logger.info("新闻查询列表,用时:"+(endMillis-startMillis));
		} catch(RuyicaiException e){
			result = e.getErrorCode();
		} catch(Exception e) {
			result = ErrorCode.ERROR;
			logger.error("查询新闻列表发生异常", e);
		}
		rd.setErrorCode(result.value);
		return rd;
	}
	
	/**
	 * 查询新闻内容
	 * @param id
	 * @return
	 */
	@RequestMapping(value = "/findNewsById", method = RequestMethod.POST)
	public @ResponseBody
	ResponseData getInfo(@RequestParam(value = "id") String id) {
		ResponseData rd = new ResponseData();
		ErrorCode result = ErrorCode.OK;
		try {
			long startMillis = System.currentTimeMillis();
			News news = selectNewsService.findNewsById(id);
			rd.setValue(news);
			long endMillis = System.currentTimeMillis();
			logger.info("查询新闻内容,用时:"+(endMillis-startMillis));
		} catch(RuyicaiException e){
			result = e.getErrorCode();
		} catch(Exception e) {
			result = ErrorCode.ERROR;
			logger.error("查询新闻内容发生异常", e);
		}
		rd.setErrorCode(result.value);
		return rd;
	}
	
}
