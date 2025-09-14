package org.ppit.core.learn;

import java.util.ArrayList;
import org.ppit.core.concept.Concept;

class ContainerEntry
{
	private ArrayList<Integer> m_baseIds = new ArrayList<Integer>();
	private Concept m_concept = null;
	
	public ContainerEntry(Concept concept) {
		m_concept = concept;
	}
	
	public ContainerEntry clone() {
		ContainerEntry clone = new ContainerEntry(m_concept);
		
		for(Integer id: m_baseIds)
			clone.addBaseId(id);
		
		return clone;
	}
	
	public void addBaseId(Integer Id) {
		m_baseIds.add(Id);
	}
	
	public ArrayList<Integer> getBaseIdList() {
		return m_baseIds;
	}
	
	public boolean removeBaseId(Integer id) {
		return m_baseIds.remove(id);
	}
	
	public void removeBaseIds() {
		m_baseIds.clear();
	}
	
	Concept getConcept() {
		return m_concept;
	}
}
