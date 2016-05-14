package com.citsamex.core.util;

import java.util.ResourceBundle;

/**
 * 项目参数工具类
 * 
 * @author zhangdaihao
 * 
 */
public class ResourceUtil {

	private static final ResourceBundle bundle = java.util.ResourceBundle.getBundle("config");

	/**
	 * 获取
	 * 
	 * @return
	 */
	public static final String getProperty(String name) {
		return bundle.getString(name);
	}
	
	public static void main(String[] args) {
		System.out.println(getProperty("jdbc_url"));
	}
	

}
