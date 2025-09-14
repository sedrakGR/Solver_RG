package org.ppit.core.brain.gaNode.listener;

import java.util.ArrayList;

import org.ppit.core.brain.instance.InstanceBase;

/**
 * Collects all instances which have been activated.
 * @author Karen
 */
public class GAListenerCollecter extends GAListener {
	
	private ArrayList<InstanceBase> m_activatedInstances = new ArrayList<InstanceBase>(); 
	private InstanceBase m_lastInstance = null;
	
	public void handleInstanceActivated(InstanceBase instance) {
		m_lastInstance = instance;
		m_activatedInstances.add(instance);
	}
	
	public InstanceBase getLastInstance() {
		return m_lastInstance;
	}
	
	public InstanceBase getActiveInstance(int id) {
		return m_activatedInstances.get(id);
	}
	
	public ArrayList<InstanceBase> getActiveInstances() {
		return m_activatedInstances;
	}
	
	public int getActiveInstanceCount() {
		return m_activatedInstances.size();
	}
	
	public ArrayList<InstanceBase> getInstaces() {
		return m_activatedInstances;
	}
	
	@Override
	public void handleCleanCaches () {
		m_activatedInstances.clear();
		m_lastInstance = null;
	}
}
