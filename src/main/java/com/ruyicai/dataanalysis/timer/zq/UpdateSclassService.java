package com.ruyicai.dataanalysis.timer.zq;

import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.ruyicai.dataanalysis.domain.Sclass;
import com.ruyicai.dataanalysis.domain.SclassInfo;
import com.ruyicai.dataanalysis.util.HttpUtil;
import com.ruyicai.dataanalysis.util.NumberUtil;
import com.ruyicai.dataanalysis.util.StringUtil;

@Service
public class UpdateSclassService {
	
	private Logger logger = LoggerFactory.getLogger(UpdateSclassService.class);

	@Value("${liansaibeisai}")
	private String url;
	
	@Autowired
	private HttpUtil httpUtil; 
	
	@SuppressWarnings("unchecked")
	public void process() {
		logger.info("开始更新联赛杯赛");
		long startmillis = System.currentTimeMillis();
		try {
			String data = httpUtil.getResponse(url, HttpUtil.GET, HttpUtil.UTF8, "");
			if (StringUtil.isEmpty(data)) {
				logger.info("更新联赛杯赛时获取数据为空");
				return;
			}
			Document doc = DocumentHelper.parseText(data);
			List<Element> matches = doc.getRootElement().elements("match");
			for(Element match : matches) {
				doProcess(match);
			}
		} catch(Exception e) {
			logger.error("更新联赛杯赛出错", e);
		}
		long endmillis = System.currentTimeMillis();
		logger.info("更新联赛杯赛结束, 共用时 " + (endmillis - startmillis));
	}

	private void doProcess(Element match) {
		try {
			String id = match.elementTextTrim("id");
			String gb_short = match.elementTextTrim("gb_short");
			String big_short = match.elementTextTrim("big_short");
			String en_short = match.elementTextTrim("en_short");
			String gb = match.elementTextTrim("gb");
			String big = match.elementTextTrim("big");
			String en = match.elementTextTrim("en");
			String type = match.elementTextTrim("type");
			String sum_round = match.elementTextTrim("sum_round");
			String curr_round = match.elementTextTrim("curr_round");
			String curr_matchSeason = match.elementTextTrim("Curr_matchSeason");
			String country = match.elementTextTrim("country");
			String areaID = match.elementTextTrim("areaID");
			Sclass sclass = Sclass.findSclass(Integer.parseInt(id));
			boolean ismod = false;
			if(null == sclass) {
				SclassInfo info = new SclassInfo();
				info.setNameCN(country);
				info.setInfo_type(Integer.parseInt(areaID));
				info.persist();
				sclass = new Sclass();
				sclass.setSclassID(Integer.parseInt(id));
				sclass.setName_JS(gb_short);
				sclass.setName_FS(big_short);
				sclass.setName_ES(en_short);
				sclass.setName_J(gb);
				sclass.setName_F(big);
				sclass.setName_E(en);
				sclass.setKind(Integer.parseInt(type));
				sclass.setCount_round(NumberUtil.parseInt(sum_round, 0));
				sclass.setCurr_round(NumberUtil.parseInt(curr_round, 0));
				sclass.setCurr_matchSeason(curr_matchSeason);
				sclass.setInfoID(info.getInfoID());
				sclass.persist();
			} else {
				if(NumberUtil.parseInt(curr_round, 0) != sclass.getCurr_round()) {
					ismod = true;
					sclass.setCurr_round(NumberUtil.parseInt(curr_round, 0));
				}
				if(!curr_matchSeason.equals(sclass.getCurr_matchSeason())) {
					ismod = true;
					sclass.setCurr_matchSeason(curr_matchSeason);
				}
				if(ismod) {
					sclass.merge();
				}
			}
		} catch(Exception e) {
			logger.error(e.getMessage(), e);
		}
	}
}
