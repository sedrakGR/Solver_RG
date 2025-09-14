package org.ppit.core.concept.action;

import java.util.Collection;
import java.util.HashMap;
import java.util.Set;

import org.ppit.core.concept.ConceptManager;
import org.ppit.util.exception.PPITException;

/**
 * @author SedrakG
 * @brief The Container which holds and manages actions
 */
public class ActionLibrary {
	/**
	 * @brief The list of actions.
	 */
	private HashMap<String, Action> m_actions = new HashMap<String, Action>();
	
	/**
	 * @param adds the given composite concept to the composite concepts.
	 */
	public void addAction(Action action) {
		m_actions.put(action.getName(), action);
	}
	
	/**
	 * @return the action which has the specified name, 
	 * if action is missing then null is returned
	 */
	public Action getAction(String name) {
		return m_actions.get(name);
	}
	
	/**
	 * @param remove the specified action
	 * @return Action, if action is found and removed, null otherwise.
	 * @throws PPITException 
	 */
	public Action removeAction(String name) throws PPITException {
		Action action = m_actions.remove(name);
		if(action != null) {
			// remove action's composite attribute as well
			//TODO: do we need to remove it?
			ConceptManager.getInstance().removeConcept(action.getPreConditionConcept().getName());
		}
		return action;
	}
	
	/**
	 * @return the list of all Action names.
	 */
	public Set<String> getListOfActionNames() {
		return m_actions.keySet();
	}
	
	/**
	 * @return the list of all Actions.
	 */
	public Collection<Action> getListOfActions() {
		return m_actions.values();
	}
	
	public void reset() {
		m_actions.clear();
	}
}
