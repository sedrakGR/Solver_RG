package org.ppit.actions.goal;


import org.ppit.core.concept.ConceptManager;

import com.opensymphony.xwork2.ActionSupport;


public class ViewGoal extends ActionSupport {
	private String m_goal = null;
	private String m_goalName = null;
	ConceptManager m_manager = ConceptManager.getInstance();

	private static final long serialVersionUID = 1L;
    

    public ViewGoal() {
        super();
    }
	
	public String execute() {
		m_goal = m_manager.getGoal(m_goalName).getJSON();
		return SUCCESS;
	}

	public String getGoalData() {
		System.out.println(m_goal);
		return m_goal;
	}

	public void setGoalName(String goalName) {
		m_goalName = goalName;
	}

}




