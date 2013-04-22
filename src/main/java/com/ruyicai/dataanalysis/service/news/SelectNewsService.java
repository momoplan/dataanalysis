package com.ruyicai.dataanalysis.service.news;

import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;
import com.ruyicai.dataanalysis.consts.ErrorCode;
import com.ruyicai.dataanalysis.domain.news.News;
import com.ruyicai.dataanalysis.exception.RuyicaiException;
import com.ruyicai.dataanalysis.util.Page;
import com.ruyicai.dataanalysis.util.StringUtil;

@Service
public class SelectNewsService {

	public void findNews(String event, Page<News> page) {
		if (StringUtil.isEmpty(event)) {
			throw new RuyicaiException(ErrorCode.PARAMTER_ERROR);
		}
		StringBuilder builder = new StringBuilder(" where");
		List<Object> params = new ArrayList<Object>();
		
		builder.append(" o.event=? ");
		params.add(event);
		News.findList(builder.toString(), "order by o.publishtime desc", params, page);
	}
	
}
