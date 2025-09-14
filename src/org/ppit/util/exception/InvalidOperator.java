package org.ppit.util.exception;

/**
 * 
 * @author Khachatryan Karen S.
 * @description this exception shall be thrown if the operator is not valid (supported)
 *
 */

public class InvalidOperator extends PPITException {
	public InvalidOperator(String error) {
		super(error);
	}
}
