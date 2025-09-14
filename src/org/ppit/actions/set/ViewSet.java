package org.ppit.actions.set;

import org.ppit.core.concept.ConceptManager;

import com.opensymphony.xwork2.ActionSupport;

public class ViewSet extends ActionSupport {
	private String m_set = null;
	private String m_setName;
	private ConceptManager m_manager = ConceptManager.getInstance();
	
	
	public ViewSet() {
        super();
    }
	
	public String execute() {
		m_set = m_manager.getConcept(m_setName).getJSON();
		return SUCCESS;
	}

	public String getSet() {
		return m_set;
	}

	public void setSetName(String setName) {
		m_setName = setName;
	}
}