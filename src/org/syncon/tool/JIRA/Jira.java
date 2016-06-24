package org.syncon.tool.JIRA;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.util.LinkedList;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.syncon.emailreport.GenerateHtml;
import org.syncon.repository.JIRARepository;
import org.syncon.utils.FinalExcelUpdater;
import org.syncon.utils.ReadProperties;



public class Jira {

	org.syncon.repository.JIRARepository repository;
	WebDriver driver;
	WebDriverWait wait;
	UploadingFailedRestart upr = new UploadingFailedRestart();
	static Logger log = Logger.getLogger(Jira.class.getName());
	boolean failedSuccess = false, passsedSuccess = false;
	private LinkedList<String> passedCasesUploadedSuccessfully = new LinkedList<>();
	Robot robot = null;

	Jira(){
		try {
			robot = new Robot();
		} catch (AWTException e) {
			log.error("Error while initilizing the Robot API");
		}
	}


	public void doLogin(String url,String uid, String pass) throws InterruptedException{
		log.debug("Starting the WebBrowser");
		driver = new FirefoxDriver(); 
		driver.manage().window().maximize();
		driver.manage().timeouts().implicitlyWait(6, TimeUnit.SECONDS);
		repository = PageFactory.initElements(driver, JIRARepository.class);
		wait = new WebDriverWait(driver, 20);
		driver.get(url);
		log.info("Logging to JIRA application");
		repository.uid.sendKeys(uid);
		repository.pass.sendKeys(pass);
		repository.loginBtn.click();
		toWait();
		log.info("Successfully logged in");

		//Upload failed testcases using the CSV file
		try {
			uploadFailedTestCasesCSV();
		} catch (IOException e) {} 

		// Change the status of the Bugs which are need to be updated due to the last run
		LinkedList<String> passtatus = null;

		try {
			passtatus = PassedTestCases.updateFailedToPassed();
		} catch (IOException e1) {
			log.error("Error while getting the count of the updated issues");
		}

		int sizePassStatus;
		try {
			sizePassStatus =passtatus.size();
		} catch(NullPointerException nulExp) {
			sizePassStatus=0 ;
		}

		if (sizePassStatus>0) {
			repository.logo.click();
			for (int i=0;i<sizePassStatus;i+=2) {
				String searchText= passtatus.get(i+1);
				log.info("Updating the status of the bug with JIRA ID : " + searchText);
				repository.searchFieldForBug.sendKeys(searchText);
				robot.keyPress(KeyEvent.VK_ENTER);
				robot.keyRelease(KeyEvent.VK_ENTER);
				Thread.sleep(1000);
				toWaitBugUploading();
				try {
					if (passtatus.get(i).equals("MarkDone")) {
						driver.findElement(By.id(ReadProperties.passedID)).click();
						Thread.sleep(3000);
						buttonclickUpdate();
					} else if (passtatus.get(i).equals("MarkFailed")) {
						driver.findElement(By.id(ReadProperties.failedID)).click();
						Thread.sleep(3000);
						buttonclickUpdate();
					}
					passedCasesUploadedSuccessfully.add(searchText);
				} catch(Exception ex) {
					log.error("Status of bug with JIRA ID : " + searchText + " cannot by updated due to "
							+ ex.getMessage());
				}		
			}
		}

		driver.quit();


		// Send email ----- 

		final int failedCases = DataInvokeFailedTestCase.failedTestforReport.size();
		final int passedCases = passedCasesUploadedSuccessfully.size();
		if ((failedCases>0 && failedSuccess)||passedCases>0 )
			if (ReadProperties.toEmailID.length>0 || ReadProperties.ccEmailID.length>0 ||
					ReadProperties.bccEmailID.length>0 && ReadProperties.fromEmailID!=null
					&& ReadProperties.emailPass!=null ) {
				try {
					log.info("Sending email");
					System.out.println("Sending Email....");
					GenerateHtml html = new GenerateHtml();
					html.createReport(DataInvokeFailedTestCase.failedTestforReport, passedCasesUploadedSuccessfully);
					System.out.println("Email sent successfully");
					log.info("Email sent successfully");
				} catch (Exception e) {
					log.error("Error occured while sending the email : " + e.getMessage());
				}
			}
	}

	// To retry the upload CSV code once failed due to any reason
	private void uploadCSVfailed() throws InterruptedException{
		csvWait();
		repository.csvUploadButton.click();
		StringSelection CSVpath = new StringSelection(DataInvokeFailedTestCase.filePath);
		Toolkit.getDefaultToolkit().getSystemClipboard().setContents(CSVpath, null);
		Thread.sleep(2000);
		robot.keyPress(KeyEvent.VK_CONTROL);
		robot.keyPress(KeyEvent.VK_V);
		robot.keyRelease(KeyEvent.VK_V);
		robot.keyRelease(KeyEvent.VK_CONTROL);
		Thread.sleep(2000);
		robot.keyPress(KeyEvent.VK_ENTER);
		robot.keyRelease(KeyEvent.VK_ENTER);
		csvUploadedWait();
		repository.FirstNextButton.click();
		csvUploadedWaitNext1();
	}

