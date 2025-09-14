package org.ppit.actions.primitive;

import org.ppit.core.concept.ConceptManager;
import org.ppit.util.exception.PPITException;

import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionSupport;


public class RemovePrimitive extends ActionSupport {
	private String m_primitiveList = null;
	private String m_primitiveName = null;
	
	private ConceptManager m_manager = ConceptManager.getInstance();
    
    public RemovePrimitive() {
        super();
    }
	
	public String execute() {
		try{
		    m_manager.removeConcept(m_primitiveName);
		    m_primitiveList = m_manager.getPrimitiveNames();
		} catch (PPITException e) {
			e.printStackTrace();
		}
		return Action.SUCCESS;
	}

	public void setPrimitiveName(String primitiveName) {
		m_primitiveName = primitiveName;
	}

	public String getPrimitiveList() {
		return m_primitiveList;
	}

}
