// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package com.ruyicai.dataanalysis.dto.lq;

import com.ruyicai.dataanalysis.dto.lq.ClasliAnalysisJclDto;
import flexjson.JSONDeserializer;
import flexjson.JSONSerializer;
import java.lang.String;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

privileged aspect ClasliAnalysisJclDto_Roo_Json {
    
    public String ClasliAnalysisJclDto.toJson() {
        return new JSONSerializer().exclude("*.class").serialize(this);
    }
    
    public static ClasliAnalysisJclDto ClasliAnalysisJclDto.fromJsonToClasliAnalysisJclDto(String json) {
        return new JSONDeserializer<ClasliAnalysisJclDto>().use(null, ClasliAnalysisJclDto.class).deserialize(json);
    }
    
    public static String ClasliAnalysisJclDto.toJsonArray(Collection<ClasliAnalysisJclDto> collection) {
        return new JSONSerializer().exclude("*.class").serialize(collection);
    }
    
    public static Collection<ClasliAnalysisJclDto> ClasliAnalysisJclDto.fromJsonArrayToClasliAnalysisJclDtoes(String json) {
        return new JSONDeserializer<List<ClasliAnalysisJclDto>>().use(null, ArrayList.class).use("values", ClasliAnalysisJclDto.class).deserialize(json);
    }
    
}
