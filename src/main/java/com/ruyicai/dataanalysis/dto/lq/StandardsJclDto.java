package com.ruyicai.dataanalysis.dto.lq;

import java.io.Serializable;
import java.util.Collection;

import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.json.RooJson;

import com.ruyicai.dataanalysis.domain.lq.StandardJcl;

@RooJavaBean
@RooJson
public class StandardsJclDto implements Serializable {

	private static final long serialVersionUID = 1L;

	private ScheduleJclDTO schedule;
	
	private Collection<StandardJcl> standards;
	
}




