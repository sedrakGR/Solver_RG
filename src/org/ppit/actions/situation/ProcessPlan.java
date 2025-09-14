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

public class ProcessPlan extends ActionSupport {
	
	private String m_situationName;
	private String m_planName;
	private int m_side;
	ArrayList<ActionInstance> m_listOfActions;

	private ConceptManager m_manager = ConceptManager.getInstance();
	private CognitionManager m_cognition = CognitionManager.getInstance();
	
    public ProcessPlan() {
        super();
    }
	
	public String execute() {
		// reset list of suggestions;
		NextSuggestedAction.reset();
		Situation situation = SituationManager.getInstance()
				.getLibraryForTest().getSituation(m_situationName);
		Plan plan = m_manager.getPlan(m_planName);
		if (plan == null) {
			System.out.println("plan is null with the given name: "
					+ m_planName);
			return ERROR;
		}

		m_listOfActions = SituationManager.getInstance().processPlan(plan,
				situation, m_side);
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
	
	public void setPlanName(String planName) {
		m_planName = planName;
	}
	
}
