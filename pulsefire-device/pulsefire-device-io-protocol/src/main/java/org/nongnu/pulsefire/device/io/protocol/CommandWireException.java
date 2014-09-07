package org.nongnu.pulsefire.device.io.protocol;

/**
 * CommandWireException for when the parse or decoding does not go correctly.
 * 
 * @author Willem Cazander
 */
public class CommandWireException extends Exception {

	private static final long serialVersionUID = 5818776470005725358L;

	/**
	 * Only create with message.
	 * @param message
	 */
	public CommandWireException(String message) {
		super(message);
	}
}
