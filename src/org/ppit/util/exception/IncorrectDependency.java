package org.ppit.util.exception;

/**
 * 
 * @author Khachatryan Karen S.
 * @description the exception shall be thrown if there is an incorrect dependency 
 * for example incorrect path.
 *
 */

public class IncorrectDependency extends PPITException {
	public IncorrectDependency(String error) {
		super(error);
	}
}
