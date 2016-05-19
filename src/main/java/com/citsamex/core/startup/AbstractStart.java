package com.citsamex.core.startup;

import java.awt.BorderLayout;
import java.awt.FileDialog;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.WindowConstants;
import javax.swing.table.DefaultTableModel;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;

/**
 * This code was edited or generated using CloudGarden's Jigloo SWT/Swing GUI
 * Builder, which is free for non-commercial use. If Jigloo is being used
 * commercially (ie, by a corporation, company or business for any purpose
 * whatever) then you should purchase a license for each developer using Jigloo.
 * Please visit www.cloudgarden.com for details. Use of Jigloo implies
 * acceptance of these licensing terms. A COMMERCIAL LICENSE HAS NOT BEEN
 * PURCHASED FOR THIS MACHINE, SO JIGLOO OR THIS CODE CANNOT BE USED LEGALLY FOR
 * ANY CORPORATE OR COMMERCIAL PURPOSE.
 */
public class AbstractStart extends javax.swing.JFrame implements  ActionListener {
	protected JPanel jPanel1;
	public JTextArea remark;
	protected JButton chooseButton;
	protected JButton impButton;
	private JLabel userLabel;
	public JComboBox yearComboBox;
	private JLabel dateLabel;
	public JTextField usernameTextField;
	protected JComboBox jComboBox;
	protected JTable dataTable;
	protected JScrollPane jScrollPane1;
	public JComboBox monthComboBox;

	protected final static String COMBOVALUE1 = "��ļ������ñ�";
	protected final static String COMBOVALUE2 = "��˾���������뻮����ϸ��";
	protected final static String COMBOVALUE3 = "β��Ӷ����ܱ�";
	protected final static String COMBOVALUE4 = "β��Ӷ����ܱ�_����";
	protected final static String COMBOVALUE5 = "ֱ����������ϸ";
	protected final static String COMBOVALUE6 = "ר��������ñ�";
	protected final static String COMBOVALUE7 = "��˾����Ѽ�ҵ�����껮����ϸ��";
	protected final static String COMBOVALUE8 = "��˾���۷���ѻ�����ϸ��";
	
	/**
	 * namemap
	 */
	private Map<String, String> map = new HashMap<String, String>();

	DefaultTableModel defaultTableModel = null;

	public AbstractStart() {
		super();
		initGUI();
		
		initData();
//		initNameList();
	}

	

