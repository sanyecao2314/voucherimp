package com.citsamex.core.startup;

import java.awt.BorderLayout;
import java.awt.FileDialog;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
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
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import com.citsamex.core.util.CopyFileUtil;
import com.citsamex.core.util.DBUtil;

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

	protected final static String COMBOVALUE1 = "公募基金费用表";
	protected final static String COMBOVALUE2 = "公司手续费收入划拨明细表";
	protected final static String COMBOVALUE3 = "尾随佣金汇总表";
	protected final static String COMBOVALUE4 = "尾随佣金汇总表_比例";
	protected final static String COMBOVALUE5 = "直销手续费明细";
	protected final static String COMBOVALUE6 = "专户基金费用表";
	
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
					remark.setText("日志信息");
					remark.setBounds(12, 34, 978, 64);
				}
				{
					chooseButton = new JButton();
					jPanel1.add(chooseButton);
					chooseButton.setText("选择文件");
					chooseButton.setBounds(131, 5, 104, 23);
					chooseButton
							.setToolTipText("\u9009\u62e9\u8981\u5145\u503c\u7684excel\u6587\u4ef6");
					chooseButton.addActionListener(this);
				}
				{
					ComboBoxModel jComboBox1Model = 
						new DefaultComboBoxModel(
								new String[] { COMBOVALUE1, COMBOVALUE2, COMBOVALUE3, COMBOVALUE4, COMBOVALUE5, COMBOVALUE6 });
					jComboBox = new JComboBox();
					jComboBox.setModel(jComboBox1Model);
					jComboBox.setBounds(6, 4, 108, 24);
					jComboBox.addActionListener(this);
					jPanel1.add(jComboBox);
				}
				{
					impButton = new JButton();
					jPanel1.add(impButton);
					impButton.setText("导入");
					impButton.setBounds(345, 7, 84, 21);
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
				{
					dateLabel = new JLabel();
					jPanel1.add(dateLabel);
					dateLabel.setText("\u671f\u95f4:");
					dateLabel.setBounds(819, 7, 35, 21);
				}
				{
					Calendar cal = Calendar.getInstance();
					int currYear = cal.get(Calendar.YEAR);
					
					ComboBoxModel yearComboBoxModel = 
						new DefaultComboBoxModel(
								new String[] { String.valueOf(currYear - 1),String.valueOf(currYear), String.valueOf(currYear + 1) });
					yearComboBox = new JComboBox();
					jPanel1.add(yearComboBox);
					yearComboBox.setModel(yearComboBoxModel);
					yearComboBox.setBounds(854, 7, 56, 21);
				}
				{
					ComboBoxModel monthComboBoxModel = 
						new DefaultComboBoxModel(
								new String[] { "01", "02", "03", "04", "05", "06", "07", "08", "09", "10", "11", "12" });
					monthComboBox = new JComboBox();
					jPanel1.add(monthComboBox);
					monthComboBox.setModel(monthComboBoxModel);
					monthComboBox.setBounds(938, 7, 42, 21);
				}
				{
//					kdDatePicker = new KDDatePicker();
//					jPanel1.add(kdDatePicker);
//					kdDatePicker.setBounds(868, 7, 91, 21);
				}

			}

			pack();
			this.setSize(1024, 778);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	protected void initData() {
		//设置默认的tablemodel.
		
		defaultTableModel = new TableModel1();
		dataTable.setModel(defaultTableModel);
		dataTable.getTableHeader().setBounds(0, 0, 984, 0);
	}
	
