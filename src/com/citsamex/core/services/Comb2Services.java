package com.citsamex.core.services;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import javax.swing.JOptionPane;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;

import com.citsamex.core.startup.MainStart;
import com.citsamex.core.util.DBUtil;
import com.citsamex.core.util.ExcelUtil;
import com.citsamex.core.vo.CombVO1;
import com.citsamex.core.vo.VoucherEntryVO;

/**
 * COMBOVALUE2 = "公司手续费收入划拨明细表";
 * @author fans.fan
 *
 */
public class Comb2Services {
	
	public static ArrayList readXls(File file){
		
		HSSFWorkbook rwb = null;// 声明一个工作簿。
		HSSFSheet sht = null;// 声明一个工作表。
		HSSFRow row = null;

		ArrayList voList = new ArrayList();
		try {
			InputStream io = new FileInputStream(file);
			rwb = new HSSFWorkbook(io);
			sht = rwb.getSheetAt(0);
			if (sht == null) {
//				JOptionPane.showMessageDialog(this, "不存在页签");
				System.out.println("不存在页签");
				return null;
			}
			String value = null;
			for (int i = 4, rows = sht.getLastRowNum(); i < rows; i++) {
				row = sht.getRow(i);
				value = ExcelUtil.getCellValue(row.getCell(0));
				if (value != null && value.contains("合计")) {
					break;
				}
					
				String[] vo = new String[14];
				for (int j = 0; j < vo.length; j++) {
					vo[j] = ExcelUtil.getCellValue(row.getCell(j));
				}
				voList.add(vo);
			}
		} catch (Exception e) {
			System.out.println();
			e.printStackTrace();
		}
		return voList;
	}
	/**
	 * 转换文件
	 * @param list 
	 * 基金名称	合计	直销认购费	直销申购费	直销赎回费	直销转换费	直销补差费	直销销售服务费	公司认购费	公司申购费	公司赎回费	公司转换费	公司补差费	公司后端申购费
	 * 直销认购费+公司认购费	应收账款/应收认购费	手续费及佣金收入/基金销售手续费收入/认购费-前端
	 * 直销申购费+公司申购费	应收账款/应收申购费	手续费及佣金收入/基金销售手续费收入/申购费-前端
	 * 直销赎回费+公司赎回费	应收账款/应收赎回费	手续费及佣金收入/基金销售手续费收入/赎回费
	 * 直销转换费+直销补差费+公司转换费+公司补差费	应收账款/应收转换费	手续费及佣金收入/基金销售手续费收入/转换费
	 * 公司后端申购费	应收账款/应收申购费-后端	手续费及佣金收入/基金销售手续费收入/申购费-后端
	 * @return
	 * @throws Exception 
	 */
	public List convertFile2VO(ArrayList list,MainStart mainUI) throws Exception{
		if(list == null || list.size() == 0){
			return null;
		}
		
		List voList = new ArrayList();
		List templist = null;
		String[] jaccsubjid = new String[6];
		String[] daccsubjid = new String[6];
		String userid = null;
		
		//借方科目检查
		String[] jsqls = new String[]{"select FAccountID from t_Account where FNumber='1122.02'",
									 "select FAccountID from t_Account where FNumber='1122.03'",
									 "select FAccountID from t_Account where FNumber='1122.04'",
									 "select FAccountID from t_Account where FNumber='1122.05'",
									 "select FAccountID from t_Account where FNumber='1122.07'",
									 "select FAccountID from t_Account where FNumber='1122.06'"
									}; 
		for (int i = 0; i < jsqls.length; i++) {
			templist = DBUtil.querySql(jsqls[i]);
			if(templist != null && templist.size() == 1){
				jaccsubjid[i] = ((HashMap)templist.get(0)).get("FAccountID").toString();
			}else{
				throw new Exception("查询借方科目异常.请执行sql:" + jsqls[i]);
			}
		}
		
		//贷方科目检查
		String[] dsqls = new String[]{"select FAccountID from t_Account where FNumber='6021.01.01'",
				 					  "select FAccountID from t_Account where FNumber='6021.01.03'",
									  "select FAccountID from t_Account where FNumber='6021.01.06'",
									  "select FAccountID from t_Account where FNumber='6021.01.05'",
									  "select FAccountID from t_Account where FNumber='6021.01.04'",
									  "select FAccountID from t_Account where FNumber='6022.01'"
									}; 
		for (int i = 0; i < dsqls.length; i++) {
			templist = DBUtil.querySql(dsqls[i]);
			if(templist != null && templist.size() == 1){
				daccsubjid[i] = ((HashMap)templist.get(0)).get("FAccountID").toString();
			}else{
				throw new Exception("查询贷方科目异常.请执行sql:" + jsqls[i]);
			}
		}
		
		//校验用户名称
		String username = mainUI.usernameTextField.getText();
		if(username == null || "".equals(username)){
			throw new Exception("请填写用户名!");
		}
		
		templist = DBUtil.querySql("select * from t_user where FName='"+username+"'");
		if(templist != null && templist.size() == 1){
			userid = ((HashMap)templist.get(0)).get("FUserID").toString();
		}else{
			throw new Exception("未查到相关用户信息,请检查用户名是否正确!");
		}
		
		//校验业务期间.
		int year = Integer.parseInt(mainUI.yearComboBox.getSelectedItem().toString());
		int month = Integer.parseInt(mainUI.monthComboBox.getSelectedItem().toString());
		
		String yearSql = "select FValue from t_systemprofile where FKey='currentyear' and FCategory='FA'";
		templist = DBUtil.querySql(yearSql);
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
		
		for(int i = 0;i<list.size();i++){
			String[] str = (String[]) list.get(i);
			VoucherEntryVO voucherEntryvo = getVoucher1(str);
			if(voucherEntryvo != null){
				voucherEntryvo.setFYear(String.valueOf(year));
				voucherEntryvo.setFPeriod(String.valueOf(month));
				voucherEntryvo.setFPreparerID(userid);
				voucherEntryvo.setFExplanation("计提" + month + "月手续费收入");
				voucherEntryvo.setJAccountID(jaccsubjid[0]);
				voucherEntryvo.setDAccountID(daccsubjid[0]);
				voList.add(voucherEntryvo);
			}
			voucherEntryvo = getVoucher2(str);
			if(voucherEntryvo != null){
				voucherEntryvo.setFYear(String.valueOf(year));
				voucherEntryvo.setFPeriod(String.valueOf(month));
				voucherEntryvo.setFPreparerID(userid);
				voucherEntryvo.setFExplanation("计提" + month + "月手续费收入");
				voucherEntryvo.setJAccountID(jaccsubjid[1]);
				voucherEntryvo.setDAccountID(daccsubjid[1]);
				voList.add(voucherEntryvo);
			}
			voucherEntryvo = getVoucher3(str);
			if(voucherEntryvo != null){
				voucherEntryvo.setFYear(String.valueOf(year));
				voucherEntryvo.setFPeriod(String.valueOf(month));
				voucherEntryvo.setFPreparerID(userid);
				voucherEntryvo.setFExplanation("计提" + month + "月手续费收入");
				voucherEntryvo.setJAccountID(jaccsubjid[2]);
				voucherEntryvo.setDAccountID(daccsubjid[2]);
				voList.add(voucherEntryvo);
			}
			voucherEntryvo = getVoucher4(str);
			if(voucherEntryvo != null){
				voucherEntryvo.setFYear(String.valueOf(year));
				voucherEntryvo.setFPeriod(String.valueOf(month));
				voucherEntryvo.setFPreparerID(userid);
				voucherEntryvo.setFExplanation("计提" + month + "月手续费收入");
				voucherEntryvo.setJAccountID(jaccsubjid[3]);
				voucherEntryvo.setDAccountID(daccsubjid[3]);
				voList.add(voucherEntryvo);
			}
			voucherEntryvo = getVoucher5(str);
			if(voucherEntryvo != null){
				voucherEntryvo.setFYear(String.valueOf(year));
				voucherEntryvo.setFPeriod(String.valueOf(month));
				voucherEntryvo.setFPreparerID(userid);
				voucherEntryvo.setFExplanation("计提" + month + "月手续费收入");
				voucherEntryvo.setJAccountID(jaccsubjid[4]);
				voucherEntryvo.setDAccountID(daccsubjid[4]);
				voList.add(voucherEntryvo);
			}
			voucherEntryvo = getVoucher6(str);
			if(voucherEntryvo != null){
				voucherEntryvo.setFYear(String.valueOf(year));
				voucherEntryvo.setFPeriod(String.valueOf(month));
				voucherEntryvo.setFPreparerID(userid);
				voucherEntryvo.setFExplanation("计提" + month + "月手续费收入");
				voucherEntryvo.setJAccountID(jaccsubjid[5]);
				voucherEntryvo.setDAccountID(daccsubjid[5]);
				voList.add(voucherEntryvo);
			}
		}
		
		return voList;
	}
	
