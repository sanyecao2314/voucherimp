package com.citsamex.core.util;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.ss.usermodel.Cell;

public class ExcelUtil {

	
	
	public static String getCellValue(Cell cell) throws Exception{
		
		if(cell == null ) return null;
 		
		if (cell.getCellType() == HSSFCell.CELL_TYPE_STRING) {
			return cell.getStringCellValue().trim().toUpperCase();
		} else if (cell.getCellType() == HSSFCell.CELL_TYPE_NUMERIC) {
			return String.valueOf(cell.getNumericCellValue());
		} else if (cell.getCellType() == HSSFCell.CELL_TYPE_BLANK) {
			return "";
		} else if (cell.getCellType() == HSSFCell.CELL_TYPE_FORMULA) {
			return String.valueOf(cell.getNumericCellValue());
		}
		else {
			System.out.println("new CellType=" + cell.getCellType());
			throw new Exception("error CellType=" + cell.getCellType());
		}
	}
}
