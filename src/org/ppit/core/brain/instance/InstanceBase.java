package org.ppit.core.brain.instance;

import java.util.HashSet;

import org.ppit.core.brain.gaNode.GANode;

/**
 * 
 * @author SedrakG
 * @brief Describes the instance of GA Node. When GA node is activated,
 * an instance of it is fired. This is the base class for NucleusInstance,
 * AR1Instance, AbstractInstance, SetInstance, ActionInstance 
 */
public abstract class InstanceBase implements Cloneable{
	/**
	 * @brief Holders are instances, which activations are dependent on this instance activation
	 */
	private HashSet<InstanceBase> m_holders = null;
	
	/**
	 * @brief node is the type which fires this instance. 
	 *  When the instance becomes partial (due to removing some attributes)  
	 *  it shall notify the node 
	 */
	private GANode m_node = null;

	/**
	 * @brief each Instance is being created in a single GANode, which serves as its type
	 * hence the instance keeps a reference to its type (GANode).
	 */
	protected InstanceBase(GANode type) {
		m_node = type;
		m_holders = new HashSet<InstanceBase>();
	}
	
	public GANode getGANode() {
		return m_node;
	}
	
	public boolean addHolder(InstanceBase holder) {
		return m_holders.add(holder);
	}
	
	public InstanceBase clone()
	{
		InstanceBase clone;
		try {
			clone = (InstanceBase)super.clone();
			
			// Passing the reference to the GA node.
			clone.m_node = m_node;
			clone.m_holders = new HashSet<InstanceBase>();
			clone.m_holders.addAll(m_holders);
		}catch (CloneNotSupportedException e) {
			e.printStackTrace();
			return null;
		}
		return clone;
	}
	
	/**
	 * This shall rather be an abstract method, however, because of "clone()" method it is here.
	 * @param path - the full name of the nested attribute.
	 * @return
	 */
	public InstanceBase getInstance(String path) {
		return null;
	}
	
	public abstract IdGroupContainer getIdGroupsInInstance();
	
}
