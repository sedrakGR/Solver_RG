package org.ppit.core.brain.gaNode.set;

import java.util.ArrayList;
import java.util.HashMap;

import org.ppit.core.brain.gaNode.GANode;
import org.ppit.core.brain.gaNode.nucleus.GANucleus;
import org.ppit.core.brain.instance.InstanceBase;
import org.ppit.core.brain.instance.abstractInstance.AbstractInstance;
import org.ppit.core.brain.instance.nucleus.NucleusInstance;
import org.ppit.core.brain.instance.set.SetInstance;
import org.ppit.core.concept.AbstractBase;
import org.ppit.core.concept.primitive.PrimitiveConcept;
import org.ppit.core.concept.rules.RuleIn;
import org.ppit.core.concept.set.SetConcept;
import org.ppit.util.exception.GAException;

/**
 * 
 * @author SedrakG
 * @brief This represents the GA node for set, which refers to a set concept 
 *
 */
public class GASet extends GANode {
	
	/**
	 * @brief Partially matched instances
	 */
	private ArrayList<SetInstance> m_partialMatches = new ArrayList<SetInstance>();
	
	private RuleIn m_elementCount = null;
	
	private HashMap<NucleusInstance, ArrayList<SetInstance>> m_matchesPerConstants = new HashMap<NucleusInstance, ArrayList<SetInstance>>();
	
	private boolean m_hasConstants = false;
	
	private ArrayList<String> m_constants = new ArrayList<String>();
	
	private String m_continuous = "";
	
	/**
	 * 
	 * @param source, the source Set concept, to which this node refers
	 */
	public GASet(AbstractBase source) {
		super(source);
		m_constants = ((SetConcept)source).getConstants();
		m_hasConstants = !m_constants.isEmpty();
		m_continuous = ((SetConcept)source).getContinuous();
	}
	
	public void setRule(RuleIn rule) {
		m_elementCount = rule;
	}
	
	public RuleIn getRule() {
		return m_elementCount;
	}

	@Override
	public void handleElementActivated(String name, InstanceBase instance) throws GAException {
		if(m_partialMatches.isEmpty()) {
			// Create the dummy partial instance. This will make sure that for all attributes
			// There will be something to start with (the new instance).
			SetInstance instanceForCurrElement = new SetInstance(this, m_elementCount.clone());
			// Add it to the partial instance list.
			m_partialMatches.add(instanceForCurrElement);
		}
		
		// Iterate over all Partial Instances, clone the instance and set the attribute. 
		// If Count is satisfied then fire the instance, else  
		//    else, if other instances are missing, add to the pending instance list.
		
		ArrayList<SetInstance> newPartialMatches = new ArrayList<SetInstance>();
		for(SetInstance partial: m_partialMatches) {			
			SetInstance newInstance = partial.clone();
			
			switch(newInstance.activateElement((AbstractInstance)instance, m_constants, m_continuous)) {
			case SetInstance.S_FIRE_KEEP : {
				newPartialMatches.add(newInstance);
				fireInstance(newInstance);
			} break;
			case SetInstance.S_FIRE_REMOVE : {
				fireInstance(newInstance);
			} break;
			case SetInstance.S_PENDING : {
				newPartialMatches.add(newInstance);
			} break;
			case SetInstance.S_FAIL : {
				// TODO discard the instance.
			} break;
			default: {
				// TODO throw an exception.
			} }
		}
		// Append new partial instances to the pending list.
		m_partialMatches.addAll(newPartialMatches);
	}

	@Override
	public void handleChildActivated(InstanceBase instance) {
		// Sets are not virtual, hence the activation of the Child doesn't have any impact.
	}
	
	@Override
	public void handleCleanCaches() {
		cleanCaches();
		
		m_partialMatches.clear();
	}

	@Override
	public void handleParentActivated(InstanceBase instance) {
		// TODO Auto-generated method stub
		
	}
}
