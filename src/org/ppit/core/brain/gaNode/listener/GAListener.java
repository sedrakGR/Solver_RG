package org.ppit.core.brain.gaNode.listener;

import org.ppit.core.brain.gaNode.GANode;
import org.ppit.core.brain.instance.InstanceBase;

/**
 * This interface is used to register the event listener for GANodes. 
 * It can be used to add additional outputs from the nodes we are interested in.
 * @author Karen
 *
 */
public abstract class GAListener {
	
	private GANode m_source = null;
	
	/**
	 * Stops previous subscription if any and sets the new one.
	 * @param source
	 */
	public void registerTo(GANode source) {
		if (m_source != null) {
			m_source.reomveListener(this);
		}
		m_source = source;
		m_source.registerListener(this);
	}
	
	public void unregister() {
		if (m_source != null) {
			m_source.reomveListener(this);
		}
	}
	
	public abstract void handleInstanceActivated(InstanceBase instance);
	
	public abstract void handleCleanCaches();
}
