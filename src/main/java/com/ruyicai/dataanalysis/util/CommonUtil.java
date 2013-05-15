package com.ruyicai.dataanalysis.util;

import org.apache.commons.lang.StringUtils;
import com.ruyicai.dataanalysis.domain.Schedule;

public class CommonUtil {

	/**
	 * 判断足球的event是否为空
	 * @param schedule
	 * @return
	 */
	public static boolean isZqEventEmpty(Schedule schedule) {
		String event = schedule.getEvent();
		String zcSfcEvent = schedule.getZcSfcEvent();
		String zcJqcEvent = schedule.getZcJqcEvent();
		String zcBqcEvent = schedule.getZcBqcEvent();
		String bdEvent = schedule.getBdEvent();
		if(StringUtils.isBlank(event)&&StringUtils.isBlank(zcSfcEvent)&&StringUtils.isBlank(zcJqcEvent)
				&&StringUtils.isBlank(zcBqcEvent)&&StringUtils.isBlank(bdEvent)) {
			return true;
		}
		return false;
	}
	
}
