package org.ppit.util.exception;

/**
 * @author Karen Khachatryan S.
 * @description this shall be thrown if the CR1 rules are not saticfied.
 */
public class WMException extends PPITException {

	public WMException(String error) {
		super("Working Memory Exception: " + error);
	}
}