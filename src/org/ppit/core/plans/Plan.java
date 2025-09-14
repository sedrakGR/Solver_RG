package org.ppit.core.plans;


import java.util.ArrayList;
import java.util.HashMap;

import org.ppit.actions.situation.NextActiveInstance;
import org.ppit.core.brain.gaNode.listener.WorkingMemoryListener;
import org.ppit.core.brain.instance.InstanceBase;
import org.ppit.core.brain.instance.action.ActionInstance;
import org.ppit.core.cognition.CognitionManager;
import org.ppit.core.percept.Situation;
import org.ppit.util.exception.PPITException;

public class Plan {
	/**
	 *  goals of the plan
	 */
	private HashMap<Integer, Goal> m_goals = new HashMap<Integer, Goal>();
	private int m_matchCount = 0;
	
	/*
	 * plan name
	 */
	private String m_name;
	
	public Plan(String planName) {
		m_name = planName;
	}
	
	public void addGoal(Goal newGoal, int priority) throws PPITException {
		if (m_goals.containsKey(priority)) {
			// in fact should never get here
			throw new PPITException("Duplicate priority in the plan");
		}
		m_goals.put(priority, newGoal);
	}
	
	/**
	 * @brief executes the plan in order to find the best action for the given situation
	 * @param currentSituation Current situation where plan is being executed
	 * @param side Own side indicator
	 * @return returns the list of best actions to achieve the goal
	 */
	public ArrayList<ActionInstance> executePlan(Situation currentSituation, int side) {
		try {
			CognitionManager.getInstance().processSituation(currentSituation);
			//WorkingMemoryListener wml = m_cognition.getWM();
			
			//m_activatedAbstracts = wml.getActivatedAbstractsJSON();
		} catch (PPITException e) {
			System.out.println("Error when processing the situation: " + e.getMessage());
			e.printStackTrace();
		}
		
		ArrayList<ActionInstance> currentBestActions = CognitionManager.getInstance().getWM().getActiveActions(side);
		
		for (int i = 1; i <= m_goals.size(); ++i) {
			Goal goalToExecute = m_goals.get(i);
			ArrayList<ActionInstance> tempList = goalToExecute.findBestMoves(currentSituation, currentBestActions, side);
			if (!tempList.isEmpty()) {
				if (goalToExecute.isPrimary()) {
					return tempList;
				}
				if (tempList.size() == 1) {
					return tempList;
				} else {
					currentBestActions = tempList;
				}
			}
		}
		return currentBestActions;
	}
	
	public String getName() {
		return m_name;
	}
	
	public HashMap<Integer, Goal> getGoals() {
		return m_goals;
	}
	
	//{"name":"name","goals":[ {"name" : "Goal 1" , "priority" : "1"}, {"name" : "Goal 3", "priority" : "2"}]}
	public String getJSON() {
		String planJSON = "{'name' : '" + m_name + "' , 'goals' : [";
		int currentGoalNumber = 0;
		for (int priority : m_goals.keySet()) {
			planJSON += "{ 'name' : '" + m_goals.get(priority).getName() + "' , ";
			planJSON += "'priority' : '" + priority + "'}";
			if (++currentGoalNumber < m_goals.size()) {
				planJSON += ",";
			}
		}
		planJSON += "]}";
		return planJSON;
	}
	
	public void resetMatchingCount() {
		m_matchCount = 0;
	}
	
	public int getMatchCount() {
		return m_matchCount;
	}
	
	public void increaseMatchingCount() {
		++m_matchCount;
	}
}
