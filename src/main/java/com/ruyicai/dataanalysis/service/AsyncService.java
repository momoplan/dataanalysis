package com.ruyicai.dataanalysis.service;

import java.util.Map;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import com.ruyicai.dataanalysis.cache.CacheService;
import com.ruyicai.dataanalysis.consts.StandardCompany;
import com.ruyicai.dataanalysis.domain.Schedule;
import com.ruyicai.dataanalysis.dto.StandardDto;
import com.ruyicai.dataanalysis.util.StringUtil;

@Service
public class AsyncService {

	private Logger logger = LoggerFactory.getLogger(AsyncService.class);
	
	@Autowired
	private CacheService cacheService;
	
	@Autowired
	private StandardService standardService;
	
	@Async
	public void updateUsualStandardsAvg(Integer scheduleId) {
		try {
			long startMillis = System.currentTimeMillis();
			Schedule schedule = Schedule.findScheduleWOBuild(scheduleId);
			if (StringUtils.isBlank(schedule.getEvent())) {
				return;
			}
			Integer betState = schedule.getBetState();
			if (betState==null||betState!=1) {
				return;
			}
			String key = StringUtil.join("_", "dadaanalysis", "UsualStandards", "avg");
			Map<String, StandardDto> map = cacheService.get(key);
			if (map==null) {
				return;
			}
			StandardDto standardDto = map.get(schedule.getEvent());
			if (standardDto==null) {
				return;
			}
			standardDto = standardService.getAvgStandardDto(schedule);
			map.put(schedule.getEvent(), standardDto);
			cacheService.set(key, map);
			long endMillis = System.currentTimeMillis();
			logger.info("updateUsualStandardsAvg用时:"+(endMillis-startMillis)+",scheduleId="+scheduleId);
		} catch (Exception e) {
			logger.error("updateUsualStandardsAvg发生异常", e);
		}
	}
	
	@Async
	public void deleteUsualStandardsCache(String event) {
		try {
			//删除平均欧赔缓存
			deleteUsualStandardsCacheById(event, "avg");
			//删除公司的平均欧赔缓存
			StandardCompany[] values = StandardCompany.values();
			for (StandardCompany standardCompany : values) {
				deleteUsualStandardsCacheById(event, standardCompany.getCompanyId());
			}
		} catch (Exception e) {
			logger.error("deleteUsualStandardsCache发生异常", e);
		}
	}
	
	private void deleteUsualStandardsCacheById(String event ,String id) {
		try {
			String key = StringUtil.join("_", "dadaanalysis", "UsualStandards", id);
			Map<String, StandardDto> map = cacheService.get(key);
			if (map!=null) {
				map.remove(event);
				cacheService.set(key, map);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/*public static void main(String[] args) {
		Map<String, StandardDto> map = new HashMap<String, StandardDto>();
		map.remove("1");
		System.out.println(map.size());
	}*/
	
	
	/*@Async
	public void saveTuserinfo(ClientInfo clientInfo) {
		try {
			//Thread.sleep(5000);
			String mac = clientInfo.getMac();
			logger.info("saveTuserinfo开始,mac="+mac);
			String softwareVersion = clientInfo.getSoftwareVersion();
			List<Tuserinfo> list = Tuserinfo.getListByImei(mac);
			if (list==null||list.size()<=0) {
				Tuserinfo tuserinfo = new Tuserinfo();
				tuserinfo.setImei(mac);
				tuserinfo.setPlatform(clientInfo.getPlatform());
				tuserinfo.setMachine(clientInfo.getMachineId());
				tuserinfo.setVersion(softwareVersion);
				tuserinfo.setCreatetime(new Date());
				tuserinfo.setLastnettime(new Date());
				tuserinfo.setChannel(CommonUtil.getChannel(clientInfo));
				tuserinfo.persist();
			} else if (list!=null&&list.size()==1) {
				Tuserinfo tuserinfo = list.get(0);
				if (!StringUtils.equals(softwareVersion, tuserinfo.getVersion())) {
					tuserinfo.setVersion(softwareVersion);
				}
				tuserinfo.setLastnettime(new Date());
				tuserinfo.merge();
			}
		} catch (Exception e) {
			logger.error("saveTuserinfo发生异常,mac="+clientInfo.getMac(), e);
		}
	}*/
	
}
