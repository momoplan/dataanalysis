package com.ruyicai.dataanalysis.service.lq;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.springframework.stereotype.Service;

import com.ruyicai.dataanalysis.consts.lq.MatchStateJcl;
import com.ruyicai.dataanalysis.domain.lq.GlobalCacheJcl;
import com.ruyicai.dataanalysis.domain.lq.ScheduleJcl;
import com.ruyicai.dataanalysis.domain.lq.SclassJcl;
import com.ruyicai.dataanalysis.dto.lq.RankingJclDTO;
import com.ruyicai.dataanalysis.dto.lq.ScheduleJclDTO;
import com.ruyicai.dataanalysis.util.BeanUtilsEx;
import com.ruyicai.dataanalysis.util.StringUtil;
import com.ruyicai.dataanalysis.util.lq.CalcJclUtil;

/**
 * 竞彩篮球-数据分析
 * @author Administrator
 *
 */
@Service
public class AnalysisJclService {

	//private Logger logger = LoggerFactory.getLogger(AnalysisJclService.class);
	
	/**
	 * 获取联赛排名
	 * @param scheduleID
	 * @param ischeckCache
	 * @return
	 */
	public Collection<RankingJclDTO> getRanking(int scheduleID, boolean ischeckCache) {
		ScheduleJcl scheduleJcl = ScheduleJcl.findScheduleJcl(scheduleID);
		if(null == scheduleJcl) {
			return new ArrayList<RankingJclDTO>();
		}
		return getRanking(ischeckCache, scheduleJcl.getSclassId());
	}
	
	/**
	 * 获取联赛排名
	 * @param ischeckCache
	 * @param sclassID
	 * @return
	 */
	public Collection<RankingJclDTO> getRanking(boolean ischeckCache, int sclassId) {
		SclassJcl sclassJcl = SclassJcl.findSclassJcl(sclassId);
		if(null == sclassJcl) {
			return new ArrayList<RankingJclDTO>();
		}
		if(ischeckCache) {
			String id = StringUtil.join("_", "dataAnalysisJcl", "Ranking", String.valueOf(sclassId));
			GlobalCacheJcl globalCache = GlobalCacheJcl.findGlobalCache(id);
			if(null != globalCache) {
				return RankingJclDTO.fromJsonArrayToRankingJclDTO(globalCache.getValue());
			}
		}
		List<ScheduleJcl> scheduleJcls = ScheduleJcl.findBySclassID(sclassId, sclassJcl.getCurrentMatchSeason());
		Map<Integer, RankingJclDTO> map = new HashMap<Integer, RankingJclDTO>();
		doCalc(scheduleJcls, map);
		return doRanking(sclassId, map);
	}
	
	/**
	 * 联赛排名-排序
	 * @param sclassID
	 * @param map
	 * @return
	 */
	private Collection<RankingJclDTO> doRanking(int sclassID, Map<Integer, RankingJclDTO> map) {
		List<RankingJclDTO> list = new LinkedList<RankingJclDTO>();
		for(Entry<Integer, RankingJclDTO> entry : map.entrySet()) {
			RankingJclDTO dto = entry.getValue();
			dto.setScoreDifference(CalcJclUtil.getScoreDifference(dto.getGainScore(), dto.getLoseScore(), dto.getMatchCount()));
			dto.setGainScore(CalcJclUtil.getFormatScore(dto.getGainScore(), dto.getMatchCount()));
			dto.setLoseScore(CalcJclUtil.getFormatScore(dto.getLoseScore(), dto.getMatchCount()));
			dto.setWinLv(CalcJclUtil.getWinLv(dto.getWinCount(), dto.getMatchCount()));
			list.add(entry.getValue());
		}
		Collections.sort(list);
		for(int i = 1; i <= list.size(); i ++) {
			RankingJclDTO dto = list.get(i - 1);
			dto.setRanking(i);
		}
		String id = StringUtil.join("_", "dataAnalysisJcl", "Ranking", String.valueOf(sclassID));
		GlobalCacheJcl globalCache = GlobalCacheJcl.findGlobalCache(id);
		if(null == globalCache) {
			globalCache = new GlobalCacheJcl();
			globalCache.setId(id);
			globalCache.setValue(RankingJclDTO.toJsonArray(list));
			globalCache.persist();
		} else {
			globalCache.setValue(RankingJclDTO.toJsonArray(list));
			globalCache.merge();
		}
		return list;
	}

