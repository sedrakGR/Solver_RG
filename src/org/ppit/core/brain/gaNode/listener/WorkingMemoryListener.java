package org.ppit.core.brain.gaNode.listener;


/**
 * 
 * @author Karen
 * @detailed The Working Memory contains the set of active at a time T abstracts.     
 * 			 This class is designed to play a role of WM, it will subscribe to given abstracts
 * 				and will support an interface to get the list of active elements.
 * 
 * To improve processing speed, we add separate listeners to each node, so that to get 
 *    activated instances directly within particular listeners. 
 */


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Set;

import org.ppit.core.brain.gaNode.GANode;
import org.ppit.core.brain.gaNode.abstractNode.GAAbstract;
import org.ppit.core.brain.gaNode.ar1.GAAR1;
import org.ppit.core.brain.instance.InstanceBase;
import org.ppit.core.brain.instance.abstractInstance.AbstractInstance;
import org.ppit.core.brain.instance.action.ActionInstance;
import org.ppit.core.brain.instance.ar1.AR1Instance;
import org.ppit.core.brain.instance.nucleus.IdGroup;
import org.ppit.core.concept.AbstractBase;
import org.ppit.core.concept.Concept;
import org.ppit.core.concept.ConceptManager;
import org.ppit.core.concept.ConceptType;
import org.ppit.core.concept.composite.CompositeConcept;
import org.ppit.util.Definitions;
import org.ppit.util.Logger;
import org.ppit.util.exception.PPITException;
import org.ppit.util.exception.WMException;

public class WorkingMemoryListener {

	private HashMap<GANode, GAListenerCollecter> m_instances = new HashMap<GANode, GAListenerCollecter>();
	private HashMap<String, GANode> m_nameMapping = new HashMap<String, GANode>();
	private HashMap<String, GANode> m_negatedNodes = new HashMap<String, GANode>();
	private ArrayList<GANode> m_processedNegations = new ArrayList<GANode>();

	public void registerTo(String name, GANode source) {
		if(m_nameMapping.put(name, source) != null) {
			Logger.getInstance().log(Logger.ERROR, "Trying to register the same abstract name: " + name + " more than once.");
		}
		if (source.getAbstract().getDetailedType() == ConceptType.COMPOSITE
				|| source.getAbstract().getDetailedType() == ConceptType.AR1
				|| source.getAbstract().getDetailedType() == ConceptType.VIRTUAL
				|| source.getAbstract().getDetailedType() == ConceptType.USAGE) {
			if (((CompositeConcept)source.getAbstract()).isNegated()) {
				m_negatedNodes.put(name, source);
			}
		}
		// It is OK to register more than one name for the same node.
		if(m_instances.containsKey(source) == false) {
			GAListenerCollecter listener = new GAListenerCollecter();
			listener.registerTo(source);
			m_instances.put(source, listener);
		}
	}
	
	public void registerNegation(String name, GANode source) {
		if (source.getAbstract().getDetailedType() == ConceptType.COMPOSITE
				|| source.getAbstract().getDetailedType() == ConceptType.AR1
				|| source.getAbstract().getDetailedType() == ConceptType.VIRTUAL
				|| source.getAbstract().getDetailedType() == ConceptType.USAGE) {
			if (((CompositeConcept)source.getAbstract()).isNegated()) {
				m_negatedNodes.put(name, source);
			}
		}
		// It is OK to register more than one name for the same node.
		if(m_instances.containsKey(source) == false) {
			GAListenerCollecter listener = new GAListenerCollecter();
			listener.registerTo(source);
			m_instances.put(source, listener);
		}
	}

	public void registerTo(GANode source) {
		registerTo(source.getAbstract().getName(), source);
	}
		
	/**
	 * @param name - the name of the abstract
	 * @return the collection of activated instances for the required abstract
	 * @throws WMException, if there is no abstract registered for the given name
	 */
	public GAListenerCollecter getActivatedInstances(String name) throws WMException {
		GANode source = m_nameMapping.get(name);
		if(source == null) {
			throw new WMException("No GANode is registered for the required abstract: " + name);
		}
		return m_instances.get(source);
	}
	
	public boolean hasActivatedInstances(String name) {
		GANode source = m_nameMapping.get(name);
		if(source == null) {
			return false;
		}
		if (!m_instances.containsKey(source)) {
			return false;
		} else if (m_instances.get(source).getActiveInstanceCount() == 0) {
			return false;
		}
		return true;
		
	}
	
	public Set<String> getActivatedAbstractNames() {
		return m_nameMapping.keySet();
	}
	
