/**
 * 
 */
package org.sdrc.rmnchadashboard.model;

/**
 * @author Harsh Pratyush(harsh@sdrc.co.in)
 *
 */
public class ResponseModel {
	
	private String fileName;
	
	private String filePath;
	
	private String outputFilePath;
	
	private int statusCode;
	
	private String message;

	public ResponseModel() {
		super();
		// TODO Auto-generated constructor stub
	}

	public ResponseModel(int statusCode, String message) {
		super();
		this.statusCode = statusCode;
		this.message = message;
	}

	public String getFileName() {
		return fileName;
	}

	public String getOutputFilePath() {
		return outputFilePath;
	}

	public void setOutputFilePath(String outputFilePath) {
		this.outputFilePath = outputFilePath;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public int getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(int statusCode) {
		this.statusCode = statusCode;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

}
