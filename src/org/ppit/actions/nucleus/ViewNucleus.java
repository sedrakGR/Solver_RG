package org.ppit.actions.nucleus;

import org.ppit.core.concept.ConceptManager;

import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionSupport;


public class ViewNucleus extends ActionSupport {
	private String m_nucleusConc = null;
	private String m_nucleusName = null;
	ConceptManager m_manager = ConceptManager.getInstance();

    public ViewNucleus() {
        super();
    }
	
	public String execute() {
		m_nucleusConc = m_manager.getConcept(m_nucleusName).getJSON();
		return Action.SUCCESS;
	}
	
	public String getNucleusConc() {
		return m_nucleusConc;
	}

	public void setNucleusName(String nucleusName) {
		m_nucleusName = nucleusName;
	}
}


