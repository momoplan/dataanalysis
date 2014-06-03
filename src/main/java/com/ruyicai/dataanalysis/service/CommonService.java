package com.ruyicai.dataanalysis.service;

import java.util.List;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruyicai.dataanalysis.service.back.LotteryService;

@Service
public class CommonService {

	@Autowired
	private LotteryService lotteryService;
	
	/**
	 * 查询竞彩对阵中有哪些日期
	 * @param type
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<String> getActivedays(String type) {
		String result = lotteryService.getjingcaiactivedays(type);
		if (StringUtils.isBlank(result)) {
			return null;
		}
		JSONObject fromObject = JSONObject.fromObject(result);
		if (fromObject==null) {
			return null;
		}
		String errorCode = fromObject.getString("errorCode");
		if (StringUtils.equals(errorCode, "0")) {
			JSONArray valueArray = fromObject.getJSONArray("value");
			if (valueArray!=null&&valueArray.size()>0) {
				return (List<String>)JSONArray.toCollection(valueArray, List.class);
			}
		}
		return null;
	}
	
}
