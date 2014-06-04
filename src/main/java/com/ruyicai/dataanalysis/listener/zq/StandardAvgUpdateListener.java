package com.ruyicai.dataanalysis.listener.zq;

import java.math.BigDecimal;
import java.util.List;
import org.apache.camel.Body;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruyicai.dataanalysis.domain.Schedule;
import com.ruyicai.dataanalysis.domain.Standard;
import com.ruyicai.dataanalysis.service.AsyncService;
import com.ruyicai.dataanalysis.util.ArithUtil;
import com.ruyicai.dataanalysis.util.CommonUtil;
import com.ruyicai.dataanalysis.util.NumberUtil;

/**
 * 足球平均欧赔更新的Jms
 * @author Administrator
 *
 */
@Service
public class StandardAvgUpdateListener {

	private Logger logger = LoggerFactory.getLogger(StandardAvgUpdateListener.class);
	
	@Autowired
	private AsyncService asyncService;
	
	public void process(@Body String scheduleId) {
		try {
			//long startmillis = System.currentTimeMillis();
			//logger.info("足球平均欧赔更新的Jms start scheduleId="+scheduleId);
			if (StringUtils.isBlank(scheduleId)) {
				return;
			}
			Schedule schedule = Schedule.findSchedule(Integer.parseInt(scheduleId), true);
			if (schedule==null) {
				return;
			}
			if (CommonUtil.isZqEventEmpty(schedule)) {
				return;
			}
			List<Standard> list = Standard.getListByScheduleId(Integer.parseInt(scheduleId));
			//更新平均欧赔
			doScheduleAvg(schedule, list);
			//更新正在进行中比赛的平均欧赔
			updateUsualStandardsAvg(schedule);
			//long endmillis = System.currentTimeMillis();
			//logger.info("足球平均欧赔更新的Jms,用时:"+(endmillis-startmillis)+",scheduleId="+scheduleId);
		} catch (Exception e) {
			logger.error("足球平均欧赔更新的Jms发生异常", e);
		}
	}
	
	private void doScheduleAvg(Schedule schedule, List<Standard> list) {
		try {
			if (list==null || list.size()<=0) {
				return ;
			}
			Double t_h = 0D;
			Double t_s = 0D;
			Double t_g = 0D;
			for (Standard standard : list) {
				Double firstHomeWin = standard.getFirstHomeWin(); //初盘主胜
				Double firstStandoff = standard.getFirstStandoff(); //初盘和局
				Double firstGuestWin = standard.getFirstGuestWin(); //初盘客胜
				Double homeWin = standard.getHomeWin(); //主胜
				Double standoff = standard.getStandoff(); //和局
				Double guestWin = standard.getGuestWin(); //客胜
				if(homeWin!=null && standoff!=null && guestWin!=null) {
					t_h = ArithUtil.add(t_h, homeWin);
					t_s = ArithUtil.add(t_s, standoff);
					t_g = ArithUtil.add(t_g, guestWin);
				} else {
					t_h = ArithUtil.add(t_h, firstHomeWin);
					t_s = ArithUtil.add(t_s, firstStandoff);
					t_g = ArithUtil.add(t_g, firstGuestWin);
				}
			}
			updateScheduleAvg(schedule, t_h, t_s, t_g, list.size());
		} catch (Exception e) {
			logger.error("足球平均欧赔更新的Jms-doScheduleAvg发生异常", e);
		}
	}
	
	private void updateScheduleAvg(Schedule schedule, Double t_h, Double t_s, Double t_g, int oddsSize) {
		if(schedule!=null && oddsSize>0) {
			boolean isModify = false;
			BigDecimal b = new BigDecimal(t_h / oddsSize);
			b = b.setScale(2, BigDecimal.ROUND_HALF_UP);
			Double avgH = schedule.getAvgH();
			if (!NumberUtil.compare(b.toString(), avgH)) {
				isModify = true;
				schedule.setAvgH(b.doubleValue());
			}
			b = new BigDecimal(t_s / oddsSize);
			b = b.setScale(2, BigDecimal.ROUND_HALF_UP);
			Double avgS = schedule.getAvgS();
			if (!NumberUtil.compare(b.toString(), avgS)) {
				isModify = true;
				schedule.setAvgS(b.doubleValue());
			}
			b = new BigDecimal(t_g / oddsSize);
			b = b.setScale(2, BigDecimal.ROUND_HALF_UP);
			Double avgG = schedule.getAvgG();
			if (!NumberUtil.compare(b.toString(), avgG)) {
				isModify = true;
				schedule.setAvgG(b.doubleValue());
			}
			if (isModify) {
				schedule.merge();
			}
		}
	}
	
	private void updateUsualStandardsAvg(Schedule schedule) {
		try {
			if (StringUtils.isBlank(schedule.getEvent())) {
				return;
			}
			asyncService.updateUsualStandardsAvg(schedule.getScheduleID());
		} catch (Exception e) {
			logger.error("足球平均欧赔更新的Jms-updateUsualStandardsAvg发生异常", e);
		}
	}
	
}
