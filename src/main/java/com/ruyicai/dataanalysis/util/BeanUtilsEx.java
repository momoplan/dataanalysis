package com.ruyicai.dataanalysis.util;

import java.lang.reflect.InvocationTargetException;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.beanutils.converters.DateConverter;

/**
 * 改进BeanUtils使其支持java.util.Date类型
 * 如果不改进，遇到Date类型为null的变量，调用copyProperties方法会出现异常
 * 参考:http://hilor.iteye.com/blog/283553
 * @author Administrator
 *
 */
public class BeanUtilsEx extends BeanUtils {

	static {
		ConvertUtils.register(new DateConverter(null), java.util.Date.class);
	}

	public static void copyProperties(Object dest, Object orig) {
		try {
			BeanUtils.copyProperties(dest, orig);
		} catch (IllegalAccessException ex) {
			ex.printStackTrace();
		} catch (InvocationTargetException ex) {
			ex.printStackTrace();
		}
	}

}
