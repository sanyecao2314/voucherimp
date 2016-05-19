package com.citsamex.core.util;

import org.assertj.core.util.Sets;

public class StringUtil {

	public static boolean isEmpty(String str){
		return str == null || "".equals(str);
	}
}
