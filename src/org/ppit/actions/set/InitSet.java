package org.ppit.actions.set;


import org.ppit.core.concept.ConceptManager;

import com.opensymphony.xwork2.ActionSupport;

public class InitSet extends ActionSupport {
	private String m_setList = null;
	private String m_conceptList = null;
	private ConceptManager m_manager = ConceptManager.getInstance();
    
    public InitSet() {
        super();
    }
	
	public String execute() {
		m_setList = m_manager.getSetNames();
		m_conceptList = m_manager.getConceptNames("all");
		return SUCCESS;
	}

	public String getSetList() {
		return m_setList;
	}
	
	public String getConceptList() {
		return m_conceptList;
	}
}