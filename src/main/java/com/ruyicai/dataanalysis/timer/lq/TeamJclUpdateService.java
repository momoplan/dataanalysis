package com.ruyicai.dataanalysis.timer.lq;

import java.util.List;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.ruyicai.dataanalysis.domain.jcl.TeamJcl;
import com.ruyicai.dataanalysis.util.HttpUtil;
import com.ruyicai.dataanalysis.util.StringUtil;

/**
 * 球队信息更新
 * @author Administrator
 *
 */
@Service
public class TeamJclUpdateService {

	private Logger logger = LoggerFactory.getLogger(TeamJclUpdateService.class);
	
	@Value("${teamJclUrl}")
	private String teamJclUrl;
	
	@Autowired
	private HttpUtil httpUtil;
	
	@SuppressWarnings("unchecked")
	public void process() {
		logger.info("竞彩篮球-球队信息更新开始");
		long startmillis = System.currentTimeMillis();
		try {
			String data = httpUtil.getResponse(teamJclUrl, HttpUtil.GET, HttpUtil.UTF8, "");
			if (StringUtil.isEmpty(data)) {
				logger.info("竞彩篮球-球队信息更新时获取数据为空");
				return;
			}
			Document doc = DocumentHelper.parseText(data);
			List<Element> teams = doc.getRootElement().elements("i");
			for(Element team : teams) {
				doProcess(team);
			}
		} catch (DocumentException e) {
			logger.error("竞彩篮球-球队信息更新异常", e);
		}
		long endmillis = System.currentTimeMillis();
		logger.info("竞彩篮球-球队信息更新结束,共用时 {}", new Long[] {endmillis - startmillis});
	}
	
	/**
	 * 解析数据
	 * @param team
	 */
	private void doProcess(Element team) {
		String teamId = team.elementTextTrim("id"); //球队id
		String lsID = team.elementTextTrim("lsID"); //联赛id
		Integer sclassId = StringUtil.isEmpty(lsID) ? null : Integer.parseInt(lsID);
		String nameJ = team.elementTextTrim("gb"); //简体名称
		TeamJcl teamJcl = TeamJcl.findTeamJcl(Integer.parseInt(teamId));
		if (teamJcl==null) {
			teamJcl = new TeamJcl();
			teamJcl.setTeamId(Integer.parseInt(teamId));
			teamJcl.setSclassId(sclassId);
			teamJcl.setNameJ(nameJ);
			teamJcl.persist();
		} else {
			boolean isModify = false;
			
			Integer sclassId_old = teamJcl.getSclassId();
			if (sclassId!=null && (sclassId_old==null||sclassId!=sclassId_old)) {
				isModify = true;
				teamJcl.setSclassId(sclassId);
			}
			String nameJ_old = teamJcl.getNameJ();
			if (!StringUtil.isEmpty(nameJ) && (StringUtil.isEmpty(nameJ_old)||!nameJ.equals(nameJ_old))) {
				isModify = true;
				teamJcl.setNameJ(nameJ);
			}
			if (isModify) {
				teamJcl.merge();
			}
		}
	}
	
}
