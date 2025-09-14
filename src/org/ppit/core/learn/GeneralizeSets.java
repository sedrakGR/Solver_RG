package org.ppit.core.learn;

import java.util.ArrayList;
import org.ppit.core.concept.Concept;
import org.ppit.core.concept.composite.CompositeConcept;
import org.ppit.core.concept.set.SetConcept;

//does not ready, just written something wrong
public class GeneralizeSets implements GeneralizationBase
{
	CompositeConcept m_leftConcept = null;
	CompositeConcept m_rightConcept = null;
	GeneralizeComposites m_genComposites = null;
	
	
	public void initialize(Concept left, Concept right) {
		SetConcept leftSet = (SetConcept)left;
		SetConcept rightSet = (SetConcept)right;
		m_leftConcept = leftSet.getElement();
		m_rightConcept = rightSet.getElement();
		m_genComposites.initialize(m_leftConcept, m_rightConcept);
	}
	
	public void generalize() {
		m_genComposites.generalize();
	}
	
	public ArrayList<Concept> generateConcepts() {
		return m_genComposites.generateConcepts();
	}
}
