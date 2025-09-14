package org.ppit.actions.composite;



import org.ppit.core.concept.ConceptManager;

import com.opensymphony.xwork2.ActionSupport;

public class InitComposite extends ActionSupport {

	private String m_conceptList;
	private ConceptManager m_manager = ConceptManager.getInstance();

	private static final long serialVersionUID = 1L;

    public InitComposite() {
        super();
    }
	
	public String execute() {
		m_conceptList = m_manager.getConceptNames("all");
		return SUCCESS;
	}

	public String getConceptList() {
		return m_conceptList;
	}
}