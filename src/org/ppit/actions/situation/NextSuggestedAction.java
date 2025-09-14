package org.ppit.actions.situation;

import org.ppit.core.percept.SituationManager;

import com.opensymphony.xwork2.ActionSupport;

public class NextSuggestedAction extends ActionSupport {
	
	private static final long serialVersionUID = 1L;
	private static int m_currId = 0;
	private static SituationManager m_manager = SituationManager.getInstance();
	
	private String m_activeInstance = "";
	
    public NextSuggestedAction() {
        super();
    }
    
    public static void reset() {
    	m_currId = 0;
    }
	
	public String execute() {
		if (m_currId == m_manager.getSuggestedActionCount()) {
			m_currId = 0;
		}

		m_activeInstance = m_manager.getSuggestedAction(m_currId)
				.getIdGroupsInInstance().getOnlyIdsJSON(m_currId);

		++m_currId;
		return SUCCESS;
	}
	
	public String getNextSuggestedInstance() {
		return m_activeInstance;
	}
}
