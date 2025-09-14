package org.ppit.core.brain.instance.action;

import org.ppit.core.brain.gaNode.GANode;
import org.ppit.core.brain.instance.IdGroupContainer;
import org.ppit.core.brain.instance.InstanceBase;
import org.ppit.core.brain.instance.abstractInstance.AbstractInstance;
import org.ppit.core.brain.instance.nucleus.NucleusInstance;
import org.ppit.core.concept.AbstractBase;
import org.ppit.core.concept.action.Action;
import org.ppit.core.concept.expression.Expression;
import org.ppit.util.Definitions;
import org.ppit.util.exception.UnsolvedDependency;

public class ActionInstance extends InstanceBase {
	
	public ActionInstance(GANode type, AbstractInstance precodnition) {
		super(type);
		m_precondition = precodnition;
	}
	
	public InstanceBase getInstance(String path)
	{
		if(path.isEmpty()) {
			return this;
		}
		if (path.equals(Definitions.preCondConceptJSON)) {
			return m_precondition;
		}
		if (path.startsWith(Definitions.preCondConceptJSON)) {
			path = path.substring(Definitions.preCondConceptJSON.length());
		}
		return m_precondition.getInstance(path);
		//return null;
	}
	
	public boolean checkSide(int side) {
		AbstractBase abstractBase = getGANode().getAbstract();
		Action action = (Action)abstractBase;
		Expression sideIndicator = action.getSideIndicator().clone();
		//actually should be only one dependency
		for (String dependency : sideIndicator.getDependencies()) {
			InstanceBase dependentInstance = getInstance(dependency);
			sideIndicator.setDependency(dependency, (NucleusInstance)dependentInstance);
		}
		int sideOfInstance; 
		try {
			sideOfInstance = sideIndicator.evaluate();
		} catch (UnsolvedDependency e) {
			e.printStackTrace();
			return false;
		}
		return (sideOfInstance == side);
	}
	
	/**
	 * @brief Instance of action's precondition
	 */
	private AbstractInstance m_precondition = null;

	@Override
	public IdGroupContainer getIdGroupsInInstance() {
		// TODO implement me :-)
		if(m_precondition != null) {
			return m_precondition.getIdGroupsInInstance();
		}
		
		return new IdGroupContainer();
	}
}
