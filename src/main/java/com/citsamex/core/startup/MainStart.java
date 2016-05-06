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
	 * 0 = ����ѡ��.
	 * 1 = ѡ���ļ�
	 * 2 = ��ȡ�ļ�
	 * 3 = ת���ļ�
	 * 4 = �����ļ�
	 * 
	 */
	private int billstatus = 0;
	
	@Override
	public void actionPerformed(ActionEvent e) {
		
		if(e.getSource() == jComboBox){	//����ѡ����.
			onBOComboBox();
			billstatus = 0;
		}
		else if (e.getSource() == chooseButton) {	//ѡ���ļ�.
			if(chooseButtonKeyReleased() && onBOReadButton()){
				billstatus = 2;
				remark.setText(null);
			}  
		}
//		else if(e.getSource() == readButton){	//��ȡ�ļ�
//			if(billstatus != 1){
//				JOptionPane.showMessageDialog(this, "����ѡ���ļ�!");
//				return ;
//			}
//			
//			if(onBOReadButton()){
//				billstatus = 2;
//			}
//		}
		else if(e.getSource() == impButton){	//ת�� ����
			if(billstatus != 2){
				JOptionPane.showMessageDialog(this, "����ѡ���ļ�!");
				return ;
			}
			remark.setText(null);
			if(onBOConvertButton() && onBOImpButton()){
				list = null;
				volist = null;
				billstatus = 4;
				remark.setText("���ݵ���ɹ�.");
			}
		}
//		else if(e.getSource() == impButton){	//����
//			if(billstatus != 3){
//				JOptionPane.showMessageDialog(this, "����ת���ļ�!");
//				return ;
//			}
//			if(){
//				billstatus = 4;
//			}
//		}
	}
	
	/**
	 * ѡ��ĵ������ͷ����仯ʱ,tablemodel��֮�仯.
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
		//�����ʾ��Ϣ�������Ϣ.
		remark.setText(null);
	}
	
	ArrayList list = null;
	/**
	 * ��ȡExcel�ļ�.
	 */
	private boolean onBOReadButton(){
		if (selectedFile == null ) {
			JOptionPane.showMessageDialog(this, "����ѡ���ļ�!");
			return false;
		}
		
		//ɾ��ԭ����.
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
	 * ת���ļ�
	 * @return
	 */
	private boolean onBOConvertButton() {
		if(list == null || list.size() == 0){
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
	 * ��������
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
