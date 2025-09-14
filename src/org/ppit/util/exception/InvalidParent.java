package org.ppit.util.exception;

/**
 * 
 * @author Khachatryan Karen S.
 * @description this exception shall be thrown if the reffered concept
 * (parent concept) is not found in the library.
 *
 */

public class InvalidParent extends PPITException {
	public InvalidParent (String error) {
		super(error);
	}
}
