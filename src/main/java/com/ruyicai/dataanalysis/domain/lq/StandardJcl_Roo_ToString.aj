// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package com.ruyicai.dataanalysis.domain.lq;

import java.lang.String;

privileged aspect StandardJcl_Roo_ToString {
    
    public String StandardJcl.toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("CompanyId: ").append(getCompanyId()).append(", ");
        sb.append("CompanyName: ").append(getCompanyName()).append(", ");
        sb.append("FanHuanLv: ").append(getFanHuanLv()).append(", ");
        sb.append("FirstGuestWin: ").append(getFirstGuestWin()).append(", ");
        sb.append("FirstHomeWin: ").append(getFirstHomeWin()).append(", ");
        sb.append("GuestWin: ").append(getGuestWin()).append(", ");
        sb.append("GuestWinLv: ").append(getGuestWinLv()).append(", ");
        sb.append("HomeWin: ").append(getHomeWin()).append(", ");
        sb.append("HomeWinLv: ").append(getHomeWinLv()).append(", ");
        sb.append("K_g: ").append(getK_g()).append(", ");
        sb.append("K_h: ").append(getK_h()).append(", ");
        sb.append("ModifyTime: ").append(getModifyTime()).append(", ");
        sb.append("OddsId: ").append(getOddsId()).append(", ");
        sb.append("ScheduleId: ").append(getScheduleId());
        return sb.toString();
    }
    
}