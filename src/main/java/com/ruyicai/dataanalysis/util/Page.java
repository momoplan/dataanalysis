package com.ruyicai.dataanalysis.util;

import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang.StringUtils;
import org.springframework.roo.addon.tostring.RooToString;
import com.ruyicai.dataanalysis.consts.ErrorCode;
import com.ruyicai.dataanalysis.exception.RuyicaiException;

@RooToString
public class Page<E> {

	/** 开始记录行数FirstResult */
	private Integer pageIndex;
	/** 总页数 */
	@SuppressWarnings("unused")
	private Integer totalPage;
	/** 每页显示记录数maxResult */
	private Integer maxResult;
	/** 总记录数 */
	private Integer totalResult = 0;
	/** 排序字段名称 */
	private String orderBy = null;
	/** 排序顺序 */
	private String orderDir = null;
	/** 查询结果集 */
	private List<E> list;
	
	public Page() {
		super();
	}

	public Page(int pageIndex,int maxResult) {
		super();
		this.pageIndex = pageIndex;
		this.maxResult = maxResult;
	}

	public Page(Integer pageIndex, Integer maxResult, String orderBy, String orderDir) {
		super();
		this.pageIndex = pageIndex;
		this.maxResult = maxResult;
		this.orderBy = orderBy;
		this.orderDir = orderDir;
	}

	public Page(Integer pageIndex, Integer maxResult, Integer totalResult, String orderBy, String orderDir, List<E> list) {
		super();
		this.pageIndex = pageIndex;
		this.maxResult = maxResult;
		this.totalResult = totalResult;
		this.orderBy = orderBy;
		this.orderDir = orderDir;
		this.list = list;
	}
	
	/**
	 * 当前页数
	 */
	public Integer getCurrentPageNo() {
		if (pageIndex == null || maxResult == null || maxResult == 0) {
			return 1;
		}
		return pageIndex/maxResult+1;
	}

	public Integer getPageIndex() {

		return null == pageIndex ? 0 : pageIndex;
	}

	public void setPageIndex(Integer pageIndex) {
		this.pageIndex = pageIndex;
	}

	public Integer getTotalPage() {
		
		return totalResult % getMaxResult() == 0 ? totalResult / getMaxResult() : totalResult / getMaxResult() + 1;
	}

	public void setTotalPage(Integer totalPage) {
		this.totalPage = totalPage;
	}

	public Integer getMaxResult() {
		return null == maxResult ? 15 : maxResult; 
	}

	public void setMaxResult(Integer maxResult) {
		this.maxResult = maxResult;
	}

	public List<E> getList() {
		return list;
	}
	
	public void setList(List<E> list) {
		this.list = list;
	}

	public Integer getTotalResult() {
		return totalResult;
	}

	public void setTotalResult(Integer totalResult) {
		this.totalResult = totalResult;
	}

	public String getOrderBy() {
		return orderBy;
	}

	public void setOrderBy(String orderBy) {
		this.orderBy = orderBy;
	}

	public String getOrderDir() {
		return orderDir;
	}

	public void setOrderDir(String orderDir) {
		String lowcaseOrderDir = StringUtils.lowerCase(orderDir);
		String[] orderDirs = StringUtils.split(lowcaseOrderDir, ',');
		for (String orderDirStr : orderDirs) {
			if (!Sort.DESC.equalsIgnoreCase(orderDirStr) && !Sort.ASC.equalsIgnoreCase(orderDirStr)) {
				throw new RuyicaiException(ErrorCode.ERROR);
			}
		}
		this.orderDir = lowcaseOrderDir;
	}
	
	public boolean isOrderBySetted() {
		return StringUtils.isNotBlank(orderBy);
	}

	public List<Sort> fetchSort() {
		List<Sort> orders = new ArrayList<Sort>();
		if(StringUtils.isBlank(orderBy) || StringUtils.isBlank(orderDir)){
			return orders;
		}
		String[] orderBys = StringUtils.split(orderBy, ',');
		String[] orderDirs = StringUtils.split(orderDir, ',');
		if (orderBys.length != orderDirs.length) {
			throw new RuyicaiException(ErrorCode.ERROR);
		}

		for (int i = 0; i < orderBys.length; i++) {
			orders.add(new Sort(orderBys[i], orderDirs[i]));
		}
		return orders;
	}
	
	public static class Sort {
		public static final String ASC = "asc";
		public static final String DESC = "desc";

		private final String property;
		private final String dir;

		public Sort(String property, String dir) {
			this.property = property;
			this.dir = dir;
		}

		public String getProperty() {
			return property;
		}

		public String getDir() {
			return dir;
		}
	}
	
}
