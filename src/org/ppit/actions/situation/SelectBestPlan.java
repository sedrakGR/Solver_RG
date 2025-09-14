package org.ppit.actions.situation;

import java.util.ArrayList;

import org.ppit.core.brain.gaNode.listener.WorkingMemoryListener;
import org.ppit.core.brain.instance.action.ActionInstance;
import org.ppit.core.cognition.CognitionManager;
import org.ppit.core.concept.ConceptManager;
import org.ppit.core.percept.Situation;
import org.ppit.core.percept.SituationManager;
import org.ppit.core.plans.Goal;
import org.ppit.core.plans.Plan;
import org.ppit.util.exception.PPITException;

import com.opensymphony.xwork2.ActionSupport;

public class SelectBestPlan extends ActionSupport {
	
	private String m_situationName;
	private int m_side;
	ArrayList<ActionInstance> m_listOfActions;

    public SelectBestPlan() {
        super();
    }
	
	public String execute() {
		Situation situation = SituationManager.getInstance()
				.getLibraryForTest().getSituation(m_situationName);
		try {
			Plan bestPlan = SituationManager.getInstance().selectBestPlan(situation, m_side);
			m_listOfActions = SituationManager.getInstance().processPlan(bestPlan,
					situation, m_side);
		} catch (PPITException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (m_listOfActions.isEmpty()) {
			return ERROR;
		}
		return SUCCESS;
	}
	
	

	public void setSituationName(String situationName) {
		if (situationName.isEmpty()) {
			m_situationName = SituationManager.getInstance().getLastSituationName();
		} else {
			m_situationName =  situationName;
		}
	}
	
	public void setSide(String side) {
		m_side = Integer.parseInt(side);
	}
	
}
