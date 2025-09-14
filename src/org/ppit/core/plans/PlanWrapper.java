package org.ppit.core.plans;

import org.ppit.core.brain.instance.InstanceBase;
import org.ppit.core.cognition.CognitionManager;
import org.ppit.core.concept.composite.CompositeConcept;
import org.ppit.util.exception.WMException;

public class PlanWrapper {
	
	private Plan m_plan = null;
	
	private CompositeConcept m_precondition = null;
	String m_name;
	
	private Evaluator m_evaluator;

	
	public PlanWrapper(String name, Plan plan, CompositeConcept concept) {
		m_name = name;
		m_plan = plan;
		m_precondition = concept;
		m_evaluator = new Evaluator();
	}
	
	public String getName() {
		return m_name;
	}
	
	public boolean isGoodForProcessedSituation() {
		if (CognitionManager.getInstance().getWM().hasActivatedInstances(m_precondition.getName())) {
			m_plan.increaseMatchingCount();
			return true;
		}
		return false;
	}
	
	public InstanceBase getPreConditionActiveInstance() {
		try {
			return CognitionManager.getInstance().getWM().getActivatedInstances(m_precondition.getName()).getActiveInstance(0);
		} catch (WMException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	public void setCriteria(int priority, Criteria criteria) {
		m_evaluator.setCriteria(priority, criteria);
	}
	
	public Evaluator getEvaluator() {
		return m_evaluator;
	}
	
	public Plan getPlan() {
		return m_plan;
	}
	
	public CompositeConcept getPreCondition() {
		return m_precondition;
	}
	
	public String getJSON()	{
		String planWrapperJSON = "{'name' : '" + m_name + "', ";
		planWrapperJSON += "'preCondition' : '" + m_precondition.getName() + "', ";
		planWrapperJSON += "'plan' : '" + m_plan.getName() + "', ";
		planWrapperJSON += m_evaluator.getJSON();
		planWrapperJSON += "}";
		return planWrapperJSON;
	}
	
}
