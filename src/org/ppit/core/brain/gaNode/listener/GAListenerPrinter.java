package org.ppit.core.brain.gaNode.listener;

import org.ppit.core.brain.instance.InstanceBase;
import org.ppit.core.brain.instance.abstractInstance.AbstractInstance;
import org.ppit.core.brain.instance.nucleus.NucleusInstance;

public class GAListenerPrinter extends GAListener {

	@Override
	/**
	 * For now we do make manual switch, but this shall be fixed later (to use virtuality)
	 */
	public void handleInstanceActivated(InstanceBase instance) {
		switch (instance.getGANode().getAbstract().CONCEPT_TYPE) {
		case NUCLEUS:
			printInstance((NucleusInstance)instance);
			break;
		case COMPOSITE:
			printInstance((AbstractInstance)instance);
			break;
		case SET:
		case ACTION:
		default:
			break;
		}
	}
	
	public void printInstance(NucleusInstance instance) {
		System.out.println("A nucleus Instace has been activated for " + instance.getType() + " type." +
				" The value is: " + instance.getValue());
	}
	public void printInstance(AbstractInstance instance) {
		
	}
	
	@Override
	public void handleCleanCaches () {
		// Nothing can be done :-)
	}
}
