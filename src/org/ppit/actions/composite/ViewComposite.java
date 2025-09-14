package org.ppit.actions.composite;


import org.ppit.core.concept.ConceptManager;

import com.opensymphony.xwork2.ActionSupport;


public class ViewComposite extends ActionSupport {
	private String m_compositeConc = null;
	private String m_compositeName = null;
	ConceptManager m_manager = ConceptManager.getInstance();

	private static final long serialVersionUID = 1L;
    

    public ViewComposite() {
        super();
    }
	
	public String execute() {
		m_compositeConc = m_manager.getConcept(m_compositeName).getJSON();
		return SUCCESS;
	}

	public String getCompositeConc() {
		return m_compositeConc;
	}

	public void setCompositeName(String compositeName) {
		m_compositeName = compositeName;
	}

}




