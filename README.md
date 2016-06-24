#SyncOn - Provides Sync between Selenium and JIRA

syncOn provides a complete sync between Selenium and JIRA i.e. every failed test case while running an automation script is uploaded to JIRA as a Bug and assigned to the respective developer. Once done an automated summary report is sent over email to all the configured users. SyncOn also updates the status of a test cases if it has passed now which failed earlier in last run. 


### Prerequisities
Add these mentioned below librarries into your project OR You can simply use the [pom.xml] (pom.xml) included in the project
* Apache POI
* JavaMail API
* TestNG
* Log4j


### Configuring syncOn
It involves two steps :

##### STEP 1: Simply Copy and Paste this listener in your testng.xml file 

```
<listeners>
<listener class-name="org.syncon.utils.SyncListner"></listener>
</listeners>
```

##### STEP 2: Add syncon.properties file in classpath

Syncon can be completely configured by simply adding syncon.properties file in classpath or you can also include the [syncOn.properties] (syncOn.properties) file mentioned in the project. Below are the mandatory properties keys which we need to define in this properties file before running a syncon project: 


```
#Use tool JIRA
syncon.initiateTool = JIRA

#Default assignee to whom all the created bugs will be assigned
defaultAssignee = assignee

#Default priority of the issues
defaultPriority = Highest

#JIRA project under which all the Bugs will be upadated
defaultJIRAProject = TestJIRAProject

#Unique syncon_projectID 
syncon.ProjectID = SyncOnProject

#Element ID for the passed testcases (Can be retrieved by using Inspect Element on the done/anystatus button in JIRA)
syncon.JIRAPassedElementID = action_id_11

#Element ID for the failed testcases (Can be retrieved by using Inspect Element on the open/anystatus button in JIRA)
syncon.JIRAFailedElementID = action_id_15

#URL to the JIRA tool used
syncon.initiateTool.URL = https://yourURL.atlassian.net/login

#Login Username for JIRA
syncon.initiateTool.Username = admin

#Login Password for JIRA
syncon.initiateTool.Password = admin

```

####### Other property keys to configure behaviour of JIRA
To define an assginee for  any particular class or for any particular testcase use the following syntax :

Note:  Keep updating the key value as 1, 2,3 .... and so on. 
######### Test level assignee and priority :

```
syncon.assginee.testLevel-1 = packageName.classname,testcaseName,assigneeName

syncon.assginee.testLevel-2 = packageName.classname,testcaseName2,assigneeName2

syncon.priority.testLevel-1 = packageName.classname,testcaseName,MEDIUM

syncon.priority.testLevel-2 = packageName.classname,testcaseName2,LOW
```

######### Class level assignee and priority :

```
syncon.assginee.classLevel-1 = packageName.classname,assigneeName

syncon.assginee.classLevel-2 = packageName.classname,assigneeName2

syncon.priority.classLevel-1 = packageName.classname,MEDIUM

syncon.priority.classLevel-2 = packageName.classname,LOW
```

######### Other configurations :

```
syncon.issueType :: By deafult all the new bugs will be created as "BUG" as their type on JIRA.
To change it to other type its value can be declared in here for example if its value is set as 
TASK all the new issues will be uplaoded as TASK on JIRA

syncon.Directory :: By default the necessary files are created in C drive.
But it can cause an error if you are not running the project as an administrator.
So define the directory as "D:"/ "E:"
```

####### Configure email report by adding these properties in syncon.properties file

Report sendiong can be activayed by simply adding these properties and there values 
in the syncon properties file

```
# Set from emailID (For now "from" can only contain gmail ids only)
syncon.setReportUserID = abc@gmail.com

# Set password
syncon.setReportPassword = abc123

# To,Cc & Bcc email ID
syncon.report.TO= xyz@outlook.com,ab123@gmail.com
syncon.report.CC= anyone@yahoo.com,anothercc@gmail.com
syncon.report.BCC=anyoneagain@gmail.com
```

## License

This project is licensed under the MIT License - see the [LICENSE.md] (LICENSE.md) file for details
