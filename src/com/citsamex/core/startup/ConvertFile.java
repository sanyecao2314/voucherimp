package com.citsamex.core.startup;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.JOptionPane;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;


/**
 * 转换文件
 * 
 * @author fans.fan
 * 
 */
public class ConvertFile {

	File file = null;
	// File newfile = null;

	AbstractStart start = null;

	/**
	 * 所有正常请款, key=name_cn value
	 */
	Map<String, List<String[]>> dommap = new HashMap<String, List<String[]>>();

	/**
	 * 所有退票, key=name_cn+ticketNo value=退票费用
	 */
	Map<String, String> bnctmap = new HashMap<String, String>();

	/**
	 * 所有改签Reissue, key=name_cn+RoutingCode value=改签费用
	 */
	Map<String, String> reissuemap = new HashMap<String, String>();

	public String doConvertRun(AbstractStart start, File file) {

		this.start = start;

		if (file == null) {
			JOptionPane.showMessageDialog(start, "请先选择文件!");
			return null;
		}

		String path = file.getAbsolutePath();
		// path.
		// String newpath = path.substring(0, path.length() - 4) +
		// "-convert.xls";
		// boolean issuccopy = CopyFileUtil.copyFile(file.getAbsolutePath(),
		// newpath, true);
		// // 拷贝成功
		// if (!issuccopy) {
		// JOptionPane.showMessageDialog(start, "复制文件失败");
		// return null;
		// }
		HSSFWorkbook rwb = null;// 声明一个工作簿。
		HSSFSheet sht = null;// 声明一个工作表。
		HSSFRow row = null;

		StringBuffer sb = new StringBuffer();
		try {
			InputStream io = new FileInputStream(file);
			rwb = new HSSFWorkbook(io);
			sht = rwb.getSheet("Report");
			if (sht == null) {
				JOptionPane.showMessageDialog(start, "不存在Report页签");
				return null;
			}
			for (int i = 7, rows = sht.getLastRowNum(); i < rows; i++) {
				row = sht.getRow(i);
				validRowData(row, i + 1);
			}

			processMapData(path);

//			// 新建一输出文件流
//			FileOutputStream fOut = new FileOutputStream(path);
//			// 把相应的Excel 工作簿存盘
//			rwb.write(fOut);
//			fOut.flush();
//			// 操作结束，关闭文件
//			fOut.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
//		start.remark.setText(start.remark.getText() + "\n" + "处理完成,新文件路径:"
//				+ file.getAbsolutePath());
//		JOptionPane.showMessageDialog(start,
//				"处理完成,新文件路径:" + file.getAbsolutePath());

		return null;
	}

	private boolean isNotNull(HSSFCell cell) {
		if (cell == null) {
			return false;
		}

//		cell.getCellType();
//		cell.setCellType(HSSFCell.CELL_TYPE_STRING);
		if (cell.getCellType() == HSSFCell.CELL_TYPE_STRING) {
			try {
				if (cell.getStringCellValue() == null) {
					return false;
				}
				if ("".equals(cell.getStringCellValue().trim())) {
					return false;
				}
			} catch (Exception e) {
			}
		}
		return true;
	}

	/**
	 * @param row
	 * @param rownum
	 * @return
	 */
	/**
	 * @param row
	 * @param rownum
	 * @return
	 */
	private String validRowData(HSSFRow row, int rownum) {
		HSSFCell cell = null;// 声明一个单元格。

		/**
		 * 0-InvoiceNo;4-分类描述(projectNo);5-TicketNo;6-name_cn;7-AirlineCode;8-
		 * FlightNo;9-ClassCode;
		 * 10-RoutingCode;11-DepartureDate;12-ArrDate;13-DepartTime
		 * ;14-ArrTime,22-RealSales
		 */
		int[] rows = new int[] { 0, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 22 };
		String[] strs = new String[13];

		for (int i = 0; i < rows.length; i++) {
			cell = row.getCell(rows[i]);
			if (!isNotNull(cell)) {
				int temprow = rows[i] + 1;
				return "第" + rownum + "行,第" + temprow + "列,值为空.\n";
			}
			if (cell.getCellType() == HSSFCell.CELL_TYPE_STRING) {
				strs[i] = cell.getStringCellValue().trim().toUpperCase();
			} else if (cell.getCellType() == HSSFCell.CELL_TYPE_NUMERIC) {
//				HSSFCell.CELL_TYPE_BLANK;3
				strs[i] = String.valueOf(cell.getNumericCellValue());
			} else {
				System.out.println("new CellType=" + cell.getCellType());
			}
		}

		// 改签
		if (strs[1].indexOf("REISSUE") != -1) {
			if (reissuemap.containsKey(strs[3] + strs[7])) {
				BigDecimal realsales = new BigDecimal(reissuemap.get(strs[3]
						+ strs[7]));
				BigDecimal nowrealsales = new BigDecimal(strs[12]);
				reissuemap.put(strs[3] + strs[7],
						String.valueOf(realsales.add(nowrealsales)));
			} else {
				reissuemap.put(strs[3] + strs[7], strs[12]);
			}
			return null;
		}

		// 退票
		if (strs[1].indexOf("DOM") != -1 && strs[0].startsWith("BNCT")) {
			bnctmap.put(strs[3] + strs[2], strs[12]);
			return null;
		}

		// 请款
		if (strs[1].indexOf("DOM") != -1) {
			Collection<String[]> coll = getColl(strs);
			if (dommap.containsKey(strs[3])) {
				List<String[]> domlist = dommap.get(strs[3]);
				domlist.addAll(coll);
				dommap.put(strs[3], domlist);
			} else {
				List<String[]> domlist = new ArrayList<String[]>();
				domlist.addAll(coll);
				dommap.put(strs[3], domlist);
			}
			return null;
		}
		return null;
	}

	/**
	 * @param strs
	 *            0-InvoiceNo;4-分类描述(projectNo);5-TicketNo;
	 *            6-name_cn;7-AirlineCode;8-FlightNo;9-ClassCode;
	 *            10-RoutingCode;11-DepartureDate
	 *            ;12-ArrDate;13-DepartTime;14-ArrTime,22-RealSales
	 * @return Collection
	 */
	private Collection<String[]> getColl(String[] strs) {
		// String[] airlineCodes = strs[4].split("/");
		String[] flightNos = strs[5].split("/");
		String[] classCodes = strs[6].split("/");
		String[] routingCodes = strs[7].split("/");

		Collection<String[]> coll = new ArrayList<String[]>();

		// 出发日期 出发城市 到达地 航班号 舱位 机票费用 改签费用 invno name ticketno
		String[] departures = new String[] { strs[8],
				routingCodes[0].split("-")[0], routingCodes[0].split("-")[1],
				flightNos[0], classCodes[0], strs[12], strs[0], strs[3],
				strs[10], strs[11],strs[2] };
		coll.add(departures);
		int len = flightNos.length;
		if (len > 1) {
			String[] arrs = new String[] { strs[9],
					routingCodes[len - 1].split("-")[0],
					routingCodes[len - 1].split("-")[1], flightNos[len - 1],
					classCodes[len - 1], strs[12], strs[0], strs[3], strs[10],
					strs[11],strs[2] };
			coll.add(arrs);
		}
		return coll;
	}

	/**
	 * 处理map中的数据
	 */
	private String processMapData(String path) {

		if (dommap.size() <= 0) {
			return null;
		}

		String newpath = path.substring(0, path.length() - 4) + "-convert.xls";
		HSSFWorkbook wb = new HSSFWorkbook();
		HSSFSheet sheet = wb.createSheet("sheet1");
		HSSFRow row = null;
		List<String[]> lists = null;
		Iterator<String> it = dommap.keySet().iterator();
		
		addHeadRow(sheet.createRow(0));
		int i = 1;
		while (it.hasNext()) {
			String key = it.next();
			row = sheet.createRow(i);
			lists = dommap.get(key);
			try {
				processRowData(row, lists);
				i++;
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}

		try {
			FileOutputStream fileOut = new FileOutputStream(newpath);
			wb.write(fileOut);
			fileOut.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		

		return null;
	}

	/**
	 * 添加标题行
	 * @param createRow
	 */
	private void addHeadRow(HSSFRow row) {
				row.createCell(0).setCellValue("姓名");
				// 出发日期
				row.createCell(1).setCellValue("出发日期");
				// 出发城市
				row.createCell(2).setCellValue("出发城市");
				// 到达地
				row.createCell(3).setCellValue("到达地");
				// 航班号
				row.createCell(4).setCellValue("航班号");
				// 舱位
				row.createCell(5).setCellValue("舱位");
				// 机票费用
				row.createCell(6).setCellValue("机票费用");
				// 改签费用
				row.createCell(7).setCellValue("改签费用");
				// 退票费用
				row.createCell(8).setCellValue("退票费用");
				// 起飞时间
				row.createCell(9).setCellValue("起飞时间");
				// 落地时间
				row.createCell(10).setCellValue("落地时间");

				// 返程日期
				row.createCell(11).setCellValue("返程日期");
				// 出发地
				row.createCell(12).setCellValue("出发地");
				// 到达城市
				row.createCell(13).setCellValue("到达城市");
				// 航班号
				row.createCell(14).setCellValue("航班号");
				// 舱位
				row.createCell(15).setCellValue("舱位");
				// 机票费用
				row.createCell(16).setCellValue("机票费用");
				// 改签费用
				row.createCell(17).setCellValue("改签费用");
				// 退票费用
				row.createCell(18).setCellValue("退票费用");
				// 起飞时间
				row.createCell(19).setCellValue("起飞时间");
				// 落地时间
				row.createCell(20).setCellValue("落地时间");
	}

	/**
	 * 将客人信息写入这一行.
	 * 
	 * @param row
	 * @param lists
	 * @throws ParseException
	 */
	private void processRowData(HSSFRow row, List<String[]> lists)
			throws ParseException {
		if (lists == null || lists.size() <= 0) {
			start.remark.setText(start.remark.getText() + "\n" + "");
		}
		String[] departures = null;
		String[] arrs = null;

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-ddHHmm");

		int size = lists.size();
		departures = lists.get(0);
		String sss = departures[7];
		arrs = lists.get(size - 1);
		//出发日期
		Date departuredate = sdf.parse(departures[0] + departures[8]);
		Date arrsdate = sdf.parse(arrs[0] + arrs[8]);

		String[] strs = null;
		for (int i = 0; i < size; i++) {
			strs = lists.get(i);
			Date tempDate = sdf.parse(strs[0] + strs[8]);
			if (tempDate.before(departuredate)) {
				departures = strs;
			}
			if (tempDate.after(arrsdate)) {
				arrsdate = tempDate;
				arrs = strs;
			}
		}
		
		if (departures[0].equals(arrs[0]) && departures[1].equals(arrs[1])) {
			arrs = null;
		}
		// 姓名
		row.createCell(0).setCellValue(departures[7]);
		// 出发日期
		row.createCell(1).setCellValue(departures[0]);
		// 出发城市
		row.createCell(2).setCellValue(departures[1]);
		// 到达地
		row.createCell(3).setCellValue(departures[2]);
		// 航班号
		row.createCell(4).setCellValue(departures[3]);
		// 舱位
		row.createCell(5).setCellValue(departures[4]);
		// 机票费用
		row.createCell(6).setCellValue(Double.valueOf(departures[5]));
		// 改签费用
		String key = departures[7] + departures[1] + "-" + departures[2];
		row.createCell(7).setCellValue(Double.valueOf(
				reissuemap.containsKey(key) ? reissuemap.get(key) : "0"));
		reissuemap.remove(key);
		// 退票费用
		key = departures[7] + departures[10];
		row.createCell(8).setCellValue(Double.valueOf(
				bnctmap.containsKey(key) ? bnctmap.get(key) : "0"));
		bnctmap.remove(key);
		// 起飞时间
		row.createCell(9).setCellValue(departures[8]);
		// 落地时间
		row.createCell(10).setCellValue(departures[9]);

		if (arrs != null) {
			// 返程日期
			row.createCell(11).setCellValue(arrs[0]);
			// 出发地
			row.createCell(12).setCellValue(arrs[1]);
			// 到达城市
			row.createCell(13).setCellValue(arrs[2]);
			// 航班号
			row.createCell(14).setCellValue(arrs[3]);
			// 舱位
			row.createCell(15).setCellValue(arrs[4]);
			// 机票费用
			row.createCell(16).setCellValue(Double.valueOf(arrs[5]));
			// 改签费用
			key = arrs[7] + arrs[1] + "-" + arrs[2];
			row.createCell(17).setCellValue(Double.valueOf(
					reissuemap.containsKey(key) ? reissuemap.get(key) : "0"));
			reissuemap.remove(key);
			// 退票费用
			key = arrs[7] + arrs[10];
			row.createCell(18).setCellValue(Double.valueOf(
					bnctmap.containsKey(key) ? bnctmap.get(key) : "0"));
			bnctmap.remove(key);
			// 起飞时间
			row.createCell(19).setCellValue(arrs[8]);
			// 落地时间
			row.createCell(20).setCellValue(arrs[9]);
		}
	}
}
