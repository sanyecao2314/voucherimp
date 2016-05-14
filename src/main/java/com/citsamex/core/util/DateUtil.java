package com.citsamex.core.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class DateUtil {

	/**
	 * ������ѡ���ʱ��ת��Ϊҵ��ϵͳ��Ҫ�ĸ�ʽ.
	 * 
	 * @param year
	 * @param month
	 * @return
	 */
	public static String getDateStr(String year, String month){
		Calendar cal = Calendar.getInstance();
		cal.set(Calendar.YEAR,Integer.parseInt(year));
		cal.set(Calendar.MONTH,Integer.parseInt(month) - 1);
		cal.set(Calendar.DAY_OF_MONTH, cal.getMaximum(Calendar.DAY_OF_MONTH)-1);
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		return df.format(cal.getTime());
	}
	
}
