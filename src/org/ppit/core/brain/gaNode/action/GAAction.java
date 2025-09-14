package org.ppit.core.brain.gaNode.action;

import org.ppit.core.brain.gaNode.GANode;
import org.ppit.core.brain.gaNode.listener.GAListener;
import org.ppit.core.brain.instance.InstanceBase;
import org.ppit.core.brain.instance.abstractInstance.AbstractInstance;
import org.ppit.core.brain.instance.action.ActionInstance;
import org.ppit.core.concept.AbstractBase;
import org.ppit.util.exception.GAException;

/**
 * 
 * @author SedrakG
 * @brief This represents the node of GA for an action
 */
public class GAAction extends GANode{
	/**
	 * Constructor
	 * @param source source Action to which this node refers
	 */
	public GAAction(AbstractBase source) {
		super(source);
	}

	@Override
	public void handleElementActivated(String name, InstanceBase instance) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void handleChildActivated(InstanceBase instance) {
		// TODO Auto-generated method stub
		
	}
	
	public void handleActorActivated(InstanceBase instance) throws GAException {
		if (!(instance instanceof AbstractInstance)) {
			throw new GAException("activated instance is not composite abstract");
		}
		ActionInstance action = new ActionInstance(this, (AbstractInstance)instance);
		for(GAListener listener : m_externalListeners){
			listener.handleInstanceActivated(action);
			
		}
	}
	
	@Override
	public void handleCleanCaches() {
		cleanCaches();
	}

	@Override
	public void handleParentActivated(InstanceBase instance) {
		// TODO Auto-generated method stub
		
	}
}
