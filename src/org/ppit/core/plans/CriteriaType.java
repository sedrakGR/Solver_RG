package org.ppit.core.plans;

/**
 * @brief enum to define types of criterias, currently only MIN and MAX exist
 * MIN means that the criteria needs to evaluate the the value and minimum value is better
 * MAX means that maximum value of criteria is better
 * 
 * @author Sedrak
 *
 */
public enum CriteriaType {
	MIN("MIN"),
	MAX("MAX"),
	UNKNOWN("unkown");
	
	private String m_text;
	
	private CriteriaType(String text) {
		m_text = text;
	}
	
	public String getText() {
		return m_text;
	}
	
	public static CriteriaType stringAsType(String type) {
		if (type.equalsIgnoreCase("MIN")) {
			return MIN;
		} else if (type.equalsIgnoreCase("MAX")) {
			return MAX;
		}
		// TODO: should never happen
		return UNKNOWN;
	}
}
