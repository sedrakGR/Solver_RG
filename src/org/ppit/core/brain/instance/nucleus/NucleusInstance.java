package org.ppit.core.brain.instance.nucleus;

import org.ppit.core.brain.gaNode.GANode;
import org.ppit.core.brain.instance.IdGroupContainer;
import org.ppit.core.brain.instance.InstanceBase;
import org.ppit.core.concept.primitive.PrimitiveType;
import org.ppit.util.exception.BrokenCR1;

/**
 * 
 * @author SedrakG
 * @brief an instance of a Nucleus concept 
 */
public class NucleusInstance extends InstanceBase {
	/**
	 * @brief the Primitive Type to which this instance belongs
	 */
	private PrimitiveType m_type = null;
	
	/**
	 * the value of the instance
	 */
	private int m_value = 0;
		
	/**
	 * @brief IdGroup, which describes the group of primitive instances
	 * if some instances have the same id group, that mean they formulate an AR1 (see definition of AR1/CR1 in docs)
	 */
	private IdGroup m_idGroup = null;
	
	/*
	 * The instance contains the value, the type the group id and the reference to the node
	 * which created it.
	 */
	public NucleusInstance(int value, PrimitiveType type, IdGroup idGroup, GANode node) throws BrokenCR1 {
		super(node);
		
		m_value = value;
		m_type = type;
		m_idGroup = idGroup;
		
		//m_idGroup.addPElement(m_type, this);
		m_idGroup.forceAddPElement(m_type, this);
	}
	
	/**
	 * If the path is empty or is "v" <= referenced as value attribute 
	 * then return the self instance.
	 */
	public InstanceBase getInstance(String path)
	{
		if(path.equals("") || path.equalsIgnoreCase("v")) {
			return this;
		}
		return null;
	}
	
	public PrimitiveType getType() {
		return m_type;
	}
	
	public int getValue() {
		return m_value;
	}
	
	public void setValue(int value) {
		m_value = value;
	}
	
	public IdGroup getIdGroup() {
		return m_idGroup;
	}

	@Override
	public IdGroupContainer getIdGroupsInInstance() {
		return new IdGroupContainer(m_idGroup);
	}
}