	/**
	 * 获取凭证分录.
	 * 直销认购费+公司认购费	应收账款/应收认购费	手续费及佣金收入/基金销售手续费收入/认购费-前端
	 * @param str
	 * @return
	 * @throws Exception 
	 */
	private VoucherEntryVO getVoucher1(String[] str) throws Exception{
		if(str == null || str.length ==0){
			return null;
		}
		BigDecimal amount = new BigDecimal(str[2] == null || "".equals(str[2]) ? "0":str[2].replaceAll(",", "")).add(new BigDecimal(str[8] == null || "".equals(str[8]) ? "0":str[8].replaceAll(",", "")));
		if(amount.doubleValue() <= 0){
			return null;
		}
		VoucherEntryVO voucherEntryvo = new VoucherEntryVO();
		String sql = "select * from t_item where fnumber like '%" + str[0].split("-")[0] + "'";
		List templist = DBUtil.querySql(sql);
		if(templist != null && templist.size() == 1){
			voucherEntryvo.setProject(((HashMap)templist.get(0)).get("FItemID").toString());
		}else if(templist == null || templist.size() == 0){
			throw new Exception("未找到编码包含" + str[0] + "的辅助核算项目!");
		}else{
			throw new Exception("查询辅助核算项目异常.请执行sql检查:" + sql);
		}
		sql = "select FDetailID from t_ItemDetail where FDetailCount=1 and F2039=" + voucherEntryvo.getProject();
		templist = DBUtil.querySql(sql);
		if(templist != null && templist.size() == 1){
			voucherEntryvo.setFDetailID(((HashMap)templist.get(0)).get("FDetailID").toString());
		}else if(templist == null || templist.size() == 0){
			//没有辅助核算信息.插入t_ItemDetail和t_ItemDetailV表辅助核算信息.
			Object  maxFDetailID = DBUtil.querySqlUniqueResult("select max(FDetailID)+1 from t_ItemDetail ");
			String insertItemDetail = "insert into t_ItemDetail(FDetailID,FDetailCount,F1,F2,F3,F4,F5,F8,F9,F10,F14,F2001,F2002,F2003,F2004,F2014,F2023,F2021,F2024,F2026,F2027,F2028,F2029,F2030,F2035,F2036,F2039,F2040,F2041) "
				+ "values ("+maxFDetailID+",1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,"+voucherEntryvo.getProject()+",0,0)";
			String insertItemDetailV = "insert into t_ItemDetailV(FDetailID,FItemClassID,FItemID) "
				+ "values ("+maxFDetailID+",2039,"+voucherEntryvo.getProject()+")";
			DBUtil.excuteUpdate(insertItemDetail);
			DBUtil.excuteUpdate(insertItemDetailV);
			voucherEntryvo.setFDetailID(maxFDetailID.toString());
		}else{
			throw new Exception("查询辅助核算项目异常.请执行sql检查:" + sql);
		}
		
		voucherEntryvo.setFAmount(amount.toString());
		
		return voucherEntryvo;
	}
	/**
	 * 获取凭证分录.
	 * 直销申购费+公司申购费	应收账款/应收申购费	手续费及佣金收入/基金销售手续费收入/申购费-前端
	 * @param str
	 * @return
	 * @throws Exception 
	 */
	private VoucherEntryVO getVoucher2(String[] str) throws Exception{
		if(str == null || str.length ==0){
			return null;
		}
		BigDecimal amount = new BigDecimal(str[3] == null || "".equals(str[3]) ? "0":str[3].replaceAll(",", "")).add(new BigDecimal(str[9] == null || "".equals(str[9]) ? "0":str[9].replaceAll(",", "")));
		if(amount.doubleValue() <= 0){
			return null;
		}
		VoucherEntryVO voucherEntryvo = new VoucherEntryVO();
		String sql = "select * from t_item where fnumber like '%" + str[0].split("-")[0] + "'";
		List templist = DBUtil.querySql(sql);
		if(templist != null && templist.size() == 1){
			voucherEntryvo.setProject(((HashMap)templist.get(0)).get("FItemID").toString());
		}else if(templist == null || templist.size() == 0){
			throw new Exception("未找到编码包含" + str[0] + "的辅助核算项目!");
		}else{
			throw new Exception("查询辅助核算项目异常.请执行sql检查:" + sql);
		}
		sql = "select FDetailID from t_ItemDetail where FDetailCount=1 and F2039=" + voucherEntryvo.getProject();
		templist = DBUtil.querySql(sql);
		if(templist != null && templist.size() == 1){
			voucherEntryvo.setFDetailID(((HashMap)templist.get(0)).get("FDetailID").toString());
		}else if(templist == null || templist.size() == 0){
			//没有辅助核算信息.插入t_ItemDetail和t_ItemDetailV表辅助核算信息.
			Object  maxFDetailID = DBUtil.querySqlUniqueResult("select max(FDetailID)+1 from t_ItemDetail ");
			String insertItemDetail = "insert into t_ItemDetail(FDetailID,FDetailCount,F1,F2,F3,F4,F5,F8,F9,F10,F14,F2001,F2002,F2003,F2004,F2014,F2023,F2021,F2024,F2026,F2027,F2028,F2029,F2030,F2035,F2036,F2039,F2040,F2041) "
				+ "values ("+maxFDetailID+",1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,"+voucherEntryvo.getProject()+",0,0)";
			String insertItemDetailV = "insert into t_ItemDetailV(FDetailID,FItemClassID,FItemID) "
				+ "values ("+maxFDetailID+",2039,"+voucherEntryvo.getProject()+")";
			DBUtil.excuteUpdate(insertItemDetail);
			DBUtil.excuteUpdate(insertItemDetailV);
			voucherEntryvo.setFDetailID(maxFDetailID.toString());
		}else{
			throw new Exception("查询辅助核算项目异常.请执行sql检查:" + sql);
		}
		voucherEntryvo.setFAmount(amount.toString());
		return voucherEntryvo;
	}
	
