// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package com.ruyicai.dataanalysis.domain;

import com.ruyicai.dataanalysis.domain.TechnicCount;
import flexjson.JSONDeserializer;
import flexjson.JSONSerializer;
import java.lang.String;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

privileged aspect TechnicCount_Roo_Json {
    
    public String TechnicCount.toJson() {
        return new JSONSerializer().exclude("*.class").serialize(this);
    }
    
    public static TechnicCount TechnicCount.fromJsonToTechnicCount(String json) {
        return new JSONDeserializer<TechnicCount>().use(null, TechnicCount.class).deserialize(json);
    }
    
    public static String TechnicCount.toJsonArray(Collection<TechnicCount> collection) {
        return new JSONSerializer().exclude("*.class").serialize(collection);
    }
    
    public static Collection<TechnicCount> TechnicCount.fromJsonArrayToTechnicCounts(String json) {
        return new JSONDeserializer<List<TechnicCount>>().use(null, ArrayList.class).use("values", TechnicCount.class).deserialize(json);
    }
    
}
