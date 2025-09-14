package org.ppit.actions.plan;
import org.ppit.core.concept.ConceptManager;
import com.opensymphony.xwork2.ActionSupport;

public class ViewPlan extends ActionSupport {
	private String m_plan = null;
	private String m_planName = null;
	ConceptManager m_manager = ConceptManager.getInstance();

	private static final long serialVersionUID = 1L;
    

    public ViewPlan() {
        super();
    }
	
	public String execute() {
		m_plan = m_manager.getPlan(m_planName).getJSON();
		return SUCCESS;
	}

	public String getPlanData() {
		return m_plan;
	}

	public void setPlanName(String planName) {
		m_planName = planName;
	}

}




