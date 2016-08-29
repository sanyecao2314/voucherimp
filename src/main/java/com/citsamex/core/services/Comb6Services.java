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
 *  COMBOVALUE6 = "ר��������ñ�";
 * @author fans.fan
 *
 */
public class Comb6Services extends SuperServices {
	
	public static ArrayList readXls(File file){
		
		HSSFWorkbook rwb = null;// ����һ����������
		HSSFSheet sht = null;// ����һ��������
		HSSFRow row = null;

		ArrayList voList = new ArrayList();
		try {
			InputStream io = new FileInputStream(file);
			rwb = new HSSFWorkbook(io);
			sht = rwb.getSheetAt(0);
			if (sht == null) {
//				JOptionPane.showMessageDialog(this, "������ҳǩ");
				System.out.println("������ҳǩ");
				return null;
			}
			String value = null;
			for (int i = 4, rows = sht.getLastRowNum(); i < rows; i++) {
				row = sht.getRow(i);
				value = ExcelUtil.getCellValue(row.getCell(0));
				if (value != null && value.contains("��")) {
					break;
				}
					
				String[] vo = new String[8];
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
	 * ת���ļ�
	 * @param list 
	 * "����","�������","��������","����ѻ���","�йܷ�","���۷���","��֤��"
	 * @return
	 * @throws Exception 
	 */
	public List convertFile2VO(ArrayList list,MainStart mainUI) throws Exception{
		if(list == null || list.size() == 0){
			return null;
		}
		
		List voList = new ArrayList();
		List templist = null;
		String jaccsubjid = getAccsubjid("1122.01");
		String daccsubjid = getAccsubjid("6001.01.02");
		String userid = getUserId(mainUI.usernameTextField.getText());
		
		
		//У��ҵ���ڼ�.
		int year = Integer.parseInt(mainUI.yearComboBox.getSelectedItem().toString());
		int month = Integer.parseInt(mainUI.monthComboBox.getSelectedItem().toString());
		checkYearAndMonth(year, month);
		
		String deptid = null;
		String empid = null;
		for(int i = 0;i<list.size();i++){
			String[] str = (String[]) list.get(i);
			VoucherEntryVO voucherEntryvo = new VoucherEntryVO();
			
			if(str[5]==null || "".equals(str[5]))continue;
			if(Double.parseDouble(str[5]) == 0.0d) continue;
			
			//����һ������������	�������
			String sql = "select FItemID from t_item where fnumber like '%" + str[1] + "'";
			voucherEntryvo.setProject(getFItemID(sql, str[1]));
			//����һ������������	����			
			sql = "select FItemID from t_item where fname like '%" + str[3] + "'";
			deptid = getFItemID(sql, str[3]);
			//������������������	ְԱ
			sql = "select FItemID from t_item where fname = '"+str[4]+"'";
			empid = getFItemID(sql, str[4]);
			
			//�跽��Ŀ��Ӧ���˿�/Ӧ�չ����			2039������Ŀ
			sql = "select FDetailID from t_ItemDetail where FDetailCount=1 and F2039=" + voucherEntryvo.getProject();
			templist = DBUtil.querySql(sql);
			if(templist != null && templist.size() == 1){
				voucherEntryvo.setFJDetailID(((HashMap)templist.get(0)).get("FDetailID").toString());
			}else if(templist == null || templist.size() == 0){
				//û�и���������Ϣ.����t_ItemDetail��t_ItemDetailV����������Ϣ.
				Object  maxFDetailID = DBUtil.querySqlUniqueResult("select max(FDetailID)+1 from t_ItemDetail ");
				String insertItemDetail = "insert into t_ItemDetail(FDetailID,FDetailCount,F1,F2,F3,F4,F5,F8,F9,F10,F14,F2001,F2002,F2003,F2004,F2014,F2023,F2021,F2024,F2026,F2027,F2028,F2029,F2030,F2035,F2036,F2039,F2040,F2041) "
					+ "values ("+maxFDetailID+",1,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,"+voucherEntryvo.getProject()+",0,0)";
				String insertItemDetailV = "insert into t_ItemDetailV(FDetailID,FItemClassID,FItemID) "
					+ "values ("+maxFDetailID+",2039,"+voucherEntryvo.getProject()+")";
				DBUtil.excuteUpdate(insertItemDetail);
				DBUtil.excuteUpdate(insertItemDetailV);
				voucherEntryvo.setFJDetailID(maxFDetailID.toString());
			}else{
				throw new Exception("��ѯ����������Ŀ�쳣.��ִ��sql���:" + sql);
			}
			
			//������Ŀ����Ӫҵ������/���������/ר�����������		2���� 3ְԱ2039������Ŀ
			sql = "select FDetailID from t_ItemDetail where FDetailCount=3 and F2=" + deptid + " and F3=" + empid + " and F2039=" + voucherEntryvo.getProject();
			templist = DBUtil.querySql(sql);
			if(templist != null && templist.size() == 1){
				voucherEntryvo.setFDDetailID(((HashMap)templist.get(0)).get("FDetailID").toString());
			}else if(templist == null || templist.size() == 0){
				//û�и���������Ϣ.����t_ItemDetail��t_ItemDetailV����������Ϣ.
				Object  maxFDetailID = DBUtil.querySqlUniqueResult("select max(FDetailID)+1 from t_ItemDetail ");
				String insertItemDetail = "insert into t_ItemDetail(FDetailID,FDetailCount,F1,F2,F3,F4,F5,F8,F9,F10,F14,F2001,F2002,F2003,F2004,F2014,F2023,F2021,F2024,F2026,F2027,F2028,F2029,F2030,F2035,F2036,F2039,F2040,F2041) "
					+ "values ("+maxFDetailID+",3,0,"+deptid+","+empid+",0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,"+voucherEntryvo.getProject()+",0,0)";
				String insertItemDetailV = "insert into t_ItemDetailV(FDetailID,FItemClassID,FItemID) "
					+ "values ("+maxFDetailID+",2,"+deptid+")";
				DBUtil.excuteUpdate(insertItemDetail);
				DBUtil.excuteUpdate(insertItemDetailV);
				insertItemDetailV = "insert into t_ItemDetailV(FDetailID,FItemClassID,FItemID) "
					+ "values ("+maxFDetailID+",3,"+empid+")";
				DBUtil.excuteUpdate(insertItemDetailV);
				insertItemDetailV = "insert into t_ItemDetailV(FDetailID,FItemClassID,FItemID) "
					+ "values ("+maxFDetailID+",2039,"+voucherEntryvo.getProject()+")";
				DBUtil.excuteUpdate(insertItemDetailV);
				voucherEntryvo.setFDDetailID(maxFDetailID.toString());
			}else{
				throw new Exception("��ѯ����������Ŀ�쳣.��ִ��sql���:" + sql);
			}
			voucherEntryvo.setFYear(String.valueOf(year));
			voucherEntryvo.setFPeriod(String.valueOf(month));
			voucherEntryvo.setFPreparerID(userid);
			voucherEntryvo.setFExplanation("����" + month + "��ר�����������");
			voucherEntryvo.setJAccountID(jaccsubjid);
			voucherEntryvo.setDAccountID(daccsubjid);
			voucherEntryvo.setFAmount(str[5]);
			voList.add(voucherEntryvo);
		}
		
		return voList;
	}
	
	/**
	 * �������ݵ�K3ϵͳ.
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
			String dvoucherentrySql1 = null;
			String dvoucherentrySql2 = null;
			
			//ƾ֤ID.
			int FVoucherID = Integer.parseInt(DBUtil.querySqlUniqueResult("select FMaxNum+1 from icmaxnum where FTableName='t_voucher'").toString());
			//ƾ֤��
			VoucherEntryVO tempvo = (VoucherEntryVO) volist.get(0);
			Object obj = DBUtil.querySqlUniqueResult("select max(fnumber)+1 from t_voucher where FYear= "+tempvo.getFYear()+" and FPeriod="+tempvo.getFPeriod());
			int fnumber = Integer.parseInt(obj == null ? "1":obj.toString());
			
			BigDecimal amount = new BigDecimal("0");
			for (int i = 0; i < volist.size(); i++) {
				VoucherEntryVO vevo = (VoucherEntryVO) volist.get(i);
				String tempAmount = vevo.getFAmount().replaceAll(",","");
				BigDecimal taxAmount = new BigDecimal(tempAmount).multiply(new BigDecimal(0.06)).divide(new BigDecimal(1.06), 2, 4);
				BigDecimal tempAmount2 = new BigDecimal(tempAmount).subtract(taxAmount);
				jvoucherentrySql = "insert into t_VoucherEntry(FBrNo,FVoucherID,FEntryID,FExplanation,FAccountID,FDetailID,FCurrencyID,FExchangeRate,FDC,FAmountFor,FAmount,FQuantity,FMeasureUnitID,FUnitPrice,FInternalInd,FAccountID2,FSettleTypeID,FSettleNo,FTransNo,FCashFlowItem,FTaskID,FResourceID,FExchangeRateType,FSideEntryID) "
					+ "values(0," + FVoucherID + ","+i*3+",'"+vevo.getFExplanation()+"'," + vevo.getJAccountID() + "," + vevo.getFJDetailID() + ",1,1,1," + tempAmount + "," + tempAmount + ",0,0,0,null," + vevo.getDAccountID() + ",0,null,null,0,0,0,1,"+(i*3+1)+")";
				dvoucherentrySql1 = "insert into t_VoucherEntry(FBrNo,FVoucherID,FEntryID,FExplanation,FAccountID,FDetailID,FCurrencyID,FExchangeRate,FDC,FAmountFor,FAmount,FQuantity,FMeasureUnitID,FUnitPrice,FInternalInd,FAccountID2,FSettleTypeID,FSettleNo,FTransNo,FCashFlowItem,FTaskID,FResourceID,FExchangeRateType,FSideEntryID) "
					+ "values(0," + FVoucherID + ","+(i*3+1)+",'"+vevo.getFExplanation()+"'," + getFAccountID() + ",0,1,1,0," + taxAmount + "," + taxAmount + ",0,0,0,null," + vevo.getJAccountID() + ",0,null,null,0,0,0,1,"+i*3+");";
				dvoucherentrySql2 = "insert into t_VoucherEntry(FBrNo,FVoucherID,FEntryID,FExplanation,FAccountID,FDetailID,FCurrencyID,FExchangeRate,FDC,FAmountFor,FAmount,FQuantity,FMeasureUnitID,FUnitPrice,FInternalInd,FAccountID2,FSettleTypeID,FSettleNo,FTransNo,FCashFlowItem,FTaskID,FResourceID,FExchangeRateType,FSideEntryID) "
						+ "values(0," + FVoucherID + ","+(i*3+2)+",'"+vevo.getFExplanation()+"'," + vevo.getDAccountID() + "," + vevo.getFDDetailID() + ",1,1,0," + tempAmount2 + "," + tempAmount2 + ",0,0,0,null," + vevo.getJAccountID() + ",0,null,null,0,0,0,1,"+i*3+");";
				amount = amount.add(new BigDecimal(tempAmount));
				stat.execute(jvoucherentrySql);
				stat.execute(dvoucherentrySql1);
				stat.execute(dvoucherentrySql2);
			}

			String dateStr = DateUtil.getDateStr(tempvo.getFYear(), tempvo.getFPeriod());
			voucherSql = "insert into t_Voucher(FBrNo,FVoucherID,FDate,FYear,FPeriod,FGroupID,FNumber,FReference,FExplanation,FAttachments,FEntryCount,FDebitTotal,FCreditTotal,FInternalInd,FChecked,FPosted,FPreparerID,FCheckerID,	FPosterID,FCashierID,	FHandler,FOwnerGroupID,FObjectName,FParameter,FSerialNum,FTranType,FTransDate,FFrameWorkID,FApproveID,FFootNote,UUID) "
				+ "values (0," + FVoucherID + ",'" + dateStr + " 00:00:00.000'," + tempvo.getFYear() + "," + tempvo.getFPeriod() + ",1," + fnumber + ",null,'"+tempvo.getFExplanation()+"',0,3," + amount + "," + amount + ",null,0,0," + tempvo.getFPreparerID() + ",-1,-1,-1,null,0,null,null,123,0,'" + dateStr + " 00:00:00.000',	-1,	-1,'','"+UUID.randomUUID()+"')";
			stat.execute(voucherSql);

			//�������ñ�
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

	private String getFAccountID() throws Exception{
		//  Ӧ��˰��-Ӧ����ֵ˰-����˰
		return getAccsubjid("2221.04.02");
	}

	
}
