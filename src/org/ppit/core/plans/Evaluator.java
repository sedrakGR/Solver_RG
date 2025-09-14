package org.ppit.core.plans;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.apache.commons.collections.MapIterator;
import org.ppit.core.brain.gaNode.listener.GAListenerCollecter;
import org.ppit.core.brain.gaNode.listener.WorkingMemoryListener;
import org.ppit.core.brain.instance.InstanceBase;
import org.ppit.core.brain.instance.abstractInstance.AbstractInstance;
import org.ppit.core.cognition.CognitionManager;
import org.ppit.core.concept.composite.CompositeConcept;
import org.ppit.core.percept.Situation;
import org.ppit.util.exception.PPITException;
import org.ppit.util.exception.WMException;

public class Evaluator {
	/**
	 * @brief the map of priorities to criteria.
	 * The lesser number of priorities indicates the higher priority
	 */
	Map<Integer, Criteria> m_criteriaMap;
	public Evaluator() {
		m_criteriaMap = new TreeMap<Integer, Criteria>();
	}
	
	public void setCriteria(int priority, Criteria criteria) {
		m_criteriaMap.put(priority, criteria);
	}
	
	public void evaluate(ArrayList<GameTreeNode> nodesToEvaluate) {
		//TODO:
	}
	
	public int evaluate(Situation situation) {
		//TODO:
		return 1;
	}
	
	public HashMap<Integer, Integer> evaluate(InstanceBase activeInstance) {
		if (activeInstance != null) {
			// represents the mapping of priority and its evaluated value;
			HashMap<Integer, Integer> evaluation = new HashMap<Integer, Integer>();
			for (int i = 0; i < m_criteriaMap.size(); ++i) {
				// i also indicates the priority - current highest priority is i + 1
				Criteria criteria = m_criteriaMap.get(i + 1);
				int value = criteria.evaluate((AbstractInstance)activeInstance);
				evaluation.put(i + 1, value);
			}
			return evaluation;
		} else {
			return null;
		}
	}
	
	public HashMap<Integer, Integer> evaluate(GameTreeNode node, CompositeConcept postCondition) {
		
		InstanceBase activeInstance = null;
		for (InstanceBase instance : node.getActiveAbstracts()) {
			if (instance.getGANode().getAbstract().equals(postCondition)) {
				activeInstance = instance;
				break;
			}
		} 

		// We actually expect only 1 instance
		//TODO: fixme if needed, maybe 1 instance will not be enough
		if (activeInstance != null) {
			// represents the mapping of priority and its evaluated value;
			HashMap<Integer, Integer> evaluation = new HashMap<Integer, Integer>();
			for (int i = 0; i < m_criteriaMap.size(); ++i) {
				// i also indicates the priority - current highest priority is i + 1
				Criteria criteria = m_criteriaMap.get(i + 1);
				int value = criteria.evaluate((AbstractInstance)activeInstance);
				evaluation.put(i + 1, value);
			}
			return evaluation;
		} else {
			return null;
		}
		
	}
	
	public boolean isEmpty() {
		return m_criteriaMap.isEmpty();
	}
	
	public Map<Integer, Criteria> getCriteriaMap() {
		return m_criteriaMap;
	}
	
	public String getJSON() {
		String evaluatorJSON = "'evaluator' : [";
		int count = 0;
		for (Map.Entry<Integer, Criteria> entry : m_criteriaMap.entrySet()) {
			++count;
			evaluatorJSON += "{" + entry.getValue().getJSON() + " , 'priority' : '" + entry.getKey() + "'}";
			if (count < m_criteriaMap.size()) {
				evaluatorJSON += " , ";
			}
		}
		evaluatorJSON += "]";
		return evaluatorJSON;
	}
	
	/**
	 * Compares two nodes evaluations
	 * @param otherNode Node to compare with
	 * @return Returns 1/0/-1 value range, 1 - if this node is better, 0 if they are equal and -1 if otherNode is better
	 */
	public static int compareEvaluation(HashMap<Integer, Integer> first, HashMap<Integer, Integer> second) throws PPITException {
		if (first.size() != second.size()) {
			throw new PPITException("Different evaluations are trying to be compared");
		}
		for (int i = 0; i < first.size(); ++i) {
			if (first.get(i + 1) == second.get(i + 1)) {
				continue;
				// compare next priority
			} else {
				return first.get(i + 1) > second.get(i + 1) ? 1 : -1;
			}
		}
		// if all priority criteria values are equal then those evaluations are equal
		return 0;
	}
}
