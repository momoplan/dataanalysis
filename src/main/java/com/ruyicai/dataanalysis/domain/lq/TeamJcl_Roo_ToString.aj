// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package com.ruyicai.dataanalysis.domain.lq;

import java.lang.String;

privileged aspect TeamJcl_Roo_ToString {
    
    public String TeamJcl.toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Flag: ").append(getFlag()).append(", ");
        sb.append("NameJ: ").append(getNameJ()).append(", ");
        sb.append("SclassId: ").append(getSclassId()).append(", ");
        sb.append("ShortJ: ").append(getShortJ()).append(", ");
        sb.append("Support: ").append(getSupport()).append(", ");
        sb.append("TeamId: ").append(getTeamId()).append(", ");
        sb.append("TeamJclCache: ").append(getTeamJclCache());
        return sb.toString();
    }
    
}