	/**
	 * 获取凭证分录.
	 * 直销赎回费+公司赎回费	应收账款/应收赎回费	手续费及佣金收入/基金销售手续费收入/赎回费
	 * @param str
	 * @return
	 * @throws Exception 
	 */
	private VoucherEntryVO getVoucher3(String[] str) throws Exception{
		if(str == null || str.length ==0){
			return null;
		}
		BigDecimal amount = new BigDecimal(str[4] == null || "".equals(str[4]) ? "0":str[4].replaceAll(",", "")).add(new BigDecimal(str[10] == null || "".equals(str[10]) ? "0":str[10].replaceAll(",", "")));
		if(amount.doubleValue() <= 0){
			return null;
		}
		VoucherEntryVO voucherEntryvo = new VoucherEntryVO();
		String sql = "select * from t_item where fnumber like '%" + str[0].split("-")[0] + "'";
		List templist = DBUtil.querySql(sql);
		if(templist != null && templist.size() == 1){
			voucherEntryvo.setProject(((HashMap)templist.get(0)).get("FItemID").toString());
		}else if(templist == null || templist.size() == 0){
			throw new Exception("未找到编码包含" + str[0] + "的辅助核算项目!");
		}else{
			throw new Exception("查询辅助核算项目异常.请执行sql检查:" + sql);
		}
		sql = "select FDetailID from t_ItemDetail where FDetailCount=1 and F2039=" + voucherEntryvo.getProject();
		templist = DBUtil.querySql(sql);
		if(templist != null && templist.size() == 1){
			voucherEntryvo.setFDetailID(((HashMap)templist.get(0)).get("FDetailID").toString());
		}else if(templist == null || templist.size() == 0){
			//没有辅助核算信息.插入t_ItemDetail和t_ItemDetailV表辅助核算信息.
			Object  maxFDetailID = DBUtil.querySqlUniqueResult("select max(FDetailID)+1 from t_ItemDetail ");
			String insertItemDetail = "insert into t_ItemDetail(FDetailID,FDetailCount,F1,F2,F3,F4,F5,F8,F9,F10,F14,F2001,F2002,F2003,F2004,F2014,F2023,F2021,F2024,F2026,F2027,F2028,F2029,F2030,F2035,F2036,F2039,F2040,F2041) "
				+ "values ("+maxFDetailID+",1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,"+voucherEntryvo.getProject()+",0,0)";
			String insertItemDetailV = "insert into t_ItemDetailV(FDetailID,FItemClassID,FItemID) "
				+ "values ("+maxFDetailID+",2039,"+voucherEntryvo.getProject()+")";
			DBUtil.excuteUpdate(insertItemDetail);
			DBUtil.excuteUpdate(insertItemDetailV);
			voucherEntryvo.setFDetailID(maxFDetailID.toString());
		}else{
			throw new Exception("查询辅助核算项目异常.请执行sql检查:" + sql);
		}
		voucherEntryvo.setFAmount(amount.toString());
		return voucherEntryvo;
	}
	
