package com.ruyicai.dataanalysis.controller;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import flexjson.JSONDeserializer;
import flexjson.JSONSerializer;

public class ResponseData {
	private boolean isDepreciated=false;
	private String errorCode;
	private Object value;
	public boolean isDepreciated() {
		return isDepreciated;
	}
	public void setDepreciated(boolean isDepreciated) {
		this.isDepreciated = isDepreciated;
	}
	public String getErrorCode() {
		return errorCode;
	}
	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}
	public Object getValue() {
		return value;
	}
	public void setValue(Object value) {
		this.value = value;
	}
	
	public String toJson() {
        return new JSONSerializer().exclude("*.class").serialize(this);
    }
    
    public static ResponseData fromJsonToResponseData(String json) {
        return new JSONDeserializer<ResponseData>().use(null, ResponseData.class).deserialize(json);
    }
    
    public static String toJsonArray(Collection<ResponseData> collection) {
        return new JSONSerializer().exclude("*.class").serialize(collection);
    }
    
    public static Collection<ResponseData> fromJsonArrayToResponseDatas(String json) {
        return new JSONDeserializer<List<ResponseData>>().use(null, ArrayList.class).use("values", ResponseData.class).deserialize(json);
    }
}
