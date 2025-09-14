package org.ppit.actions.action;


import org.ppit.core.concept.ConceptManager;

import com.opensymphony.xwork2.ActionSupport;


public class InitAction extends ActionSupport {
	
	/**
	 * @brief List of action names in a string
	 */
	private String m_actionList = "";
    
    /**
     * @see HttpServlet#HttpServlet()
     */
    public InitAction() {
        super();
    }
	
	public String execute() {
		m_actionList = ConceptManager.getInstance().getActionNames();
		return SUCCESS;
	}

	public String getActionList() {
		return m_actionList;
	}
}