	/**
	 * 获取凭证分录.
	 * 直销转换费+直销补差费+公司转换费+公司补差费	应收账款/应收转换费	手续费及佣金收入/基金销售手续费收入/转换费
	 * @param str
	 * @return
	 * @throws Exception 
	 */
	private VoucherEntryVO getVoucher4(String[] str) throws Exception{
		if(str == null || str.length ==0){
			return null;
		}
		BigDecimal amount = new BigDecimal(str[5] == null || "".equals(str[5]) ? "0":str[5].replaceAll(",", ""))
								.add(new BigDecimal(str[6] == null || "".equals(str[6]) ? "0":str[6].replaceAll(",", "")))
								.add(new BigDecimal(str[11] == null || "".equals(str[11]) ? "0":str[11].replaceAll(",", "")))
								.add(new BigDecimal(str[12] == null || "".equals(str[12]) ? "0":str[12].replaceAll(",", "")));
		if(amount.doubleValue() <= 0){
			return null;
		}
		VoucherEntryVO voucherEntryvo = new VoucherEntryVO();
		String sql = "select * from t_item where fnumber like '%" + str[0].split("-")[0] + "'";
		List templist = DBUtil.querySql(sql);
		if(templist != null && templist.size() == 1){
			voucherEntryvo.setProject(((HashMap)templist.get(0)).get("FItemID").toString());
		}else if(templist == null || templist.size() == 0){
			throw new Exception("未找到编码包含" + str[0] + "的辅助核算项目!");
		}else{
			throw new Exception("查询辅助核算项目异常.请执行sql检查:" + sql);
		}
		sql = "select FDetailID from t_ItemDetail where FDetailCount=1 and F2039=" + voucherEntryvo.getProject();
		templist = DBUtil.querySql(sql);
		if(templist != null && templist.size() == 1){
			voucherEntryvo.setFDetailID(((HashMap)templist.get(0)).get("FDetailID").toString());
		}else if(templist == null || templist.size() == 0){
			//没有辅助核算信息.插入t_ItemDetail和t_ItemDetailV表辅助核算信息.
			Object  maxFDetailID = DBUtil.querySqlUniqueResult("select max(FDetailID)+1 from t_ItemDetail ");
			String insertItemDetail = "insert into t_ItemDetail(FDetailID,FDetailCount,F1,F2,F3,F4,F5,F8,F9,F10,F14,F2001,F2002,F2003,F2004,F2014,F2023,F2021,F2024,F2026,F2027,F2028,F2029,F2030,F2035,F2036,F2039,F2040,F2041) "
				+ "values ("+maxFDetailID+",1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,"+voucherEntryvo.getProject()+",0,0)";
			String insertItemDetailV = "insert into t_ItemDetailV(FDetailID,FItemClassID,FItemID) "
				+ "values ("+maxFDetailID+",2039,"+voucherEntryvo.getProject()+")";
			DBUtil.excuteUpdate(insertItemDetail);
			DBUtil.excuteUpdate(insertItemDetailV);
			voucherEntryvo.setFDetailID(maxFDetailID.toString());
		}else{
			throw new Exception("查询辅助核算项目异常.请执行sql检查:" + sql);
		}
		voucherEntryvo.setFAmount(amount.toString());
		return voucherEntryvo;
	}
	
