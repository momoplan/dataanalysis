// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package com.ruyicai.dataanalysis.dto;

import com.ruyicai.dataanalysis.domain.TechnicCount;
import com.ruyicai.dataanalysis.dto.ScheduleDTO;

privileged aspect TechnicCountDto_Roo_JavaBean {
    
    public ScheduleDTO TechnicCountDto.getSchedule() {
        return this.schedule;
    }
    
    public void TechnicCountDto.setSchedule(ScheduleDTO schedule) {
        this.schedule = schedule;
    }
    
    public TechnicCount TechnicCountDto.getTechnicCount() {
        return this.technicCount;
    }
    
    public void TechnicCountDto.setTechnicCount(TechnicCount technicCount) {
        this.technicCount = technicCount;
    }
    
}
