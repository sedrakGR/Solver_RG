package org.ppit.actions.planWrapper;
import org.ppit.core.concept.ConceptManager;
import com.opensymphony.xwork2.ActionSupport;

public class ViewPlanWrapper extends ActionSupport {
	private String m_planWrapper = null;
	private String m_planWrapperName = null;
	ConceptManager m_manager = ConceptManager.getInstance();

	private static final long serialVersionUID = 1L;
    

    public ViewPlanWrapper() {
        super();
    }
	
	public String execute() {
		m_planWrapper = m_manager.getPlanWrapper(m_planWrapperName).getJSON();
		return SUCCESS;
	}

	public String getPlanWrapperData() {
		return m_planWrapper;
	}

	public void setPlanWrapperName(String planWrapperName) {
		m_planWrapperName = planWrapperName;
	}

}




