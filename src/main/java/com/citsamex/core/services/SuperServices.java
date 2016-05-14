package com.citsamex.core.services;

import java.util.HashMap;
import java.util.List;

import com.citsamex.core.util.DBUtil;

public class SuperServices {

	/**
	 * 根据科目编码,查询科目Id
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
			throw new RuntimeException("查询科目异常.请执行sql:" + sql);
		}
		return accsubjid;
	}
	
	/**
	 * 获取辅助核算项信息.
	 * @param sql
	 * @return
	 * @throws Exception
	 */
	public String getFItemID(String sql, String fnumber) throws Exception{
		List templist = DBUtil.querySql(sql);
		if(templist != null && templist.size() == 1){
			return ((HashMap)templist.get(0)).get("FItemID").toString();
		}else if(templist == null || templist.size() == 0){
			throw new Exception("未找到编码包含" + fnumber + "的辅助核算项目!");
		}else{
			throw new Exception("查询辅助核算项目异常.请执行sql检查:" + sql);
		}
	}
	
	/**
	 * 获取用户Id
	 * @param username
	 * @return
	 * @throws Exception
	 */
	public String getUserId(String username) throws Exception{
		if(username == null || "".equals(username)){
			throw new Exception("请填写用户名!");
		}
		
		String userid = "";
		List templist = DBUtil.querySql("select * from t_user where FName='"+username+"'");
		if(templist != null && templist.size() == 1){
			userid = ((HashMap)templist.get(0)).get("FUserID").toString();
		}else{
			throw new Exception("未查到相关用户信息,请检查用户名是否正确!");
		}
		return userid;
	}
	
	/**
	 * 校验业务期间
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
			throw new Exception("该财年已结账,请重新选择年份!");
		}
		String monthSql = "select FValue from t_systemprofile where FKey='currentperiod' and FCategory='FA'";
		templist = DBUtil.querySql(monthSql);
		int sysmonth = Integer.parseInt(((HashMap)templist.get(0)).get("FValue").toString());
		if(month < sysmonth){
			throw new Exception("该会计期间已结账,请重新选择月份!");
		}
	}
	
}
