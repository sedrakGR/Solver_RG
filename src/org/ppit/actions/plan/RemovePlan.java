package org.ppit.actions.plan;

import org.ppit.core.concept.ConceptManager;
import org.ppit.util.exception.PPITException;
import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionSupport;

public class RemovePlan extends ActionSupport {
	private static final long serialVersionUID = 1L;
	private String m_plansList = null;
	private String m_planName = null;
	private ConceptManager m_manager = ConceptManager.getInstance();
    

    public RemovePlan() {
        super();
    }
	
	public String execute() {
		try{
			m_manager.removePlan(m_planName);
			m_plansList = m_manager.getPlanNames();
		} catch (PPITException e) {
			e.printStackTrace();
		}
		return Action.SUCCESS;
	}

	public void setPlanName(String planName) {
		m_planName = planName;
	}

	public String getPlanList() {
		return m_plansList;
	}
}



