package net.ee.pfanalyzer.io;

public class IllegalDataException extends IllegalArgumentException {

	public IllegalDataException(String dataName, String message) {
		super("Invalid data type for \"" + dataName + "\": " + message);
	}

	public IllegalDataException(String message, Throwable cause) {
		super(message, cause);
	}
}
