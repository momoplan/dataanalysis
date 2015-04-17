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

import com.ruyicai.dataanalysis.domain.lq.TechnicCountJcl;
import com.ruyicai.dataanalysis.domain.lq.TechnicCountJclPK;
import com.ruyicai.dataanalysis.util.HttpUtil;
import com.ruyicai.dataanalysis.util.StringUtil;

/**
 * @Description: 篮球技术统计更新
 * 
 * @author chenchuang   
 * @date 2015年3月9日上午11:17:25
 * @version V1.0   
 *
 */
@Service
public class TechnicCountJclUpdateService {

	private Logger logger = LoggerFactory.getLogger(TechnicCountJclUpdateService.class);
	
	@Value("${technicCountJclUrl}")
	private String technicCountJclUrl;
	
	@Autowired
	private HttpUtil httpUtil;
	
	public void processData(Integer id){
		logger.info("竞彩篮球-技术统计信息更新开始");
		long startmillis = System.currentTimeMillis();
		try {
			String data = httpUtil.getResponse(technicCountJclUrl+"?id="+id, HttpUtil.GET, HttpUtil.UTF8, "");
			if (StringUtil.isEmpty(data)) {
				logger.info("竞彩篮球-技术统计信息更新时获取数据为空");
				return;
			}
			Document doc = DocumentHelper.parseText(data);
			Element matchs = doc.getRootElement();
			processMatchElements(matchs,id);
		} catch (DocumentException e) {
			logger.error("竞彩篮球-技术统计信息更新异常", e);
		}
		long endmillis = System.currentTimeMillis();
		logger.info("竞彩篮球-技术统计信息更新结束,共用时 {}", new Long[] {endmillis - startmillis});
	}
	
	
	/**
	 * 解析球员数据
	 * @param match
	 */
	@SuppressWarnings("unchecked")
	private void processMatchElements(Element match , Integer id) {
		List<Element> homePalyerList = match.element("HomePlayerList").elements("I");
		for(Element player : homePalyerList) {
			processMatchPlayer(player,id,"1");	//1主队
		}
		List<Element> guestPlayerList = match.element("GuestPlayerList").elements("I");
		for(Element player : guestPlayerList) {
			processMatchPlayer(player,id,"2");	//2客队
		}
	}
	
	private void processMatchPlayer(Element players,Integer id,String flag){
		String playerId = players.elementTextTrim("playerID");	//球员id
		String player = players.elementTextTrim("player");	//球员名
		String location = players.elementTextTrim("location");
		String playtime = players.elementTextTrim("playtime");
		String shoot = players.elementTextTrim("shoot");
		String threemin = players.elementTextTrim("threemin");
		String punishball = players.elementTextTrim("punishball");
		String attack = players.elementTextTrim("attack");
		String defend = players.elementTextTrim("defend");
		String helpattack = players.elementTextTrim("helpattack");
		String foul = players.elementTextTrim("foul");
		String rob = players.elementTextTrim("rob");
		String misplay = players.elementTextTrim("misplay");
		String cover = players.elementTextTrim("cover");
		String score = players.elementTextTrim("score");
		
		TechnicCountJclPK pk = new TechnicCountJclPK(id,playerId,flag);
		TechnicCountJcl technicCountJcl = TechnicCountJcl.findTechnicCountJcl(pk);
		if (technicCountJcl == null) {
			technicCountJcl = new TechnicCountJcl();
			technicCountJcl.setId(pk);
			technicCountJcl.setPlayer(player);
			technicCountJcl.setLocation(location);
			technicCountJcl.setPlaytime(playtime);
			technicCountJcl.setShoot(shoot);
			technicCountJcl.setThreemin(threemin);
			technicCountJcl.setPunishball(punishball);
			technicCountJcl.setAttack(attack);
			technicCountJcl.setDefend(defend);
			technicCountJcl.setHelpattack(helpattack);
			technicCountJcl.setFoul(foul);
			technicCountJcl.setRob(rob);
			technicCountJcl.setMisplay(misplay);
			technicCountJcl.setCover(cover);
			technicCountJcl.setScore(score);
			technicCountJcl.persist();
		} else {
			boolean ismod = false;
			String threemin_old = technicCountJcl.getThreemin();
			if (!StringUtil.isEmpty(threemin) && (StringUtil.isEmpty(threemin_old)||!threemin.equals(threemin_old))) {
				ismod = true;
				technicCountJcl.setThreemin(threemin);
			}
			String punishball_old = technicCountJcl.getPunishball();
			if (!StringUtil.isEmpty(punishball) && (StringUtil.isEmpty(punishball_old)||!punishball.equals(punishball_old))) {
				ismod = true;
				technicCountJcl.setPunishball(punishball);
			}
			String attack_old = technicCountJcl.getAttack();
			if(!StringUtil.isEmpty(attack) && (StringUtil.isEmpty(attack_old)||!attack.equals(attack_old))){
				ismod = true;
				technicCountJcl.setAttack(attack);
			}
			String defend_old = technicCountJcl.getDefend();
			if(!StringUtil.isEmpty(defend) && (StringUtil.isEmpty(defend_old)||!defend.equals(defend_old))){
				ismod = true;
				technicCountJcl.setDefend(defend);
			}
			String helpattack_old = technicCountJcl.getHelpattack();
			if(!StringUtil.isEmpty(helpattack) && (StringUtil.isEmpty(helpattack_old)||!helpattack.equals(helpattack_old))){
				ismod = true;
				technicCountJcl.setHelpattack(helpattack);
			}
			String foul_old = technicCountJcl.getFoul();
			if(!StringUtil.isEmpty(foul) && (StringUtil.isEmpty(foul_old)||!foul.equals(foul_old))){
				ismod = true;
				technicCountJcl.setFoul(foul);
			}
			String rob_old = technicCountJcl.getRob();
			if(!StringUtil.isEmpty(rob) && (StringUtil.isEmpty(rob_old)||!rob.equals(rob_old))){
				ismod = true;
				technicCountJcl.setRob(rob);
			}
			String misplay_old = technicCountJcl.getMisplay();
			if(!StringUtil.isEmpty(misplay) && (StringUtil.isEmpty(misplay_old)||!misplay.equals(misplay_old))){
				ismod = true;
				technicCountJcl.setMisplay(misplay);
			}
			String cover_old = technicCountJcl.getCover();
			if(!StringUtil.isEmpty(cover) && (StringUtil.isEmpty(cover_old)||!cover.equals(cover_old))){
				ismod = true;
				technicCountJcl.setCover(cover);
			}
			String score_old = technicCountJcl.getScore();
			if(!StringUtil.isEmpty(score) && (StringUtil.isEmpty(score_old)||!score.equals(score_old))){
				ismod = true;
				technicCountJcl.setScore(score);
			}
			if (ismod) {
				technicCountJcl.merge();
			}
		}
	}
	
}




