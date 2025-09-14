package org.ppit.core.concept.rules;

import java.util.ArrayList;

import org.ppit.core.brain.instance.InstanceBase;
import org.ppit.core.brain.instance.nucleus.NucleusInstance;
import org.ppit.util.Definitions;

public abstract class BaseRule implements Cloneable {
	
	// The name of the Left attribute.
	private String m_prefix = null;
	
	BaseRule(String leftName) {
		m_prefix = leftName;
	}
	
	public BaseRule clone() {
		try {
			BaseRule clone = (BaseRule)super.clone();
			clone.m_prefix = new String(m_prefix);
			
			return clone;
		}catch (CloneNotSupportedException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/*
	 * When traveling through the hierarchy of concepts the name of the left side
	 * shall be prepended so, that it will be "x", "a.x", "b.a.x" and so forth.
	 */
	public void addPrefix(String holderName) {
		if(m_prefix.isEmpty() == false)
			m_prefix = holderName + Definitions.subattributeAccessor + m_prefix;
		else
			m_prefix = holderName;
	}
	
	public void removePrefix() {
		if(!m_prefix.isEmpty()) {
			int rootEnd = m_prefix.indexOf(Definitions.subattributeAccessor);
			if (rootEnd != -1) {
				m_prefix = m_prefix.substring(rootEnd + 1);
			}
		}
	}
	
	public String getPrefix() {
		return m_prefix;
	}
	
	public abstract int setAndCheckDependency(String name, InstanceBase instance);
	public abstract ArrayList<String> getDependencies();
	public abstract void setDependency(String depNodeName, NucleusInstance refToInstance);
	
	public static final int R_OK = 1;	// The Rule is satisfied.
	public static final int R_FAIL = 2;	// Attributes are set but they don't support the rules.
	public static final int R_L_PENDING = 3; // Left attribute is pending
	public static final int R_R_PENDING = 4; // Right expression is pending
	public static final int R_PENDING = 5; // Rule is not complete (unclear which side is pending)
}
