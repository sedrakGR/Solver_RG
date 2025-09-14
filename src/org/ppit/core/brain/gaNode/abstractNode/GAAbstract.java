package org.ppit.core.brain.gaNode.abstractNode;

import java.util.ArrayList;

import org.ppit.core.brain.gaNode.GANode;
import org.ppit.core.brain.instance.InstanceBase;
import org.ppit.core.brain.instance.abstractInstance.AbstractInstance;
import org.ppit.core.brain.instance.ar1.AR1Instance;
import org.ppit.core.concept.AbstractBase;
import org.ppit.core.concept.rules.Rule;
import org.ppit.core.concept.rules.RuleGroup;
import org.ppit.util.exception.GAException;

/**
 * 
 * @author SedrakG
 * @brief This represents Node of Abstract (Composite Concept) in the graph of abstracts
 *
 */
public class GAAbstract extends GANode {
	/**
	 * The list of partially matched Abstracts.
	 */
	private ArrayList<AbstractInstance> m_partialMatches = new ArrayList<AbstractInstance>();
	
	private boolean m_hasActiveInstance = false;
	
	/**
	 * List of dependent rules in the abstract of this node. These rules shall be satisfied 
	 *   in order an instance be fired forward
	 */
	RuleGroup m_dependentRules = null;
	
	/**
	 * m_abstract, indicates if the Abstract (Concept) of this GA Node is abstract (has abstract rule,
	 * for more about abstract rule please see RuleAbstract or read from Docs)
	 */
	boolean m_abstract = false;
	
	boolean m_negated = false;
	
	/**
	 * @param source Abstract to which this GA Node refers
	 * @param isAbstract, indicates if this Abstract (Concept) is abstract (has abstract rule,
	 * for more about abstract rule please see RuleAbstract or read from Docs)
	 * @param dependencies - the RuleGroup of dependent nodes.
	 */
	public GAAbstract(AbstractBase source, boolean isAbstract, boolean isNegated, RuleGroup dependencies) {
		super(source);
		m_abstract = isAbstract;
		m_negated = isNegated;
		m_dependentRules = dependencies;
	}

	public boolean isAbstract() {
		return m_abstract;
	}

	@Override
	public void handleElementActivated(String name, InstanceBase instance) throws GAException {
		// Virtual Abstracts do not use their have connections to get activated.
		
		if (getAbstract().getName().equals("test.check") || instance.getGANode().getAbstract().getName().equals("test.check")) {
			int i = 0;
			int j = i;
		}
		if(m_abstract)
			return;
		
		if(m_partialMatches.isEmpty()) {
			// Create the dummy partial instance. This will make sure that for all attributes
			// There will be something to start with (the new instance).
			AbstractInstance instanceForCurrElement = 
				new AbstractInstance(this, m_dependentRules.clone(), this.getElements().size());

			// Add it to the partial instance list.
			m_partialMatches.add(instanceForCurrElement);
		}
		
		// Iterate over all Partial Instances, check if this attribute is not set yet, then
		//    clone the instance and set the attribute. 
		// Check the Rule state: If Rules are violated then discard the instance
		//    if rules are satisfied and other attributes are not missing then fire the instance, 
		//    else add to the pending instance list.
		
		ArrayList<AbstractInstance> newPartialMatches = new ArrayList<AbstractInstance>();
				
		for(AbstractInstance partial: m_partialMatches) {
			// If the partial instance already contains this attribute, then nothing to do with it.
			if(partial.getElement(name) != null) {
				continue;
			}
			
			AbstractInstance newInstance = partial.clone();
			switch(newInstance.activateElement(name, instance)) {
			case AbstractInstance.A_FIRE : {
				// Fire the instance.
				// the negated abstracts are being activated in post processing
				// also negated abstract instace completness in fact means abstract is not found in the situation
				m_hasActiveInstance = true;
				if (!m_negated) {
					fireInstance(newInstance);
				}
			} break;
			case AbstractInstance.A_FAIL : {
				// TODO Discard the instance.
			} break;
			case AbstractInstance.A_A_PENDING :
			case AbstractInstance.A_R_PENDING : {
				// Queue the instance.
				newPartialMatches.add(newInstance);
			} break;
			default: {
				// TODO throw an exception.
			} }
		}
		// Append new partial instances to the pending list.
		m_partialMatches.addAll(newPartialMatches);
	}

	@Override
	public void handleChildActivated(InstanceBase instance) throws GAException {
		if(m_abstract) {
			RuleGroup dependencies = m_dependentRules.clone();
			if(dependencies.setAndCheckDependency("", instance) == Rule.R_OK) {
				fireInstance(instance);
			}
		}
	}
	
	@Override
	public void handleCleanCaches() {
		cleanCaches();
		
		m_partialMatches.clear();
		m_hasActiveInstance = false;
	}
	
	public boolean isActivedAsNegated() {
		if (!m_negated) {
			return false;
		}
		return !m_hasActiveInstance;
	}

	@Override
	public void handleParentActivated(InstanceBase instance) {
		if (m_negated) {
			m_hasActiveInstance = true;
		}
		
	}
}
