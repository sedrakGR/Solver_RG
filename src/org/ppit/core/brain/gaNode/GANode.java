package org.ppit.core.brain.gaNode;

import java.util.HashSet;

import org.ppit.core.brain.GAEdge;
import org.ppit.core.brain.connection.*;
import org.ppit.core.brain.gaNode.action.GAAction;
import org.ppit.core.brain.gaNode.listener.GAListener;
import org.ppit.core.brain.instance.InstanceBase;
import org.ppit.core.concept.AbstractBase;
import org.ppit.core.concept.ConceptType;
import org.ppit.util.exception.GAException;

/**
 * 
 * @author Karen
 * @detailed The Node in the Graph of Abstracts. This serves as a base    
 * 			 	class both for GANucleuses, GAAR1s, GAAbstracts, GASets, GAActions.
 *			 It holds all "Be" "Have" and "Do" connections.
 * 	We define separate containers for each kind of connection.
 *  
 */
public abstract class GANode {
	
	/**
	 * @brief The abstract to which this GA Node refers
	 */
	private AbstractBase m_abstract = null;
	
	/**
	 * @brief indicates if the node is already visited by some algorithm.
	 * 		  this is designed to be used by visualization tools, but can be used
	 * 		  for the arbitrary algorithm. However, one shall keep in mind to reset the valus 
	 * 		  of all nodes before running the algorithm.
	 */
	private Object m_isVisited = null;
	
	/**
	 * Constructor
	 * @param source, the source Abstract of the node
	 */
	public GANode(AbstractBase source) {
		m_abstract = source;
	}
	
	public AbstractBase getAbstract() {
		return m_abstract;
	}
	
	/**
	 *  The list of handlers which are registered to be notified about
	 *  the fired events. 
	 */
	protected HashSet<GAListener> m_externalListeners = new HashSet<GAListener>();
	
	/**
	 * All these are HashSets to avoid possible "double" registration.
	 */
	private HashSet<GAEdge> m_children = new HashSet<GAEdge>();
	private HashSet<GAEdge> m_parents = new HashSet<GAEdge>();
	private HashSet<GAEdge> m_composers = new HashSet<GAEdge>();
	private HashSet<GAEdge> m_elements = new HashSet<GAEdge>();
	private HashSet<GAEdge> m_performers = new HashSet<GAEdge>();
	private HashSet<GAEdge> m_actions = new HashSet<GAEdge>();
	
	/**
	 * for do - A.create(B) -> B does A
	 * @return Returns true if connection is created and false if not
	 */
	public void createDoConnection(GANode preconditionNode) throws GAException {
		if (m_abstract.CONCEPT_TYPE != ConceptType.ACTION) {
			throw new GAException("could not create DO connection");
		} else {
			DoEdge edge = new DoEdge(preconditionNode, this);
			edge.buildConnection();
		}
	}
	
	/**
	 * for be - A.create(B) -> A is B
	 * @return Returns true if connection is created and false if not
	 */
	public void createBeConnection(GANode parentNode) throws GAException {
		if (m_abstract.CONCEPT_TYPE != parentNode.getAbstract().CONCEPT_TYPE) {
			throw new GAException("could not create BE connection");
		} else {
			BeEdge edge = new BeEdge(parentNode, this);
			edge.buildConnection();
		}	
	}

	/**
	 * for have - A.create(B) -> A has B
	 * @return Returns true if connection is created and false if not
	 */
	public void createHaveConnection(String attributeName, GANode attributeNode) {
		HaveEdge edge = new HaveEdge(this, attributeNode, attributeName);
		edge.buildConnection();
	}
	
	/**
	 * The output nodes of the Graph of Abstracts 
	 * (Working Memory as well as Goal checking can be modeled with this).
	 * @param listener
	 */
	public void registerListener(GAListener listener) {
		m_externalListeners.add(listener);
	}
	public void reomveListener(GAListener listener) {
		m_externalListeners.remove(listener);
	}
	
	public void addConnectionChild(GAEdge child) {
		m_children.add(child);
	}
	
	public void addConnectionParent(GAEdge parent) {
		m_parents.add(parent);
	}
	
	public void addConnectionComposer(GAEdge composer) {
		m_composers.add(composer);
	}
	
	public void addConnectionElement(GAEdge element) {
		m_elements.add(element);
	}

