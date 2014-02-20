package com.ruyicai.dataanalysis.service.news;

import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruyicai.dataanalysis.consts.ErrorCode;
import com.ruyicai.dataanalysis.dao.NewsDao;
import com.ruyicai.dataanalysis.domain.news.News;
import com.ruyicai.dataanalysis.exception.RuyicaiException;
import com.ruyicai.dataanalysis.util.Page;
import com.ruyicai.dataanalysis.util.StringUtil;

@Service
public class SelectNewsService {

	@Autowired
	private NewsDao newsDao;
	
	public void findNews(String event, Page<News> page) {
		if (StringUtil.isEmpty(event)) {
			throw new RuyicaiException(ErrorCode.PARAMTER_ERROR);
		}
		StringBuilder builder = new StringBuilder(" where");
		List<Object> params = new ArrayList<Object>();
		
		builder.append(" o.event=? ");
		params.add(event);
		newsDao.findListWithPage(builder.toString(), "order by o.publishtime desc", params, page);
	}
	
}
