// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package com.ruyicai.dataanalysis.domain;

import java.lang.String;

privileged aspect CupMatchRankingPK_Roo_ToString {
    
    public String CupMatchRankingPK.toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("League: ").append(getLeague()).append(", ");
        sb.append("Ranking: ").append(getRanking()).append(", ");
        sb.append("Season: ").append(getSeason());
        return sb.toString();
    }
    
}
