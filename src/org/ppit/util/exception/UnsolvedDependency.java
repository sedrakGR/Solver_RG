package org.ppit.util.exception;

/**
 * 
 * @author Khachatryan Karen S.
 * @description the exception shall be thrown if there is not solved dependency 
 * and evaluate is called
 *
 */

public class UnsolvedDependency extends PPITException {
	public UnsolvedDependency(String error) {
		super(error);
	}
}