	private void initGUI() {
		try {
			setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
			BorderLayout thisLayout = new BorderLayout();
			getContentPane().setLayout(thisLayout);
			this.setTitle("voucherimp");
			this.setPreferredSize(new java.awt.Dimension(1024, 760));
			{
				jPanel1 = new JPanel();
				getContentPane().add(jPanel1, BorderLayout.NORTH);
				jPanel1.setLayout(null);
				jPanel1.setPreferredSize(new java.awt.Dimension(1008, 707));
				jPanel1.setSize(1024, 760);
				{
					dataTable = new JTable();
					dataTable.setLayout(null);
					dataTable.setSize(500, 800);
					dataTable.setAutoResizeMode(JTable.AUTO_RESIZE_NEXT_COLUMN);
//					jScrollPane1 = new JScrollPane(dataTable,ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS,ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER );
					jScrollPane1 = new JScrollPane(dataTable);
					jPanel1.add(jScrollPane1);
					jScrollPane1.setBounds(0, 112, 987, 606);
					{
//						jScrollPane1.setViewportView(dataTable);
						dataTable.setPreferredSize(new java.awt.Dimension(984, 9999));
						dataTable.setAutoResizeMode(JTable.AUTO_RESIZE_NEXT_COLUMN);
//						jTable1.setModel(jTable1Model);
					}
				}
				{
					remark = new JTextArea();
					jPanel1.add(remark);
					remark.setText("��־��Ϣ");
					remark.setBounds(12, 34, 978, 64);
				}
				{
					chooseButton = new JButton();
					jPanel1.add(chooseButton);
					chooseButton.setText("ѡ���ļ�");
					chooseButton.setBounds(231, 5, 104, 23);
					chooseButton
							.setToolTipText("\u9009\u62e9\u8981\u5145\u503c\u7684excel\u6587\u4ef6");
					chooseButton.addActionListener(this);
				}
				{
					ComboBoxModel jComboBox1Model = 
						new DefaultComboBoxModel(
								new String[] { COMBOVALUE7, COMBOVALUE8 });
					jComboBox = new JComboBox();
					jComboBox.setModel(jComboBox1Model);
					jComboBox.setBounds(6, 4, 208, 24);
					jComboBox.addActionListener(this);
					jPanel1.add(jComboBox);
				}
				{
					impButton = new JButton();
					jPanel1.add(impButton);
					impButton.setText("����");
					impButton.setBounds(445, 7, 84, 21);
					impButton.setToolTipText("\u5c06\u9009\u62e9\u7684Excel\u6587\u4ef6\u6570\u636e\u8bfb\u53d6\u5230\u754c\u9762");
					impButton.addActionListener(this);
				}
				{
					userLabel = new JLabel();
					jPanel1.add(userLabel);
					userLabel.setText("\u7528\u6237:");
					userLabel.setBounds(693, 7, 42, 21);
				}
				{
					usernameTextField = new JTextField();
					jPanel1.add(usernameTextField);
					usernameTextField.setText("username");
					usernameTextField.setBounds(742, 7, 63, 21);
				}

			}

			pack();
			this.setSize(1024, 778);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	protected void initData() {
		//����Ĭ�ϵ�tablemodel.
		
		defaultTableModel = new TableModel7();
		dataTable.setModel(defaultTableModel);
		dataTable.getTableHeader().setBounds(0, 0, 984, 0);
	}
	

	/**
	 * ѡ���ļ�
	 */
	protected boolean chooseButtonKeyReleased() {
		JFileChooser chooser = new JFileChooser();
//		FileNameExtensionFilter filter = new FileNameExtensionFilter("xls",
//				"xls");
//		chooser.setFileFilter(filter);
		int returnVal = chooser.showOpenDialog(this);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			return analyseFile(chooser.getSelectedFile());
		}
		return false;
	}
	
	
	/**
	 * ѡ��ת���ļ�
	 */
	private void convertButtonKeyReleased() {
		JFileChooser chooser = new JFileChooser();
//		FileNameExtensionFilter filter = new FileNameExtensionFilter("xls",
//				"xls");
//		chooser.setFileFilter(filter);
		int returnVal = chooser.showOpenDialog(this);
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			analyseFile(chooser.getSelectedFile());
		}
	}
	
	File selectedFile = null;
	
	File newfile = null;
	/**
	 * ����excel�ļ�,�����ݷŵ�����ͻ�����.
	 * 
	 * @param file
	 */
	private boolean analyseFile(File choosefile) {
		if (choosefile == null) {
			System.out.println("ѡ����ļ�Ϊ��");
			return false;
		}
		if (!choosefile.getName().endsWith("xls")) {
			JOptionPane.showMessageDialog(this, "��������ļ����Ͳ���ȷ,ֻ����xls��ʽ���ļ���");
			selectedFile = null;
			return false;
		}
		selectedFile = choosefile;
		return true;
	}

    
    /**
	 * �����ļ�ѡ������û�ѯ�ʵ����ļ���
	 * 
	 * @param mode the mode of the dialog; either
	 *              <code>FileDialog.LOAD</code> or <code>FileDialog.SAVE</code>
	 * @return ����û�ѡ��ȡ������ null�����򷵻������ļ���(·��+����)
	 */
	private String getFilename(String month) {
		FileDialog fileDialog = new FileDialog(this,"�ļ�����",FileDialog.SAVE);
//		UIFileChooser filechoose = getFileChooseDlg();
//		.showSaveDialog(this);
		fileDialog.setFile("output_" + month.replace("-","") + ".xls");
		fileDialog.setVisible(true);
		String directory = fileDialog.getDirectory();
		String file = fileDialog.getFile();
		if (isEmpty(directory) || isEmpty(file)) {
			return null;
		} else {
			String filename = directory + file;
			return filename;
		}
	}
	