	/**
	 * 联赛排名-计算积分
	 * @param scheduleJcls
	 * @param map
	 */
	private void doCalc(List<ScheduleJcl> scheduleJcls, Map<Integer, RankingJclDTO> map) {
		for(ScheduleJcl s : scheduleJcls) {
			String sclassName = s.getSclassNameJs(); //联赛名称
			//主队
			String homeTeamId = s.getHomeTeamId();
			String homeScore = s.getHomeScore();
			homeScore = StringUtil.isEmpty(homeScore)?"0":homeScore;
			String homeTeam = s.getHomeTeam();
			//客队
			String guestTeamId = s.getGuestTeamId();
			String guestScore = s.getGuestScore();
			guestScore = StringUtil.isEmpty(guestScore)?"0":guestScore;
			String guestTeam = s.getGuestTeam();
			//完场并且不是季前赛
			if (MatchStateJcl.wanChang.value.equals(s.getMatchState())&&!sclassName.equals("NBA季前")) {
				RankingJclDTO homeRanking = map.get(Integer.parseInt(homeTeamId));
				if (homeRanking == null) {
					homeRanking = new RankingJclDTO();
					homeRanking.setTeamId(Integer.parseInt(homeTeamId));
					homeRanking.setTeamName(homeTeam);
					map.put(Integer.parseInt(homeTeamId), homeRanking);
				}
				RankingJclDTO guestRanking = map.get(Integer.parseInt(guestTeamId));
				if (guestRanking==null) {
					guestRanking = new RankingJclDTO();
					guestRanking.setTeamId(Integer.parseInt(guestTeamId));
					guestRanking.setTeamName(guestTeam);
					map.put(Integer.parseInt(guestTeamId), guestRanking);
				}
				homeRanking.setMatchCount(homeRanking.getMatchCount() + 1);
				guestRanking.setMatchCount(guestRanking.getMatchCount() + 1);
				if(Integer.parseInt(homeScore) > Integer.parseInt(guestScore)) { //主胜
					homeRanking.setWinCount(homeRanking.getWinCount() + 1); //主队胜次数
					guestRanking.setLoseCount(guestRanking.getLoseCount() + 1); //客队负次数
				}
				if(Integer.parseInt(homeScore) < Integer.parseInt(guestScore)) { //客胜
					homeRanking.setLoseCount(homeRanking.getLoseCount() + 1); //主队负次数
					guestRanking.setWinCount(guestRanking.getWinCount() + 1); //客队胜次数
				}
				homeRanking.setGainScore(homeRanking.getGainScore()+Integer.parseInt(homeScore)); //主队得分
				homeRanking.setLoseScore(homeRanking.getLoseScore()+Integer.parseInt(guestScore)); //主队失分
				guestRanking.setGainScore(guestRanking.getGainScore()+Integer.parseInt(guestScore)); //客队得分
				guestRanking.setLoseScore(guestRanking.getLoseScore()+Integer.parseInt(homeScore)); //客队失分
			}
		}
	}
	
	/**
	 * 主队近期战绩
	 * @param scheduleID
	 * @return
	 */
	public Collection<ScheduleJclDTO> getPreHomeSchedules(int scheduleID) {
		ScheduleJcl scheduleJcl = ScheduleJcl.findScheduleJcl(scheduleID);
		if(null == scheduleJcl) {
			return new ArrayList<ScheduleJclDTO>();
		}
		List<ScheduleJcl> schedules = ScheduleJcl.findPreSchedules(scheduleJcl.getHomeTeamId(), scheduleJcl.getMatchTime());
		return buildDTOS(schedules);
	}
	
