package com.ruyicai.dataanalysis.domain.lq;

import javax.persistence.Column;

import org.springframework.roo.addon.entity.RooIdentifier;
import org.springframework.roo.addon.tostring.RooToString;

/**
 * @Description: 篮球技术统计主键数据
 * 
 * @author chenchuang   
 * @date 2015年3月17日上午11:26:01
 * @version V1.0   
 *
 */
@RooIdentifier
@RooToString
public class TechnicCountJclPK {

	private static final long serialVersionUID = 1L;

	@Column(name = "scheduleId")
	private Integer scheduleId;

	@Column(name = "playerId")
	private String playerId;
	
	/**1-主队；2-客队*/
	@Column(name = "flag")
	private String flag;
}




