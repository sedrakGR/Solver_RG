
package org.ppit.util.exception;

/**
 * @author Karen Khachatryan S.
 * @description this shall be thrown if the CR1 rules are not saticfied.
 */
public class ConceptNotFound extends PPITException {

	public ConceptNotFound(String name) {
		super("Concept with " + name + " is not found.");
	}
}
