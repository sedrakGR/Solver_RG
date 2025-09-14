package org.ppit.util.exception;

/**
 * @author Karen Khachatryan S.
 * @description this shall be thrown if the there are concepts with duplicate names
 */
public class NameDuplication extends PPITException {

	public NameDuplication(String conceptName) {
		super("The concept with " + conceptName + 
				" name already exists in the library.");
	}

	public NameDuplication(String conceptName, String extra) {
		super("The concept with " + conceptName + 
				" name already exists in the library: " + extra);
	}

}
