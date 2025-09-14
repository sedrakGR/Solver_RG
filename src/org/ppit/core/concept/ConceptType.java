package org.ppit.core.concept;

/**
 * @author Vahan Tadevosyan
 */

public enum ConceptType {
	
	COMPOSITE 			('c', "CompositeConcept"),
	AR1					('r', "AR1Concept"),
	PRIMITIVE			('p', "PrimitiveConcept"),
	NUCLEUS				('n', "NucleusConcept"),
	SET					('s', "SetConcept"),
	ACTION				('a', "ACTION"),
	VIRTUAL				('v', "VirtualConcept"),
	USAGE				('u', "UsageConcept");
	
	private char m_type;
	private String m_name = null;
	
	private ConceptType(char type, String name) {
		m_type = type;
		m_name = name;
	}
	
	public char getType () {
		return m_type;
	}
	
	public String getName() {
		return m_name;
	}
	
	public static ConceptType getConceptTypeByType(char type) {
		switch (type) {
			case 'c':
				return ConceptType.COMPOSITE;
			case 'p' :
				return ConceptType.PRIMITIVE;
			case 'n' :
				return ConceptType.NUCLEUS;
			case 's' :
				return ConceptType.SET;
			case 'a' :
				return ConceptType.ACTION;
			case 'v' :
				return ConceptType.VIRTUAL;
			case 'u' :
				return ConceptType.USAGE;
			case 'r' :
				return ConceptType.AR1;
		}
		
		return null;
	}
}
