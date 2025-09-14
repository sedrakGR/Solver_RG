package org.ppit.actions.composite;


import org.ppit.core.concept.ConceptManager;
import org.ppit.util.exception.PPITException;

import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionSupport;


public class RemoveComposite extends ActionSupport {
	private String m_conceptList = null;
	private String m_compositeName = null;
	private ConceptManager m_manager = ConceptManager.getInstance();
    

    public RemoveComposite() {
        super();
    }
	
	public String execute() {
		try{
			m_manager.removeConcept(m_compositeName);
			m_conceptList = m_manager.getConceptNames("all");
		} catch (PPITException e) {
			e.printStackTrace();
		}
		return Action.SUCCESS;
	}



	public void setcompositeName(String compositeName) {
		m_compositeName = compositeName;
	}

	public String getConceptList() {
		return m_conceptList;
	}
}



