package org.ppit.util.exception;

/**
 * 
 * @author Khachatryan Karen S.
 * @description the exception shall be thrown if expression is not correct.
 *
 */

public class InvalidExpression extends PPITException {
	public InvalidExpression(String error) {
		super(error);
	}
}
