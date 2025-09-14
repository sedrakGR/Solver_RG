package org.ppit.actions.nucleus;


import org.ppit.core.concept.ConceptManager;
import org.ppit.util.exception.PPITException;

import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionSupport;


public class RemoveNucleus extends ActionSupport {
	private String m_nucleusList = null;
	private String m_nucleusName = null;
	private ConceptManager m_manager = ConceptManager.getInstance();
    
    /**
     * @see HttpServlet#HttpServlet()
     */
    public RemoveNucleus() {
        super();
    }
	
	public String execute() {
		try {
			m_manager.removeConcept(m_nucleusName);
			m_nucleusList = m_manager.getNucleusNames();
		} catch (PPITException e) {
			e.printStackTrace();
		}
		return Action.SUCCESS;
	}

	public String getNucleusList() {
		return m_nucleusList;
	}

	public void setNucleusName(String nucleusName) {
		m_nucleusName = nucleusName;
	}

}


