// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package com.ruyicai.dataanalysis.domain;

import com.ruyicai.dataanalysis.domain.CupMatch_Grouping;
import java.util.List;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Table;
import org.springframework.transaction.annotation.Transactional;

privileged aspect CupMatch_Grouping_Roo_Entity {
    
    declare @type: CupMatch_Grouping: @Entity;
    
    declare @type: CupMatch_Grouping: @Table(name = "CupMatch_Grouping");
    
    @PersistenceContext(unitName = "persistenceUnit")
    transient EntityManager CupMatch_Grouping.entityManager;
    
    @Transactional("transactionManager")
    public void CupMatch_Grouping.persist() {
        if (this.entityManager == null) this.entityManager = entityManager();
        this.entityManager.persist(this);
    }
    
    @Transactional("transactionManager")
    public void CupMatch_Grouping.remove() {
        if (this.entityManager == null) this.entityManager = entityManager();
        if (this.entityManager.contains(this)) {
            this.entityManager.remove(this);
        } else {
            CupMatch_Grouping attached = CupMatch_Grouping.findCupMatch_Grouping(this.groupID);
            this.entityManager.remove(attached);
        }
    }
    
    @Transactional("transactionManager")
    public void CupMatch_Grouping.flush() {
        if (this.entityManager == null) this.entityManager = entityManager();
        this.entityManager.flush();
    }
    
    @Transactional("transactionManager")
    public void CupMatch_Grouping.clear() {
        if (this.entityManager == null) this.entityManager = entityManager();
        this.entityManager.clear();
    }
    
    @Transactional("transactionManager")
    public CupMatch_Grouping CupMatch_Grouping.merge() {
        if (this.entityManager == null) this.entityManager = entityManager();
        CupMatch_Grouping merged = this.entityManager.merge(this);
        this.entityManager.flush();
        return merged;
    }
    
    public static final EntityManager CupMatch_Grouping.entityManager() {
        EntityManager em = new CupMatch_Grouping().entityManager;
        if (em == null) throw new IllegalStateException("Entity manager has not been injected (is the Spring Aspects JAR configured as an AJC/AJDT aspects library?)");
        return em;
    }
    
    public static long CupMatch_Grouping.countCupMatch_Groupings() {
        return entityManager().createQuery("SELECT COUNT(o) FROM CupMatch_Grouping o", Long.class).getSingleResult();
    }
    
    public static List<CupMatch_Grouping> CupMatch_Grouping.findAllCupMatch_Groupings() {
        return entityManager().createQuery("SELECT o FROM CupMatch_Grouping o", CupMatch_Grouping.class).getResultList();
    }
    
    public static CupMatch_Grouping CupMatch_Grouping.findCupMatch_Grouping(int groupID) {
        return entityManager().find(CupMatch_Grouping.class, groupID);
    }
    
    public static List<CupMatch_Grouping> CupMatch_Grouping.findCupMatch_GroupingEntries(int firstResult, int maxResults) {
        return entityManager().createQuery("SELECT o FROM CupMatch_Grouping o", CupMatch_Grouping.class).setFirstResult(firstResult).setMaxResults(maxResults).getResultList();
    }
    
}
