package org.ppit.core.learn;

import java.util.ArrayList;
import org.ppit.core.concept.Concept;

class BaseEntry
{
	private ArrayList<Integer> m_leftIds = new ArrayList<Integer>();
	private ArrayList<Integer> m_rightIds = new ArrayList<Integer>();
		
	private Concept m_concept;
	private int m_id;
	
	public BaseEntry() {
		m_concept = null;
		m_id = 0;
	}
	
	public BaseEntry(Concept baseConcept) {
		m_concept = baseConcept;
		m_id = 0;
	}
	
	public BaseEntry(Concept baseConcept, int id) {
		m_concept = baseConcept;
		m_id = id;
	}
	
	public BaseEntry clone() {
		BaseEntry clone = new BaseEntry(m_concept, m_id);
		
		for(Integer lId: m_leftIds)
			clone.addLeftCE(lId);
		for(Integer rId: m_rightIds)
			clone.addRightCE(rId);
		
		return clone;
	}
	
	public void addLeftCE(Integer lId) {
		m_leftIds.add(lId);
	}
	
	public void addRightCE(Integer rId) {
		m_rightIds.add(rId);
	}
	
	public ArrayList<Integer> getLeftIdList() {
		return m_leftIds;
	}
	
	public ArrayList<Integer> getRightIdList() {
		return m_rightIds;
	}
	
	public boolean removeLeftCE(Integer leftId) {
		return m_leftIds.remove(leftId);
	}
	
	public boolean removeRightCE(Integer rightId) {
		return m_rightIds.remove(rightId);
	}
	
	public int RLreferenceSize()
	{
		int count = 0;
		if(!m_rightIds.isEmpty()) {
			count += m_rightIds.size();
		}
		if(!m_leftIds.isEmpty()) {
			count += m_rightIds.size();
		}
		return count;
	}
	
	public void setId(int id) {
		m_id = id;
	}
	
	public int getId() {
		return m_id;
	}
	
	public Concept getConcept() {
		return m_concept;
	}
}
