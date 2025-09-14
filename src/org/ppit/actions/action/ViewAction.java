package org.ppit.actions.action;

import org.ppit.core.concept.ConceptManager;

import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionSupport;


public class ViewAction extends ActionSupport {

	/**
	 * @brief Action content in a JSON string
	 */
	private String m_actionContent = "";
	/**
	 * @brief Name of the action
	 */
	private String m_actionName;

	private static final long serialVersionUID = 1L;
	private ConceptManager m_conceptManager = ConceptManager.getInstance();
    

    public ViewAction() {
        super();
    }
	
	public String execute() {
		m_actionContent = m_conceptManager.getAction(m_actionName).getJSON();
		return Action.SUCCESS;
	}

	//TODO: Fix the name of this method
	public String getActionConc() {
		return m_actionContent;
	}

	public void setActionName(String actionName) {
		m_actionName = actionName;
	}
}
