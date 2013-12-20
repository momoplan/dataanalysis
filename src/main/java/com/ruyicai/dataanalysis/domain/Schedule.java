package com.ruyicai.dataanalysis.domain;

import java.util.ArrayList;
import java.util.Collection;
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

import com.ruyicai.dataanalysis.cache.ScheduleCache;
import com.ruyicai.dataanalysis.consts.MatchState;

import flexjson.JSON;
import flexjson.JSONDeserializer;
import flexjson.JSONSerializer;

/**
 * @author fuqiang
 * 赛程表
 */
@RooJavaBean
@RooToString
@RooJson
@RooEntity(versionField="", table="Schedule", identifierField="scheduleID", persistenceUnit="persistenceUnit", transactionManager="transactionManager")
public class Schedule {
	
	@Id
	@Column(name = "ScheduleID")
	private int scheduleID;
	
	private Integer sclassID;

	private String matchSeason;

	private Integer round;

	private String grouping;

	private Integer homeTeamID;

	private Integer guestTeamID;

	private String homeTeam;

	private String guestTeam;

	private Integer neutrality;
	
	private Date matchTime;
	
	private Date matchTime2;
	
	private String location;
	
	private String home_Order;
	
	private String guest_Order;
	
	private Integer matchState;
	
	private Integer weatherIcon;
	
	private String weather;
	
	private String temperature;
	
	private String tv;

	private String umpire;

	private Integer visitor;

	private Integer homeScore;
	
	private Integer guestScore;

	private Integer homeHalfScore;
	
	private Integer guestHalfScore;

	@Column(name = "`explain`")
	private String explain;
	
	private Integer home_Red;
	
	private Integer guest_Red;

	private Integer home_Yellow;

	private Integer guest_Yellow;

	private Date bf_changetime;

	private Integer sequence;

	private Integer isWFC;

	private Integer isGoalC;

	private Integer europeOddsShow;
	
	private Integer shangpan;

	private Integer oddsSequence;

	private Integer aoShow;

	private Integer bbinShow;

	private Integer isanaly;

	private String grouping2;

	private String explain_en;
	
	private Integer bfShow;

	private Integer subSclassID;

	private Integer nowGoal_IsAnaly;

	private Integer nowScore_IsAnaly;

	private String explainlist;
	
	private String event;
	
	private String zcSfcEvent;
	
	private String zcJqcEvent;
	
	private String zcBqcEvent;
	
	private String bdEvent;
	
	private Double avgH;
	
	private Double avgS;
	
	private Double avgG;
	
	private String turn;
	
	private String zcSfcTurn;
	
	private String zcJqcTurn;
	
	private String zcBqcTurn;
	
	private String bdTurn;
	
	@Autowired
	private transient ScheduleCache scheduleCache;
	
	@JSON(include = false)
	public ScheduleCache getScheduleCache() {
		return scheduleCache;
	}

	public void setScheduleCache(ScheduleCache scheduleCache) {
		this.scheduleCache = scheduleCache;
	}
	
	@Transactional
    public void persist() {
        if (this.entityManager == null) this.entityManager = entityManager();
        this.entityManager.persist(this);
        scheduleCache.setToMemcache(this);
    }
	
	@Transactional
    public Schedule merge() {
        if (this.entityManager == null) this.entityManager = entityManager();
        Schedule merged = this.entityManager.merge(this);
        this.entityManager.flush();
        scheduleCache.setToMemcache(merged);
        return merged;
    }
	
	public static Schedule findScheduleWOBuild(int scheduleID) {
		return new Schedule().getScheduleCache().getSchedule(scheduleID);
    }

	public static Schedule findSchedule(int scheduleID) {
        Schedule schedule = new Schedule().getScheduleCache().getSchedule(scheduleID);
        buildSchedule(schedule);
        return schedule;
    }

	private static void buildSchedule(Schedule schedule) {
		if(null != schedule) {
        	Team team = Team.findTeam(schedule.getHomeTeamID());
        	if(null != team) {
        		schedule.setHomeTeam(team.getName_J());
        	}
        	team = Team.findTeam(schedule.getGuestTeamID());
        	if(null != team) {
        		schedule.setGuestTeam(team.getName_J());
        	}
        }
	}
	
	public static Schedule findById(int scheduleID) {
        Schedule schedule = entityManager().find(Schedule.class, scheduleID);
        buildSchedule(schedule);
        return schedule;
    }
	
