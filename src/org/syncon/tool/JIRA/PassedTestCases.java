package org.syncon.tool.JIRA;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.LinkedList;

import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.syncon.utils.ExcelUpdate;

public class PassedTestCases {

	Cell cell;
	HSSFRow rowA ;
	static HSSFSheet firstSheet;
	private static int total;
	static LinkedList<String> passtatus = new LinkedList<>();
	final private static String filepath = ExcelUpdate.getfilepath;
	static Logger log = Logger.getLogger(PassedTestCases.class.getName());

	// It returns the list of test cases which
	// passed in this current run. Along with their previous status.

	static LinkedList<String> updateFailedToPassed() throws IOException {
		FileInputStream fs = new FileInputStream(filepath);
		@SuppressWarnings("resource")
		HSSFWorkbook workbook = new HSSFWorkbook(fs);
		firstSheet = workbook.getSheetAt(0);
		total=firstSheet.getLastRowNum()+1;

		for (int i=1;i<total;i++) {
			boolean passedControl = false;
			boolean failedControl = false;

			try {
				passedControl= firstSheet.getRow(i).getCell(7).getStringCellValue().equals("FailedUpdated");
			} catch(NullPointerException nu) {
				passedControl= true;
			}

			try {
				failedControl= firstSheet.getRow(i).getCell(7).getStringCellValue().equals("PassedUpdated");
			} catch(NullPointerException nu) {
			}
			String toolId =  firstSheet.getRow(i).getCell(5).getStringCellValue();

			if (!(toolId.equals("NA")) &&
					firstSheet.getRow(i).getCell(6).getStringCellValue().equals("PASSED")&&passedControl) {
				passtatus.add("MarkDone");
				passtatus.add(toolId);
				Row row = firstSheet.getRow(i);
				Cell column = row.createCell(7);
				column.setCellValue("PassedUpdated");
				log.info("Bug with JIRA ID : " + toolId + " updated as Passed in excel");
			}

			else if (!(toolId.equals("NA")) &&
					firstSheet.getRow(i).getCell(6).getStringCellValue().equals("FAILED")&&failedControl){
				passtatus.add("MarkFailed");
				passtatus.add(toolId);
				Row row = firstSheet.getRow(i);
				Cell column = row.createCell(7);
				column.setCellValue("FailedUpdated");
				log.info("Bug with JIRA ID : " + toolId + " updated as Failed in excel");
			}
		}	

		FileOutputStream fos = null;
		try {
			log.info("Upadte the excel after setting the status for the updated bugs");
			fos = new FileOutputStream(new File(filepath));
			workbook.write(fos);
		} catch (IOException e) {
			log.error("Error occured while updating the bugs status");
		} 
		catch (IllegalArgumentException m) {
		} finally {
			if (fos != null) {
				try {
					log.info("Flushing the excel file after update status");
					fos.flush();
					fos.close();
				} catch (IOException e) {
					log.error("Error occured while flushing the chnaged file");
				}
			}
		}
		return passtatus;
	}
}