	public void addConnectionPerformer(GAEdge performer) {
		m_performers.add(performer);
	}
	
	public void addConnectionAction (GAEdge action) {
		m_actions.add(action);
	}
	
	/**
	 * 
	 * @return Children of the GA Node
	 * e.g. A node is the parent of B, then B is retrieved
	 */
	public HashSet<GAEdge> getChildren() {
		return m_children;
	}
	
	/**
	 * 
	 * @return Elements of the GA Node
	 * e.g. A node contains B node as attribute, then B is retrieved
	 */
	public HashSet<GAEdge> getElements() {
		return m_elements;
	}
	
	/**
	 * 
	 * @return Actions of the GA Node
	 * e.g. A node does B node (Action), then B is retrieved
	 */
	public HashSet<GAEdge> getActions() {
		return m_actions;
	}
	
	/**
	 * 
	 * @return Container nodes of the GA Node
	 * e.g. A node contains B node as attribute, then A is retrieved
	 */
	public HashSet<GAEdge> getComposers() {
		return m_composers;
	}
	
	/**
	 * 
	 * @return Parent nodes of the GA Node
	 * e.g. A node is the parent of B, then A is retrieved
	 */
	public HashSet<GAEdge> getParents() {
		return m_parents;
	}
	
	/**
	 * 
	 * @return Performer nodes of the GA Node
	 * e.g. A node does B node (Action), then A is retrieved
	 */
	public HashSet<GAEdge> getPerformers() {
		return m_performers;
	}
	
	/**
	 * Fires the instance through the graph. Basically the "composers" and "parents" are getting
	 * activated. The activation of "Parent" connections is for supporting "virtual abstracts".
	 * @throws GAException 
	 */
	public void fireInstance(InstanceBase instance) throws GAException {
		if (instance != null && instance.getGANode() != null && (instance.getGANode().getAbstract().getName().equals("kingCannotMove") || getAbstract().getName().equals("Check"))) {
			int i = 1;
			int j = i;
		}
		for(GAListener listener : m_externalListeners){
			listener.handleInstanceActivated(instance);
		}
		for(GAEdge edge : m_parents) {
			edge.getMaster().handleChildActivated(instance);
		}
		for(GAEdge edge : m_composers) {
			edge.getMaster().handleElementActivated(((HaveEdge)edge).getAttributeName(), instance);
		}
		for(GAEdge edge : m_actions) {
			((GAAction)edge.getSlave()).handleActorActivated(instance);
		}
		
		for (GAEdge edge : m_children) {
			edge.getSlave().handleParentActivated(instance);
		}
	}
	
	/**
	 * Each (abstract) node shall implement the handling of its element activation. 
	 * @param name - the name of the element (as it appears in the composer - real name not the db name)
	 * @param instance - the instance of the element
	 * @throws GAException 
	 */
	public abstract void handleElementActivated(String name, InstanceBase instance) throws GAException;
	
	public abstract void handleParentActivated(InstanceBase instance);
	
	/**
	 * Each virtual abstract as well as usual abstract shall handle the activation of its
	 * child abstracts. This is particularly important for "Nucleus" abstracts and
	 * for the "Virtual" abstracts. In the case of the second this means that actually a
	 * defined instance has been activated and consequently either existing rules shall be checed
	 * or the instance shall be fired further.
	 * @param instance - the instance of the activated attribute. 
	 * @throws GAException 
	 */
	public abstract void handleChildActivated(InstanceBase instance) throws GAException;
	
	/**
	 * Abstracts might have collected local partial matches. This function shall reset all partial matches.
	 */
	public abstract void handleCleanCaches();
	
	/**
	 * The generic forwarding for clean cache call. It notifies all parents (in case if they are virtual they need this call), 
	 * all composers as well as all listeners to reset their current caches.
	 */
	public void cleanCaches() {
		for(GAListener listener : m_externalListeners){
			listener.handleCleanCaches();
		}
		for(GAEdge edge : m_parents) {
			edge.getMaster().handleCleanCaches();
		}
		for(GAEdge edge : m_composers) {
			edge.getMaster().handleCleanCaches();
		}
	}
	
	public void setVisited(Object visited) {
		m_isVisited = visited;
	}
	
	public boolean isVisited() {
		return m_isVisited != null;
	}
	
	public Object getVisitor() {
		return m_isVisited;
	}
}
