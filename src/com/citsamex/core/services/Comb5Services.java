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
 * COMBOVALUE5 = "ֱ����������ϸ";
 * @author fans.fan
 *
 */
public class Comb5Services {
	
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
			for (int i = 3, rows = sht.getLastRowNum(); i < rows; i++) {
				row = sht.getRow(i);
				value = ExcelUtil.getCellValue(row.getCell(0));
				if (value != null && value.contains("�ϼ�")) {
					break;
				}else if(value != null && value.contains("С��")){
					continue;
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
	
	/**
	 * ת���ļ�
	 * @param list 
	 * ����	���ۻ���	��������	ҵ������	�������	��������	ȷ�Ͻ��	����	������	�����ۻ���
	 * @return
	 * @throws Exception 
	 */
	public List convertFile2VO(ArrayList list,MainStart mainUI) throws Exception{
		if(list == null || list.size() == 0){
			return null;
		}
		
		List voList = new ArrayList();
		List templist = null;
		String jaccsubjid = null;
		String daccsubjid = null;
		String userid = null;
		
		templist = DBUtil.querySql("select FAccountID from t_Account where FNumber='6602.02.12.01'");
		if(templist != null && templist.size() == 1){
			jaccsubjid = ((HashMap)templist.get(0)).get("FAccountID").toString();
		}else{
			throw new Exception("��ѯ������Ŀ�쳣.��ִ��sql:select FAccountID from t_Account where FNumber='6602.02.12.01'");
		}
		
		templist = DBUtil.querySql("select FAccountID from t_Account where FNumber='2202.02'");
		if(templist != null && templist.size() == 1){
			daccsubjid = ((HashMap)templist.get(0)).get("FAccountID").toString();
		}else{
			throw new Exception("��ѯ�跽��Ŀ�쳣.��ִ��sql:select FAccountID from t_Account where FNumber='2202.02'");
		}
		
		//У���û�����
		String username = mainUI.usernameTextField.getText();
		if(username == null || "".equals(username)){
			throw new Exception("����д�û���!");
		}
		
		templist = DBUtil.querySql("select * from t_user where FName='"+username+"'");
		if(templist != null && templist.size() == 1){
			userid = ((HashMap)templist.get(0)).get("FUserID").toString();
		}else{
			throw new Exception("δ�鵽����û���Ϣ,�����û����Ƿ���ȷ!");
		}
		
		//У��ҵ���ڼ�.
		int year = Integer.parseInt(mainUI.yearComboBox.getSelectedItem().toString());
		int month = Integer.parseInt(mainUI.monthComboBox.getSelectedItem().toString());
		
		String yearSql = "select FValue from t_systemprofile where FKey='currentyear' and FCategory='FA'";
		templist = DBUtil.querySql(yearSql);
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
		
		//ְԱId
		String empid = null;
		//����Id
		String deptId = null;
		
		for(int i = 0;i<list.size();i++){
			String[] str = (String[]) list.get(i);
			if(str[9]==null || "".equals(str[9]))continue;
			if(Double.parseDouble(str[9]) == 0.0d) continue;
			
			VoucherEntryVO voucherEntryvo = new VoucherEntryVO();
			
			//����һ������������	������			
			String sql = "select * from t_item where FItemClassID=8 and fname like '%" + str[1] + "'";
			templist = DBUtil.querySql(sql);
			if(templist != null && templist.size() == 1){
				voucherEntryvo.setFxss(((HashMap)templist.get(0)).get("FItemID").toString());
			}else if(templist == null || templist.size() == 0){
				throw new Exception("δ�ҵ����ư���" + str[1] + "�ĸ���������Ŀ!");
			}else{
				throw new Exception("��ѯ����������Ŀ�쳣.��ִ��sql���:" + sql);
			}
			//���ڶ�������������	�������
			sql = "select * from t_item where fnumber like '%" + str[4] + "'";
			templist = DBUtil.querySql(sql);
			if(templist != null && templist.size() == 1){
				voucherEntryvo.setProject(((HashMap)templist.get(0)).get("FItemID").toString());
			}else if(templist == null || templist.size() == 0){
				throw new Exception("δ�ҵ��������" + str[4] + "�ĸ���������Ŀ!");
			}else{
				throw new Exception("��ѯ����������Ŀ�쳣.��ִ��sql���:" + sql);
			}
			//������������������	ְԱ
			sql = "select * from t_item where fnumber = '059' and FItemClassID=3 ";
			templist = DBUtil.querySql(sql);
			if(templist != null && templist.size() == 1){
				empid = ((HashMap)templist.get(0)).get("FItemID").toString();
			}else if(templist == null || templist.size() == 0){
				throw new Exception("δ�ҵ��������059�ĸ���������Ŀ--ְԱ!");
			}else{
				throw new Exception("��ѯ����������Ŀ�쳣.��ִ��sql���:" + sql);
			}
			//���ĸ����������� ����
			sql = "select * from t_item where fnumber = '04.001' and FItemClassID=2";
			templist = DBUtil.querySql(sql);
			if(templist != null && templist.size() == 1){
				deptId = ((HashMap)templist.get(0)).get("FItemID").toString();
			}else if(templist == null || templist.size() == 0){
				throw new Exception("δ�ҵ��������04.001�ĸ���������Ŀ--����!");
			}else{
				throw new Exception("��ѯ����������Ŀ�쳣.��ִ��sql���:" + sql);
			}
			
			//�跽��Ŀ���������/�г�Ӫ����֧/����������֧��/ֱ��������֧��	2���� 3ְԱ2039������Ŀ 
			sql = "select FDetailID from t_ItemDetail where FDetailCount=3 and F2=" + deptId + " and F3=" + empid + "and F2039=" + voucherEntryvo.getProject();
			templist = DBUtil.querySql(sql);
			if(templist != null && templist.size() == 1){
				voucherEntryvo.setFJDetailID(((HashMap)templist.get(0)).get("FDetailID").toString());
			}else if(templist == null || templist.size() == 0){
				//û�и���������Ϣ.����t_ItemDetail��t_ItemDetailV����������Ϣ.
				Object  maxFDetailID = DBUtil.querySqlUniqueResult("select max(FDetailID)+1 from t_ItemDetail ");
				String insertItemDetail = "insert into t_ItemDetail(FDetailID,FDetailCount,F1,F2,F3,F4,F5,F8,F9,F10,F14,F2001,F2002,F2003,F2004,F2014,F2023,F2021,F2024,F2026,F2027,F2028,F2029,F2030,F2035,F2036,F2039,F2040,F2041) "
					+ "values ("+maxFDetailID+",3,0,"+deptId+","+empid+",0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,"+voucherEntryvo.getProject()+",0,0)";
				DBUtil.excuteUpdate(insertItemDetail);

				String insertItemDetailV = "insert into t_ItemDetailV(FDetailID,FItemClassID,FItemID) "
					+ "values ("+maxFDetailID+",2,"+deptId+")";
				DBUtil.excuteUpdate(insertItemDetailV);
				insertItemDetailV = "insert into t_ItemDetailV(FDetailID,FItemClassID,FItemID) "
					+ "values ("+maxFDetailID+",3,"+empid+")";
				DBUtil.excuteUpdate(insertItemDetailV);
				insertItemDetailV = "insert into t_ItemDetailV(FDetailID,FItemClassID,FItemID) "
					+ "values ("+maxFDetailID+",2039,"+voucherEntryvo.getProject()+")";
				DBUtil.excuteUpdate(insertItemDetailV);
				voucherEntryvo.setFJDetailID(maxFDetailID.toString());
			}else{
				throw new Exception("��ѯ����������Ŀ�쳣.��ִ��sql���:" + sql);
			}
			
			//������Ŀ��Ӧ���˿�/��������Ӷ��		 8��Ӧ��
			sql = "select FDetailID from t_ItemDetail where FDetailCount=1 and F8=" + voucherEntryvo.getFxss();
			templist = DBUtil.querySql(sql);
			if(templist != null && templist.size() == 1){
				voucherEntryvo.setFDDetailID(((HashMap)templist.get(0)).get("FDetailID").toString());
			}else if(templist == null || templist.size() == 0){
				//û�и���������Ϣ.����t_ItemDetail��t_ItemDetailV����������Ϣ.
				Object  maxFDetailID = DBUtil.querySqlUniqueResult("select max(FDetailID)+1 from t_ItemDetail ");
				String insertItemDetail = "insert into t_ItemDetail(FDetailID,FDetailCount,F1,F2,F3,F4,F5,F8,F9,F10,F14,F2001,F2002,F2003,F2004,F2014,F2023,F2021,F2024,F2026,F2027,F2028,F2029,F2030,F2035,F2036,F2039,F2040,F2041) "
					+ "values ("+maxFDetailID+",1,0,0,0,0,0,"+voucherEntryvo.getFxss()+",0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0)";
				String insertItemDetailV = "insert into t_ItemDetailV(FDetailID,FItemClassID,FItemID) "
					+ "values ("+maxFDetailID+",8,"+voucherEntryvo.getFxss()+")";
				DBUtil.excuteUpdate(insertItemDetail);
				DBUtil.excuteUpdate(insertItemDetailV);
				voucherEntryvo.setFDDetailID(maxFDetailID.toString());
			}else{
				throw new Exception("��ѯ����������Ŀ�쳣.��ִ��sql���:" + sql);
			}
			
			voucherEntryvo.setFYear(String.valueOf(year));
			voucherEntryvo.setFPeriod(String.valueOf(month));
			voucherEntryvo.setFPreparerID(userid);
			voucherEntryvo.setFExplanation("����" + month + "��ֱ��������");
			voucherEntryvo.setJAccountID(jaccsubjid);
			voucherEntryvo.setDAccountID(daccsubjid);
			voucherEntryvo.setFAmount(str[9]);
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
			String dvoucherentrySql = null;
			
			//ƾ֤ID.
			int FVoucherID = Integer.parseInt(DBUtil.querySqlUniqueResult("select FMaxNum+1 from icmaxnum where FTableName='t_voucher'").toString());
			//ƾ֤��
			VoucherEntryVO tempvo = (VoucherEntryVO) volist.get(0);
			Object obj = DBUtil.querySqlUniqueResult("select max(fnumber)+1 from t_voucher where FYear= "+tempvo.getFYear()+" and FPeriod="+tempvo.getFPeriod());
			int fnumber = Integer.parseInt(obj == null ? "1":obj.toString());
			
			Calendar cal = Calendar.getInstance();
			cal.set(Calendar.YEAR,Integer.parseInt(tempvo.getFYear()));
			cal.set(Calendar.MONTH,Integer.parseInt(tempvo.getFPeriod()) - 1);
			cal.set(Calendar.DAY_OF_MONTH, cal.getMaximum(Calendar.DAY_OF_MONTH) - 1);
			DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
			String dateStr = df.format(cal.getTime());
			BigDecimal amount = new BigDecimal("0");
			for (int i = 0; i < volist.size(); i++) {
				VoucherEntryVO vevo = (VoucherEntryVO) volist.get(i);
				String tempAmount = vevo.getFAmount().replaceAll(",","");
				jvoucherentrySql = "insert into t_VoucherEntry(FBrNo,FVoucherID,FEntryID,FExplanation,FAccountID,FDetailID,FCurrencyID,FExchangeRate,FDC,FAmountFor,FAmount,FQuantity,FMeasureUnitID,FUnitPrice,FInternalInd,FAccountID2,FSettleTypeID,FSettleNo,FTransNo,FCashFlowItem,FTaskID,FResourceID,FExchangeRateType,FSideEntryID) "
					+ "values(0," + FVoucherID + ","+i*2+",'" + vevo.getFExplanation() + "'," + vevo.getJAccountID() + "," + vevo.getFJDetailID() + ",1,1,1," + tempAmount + "," + tempAmount + ",0,0,0,null," + vevo.getDAccountID() + ",0,null,null,0,0,0,1,"+(i*2+1)+")";
				dvoucherentrySql = "insert into t_VoucherEntry(FBrNo,FVoucherID,FEntryID,FExplanation,FAccountID,FDetailID,FCurrencyID,FExchangeRate,FDC,FAmountFor,FAmount,FQuantity,FMeasureUnitID,FUnitPrice,FInternalInd,FAccountID2,FSettleTypeID,FSettleNo,FTransNo,FCashFlowItem,FTaskID,FResourceID,FExchangeRateType,FSideEntryID) "
					+ "values(0," + FVoucherID + ","+(i*2+1)+",'" + vevo.getFExplanation() + "'," + vevo.getDAccountID() + "," + vevo.getFDDetailID() + ",1,1,0," + tempAmount + "," + tempAmount + ",0,0,0,null," + vevo.getJAccountID() + ",0,null,null,0,0,0,1,"+i*2+")";
				amount = amount.add(new BigDecimal(tempAmount));
				stat.execute(jvoucherentrySql);
				stat.execute(dvoucherentrySql);
			}

			voucherSql = "insert into t_Voucher(FBrNo,FVoucherID,FDate,FYear,FPeriod,FGroupID,FNumber,FReference,FExplanation,FAttachments,FEntryCount,FDebitTotal,FCreditTotal,FInternalInd,FChecked,FPosted,FPreparerID,FCheckerID,	FPosterID,FCashierID,	FHandler,FOwnerGroupID,FObjectName,FParameter,FSerialNum,FTranType,FTransDate,FFrameWorkID,FApproveID,FFootNote,UUID) "
				+ "values (0," + FVoucherID + ",'" + dateStr + " 00:00:00.000'," + tempvo.getFYear() + "," + tempvo.getFPeriod() + ",1," + fnumber + ",null,'" + tempvo.getFExplanation() + "',0,2," + amount + "," + amount + ",null,0,0," + tempvo.getFPreparerID() + ",-1,-1,-1,null,0,null,null,123,0,'" + dateStr + " 00:00:00.000',	-1,	-1,'','"+UUID.randomUUID()+"')";
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