//	private void initNameList(){
//		newfile = new File("C:\\bayerreport\\model\\namelist.xls");
//		HSSFWorkbook rwb = null;// 声明一个工作簿。
//		HSSFSheet sht = null;// 声明一个工作表。
//		HSSFRow row = null;
//		HSSFCell cell0 = null;// 声明一个单元格。
//		HSSFCell cell1 = null;// 声明一个单元格。
//		InputStream io = null;
//		try {
//			io = new FileInputStream(newfile);
//			rwb = new HSSFWorkbook(io);
//			sht = rwb.getSheetAt(0);
//			StringBuffer sb = new StringBuffer();
//			for (int i = 1,rows = sht.getLastRowNum(); i <= rows; i++) {
//				row  = sht.getRow(i);
//				//pingyin
//				cell0 = row.getCell(0);
//				//汉字
//				cell1 = row.getCell(1);
//				if (cell0 != null && cell0.getStringCellValue() != null
//						&& !"".equals(cell0.getStringCellValue().trim())
//						&& cell1 != null && cell1.getStringCellValue() != null
//						&& !"".equals(cell1.getStringCellValue().trim())) {
//					String pingyin = cell0.getStringCellValue().trim().replace(" ", "").replace("/", "").toUpperCase();
//					String hanzi = cell1.getStringCellValue().trim().replace(" ", "").replace("/", "");
//					if (map.containsKey(pingyin)) {
//						sb.append("有相同的拼音姓名:" + pingyin + "\n");
//					}else{
//						map.put(pingyin, hanzi);
//					}
//				}
//			}
//			if (sb.length() > 0) {
//				remark.setText(sb.toString());
//			}
//			
//			io.close();
//		} catch (Exception e) {
//			e.printStackTrace();
//		}finally{
//		}
//	}


	/**
	 * 运行,将数据提交到系统中.
	 */
