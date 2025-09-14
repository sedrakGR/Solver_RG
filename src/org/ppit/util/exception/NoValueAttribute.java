package org.ppit.util.exception;

/**
 * @author Karen Khachatryan S.
 * @description this shall be thrown if the CR1 rules are not saticfied.
 */
public class NoValueAttribute extends PPITException {

	public NoValueAttribute (String conceptName) {
		super("Error: Value attribute doesn't exists in " +
				conceptName + " concept.");
	}
}
