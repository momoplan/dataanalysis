// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package com.ruyicai.dataanalysis.domain.lq;

import com.ruyicai.dataanalysis.domain.lq.TeamJcl;
import flexjson.JSONDeserializer;
import flexjson.JSONSerializer;
import java.lang.String;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

privileged aspect TeamJcl_Roo_Json {
    
    public String TeamJcl.toJson() {
        return new JSONSerializer().exclude("*.class").serialize(this);
    }
    
    public static TeamJcl TeamJcl.fromJsonToTeamJcl(String json) {
        return new JSONDeserializer<TeamJcl>().use(null, TeamJcl.class).deserialize(json);
    }
    
    public static String TeamJcl.toJsonArray(Collection<TeamJcl> collection) {
        return new JSONSerializer().exclude("*.class").serialize(collection);
    }
    
    public static Collection<TeamJcl> TeamJcl.fromJsonArrayToTeamJcls(String json) {
        return new JSONDeserializer<List<TeamJcl>>().use(null, ArrayList.class).use("values", TeamJcl.class).deserialize(json);
    }
    
}