package org.syncon.utils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.openqa.selenium.ElementNotVisibleException;
import org.syncon.tool.JIRA.UploadingFailedRestart;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;
import org.testng.Reporter;

public  class SyncListner implements ITestListener{
	public static ArrayList<String> arrlist = new ArrayList<String>();

	static Logger log = Logger.getLogger(SyncListner.class.getName());

	// Prepares required data to push on the Excel sheet formed
	public void dataEntry(ITestResult tr){

		log.info("Start updating required information for test case :"+tr.getName());
		String getClassName = null;
		try {
			getClassName = Reporter.getCurrentTestResult().getInstanceName();
		} catch (NullPointerException nil) {
			log.error("Unable to read the test class name");
			nil.printStackTrace();
		}
		arrlist.add(tr.getName());

		// Description for JIRA
		String description= tr.getMethod().getDescription();
		if(description == null) {
			arrlist.add("NotDefined");
		} else {
			arrlist.add(description);
		}

		// Issue type for JIRA
		if(ReadProperties.issueType == null){
			arrlist.add("Bug");
		} else {
			arrlist.add(ReadProperties.issueType);
		}

		// Assignee name for JIRA
		int assigneeInt = ReadProperties.classLevelAssignee.indexOf(getClassName);
		int testlevelAssignee = ReadProperties.testLevelAssignee.indexOf(tr.getName());
		if (testlevelAssignee>=0) {
			arrlist.add(ReadProperties.testLevelAssignee.get(testlevelAssignee+1));
		} else if (assigneeInt>=0) {
			arrlist.add(ReadProperties.classLevelAssignee.get(assigneeInt+1));
		} else if (ReadProperties.defaultAssignee== null) {
			arrlist.add("NotDefined");
		} else {
			arrlist.add(ReadProperties.defaultAssignee);
		}

		// Priority name for JIRA
		int priorityInt = ReadProperties.classLevelPriority.indexOf(getClassName);
		int testlevelPriority = ReadProperties.testLevelPriority.indexOf(tr.getName());
		if (testlevelPriority>=0) {
			arrlist.add(ReadProperties.testLevelPriority.get(testlevelPriority+1));
		} else if (priorityInt>=0) {
			arrlist.add(ReadProperties.classLevelPriority.get(priorityInt+1));
		} else if (ReadProperties.defaultPriority== null) {
			arrlist.add("NotDefined");
		} else {
			arrlist.add(ReadProperties.defaultPriority);
		}	

		log.info("Required information updated successfully for test case :"+ tr.getName());
	}

	// Update excel for the failed test case
	@Override
	public void onTestFailure(ITestResult tr) {
		dataEntry(tr);
		arrlist.add("NA");
		arrlist.add("FAILED");
	}

	// Update excel for the passed test case
	@Override
	public void onTestSuccess(ITestResult result) {
		dataEntry(result);
		arrlist.add("NA");
		arrlist.add("PASSED");
	}

	boolean propertiesFile = true;
	@Override
	public void onStart(ITestContext context) {
		
		// Start reading the properties file
		try {
			ReadProperties.readProp();
			PropertyConfigurator.configure("log4j.properties");
			log.info("syncon.properties file read successfully");
		} catch (IOException e) {
			propertiesFile = false;
			log.fatal("syncon.properties file not found or error occured while reading the same."
					+ "*** BUGS WILL NOT BE UPDATED");
		}
	}


	// Upload the test cases to JIRA
	UploadingFailedRestart upf= new UploadingFailedRestart();
	@Override
	public void onFinish(ITestContext context) {
		if ( arrlist.size()>0 && propertiesFile) {
			log.info("Start uploading the test results to JIRA");
			try {
				boolean runIf = false;
				
				try {
					runIf= ExcelUpdate.runExcel();}
				catch (FileNotFoundException fnfe) {
					log.fatal("Error occured while reading/writing excel file");
				}
				
				if (runIf) {
					try {
						if (ReadProperties.toolUID!=null && ReadProperties.toolUrl!=null && ReadProperties.toolPass!=null){
							upf.JIRAuploadStart();
						}
					} 

					catch (NullPointerException tr) {
						try {
							upf.uploadRestart("Mandatory value missing.Refer to console.");
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					} 

					catch (ElementNotVisibleException enve) {
						try {
							upf.uploadRestart("Oops !!! Request timeout. Press OK to try again");
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					} 

					catch (Exception ex) {
						try {
							upf.uploadRestart("Oops !!! Request timeout. Press OK to try again");
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				 } else {
					log.info("Nothing to upload. No changes done");
					log.info("Closing the process");
				}
			} catch (IOException e) {
				log.fatal("Required syncon excel file not found");
				try {
					upf.uploadRestart("Required file removed by the user");
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
			}
		}
		else {
			log.info("Nothing to upload : Nothing Changed");
		}
	}

	@Override
	public void onTestStart(ITestResult result) {		
	}

	@Override
	public void onTestSkipped(ITestResult result) {		
	}

	@Override
	public void onTestFailedButWithinSuccessPercentage(ITestResult result) {
	}

}
