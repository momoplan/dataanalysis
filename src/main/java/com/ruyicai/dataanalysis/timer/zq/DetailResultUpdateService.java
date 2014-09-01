package com.ruyicai.dataanalysis.timer.zq;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.ruyicai.dataanalysis.domain.DetailResult;
import com.ruyicai.dataanalysis.domain.GlobalCache;
import com.ruyicai.dataanalysis.domain.Schedule;
import com.ruyicai.dataanalysis.util.CommonUtil;
import com.ruyicai.dataanalysis.util.DateUtil;
import com.ruyicai.dataanalysis.util.HttpUtil;
import com.ruyicai.dataanalysis.util.NumberUtil;
import com.ruyicai.dataanalysis.util.StringUtil;

@Service
public class DetailResultUpdateService {
	
	private Logger logger = LoggerFactory.getLogger(DetailResultUpdateService.class);

	@Value("${detailresult}")
	private String url;
	
	@Value("${detailResultDate}")
	private String urlDate;
	
	@Autowired
	private HttpUtil httpUtil; 

	public void process() {
		logger.info("开始更新当天比赛的入球、红黄牌事件");
		long startmillis = System.currentTimeMillis();
		try {
//			String data = httpUtil.downfile(url, HttpUtil.GBK);
			String data = httpUtil.getResponse(url, HttpUtil.GET, HttpUtil.UTF8, "");
			if (StringUtil.isEmpty(data)) {
				logger.info("更新当天比赛的入球、红黄牌事件时获取数据为空");
				return;
			}
			Map<Integer, List<Integer>> map = new HashMap<Integer, List<Integer>>();
			String[] datas = data.split("\\;");
			for(int i = 0 ; i < datas.length; i++) {
				String value = datas[i];
				value = value.replaceFirst("^\\s*", "");
				if(value.startsWith("rq")) {
					doProcess(value, map);
				}
			}
			filterDetailResults(map);
		} catch(Exception e) {
			logger.error("更新当天比赛的入球、红黄牌事件出错", e);
		}
		long endmillis = System.currentTimeMillis();
		logger.info("更新当天比赛的入球、红黄牌事件结束, 共用时 " + (endmillis - startmillis));
	}

	private void doProcess(String data, Map<Integer, List<Integer>> map) {
		try {
			data = data.replaceFirst("rq\\[\\d+\\]=", "");
			data = data.replaceAll("\"", "");
			String[] values = data.split("\\^");
			String scheduleID = values[0];
			String zk = values[1];
			String kind = values[2];
			String happenTime = values[3];
			String playername = values.length >= 5 ? values[4] : null;
			String playerID = values.length >= 6 ? values[5] : null;
			String playername_j = values.length >= 7 ? values[6] : null;
			Schedule schedule = Schedule.findSchedule(Integer.parseInt(scheduleID), true);
			if(schedule==null) {
				return;
			}
			if (CommonUtil.isZqEventEmpty(schedule)) {
				return ;
			}
			List<Integer> list = map.get(Integer.parseInt(scheduleID));
			if (list==null) {
				list = new ArrayList<Integer>();
			}
			//一个球队同一时间有可能有两个进球(如:829250赛事)
			DetailResult result = DetailResult.findDetailResult(Integer.parseInt(scheduleID), Integer.parseInt(happenTime), 
					Integer.parseInt(kind), Integer.parseInt(zk), NumberUtil.parseInt(playerID, 0));
			if(null == result) {
				result = new DetailResult();
				result.setScheduleID(Integer.parseInt(scheduleID));
				result.setTeamID(Integer.parseInt(zk));
				result.setModifyTime(new Date());
				result.setHappenTime(Integer.parseInt(happenTime));
				result.setKind(Integer.parseInt(kind));
				result.setPlayerID(NumberUtil.parseInt(playerID, 0));
				result.setPlayername(playername);
				result.setPlayername_j(playername_j);
				result.setDeleteState(1); //1:正常;0:已删
				result.persist();
				updateCache(result.getScheduleID());
			}
			list.add(result.getId());
			map.put(Integer.parseInt(scheduleID), list);
		} catch(Exception e) {
			logger.error(e.getMessage(), e);
		}
	}

	private void updateCache(int scheduleID) {
		String id = StringUtil.join("_", "dataanalysis", "DetailResult", String.valueOf(scheduleID));
		GlobalCache globalCache = GlobalCache.findGlobalCache(id);
		List<DetailResult> detailResults = DetailResult.findDetailResults(scheduleID);
		if(null == globalCache) {
			globalCache = new GlobalCache();
			globalCache.setId(id);
			globalCache.setValue(DetailResult.toJsonArray(detailResults));
			globalCache.persist();
		} else {
			globalCache.setValue(DetailResult.toJsonArray(detailResults));
			globalCache.merge();
		}
	}
	
	/**
	 * 过滤多余的detailResult
	 * 防止球探数据出错，以最新数据为准
	 * @param map
	 */
	private void filterDetailResults(Map<Integer, List<Integer>> map) {
		if (map!=null) {
			for(Entry<Integer, List<Integer>> entry : map.entrySet()) {
				boolean isUpdateCache = false;
				Integer scheduleId = entry.getKey();
				List<Integer> list = entry.getValue();
				List<DetailResult> detailResults = DetailResult.findDetailResults(scheduleId);
				for (DetailResult detailResult : detailResults) {
					Integer id = detailResult.getId();
					if (!list.contains(id)) { //detailResult需删除
						isUpdateCache = true;
						//logger.info("删除detailResult:"+detailResult.toString());
						detailResult.setDeleteState(0);
						detailResult.merge();
					}
				}
				if (isUpdateCache) {
					updateCache(scheduleId);
				}
			}
		}
	}
	
	public void processByDate(String date) {
		logger.info("开始更新"+date+"比赛的入球、红黄牌事件");
		long startmillis = System.currentTimeMillis();
		try {
//			String data = httpUtil.downfile(urlDate+"?date="+date, HttpUtil.UTF8);
			String data = httpUtil.getResponse(urlDate+"?date="+date, HttpUtil.GET, HttpUtil.UTF8, "");
			if (StringUtil.isEmpty(data)) {
				logger.info("更新"+date+"比赛的入球、红黄牌事件时获取数据为空");
				return;
			}
			Map<Integer, List<Integer>> map = new HashMap<Integer, List<Integer>>();
			String[] datas = data.split("\\;");
			for(int i = 0 ; i < datas.length; i++) {
				String value = datas[i];
				value = value.replaceFirst("^\\s*", "");
				if(value.startsWith("rq")) {
					doProcess(value, map);
				}
			}
			filterDetailResults(map);
		} catch(Exception e) {
			logger.error("更新"+date+"比赛的入球、红黄牌事件出错", e);
		}
		long endmillis = System.currentTimeMillis();
		logger.info("更新"+date+"比赛的入球、红黄牌事件结束, 共用时 " + (endmillis - startmillis));
	}
	
	public void processPreDay() {
		Date preDate = DateUtil.getPreDate(1);
		String preDateStr = DateUtil.format("yyyy-MM-dd", preDate);
		processByDate(preDateStr);
	}
	
	/*public static void main(String[] args) {
		new UpdateDetailResultService().processPreDay();
	}*/
	
}
