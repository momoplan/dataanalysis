package com.ruyicai.dataanalysis.timer.lq;

import java.util.List;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.ruyicai.dataanalysis.domain.jcl.SclassJcl;
import com.ruyicai.dataanalysis.util.HttpUtil;
import com.ruyicai.dataanalysis.util.StringUtil;

/**
 * 竞彩篮球-联赛更新
 * @author Administrator
 *
 */
@Service
public class SclassJclUpdateService {

	private Logger logger = LoggerFactory.getLogger(SclassJclUpdateService.class);
	
	@Value("${leagueJclUrl}")
	private String leagueJclUrl;
	
	@Autowired
	private HttpUtil httpUtil; 
	
	@SuppressWarnings("unchecked")
	public void process() {
		logger.info("竞彩篮球-联赛更新开始");
		long startmillis = System.currentTimeMillis();
		try {
			String data = httpUtil.getResponse(leagueJclUrl, HttpUtil.GET, HttpUtil.UTF8, "");
			if (StringUtil.isEmpty(data)) {
				logger.info("竞彩篮球-联赛更新时获取数据为空");
				return;
			}
			Document doc = DocumentHelper.parseText(data);
			List<Element> matches = doc.getRootElement().elements("match");
			for(Element match : matches) {
				doProcess(match);
			}
		} catch(Exception e) {
			logger.error("竞彩篮球-联赛更新异常", e);
		}
		long endmillis = System.currentTimeMillis();
		logger.info("竞彩篮球-联赛更新结束, 共用时 " + (endmillis - startmillis));
	}
	
	/**
	 * 解析联赛数据
	 * @param match
	 */
	private void doProcess(Element match) {
		try {
			String sclassId = match.elementTextTrim("id"); //联赛ID
			String nameJs = match.elementTextTrim("short"); //简称
			String nameJ = match.elementTextTrim("gb"); //简体全称
			String type = match.elementTextTrim("type"); //比赛分几节
			String currentMatchSeason = match.elementTextTrim("Curr_matchSeason"); //当前赛事
			String sclassKind = match.elementTextTrim("sclass_kind"); //类型(1联赛2杯赛)
			String sclassTime = match.elementTextTrim("sclass_time"); //1节打几分钟
			
			SclassJcl sclassJcl = SclassJcl.findSclassJcl(Integer.parseInt(sclassId));
			if (sclassJcl==null) {
				sclassJcl = new SclassJcl();
				sclassJcl.setSclassId(Integer.parseInt(sclassId));
				sclassJcl.setNameJs(nameJs);
				sclassJcl.setNameJ(nameJ);
				sclassJcl.setType(type);
				sclassJcl.setCurrentMatchSeason(currentMatchSeason);
				sclassJcl.setSclassKind(sclassKind);
				sclassJcl.setSclassTime(sclassTime);
				sclassJcl.persist();
			} else {
				boolean isModify = false;
				
				String nameJs_old = sclassJcl.getNameJs();
				if (!StringUtil.isEmpty(nameJs) && (StringUtil.isEmpty(nameJs_old)||!nameJs.equals(nameJs_old))) {
					isModify = true;
					sclassJcl.setNameJs(nameJs);
				}
				String nameJ_old = sclassJcl.getNameJ();
				if (!StringUtil.isEmpty(nameJ) && (StringUtil.isEmpty(nameJ_old)||!nameJ.equals(nameJ_old))) {
					isModify = true;
					sclassJcl.setNameJ(nameJ);
				}
				String type_old = sclassJcl.getType();
				if (!StringUtil.isEmpty(type) && (StringUtil.isEmpty(type_old)||!type.equals(type_old))) {
					isModify = true;
					sclassJcl.setType(type);
				}
				String currentMatchSeason_old = sclassJcl.getCurrentMatchSeason();
				if (!StringUtil.isEmpty(currentMatchSeason) && (StringUtil.isEmpty(currentMatchSeason_old)||!currentMatchSeason.equals(currentMatchSeason_old))) {
					isModify = true;
					sclassJcl.setCurrentMatchSeason(currentMatchSeason);
				}
				String sclassKind_old = sclassJcl.getSclassKind();
				if (!StringUtil.isEmpty(sclassKind) && (StringUtil.isEmpty(sclassKind_old)||!sclassKind.equals(sclassKind_old))) {
					isModify = true;
					sclassJcl.setSclassKind(sclassKind);
				}
				String sclassTime_old = sclassJcl.getSclassTime();
				if (!StringUtil.isEmpty(sclassKind) && (StringUtil.isEmpty(sclassTime_old)||!sclassKind.equals(sclassTime_old))) {
					isModify = true;
					sclassJcl.setSclassTime(sclassTime);
				}
				if (isModify) {
					sclassJcl.merge();
				}
			}
		} catch(Exception e) {
			logger.error("竞彩篮球-解析联赛数据异常", e);
		}
	}
	
}