	public static Schedule findByEvent(String event) {
		List<Schedule> schedules = entityManager().createQuery("select o from Schedule o where event=?", Schedule.class)
				.setParameter(1, event).getResultList();
		if(null == schedules || schedules.isEmpty()) {
			return null;
		}
		Schedule schedule = schedules.get(0);
		buildSchedule(schedule);
        return schedule;
	}
	
	public static Schedule findByZcSfcEvent(String zcEvent) {
		List<Schedule> schedules = entityManager().createQuery("select o from Schedule o where zcSfcEvent=?", Schedule.class)
				.setParameter(1, zcEvent).getResultList();
		if(null == schedules || schedules.isEmpty()) {
			return null;
		}
		Schedule schedule = schedules.get(0);
		buildSchedule(schedule);
        return schedule;
	}
	
	public static Schedule findByZcJqcEvent(String zcEvent) {
		List<Schedule> schedules = entityManager().createQuery("select o from Schedule o where zcJqcEvent=?", Schedule.class)
				.setParameter(1, zcEvent).getResultList();
		if(null == schedules || schedules.isEmpty()) {
			return null;
		}
		Schedule schedule = schedules.get(0);
		buildSchedule(schedule);
        return schedule;
	}
	
	public static Schedule findByZcBqcEvent(String zcEvent) {
		List<Schedule> schedules = entityManager().createQuery("select o from Schedule o where zcBqcEvent=?", Schedule.class)
				.setParameter(1, zcEvent).getResultList();
		if(null == schedules || schedules.isEmpty()) {
			return null;
		}
		Schedule schedule = schedules.get(0);
		buildSchedule(schedule);
        return schedule;
	}
	
	public static Schedule findByBdEvent(String bdEvent) {
		List<Schedule> schedules = entityManager().createQuery("select o from Schedule o where bdEvent=?", Schedule.class)
				.setParameter(1, bdEvent).getResultList();
		if(null == schedules || schedules.isEmpty()) {
			return null;
		}
		Schedule schedule = schedules.get(0);
		buildSchedule(schedule);
        return schedule;
	}
	
	public static List<Schedule> findPreSchedules(int teamID, Date time) {
		List<Schedule> schedules = entityManager().createQuery("select o from Schedule o where (homeTeamID=? or guestTeamID=?) and matchTime < ? and matchState=? order by matchTime desc", Schedule.class)
				.setParameter(1, teamID).setParameter(2, teamID).setParameter(3, time).setParameter(4, MatchState.WANCHANG.value).setFirstResult(0).setMaxResults(10).getResultList();
		if(null != schedules) {
			for(Schedule schedule : schedules) {
				buildSchedule(schedule);
			}
		}
		return schedules;
	}
	
	public static List<Schedule> findAfterSchedules(int teamID, Date time) {
		List<Schedule> schedules = entityManager().createQuery("select o from Schedule o where (homeTeamID=? or guestTeamID=?) and matchTime > ? order by matchTime asc", Schedule.class)
				.setParameter(1, teamID).setParameter(2, teamID).setParameter(3, time).setFirstResult(0).setMaxResults(5).getResultList();
		if(null != schedules) {
			for(Schedule schedule : schedules) {
				buildSchedule(schedule);
			}
		}
		return schedules;
	}
	
	public static List<Schedule> findPreClashSchedules(int homeTeamID, int guestTeamID, Date time) {
		List<Schedule> schedules = entityManager().createQuery("select o from Schedule o where ((homeTeamID=? and guestTeamID=?) or (homeTeamID=? and guestTeamID=?)) and matchTime < ? and matchState=? order by matchTime desc", Schedule.class)
				.setParameter(1, homeTeamID).setParameter(2, guestTeamID).setParameter(3, guestTeamID).setParameter(4, homeTeamID).setParameter(5, time).setParameter(6, MatchState.WANCHANG.value).setFirstResult(0).setMaxResults(5).getResultList();
		if(null != schedules) {
			for(Schedule schedule : schedules) {
				buildSchedule(schedule);
			}
		}
		return schedules;
	}
	
	public static List<Schedule> findByEventAndDay(String day) {
		List<Schedule> schedules = entityManager().createQuery("select o from Schedule o where event is not null and SUBSTR(event,3,8)=?  AND SUBSTR(event,1,1)=? order by matchTime asc", Schedule.class)
				.setParameter(1, day).setParameter(2, "1").getResultList();
		if(null != schedules) {
			for(Schedule schedule : schedules) {
				buildSchedule(schedule);
			}
		}
		return schedules;
	}
	
