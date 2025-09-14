package org.ppit.core.brain.gaNode.ar1;

import java.util.HashMap;

import org.ppit.core.brain.gaNode.GANode;
import org.ppit.core.brain.instance.InstanceBase;
import org.ppit.core.brain.instance.ar1.AR1Instance;
import org.ppit.core.brain.instance.nucleus.NucleusInstance;
import org.ppit.core.concept.AbstractBase;
import org.ppit.util.exception.GAException;

/**
 * 
 * @author SedrakG
 * @brief This represents the class GAAR1, node of AR1 (a.k.a. CR1) in the graph of abstracts
 */
public class GAAR1 extends GANode {
	
	/**
	 * If AR1Instance is filled, it is fired and removed from the map.
	 * HashMap<int: groupId, AR1Instance: partialInstance> m_partialMatches;
	 */
	private HashMap<Integer, AR1Instance> m_partialMatches = null;
	
	private boolean m_abstract = false;
	
	boolean m_negated = false;
	
	boolean m_hasActiveInstace = false;

	/**
	 * 
	 */
	public GAAR1(AbstractBase source, boolean isAbstract, boolean isNegated) {
		super(source);
		m_abstract = isAbstract;
		m_negated = isNegated;
		m_partialMatches = new HashMap<Integer, AR1Instance>();
	}
	
	public boolean isAbstract() {
		return m_abstract;
	}

	@Override
	public void handleElementActivated(String name, InstanceBase instance) throws GAException {
		// Virtual AR1s doesn't use their have connections to get activated.
		if(m_abstract)
			return;
		
		NucleusInstance element = (NucleusInstance) instance;
		AR1Instance ar1Instance = m_partialMatches.get(element.getIdGroup().getIdGroup());
		if(ar1Instance == null) {
			ar1Instance = new AR1Instance(this, element.getIdGroup(), this.getElements().size());
			m_partialMatches.put(ar1Instance.getGroupId(), ar1Instance);
		}
		
		boolean isComplete = ar1Instance.activateElement(name, element);
		m_hasActiveInstace |= isComplete;
		if (isComplete && !m_negated) {
			fireInstance(ar1Instance);
			
			// Remove the instance from the list.
			m_partialMatches.remove(ar1Instance.getGroupId());
		}
	}
	
	public boolean isActivedAsNegated() {
		if (!m_negated) {
			return false;
		}
		for (AR1Instance instance : m_partialMatches.values()) {
			if (instance.isComplete()) {
				return false;
			}
		}
		return !m_hasActiveInstace;
	}

	@Override
	public void handleChildActivated(InstanceBase instance) throws GAException {
		// AR1s don't have rules to check, hence, 
		//  any child activation is forwarded silently if the Node is virtual, otherwise is ignored.
		if (m_abstract) 
			fireInstance(instance);
	}

	@Override
	public void handleCleanCaches() {
		cleanCaches();
		
		m_partialMatches.clear();
		m_hasActiveInstace = false;
	}

	@Override
	public void handleParentActivated(InstanceBase instance) {
		if (m_negated) {
			m_hasActiveInstace = true;
		}
		
	}
}
