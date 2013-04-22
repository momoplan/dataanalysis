package com.ruyicai.dataanalysis.domain;

import javax.persistence.Column;
import javax.persistence.Id;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.roo.addon.entity.RooEntity;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.json.RooJson;
import org.springframework.roo.addon.tostring.RooToString;
import org.springframework.transaction.annotation.Transactional;

import com.ruyicai.dataanalysis.cache.TeamCache;

import flexjson.JSON;

/**
 * @author fuqiang
 * 球队资料表
 */
@RooJavaBean
@RooToString
@RooJson
@RooEntity(versionField="", table="Team", identifierField="teamID", persistenceUnit="persistenceUnit", transactionManager="transactionManager")
public class Team {

	@Id
	@Column(name = "TeamID")
	private int teamID;

	private Integer sClassID;

	private String name_J;

	private String name_F;

	private String name_E;
	
	@Autowired
	private transient TeamCache teamCache;
	
	@JSON(include = false)
	public TeamCache getTeamCache() {
		return teamCache;
	}

	public void setTeamCache(TeamCache teamCache) {
		this.teamCache = teamCache;
	}

	public static Team findById(int teamID) {
        return entityManager().find(Team.class, teamID);
    }
	
	@Transactional
    public void persist() {
        if (this.entityManager == null) this.entityManager = entityManager();
        this.entityManager.persist(this);
        teamCache.setToMemcache(this);
    }
	
	@Transactional
    public Team merge() {
        if (this.entityManager == null) this.entityManager = entityManager();
        Team merged = this.entityManager.merge(this);
        this.entityManager.flush();
        teamCache.setToMemcache(merged);
        return merged;
    }
	
	public static Team findTeam(int teamID) {
		return new Team().getTeamCache().getTeam(teamID);
	}
}
