package org.ppit.core.concept;

import org.ppit.core.brain.gaNode.GANode;
import org.ppit.core.concept.composite.CompositeConcept;
import org.ppit.core.concept.primitive.PrimitiveConcept;
import org.ppit.util.Definitions;
import org.ppit.util.exception.PPITException;

/**
 * 
 * @author SedrakG
 * This is the base class for Action and Concept
 */
public class AbstractBase {
	/**
	 * @brief The full name (database name) of the abstract, 
	 * 		  every abstract has name and this serves as an ID for it.
	 */
	private String m_name = null;
		
	/**
	 * @brief node of GA, which refers to this abstract 
	 */
	GANode m_node = null;
	/**
	 * @brief type of concept kept in base concept or action
	 * it can have one of values
	 * p if concept is primitive
	 * c if composite
	 * s if set
	 * a is action 
	 */
	final public ConceptType CONCEPT_TYPE;
	
	public AbstractBase(ConceptType type, String name) {
		CONCEPT_TYPE = type;
		m_name = name;
	}
	
	public ConceptType getDetailedType() {
		switch (CONCEPT_TYPE) {
		case USAGE:
		case VIRTUAL:
		case ACTION:
		case SET:
			return CONCEPT_TYPE;
		case NUCLEUS:
		case PRIMITIVE:
		{
			PrimitiveConcept pc = (PrimitiveConcept)this;
			if(pc.isNucleus()) {
				return ConceptType.NUCLEUS;
			} else {
				return ConceptType.PRIMITIVE;
			}
				
		}
		case COMPOSITE:
		case AR1:
		{
			CompositeConcept cc = (CompositeConcept)this;
			if(cc.isUsage()) {
				return ConceptType.USAGE;
			}
			if(cc.isAbstract()) {
				return ConceptType.VIRTUAL;
			}
			if(cc.isCR1()) {
				return ConceptType.AR1;
			}
			return ConceptType.COMPOSITE;
		}
		}
		return CONCEPT_TYPE;
	}
	
	/**
	 * @brief Get the full name of abstract (as appears in DB)
	 * @return returns the full name
	 */
	public String getName() {
		return m_name;
	}

	/**
	 * @brief Returns true, if the concept is a Zero level concept, 
	 *   which means that it is not a attribute of other concepts.
	 * @detail This is necessary when creating GA from already loaded concepts.
	 */
	public boolean isZeroLevel() {
		return m_name.indexOf(Definitions.subattributeAccessorChar) == -1;
	}
	
	/**
	 * @return-s the Root of the abstract name. For example for "K.T.P" abstract it will return "K".
	 */
	public String getRoot() {
		int nestingPos = m_name.indexOf(Definitions.subattributeAccessorChar);
		if(nestingPos == -1) {
			return m_name;
		}
		return m_name.substring(0, nestingPos);
	}

	/**
	 * 
	 * @return Returns the node which refers to this abstract
	 * @throws PPITException
	 */
	public GANode getGANode() throws PPITException {
		if (m_node != null) {
			return m_node;
		} else if (CONCEPT_TYPE != ConceptType.ACTION) {
			Concept concept = (Concept)this;
			//TODO: Karen please review this,
			// if concept node is null, then we set is as parent's node
			m_node = concept.getParent().getGANode(); 
			return m_node;
		} else {
			throw new PPITException("There is no GA Node asociated with the action");
		}
	}
	
	/**
	 * 
	 * @param GA node, node which contains this
	 */
	public void setGANode(GANode node) {
		m_node = node;
	}
	
	public boolean hasGANode() {
		return m_node != null;
	}
	
	public String toText(boolean full) {
		return m_name;
	}
	
}
