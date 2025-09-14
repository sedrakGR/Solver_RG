package org.ppit.core.concept.rules;

import java.util.ArrayList;

import org.ppit.core.brain.instance.InstanceBase;
import org.ppit.core.brain.instance.nucleus.NucleusInstance;
import org.ppit.util.exception.UnsolvedDependency;

/*
 * Contains the complete definition of a single Rule. It holds references both on right 
 * and on left expressions, so that when nucleus instances are set, it can check to answer if 
 * Rule is satisfied or not.
 */
public class Rule extends BaseRule {
	
	// The instance of the left attribute. 
	private NucleusInstance m_left = null; 
	
	// The operator and the value of the right side operator.
	private IRule m_right = null;
	
	public Rule(IRule right, String leftName) {
		super(leftName);
		m_right = right;
	}
	
	public Rule clone()
	{
		Rule clone = (Rule)super.clone();
		
		// We do not need to clone the nucleus instance, there are two cases:
		//  1 - we will use it, this means there is no need to duplicate the same instance
		//  2 - we won't use it, this means we will set the new one, and this will be reset
		clone.m_right = m_right.clone();
		clone.m_left = m_left;
		
		return clone;
	}
	
	/**
	 * @brief Check and Extract the nucleus abstracts from the given instance 
	 *        in order to initialize its Left node and possible references in the Right expression.
	 * @param name - the name of the instance, this shall match to the Rule prefix root.
	 * @param instance
	 * @return
	 */
	public int setAndCheckDependency(String name, InstanceBase instance) {
		// TODO Add a dependency list caching to improve speed.
		
		// Solving the dependencies.
		InstanceBase leftInstance = instance.getInstance(getPrefix());
		
		if(leftInstance != null && leftInstance instanceof NucleusInstance) {
			this.setLeft((NucleusInstance)leftInstance);
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
	
	public void setLeft(NucleusInstance leftInstance) {
		m_left = leftInstance;
	}
	
	public void setDependency(String depNodeName, NucleusInstance refToInstance){
		m_right.setDependency(depNodeName, refToInstance);
	}
	
	public ArrayList<String> getDependencies() {
		return m_right.getDependencies();
	}
	
	/*
	 *  If the left side corresponds to the right side then R_OK is returned
	 *  If the left side doesn't satisfy the right side conditions then R_FAIL is returned
	 *  If the right side (or the left side) has unsolved dependencies then R_PENDING is returned
	 */
	public int check()
	{
		if (m_left == null)
			return R_L_PENDING;
		if (m_right == null)
			return R_R_PENDING;
		try {
			if(m_right.check(m_left.getValue()) == true)
				return R_OK;
			return R_FAIL;
		}catch (UnsolvedDependency e) {
			return R_R_PENDING;
		}
	}
	
	//new method, returns rule of Rule :)
	public IRule getRule() {
		return m_right;
	}
}
