package org.syncon.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.LinkedList;

import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

// Update the excel with all the results and bug Ids
public class FinalExcelUpdater {

	static Logger log = Logger.getLogger(FinalExcelUpdater.class.getName());
	final static private String filepath = ExcelUpdate.getfilepath;

	public void updateExcel(LinkedList<String> elements) throws IOException{
		log.info("Final Updation of the Excel Started");
		FileInputStream fs = new FileInputStream(filepath);
		@SuppressWarnings("resource")
		HSSFWorkbook workbook = new HSSFWorkbook(fs);
		HSSFSheet firstSheet = workbook.getSheetAt(0);
		for (int i=0;i<elements.size();i+=2) {
			int foundNumber = findRow(firstSheet,elements.get(i));
			if (foundNumber>0) {
				firstSheet.getRow(foundNumber).getCell(5).setCellValue(elements.get(i+1));
			}
		}
		FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(new File(filepath));
			workbook.write(fos);
		} catch (IOException e) {
			log.error("During Final Updation File not found / Error occured while reading the file");
		} 
		catch (IllegalArgumentException m) {
		} finally {
			if (fos != null) {
				try {
					fos.flush();
					fos.close();
				} catch (IOException e) {
					log.error("During Final Updation File not found / Error occured while reading the file");
				}
			}
		}
		log.info("Final Updation of the Excel Completed");
	}

	private int findRow(HSSFSheet sheet, String cellContent) {
		for (Row row : sheet) {
			for (Cell cell : row) {
				if (cell.getCellType() == Cell.CELL_TYPE_STRING) {
					if (cell.getStringCellValue().equals(cellContent)) {
						return row.getRowNum();  
					}
				}
			}
		}
		return 0;               

	}
}
