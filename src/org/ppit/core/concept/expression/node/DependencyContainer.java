package org.ppit.core.concept.expression.node;

import java.util.ArrayList;
import java.util.HashMap;

import org.ppit.core.brain.instance.nucleus.NucleusInstance;

public class DependencyContainer {
	private HashMap<String, ArrayList<NodeReference>> m_dependencyMap = null;
	
	public DependencyContainer() {
		m_dependencyMap = new HashMap<String, ArrayList<NodeReference>>();
	}
	
	public void addDependent(String name, NodeReference node){
		ArrayList<NodeReference> nodes = m_dependencyMap.get(name);
		if(nodes == null) {
			nodes = new ArrayList<NodeReference>();
			m_dependencyMap.put(name, nodes);
		}
		nodes.add(node);
	}

	public boolean setDependency(String depNodeName, NucleusInstance refToInstance){
		ArrayList<NodeReference> refNodes = m_dependencyMap.get(depNodeName);
		if (refNodes == null) 
			return false;
		
		for(NodeReference refNode : refNodes) {
			refNode.setReference(refToInstance);
		}
		return true;
	}
	
	public ArrayList<String> getDependencies() {
		return  new ArrayList<String> (m_dependencyMap.keySet());
	}
	
	public boolean isEmpty() {
		return m_dependencyMap.isEmpty();
	}
}
