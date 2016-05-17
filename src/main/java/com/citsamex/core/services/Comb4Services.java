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

import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import com.citsamex.core.startup.MainStart;
import com.citsamex.core.util.DBUtil;
import com.citsamex.core.util.DateUtil;
import com.citsamex.core.util.ExcelUtil;
import com.citsamex.core.vo.VoucherEntryVO;

/**
 * COMBOVALUE4 = "尾随佣金汇总表_比例";
 * @author fans.fan
 *
 */
public class Comb4Services extends SuperServices {
	
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
			for (int i = 7, rows = sht.getLastRowNum(); i < rows; i++) {
				row = sht.getRow(i);
				value = ExcelUtil.getCellValue(row.getCell(12));
				if (value != null && value.contains("基金代码")) {
					continue;
				}else if(value == null || "".equals(value.trim())){
					continue;
				}
					
				String[] vo = new String[7];
				vo[0] = ExcelUtil.getCellValue(row.getCell(6));
				vo[1] = ExcelUtil.getCellValue(row.getCell(9));
				vo[2] = ExcelUtil.getCellValue(row.getCell(12));
				vo[3] = ExcelUtil.getCellValue(row.getCell(18));
				vo[4] = ExcelUtil.getCellValue(row.getCell(21));
				vo[5] = ExcelUtil.getCellValue(row.getCell(27));
				vo[6] = ExcelUtil.getCellValue(row.getCell(32));
//				for (int j = 6; j < 6+vo.length; j++) {
//					vo[j] = ExcelUtil.getCellValue(row.getCell(j));
//				}
				voList.add(vo);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return voList;
	}
	

