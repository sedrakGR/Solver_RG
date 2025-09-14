package org.ppit.util.exception;

/**
 * @author Karen Khachatryan S.
 * @description this shall be thrown if the CR1 rules are not saticfied.
 */
public class InvalidSituation extends PPITException {

	public InvalidSituation(String error) {
		super("Invalid Situation: " + error);
	}
}
