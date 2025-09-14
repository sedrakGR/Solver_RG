package org.ppit.actions.nucleus;

import org.ppit.core.concept.ConceptManager;

import com.opensymphony.xwork2.ActionSupport;

/**
 * Servlet implementation class InitNucleus
 */
public class InitNucleus extends ActionSupport {
	
	private String m_nucleusList;
	private ConceptManager m_manager = ConceptManager.getInstance();

	private static final long serialVersionUID = 1L;
    

    public InitNucleus() {
        super();
    }
	
	public String execute() {
		m_nucleusList = m_manager.getNucleusNames();
		return SUCCESS;
	}

	public String getNucleusList() {
		return m_nucleusList;
	}
}


