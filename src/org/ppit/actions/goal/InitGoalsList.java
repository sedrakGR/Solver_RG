package org.ppit.actions.goal;

import org.ppit.core.concept.ConceptManager;

import com.opensymphony.xwork2.ActionSupport;

public class InitGoalsList extends ActionSupport {

	private String m_goalsList = "";
    
    /**
     * @see HttpServlet#HttpServlet()
     */
    public InitGoalsList() {
        super();
    }
	
	public String execute() {
		m_goalsList = ConceptManager.getInstance().getGoalNames();
		return SUCCESS;
	}

	public String getGoalList() {
		return m_goalsList;
	}
}
