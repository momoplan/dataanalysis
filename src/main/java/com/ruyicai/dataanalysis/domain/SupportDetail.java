package com.ruyicai.dataanalysis.domain;

import java.util.Date;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.TypedQuery;
import org.springframework.roo.addon.entity.RooEntity;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.json.RooJson;
import org.springframework.roo.addon.tostring.RooToString;

/**
 * 球队支持明细
 * @author Administrator
 *
 */
@RooJavaBean
@RooToString
@RooJson
@RooEntity(versionField="", table="supportdetail", identifierField="id", persistenceUnit="persistenceUnit", transactionManager="transactionManager")
public class SupportDetail {

	@Id
	@Column(name = "id")
	private int id;
	
	private String userno;
	
	private Integer teamid;
	
	private Date createtime;

	public static List<SupportDetail> findByUsernoTeamid(String userno, String teamid) {
		TypedQuery<SupportDetail> q = entityManager().createQuery(
				"select o from SupportDetail o where o.userno=? and o.teamid=?", SupportDetail.class);
		q.setParameter(1, userno).setParameter(2, Integer.parseInt(teamid));
		return q.getResultList();
	}
	
}
