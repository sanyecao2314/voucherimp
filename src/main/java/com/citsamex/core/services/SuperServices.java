package com.citsamex.core.services;

import java.util.HashMap;
import java.util.List;

import com.citsamex.core.util.DBUtil;

public class SuperServices {

	/**
	 * ���ݿ�Ŀ����,��ѯ��ĿId
	 * 
	 * @param accNumber
	 * @return
	 * @throws Exception
	 */
	public String getAccsubjid(String accNumber) throws Exception{
		String accsubjid = "";
		String sql = "select FAccountID from t_Account where FNumber='" + accNumber + "'";
		List templist = DBUtil.querySql(sql);
		if(templist != null && templist.size() == 1){
			accsubjid = ((HashMap)templist.get(0)).get("FAccountID").toString();
		}else{
			throw new RuntimeException("��ѯ��Ŀ�쳣.��ִ��sql:" + sql);
		}
		return accsubjid;
	}
	
	/**
	 * ��ȡ������������Ϣ.
	 * @param sql
	 * @return
	 * @throws Exception
	 */
	public String getFItemID(String sql, String fnumber) throws Exception{
		List templist = DBUtil.querySql(sql);
		if(templist != null && templist.size() == 1){
			return ((HashMap)templist.get(0)).get("FItemID").toString();
		}else if(templist == null || templist.size() == 0){
			throw new Exception("δ�ҵ��������" + fnumber + "�ĸ���������Ŀ!");
		}else{
			throw new Exception("��ѯ����������Ŀ�쳣.��ִ��sql���:" + sql);
		}
	}
	
	/**
	 * ��ȡ�û�Id
	 * @param username
	 * @return
	 * @throws Exception
	 */
	public String getUserId(String username) throws Exception{
		if(username == null || "".equals(username)){
			throw new Exception("����д�û���!");
		}
		
		String userid = "";
		List templist = DBUtil.querySql("select * from t_user where FName='"+username+"'");
		if(templist != null && templist.size() == 1){
			userid = ((HashMap)templist.get(0)).get("FUserID").toString();
		}else{
			throw new Exception("δ�鵽����û���Ϣ,�����û����Ƿ���ȷ!");
		}
		return userid;
	}
	
	/**
	 * У��ҵ���ڼ�
	 * 
	 * @param year
	 * @param month
	 * @throws Exception
	 */
	public void checkYearAndMonth(int year, int month) throws Exception{
		String yearSql = "select FValue from t_systemprofile where FKey='currentyear' and FCategory='FA'";
		List templist = DBUtil.querySql(yearSql);
		int sysyear = Integer.parseInt(((HashMap)templist.get(0)).get("FValue").toString());
		if(year < sysyear){
			throw new Exception("�ò����ѽ���,������ѡ�����!");
		}
		String monthSql = "select FValue from t_systemprofile where FKey='currentperiod' and FCategory='FA'";
		templist = DBUtil.querySql(monthSql);
		int sysmonth = Integer.parseInt(((HashMap)templist.get(0)).get("FValue").toString());
		if(month < sysmonth){
			throw new Exception("�û���ڼ��ѽ���,������ѡ���·�!");
		}
	}
	
}
