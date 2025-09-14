package org.ppit.actions.action;

import org.ppit.core.concept.ConceptManager;
import org.ppit.util.exception.PPITException;

import com.opensymphony.xwork2.ActionSupport;


public class RemoveAction extends ActionSupport {
	private String m_actionList = null;
	private String m_actionName = null;
	
	private ConceptManager m_conceptManager = ConceptManager.getInstance();

    /**
     * @see HttpServlet#HttpServlet()
     */
    public RemoveAction() {
        super();
    }
	
	public String execute() {		
		try {
			m_conceptManager.removeAction(m_actionName);
			m_actionList = m_conceptManager.getActionNames();
		} catch (PPITException e) {
			e.printStackTrace();
		}
		return SUCCESS;
	}

	public void setActionName(String actionName) {
		m_actionName = actionName;
	}

	public String getActionList() {
		return m_actionList;
	}
}
