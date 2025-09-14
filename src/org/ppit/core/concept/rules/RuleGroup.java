package org.ppit.core.concept.rules;

import java.util.ArrayList;
import java.util.Collection;

import org.ppit.core.brain.instance.InstanceBase;
import org.ppit.util.exception.GAException;

public class RuleGroup implements Cloneable {

	private ArrayList<BaseRule> m_rules = null;
	
	@Override
	public RuleGroup clone() {
		RuleGroup clone;
		try {
			clone = (RuleGroup)super.clone();
			if(m_rules != null ){
				clone.m_rules = new ArrayList<BaseRule>();
				for (BaseRule r : m_rules) {
					clone.m_rules.add(r.clone());
				}
			}
		}catch (CloneNotSupportedException e) {
			e.printStackTrace();
			return null;
		}
		return clone;
	}
	
	public void addRule(BaseRule r){
		if(m_rules == null) {
			m_rules = new ArrayList<BaseRule>(); 
		}
		m_rules.add(r); 
	}
	
	public boolean isEmpty() {
		if(m_rules == null) {
			return true;
		}
		return m_rules.isEmpty();
	}
	
	public Collection<BaseRule> getRules() {
		return m_rules;
	}
	
	public void addPrefix(String prefix) {
		if (m_rules != null) {
			for(BaseRule r : m_rules) {
				r.addPrefix(prefix);
			}
		}
	}
	
	/**
	 *  If all Rules are OK with this operation, then returns OK
	 *  If one of Rules violates then returns FAIL
	 *  If there are pending rules then returns PENDING
	 * @return the state of the RuleGroup after setting this attribute.
	 * @throws GAException 
	 */
	public int setAndCheckDependency (String name, InstanceBase instance) throws GAException {
		int state = Rule.R_OK;
		if(m_rules == null) {
			return state;
		}
		for(BaseRule r : m_rules) {
			int ruleState = r.setAndCheckDependency(name, instance);
			switch(ruleState){
			case BaseRule.R_OK : {
				
			}break;
			case BaseRule.R_FAIL : {
				return BaseRule.R_FAIL;
			}
			case BaseRule.R_PENDING :
			case BaseRule.R_L_PENDING : 
			case BaseRule.R_R_PENDING : {
				state = BaseRule.R_PENDING;
			} break;
			default :
				throw new GAException("Incorrect rule state: " + ruleState + " was returned");
			}
		}
		return state;
	}
	
	
	/**
	 * @brief Adds all rules of given RuleGroup in the list of rules
	 * @param ruleGroup, the RuleGroup, rules of which are copied
	 */
	public void addRuleGroup(RuleGroup ruleGroup) {
		if(ruleGroup != null && ruleGroup.isEmpty() == false){
			if(m_rules == null) {
				m_rules = new ArrayList<BaseRule>(); 
			}
			m_rules.addAll(ruleGroup.getRules());	
		}
	}
	
	/**
	 * @brief map keep to dependent rules,
	 * where the key is name of dependency concept, and the value is a List of rules
	 * dependent from that concept
	 */
	//private HashMap<String, ArrayList<IRule>> m_dependencyMap;
	
	//private HashMap<String, Boolean> m_unsolvedDeps = new HashMap<String, Boolean>();
	

	
	/**
	 * @brief setDependency Sets dependency of the concept
	 * @param depNodeName The name of dependent node
	 * @param refToConcept reference to a concept in the dependent
	 * @return returns true if dependency is set and false in other case
	 */
	/*
	public boolean setDependency(String depNodeName, PrimitiveConcept primitiveConcept)
	{
		boolean isSet = false;
		// Get rules from map with the key is depNodeName,
		// and set the dependency for that rule
		for (IRule rule : m_dependencyMap.get(depNodeName)) {
			isSet |= rule.setDependency(depNodeName, primitiveConcept); 
		}
		m_unsolvedDeps.remove(depNodeName);
		return isSet;
	}
	
	/*void x{
		m_dependencyMap = new HashMap<String, ArrayList<IRule>>();
		for (IRule rule : m_rules)
		{
			for (String dependency : rule.getDependencies())
			{
				ArrayList<IRule> dependets = m_dependencyMap.get(dependency);
				if (dependets == null)
				{
					// if the list of dependencies for the current primitive concept is not initialized
					// then initialize the list at first
					dependets = new ArrayList<IRule>();
				}
				dependets.add(rule);
				m_dependencyMap.put(dependency, dependets);
				m_unsolvedDeps.put(dependency, false);
			}
		}
		
		// add also own rule dependencies
		for (String dependency : m_ownRule.getDependencies()) {
			ArrayList<IRule> dependets = m_dependencyMap.get(dependency);
			if (dependets == null) {
				// if the list of dependencies for the current primitive concept is not initialized
				// then initialize the list at first
				dependets = new ArrayList<IRule>();
			}
			dependets.add(m_ownRule);
			m_dependencyMap.put(dependency, dependets);
			m_unsolvedDeps.put(dependency, false);
		}
	}*/
}
