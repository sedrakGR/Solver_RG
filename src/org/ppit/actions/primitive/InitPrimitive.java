package org.ppit.actions.primitive;

import org.ppit.core.concept.ConceptManager;

import com.opensymphony.xwork2.ActionSupport;

public class InitPrimitive extends ActionSupport {

	private String m_primitiveList = null;
	
	private ConceptManager m_manager = ConceptManager.getInstance();
    
    public InitPrimitive() {
        super();
    }
	
	public String execute() {
		m_primitiveList = m_manager.getPrimitiveNames(); 
		return SUCCESS;
	}

	public String getPrimitiveList() {
		return m_primitiveList;
	}

}



