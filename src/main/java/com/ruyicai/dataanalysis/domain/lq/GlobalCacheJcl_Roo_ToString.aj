// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package com.ruyicai.dataanalysis.domain.lq;

import java.lang.String;

privileged aspect GlobalCacheJcl_Roo_ToString {
    
    public String GlobalCacheJcl.toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Id: ").append(getId()).append(", ");
        sb.append("Value: ").append(getValue());
        return sb.toString();
    }
    
}
