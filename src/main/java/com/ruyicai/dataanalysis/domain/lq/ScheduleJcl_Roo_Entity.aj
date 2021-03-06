// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package com.ruyicai.dataanalysis.domain.lq;

import com.ruyicai.dataanalysis.domain.lq.ScheduleJcl;
import java.lang.Integer;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Table;
import org.springframework.transaction.annotation.Transactional;

privileged aspect ScheduleJcl_Roo_Entity {
    
    declare @type: ScheduleJcl: @Entity;
    
    declare @type: ScheduleJcl: @Table(name = "schedulejcl");
    
    @PersistenceContext(unitName = "persistenceUnit")
    transient EntityManager ScheduleJcl.entityManager;
    
    @Transactional("transactionManager")
    public void ScheduleJcl.flush() {
        if (this.entityManager == null) this.entityManager = entityManager();
        this.entityManager.flush();
    }
    
    @Transactional("transactionManager")
    public void ScheduleJcl.clear() {
        if (this.entityManager == null) this.entityManager = entityManager();
        this.entityManager.clear();
    }
    
    public static final EntityManager ScheduleJcl.entityManager() {
        EntityManager em = new ScheduleJcl().entityManager;
        if (em == null) throw new IllegalStateException("Entity manager has not been injected (is the Spring Aspects JAR configured as an AJC/AJDT aspects library?)");
        return em;
    }
    
    public static long ScheduleJcl.countScheduleJcls() {
        return entityManager().createQuery("SELECT COUNT(o) FROM ScheduleJcl o", Long.class).getSingleResult();
    }
    
    public static List<ScheduleJcl> ScheduleJcl.findAllScheduleJcls() {
        return entityManager().createQuery("SELECT o FROM ScheduleJcl o", ScheduleJcl.class).getResultList();
    }
    
    public static ScheduleJcl ScheduleJcl.findScheduleJcl(Integer scheduleId) {
        if (scheduleId == null) return null;
        return entityManager().find(ScheduleJcl.class, scheduleId);
    }
    
    public static List<ScheduleJcl> ScheduleJcl.findScheduleJclEntries(int firstResult, int maxResults) {
        return entityManager().createQuery("SELECT o FROM ScheduleJcl o", ScheduleJcl.class).setFirstResult(firstResult).setMaxResults(maxResults).getResultList();
    }
    
}
