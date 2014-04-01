package com.ruyicai.dataanalysis.consts;

import org.apache.commons.lang.StringUtils;

public enum LetgoalCompany {

	am("1", "澳门"),
	lb("4", "立博"),
	bet365("8", "Bet365"),
	jbb("23", "金宝博"),
	otbet("24", "12bet"),
	lj("31", "利记");
	
	private String companyId;
	
	private String companyName;
	
	public String getCompanyId() {
		return companyId;
	}

	public String getCompanyName() {
		return companyName;
	}

	private LetgoalCompany(String companyId, String companyName) {
		this.companyId = companyId;
		this.companyName = companyName;
	}
	
	public static boolean containsCompanyId(String companyId) {
		if (StringUtils.isBlank(companyId)) {
			return false;
		}
		LetgoalCompany[] values = LetgoalCompany.values();
		for (LetgoalCompany standardCompany : values) {
			if (StringUtils.equals(standardCompany.getCompanyId(), companyId)) {
				return true;
			}
		}
		return false;
	}
	
}
