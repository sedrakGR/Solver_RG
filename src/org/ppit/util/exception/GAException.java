package org.ppit.util.exception;

/**
 * 
 * @author Khachatryan Karen S.
 * @description the base class of GA exception. 
 *
 */

public class GAException extends PPITException {
	public GAException  (String error) {
		super("GA Exception " + error);
	}
}