	/**
	 * 客队近期战绩
	 * @param scheduleID
	 * @return
	 */
	public Collection<ScheduleJclDTO> getPreGuestSchedules(int scheduleID) {
		ScheduleJcl schedule = ScheduleJcl.findScheduleJcl(scheduleID);
		if(null == schedule) {
			return new ArrayList<ScheduleJclDTO>();
		}
		List<ScheduleJcl> schedules = ScheduleJcl.findPreSchedules(schedule.getGuestTeamId(), schedule.getMatchTime());
		return buildDTOS(schedules);
	}
	
	/**
	 * 主队未来赛事
	 * @param scheduleID
	 * @return
	 */
	public Collection<ScheduleJclDTO> getAfterHomeSchedules(int scheduleID) {
		ScheduleJcl schedule = ScheduleJcl.findScheduleJcl(scheduleID);
		if(null == schedule) {
			return new ArrayList<ScheduleJclDTO>();
		}
		List<ScheduleJcl> schedules = ScheduleJcl.findAfterSchedules(schedule.getHomeTeamId(), schedule.getMatchTime());
		return buildDTOS(schedules);
	}
	
	/**
	 * 客队未来赛事
	 * @param scheduleID
	 * @return
	 */
	public Collection<ScheduleJclDTO> getAfterGuestSchedules(int scheduleID) {
		ScheduleJcl schedule = ScheduleJcl.findScheduleJcl(scheduleID);
		if(null == schedule) {
			return new ArrayList<ScheduleJclDTO>();
		}
		List<ScheduleJcl> schedules = ScheduleJcl.findAfterSchedules(schedule.getGuestTeamId(), schedule.getMatchTime());
		return buildDTOS(schedules);
	}
	
	/**
	 * 历史交锋
	 * @param scheduleID
	 * @return
	 */
	public Collection<ScheduleJclDTO> getPreClashSchedules(int scheduleID) {
		ScheduleJcl schedule = ScheduleJcl.findScheduleJcl(scheduleID);
		if(null == schedule) {
			return new ArrayList<ScheduleJclDTO>();
		}
		List<ScheduleJcl> schedules = ScheduleJcl.findPreClashSchedules(schedule.getHomeTeamId(), schedule.getGuestTeamId(), schedule.getMatchTime());
		return buildDTOS(schedules);
	}
	
	/**
	 * 设置ScheduleJclDTO列表的属性
	 * @param schedules
	 * @return
	 */
	public List<ScheduleJclDTO> buildDTOS(List<ScheduleJcl> schedules) {
		List<ScheduleJclDTO> dtos = new ArrayList<ScheduleJclDTO>();
		for(ScheduleJcl s : schedules) {
			ScheduleJclDTO dto = buildDTO(s);
			dtos.add(dto);
		}
		return dtos;
	}
	
	/**
	 * 设置ScheduleJclDTO的属性
	 * @param scheduleJcl
	 * @return
	 */
	public ScheduleJclDTO buildDTO(ScheduleJcl scheduleJcl) {
		ScheduleJclDTO dto = new ScheduleJclDTO();
		try {
			SclassJcl sclass = SclassJcl.findSclassJcl(scheduleJcl.getSclassId());
			BeanUtilsEx.copyProperties(dto, scheduleJcl);
			dto.setSclassName(sclass.getNameJ());
			dto.setSclassShortName(sclass.getNameJs());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return dto;
	}
	
	/**
	 * 更新所有联赛的排名
	 */
	public void updateAllRanking() {
		List<SclassJcl> sclassJcls = SclassJcl.findAllSclassJcls();
		for(SclassJcl sclassJcl : sclassJcls) {
			getRanking(false, sclassJcl.getSclassId());
		}
	}
	
}
