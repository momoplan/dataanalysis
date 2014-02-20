package com.ruyicai.dataanalysis.listener.lq;

import net.sf.json.JSONObject;
import org.apache.camel.Header;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruyicai.dataanalysis.domain.lq.ScheduleJcl;
import com.ruyicai.dataanalysis.service.back.LotteryService;

/**
 * 竞彩篮球-完场的JMS
 * @author Administrator
 *
 */
@Service
public class ScheduleJclFinishListener {

	private Logger logger = LoggerFactory.getLogger(ScheduleJclFinishListener.class);
	
	@Autowired
	private LotteryService lotteryService;
	
	public void process(@Header("EVENT") String event) {
		try {
			logger.info("竞彩篮球-完场的JMS start, event={}", event);
			if (StringUtils.isBlank(event)||!StringUtils.startsWith(event, "0")) { //不是篮球
				logger.info("竞彩篮球-完场的JMS,event为空或不是篮球,event={}", event);
				return;
			}
			ScheduleJcl scheduleJcl = ScheduleJcl.findByEvent(event, false);
			if (scheduleJcl==null) {
				logger.info("竞彩篮球-完场的JMS,ScheduleJcl为空,event={}", event);
				return;
			}
			logger.info("竞彩篮球-完场的JMS,ScheduleJcl:"+scheduleJcl.toJson()+",event="+event);
			
			String[] events = StringUtils.split(event, "_");
			String result = lotteryService.getJingcaimatches("J00005", events[1], events[2], events[3]);
			if (StringUtils.isBlank(result)) {
				logger.info("竞彩篮球-完场的JMS,lottery返回result为空,event={}", event);
				return;
			}
			JSONObject fromObject = JSONObject.fromObject(result);
			if (fromObject==null) {
				logger.info("竞彩篮球-完场的JMS,fromObject为空,event={}", event);
				return;
			}
			String errorCode = fromObject.getString("errorCode");
			String valueString = fromObject.getString("value");
			if (!StringUtils.equals(errorCode, "0")||StringUtils.equals(valueString, "null")) {
				logger.info("竞彩篮球-完场的JMS,errorCode不是0或valueString为空,event={}", event);
				return;
			}
			JSONObject valueObject = fromObject.getJSONObject("value");
			String resultString = valueObject.getString("result");
			if (StringUtils.equals(resultString, "null")) {
				logger.info("竞彩篮球-完场的JMS,resultString为空,event={}", event);
				return;
			}
			String matchesString = valueObject.getString("matches");
			if (StringUtils.equals(matchesString, "null")) {
				logger.info("竞彩篮球-完场的JMS,matchesString为空,event={}", event);
				return;
			}
			JSONObject matchesObject = valueObject.getJSONObject("matches");
			String audit = matchesObject.getString("audit"); //审核状态(0:已审核)
			if (StringUtils.equals(audit, "null")||!StringUtils.equals(audit, "0")) {
				logger.info("竞彩篮球-完场的JMS,未审核,event={}", event);
				return;
			}
			JSONObject resultObject = valueObject.getJSONObject("result");
			boolean modify = false;
			//让分
			String letScore = scheduleJcl.getLetScore();
			String letpoint = resultObject.getString("letpoint");
			if (StringUtils.isNotBlank(letpoint)&&!StringUtils.equals(letpoint, "null")
					&&!StringUtils.equals(letpoint, letScore)) {
				modify = true;
				scheduleJcl.setLetScore(letpoint);
			}
			//预设总分
			String totalScore = scheduleJcl.getTotalScore();
			String basepoint = resultObject.getString("basepoint");
			if (StringUtils.isNotBlank(basepoint)&&!StringUtils.equals(basepoint, "null")
					&&!StringUtils.equals(basepoint, totalScore)) {
				modify = true;
				scheduleJcl.setTotalScore(basepoint);
			}
			if (modify) {
				logger.info("竞彩篮球-完场的JMS,开始更新让分总分,event={}", event);
				scheduleJcl.merge();
			}
		} catch (Exception e) {
			logger.error("竞彩篮球-完场的JMS发生异常", e);
		}
	}
	
}