	public static List<Schedule> findProcessingMatches() {
		List<Schedule> schedules = entityManager().createQuery("select o from Schedule o where event is not null and matchState in('1', '2', '3', '4') AND SUBSTR(event,1,1)=? order by matchTime asc", Schedule.class)
				.setParameter(1, "1").getResultList();
		if(null != schedules) {
			for(Schedule schedule : schedules) {
				buildSchedule(schedule);
			}
		}
		return schedules;
	}
	
	public static List<Schedule> findByZcSfcEventAndLotNoAndBatchCode(String lotNo, String batchCode) {
		List<Schedule> schedules = entityManager().createQuery("select o from Schedule o where zcSfcEvent is not null and SUBSTR(zcSfcEvent,1,6)=? AND SUBSTR(zcSfcEvent,8,7)=? order by matchTime asc", Schedule.class)
				.setParameter(1, lotNo).setParameter(2, batchCode).getResultList();
		if(null != schedules) {
			for(Schedule schedule : schedules) {
				buildSchedule(schedule);
			}
		}
		return schedules;
	}
	
	public static List<Schedule> findByZcJqcEventAndLotNoAndBatchCode(String lotNo, String batchCode) {
		List<Schedule> schedules = entityManager().createQuery("select o from Schedule o where zcJqcEvent is not null and SUBSTR(zcJqcEvent,1,6)=? AND SUBSTR(zcJqcEvent,8,7)=? order by matchTime asc", Schedule.class)
				.setParameter(1, lotNo).setParameter(2, batchCode).getResultList();
		if(null != schedules) {
			for(Schedule schedule : schedules) {
				buildSchedule(schedule);
			}
		}
		return schedules;
	}
	
	public static List<Schedule> findByZcBqcEventAndLotNoAndBatchCode(String lotNo, String batchCode) {
		List<Schedule> schedules = entityManager().createQuery("select o from Schedule o where zcBqcEvent is not null and SUBSTR(zcBqcEvent,1,6)=? AND SUBSTR(zcBqcEvent,8,7)=? order by matchTime asc", Schedule.class)
				.setParameter(1, lotNo).setParameter(2, batchCode).getResultList();
		if(null != schedules) {
			for(Schedule schedule : schedules) {
				buildSchedule(schedule);
			}
		}
		return schedules;
	}
	
	public static List<Schedule> findByBdEventAndBatchCode(String batchCode) {
		List<Schedule> schedules = entityManager().createQuery("select o from Schedule o where bdEvent is not null and SUBSTR(bdEvent,1,8)=? order by matchTime asc", Schedule.class)
				.setParameter(1, batchCode).getResultList();
		if(null != schedules) {
			for(Schedule schedule : schedules) {
				buildSchedule(schedule);
			}
		}
		return schedules;
	}
	
	public static List<Schedule> findBdProcessingMatches() {
		List<Schedule> schedules = entityManager().createQuery("select o from Schedule o where bdEvent is not null and matchState in('1', '2', '3', '4') order by matchTime asc", Schedule.class)
				.getResultList();
		if(null != schedules) {
			for(Schedule schedule : schedules) {
				buildSchedule(schedule);
			}
		}
		return schedules;
	}
	
	public static List<Schedule> findBySclassID(Integer sclassID, String matchSeason) {
		List<Schedule> schedules = entityManager().createQuery("select o from Schedule o where sclassID=? and matchSeason=?", Schedule.class)
				.setParameter(1, sclassID).setParameter(2, matchSeason).getResultList();
		if(null != schedules) {
			for(Schedule schedule : schedules) {
				buildSchedule(schedule);
			}
		}
		return schedules;
	}
	
	public static List<Schedule> findByDay(Date day) {
		List<Schedule> schedules = entityManager().createQuery("select o from Schedule o where matchTime=?", Schedule.class)
				.setParameter(1, day).getResultList();
		if(null != schedules) {
			for(Schedule schedule : schedules) {
				buildSchedule(schedule);
			}
		}
		return schedules;
	}
	
	public String toJson() {
        return new JSONSerializer().exclude("*.class", "scheduleCache").serialize(this);
    }
	
	public static Schedule fromJsonToSchedule(String json) {
        return new JSONDeserializer<Schedule>().use(null, Schedule.class).deserialize(json);
    }
    
    public static String toJsonArray(Collection<Schedule> collection) {
        return new JSONSerializer().exclude("*.class").serialize(collection);
    }
    
    public static Collection<Schedule> fromJsonArrayToSchedules(String json) {
        return new JSONDeserializer<List<Schedule>>().use(null, ArrayList.class).use("values", Schedule.class).deserialize(json);
    }
}
