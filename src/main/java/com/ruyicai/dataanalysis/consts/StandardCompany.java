package com.ruyicai.dataanalysis.consts;

import org.apache.commons.lang.StringUtils;

public enum StandardCompany {

	wlxe("115", "威廉希尔"),
	lb("82", "立博"),
	bwin("255", "bwin"),
	am("80", "澳门"),
	bet365("281", "bet365");
	
	private String companyId;
	
	private String companyName;
	
	public String getCompanyId() {
		return companyId;
	}

	public String getCompanyName() {
		return companyName;
	}

	private StandardCompany(String companyId, String companyName) {
		this.companyId = companyId;
		this.companyName = companyName;
	}
	
	public static boolean containsCompanyId(String companyId) {
		if (StringUtils.isBlank(companyId)) {
			return false;
		}
		StandardCompany[] values = StandardCompany.values();
		for (StandardCompany standardCompany : values) {
			if (StringUtils.equals(standardCompany.getCompanyId(), companyId)) {
				return true;
			}
		}
		return false;
	}
	
}
