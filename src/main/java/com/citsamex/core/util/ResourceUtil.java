package com.citsamex.core.util;

import java.util.ResourceBundle;

/**
 * ��Ŀ����������
 * 
 * @author zhangdaihao
 * 
 */
public class ResourceUtil {

	private static final ResourceBundle bundle = java.util.ResourceBundle.getBundle("config");

	/**
	 * ��ȡ
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
