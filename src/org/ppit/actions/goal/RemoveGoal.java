package org.ppit.actions.goal;
import org.ppit.core.concept.ConceptManager;
import org.ppit.util.exception.PPITException;

import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionSupport;

public class RemoveGoal extends ActionSupport {
	private static final long serialVersionUID = 1L;
	private String m_goalsList = null;
	private String m_goalName = null;
	private ConceptManager m_manager = ConceptManager.getInstance();
    

    public RemoveGoal() {
        super();
    }
	
	public String execute() {
		try{
			m_manager.removeGoal(m_goalName);
			m_goalsList = m_manager.getGoalNames();
		} catch (PPITException e) {
			e.printStackTrace();
		}
		return Action.SUCCESS;
	}

	public void setGoalName(String goalName) {
		m_goalName = goalName;
	}

	public String getGoalList() {
		return m_goalsList;
	}
}



