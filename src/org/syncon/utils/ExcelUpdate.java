package org.syncon.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;

public class ExcelUpdate extends SyncListner{

	static final  String[] headerNames = 
		{"Failed TestCase","Bug Summary","Project Issue Type","Assignee",
		"Priority","Tool BugID","Current Status"};
	static boolean newChangesMade = false;
	public static String getfilepath;
	static Logger log = Logger.getLogger(ExcelUpdate.class.getName());


	//Get the filePath name used to store Excel and Csv file
	public static String fileExt() {	
		log.info ("Generating the output file path");
		if (System.getProperty("os.name").contains("Windows")) {
			if (ReadProperties.directory==null) {
				getfilepath="C:\\syncOn_data_"+ReadProperties.projectID.toString()+".xls";
			} else {
				getfilepath=ReadProperties.directory+"\\syncOn_data_"+ReadProperties.projectID.toString()+".xls";
			}
		} else {		
			getfilepath= ReadProperties.directory;
		}
		log.info("Output file generated with name : "+getfilepath);
		return getfilepath;
	}

	//Updates the existing excel file
	public static boolean runExcel() throws IOException{
		String filepath= fileExt() ;
		log.info("Updating / Reading the excel file");
		try {
			FileInputStream fs = new FileInputStream(filepath);
			Cell cell;
			@SuppressWarnings("resource")
			HSSFWorkbook workbook = new HSSFWorkbook(fs);
			HSSFSheet firstSheet = workbook.getSheetAt(0);
			int tot=firstSheet.getLastRowNum()+1;
			HSSFRow rowA ;
			try {		
				for (int j=0; j <tot-1; j++) {
					cell = firstSheet.getRow(j+1).getCell(0);
					int ALfoundIndex = arrlist.indexOf(cell.getStringCellValue());
					if (ALfoundIndex>=0) {
						for (int kt=0;kt<7;kt++) {
							if (!(kt==5)) {
								if (!(arrlist.get(ALfoundIndex+kt))
										.equals(firstSheet.getRow(j+1).getCell(kt).getStringCellValue())) {
									firstSheet.getRow(j+1).getCell(kt).setCellValue(arrlist.get(ALfoundIndex+kt));
									if (!newChangesMade) {
										newChangesMade= true;
									}
								}
							}
						}

						for (int k=0;k<7;k++) {									
							arrlist.remove(ALfoundIndex);
						}
					}
				}

				for (int s=0;s<(arrlist.size()/7);s++) {
					rowA = firstSheet.createRow(s+tot);
					HSSFCell cellA = rowA.createCell(0);
					cellA.setCellValue(arrlist.get((s*7)));
					for (int j=1;j<7;j++) {
						HSSFCell cellB = rowA.createCell(j);
						cellB.setCellValue(arrlist.get((s*7)+j));
						if (!newChangesMade) {
							newChangesMade= true;
						}
					}
				}
			} catch (IllegalArgumentException q) {
				log.error("IllegalArgumentException in excel file");
			}
			FileOutputStream fos = null;
			try {
				fos = new FileOutputStream(new File(filepath));
				workbook.write(fos);
			} catch (IOException e) {
				log.error("Error occured while writing the excel file");
			} 
			catch (IllegalArgumentException m) {
				log.error("IllegalArgumentException in excel file");
			}finally {
				if (fos != null) {
					try {
						fos.flush();
						fos.close();
						log.info("Changes made to Excel saved successfully");
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
			return newChangesMade;


			// Creates a new file and write data for all the testcases
		} catch(FileNotFoundException fnfe){

			@SuppressWarnings("resource")
			HSSFWorkbook workbook = new HSSFWorkbook();
			HSSFSheet firstSheet = workbook.createSheet("FIRST SHEET");
			HSSFRow rowA;{
				try{
					CellStyle headerStyle = workbook.createCellStyle();
					headerStyle.setFillBackgroundColor(IndexedColors.BLACK.getIndex());
					headerStyle.setFillPattern(CellStyle.BIG_SPOTS);
					Font font = workbook.createFont();
					font.setColor(HSSFColor.WHITE.index);
					font.setBold(true);
					font.setFontName("Calibri");
					headerStyle.setFont(font);
					headerStyle.setWrapText(true);

					rowA = firstSheet.createRow(0);
					HSSFCell cellHeader;
					for (int jq=0;jq<7;jq++){
						cellHeader = rowA.createCell(jq);
						cellHeader.setCellValue(headerNames[jq]);
						cellHeader.setCellStyle(headerStyle);
					}

					for(int s=0;s<(arrlist.size()/7);s++){
						rowA = firstSheet.createRow(s+1);
						HSSFCell cellA = rowA.createCell(0);
						cellA.setCellValue(arrlist.get((s*7)));
						for(int j=0;j<7;j++){
							HSSFCell cellB = rowA.createCell(j);
							cellB.setCellValue(arrlist.get((s*7)+j));
						}
					}
				} catch(IllegalArgumentException q){
					log.error("IllegalArgumentException in excel file");
				}
				FileOutputStream fos = null;
				try {
					fos = new FileOutputStream(new File(filepath));
					workbook.write(fos);
				} catch(IOException e) {
					log.error("Error occured while writing the excel file");
				} 
				catch (IllegalArgumentException m) {
					log.error("IllegalArgumentException in excel file");
				} finally {
					if (fos != null) {
						try {
							fos.flush();
							fos.close();
						} catch(IOException e){
							e.printStackTrace();
						}
					}
				}
			}
			return true;
		}
	}
}
