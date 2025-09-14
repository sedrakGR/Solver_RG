
package org.ppit.actions.situation;

import org.ppit.core.brain.gaNode.listener.GAListenerCollecter;
import org.ppit.core.brain.gaNode.listener.WorkingMemoryListener;
import org.ppit.core.cognition.CognitionManager;
import org.ppit.util.exception.PPITException;

import com.opensymphony.xwork2.ActionSupport;

public class NextActiveInstance extends ActionSupport {
	
	private static String m_abstractName = "";
	private static GAListenerCollecter m_collector;
	private static int m_currId = 0;
	
	private String m_activeInstance = "";
	private boolean m_new = false;
	private CognitionManager m_cognition = CognitionManager.getInstance();
	
    public NextActiveInstance() {
        super();
    }
    
    public static void reset() {
    	m_currId = 0;
    	m_abstractName = "";
    }
	
	public String execute() {
		try {
			if(m_new) {
				WorkingMemoryListener wml = m_cognition.getWM();
				m_collector = wml.getActivatedInstances(m_abstractName);
				m_currId = 0;
			}
			if(m_currId == m_collector.getActiveInstanceCount()){
				m_currId = 0;
			}
			
			m_activeInstance = m_collector.getActiveInstance(m_currId)
				.getIdGroupsInInstance().getOnlyIdsJSON(m_currId);
			
			++m_currId;
		} catch (PPITException e) {
			System.out.println("Error when processing the situation: " + e.getMessage());
			e.printStackTrace();
			return ERROR;
		}
		return SUCCESS;
	}

	public void setAbstractName(String abstractName) {
		if(!m_abstractName.equals(abstractName)) {
			m_new = true;
		}
		m_abstractName = abstractName;
	}
	
	public String getNextInstance() {
		return m_activeInstance;
	}
}
