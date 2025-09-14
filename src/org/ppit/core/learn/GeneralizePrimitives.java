package org.ppit.core.learn;

import java.util.ArrayList;
import org.ppit.core.concept.Concept;
import org.ppit.core.concept.primitive.PrimitiveConcept;

public class GeneralizePrimitives implements GeneralizationBase
{
	PrimitiveConcept m_leftConcept = null;
	PrimitiveConcept m_rightConcept = null;
	
	public void initialize(Concept left, Concept right) {
		m_leftConcept = (PrimitiveConcept)left;
		m_rightConcept = (PrimitiveConcept)right;
	}
	
	public void generalize() {
		if(m_leftConcept.getType().getName().equals(m_rightConcept.getType().getName())) {
			ArrayList<PairedEntry> peList = new ArrayList<PairedEntry>();

			ContainerEntry left = new ContainerEntry(m_leftConcept.getParent());
			ContainerEntry right = new ContainerEntry(m_rightConcept.getParent());
			BaseEntry base = new BaseEntry(m_leftConcept.getParent());
			
			peList.add(new PairedEntry(base, left, right));			
			m_peList.add(peList);
		}
	}
	
	public Concept generateConcepts() {
		PrimitiveConcept concept = null;
		
		for(ArrayList<PairedEntry> peList: m_peList) {
			concept = (PrimitiveConcept) peList.get(0).getBase().getConcept();
			//give some name to the concept
		}
		
		return concept;
	}
	
	public void print_result() {
		int index = 0;
		System.out.println("here we go");
		
		for(ArrayList<PairedEntry> peList: m_peList) {
			if(peList.isEmpty())
				continue;
			
			System.out.print(index + ".");
			
			for(PairedEntry pe: peList)
				System.out.println("\t" + pe.getLeft().getConcept().getName() +"\t" +  pe.getBase().getConcept().getName() +"\t" +  pe.getRight().getConcept().getName());
			
			++index;
		}
	}
}
