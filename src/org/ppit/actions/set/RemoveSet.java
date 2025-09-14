package org.ppit.actions.set;

import org.ppit.core.concept.ConceptManager;
import org.ppit.util.exception.PPITException;

import com.opensymphony.xwork2.ActionSupport;

public class RemoveSet extends ActionSupport {
	private String m_setName = null;
	private String m_conceptList = null;
	private String m_setList = null;
	private ConceptManager m_manager = ConceptManager.getInstance(); 

    public RemoveSet() {
        super();
    }
	
	public String execute() {		
		try{
			m_manager.removeConcept(m_setName);
			m_setList = m_manager.getSetNames();
			m_conceptList = m_manager.getConceptNames("all");
		} catch (PPITException e) {
			e.printStackTrace();
		}
		return SUCCESS;
	}

	public void setSetName(String setName) {
		m_setName = setName;
	}
	
	public String getConceptList() {
		return m_conceptList;
	}
	
	public String getSetList() {
		return m_setList;
	}
}
