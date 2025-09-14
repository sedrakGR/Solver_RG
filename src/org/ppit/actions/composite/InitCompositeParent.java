package org.ppit.actions.composite;



import org.ppit.core.concept.ConceptManager;

import com.opensymphony.xwork2.ActionSupport;

public class InitCompositeParent extends ActionSupport {

	private String m_conceptList;
	private ConceptManager m_manager = ConceptManager.getInstance();

    public InitCompositeParent() {
        super();
    }
	
	public String execute() {
		m_conceptList = m_manager.getConceptNames("composite");
		return SUCCESS;
	}
	
	public String getConceptList() {
		return m_conceptList;
	}

	public void setConceptList(String conceptList) {
		m_conceptList = conceptList;
	}

}