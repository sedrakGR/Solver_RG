package org.ppit.core.concept.rules;

import java.util.ArrayList;

import org.ppit.core.brain.instance.InstanceBase;
import org.ppit.core.brain.instance.nucleus.NucleusInstance;
import org.ppit.core.brain.instance.set.SetInstance;
import org.ppit.util.exception.UnsolvedDependency;

public class CountRule extends BaseRule {

	private RuleIn m_countRange = null;
	private SetInstance m_left = null;
	
	public CountRule(String leftName, RuleIn countRange) {
		super(leftName);
		m_countRange = countRange;
	}
	
	@Override
	public CountRule clone() {
		CountRule clone = (CountRule)super.clone();
		clone.m_countRange = m_countRange.clone();
		
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
	
	@Override
	public ArrayList<String> getDependencies() {
		return m_countRange.getDependencies();
	}

	@Override
	public void setDependency(String depNodeName, NucleusInstance refToInstance) {
		m_countRange.setDependency(depNodeName, refToInstance);
	}
	
	public int check()
	{
		if (m_left == null)
			return R_L_PENDING;
		if (m_countRange == null)
			return R_R_PENDING;
		try {
			if(m_countRange.check(m_left.getCount()) == true)
				return R_OK;
			return R_FAIL;
		}catch (UnsolvedDependency e) {
			return R_R_PENDING;
		}
	}
}
