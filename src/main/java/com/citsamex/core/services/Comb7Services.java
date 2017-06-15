package com.citsamex.core.services;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
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
import com.citsamex.core.util.StringUtil;
import com.citsamex.core.vo.VoucherEntryVO;

/**
 *  COMBOVALUE7 = "公司管理费及业绩报酬划拨明细表";
 * @author fans.fan
 *
 */
public class Comb7Services extends SuperServices {
	
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
			for (int i = 6, rows = sht.getLastRowNum(); i < rows; i++) {
				row = sht.getRow(i);
				value = ExcelUtil.getCellValue(row.getCell(0));
				if(StringUtil.isEmpty(value)){
					break;
				}
					
				String[] vo = new String[10];
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
	
	private static String jaccsubjid1 = null;		//银行存款
	private static String jaccsubjid2 = null;		//风险金专户
	private static String daccsubjid1 = null;		//专户管理费收入
	private static String daccsubjid21 = null;		//专户管理费收入 	 管理费用这个
	private static String daccsubjid22 = null;		//专户  业绩报酬用这个
	
	/**
	 * 转换文件
	 * @param list 
	 * "日期","基金代码","基金名称","管理费汇总","托管费","销售费用","保证金"
	 * @return
	 * @throws Exception 
	 */
	public List convertFile2VO(ArrayList<String[]> list,MainStart mainUI) throws Exception{
		if(list == null || list.size() == 0){
			return null;
		}
		
		List voList = new ArrayList();
		List templist = null;
		jaccsubjid1 = getAccsubjid("1002.01.01.04");		//银行存款
		jaccsubjid2 = getAccsubjid("1002.01.01.06");		//风险金专户
		daccsubjid1 = getAccsubjid("2221.04.02");		//应交税费-增值税-销项税额
		daccsubjid21 = getAccsubjid("6001.01.02");		//专户管理费收入 	 管理费用这个
		daccsubjid22 = getAccsubjid("6001.02.02");		//专户  业绩报酬用这个
		
		//2221.04.02   应交税费-增值税-销项税额
		String userid = getUserId(mainUI.usernameTextField.getText());
		
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		
		String projectId = null, deptid = null, empid = null, sql = null,fDetailID = null;
		for(int i = 0;i<list.size();i++){
			String[] str = (String[]) list.get(i);
			
			//校验业务期间.
			Date date = df.parse(str[0]);
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(date);
			int year = calendar.get(Calendar.YEAR);
			int month = calendar.get(Calendar.MONTH) + 1;
			checkYearAndMonth(year, month);
			
			//检查第一个辅助核算项	工程项目
			sql = "select FItemID from t_item where fnumber = '" + str[1] + "' and FItemClassID=2039";
			projectId = getFItemID(sql, str[1]);
			//检查第一个辅助核算项	部门			
			sql = "select FItemID from t_item where fnumber = '" + str[4] + "'  and FItemClassID=2";
			deptid = getFItemID(sql, str[4]);
			//检查第三个辅助核算项	职员
			sql = "select FItemID from t_item where fnumber = '"+str[5]+"' and FItemClassID=3";
			empid = getFItemID(sql, str[5]);
			
			//贷方科目：主营业务收入/管理费收入/专户管理费收入		2部门 3职员2039工程项目
			sql = "select  min(FDetailID) as FDetailID from t_ItemDetail where FDetailCount=3 and F2=" + deptid + " and F3=" + empid + " and F2039=" + projectId;
			templist = DBUtil.querySql(sql);
			if(templist != null && templist.size() == 1 && ((HashMap)templist.get(0)).get("FDetailID") != null){
				fDetailID = ((HashMap)templist.get(0)).get("FDetailID").toString();
			} else {
				//没有辅助核算信息.插入t_ItemDetail和t_ItemDetailV表辅助核算信息.
				Object  maxFDetailID = DBUtil.querySqlUniqueResult("select max(FDetailID)+1 from t_ItemDetail ");
				String insertItemDetail = "insert into t_ItemDetail(FDetailID,FDetailCount,F1,F2,F3,F4,F5,F8,F9,F10,F14,F2001,F2002,F2003,F2004,F2014,F2023,F2021,F2024,F2026,F2027,F2028,F2029,F2030,F2035,F2036,F2039,F2040,F2041) "
					+ "values ("+maxFDetailID+",3,0,"+deptid+","+empid+",0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,"+ projectId +",0,0)";
				String insertItemDetailV = "insert into t_ItemDetailV(FDetailID,FItemClassID,FItemID) "
					+ "values ("+maxFDetailID+",2,"+deptid+")";
				DBUtil.excuteUpdate(insertItemDetail);
				DBUtil.excuteUpdate(insertItemDetailV);
				insertItemDetailV = "insert into t_ItemDetailV(FDetailID,FItemClassID,FItemID) "
					+ "values ("+maxFDetailID+",3,"+empid+")";
				DBUtil.excuteUpdate(insertItemDetailV);
				insertItemDetailV = "insert into t_ItemDetailV(FDetailID,FItemClassID,FItemID) "
					+ "values ("+maxFDetailID+",2039,"+projectId+")";
				DBUtil.excuteUpdate(insertItemDetailV);
				fDetailID = maxFDetailID.toString();
			}
			
			VoucherEntryVO voucherEntryvo = new VoucherEntryVO();
			voucherEntryvo.setFYear(String.valueOf(year));
			voucherEntryvo.setFPeriod(String.valueOf(month));
			voucherEntryvo.setFDay(str[0]);
			voucherEntryvo.setFPreparerID(userid);
			voucherEntryvo.setFExplanation(str[2] + "管理费收入");
//			voucherEntryvo.setJAccountID(jaccsubjid1);
//			voucherEntryvo.setDAccountID(daccsubjid1);
			voucherEntryvo.setFJDetailID("0");
			voucherEntryvo.setFDDetailID(fDetailID);
			
			//管理费
			double glf = getAmount(str[7]);
			//业绩报酬
			double yjbc = getAmount(str[8]);
			//风险金
			double fxj = getAmount(str[9]);
			//管理费 + 业绩报酬 — 风险金
			voucherEntryvo.setFAmount(new BigDecimal(glf + yjbc).setScale(2, 4).toString());
			voucherEntryvo.setFxj(fxj+ "");
			voucherEntryvo.setGlf(glf > 0);
			
			
			voList.add(voucherEntryvo);
			
//			//管理费
//			if(!StringUtil.isEmpty(str[7]) && Double.parseDouble(str[7]) != 0.0d){
//				VoucherEntryVO voucherEntryvo = new VoucherEntryVO();
//				voucherEntryvo.setFYear(String.valueOf(year));
//				voucherEntryvo.setFPeriod(String.valueOf(month));
//				voucherEntryvo.setFDay(str[0]);
//				voucherEntryvo.setFPreparerID(userid);
//				voucherEntryvo.setFExplanation(str[2] + "管理费收入");
////				voucherEntryvo.setJAccountID(jaccsubjid1);
////				voucherEntryvo.setDAccountID(daccsubjid1);
//				voucherEntryvo.setFJDetailID("0");
//				voucherEntryvo.setFDDetailID(fDetailID);
//				if (!StringUtil.isEmpty(str[9]) && Double.parseDouble(str[9]) != 0.0d) {
//					//管理费—风险金
//					voucherEntryvo.setFAmount(new BigDecimal(Double.parseDouble(str[7]) - Double.parseDouble(str[9])).setScale(2, 4).toString());
//				} else {
//					voucherEntryvo.setFAmount(str[7]);
//				}
//				voList.add(voucherEntryvo);
//			} else if(!StringUtil.isEmpty(str[9]) && Double.parseDouble(str[9]) != 0.0d){
//				//风险金
//				VoucherEntryVO voucherEntryvo = new VoucherEntryVO();
//				voucherEntryvo.setFYear(String.valueOf(year));
//				voucherEntryvo.setFPeriod(String.valueOf(month));
//				voucherEntryvo.setFDay(str[0]);
//				voucherEntryvo.setFPreparerID(userid);
//				voucherEntryvo.setFExplanation(str[2] + "管理费收入");
////				voucherEntryvo.setJAccountID(jaccsubjid2);
////				voucherEntryvo.setDAccountID(daccsubjid2);
//				voucherEntryvo.setFJDetailID("0");
//				voucherEntryvo.setFDDetailID(fDetailID);
//				voucherEntryvo.setFAmount(str[7]);
//				voList.add(voucherEntryvo);
//			} else if(!StringUtil.isEmpty(str[8]) && Double.parseDouble(str[8]) != 0.0d){
//				//业绩报酬
//				VoucherEntryVO voucherEntryvo = new VoucherEntryVO();
//				voucherEntryvo.setFYear(String.valueOf(year));
//				voucherEntryvo.setFPeriod(String.valueOf(month));
//				voucherEntryvo.setFDay(str[0]);
//				voucherEntryvo.setFPreparerID(userid);
//				voucherEntryvo.setFExplanation(str[2] + "管理费收入");
////				voucherEntryvo.setJAccountID(jaccsubjid1);
////				voucherEntryvo.setDAccountID(daccsubjid2);
//				voucherEntryvo.setFJDetailID("0");
//				voucherEntryvo.setFDDetailID(fDetailID);
//				voucherEntryvo.setFAmount(str[8]);
//				voList.add(voucherEntryvo);
//			};
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
			
			return 0;}
		
		Connection conn = null;
		Statement stat = null;
		try {
			conn = DBUtil.getConnection();
			conn.getAutoCommit();
			conn.setAutoCommit(false);
			stat = conn.createStatement();
			
			String voucherSql = null;
			String jvoucherentrySql1 = null;
			String jvoucherentrySql2 = null;
			String dvoucherentrySql1 = null;
			String dvoucherentrySql2 = null;
			
			//凭证ID.
			int initFVoucherID = Integer.parseInt(DBUtil.querySqlUniqueResult("select FMaxNum+1 from icmaxnum where FTableName='t_voucher'").toString());
			//凭证号
			VoucherEntryVO vevo = (VoucherEntryVO) volist.get(0);
			Object obj = DBUtil.querySqlUniqueResult("select max(fnumber)+1 from t_voucher where FYear= "+vevo.getFYear()+" and FPeriod="+vevo.getFPeriod());
			int initfnumber = Integer.parseInt(obj == null ? "1":obj.toString());
			for (int i = 0; i < volist.size(); i++) {
				int FVoucherID = initFVoucherID + i;
				int fnumber = initfnumber + i;
				vevo = (VoucherEntryVO) volist.get(i);
//				String dateStr = DateUtil.getDateStr(vevo.getFYear(), vevo.getFPeriod());
				String dateStr = vevo.getFDay();
				BigDecimal amount = new BigDecimal("0");
				String tempAmount = vevo.getFAmount().replaceAll(",","");
				String fxj = vevo.getFxj();
				tempAmount = StringUtil.isEmpty(tempAmount)? "0" : tempAmount;
				System.out.println("tempAmount=" + tempAmount);
				BigDecimal tempAmount1 = new BigDecimal(tempAmount).subtract(new BigDecimal(fxj));
				BigDecimal tempAmount2 = new BigDecimal(fxj);
				BigDecimal tempAmount3 = new BigDecimal(tempAmount).divide(new BigDecimal(1.06), 10, 4).multiply(new BigDecimal("0.06")).setScale(2, 4);
				BigDecimal tempAmount4 = new BigDecimal(tempAmount).subtract(tempAmount3);
				String daccsubjid2 = vevo.isGlf() ? daccsubjid21 : daccsubjid22;
				
				jvoucherentrySql1 = "insert into t_VoucherEntry(FBrNo,FVoucherID,FEntryID,FExplanation,FAccountID,FDetailID,FCurrencyID,FExchangeRate,FDC,FAmountFor,FAmount,FQuantity,FMeasureUnitID,FUnitPrice,FInternalInd,FAccountID2,FSettleTypeID,FSettleNo,FTransNo,FCashFlowItem,FTaskID,FResourceID,FExchangeRateType,FSideEntryID) "
					+ "values(0," + FVoucherID + ","+0+",'"+vevo.getFExplanation()+"'," + jaccsubjid1 + "," + vevo.getFJDetailID() + ",1,1,1," + tempAmount1 + "," + tempAmount1 + ",0,0,0,null," + daccsubjid1 + ",0,null,null,0,0,0,1,"+1+")";
				jvoucherentrySql2 = "insert into t_VoucherEntry(FBrNo,FVoucherID,FEntryID,FExplanation,FAccountID,FDetailID,FCurrencyID,FExchangeRate,FDC,FAmountFor,FAmount,FQuantity,FMeasureUnitID,FUnitPrice,FInternalInd,FAccountID2,FSettleTypeID,FSettleNo,FTransNo,FCashFlowItem,FTaskID,FResourceID,FExchangeRateType,FSideEntryID) "
						+ "values(0," + FVoucherID + ","+1+",'"+vevo.getFExplanation()+"'," + jaccsubjid2 + "," + vevo.getFJDetailID() + ",1,1,1," + tempAmount2 + "," + tempAmount2 + ",0,0,0,null," + daccsubjid2 + ",0,null,null,0,0,0,1,"+1+")";
				dvoucherentrySql1 = "insert into t_VoucherEntry(FBrNo,FVoucherID,FEntryID,FExplanation,FAccountID,FDetailID,FCurrencyID,FExchangeRate,FDC,FAmountFor,FAmount,FQuantity,FMeasureUnitID,FUnitPrice,FInternalInd,FAccountID2,FSettleTypeID,FSettleNo,FTransNo,FCashFlowItem,FTaskID,FResourceID,FExchangeRateType,FSideEntryID) "
					+ "values(0," + FVoucherID + ","+2+",'"+vevo.getFExplanation()+"'," + daccsubjid1 + ",0,1,1,0," + tempAmount3 + "," + tempAmount3 + ",0,0,0,null," + jaccsubjid1 + ",0,null,null,0,0,0,1,"+0+");";
				dvoucherentrySql2 = "insert into t_VoucherEntry(FBrNo,FVoucherID,FEntryID,FExplanation,FAccountID,FDetailID,FCurrencyID,FExchangeRate,FDC,FAmountFor,FAmount,FQuantity,FMeasureUnitID,FUnitPrice,FInternalInd,FAccountID2,FSettleTypeID,FSettleNo,FTransNo,FCashFlowItem,FTaskID,FResourceID,FExchangeRateType,FSideEntryID) "
						+ "values(0," + FVoucherID + ","+3+",'"+vevo.getFExplanation()+"'," + daccsubjid2 + "," + vevo.getFDDetailID() + ",1,1,0," + tempAmount4 + "," + tempAmount4 + ",0,0,0,null," + jaccsubjid2 + ",0,null,null,0,0,0,1,"+0+");";
				amount = amount.add(new BigDecimal(tempAmount));
				System.out.println(jvoucherentrySql1);
				System.out.println(jvoucherentrySql2);
				System.out.println(dvoucherentrySql1);
				System.out.println(dvoucherentrySql2);
				stat.addBatch(jvoucherentrySql1);
				stat.addBatch(jvoucherentrySql2);
				stat.addBatch(dvoucherentrySql1);
				stat.addBatch(dvoucherentrySql2);
				voucherSql = "insert into t_Voucher(FBrNo,FVoucherID,FDate,FYear,FPeriod,FGroupID,FNumber,FReference,FExplanation,FAttachments,FEntryCount,FDebitTotal,FCreditTotal,FInternalInd,FChecked,FPosted,FPreparerID,FCheckerID,	FPosterID,FCashierID,	FHandler,FOwnerGroupID,FObjectName,FParameter,FSerialNum,FTranType,FTransDate,FFrameWorkID,FApproveID,FFootNote,UUID) "
						+ "values (0," + FVoucherID + ",'" + dateStr + " 00:00:00.000'," + vevo.getFYear() + "," + vevo.getFPeriod() + ",1," + fnumber + ",null,'"+vevo.getFExplanation()+"',0,3," + amount + "," + amount + ",null,0,0," + vevo.getFPreparerID() + ",-1,-1,-1,null,0,null,null,123,0,'" + dateStr + " 00:00:00.000',	-1,	-1,'','"+UUID.randomUUID()+"')";
				stat.addBatch(voucherSql);
				//更新配置表
				String updateSql = "update icmaxnum set FMaxNum="+FVoucherID+" where FTableName='t_voucher'" ;
				stat.addBatch(updateSql);
			}
			stat.executeBatch();
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

//	private String getFAccountID() throws Exception{
//		//  应交税费-增值税-销项税额
//		return getAccsubjid("2221.04.02");
//	}
	
	
	private double getAmount(String str){
		if (StringUtil.isEmpty(str) || Double.valueOf(str.replace(",", "")) == 0) {
			return 0d;
		} else {
			return Double.valueOf(str.replace(",", ""));
		}
	}

	
}
