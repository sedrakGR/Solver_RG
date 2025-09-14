package org.ppit.actions.plan;

import org.ppit.core.concept.ConceptManager;

import com.opensymphony.xwork2.ActionSupport;

public class InitPlansList extends ActionSupport {

	private String m_PlansList = "";
    
    /**
     * @see HttpServlet#HttpServlet()
     */
    public InitPlansList() {
        super();
    }
	
	public String execute() {
		m_PlansList = ConceptManager.getInstance().getPlanNames();
		return SUCCESS;
	}

	public String getPlanList() {
		return m_PlansList;
	}
}