	/**
	 * 获取凭证分录.
	 * 公司后端申购费	应收账款/应收申购费-后端	手续费及佣金收入/基金销售手续费收入/申购费-后端
	 * @param str
	 * @return
	 * @throws Exception 
	 */
	private VoucherEntryVO getVoucher5(String[] str) throws Exception{
		if(str == null || str.length ==0){
			return null;
		}
		BigDecimal amount = new BigDecimal(str[13] == null || "".equals(str[13]) ? "0":str[13].replaceAll(",", "")); 
		if(amount.doubleValue() <= 0){
			return null;
		}
		VoucherEntryVO voucherEntryvo = new VoucherEntryVO();
		String sql = "select * from t_item where fnumber like '%" + str[0].split("-")[0] + "'";
		List templist = DBUtil.querySql(sql);
		if(templist != null && templist.size() == 1){
			voucherEntryvo.setProject(((HashMap)templist.get(0)).get("FItemID").toString());
		}else if(templist == null || templist.size() == 0){
			throw new Exception("未找到编码包含" + str[0] + "的辅助核算项目!");
		}else{
			throw new Exception("查询辅助核算项目异常.请执行sql检查:" + sql);
		}
		sql = "select FDetailID from t_ItemDetail where FDetailCount=1 and F2039=" + voucherEntryvo.getProject();
		templist = DBUtil.querySql(sql);
		if(templist != null && templist.size() == 1){
			voucherEntryvo.setFDetailID(((HashMap)templist.get(0)).get("FDetailID").toString());
		}else if(templist == null || templist.size() == 0){
			//没有辅助核算信息.插入t_ItemDetail和t_ItemDetailV表辅助核算信息.
			Object  maxFDetailID = DBUtil.querySqlUniqueResult("select max(FDetailID)+1 from t_ItemDetail ");
			String insertItemDetail = "insert into t_ItemDetail(FDetailID,FDetailCount,F1,F2,F3,F4,F5,F8,F9,F10,F14,F2001,F2002,F2003,F2004,F2014,F2023,F2021,F2024,F2026,F2027,F2028,F2029,F2030,F2035,F2036,F2039,F2040,F2041) "
				+ "values ("+maxFDetailID+",1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,"+voucherEntryvo.getProject()+",0,0)";
			String insertItemDetailV = "insert into t_ItemDetailV(FDetailID,FItemClassID,FItemID) "
				+ "values ("+maxFDetailID+",2039,"+voucherEntryvo.getProject()+")";
			DBUtil.excuteUpdate(insertItemDetail);
			DBUtil.excuteUpdate(insertItemDetailV);
			voucherEntryvo.setFDetailID(maxFDetailID.toString());
		}else{
			throw new Exception("查询辅助核算项目异常.请执行sql检查:" + sql);
		}
		voucherEntryvo.setFAmount(amount.toString());
		return voucherEntryvo;
	}
	
