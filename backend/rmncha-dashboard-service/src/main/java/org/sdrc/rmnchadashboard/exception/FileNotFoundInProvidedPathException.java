package org.sdrc.rmnchadashboard.exception;



public class FileNotFoundInProvidedPathException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public FileNotFoundInProvidedPathException() {
		super();
	}

	public FileNotFoundInProvidedPathException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public FileNotFoundInProvidedPathException(String message, Throwable cause) {
		super(message, cause);
	}

	public FileNotFoundInProvidedPathException(String message) {
		super(message);
	}

	public FileNotFoundInProvidedPathException(Throwable cause) {
		super(cause);
	}

}
