package org.ppit.core.brain.instance.ar1;

import java.util.HashMap;

import org.ppit.core.brain.gaNode.ar1.GAAR1;
import org.ppit.core.brain.instance.IdGroupContainer;
import org.ppit.core.brain.instance.InstanceBase;
import org.ppit.core.brain.instance.nucleus.IdGroup;
import org.ppit.core.brain.instance.nucleus.NucleusInstance;
import org.ppit.util.Definitions;
import org.ppit.util.exception.GAException;

/**
 * 
 * @author SedrakG
 * This describes the instance of AR1 node in Graph of Abstracts
 */
public class AR1Instance extends InstanceBase{
	/**
	 * @brief Attributes of AR1, which are NucleusIstances
	 * HashMap<String: attribute_name, NucleusInstance: value> m_attributes;
	 */
	private HashMap<String, NucleusInstance> m_attributes = null;
	
	/**
	 * @brief the group of AR1, which is contained also in Nucleus Instances of this AR1
	 */
	private IdGroup m_groupId = null;
	
	/**
	 * @brief the number of attributes in the AR1 type.
	 */
	int m_numOfAttributes = 0;
	
	/**
	 * The number of "set" attributes.
	 */
	int m_currNumOfAttributes = 0;
	
	/**
	 * 
	 * @param type - the AR1 GANode which was used to create this instance.
	 * @param groupId - the Id of the AR1 group, this shall be the same for all attributes.
	 * @param numOfAttributes - the number of attributes in this AR1. It is used to 
	 * 	 	  whether the Instance is complete or not.
	 */
	public AR1Instance(GAAR1 type, IdGroup groupId, int numOfAttributes) {
		super(type);
		m_groupId = groupId;
		m_numOfAttributes = numOfAttributes;
		m_currNumOfAttributes = 0;
		m_attributes = new HashMap<String, NucleusInstance>();
	}
	
	public AR1Instance clone(){
		return null;
	}
	
	public InstanceBase getInstance(String path)
	{
		if(path.isEmpty()) {
			return this;
		}

		int rootEnd = path.indexOf(Definitions.subattributeAccessor);
		String root = path;
		if(rootEnd != -1) {
			root = path.substring(0, rootEnd);
		}
		// The attributes inside the abstract are relative, hence, 
		// the root is the name of the attribute.
		InstanceBase attr = m_attributes.get(root);
		if (attr == null) {
			return null;
		} else {
			if(rootEnd == -1) {
				return attr;
			} else {
				return attr.getInstance(path.substring(rootEnd + 1));
			}
		}
	}

	/**
	 * Returns the NucleusInstance of the element.
	 * @param name - the relative name of the element.
	 */
	public NucleusInstance getElement(String name) {
		return m_attributes.get(name);
	}
	
	/**
	 * @brief if an attribute with the same name already was set, it will be replaced.
	 * @param name - the name of the element.
	 * @param instance - the instance of the element.
	 * @return true if the AR1 instance is complete, false othervise
	 * @throws GAException
	 */
	public boolean activateElement(String name, NucleusInstance instance) throws GAException {
		// Check assertion
		if (instance.getIdGroup().getIdGroup() != m_groupId.getIdGroup()) {
			throw new GAException("An element with " + instance.getIdGroup().getIdGroup() + "" +
					" has been passed while " + m_groupId + " was expected.");
		}
		if(m_attributes.containsKey(name) == false)
			++m_currNumOfAttributes;
		
		m_attributes.put(name, instance);
		instance.addHolder(this);
		
		if(m_currNumOfAttributes == m_numOfAttributes) {
			return true;
		}
		return false;
	}
	
	/**
	 * @brief if an attribute has been removed, the instance gets de-activated
	 *        it becomes a partial instance in its creator and triggers its holders to
	 *        become partial instances as well.
	 * @param name - the name of the de-activated attribute.
	 */
	public void deactivateElement(String name) {
		NucleusInstance instance = m_attributes.remove(name); 
		if(instance != null){
			--m_currNumOfAttributes;
			
			// Check following 3 cases:
			// 1st. 
			if(m_currNumOfAttributes == 0) {
				// TODO
				// Tell the "Type - GANode" to remove the instance, it is dummy!
			} else if (m_currNumOfAttributes == m_numOfAttributes - 1) {
				// TODO
				// This is the roll-back operation, tell all holders to threat this attribute
				// invalid and tell the Type node to add this as a partial instance.
			} else {
				// TODO
				// Do nothing, this shall already be a partial instance in the Type node.
			}
		}
	}
	
	public int getGroupId() {
		return m_groupId.getIdGroup();
	}

	@Override
	public IdGroupContainer getIdGroupsInInstance() {
		return new IdGroupContainer(m_groupId);
	}
	
	public boolean isComplete() {
		return m_currNumOfAttributes == m_numOfAttributes;
	}
}
