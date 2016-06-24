package org.syncon.utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.Properties;

public class ReadProperties {

	static final Properties prop = new Properties();
	static InputStream input = null;

	public static String defaultAssignee,defaultPriority,issueType,toolUID,
	toolPass,defaultProject, passedID, failedID,toolUrl, fromEmailID, emailPass,projectID=null,directory;
	public static String[] toEmailID ,ccEmailID, bccEmailID;
	public static LinkedList<String> classLevelAssignee = new LinkedList<String>();
	public static LinkedList<String> classLevelPriority = new LinkedList<String>();
	public static LinkedList<String> testLevelAssignee = new LinkedList<String>();
	public static LinkedList<String> testLevelPriority = new LinkedList<String>();

	public static void readProp() throws IOException{
		input = new FileInputStream("syncon.properties");
		prop.load(input);
		fromEmailID= prop.getProperty("syncon.setReportUserID");
		emailPass = prop.getProperty("syncon.setReportPassword");

		try{
			toEmailID= prop.getProperty("syncon.report.TO").split(",");
		} catch (NullPointerException nu) {}

		try{
			ccEmailID= prop.getProperty("syncon.report.CC").split(",");
		} catch (NullPointerException nu) {}

		try{
			bccEmailID= prop.getProperty("syncon.report.BCC").split(",");
		} catch (NullPointerException nu) {}			

		// Default Values
		passedID= prop.getProperty("syncon.JIRAPassedElementID");
		failedID= prop.getProperty("syncon.JIRAFailedElementID");
		directory=prop.getProperty("syncon.Directory");
		projectID=prop.getProperty("syncon.ProjectID");
		defaultAssignee= prop.getProperty("defaultAssignee");
		defaultPriority= prop.getProperty("defaultPriority");
		issueType= prop.getProperty("syncon.issueType");
		defaultProject= prop.getProperty("defaultJIRAProject");
		toolUrl= prop.getProperty("syncon.initiateTool.URL");
		toolUID= prop.getProperty("syncon.initiateTool.Username");
		toolPass= prop.getProperty("syncon.initiateTool.Password");


		int i=1,j=1;

		// Test Level Assignee
		while (!(prop.getProperty("syncon.assginee.testLevel-"+i)==null)) {
			String[] splitName= prop.getProperty("syncon.assginee.testLevel-"+i).split(",");
			testLevelAssignee.add(splitName[0]);//this will be the class name
			testLevelAssignee.add(splitName[1]);// this is the name of the test
			testLevelAssignee.add(splitName[2]);//this will be the Assignee name
			i++;
		}
		i=1;

		// Test Level Priority
		while (!(prop.getProperty("syncon.priority.testLevel-"+i)==null)) {
			String[] splitName= prop.getProperty("syncon.priority.testLevel-"+i).split(",");
			testLevelPriority.add(splitName[0]);//this will be the class name
			testLevelPriority.add(splitName[1]);// this is the name of the test
			testLevelPriority.add(splitName[2]);//this will be the Assignee name
			i++;
		}

		// Class Level Assignee
		while (!(prop.getProperty("syncon.assginee.classLevel-"+j)==null)) {
			String[] splitName= prop.getProperty("syncon.assginee.classLevel-"+j).split(",");
			classLevelAssignee.add(splitName[0]);//even will be the class name
			classLevelAssignee.add(splitName[1]);//odd will be the Assignee name
			j++;
		}

		// Class Level Priority
		int f=1;
		while (!(prop.getProperty("syncon.priority.classLevel-"+f)==null)) {
			String[] splitName1= prop.getProperty("syncon.priority.classLevel-"+f).split(",");
			classLevelPriority.add(splitName1[0]);//even will be the class name
			classLevelPriority.add(splitName1[1]);//odd will be the priority name
			f++;
		}
	} 
}
