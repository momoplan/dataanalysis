package com.ruyicai.dataanalysis.domain.jcl;

import java.util.Date;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Id;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.roo.addon.entity.RooEntity;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.json.RooJson;
import org.springframework.roo.addon.tostring.RooToString;
import org.springframework.transaction.annotation.Transactional;
import com.ruyicai.dataanalysis.cache.jcl.ScheduleJclCache;
import com.ruyicai.dataanalysis.consts.jcl.MatchStateJcl;
import com.ruyicai.dataanalysis.util.StringUtil;
import flexjson.JSON;
import flexjson.JSONDeserializer;
import flexjson.JSONSerializer;

/**
 * 竞彩篮球赛程赛果
 * @author Administrator
 *
 */
@RooJavaBean
@RooToString
@RooJson
@RooEntity(versionField="", table="schedulejcl", identifierField="scheduleId", persistenceUnit="persistenceUnit", transactionManager="transactionManager")
public class ScheduleJcl {

	@Id
	@Column(name = "scheduleId")
	private Integer scheduleId;
	
	private Integer sclassId;
	
	private String sclassNameJs;
	
	private String sclassType;
	
	private Date matchTime;
	
	private String matchState;
	
	private String remainTime;
	
	private String homeTeamId;
	
	private String homeTeam;
	
	private String guestTeamId;
	
	private String guestTeam;
	
	private String homeScore;
	
	private String guestScore;
	
	private String homeOne;
	
	private String guestOne;
	
	private String homeTwo;
	
	private String guestTwo;
	
	private String homeThree;
	
	private String guestThree;
	
	private String homeFour;
	
	private String guestFour;
	
	private String addTime;
	
	private String homeAddTime1;
	
	private String guestAddTime1;
	
	private String homeAddTime2;
	
	private String guestAddTime2;
	
	private String homeAddTime3;
	
	private String guestAddTime3;
	
	private String homeWinLv;
	
	private String guestWinLv;
	
	private String matchSeason;
	
	private String matchType;
	
	private String event;
	
	private Double avgH;
	
	private Double avgG;
	
	private String letScore;
	
	private String totalScore;
	
	private String turn;
	
	
	@Autowired
	private transient ScheduleJclCache scheduleJclCache;
	
	@JSON(include = false)
	public ScheduleJclCache getScheduleJclCache() {
		return scheduleJclCache;
	}

	public void setScheduleJclCache(ScheduleJclCache scheduleJclCache) {
		this.scheduleJclCache = scheduleJclCache;
	}
	
	@Transactional
    public void persist() {
        if (this.entityManager == null) this.entityManager = entityManager();
        this.entityManager.persist(this);
        scheduleJclCache.setToMemcache(this);
    }
	
	@Transactional
    public ScheduleJcl merge() {
        if (this.entityManager == null) this.entityManager = entityManager();
        ScheduleJcl merged = this.entityManager.merge(this);
        this.entityManager.flush();
        scheduleJclCache.setToMemcache(merged);
        return merged;
    }
	
	public static ScheduleJcl findScheduleJclNotBuild(int scheduleId) {
		return new ScheduleJcl().getScheduleJclCache().getScheduleJcl(scheduleId);
    }
	
	public static ScheduleJcl findScheduleJcl(int scheduleId) {
		ScheduleJcl scheduleJcl = new ScheduleJcl().getScheduleJclCache().getScheduleJcl(scheduleId);
        buildScheduleJcl(scheduleJcl);
        return scheduleJcl;
    }
	
	public static ScheduleJcl findById(int scheduleId) {
		ScheduleJcl scheduleJcl = entityManager().find(ScheduleJcl.class, scheduleId);
		buildScheduleJcl(scheduleJcl);
        return scheduleJcl;
    }
	
	public static ScheduleJcl findByEvent(String event) {
		List<ScheduleJcl> scheduleJcls = entityManager().createQuery("select o from ScheduleJcl o where event=?", ScheduleJcl.class)
				.setParameter(1, event).getResultList();
		if(null == scheduleJcls || scheduleJcls.isEmpty()) {
			return null;
		}
		ScheduleJcl scheduleJcl = scheduleJcls.get(0);
		buildScheduleJcl(scheduleJcl);
        return scheduleJcl;
	}
	
	public static List<ScheduleJcl> findBySclassID(Integer sclassID, String matchSeason) {
		List<ScheduleJcl> scheduleJcls = entityManager().createQuery("select o from ScheduleJcl o where sclassId=? and matchSeason=? and matchType=?", ScheduleJcl.class)
				.setParameter(1, sclassID).setParameter(2, matchSeason).setParameter(3, "常规赛").getResultList();
		if(null != scheduleJcls) {
			for(ScheduleJcl scheduleJcl : scheduleJcls) {
				buildScheduleJcl(scheduleJcl);
			}
		}
		return scheduleJcls;
	}
	