	/**
	 * ���뵥Ԫ��,����û���������,ͨ��ѭ������ƶ���Ԫ��ʵ��.
	 * fans.fan
	 * @param row
	 * @param i
	 */
	private void insertCell(HSSFRow row,int i){
		int lastcellnum = row.getLastCellNum(); //���һ����Ԫ��
		HSSFCell cell = null;
		for (; lastcellnum <= i  ; lastcellnum--) {
			cell = row.getCell(lastcellnum);
			row.moveCell(cell, (short) (lastcellnum + 1));
		}
		
	}
	
	
	public static boolean isEmpty(String str)
    {
        return str == null || str.length() == 0;
    }

	public void actionPerformed(ActionEvent e) {
		
		System.out.println("actionPerformed" + e.getSource());
	}

}

class TableModel1 extends DefaultTableModel{
	static Object[] columnnames = new Object[]{"����","�������","��������","����ѻ���","�йܷ�","���۷���","��֤��"};
	public TableModel1(){
		super(columnnames,0);
	}
}
//��������	�ϼ�	ֱ���Ϲ���	ֱ���깺��	ֱ����ط�	ֱ��ת����	ֱ�������	ֱ�����۷����	��˾�Ϲ���	��˾�깺��	��˾��ط�	��˾ת����	��˾�����	��˾����깺��
class TableModel2 extends DefaultTableModel{
	static Object[] columnnames = new Object[]{"��������","�ϼ�","ֱ���Ϲ���","ֱ���깺��","ֱ����ط�","ֱ��ת����","ֱ�������","ֱ�����۷����","��˾�Ϲ���","��˾�깺��","��˾��ط�","��˾ת����","��˾�����","��˾����깺��"};
	public TableModel2(){
		super(columnnames,0);
	}
}
//������				�������			��������				�йܽ���ۼ�							�йܷݶ��ۼ�						ƽ���йܽ��					β��Ӷ��			
class TableModel3 extends DefaultTableModel{
	static Object[] columnnames = new Object[]{"������","�������","��������","�йܽ���ۼ�","�йܷݶ��ۼ�","ƽ���йܽ��","β��Ӷ��"};
	public TableModel3(){
		super(columnnames,0);
	}
}
//�����̴���			������			�������						��������			ƽ���йܷݶ�						ƽ���йܽ��					β��Ӷ��	
class TableModel4 extends DefaultTableModel{
	static Object[] columnnames = new Object[]{"�����̴���","������","�������","��������","ƽ���йܷݶ�","ƽ���йܽ��","β��Ӷ��"};
	public TableModel4(){
		super(columnnames,0);
	}
}
//����	���ۻ���	��������	ҵ������	��������	ȷ�Ͻ��	����	������	�����ۻ���
class TableModel5 extends DefaultTableModel{
	static Object[] columnnames = new Object[]{"����","���ۻ���","��������","ҵ������","�������","��������","ȷ�Ͻ��","����","������","�����ۻ���"};
	public TableModel5(){
		super(columnnames,0);
	}
}
//����	�������	��������	����ѻ���	�йܷ�	���۷���
class TableModel6 extends DefaultTableModel{
	static Object[] columnnames = new Object[]{"����","�������","��������","����","ְԱ","����ѻ���","�йܷ�","���۷���"};
	public TableModel6(){
		super(columnnames,0);
	}
}
//����ʱ��	��Ŀ����	��Ŀ����	����	��Ŀ�Ŷ�	ְԱ	��Ŀ����	�����	ҵ������
class TableModel7 extends DefaultTableModel{
	static Object[] columnnames = new Object[]{"����ʱ��","��Ŀ����","��Ŀ����","����","��Ŀ�Ŷ�","ְԱ","��Ŀ����","�����","ҵ������"};
	public TableModel7(){
		super(columnnames,0);
	}
}

//����ʱ��	��Ŀ����	��Ŀ����	����	��Ŀ�Ŷ�	ְԱ	��Ŀ����	���۷����
class TableModel8 extends DefaultTableModel{
	static Object[] columnnames = new Object[]{"����ʱ��","��Ŀ����","��Ŀ����","����","��Ŀ�Ŷ�","ְԱ","��Ŀ����","���۷����"};
	public TableModel8(){
		super(columnnames,0);
	}
}