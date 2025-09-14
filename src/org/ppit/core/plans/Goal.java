package org.ppit.core.plans;

import java.util.ArrayList;
import java.util.HashMap;

import org.ppit.core.brain.instance.InstanceBase;
import org.ppit.core.brain.instance.action.ActionInstance;
import org.ppit.core.cognition.CognitionManager;
import org.ppit.core.concept.composite.CompositeConcept;
import org.ppit.core.percept.Situation;
import org.ppit.util.exception.PPITException;

public class Goal {
	/**
	 * precondition is the concept that's needed for the goal to be applicable in a situation
	 */
	private CompositeConcept m_preConditionConcept = null;
	/**
	 * postcondition is the concept that indicates if the goal is achieved or not, if matched, then goal is achieved
	 */
	private CompositeConcept m_postConditionConcept = null;
	/**
	 * describes the depth of the tree to construct for reaching the postcondition
	 */
	private int m_depth = 1;
	
	/**
	 * describes if the goal is primary or midterm, because in the case its primary the processing might be different
	 */
	private boolean m_primary;
	
	private Evaluator m_evaluator;
	
	private String m_name;
	
	/**
	 * @brief the mapping of situations to the list of active in that situation abstracts
	 */	
	private HashMap<Situation, ArrayList<InstanceBase>> m_activeAbstracts = new HashMap<Situation, ArrayList<InstanceBase>>();
	
	public Goal(String name, boolean isPrimary) {
		//empty at least for now
		m_name = name;
		m_primary = isPrimary;
		m_evaluator = new Evaluator();
	}
	
	public void setPreCondition(CompositeConcept preCondition) {
		m_preConditionConcept = preCondition;
	}
	
	public void setPostCondition(CompositeConcept postCondition) {
		m_postConditionConcept = postCondition;
	}
	
	public void setDepth(int depth) {
		m_depth = depth;
	}
	
	public void setCriteria(int priority, Criteria criteria) {
		m_evaluator.setCriteria(priority, criteria);
	}
	
	public Evaluator getEvaluator() {
		return m_evaluator;
	}
	
	public String getName() {
		return m_name;
	}
	
	public int getDepth() {
		return m_depth;
	}
	
	public CompositeConcept getPreCondition() {
		return m_preConditionConcept;
	}
	
	public CompositeConcept getPostCondition() {
		return m_postConditionConcept;
	}
	
	public boolean isPrimary() {
		return m_primary;
	}
	
	public ArrayList<ActionInstance> findBestMoves(Situation situation, ArrayList<ActionInstance> activeActions, int side) {
		//TODO: activeActions compare against bestActions
		//TODO: fix issue of evaluation assignment, need min max to keep best actions instead of immediate removing after evaluation value
		
		ArrayList<ActionInstance> bestActionsList = new ArrayList<ActionInstance>();
		HashMap<Situation, ActionInstance> situationsToEvaluate = new HashMap<Situation, ActionInstance>();
		// checks if precondition is satisfied in the given situation
		if (m_preConditionConcept == null
				|| (m_preConditionConcept != null && CognitionManager.getInstance().getWM().hasActivatedInstances(m_preConditionConcept.getName()))) {
			m_activeAbstracts.put(situation, CognitionManager.getInstance().getWM().getActiveInstances());
			GameTree tree = generateTree(situation, m_depth, side);
			ArrayList<GameTreeNode> finalNodes = tree.getNodesInDepth(m_depth);
			for (GameTreeNode gameTreeNode : finalNodes) {
				if (m_postConditionConcept == null
						|| (m_postConditionConcept != null && gameTreeNode.hasActiveInstances(m_postConditionConcept.getName()))) {
					//goal is achieved in the current
				} else {
					tree.removeNode(gameTreeNode);
				}
			}
			if (!m_evaluator.isEmpty()) {
			// again get the list since this is changed, some nodes might be removed because they don't satisfy the postcondition
				finalNodes = tree.getNodesInDepth(m_depth);
				ArrayList<GameTreeNode> bestNodes = new ArrayList<GameTreeNode>();
				for (GameTreeNode leaveNode : finalNodes) {
					HashMap<Integer, Integer> evaluation = m_evaluator.evaluate(leaveNode, m_postConditionConcept);
					leaveNode.setEvaluationValue(evaluation);
					if (!bestNodes.isEmpty()) {
						// all the best nodes have the same value, thus just compare with one
						try {
							int result = leaveNode.compareEvaluation(bestNodes.get(0));
							if (result == 1) {
								// not a best node anymore, just remove it
								for (GameTreeNode bestNode : bestNodes) {
									tree.removeNode(bestNode);
								}
								bestNodes.clear();
								bestNodes.add(leaveNode);
							} else if (result == 0) {
								bestNodes.add(leaveNode);
							} else {
								tree.removeNode(leaveNode);
							}
						} catch (PPITException e) {
							e.printStackTrace();
							//do nothing here
						}
					} else {
						bestNodes.add(leaveNode);
					}
				}
			}// else {
				//already have the best moves list in the tree
				for (ActionInstance instance : tree.getRoot().getAppliedActions()) {
					bestActionsList.add(instance);
				}
			//}
		}
		for (int i = 0; i < bestActionsList.size(); ++i) {
			bestActionsList.get(i).getIdGroupsInInstance().print(true);
		}
		return bestActionsList;
	}
	
	//e.g. {"name":"aaaaa", "primary" : "true", "preCondition":"comp1", "postCondition":"comp2", "depth":"8",
	// "evaluator" : [ {"criteria" : "expression1", "prioroity" : "1", "type" : "MIN"}, {"criteria" : "expression2", "prioroity" : "2", "type" : "MAX"}]}
	public String getJSON()	{
		String goalJSON = "{'name' : '" + getName() + "', ";
		goalJSON += "'primary' : '" + (m_primary ? "true" : "false") + "', ";
		goalJSON += "'preCondition' : '" + m_preConditionConcept.getName() + "', ";
		goalJSON += "'postCondition' : '" + m_postConditionConcept.getName() + "', ";
		goalJSON += "'depth' : '" + m_depth + "', ";
		goalJSON += m_evaluator.getJSON();
		goalJSON += "}";
		return goalJSON;
	}
	
	/**
	 * 
	 * @param initialSituation, initial situation 
	 * @param depth, the depth of the game tree
	 * @param myMove, indicates if the move is goal achieving side move or opponents 
	 */
	public GameTree generateTree(Situation initialSituation, int depth, int side) {
		return new GameTree(initialSituation, side, depth);
	}
	
}
