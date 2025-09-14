package org.ppit.actions.situation;

import java.util.ArrayList;

import org.ppit.core.brain.gaNode.listener.WorkingMemoryListener;
import org.ppit.core.brain.instance.action.ActionInstance;
import org.ppit.core.cognition.CognitionManager;
import org.ppit.core.concept.ConceptManager;
import org.ppit.core.percept.Situation;
import org.ppit.core.percept.SituationManager;
import org.ppit.core.plans.Goal;
import org.ppit.util.exception.PPITException;

import com.opensymphony.xwork2.ActionSupport;

public class ProcessSituation extends ActionSupport {
	
	private String m_situationName;
	private String m_activatedAbstracts = "";
	
	private SituationManager m_manager = SituationManager.getInstance();
	private CognitionManager m_cognition = CognitionManager.getInstance();
	
    public ProcessSituation() {
        super();
    }
	
	public String execute() {
		try {
			NextActiveInstance.reset();
			Situation situation = m_manager.getLibraryForTest().getSituation(m_situationName);
			//TODO
			//situation.toFen() + "w" + "- - 0 1";
			m_cognition.processSituation(situation);
			WorkingMemoryListener wml = m_cognition.getWM();
			m_activatedAbstracts = wml.getActivatedAbstractsJSON();
		} catch (PPITException e) {
			System.out.println("Error when processing the situation: " + e.getMessage());
			e.printStackTrace();
			return ERROR;
		}
		
		return SUCCESS;
	}

	public void setSituationName(String situationName) {
		if (situationName.isEmpty()) {
			m_situationName = m_manager.getLastSituationName();
		} else {
			m_situationName = situationName;
		}
	}
	
	public String getActivatedAbstracts() {
		
		return m_activatedAbstracts;
	}
}
