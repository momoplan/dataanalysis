package com.ruyicai.dataanalysis.domain.lq;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Id;
import org.springframework.roo.addon.entity.RooEntity;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.json.RooJson;
import org.springframework.roo.addon.tostring.RooToString;

/**
 * 竞彩赛果表
 * @author Administrator
 *
 */
@RooJavaBean
@RooToString
@RooJson
@RooEntity(versionField="", table="tjingcairesult", identifierField="id", persistenceUnit="newsPersistenceUnit", transactionManager="newsTransactionManager")
public class JingCaiResult {
	
	@Id
	@Column(name = "id")
	private String id;
	
	private String letpoint;
	
	private String basepoint;
	
	private BigDecimal audit;
	
}
