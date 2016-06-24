package org.syncon.utils;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.log4j.FileAppender;
import org.apache.log4j.Layout;
import org.apache.log4j.spi.ErrorCode;

//Customized to generate new log file for every run 

public class CustomizedLogReport extends FileAppender{

	public CustomizedLogReport() {
	}

	public static  String filename = ExcelUpdate.fileExt().replace("xls", "log");

	public CustomizedLogReport(Layout layout,
			boolean append, boolean bufferedIO, int bufferSize)
					throws IOException {
		super(layout, filename, append, bufferedIO, bufferSize);
	}

	public CustomizedLogReport(Layout layout, 
			boolean append) throws IOException {
		super(layout, filename, append);
	}

	public CustomizedLogReport(Layout layout)
			throws IOException {
		super(layout, filename);
	}

	public void activateOptions() {
		if (filename != null) {
			try {
				final String DOT = ".";
				final String HIPHEN = "-";
				String newFileName;
				DateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");
				Date date = new Date();
				final String time = dateFormat.format(date); 
				final int dotIndex = filename.indexOf(DOT);
				if (dotIndex != -1) {
					// the file name has an extension. so, insert the time stamp
					// between the file name and the extension
					newFileName = filename.substring(0, dotIndex)  + HIPHEN+
							time  + DOT+
							filename.substring(dotIndex +  1);
				} else {
					// the file name has no extension. So, just append the timestamp
					// at the end.
					newFileName = filename +  HIPHEN +  time;
				}
				setFile(newFileName, fileAppend, bufferedIO, bufferSize);
			} catch (Exception e) {
				errorHandler.error("Error while activating log options", e,
						ErrorCode.FILE_OPEN_FAILURE);
			}
		}
	}
}
