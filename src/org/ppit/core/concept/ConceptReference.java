package org.ppit.core.concept;

/**
 * 
 * @author SedrakG
 * @brief this class represents a reference to a concept by it's name
 * This is currently used for keeping parent of a concept in that concept
 *
 */
public class ConceptReference {
	/**
	 * @brief the parent concept
	 */
	private Concept m_concept = null;
	
	/**
	 * @brief name of the concept of the reference
	 */
	private String m_name = null;
	
	/**
	 * 
	 * @param name Name of the the concept to which reference is being created
	 */
	public ConceptReference(String name) {
		m_name = name;
	}
	
	public ConceptReference(String name, Concept concept) {
		m_name = name;
		m_concept = concept;
	}
	
	/**
	 * 
	 * @return Returns name of the concept of the reference
	 */
	public String getName() {
		return m_name;
	}
	
	/**
	 * 
	 * @return Returns the concept of the reference
	 */
	public Concept getConcept(){
		if (m_concept == null) {
			m_concept = ConceptManager.getInstance().getConcept(m_name);
		}
		return m_concept;
	}
}
