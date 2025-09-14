package org.ppit.actions.planWrapper;

import org.ppit.core.concept.ConceptManager;
import org.ppit.util.exception.PPITException;
import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionSupport;

public class RemovePlanWrapper extends ActionSupport {
	private static final long serialVersionUID = 1L;
	private String m_wrappersList = null;
	private String m_WrapperName = null;
	private ConceptManager m_manager = ConceptManager.getInstance();
    

    public RemovePlanWrapper() {
        super();
    }
	
	public String execute() {
		try{
			m_manager.removePlanWrapper(m_WrapperName);
			m_wrappersList = m_manager.getPlanWrapperNames();
		} catch (PPITException e) {
			e.printStackTrace();
		}
		return Action.SUCCESS;
	}

	public void setPlanWrapperName(String wrapperName) {
		m_WrapperName = wrapperName;
	}

	public String getPlanWrapperList() {
		return m_wrappersList;
	}
}



