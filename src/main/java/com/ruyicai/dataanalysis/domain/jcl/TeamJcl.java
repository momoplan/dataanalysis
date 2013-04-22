package com.ruyicai.dataanalysis.domain.jcl;

import javax.persistence.Column;
import javax.persistence.Id;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.roo.addon.entity.RooEntity;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.json.RooJson;
import org.springframework.roo.addon.tostring.RooToString;
import org.springframework.transaction.annotation.Transactional;
import com.ruyicai.dataanalysis.cache.jcl.TeamJclCache;
import flexjson.JSON;

/**
 * 球队信息
 * @author Administrator
 *
 */
@RooJavaBean
@RooToString
@RooJson
@RooEntity(versionField="", table="teamjcl", identifierField="teamId", persistenceUnit="persistenceUnit", transactionManager="transactionManager")
public class TeamJcl {

	@Id
	@Column(name = "teamId")
	private Integer teamId;
	
	@Column(name = "sclassId")
	private Integer sclassId;
	
	@Column(name = "nameJ")
	private String nameJ;
	
	@Autowired
	private transient TeamJclCache teamJclCache;
	
	@JSON(include = false)
	public TeamJclCache getTeamJclCache() {
		return teamJclCache;
	}

	public void setTeamJclCache(TeamJclCache teamJclCache) {
		this.teamJclCache = teamJclCache;
	}

	public static TeamJcl findById(int teamId) {
        return entityManager().find(TeamJcl.class, teamId);
    }
	
	@Transactional
    public void persist() {
        if (this.entityManager == null) this.entityManager = entityManager();
        this.entityManager.persist(this);
        teamJclCache.setToMemcache(this);
    }
	
	@Transactional
    public TeamJcl merge() {
        if (this.entityManager == null) this.entityManager = entityManager();
        TeamJcl merged = this.entityManager.merge(this);
        this.entityManager.flush();
        teamJclCache.setToMemcache(merged);
        return merged;
    }
	
	public static TeamJcl findTeamJcl(int teamId) {
		return new TeamJcl().getTeamJclCache().getTeamJcl(teamId);
	}
	
}
