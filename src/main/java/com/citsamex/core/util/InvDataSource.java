package com.citsamex.core.util;

import java.util.ResourceBundle;

import org.apache.commons.dbcp.BasicDataSource;

/**
 * ���ݿ����Ӳ�����.
 * @author fans.fan
 *
 */
public class InvDataSource extends BasicDataSource {

	private static final ResourceBundle props = java.util.ResourceBundle.getBundle("config");

	public static String getProperty(String key) {
		return props.getString(key);
	}

	public InvDataSource() {
		String driver = props.getString("DRIVER");
		String url = props.getString("URL");
		String username = props.getString("USERNAME");
		String password = props.getString("PASSWORD");

		this.setDriverClassName(driver);
		this.setUrl(url);
		this.setUsername(username);
		this.setPassword(password);
	}

}