//	protected void runButtonKeyReleased() {
////		this.chooseButton.setEnabled(false);
//		
//		if (file == null) {
//			JOptionPane.showMessageDialog(this, "请先选择文件!");
//			return ;
//		}
//		
//		System.out.println(file.getAbsolutePath());
//		String path = file.getAbsolutePath();
////		path.
//		String newpath = path.substring(0, path.length()-4) + "-new.xls";
//		boolean issuccopy = CopyFileUtil.copyFile(file.getAbsolutePath(), newpath, true);
//		//拷贝成功
//		if (issuccopy) {
//			newfile = new File(newpath);
//			HSSFWorkbook rwb = null;// 声明一个工作簿。
//			HSSFSheet sht = null;// 声明一个工作表。
//			HSSFRow row = null;
//			HSSFCell cell = null;// 声明一个单元格。
//			Connection conn = null;
//			try {
//				InputStream io = new FileInputStream(newfile);
//				rwb = new HSSFWorkbook(io);
//				sht = rwb.getSheet("Report");
//				if (sht == null) {
//					JOptionPane.showMessageDialog(this, "不存在Report页签");
//					return;
//				}
//				//标题行
//				row = sht.getRow(6);
//				//插入标题
//				insertCell(row, 6);
//				row.getCell(6).setCellValue("name_cn");
//				conn = DBUtil.getConnection();
//				String sql = "select pname from dbo.T_BLUESKY_ORD_TKTDATA where ticketnumber ='";
//				for (int i = 7,rows = sht.getLastRowNum(); i < rows; i++) {
//					row = sht.getRow(i);
//					insertCell(row, 6);
//					
//					//ticketno
//					cell = row.getCell(5);
//					if (cell != null && cell.getStringCellValue() != null
//							&& !"".equals(cell.getStringCellValue().trim())) {
//						Object obj = DBUtil.querySqlUniqueResult(conn, sql + cell.getStringCellValue().trim() +"'", null);
//						row.getCell(6).setCellValue(String.valueOf(obj));
//					}
//					//分类描述(ProjectNo)
//					cell = row.getCell(4);
//					if (cell != null && cell.getStringCellValue() != null
//							&& !"".equals(cell.getStringCellValue().trim())) {
//						String projectno = cell.getStringCellValue().trim();
//						if (projectno.indexOf("Int'l") != -1) {	//国际票
//							//pax name
//							cell = row.getCell(1);
//							if (cell != null && cell.getStringCellValue() != null
//									&& !"".equals(cell.getStringCellValue().trim())) {
//								String paxname = cell.getStringCellValue().trim().replace(" ", "").replace("/", "").toUpperCase();
//								if (map.containsKey(paxname)) {
//									row.getCell(6).setCellValue(map.get(paxname));
//								}
//							}
//						}
//					}
//				}
//				
//				// 新建一输出文件流
//				FileOutputStream fOut = new FileOutputStream(newpath);
//				// 把相应的Excel 工作簿存盘
//				rwb.write(fOut);
//				fOut.flush();
//				// 操作结束，关闭文件
//				fOut.close();
//			}catch(Exception e){
//				System.out.println(e);
//				e.printStackTrace();
//			}finally{
//				if(conn != null){
//					try {
//						conn.close();
//					} catch (SQLException e) {
//						e.printStackTrace();
//					}
//				}
//			}
//			remark.setText("处理完成,新文件路径:"+newfile.getAbsolutePath());
//			JOptionPane.showMessageDialog(this, "处理完成,新文件路径:"+newfile.getAbsolutePath());
//		}else{
//			JOptionPane.showMessageDialog(this, "复制文件异常");
//		}
//	}

	/**
	 * 选择文件
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
	 * 选择转换文件
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
	
	/**
	 * 转换文件
	 */
	private void convertRunButtonKeyReleased() {
		ConvertFile cf = new ConvertFile();
		cf.doConvertRun(this, selectedFile);
	}
	

	File selectedFile = null;
	
	File newfile = null;
	/**
	 * 解析excel文件,将数据放到界面和缓存中.
	 * 
	 * @param file
	 */
	private boolean analyseFile(File choosefile) {
		if (choosefile == null) {
			System.out.println("选择的文件为空");
			return false;
		}
		if (!choosefile.getName().endsWith("xls")) {
			JOptionPane.showMessageDialog(this, "您输入的文件类型不正确,只运行xls格式的文件！");
			selectedFile = null;
			return false;
		}
		selectedFile = choosefile;
		return true;
	}

    
    /**
	 * 弹出文件选择框向用户询问导出文件名
	 * 
	 * @param mode the mode of the dialog; either
	 *              <code>FileDialog.LOAD</code> or <code>FileDialog.SAVE</code>
	 * @return 如果用户选择取消返回 null，否则返回完整文件名(路径+名称)
	 */
	private String getFilename(String month) {
		FileDialog fileDialog = new FileDialog(this,"文件保存",FileDialog.SAVE);
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
	 * 插入单元格,本身没有这个功能,通过循环向后移动单元格实现.
	 * fans.fan
	 * @param row
	 * @param i
	 */
	private void insertCell(HSSFRow row,int i){
		int lastcellnum = row.getLastCellNum(); //最后一个单元格
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
	static Object[] columnnames = new Object[]{"日期","基金代码","基金名称","管理费汇总","托管费","销售费用","保证金"};
	public TableModel1(){
		super(columnnames,0);
	}
}
//基金名称	合计	直销认购费	直销申购费	直销赎回费	直销转换费	直销补差费	直销销售服务费	公司认购费	公司申购费	公司赎回费	公司转换费	公司补差费	公司后端申购费
class TableModel2 extends DefaultTableModel{
	static Object[] columnnames = new Object[]{"基金名称","合计","直销认购费","直销申购费","直销赎回费","直销转换费","直销补差费","直销销售服务费","公司认购费","公司申购费","公司赎回费","公司转换费","公司补差费","公司后端申购费"};
	public TableModel2(){
		super(columnnames,0);
	}
}
//销售商				基金代码			基金名称				托管金额累计							托管份额累计						平均托管金额					尾随佣金			
class TableModel3 extends DefaultTableModel{
	static Object[] columnnames = new Object[]{"销售商","基金代码","基金名称","托管金额累计","托管份额累计","平均托管金额","尾随佣金"};
	public TableModel3(){
		super(columnnames,0);
	}
}
//销售商代码			销售商			基金代码						基金名称			平均托管份额						平均托管金额					尾随佣金	
class TableModel4 extends DefaultTableModel{
	static Object[] columnnames = new Object[]{"销售商代码","销售商","基金代码","基金名称","平均托管份额","平均托管金额","尾随佣金"};
	public TableModel4(){
		super(columnnames,0);
	}
}
//日期	销售机构	申请日期	业务类型	基金名称	确认金额	费率	手续费	归销售机构
class TableModel5 extends DefaultTableModel{
	static Object[] columnnames = new Object[]{"日期","销售机构","申请日期","业务类型","基金代码","基金名称","确认金额","费率","手续费","归销售机构"};
	public TableModel5(){
		super(columnnames,0);
	}
}
//日期	基金代码	基金名称	管理费汇总	托管费	销售费用
class TableModel6 extends DefaultTableModel{
	static Object[] columnnames = new Object[]{"日期","基金代码","基金名称","部门","职员","管理费汇总","托管费","销售费用"};
	public TableModel6(){
		super(columnnames,0);
	}
}