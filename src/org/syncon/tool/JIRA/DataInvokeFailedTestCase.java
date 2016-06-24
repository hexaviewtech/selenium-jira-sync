package org.syncon.tool.JIRA;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;

import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.syncon.utils.ExcelUpdate;


public class DataInvokeFailedTestCase  {

	/*Creates CSV file to import in JIRA*/

	Cell cell;
	HSSFRow rowA ;
	static HSSFSheet firstSheet;
	private static int total;
	private static FileWriter fileWriter = null;
	private static final String COMMA_DELIMITER = ",";
	private static final String NEW_LINE_SEPARATOR = "\n";
	private static final String FILE_HEADER = "Failed TestCase,Bug Summary,Project Name,Assignee,Priority,Tool BugID,Current Status";
	static String filePath = ExcelUpdate.getfilepath.substring(0, ExcelUpdate.getfilepath.length()-4)+"_TEMP.csv";
	public static LinkedList<String> failedTestforReport = new LinkedList<>();
	public static LinkedList<String> updatedTestforReport = new LinkedList<>();


	private static void DataInvokeTempIni() throws IOException{
		FileInputStream fs = new FileInputStream(ExcelUpdate.getfilepath);
		@SuppressWarnings("resource")
		HSSFWorkbook workbook = new HSSFWorkbook(fs);
		firstSheet = workbook.getSheetAt(0);
		total=firstSheet.getLastRowNum()+1;
	}

	public static void failedCasesDeleteCSV(){
		File csvFile = new File(filePath);
		csvFile.delete();
	}


	static boolean failedCasesCreateCSV() throws IOException{
		DataInvokeTempIni();

		LinkedList<String> prestatus = new LinkedList<>();
		for (int i=1;i<total;i++) {
			boolean statusOfTheIssue = firstSheet.getRow(i).getCell(6).getStringCellValue().equals("FAILED");
			String issueID= firstSheet.getRow(i).getCell(5).getStringCellValue();
			if (statusOfTheIssue) {
				try {
					if (firstSheet.getRow(i).getCell(7).getStringCellValue().equals("PassedUpdated")
							& (!issueID.equals("NA")))
						updatedTestforReport.add(firstSheet.getRow(i).getCell(0).getStringCellValue());
				}
				catch (NullPointerException nu) {
				}
				if (issueID.equals("NA")) {
					failedTestforReport.add(firstSheet.getRow(i).getCell(0).getStringCellValue());
					for (int j=0;j<7;j++) {
						prestatus.add(firstSheet.getRow(i).getCell(j).getStringCellValue());
					}
				}
			} else {
				if (!issueID.equals("NA")) {
					try {
						firstSheet.getRow(i).getCell(7).getStringCellValue().equals("FailedUpdated");
						updatedTestforReport.add(firstSheet.getRow(i).getCell(0).getStringCellValue());
					} catch(NullPointerException nu) {
						updatedTestforReport.add(firstSheet.getRow(i).getCell(0).getStringCellValue());
					}
				}
			}
		}
		return generateCsvEssentilas(prestatus.size(),prestatus,filePath);
	}




	private static boolean generateCsvEssentilas(int elementsPresent, LinkedList<String> status, String filePath){
		if (elementsPresent>0){
			try {
				fileWriter = new FileWriter(filePath);
				fileWriter.append(FILE_HEADER.toString());
				fileWriter.append(NEW_LINE_SEPARATOR);

				for (int k=0;k<elementsPresent;k=k+7) {
					for (int s=k;s<k+7;s++) {
						if (s<k+6) {
							fileWriter.append(status.get(s));
							fileWriter.append(COMMA_DELIMITER);
						} else {
							fileWriter.append(status.get(s));
							fileWriter.append(NEW_LINE_SEPARATOR);
						}
					}
				}
			} catch(Exception ex) {
				ex.printStackTrace();
			}
			try {
				fileWriter.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				fileWriter.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			return true;

		} else {
			return false;
		}
	}

}