	/**
	 * 转换文件
	 * @param list 
	 * 销售商				基金代码			基金名称				托管金额累计							托管份额累计						平均托管金额					尾随佣金			
	 * @return
	 * @throws Exception 
	 */
	public List<VoucherEntryVO> convertFile2VO(ArrayList<String[]> list,MainStart mainUI) throws Exception{
		if(list == null || list.size() == 0){
			return null;
		}
		
		List<VoucherEntryVO> voList = new ArrayList<VoucherEntryVO>();
		List templist = null;
		String jaccsubjid = getAccsubjid("6602.02.07");
		String daccsubjid = getAccsubjid("2202.01");
		String userid = getUserId(mainUI.usernameTextField.getText());
		
		//校验业务期间.
		int year = Integer.parseInt(mainUI.yearComboBox.getSelectedItem().toString());
		int month = Integer.parseInt(mainUI.monthComboBox.getSelectedItem().toString());
		checkYearAndMonth(year, month);
		
		String deptid = null;
		
		for(String[] str : list){
			if(str[6]==null || "".equals(str[6]))continue;
			if(Double.parseDouble(str[6].replaceAll(",", "")) == 0.0d) continue;
			
			VoucherEntryVO voucherEntryvo = new VoucherEntryVO();
			//检查第一个辅助核算项	销售商			
			String sql = "select FItemID from t_item where FItemClassID=8 and fnumber like '%" + str[0] + "' and fname like '%"+str[1]+"%' ";
			voucherEntryvo.setFxss(getFItemID(sql, str[0]));
			//检查第二个辅助核算项	基金代码
			sql = "select FItemID from t_item where fnumber like '%" + str[2] + "'";
			voucherEntryvo.setProject(getFItemID(sql, str[2]));
			//检查第三个辅助核算项	部门
			sql = "select FItemID from t_item where fnumber = '04.001' and FItemClassID=2";
			deptid = getFItemID(sql, "04.001");
			
			//借方科目：管理费用/市场营销开支/尾随佣金		F2部门/F8供应商/F2039工程项目
			sql = "select FDetailID from t_ItemDetail where FDetailCount=3 and F2="+deptid+" and F8=" + voucherEntryvo.getFxss() + "and F2039=" + voucherEntryvo.getProject();
			templist = DBUtil.querySql(sql);
			if(templist != null && templist.size() == 1){
				voucherEntryvo.setFJDetailID(((HashMap)templist.get(0)).get("FDetailID").toString());
			}else if(templist == null || templist.size() == 0){
				//没有辅助核算信息.插入t_ItemDetail和t_ItemDetailV表辅助核算信息.
				Object  maxFDetailID = DBUtil.querySqlUniqueResult("select max(FDetailID)+1 from t_ItemDetail ");
				String insertItemDetail = "insert into t_ItemDetail(FDetailID,FDetailCount,F1,F2,F3,F4,F5,F8,F9,F10,F14,F2001,F2002,F2003,F2004,F2014,F2023,F2021,F2024,F2026,F2027,F2028,F2029,F2030,F2035,F2036,F2039,F2040,F2041) "
					+ "values ("+maxFDetailID+",3,0,"+deptid+",0,0,0,"+voucherEntryvo.getFxss()+",0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,"+voucherEntryvo.getProject()+",0,0)";
				String insertItemDetailV = "insert into t_ItemDetailV(FDetailID,FItemClassID,FItemID) "
					+ "values ("+maxFDetailID+",8,"+voucherEntryvo.getProject()+")";
				DBUtil.excuteUpdate(insertItemDetail);
				DBUtil.excuteUpdate(insertItemDetailV);
				insertItemDetailV = "insert into t_ItemDetailV(FDetailID,FItemClassID,FItemID) "
					+ "values ("+maxFDetailID+",2,"+deptid+")";
				DBUtil.excuteUpdate(insertItemDetailV);
				insertItemDetailV = "insert into t_ItemDetailV(FDetailID,FItemClassID,FItemID) "
					+ "values ("+maxFDetailID+",2039,"+voucherEntryvo.getProject()+")";
				DBUtil.excuteUpdate(insertItemDetailV);
				voucherEntryvo.setFJDetailID(maxFDetailID.toString());
			}else{
				throw new Exception("查询辅助核算项目异常.请执行sql检查:" + sql);
			}
			
			//贷方科目：应付账款/尾随佣金		辅助核算项目是销售商
			sql = "select FDetailID from t_ItemDetail where FDetailCount=1 and F8=" + voucherEntryvo.getFxss();
			templist = DBUtil.querySql(sql);
			if(templist != null && templist.size() == 1){
				voucherEntryvo.setFDDetailID(((HashMap)templist.get(0)).get("FDetailID").toString());
			}else if(templist == null || templist.size() == 0){
				//没有辅助核算信息.插入t_ItemDetail和t_ItemDetailV表辅助核算信息.
				Object  maxFDetailID = DBUtil.querySqlUniqueResult("select max(FDetailID)+1 from t_ItemDetail ");
				String insertItemDetail = "insert into t_ItemDetail(FDetailID,FDetailCount,F1,F2,F3,F4,F5,F8,F9,F10,F14,F2001,F2002,F2003,F2004,F2014,F2023,F2021,F2024,F2026,F2027,F2028,F2029,F2030,F2035,F2036,F2039,F2040,F2041) "
					+ "values ("+maxFDetailID+",1,0,0,0,0,0,"+voucherEntryvo.getFxss()+",0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0)";
				String insertItemDetailV = "insert into t_ItemDetailV(FDetailID,FItemClassID,FItemID) "
					+ "values ("+maxFDetailID+",8,"+voucherEntryvo.getFxss()+")";
				DBUtil.excuteUpdate(insertItemDetail);
				DBUtil.excuteUpdate(insertItemDetailV);
				voucherEntryvo.setFDDetailID(maxFDetailID.toString());
			}else{
				throw new Exception("查询辅助核算项目异常.请执行sql检查:" + sql);
			}
			
			voucherEntryvo.setFYear(String.valueOf(year));
			voucherEntryvo.setFPeriod(String.valueOf(month));
			voucherEntryvo.setFPreparerID(userid);
			voucherEntryvo.setFExplanation("计提" + month + "月尾随佣金");
			voucherEntryvo.setJAccountID(jaccsubjid);
			voucherEntryvo.setDAccountID(daccsubjid);
			//str[6]传入的是价税合计,系统要是的不含税价格 不含税价格=价税合计/1.06*0.06
			voucherEntryvo.setFAmount(new BigDecimal(str[6].replaceAll(",","")).subtract(new BigDecimal(str[6].replaceAll(",","")).multiply(new BigDecimal(0.06)).divide(new BigDecimal(1.06), 2, 4)).toString());
			voList.add(voucherEntryvo);
		}
		
		if (voList == null || voList.isEmpty()) {
			mainUI.remark.setText("没有需要导入的数据.");
		}
		return voList;
	}
	
