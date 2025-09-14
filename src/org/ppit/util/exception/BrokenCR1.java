package org.ppit.util.exception;

/**
 * @author Karen Khachatryan S.
 * @description this shall be thrown if the CR1 rules are not saticfied.
 */
public class BrokenCR1 extends PPITException {

	public BrokenCR1(String error) {
		super("Broken CR1: " + error);
	}
}
