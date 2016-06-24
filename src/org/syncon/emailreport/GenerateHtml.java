package org.syncon.emailreport;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.LinkedList;

import javax.mail.MessagingException;
import javax.mail.internet.AddressException;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.syncon.utils.ExcelUpdate;
import org.syncon.utils.ReadProperties;

public class GenerateHtml {

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

	public void createReport(LinkedList<String> failed, LinkedList<String> updated) throws IOException, AddressException, MessagingException {
		String hyperlink;
		String url= ReadProperties.toolUrl;
		if (url.contains("login")) {
			String[] splitName= url.split("login");
			hyperlink= splitName[0]+"/browse/";
		} else {
			hyperlink= ReadProperties.toolUrl+"/browse/";
		}


		String reportHTML ="";
		String headStart = "<html><head><title>syncOn Report</title><style type=\"text/css\">";
		String cssFailed= ".datagrid table { border-collapse: collapse; text-align: left; width: 100%; } .datagrid {font: normal 12px/150% Geneva, Arial, Helvetica, sans-serif; background: #fff; overflow: hidden; border: 1px solid #F41B0F; -webkit-border-radius: 1px; -moz-border-radius: 1px; border-radius: 1px; }.datagrid table td, .datagrid table th { padding: 4px 6px; }.datagrid table thead th {background:-webkit-gradient( linear, left top, left bottom, color-stop(0.05, #F8726B), color-stop(1, #F8635B) );background:-moz-linear-gradient( center top, #006699 5%, #00557F 100% );filter:progid:DXImageTransform.Microsoft.gradient(startColorstr='#006699', endColorstr='#00557F');background-color:#006699; color:#FFFFFF; font-size: 15px; font-weight: bold; border-left: 1px solid #0070A8; } .datagrid table thead th:first-child { border: none; }.datagrid table tbody td { color: #00557F; border-left: 2px solid #E1EEF4;font-size: 13px;font-weight: normal; }.datagrid table tbody .alt td { background: #E1EEf4; color: #00557F; }.datagrid table tbody td:first-child { border-left: none; }.datagrid table tbody tr:last-child td { border-bottom: none; }";
		String cssPassed= ".datagrid2 table { border-collapse: collapse; text-align: left; width: 100%; } .datagrid2 {font: normal 12px/150% Geneva, Arial, Helvetica, sans-serif; background: #fff; overflow: hidden; border: 1px solid #3B6936; -webkit-border-radius: 1px; -moz-border-radius: 1px; border-radius: 1px; }.datagrid2 table td, .datagrid2 table th { padding: 4px 6px; }.datagrid2 table thead th {background:-webkit-gradient( linear, left top, left bottom, color-stop(0.05, #76AB4C), color-stop(1, #98C079) );background:-moz-linear-gradient( center top, #006699 5%, #00557F 100% );filter:progid:DXImageTransform.Microsoft.gradient(startColorstr='#006699', endColorstr='#00557F');background-color:#006699; color:#FFFFFF; font-size: 15px; font-weight: bold; border-left: 1px solid #0070A8; } .datagrid2 table thead th:first-child { border: none; }.datagrid2 table tbody td { color: #00557F; border-left: 2px solid #E1EEF4;font-size: 13px;font-weight: normal; }.datagrid2 table tbody .alt td { background: #E1EEf4; color: #00557F; }.datagrid2 table tbody td:first-child { border-left: none; }.datagrid22 table tbody tr:last-child td { border-bottom: none; }";
		String headClose ="</style></head><body>";

		String failedHeader ="<div class=\"datagrid\"><table><thead><tr><th>Test</th><th>Assignee</th><th>Priority</th><th>Jira BugId</th></tr></thead><tbody>";
		String failedClose="</tbody></table></div>";
		String passedHeader ="<div class=\"datagrid2\"><table><thead><tr><th>Test</th><th>Assignee</th><th>Priority</th><th>Jira BugId</th><th>JIRA Updated Status</th></tr></thead><tbody>";
		String passedClose="</tbody></table></div>";
		String finalClose ="</body></html>";
		FileInputStream fs = new FileInputStream(ExcelUpdate.getfilepath);
		@SuppressWarnings("resource")
		HSSFWorkbook workbook = new HSSFWorkbook(fs);
		HSSFSheet firstSheet = workbook.getSheetAt(0);
		String testCaseName,assignee,priority,jiraBugID,updationState;

		if (failed.size()>0) {
			reportHTML+="<h3 style=\"color:#1f4977;font-family: verdana;\">Failed Test Cases</h3>";
			reportHTML+=failedHeader;
			int start =0;
			while (start<failed.size()) {
				testCaseName=failed.get(start);
				int foundNumber = findRow(firstSheet,failed.get(start));
				assignee= firstSheet.getRow(foundNumber).getCell(3).getStringCellValue();
				priority= firstSheet.getRow(foundNumber).getCell(4).getStringCellValue();
				jiraBugID=firstSheet.getRow(foundNumber).getCell(5).getStringCellValue();
				if (start%2==0) {
					reportHTML+="<tr class=\"alt\"><td>"+testCaseName
							+"</td><td>"+assignee+"</td><td>"+priority+"</td><td><a href="+hyperlink+jiraBugID+">"+jiraBugID+"</td></tr>";
				} else {
					reportHTML+="<tr><td>"+testCaseName
							+"</td><td>"+assignee+"</td><td>"+priority+"</td><td><a href="+hyperlink+jiraBugID+">"+jiraBugID+"</td></tr>";
				}
				start++;
			}
			reportHTML+=failedClose+"<br>";
		}


		if (updated.size()>0) {
			reportHTML+="<h3 style=\"color:#1f4977;font-family: verdana;\">Updated Test Cases</h3>";
			reportHTML+=passedHeader;
			int start =0;
			while(start<updated.size()){
				testCaseName=updated.get(start);

				int foundNumber = findRow(firstSheet,updated.get(start));

				assignee= firstSheet.getRow(foundNumber).getCell(3).getStringCellValue();
				priority= firstSheet.getRow(foundNumber).getCell(4).getStringCellValue();
				jiraBugID=firstSheet.getRow(foundNumber).getCell(5).getStringCellValue();
				updationState=firstSheet.getRow(foundNumber).getCell(7).getStringCellValue();
				if (start%2==0) {
					reportHTML+="<tr class=\"alt\"><td>"+testCaseName
							+"</td><td>"+assignee+"</td><td>"+priority+"</td><td><a href="+hyperlink+jiraBugID+">"+jiraBugID+"</td><td>"+updationState+"</td></tr>";
				} else {
					reportHTML+="<tr><td>"+testCaseName
							+"</td><td>"+assignee+"</td><td>"+priority+"</td><td><a href="+hyperlink+jiraBugID+">"+jiraBugID+"</td><td>"+updationState+"</td></tr>";
				}
				start++;
			}
			reportHTML+=passedClose;
		}	
		Sendmail.generateAndSendEmail(headStart+cssFailed+cssPassed+headClose+reportHTML+finalClose);
	}
}
