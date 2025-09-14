package org.ppit.core.brain.instance.abstractInstance;

import java.util.HashMap;

import org.ppit.core.brain.gaNode.abstractNode.GAAbstract;
import org.ppit.core.brain.instance.IdGroupContainer;
import org.ppit.core.brain.instance.InstanceBase;
import org.ppit.core.concept.rules.Rule;
import org.ppit.core.concept.rules.RuleGroup;
import org.ppit.util.Definitions;
import org.ppit.util.exception.GAException;

/**
 * 
 * @author SedrakG
 * @brief This describes the instance of Abstract Node of GA
 */
public class AbstractInstance extends InstanceBase{
	
	/**
	 * @brief Attribute instances of abstract (sub attributes).
	 * HashMap<String: attribute_name, InstanceBase: value> m_attributes;
	 */
	private HashMap<String, InstanceBase> m_attributes = null;
	
	/**
	 * @brief The Rules of the Abstract.
	 */
	RuleGroup m_rules = null;
	
	/**
	 * @brief the number of attributes in the Abstract type.
	 */
	int m_numOfAttributes = 0;
	
	/**
	 * The number of "set" attributes.
	 */
	int m_currNumOfAttributes = 0;
	
	// NOTE: currently this does not represent the real status, it just indicates if the status is FIRE or not
	int m_status =  A_FAIL;

	
	public AbstractInstance(GAAbstract type, RuleGroup rules, int numOfAttributes) {
		super(type);
		m_rules = rules;
		m_attributes = new HashMap<String, InstanceBase> ();
		m_numOfAttributes = numOfAttributes;
		m_currNumOfAttributes = 0;
	}
	
	public AbstractInstance clone()
	{
		AbstractInstance clone;
		clone = (AbstractInstance)super.clone();
		
		// TODO add the holder class to attributes (InstanceBase).
		// Don't forget to remove the holder later, when destroying the instance.
		
		clone.m_attributes = new HashMap<String, InstanceBase> ();
		clone.m_attributes.putAll(m_attributes);
		
		if(m_rules != null) {
			clone.m_rules = m_rules.clone();
		}
		clone.m_numOfAttributes = m_numOfAttributes;
		clone.m_currNumOfAttributes = m_currNumOfAttributes;
		
		return clone;
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
	 * Returns the Instance of the element.
	 * @param name - the relative name of the element.
	 */
	public InstanceBase getElement(String name) {
		return m_attributes.get(name);
	}
	
	/**
	 * Set the new attribute. This shall be set also in the Rule if there is a dependency.
	 * @param name - the name of the attribute (this is the relative name)
	 * @param instance - the instance of the attribute.
	 * @throws GAException 
	 */
	public int activateElement(String name, InstanceBase instance) throws GAException {
		// TODO check the handling in RuleGroup. Maybe we shall not allow setting 
		//      twice for the same attribute (logically we assume that it doesn't happen).
		if(m_attributes.put(name, instance) == null) {
			// If this element wasn't set before.
			++m_currNumOfAttributes;
		}
		int ruleGroupState = Rule.R_OK;
		if(m_rules != null) {
			ruleGroupState = m_rules.setAndCheckDependency(name, this);
		}
		switch (ruleGroupState) {
		case Rule.R_OK: {
			if( m_currNumOfAttributes == m_numOfAttributes) {
				m_status = A_FIRE;
				return A_FIRE;
			} else {
				return A_A_PENDING;
			}
		}
		case Rule.R_FAIL: {
			return A_FAIL;
		}
		case Rule.R_PENDING: {
			// If Rules are still pending and all attributes are set then something is wrong with this
			// abstract. 
			// TODO maybe we need an exception in this case.
			if (m_currNumOfAttributes == m_numOfAttributes) {
				return A_FAIL;
			} else {
				return A_R_PENDING;
			}
		}
		default: {
			throw new GAException("Incorrect RuleGroup state: " + ruleGroupState + " has been returned");
		} }
	}	
	
	public static final int A_FIRE = 1;	// The Instance is ready.
	public static final int A_FAIL = 2;	// The Instance is invalid.
	public static final int A_R_PENDING = 3; // There is not satisfied Rule.
	public static final int A_A_PENDING = 4; // Rules are satisfied, though, some attributes are missing.


	@Override
	public IdGroupContainer getIdGroupsInInstance() {
		IdGroupContainer idGroups = new IdGroupContainer(); 
		for (InstanceBase attr: m_attributes.values()) {
			idGroups.addIdGroup(attr.getIdGroupsInInstance());
		}
		return idGroups;
	}
	
	public boolean isActive() {
		return m_status == A_FIRE;
	}
}