	/**
	 * 导入数据到K3系统.
	 * @param volist
	 * @return
	 * @throws Exception
	 */
	public int impVO2SysVoucher(List volist,MainStart mainUI) throws Exception{
		
		if(volist == null || volist.size() == 0){
			return 0;
		}
		
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
			
			BigDecimal amount = new BigDecimal("0");
			for (int i = 0; i < volist.size(); i++) {
				VoucherEntryVO vevo = (VoucherEntryVO) volist.get(i);
				
				jvoucherentrySql = "insert into t_VoucherEntry(FBrNo,FVoucherID,FEntryID,FExplanation,FAccountID,FDetailID,FCurrencyID,FExchangeRate,FDC,FAmountFor,FAmount,FQuantity,FMeasureUnitID,FUnitPrice,FInternalInd,FAccountID2,FSettleTypeID,FSettleNo,FTransNo,FCashFlowItem,FTaskID,FResourceID,FExchangeRateType,FSideEntryID) "
					+ "values(0," + FVoucherID + ","+i*2+",'" + vevo.getFExplanation() + "'," + vevo.getJAccountID() + "," + vevo.getFJDetailID() + ",1,1,1," + vevo.getFAmount().replaceAll(",","") + "," + vevo.getFAmount().replaceAll(",","") + ",0,0,0,null," + vevo.getDAccountID() + ",0,null,null,0,0,0,1,"+(i*2+1)+")";
				dvoucherentrySql = "insert into t_VoucherEntry(FBrNo,FVoucherID,FEntryID,FExplanation,FAccountID,FDetailID,FCurrencyID,FExchangeRate,FDC,FAmountFor,FAmount,FQuantity,FMeasureUnitID,FUnitPrice,FInternalInd,FAccountID2,FSettleTypeID,FSettleNo,FTransNo,FCashFlowItem,FTaskID,FResourceID,FExchangeRateType,FSideEntryID) "
					+ "values(0," + FVoucherID + ","+(i*2+1)+",'" + vevo.getFExplanation() + "'," + vevo.getDAccountID() + "," + vevo.getFDDetailID() + ",1,1,0," + vevo.getFAmount().replaceAll(",","") + "," + vevo.getFAmount().replaceAll(",","") + ",0,0,0,null," + vevo.getJAccountID() + ",0,null,null,0,0,0,1,"+i*2+")";
				amount = amount.add(new BigDecimal(vevo.getFAmount().replaceAll(",", "")));
				stat.execute(jvoucherentrySql);
				stat.execute(dvoucherentrySql);
			}
			
			String dateStr = DateUtil.getDateStr(tempvo.getFYear(), tempvo.getFPeriod());
			voucherSql = "insert into t_Voucher(FBrNo,FVoucherID,FDate,FYear,FPeriod,FGroupID,FNumber,FReference,FExplanation,FAttachments,FEntryCount,FDebitTotal,FCreditTotal,FInternalInd,FChecked,FPosted,FPreparerID,FCheckerID,	FPosterID,FCashierID,	FHandler,FOwnerGroupID,FObjectName,FParameter,FSerialNum,FTranType,FTransDate,FFrameWorkID,FApproveID,FFootNote,UUID) "
				+ "values (0," + FVoucherID + ",'" + dateStr + " 00:00:00.000'," + tempvo.getFYear() + "," + tempvo.getFPeriod() + ",1," + fnumber + ",null,'" + tempvo.getFExplanation() + "',0,2," + amount + "," + amount + ",null,0,0," + tempvo.getFPreparerID() + ",-1,-1,-1,null,0,null,null," + fnumber + ",0,'" + dateStr + " 00:00:00.000',	-1,	-1,'','"+UUID.randomUUID()+"')";
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
			DBUtil.close(stat);
			DBUtil.close(conn);
		}
		return 0;
	}

}
