package org.ppit.actions.planWrapper;

import org.ppit.core.concept.ConceptManager;

import com.opensymphony.xwork2.ActionSupport;

public class InitWrappersList extends ActionSupport {

	private String m_WrapperList = "";
    
    /**
     * @see HttpServlet#HttpServlet()
     */
    public InitWrappersList() {
        super();
    }
	
	public String execute() {
		m_WrapperList = ConceptManager.getInstance().getPlanWrapperNames();
		return SUCCESS;
	}

	public String getPlanWrapperList() {
		return m_WrapperList;
	}
}
