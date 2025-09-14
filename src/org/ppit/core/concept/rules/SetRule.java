package org.ppit.core.concept.rules;

import java.util.ArrayList;
import org.ppit.core.brain.instance.InstanceBase;
import org.ppit.core.brain.instance.abstractInstance.AbstractInstance;
import org.ppit.core.brain.instance.nucleus.NucleusInstance;
import org.ppit.core.brain.instance.set.SetInstance;

public class SetRule extends BaseRule {

	private BaseRule m_wrapped;
	private SetInstance m_left = null;
	
	public SetRule(String leftName, BaseRule wrapperd) {
		super(leftName);
		m_wrapped = wrapperd;
	}

	@Override
	public SetRule clone() {
		SetRule clone = (SetRule)super.clone();
		clone.m_wrapped = m_wrapped.clone();
		
		// Not cloning as in case of Usual Rule's left side.
		clone.m_left = m_left;
		return clone;
	}
	
	@Override
	public int setAndCheckDependency(String name, InstanceBase instance) {
		
		// Getting the Set concept.
		InstanceBase leftInstance = instance.getInstance(getPrefix());
		
		if(leftInstance != null && leftInstance instanceof SetInstance) {
			this.setLeft((SetInstance)leftInstance);
		}
		
		for(String dependency : this.getDependencies()) {
			// Solving the dependencies.
			InstanceBase rightDep = instance.getInstance(dependency);
			
			if(rightDep != null && rightDep instanceof NucleusInstance) {
				this.setDependency(dependency, (NucleusInstance)rightDep);
			}	
		}

		return check();
	}
	
	public void setLeft(SetInstance left) {
		m_left = left;
	}
	
	public void setDependency(String depNodeName, NucleusInstance refToInstance){
		m_wrapped.setDependency(depNodeName, refToInstance);
	}

	@Override
	public ArrayList<String> getDependencies() {
		return m_wrapped.getDependencies();
	}
	
	public int check()
	{
		if (m_left == null)
			return R_L_PENDING;
		if (m_wrapped == null)
			return R_R_PENDING;
		
		// For all Set elements check that the rule satisfies.
		for(AbstractInstance ei : m_left.getElements()) {
			switch (m_wrapped.setAndCheckDependency("", ei))
			{
			case R_OK:
				continue;
			case R_FAIL:
				return R_FAIL;
			case R_L_PENDING:
				return R_L_PENDING;
			case R_R_PENDING:
				return R_R_PENDING;
			default:
				// TODO throw an exception.
			}			
		}
		return R_OK;
	}
}
