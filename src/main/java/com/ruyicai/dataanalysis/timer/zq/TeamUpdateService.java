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

import com.ruyicai.dataanalysis.domain.Team;
import com.ruyicai.dataanalysis.util.HttpUtil;
import com.ruyicai.dataanalysis.util.StringUtil;

@Service
public class TeamUpdateService {
	
	private Logger logger = LoggerFactory.getLogger(TeamUpdateService.class);

	@Value("${qiuduiziliao}")
	private String url;
	
	@Autowired
	private HttpUtil httpUtil; 
	
	@SuppressWarnings("unchecked")
	public void process() {
		logger.info("开始更新球队资料");
		long startmillis = System.currentTimeMillis();
		try {
			String data = httpUtil.getResponse(url, HttpUtil.GET, HttpUtil.UTF8, "");
			if (StringUtil.isEmpty(data)) {
				logger.info("更新球队资料时获取数据为空");
				return;
			}
			Document doc = DocumentHelper.parseText(data);
			List<Element> teams = doc.getRootElement().elements("i");
			for(Element team : teams) {
				doProcess(team);
			}
		} catch(Exception e) {
			logger.error("更新球队资料出错", e);
		}
		long endmillis = System.currentTimeMillis();
		logger.info("更新球队资料结束，共用时 " + (endmillis - startmillis));
	}

	private void doProcess(Element team) {
		try {
			String id = team.elementTextTrim("id");
			String lsID = team.elementTextTrim("lsID");
			String g = team.elementTextTrim("g");
			String b = team.elementTextTrim("b");
			String e = team.elementTextTrim("e");
			boolean ismod = false;
			Team t = Team.findTeam(Integer.parseInt(id));
			if(null == t) {
				t = new Team();
				t.setTeamID(Integer.parseInt(id));
				t.setSClassID(Integer.parseInt(lsID));
				t.setName_J(g);
				t.setName_F(b);
				t.setName_E(e);
				t.persist();
			} else {
				if(Integer.parseInt(lsID) != t.getSClassID()) {
					ismod = true;
					t.setSClassID(Integer.parseInt(lsID));
				}
				if(!g.equals(t.getName_J())) {
					ismod = true;
					t.setName_J(g);
				}
				if(!b.equals(t.getName_F())) {
					ismod = true;
					t.setName_F(b);
				}
				if(!e.equals(t.getName_E())) {
					ismod = true;
					t.setName_E(e);
				}
				if(ismod) {
					t.merge();
				}
			}
		} catch(Exception e) {
			logger.error(e.getMessage(), e);
		}
	}
}
