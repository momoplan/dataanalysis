package com.ruyicai.dataanalysis.domain.news;

import java.util.Date;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.TypedQuery;
import org.springframework.roo.addon.entity.RooEntity;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.json.RooJson;
import org.springframework.roo.addon.tostring.RooToString;

/**
 * 新闻
 * @author Administrator
 *
 */
@RooJavaBean
@RooToString
@RooJson
@RooEntity(versionField="", table="news", identifierField="id", persistenceUnit="persistenceUnit", transactionManager="transactionManager")
public class News {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name="id")
	private Integer id;
	
	@Column(name = "title")
	private String title;
	
	@Column(name = "content")
	private String content;
	
	@Column(name = "publishtime")
	private Date publishtime;
	
	@Column(name = "outid")
	private String outid;
	
	@Column(name = "event")
	private String event;
	
	@Column(name = "url")
	private String url;
	
	/*public static void findList(String where, String orderby, List<Object> params, Page<News> page) {
		try {
			TypedQuery<News> q = entityManager().createQuery(
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
			page.setList(q.getResultList());
			TypedQuery<Long> totalQ = entityManager().createQuery(
					"select count(o) from News o " + where, Long.class);
			if (null != params && !params.isEmpty()) {
				int index = 1;
				for (Object param : params) {
					totalQ.setParameter(index, param);
					index = index + 1;
				}
			}
			page.setTotalResult(totalQ.getSingleResult().intValue());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}*/
	
	public static List<News> getList(String where, String orderby, List<Object> params) {
		TypedQuery<News> q = entityManager().createQuery(
				"SELECT o FROM News o " + where + orderby, News.class);
		if (null != params && !params.isEmpty()) {
			int index = 1;
			for (Object param : params) {
				q.setParameter(index, param);
				index = index + 1;
			}
		}
		return q.getResultList();
	}
	
}
