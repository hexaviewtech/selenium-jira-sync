package org.syncon.repository;

import org.apache.log4j.Logger;
import org.apache.poi.util.SystemOutLogger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.syncon.utils.ReadProperties;

@SuppressWarnings("unused")

// Repository to all the JIRA WebElements


public class JIRARepository {	
	static Logger log = Logger.getLogger(JIRARepository.class.getName());

	@FindBy(id="quickSearchInput")public WebElement searchFieldForBug;
	@FindBy(id="key-val")public WebElement keyValueForBug;
	@FindBy(id="username")public WebElement uid;
	@FindBy(id="password")public WebElement pass;
	@FindBy(id="login")public WebElement loginBtn;
	@FindBy(id="logo")public WebElement logo;
	@FindBy(id="create_link")public WebElement create;
	@FindBy(id="summary")public WebElement summary;
	@FindBy(id="find_link")public WebElement issuesDropdown;
	@FindBy(id="bulk_create_dd_link_lnk")public WebElement csvImport;
	@FindBy(partialLinkText="download")public WebElement csvImportDetailedLog;
	@FindBy(xpath=".//*[@id='project-single-select']/span")public WebElement project;
	@FindBy(id="create-issue-submit")public WebElement afterBugSubmit;
	@FindBy(id="csvFile")public WebElement csvUploadButton;
	@FindBy(xpath=".//*[@id='aui-flag-container']/div/div/a")public WebElement bugSuccess;
	@FindBy(xpath=".//*[@id='uploadedFileSection']/div/a")public WebElement fileUploaded;
	@FindBy(id="nextButton")public WebElement FirstNextButton;
	@FindBy(id="previousButton")public WebElement FirstpreviousButton;
	@FindBy(id="CSV-select-field")public WebElement fileUplaodProjectName;
	@FindBy(id="delimiter")public WebElement delimiter;
	@FindBy(xpath=".//*[@id='jimform']/div[1]/div")public WebElement MapFieldsWait;
	@FindBy(xpath=".//*[@id='finishResults']/div[2]")public WebElement UploadFailedReason;
	@FindBy(xpath=".//*[@id='jimform']/div[1]/span/span/div[2]")public WebElement projectError;
	@FindBy(id="field-780f85493892dc48b674ac9da8974133-mapping-field")public WebElement assignee;
	@FindBy(id="field-732d0a63cabe472a91ffa4b354acb749-mapping-field")public WebElement description;
	@FindBy(id="field-aa525bdb62ff4e61f011369badfea6d8-mapping-field")public WebElement summaryUpload;
	@FindBy(id="field-502996d9790340c5fd7b86a5b93b1c9f-mapping-field")public WebElement priority;
	@FindBy(id="field-8e3a42158ee70b67cf55b33e2789a9e5-mapping-field")public WebElement issueType;
	@FindBy(id="validationButton")public WebElement validateButton;
	@FindBy(id="finishResults")public WebElement uploadedSuccessfully;

	final WebDriver driver;

	public JIRARepository(WebDriver driver){
		log.info("Reading JIRA Repository");
		this.driver = driver;
		log.info("Completed Reading JIRA Repository");
	}
}