	// Try to uplaod the CSV file 4 times as it may sometimes 
	// cause an error while doing so

	int csvUploadTryCount = 1;
	private void tryUploadCSV (){
		while (true){
			try {
				log.info("ATTEMPT NO - " + csvUploadTryCount + " to upload CSV files for New Issues");
				uploadCSVfailed();
				break;
			} catch(Exception ex) {
				log.info("ATTEMPT NO - " + csvUploadTryCount + " to upload CSV file failed");
				if (csvUploadTryCount<5) {
					System.out.println("click alt");
					robot.keyPress(KeyEvent.VK_ALT);
					robot.keyPress(KeyEvent.VK_F4);
					robot.keyRelease(KeyEvent.VK_F4);
					robot.keyRelease(KeyEvent.VK_ALT);
					driver.navigate().refresh();
					csvUploadTryCount++;
				}else{
					log.error("Maximum tries reached");
					break;
				}
			}
		}
	}

	// Only to upload failed testcases
	private void uploadFailedTestCasesCSV() throws IOException, InterruptedException{
		boolean failedOnes = DataInvokeFailedTestCase.failedCasesCreateCSV();		
		if (failedOnes) {
			repository.issuesDropdown.click();
			Thread.sleep(1000);
			repository.csvImport.click();
			Thread.sleep(1000);// Necesary as the popup time varies	

			// Uploads the csv file
			tryUploadCSV();		

			if (csvUploadTryCount<5) {
				String project = ReadProperties.defaultProject;
				repository.fileUplaodProjectName.sendKeys(project);
				driver.findElement(By.id("encoding")).click();
				driver.findElement(By.id("encoding")).click();
				try {
					repository.FirstNextButton.click();
				}catch (Exception ex) {	
				}
				mapfieldswait();
				repository.assignee.sendKeys("Assignee");
				repository.description.clear();
				repository.description.sendKeys("Description");
				repository.summaryUpload.clear();
				repository.summaryUpload.sendKeys("Summary");
				repository.priority.clear();;
				repository.priority.sendKeys("Priority");
				repository.issueType.clear();
				repository.issueType.sendKeys("Issue Type");
				repository.FirstNextButton.click();
				validationFormUplaodWait();
				repository.FirstNextButton.click();
				UplaodSuccessWait();
				try{
					String reason = repository.UploadFailedReason.getText();
					if(!(reason.contains("The affected issues will be created"))){
						driver.quit();
						upr.uploadRestart(reason);
						log.fatal("Unable to read the Log generated by the JIRA");
					}				
				}catch(Exception ex){	
				}
				FinalExcelUpdater feu = new FinalExcelUpdater();
				feu.updateExcel(JIRAimportLogReader.logReader(getLogFile()));
				DataInvokeFailedTestCase.failedCasesDeleteCSV();
				failedSuccess=true;
			} else {
				log.fatal("*** CSV FILE NOT UPLOADED EVEN AFTER 4 TRIES BUGS WILL NOT BE UPLOADED ");
			}
		}
	}		


	private String getLogFile() throws IOException{
		String winHandleBefore = driver.getWindowHandle();
		robot.keyPress(java.awt.event.KeyEvent.VK_SHIFT);
		repository.csvImportDetailedLog.click();
		robot.keyRelease(java.awt.event.KeyEvent.VK_SHIFT);
		for(String winHandle : driver.getWindowHandles()){
			driver.switchTo().window(winHandle);
		}
		String logdata= driver.getPageSource();
		driver.close();
		driver.switchTo().window(winHandleBefore);
		return logdata;
	}

	private final void toWaitBugUploading(){
		driver.manage().timeouts().pageLoadTimeout(15, TimeUnit.SECONDS);
	}
	private final void buttonclickUpdate(){
		WebElement pass = driver.findElement(By.id("edit-issue"));
		wait.until(ExpectedConditions.visibilityOf(pass));
	}
	private final void toWait(){
		wait.until(ExpectedConditions.visibilityOf(repository.logo));
	}
	public final void popupWait(){
		wait.until(ExpectedConditions.visibilityOf(repository.afterBugSubmit));
	}
	public final void successMessageWait(){
		wait.until(ExpectedConditions.visibilityOf(repository.bugSuccess));
	}
	private final void csvWait(){
		wait.until(ExpectedConditions.visibilityOf(repository.csvUploadButton));
	}
	private final void csvUploadedWait(){
		wait.until(ExpectedConditions.visibilityOf(repository.fileUploaded));
	}
	private final void csvUploadedWaitNext1(){
		wait.until(ExpectedConditions.visibilityOf(repository.FirstpreviousButton));
	}
	private final void mapfieldswait(){
		wait.until(ExpectedConditions.visibilityOf(repository.MapFieldsWait));
	}
	private final void validationFormUplaodWait(){
		wait.until(ExpectedConditions.visibilityOf(repository.validateButton));
	}
	private final void UplaodSuccessWait(){
		wait.until(ExpectedConditions.visibilityOf(repository.uploadedSuccessfully));
	}

}
