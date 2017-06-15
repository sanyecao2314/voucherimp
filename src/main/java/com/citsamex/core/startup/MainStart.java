package com.citsamex.core.startup;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import com.citsamex.core.services.Comb1Services;
import com.citsamex.core.services.Comb2Services;
import com.citsamex.core.services.Comb3Services;
import com.citsamex.core.services.Comb4Services;
import com.citsamex.core.services.Comb5Services;
import com.citsamex.core.services.Comb6Services;

public class MainStart extends AbstractStart {

	
	/**
	 * Auto-generated main method to display this JFrame
	 */
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				MainStart inst = new MainStart();
				inst.setLocationRelativeTo(null);
				inst.setVisible(true);
			}
		});
	}
	
	public MainStart(){
		super();
		usernameTextField.setText("jiny");
	}
	
	/**
	 * 0 = 下拉选择.
	 * 1 = 选择文件
	 * 2 = 读取文件
	 * 3 = 转换文件
	 * 4 = 导入文件
	 * 
	 */
	private int billstatus = 0;
	
	@Override
	public void actionPerformed(ActionEvent e) {
		
		if(e.getSource() == jComboBox){	//下拉选择项.
			onBOComboBox();
			billstatus = 0;
		}
		else if (e.getSource() == chooseButton) {	//选择文件.
			if(chooseButtonKeyReleased() && onBOReadButton()){
				billstatus = 2;
				remark.setText(null);
			}  
		}
//		else if(e.getSource() == readButton){	//读取文件
//			if(billstatus != 1){
//				JOptionPane.showMessageDialog(this, "请先选择文件!");
//				return ;
//			}
//			
//			if(onBOReadButton()){
//				billstatus = 2;
//			}
//		}
		else if(e.getSource() == impButton){	//转换 导入
			if(billstatus != 2){
				JOptionPane.showMessageDialog(this, "请先选择文件!");
				return ;
			}
			remark.setText(null);
			if(onBOConvertButton() && onBOImpButton()){
				list = null;
				volist = null;
				billstatus = 4;
				remark.setText("数据导入成功.");
			}
		}
//		else if(e.getSource() == impButton){	//导入
//			if(billstatus != 3){
//				JOptionPane.showMessageDialog(this, "请先转换文件!");
//				return ;
//			}
//			if(){
//				billstatus = 4;
//			}
//		}
	}
	
	/**
	 * 选择的导入类型发生变化时,tablemodel随之变化.
	 */
	private void onBOComboBox(){
		Object selecteditem = jComboBox.getSelectedItem();
		if(COMBOVALUE1.equals(selecteditem)){
			defaultTableModel = new TableModel1();
			dataTable.setModel(defaultTableModel);
		}else if(COMBOVALUE2.equals(selecteditem)){
			defaultTableModel = new TableModel2();
			dataTable.setModel(defaultTableModel);
		}else if(COMBOVALUE3.equals(selecteditem)){
			defaultTableModel = new TableModel3();
			dataTable.setModel(defaultTableModel);
		}else if(COMBOVALUE4.equals(selecteditem)){
			defaultTableModel = new TableModel4();
			dataTable.setModel(defaultTableModel);
		}else if(COMBOVALUE5.equals(selecteditem)){
			defaultTableModel = new TableModel5();
			dataTable.setModel(defaultTableModel);
		}else if(COMBOVALUE6.equals(selecteditem)){
			defaultTableModel = new TableModel6();
			dataTable.setModel(defaultTableModel);
		}
		//清空提示信息框里的信息.
		remark.setText(null);
	}
	
	ArrayList list = null;
	/**
	 * 读取Excel文件.
	 */
	private boolean onBOReadButton(){
		if (selectedFile == null ) {
			JOptionPane.showMessageDialog(this, "请先选择文件!");
			return false;
		}
		
		//删除原有行.
		int rowcount = defaultTableModel.getRowCount();
		for (; rowcount > 0; rowcount--) {
			defaultTableModel.removeRow(rowcount);
		}
	
		Object selecteditem = jComboBox.getSelectedItem();
		if(COMBOVALUE1.equals(selecteditem)){
			Comb1Services comb1 = new Comb1Services();
			list = comb1.readXls(selectedFile);
		}else if(COMBOVALUE2.equals(selecteditem)){
			Comb2Services comb2 = new Comb2Services();
			list = comb2.readXls(selectedFile);
		}else if(COMBOVALUE3.equals(selecteditem)){
			Comb3Services comb3 = new Comb3Services();
			list = comb3.readXls(selectedFile);
		}else if(COMBOVALUE4.equals(selecteditem)){
			Comb4Services comb4 = new Comb4Services();
			list = comb4.readXls(selectedFile);
		}else if(COMBOVALUE5.equals(selecteditem)){
			Comb5Services comb5 = new Comb5Services();
			list = comb5.readXls(selectedFile);
		}else if(COMBOVALUE6.equals(selecteditem)){
			Comb6Services comb6 = new Comb6Services();
			list = comb6.readXls(selectedFile);
		}
		
		if(list == null || list.size() == 0) return false;
		
		for (int i = 0; i < list.size(); i++) {
			defaultTableModel.addRow((String[])list.get(i));
		}
		return true;
	}
	
	List volist = null;
	
	/**
	 * 转换文件
	 * @return
	 */
	private boolean onBOConvertButton() {
		if(list == null || list.size() == 0){
			remark.setText("没有需要导入的数据.");
			return false;
		}
		
		Object selecteditem = jComboBox.getSelectedItem();
		try {
			if (COMBOVALUE1.equals(selecteditem)) {
				Comb1Services comb1 = new Comb1Services();
				volist = comb1.convertFile2VO(list, this);
			} else if (COMBOVALUE2.equals(selecteditem)) {
				Comb2Services comb2 = new Comb2Services();
				volist = comb2.convertFile2VO(list, this);
			} else if (COMBOVALUE3.equals(selecteditem)) {
				Comb3Services comb3 = new Comb3Services();
				volist = comb3.convertFile2VO(list, this);
			} else if (COMBOVALUE4.equals(selecteditem)) {
				Comb4Services comb4 = new Comb4Services();
				volist = comb4.convertFile2VO(list, this);
			} else if (COMBOVALUE5.equals(selecteditem)) {
				Comb5Services comb5 = new Comb5Services();
				volist = comb5.convertFile2VO(list, this);
			} else if (COMBOVALUE6.equals(selecteditem)) {
				Comb6Services comb6 = new Comb6Services();
				volist = comb6.convertFile2VO(list, this);
			}
		} catch (Exception e) {
			remark.setText(e.getMessage());
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	/**
	 * 导入数据
	 * @return
	 */
	private boolean onBOImpButton() {
		
		if(volist == null || volist.size() == 0){ return false; }
		
		Object selecteditem = jComboBox.getSelectedItem();
		try {
			if (COMBOVALUE1.equals(selecteditem)) {
				Comb1Services comb1 = new Comb1Services();
				int i = comb1.impVO2SysVoucher(volist, this);
			} else if (COMBOVALUE2.equals(selecteditem)) {
				Comb2Services comb2 = new Comb2Services();
				int i = comb2.impVO2SysVoucher(volist, this);
			} else if (COMBOVALUE3.equals(selecteditem)) {
				Comb3Services comb3 = new Comb3Services();
				int i = comb3.impVO2SysVoucher(volist, this);
			} else if (COMBOVALUE4.equals(selecteditem)) {
				Comb4Services comb4 = new Comb4Services();
				int i = comb4.impVO2SysVoucher(volist, this);
			} else if (COMBOVALUE5.equals(selecteditem)) {
				Comb5Services comb5 = new Comb5Services();
				int i = comb5.impVO2SysVoucher(volist, this);
			} else if (COMBOVALUE6.equals(selecteditem)) {
				Comb6Services comb6 = new Comb6Services();
				int i = comb6.impVO2SysVoucher(volist, this);
			}
		} catch (Exception e) {
			remark.setText(e.getMessage());
			return false;
		}
		return true;
	}
}
