package com.ruyicai.dataanalysis.service.news;

import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruyicai.dataanalysis.dao.NewsDao;
import com.ruyicai.dataanalysis.domain.news.News;
import com.ruyicai.dataanalysis.util.Page;

@Service
public class SelectNewsService {

	@Autowired
	private NewsDao newsDao;
	
	public void findNews(String event, Page<News> page) {
		/*if (StringUtil.isEmpty(event)) {
			throw new RuyicaiException(ErrorCode.PARAMTER_ERROR);
		}*/
		StringBuilder builder = new StringBuilder(" where");
		List<Object> params = new ArrayList<Object>();
		if (StringUtils.isNotBlank(event)) {
			builder.append(" o.event=? ");
			params.add(event);
		}
		if (builder.toString().endsWith("where")) {
			builder.delete(builder.length() - 5, builder.length());
		}
		newsDao.findListWithPage(builder.toString(), "order by o.publishtime desc", params, page);
	}
	
}
