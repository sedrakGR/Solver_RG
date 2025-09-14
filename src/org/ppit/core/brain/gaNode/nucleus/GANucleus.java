package org.ppit.core.brain.gaNode.nucleus;

import org.ppit.core.brain.GAEdge;
import org.ppit.core.brain.gaNode.GANode;
import org.ppit.core.brain.instance.InstanceBase;
import org.ppit.core.brain.instance.nucleus.NucleusInstance;
import org.ppit.core.concept.primitive.PrimitiveConcept;
import org.ppit.core.concept.rules.IRule;
import org.ppit.util.exception.GAException;
import org.ppit.util.exception.UnsolvedDependency;

/**
 * 
 * @author SedrakG
 * @brief This Class describes the node of Nucleus Type (Primitive Concept)
 */
public class GANucleus extends GANode {
	
	/**
	 * @brief This is only nucleus type's own rule
	 * Rule is not dependent
	 * Rule is not abstract
	 * Rule is not [*]
	 */
	private IRule m_rule = null;
	
	public GANucleus(PrimitiveConcept source) {
		super(source);
		m_rule = source.getRule();
	}

	@Override
	public void handleElementActivated(String name, InstanceBase instance) {
		// Do nothing - never happens
	}

	@Override
	public void handleChildActivated(InstanceBase instance) {
		// Do nothing
	}
	
	public boolean filterInstance(NucleusInstance instance) throws UnsolvedDependency {
		return m_rule.check(instance.getValue());
	}
	
	/**
	 * Check and fire the instance forward.
	 * @param instance
	 * @throws UnsolvedDependency 
	 * @throws GAException 
	 */
	public void processInstance(NucleusInstance instance) throws UnsolvedDependency, GAException {
		if (this.filterInstance(instance) == true) {
			
			// Fire generic (Parents, Composers, Listeners).
			fireInstance(instance);

			// Fire forward (Nucleus specific).
			for (GAEdge edge : getChildren()) {
				GANucleus node = (GANucleus)edge.getSlave();
				node.processInstance(instance);
			}
		}
	}

	@Override
	public void handleCleanCaches() {
		// Process generic (Parents, Composers, Listeners).
		cleanCaches();

		// Process forward (Nucleus specific).
		for (GAEdge edge : getChildren()) {
			GANucleus node = (GANucleus)edge.getSlave();
			node.cleanCaches();
		}
	}

	@Override
	public void handleParentActivated(InstanceBase instance) {
		// TODO Auto-generated method stub
		
	}
	
}
