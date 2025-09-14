package org.ppit.util.exception;

/**
 * 
 * @author Karen Khachatryan S.
 * @description this is the base class of Exceptions.
 *
 */

public class PPITException extends Exception {
	
	private String m_error;
	
	public PPITException(String error) {
		super("PPIT Exception: " + error);
		m_error = error;
	}
	
	public String getError() {
		return m_error;
	}
}
