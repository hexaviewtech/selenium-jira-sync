package org.syncon.emailreport;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.syncon.utils.ReadProperties;

public class Sendmail {
	static private Properties mailServerProperties;
	static private Session getMailSession;
	static private MimeMessage generateMailMessage;

	public static void generateAndSendEmail(String htmlText) throws AddressException, MessagingException {

		mailServerProperties = System.getProperties();
		mailServerProperties.put("mail.smtp.port", "587");
		mailServerProperties.put("mail.smtp.auth", "true");
		mailServerProperties.put("mail.smtp.starttls.enable", "true");
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		Date date = new Date();

		getMailSession = Session.getDefaultInstance(mailServerProperties, null);
		generateMailMessage = new MimeMessage(getMailSession);

		try {
			for (int i=0;i<ReadProperties.toEmailID.length;i++) {
				generateMailMessage.addRecipient(Message.RecipientType.TO, new InternetAddress(ReadProperties.toEmailID[i]));
			}
		} catch(NullPointerException nu) {}

		try {
			for (int i=0;i<ReadProperties.ccEmailID.length;i++) {
				generateMailMessage.addRecipient(Message.RecipientType.CC, new InternetAddress(ReadProperties.ccEmailID[i]));
			}
		} catch(NullPointerException nu) {}

		try {
			for (int i=0;i<ReadProperties.bccEmailID.length;i++) {
				generateMailMessage.addRecipient(Message.RecipientType.BCC, new InternetAddress(ReadProperties.bccEmailID[i]));
			}
		} catch(NullPointerException nu) {}

		generateMailMessage.setSubject(ReadProperties.defaultProject+" Status Mail- Generated @"+dateFormat.format(date));

		generateMailMessage.setContent(htmlText, "text/html");

		Transport transport = getMailSession.getTransport("smtp");
		transport.connect("smtp.gmail.com", ReadProperties.fromEmailID, ReadProperties.emailPass);
		transport.sendMessage(generateMailMessage, generateMailMessage.getAllRecipients());
		transport.close();

	}
}