	/**
	 * 获取凭证分录.
	 * 直销销售服务费	应收账款　/应收销售服务费	销售服务收入/基金
	 * @param str
	 * @return
	 * @throws Exception 
	 */
	private VoucherEntryVO getVoucher6(String[] str) throws Exception{
		if(str == null || str.length ==0){
			return null;
		}
		BigDecimal amount = new BigDecimal(str[7] == null || "".equals(str[7]) ? "0":str[7].replaceAll(",", "")); 
		if(amount.doubleValue() <= 0){
			return null;
		}
		VoucherEntryVO voucherEntryvo = new VoucherEntryVO();
		String sql = "select * from t_item where fnumber like '%" + str[0].split("-")[0] + "'";
		List templist = DBUtil.querySql(sql);
		if(templist != null && templist.size() == 1){
			voucherEntryvo.setProject(((HashMap)templist.get(0)).get("FItemID").toString());
		}else if(templist == null || templist.size() == 0){
			throw new Exception("未找到编码包含" + str[0] + "的辅助核算项目!");
		}else{
			throw new Exception("查询辅助核算项目异常.请执行sql检查:" + sql);
		}
		sql = "select FDetailID from t_ItemDetail where FDetailCount=1 and F2039=" + voucherEntryvo.getProject();
		templist = DBUtil.querySql(sql);
		if(templist != null && templist.size() == 1){
			voucherEntryvo.setFDetailID(((HashMap)templist.get(0)).get("FDetailID").toString());
		}else if(templist == null || templist.size() == 0){
			//没有辅助核算信息.插入t_ItemDetail和t_ItemDetailV表辅助核算信息.
			Object  maxFDetailID = DBUtil.querySqlUniqueResult("select max(FDetailID)+1 from t_ItemDetail ");
			String insertItemDetail = "insert into t_ItemDetail(FDetailID,FDetailCount,F1,F2,F3,F4,F5,F8,F9,F10,F14,F2001,F2002,F2003,F2004,F2014,F2023,F2021,F2024,F2026,F2027,F2028,F2029,F2030,F2035,F2036,F2039,F2040,F2041) "
				+ "values ("+maxFDetailID+",1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,"+voucherEntryvo.getProject()+",0,0)";
			String insertItemDetailV = "insert into t_ItemDetailV(FDetailID,FItemClassID,FItemID) "
				+ "values ("+maxFDetailID+",2039,"+voucherEntryvo.getProject()+")";
			DBUtil.excuteUpdate(insertItemDetail);
			DBUtil.excuteUpdate(insertItemDetailV);
			voucherEntryvo.setFDetailID(maxFDetailID.toString());
		}else{
			throw new Exception("查询辅助核算项目异常.请执行sql检查:" + sql);
		}
		voucherEntryvo.setFAmount(amount.toString());
		return voucherEntryvo;
	}
	
