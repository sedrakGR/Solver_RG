package org.ppit.core.concept.expression.node;

import org.ppit.core.brain.instance.nucleus.NucleusInstance;
import org.ppit.util.exception.*;


/**
 * 
 * @author Emil
 * @date 24.11.2011
 */
public class NodeReference extends Node {
	private NucleusInstance m_nucleusInstance = null;
	String m_refConcept = null;
	
	public NodeReference(String refName) {
		m_refConcept = refName;
	}
	
	public void setReference(NucleusInstance primConcept){
		m_nucleusInstance = primConcept;
	}
	
	@Override
	public NodeReference clone () {
		// We don't need to copy any internal state here. 
		// all we need is to create just a new instance of this NodeReference object.
		
		NodeReference clone = new NodeReference(m_refConcept);
		clone.m_nucleusInstance = m_nucleusInstance;
		return clone;
	}
	
	@Override
	public int evaluate() throws UnsolvedDependency {
		if(m_nucleusInstance == null){
			throw new UnsolvedDependency("The " + m_refConcept + " dependency is not solved.");
		}
		return m_nucleusInstance.getValue();
	}

	@Override
	public void extractDepenencies(DependencyContainer container){
		container.addDependent(m_refConcept, this);
	}
}
