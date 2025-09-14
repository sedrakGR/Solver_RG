package org.ppit.core.brain;

import java.util.Collection;
import java.util.HashMap;

import org.ppit.core.brain.gaNode.GANode;
import org.ppit.core.brain.gaNode.action.GAAction;
import org.ppit.core.brain.gaNode.nucleus.GANucleus;
import org.ppit.core.brain.instance.action.ActionInstance;
import org.ppit.core.brain.instance.nucleus.NucleusInstance;
import org.ppit.core.cognition.CognitionManager;
import org.ppit.core.concept.primitive.PrimitiveType;
import org.ppit.util.exception.GAException;
import org.ppit.util.exception.PPITException;

/**
 * 
 * @author Karen
 * @detailed Describes the root of the Graph of Abstracts. 
 * 		This will be used to apply the instance of the Primitive Types
 *		and will output the list of activated abstracts.
 */
public class GA {
	
	// The map from PrimitiveTypes to the root Attributors.
	private HashMap<PrimitiveType, GANucleus> m_roots = null;
	public GA() {
		m_roots = new HashMap<PrimitiveType, GANucleus> ();
	}
	
	// Putting the abstract into the root.
	public void addRoot(PrimitiveType type, GANucleus abstr) throws GAException {
		if(m_roots.containsKey(type)) {
			throw new GAException("The root Node for " + type.getType() + " Primitive Type already exists.");
		}
		m_roots.put(type, abstr);
	}
	
	public void processInstance(NucleusInstance instance) throws GAException, PPITException {
		GANucleus node = m_roots.get(instance.getType());
		if(node != null){
			node.processInstance(instance);
		} else {
			throw new GAException("There is no Node registered for the given NucleusType: " +
					"" + instance.getType().getType() + ". Registered roots are: " + m_roots);
		}
	}
	
	public void reset() {
		m_roots.clear();
	}
	
	public void cleanCaches() {
		for(GANode node : m_roots.values()) {
			node.cleanCaches();
		}
		//TODO: very temp test solution
		// For some reason it is not being reached from roots, this will be fixed later
		if (CognitionManager.getInstance().getWM() != null) {
			for (ActionInstance instance : CognitionManager.getInstance().getWM().getActiveActions(0)) {
				instance.getGANode().handleCleanCaches();
			}
		}
	}
	
	public Collection<GANucleus> getRoots() {
		return m_roots.values();
	}
}
