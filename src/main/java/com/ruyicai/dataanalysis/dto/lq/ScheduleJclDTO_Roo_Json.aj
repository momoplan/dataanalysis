// WARNING: DO NOT EDIT THIS FILE. THIS FILE IS MANAGED BY SPRING ROO.
// You may push code into the target .java compilation unit if you wish to edit any member(s).

package com.ruyicai.dataanalysis.dto.lq;

import com.ruyicai.dataanalysis.dto.lq.ScheduleJclDTO;
import flexjson.JSONDeserializer;
import flexjson.JSONSerializer;
import java.lang.String;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

privileged aspect ScheduleJclDTO_Roo_Json {
    
    public static ScheduleJclDTO ScheduleJclDTO.fromJsonToScheduleJclDTO(String json) {
        return new JSONDeserializer<ScheduleJclDTO>().use(null, ScheduleJclDTO.class).deserialize(json);
    }
    
    public static String ScheduleJclDTO.toJsonArray(Collection<ScheduleJclDTO> collection) {
        return new JSONSerializer().exclude("*.class").serialize(collection);
    }
    
    public static Collection<ScheduleJclDTO> ScheduleJclDTO.fromJsonArrayToScheduleJclDTO(String json) {
        return new JSONDeserializer<List<ScheduleJclDTO>>().use(null, ArrayList.class).use("values", ScheduleJclDTO.class).deserialize(json);
    }
    
}