	/**
	 * 导入数据到K3系统.
	 * @param volist
	 * @return
	 * @throws Exception
	 */
	public int impVO2SysVoucher(List volist,MainStart mainUI) throws Exception{
		
		if(volist == null || volist.size() == 0){return 0;}
		
		Connection conn = null;
		Statement stat = null;
		try {
			conn = DBUtil.getConnection();
			conn.getAutoCommit();
			conn.setAutoCommit(false);
			stat = conn.createStatement();
			
			String voucherSql = null;
			String jvoucherentrySql = null;
			String dvoucherentrySql = null;
			
			//凭证ID.
			int FVoucherID = Integer.parseInt(DBUtil.querySqlUniqueResult("select FMaxNum+1 from icmaxnum where FTableName='t_voucher'").toString());
			//凭证号
			VoucherEntryVO tempvo = (VoucherEntryVO) volist.get(0);
			Object obj = DBUtil.querySqlUniqueResult("select max(fnumber)+1 from t_voucher where FYear= "+tempvo.getFYear()+" and FPeriod="+tempvo.getFPeriod());
			int fnumber = Integer.parseInt(obj == null ? "1":obj.toString());
			
			Calendar cal = Calendar.getInstance();
			cal.set(Calendar.YEAR,Integer.parseInt(tempvo.getFYear()));
			cal.set(Calendar.MONTH,Integer.parseInt(tempvo.getFPeriod()) - 1);
			cal.set(Calendar.DAY_OF_MONTH, cal.getMaximum(Calendar.DAY_OF_MONTH));
			DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
			String dateStr = df.format(cal.getTime());
			BigDecimal amount = new BigDecimal("0");
			for (int i = 0; i < volist.size(); i++) {
				VoucherEntryVO vevo = (VoucherEntryVO) volist.get(i);
				
				jvoucherentrySql = "insert into t_VoucherEntry(FBrNo,FVoucherID,FEntryID,FExplanation,FAccountID,FDetailID,FCurrencyID,FExchangeRate,FDC,FAmountFor,FAmount,FQuantity,FMeasureUnitID,FUnitPrice,FInternalInd,FAccountID2,FSettleTypeID,FSettleNo,FTransNo,FCashFlowItem,FTaskID,FResourceID,FExchangeRateType,FSideEntryID) "
					+ "values(0," + FVoucherID + ","+i*2+",'计提" + vevo.getFPeriod() + "月手续费收入'," + vevo.getJAccountID() + "," + vevo.getFDetailID() + ",1,1,1," + vevo.getFAmount() + "," + vevo.getFAmount() + ",0,0,0,null," + vevo.getDAccountID() + ",0,null,null,0,0,0,1,"+(i*2+1)+")";
				dvoucherentrySql = "insert into t_VoucherEntry(FBrNo,FVoucherID,FEntryID,FExplanation,FAccountID,FDetailID,FCurrencyID,FExchangeRate,FDC,FAmountFor,FAmount,FQuantity,FMeasureUnitID,FUnitPrice,FInternalInd,FAccountID2,FSettleTypeID,FSettleNo,FTransNo,FCashFlowItem,FTaskID,FResourceID,FExchangeRateType,FSideEntryID) "
					+ "values(0," + FVoucherID + ","+(i*2+1)+",'计提" + vevo.getFPeriod() + "月手续费收入'," + vevo.getDAccountID() + "," + vevo.getFDetailID() + ",1,1,0," + vevo.getFAmount() + "," + vevo.getFAmount() + ",0,0,0,null," + vevo.getJAccountID() + ",0,null,null,0,0,0,1,"+i*2+")";
				amount = amount.add(new BigDecimal(vevo.getFAmount()));
				stat.execute(jvoucherentrySql);
				stat.execute(dvoucherentrySql);
			}

			voucherSql = "insert into t_Voucher(FBrNo,FVoucherID,FDate,FYear,FPeriod,FGroupID,FNumber,FReference,FExplanation,FAttachments,FEntryCount,FDebitTotal,FCreditTotal,FInternalInd,FChecked,FPosted,FPreparerID,FCheckerID,	FPosterID,FCashierID,	FHandler,FOwnerGroupID,FObjectName,FParameter,FSerialNum,FTranType,FTransDate,FFrameWorkID,FApproveID,FFootNote,UUID) "
				+ "values (0," + FVoucherID + ",'" + dateStr + " 00:00:00.000'," + tempvo.getFYear() + "," + tempvo.getFPeriod() + ",1," + fnumber + ",null,'计提" + tempvo.getFPeriod() + "月手续费收入',0,2," + amount + "," + amount + ",null,0,0," + tempvo.getFPreparerID() + ",-1,-1,-1,null,0,null,null," + fnumber + ",0,'" + dateStr + " 00:00:00.000',	-1,	-1,'','"+UUID.randomUUID()+"')";
			stat.execute(voucherSql);

			//更新配置表
			String updateSql = "update icmaxnum set FMaxNum="+FVoucherID+" where FTableName='t_voucher'" ;
			stat.execute(updateSql);
			
			conn.commit();
		} catch (Exception e) {
			conn.rollback();
			e.printStackTrace();
			throw e;
		}finally{
            if (stat != null) {
                try {
                    stat.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
		}
		
		
		return 0;
	}

}
