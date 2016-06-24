package org.syncon.tool.JIRA;

import java.io.IOException;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import org.apache.log4j.Logger;
import org.syncon.utils.ReadProperties;

public class UploadingFailedRestart{

	// Provides option to restart the process just in case 
	// it failed due to any possible reason
	// Properties file is also read again to incorporate any changes
	// that are to be made in order to complete the process

	static Logger log = Logger.getLogger(UploadingFailedRestart.class.getName());
	JFrame frame = new JFrame();

	public void uploadRestart(String message) throws InterruptedException {
		log.info("Asking to start the process again");

		int n = JOptionPane.showConfirmDialog(
				frame, message,
				"Bug Updation Failed",
				JOptionPane.OK_CANCEL_OPTION,
				JOptionPane.ERROR_MESSAGE);

		if (n == JOptionPane.OK_OPTION){
			try {
				log.info("Start Reading the properties file");
				ReadProperties.readProp();
			} catch (IOException e) {
				log.error("Error occured while reading the properties file");
			}
			log.info("Re-Initiate the JIRA tool");
			Jira jira = new Jira();
			jira.doLogin(ReadProperties.toolUrl,ReadProperties.toolUID,ReadProperties.toolPass);	
		}   
	}	


	public void JIRAuploadStart() throws InterruptedException {
		log.info("Getting an confirmation from user to start uploading the process");
		int n = JOptionPane.showConfirmDialog (
				frame, "Do you want to upload bugs to JIRA ?",
				"",
				JOptionPane.YES_NO_OPTION,
				JOptionPane.QUESTION_MESSAGE);
		if (n == JOptionPane.YES_OPTION) {
			log.info("Start updating JIRA-- ");
			Jira jira = new Jira();
			jira.doLogin(ReadProperties.toolUrl,ReadProperties.toolUID,ReadProperties.toolPass);
		}   
	}	
}