	public ArrayList<String> getActivatedAbstracts() {
		ArrayList<String> activatedAbstracts = new ArrayList<String>();

		for(Entry<GANode, GAListenerCollecter> e: m_instances.entrySet()) {
			if(e.getValue().getActiveInstanceCount() > 0) {
				// do not show subabstracts (if not zero level then don't show)
				if(e.getKey().getAbstract().CONCEPT_TYPE != ConceptType.PRIMITIVE){
					activatedAbstracts.add(e.getKey().getAbstract().getName());
				}
			}
		}
		return activatedAbstracts;
	}
	
	/**
	 * @param side indicates the side 1 - for white in chess / other games need to put the correspondance, 2 - black, 0 - all
	 * @return Returns the list of instances of actions active in the given situation
	 */
	public ArrayList<ActionInstance> getActiveActions(int side) {
		//TODO: once decided how to perform with side, this needs to be fixed
		ArrayList<ActionInstance> activatedActions = new ArrayList<ActionInstance>();
		for(Entry<GANode, GAListenerCollecter> e: m_instances.entrySet()) {
			if(e.getValue().getActiveInstanceCount() > 0) {
				if(e.getKey().getAbstract().CONCEPT_TYPE == ConceptType.ACTION){
					for (InstanceBase actionInstance : e.getValue().getActiveInstances()) {
						ActionInstance instance = (ActionInstance)actionInstance;
						// if the side is correct then this action is good to be returned
						if (side == 0) {
							activatedActions.add(instance);
						} else if (instance.checkSide(side)) {
							activatedActions.add(instance);
						}
					}
				}
			}
		}
		return activatedActions;
	}
	
	/**
	 * 
	 * @return Returns the list of active instances
	 */
	public ArrayList<InstanceBase> getActiveInstances() {
		ArrayList<InstanceBase> activateInstances = new ArrayList<InstanceBase>();
		for(Entry<GANode, GAListenerCollecter> e: m_instances.entrySet()) {
			if(e.getValue().getActiveInstanceCount() > 0) {
				if(e.getKey().getAbstract().CONCEPT_TYPE != ConceptType.PRIMITIVE){
					activateInstances.addAll(e.getValue().getActiveInstances());
				}
			}
		}
		return activateInstances;
	}
	
	public String getActivatedAbstractsJSON() {
		String json = "{'";
		json += Definitions.situationActiveAbstractsJSON + "':[";
		boolean first = true;
		for(String abst: getActivatedAbstracts()) {
			Concept concept = ConceptManager.getInstance().getConcept(abst);
			if (concept == null || concept.isZeroLevel()) {
				if (first == false) {
					json += ",";
				}
				json += "{'name':'" + abst + "'}";
				first = false;
			}
		}
		json += "]}";
		return json;
	}
	
	public void reset() {
		for(GAListenerCollecter listener: m_instances.values()) {
			listener.unregister();
		}
		m_instances.clear();
		m_nameMapping.clear();
	}
	
	public void postProcess()  throws PPITException {
		boolean isProcessed = false;
		for (GANode node : m_negatedNodes.values()) {
			if (m_processedNegations.contains(node)) {
				continue;
			}
			if (node.getAbstract().CONCEPT_TYPE == ConceptType.COMPOSITE) {
				if (((CompositeConcept)node.getAbstract()).isCR1()) {
					GAAR1 ar1 = (GAAR1)node;
					if (ar1.isActivedAsNegated()) {
						AR1Instance ar1Instance = new AR1Instance(ar1, IdGroup.getEmptyIDGroup(), 0);
						ar1.fireInstance(ar1Instance);
						m_processedNegations.add(node);
						if (!isProcessed) {
							isProcessed = true;
						}
					}
				} else {
					GAAbstract composite = (GAAbstract)node;
					//anyway it should have parent when it is negated
					if (composite.isActivedAsNegated()) {
						AbstractInstance abstractInstance = new AbstractInstance(composite, null, 0);
						composite.fireInstance(abstractInstance);
						m_processedNegations.add(node);
						if (!isProcessed) {
							isProcessed = true;
						}
					}
				}
			} else if (node.getAbstract().CONCEPT_TYPE == ConceptType.AR1) {
				//TODO: should never get here actually
				throw new PPITException("node abstract has type AR1");
			} else {
				throw new PPITException("negated node with different than composite or ar1 type");
			}
		}
		if (isProcessed) {
			postProcess();
		}
	}
	
	public void clearNegationCaches() {
		m_processedNegations.clear();
	}
}
