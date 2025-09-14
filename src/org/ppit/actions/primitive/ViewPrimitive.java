package org.ppit.actions.primitive;

import org.ppit.core.concept.ConceptManager;

import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionSupport;


public class ViewPrimitive extends ActionSupport {
	private String m_primitiveConc = null;
	private String m_primitiveName = null;
	
	ConceptManager m_manager = ConceptManager.getInstance();
    

    public ViewPrimitive() {
        super();
    }
	
	public String execute() {
		m_primitiveConc = m_manager.getConcept(m_primitiveName).getJSON();
		return SUCCESS;
	}

	public String getPrimitiveConc() {
		return m_primitiveConc;
	}

	public void setPrimitiveName(String primitiveName) {
		m_primitiveName = primitiveName;
	}

}




