// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package com.ruyicai.dataanalysis.domain;

import java.lang.String;

privileged aspect LetGoalhalf_Roo_ToString {
    
    public String LetGoalhalf.toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("CompanyID: ").append(getCompanyID()).append(", ");
        sb.append("DownOdds: ").append(getDownOdds()).append(", ");
        sb.append("FirstDownodds: ").append(getFirstDownodds()).append(", ");
        sb.append("FirstGoal: ").append(getFirstGoal()).append(", ");
        sb.append("FirstUpodds: ").append(getFirstUpodds()).append(", ");
        sb.append("Goal: ").append(getGoal()).append(", ");
        sb.append("ModifyTime: ").append(getModifyTime()).append(", ");
        sb.append("OddsID: ").append(getOddsID()).append(", ");
        sb.append("ScheduleID: ").append(getScheduleID()).append(", ");
        sb.append("UpOdds: ").append(getUpOdds()).append(", ");
        sb.append("ZouDi: ").append(getZouDi());
        return sb.toString();
    }
    
}
