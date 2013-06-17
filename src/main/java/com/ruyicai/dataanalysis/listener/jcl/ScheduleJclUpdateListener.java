package com.ruyicai.dataanalysis.listener.jcl;

import java.math.BigDecimal;
import org.apache.camel.Header;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import com.ruyicai.dataanalysis.domain.jcl.JingCaiResult;
import com.ruyicai.dataanalysis.domain.jcl.ScheduleJcl;

/**
 * 竞彩篮球-更新赛事让分、总分盘的JMS
 * @author Administrator
 *
 */
@Service
public class ScheduleJclUpdateListener {

	private Logger logger = LoggerFactory.getLogger(ScheduleJclUpdateListener.class);
	
	public void update(@Header("EVENT") String event) {
		logger.info("竞彩篮球-更新赛事让分、总分盘的JMS start, event="+event);
		try {
			if (StringUtils.isBlank(event)||!StringUtils.startsWith(event, "0")) { //不是篮球
				return ;
			}
			ScheduleJcl scheduleJcl = ScheduleJcl.findByEvent(event);
			if (scheduleJcl==null) {
				return ;
			}
			JingCaiResult jingCaiResult = JingCaiResult.findJingCaiResult(event);
			if (jingCaiResult==null) {
				return ;
			}
			BigDecimal audit = jingCaiResult.getAudit();
			if (audit==null||audit.intValue()!=0) { //未审核(0:已审核)
				return ;
			}
			boolean isMerge = false;
			String letpoint = jingCaiResult.getLetpoint(); //让分
			if (StringUtils.isNotBlank(letpoint)) { //让分
				isMerge = true;
				scheduleJcl.setLetScore(letpoint);
			}
			String basePoint = jingCaiResult.getBasepoint(); //总分盘
			if (StringUtils.isNotBlank(basePoint)) { //总分盘
				isMerge = true;
				scheduleJcl.setTotalScore(basePoint);
			}
			if (isMerge) {
				scheduleJcl.merge();
			}
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		logger.info("竞彩篮球-更新赛事让分、总分盘的JMS end, event="+event);
	}
	
}
