package com.ruyicai.dataanalysis.dao;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import org.springframework.stereotype.Service;
import com.ruyicai.dataanalysis.consts.ErrorCode;
import com.ruyicai.dataanalysis.domain.news.News;
import com.ruyicai.dataanalysis.exception.RuyicaiException;
import com.ruyicai.dataanalysis.util.Page;

@Service
public class NewsDao {

	@PersistenceContext
    EntityManager entityManager;
	
	public void findListWithPage(String where, String orderby, List<Object> params, Page<News> page) {
		TypedQuery<News> q = entityManager.createQuery(
				"SELECT o FROM News o " + where + orderby, News.class);
		if (null != params && !params.isEmpty()) {
			int index = 1;
			for (Object param : params) {
				q.setParameter(index, param);
				index = index + 1;
			}
		}
		q.setFirstResult(page.getPageIndex() * page.getMaxResult())
				.setMaxResults(page.getMaxResult());
		List<News> list = q.getResultList();
		if (list.isEmpty()) {
			throw new RuyicaiException(ErrorCode.NotHaveRecord);
		}
		page.setList(list);
		TypedQuery<Long> totalQ = entityManager.createQuery(
				"select count(o) from News o " + where, Long.class);
		if (null != params && !params.isEmpty()) {
			int index = 1;
			for (Object param : params) {
				totalQ.setParameter(index, param);
				index = index + 1;
			}
		}
		page.setTotalResult(totalQ.getSingleResult().intValue());
	}
	
}