	public static List<ScheduleJcl> findPreSchedules(String teamID, Date time) {
		List<ScheduleJcl> scheduleJcls = entityManager().createQuery("select o from ScheduleJcl o where (homeTeamId=? or guestTeamId=?) and matchTime < ? and matchState=? order by matchTime desc", ScheduleJcl.class)
				.setParameter(1, teamID).setParameter(2, teamID).setParameter(3, time).setParameter(4, MatchStateJcl.wanChang.value).setFirstResult(0).setMaxResults(10).getResultList();
		if(null != scheduleJcls) {
			for(ScheduleJcl scheduleJcl : scheduleJcls) {
				buildScheduleJcl(scheduleJcl);
			}
		}
		return scheduleJcls;
	}
	
	public static List<ScheduleJcl> findAfterSchedules(String teamID, Date time) {
		List<ScheduleJcl> scheduleJcls = entityManager().createQuery("select o from ScheduleJcl o where (homeTeamId=? or guestTeamId=?) and matchTime > ? order by matchTime asc", ScheduleJcl.class)
				.setParameter(1, teamID).setParameter(2, teamID).setParameter(3, time).setFirstResult(0).setMaxResults(5).getResultList();
		if(null != scheduleJcls) {
			for(ScheduleJcl scheduleJcl : scheduleJcls) {
				buildScheduleJcl(scheduleJcl);
			}
		}
		return scheduleJcls;
	}
	
	public static List<ScheduleJcl> findPreClashSchedules(String homeTeamID, String guestTeamID, Date time) {
		List<ScheduleJcl> scheduleJcls = entityManager().createQuery("select o from ScheduleJcl o where ((homeTeamId=? and guestTeamId=?) or (homeTeamId=? and guestTeamId=?)) and matchTime < ? and matchState=? order by matchTime desc", ScheduleJcl.class)
				.setParameter(1, homeTeamID).setParameter(2, guestTeamID).setParameter(3, guestTeamID).setParameter(4, homeTeamID).setParameter(5, time).setParameter(6, MatchStateJcl.wanChang.value).setFirstResult(0).setMaxResults(5).getResultList();
		if(null != scheduleJcls) {
			for(ScheduleJcl scheduleJcl : scheduleJcls) {
				buildScheduleJcl(scheduleJcl);
			}
		}
		return scheduleJcls;
	}
	
	public static List<ScheduleJcl> findByEventAndDay(String day) {
		List<ScheduleJcl> scheduleJcls = entityManager().createQuery("select o from ScheduleJcl o where event is not null and SUBSTR(event,3,8)=? AND SUBSTR(event,1,1)=? order by matchTime asc", ScheduleJcl.class)
				.setParameter(1, day).setParameter(2, "0").getResultList();
		if(null != scheduleJcls) {
			for(ScheduleJcl scheduleJcl : scheduleJcls) {
				buildScheduleJcl(scheduleJcl);
			}
		}
		return scheduleJcls;
	}
	
	public static List<ScheduleJcl> findProcessingMatches() {
		List<ScheduleJcl> scheduleJcls = entityManager().createQuery("select o from ScheduleJcl o where event is not null and matchState in('1','2','50','3','4','5','6','7') AND SUBSTR(event,1,1)=? order by matchTime asc", ScheduleJcl.class)
				.setParameter(1, "0").getResultList();
		if(null != scheduleJcls) {
			for(ScheduleJcl scheduleJcl : scheduleJcls) {
				buildScheduleJcl(scheduleJcl);
			}
		}
		return scheduleJcls;
	}
	
	/**
	 * 设置球队名称
	 * @param scheduleJcl
	 */
	private static void buildScheduleJcl(ScheduleJcl scheduleJcl) {
		if (scheduleJcl!=null) {
			TeamJcl teamJcl = TeamJcl.findTeamJcl(Integer.parseInt(scheduleJcl.getHomeTeamId()));
			if (teamJcl!=null&&!StringUtil.isEmpty(teamJcl.getNameJ())) {
				scheduleJcl.setHomeTeam(teamJcl.getNameJ());
			}
			teamJcl = TeamJcl.findTeamJcl(Integer.parseInt(scheduleJcl.getGuestTeamId()));
			if (teamJcl!=null&&!StringUtil.isEmpty(teamJcl.getNameJ())) {
				scheduleJcl.setGuestTeam(teamJcl.getNameJ());
			}
		}
	}
	
	public String toJson() {
        return new JSONSerializer().exclude("*.class", "scheduleJclCache").serialize(this);
    }
	
	public static ScheduleJcl fromJsonToScheduleJcl(String json) {
        return new JSONDeserializer<ScheduleJcl>().use(null, ScheduleJcl.class).deserialize(json);
    }
	
}
