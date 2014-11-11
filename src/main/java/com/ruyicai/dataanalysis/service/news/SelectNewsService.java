package com.ruyicai.dataanalysis.service.news;

import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruyicai.dataanalysis.consts.ErrorCode;
import com.ruyicai.dataanalysis.dao.NewsDao;
import com.ruyicai.dataanalysis.domain.news.News;
import com.ruyicai.dataanalysis.exception.RuyicaiException;
import com.ruyicai.dataanalysis.util.Page;

@Service
public class SelectNewsService {

	@Autowired
	private NewsDao newsDao;
	
	public void findNews(String event, String type, Page<News> page) {
		StringBuilder builder = new StringBuilder(" where 1=1 ");
		List<Object> params = new ArrayList<Object>();
		if (StringUtils.isNotBlank(event)) {
			builder.append(" and o.event=? ");
			params.add(event);
		}
		if (StringUtils.isNotBlank(type)) {
			builder.append(" and o.type=? ");
			params.add(Integer.parseInt(type));
		}
		newsDao.findListWithPage(builder.toString(), "order by o.publishtime desc", params, page);
	}
	
	public News findNewsById(String id) {
		if (StringUtils.isBlank(id)) {
			throw new RuyicaiException(ErrorCode.NotHaveRecord);
		}
		return News.findNews(Integer.parseInt(id));
	}
	
}
