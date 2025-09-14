package org.ppit.core.percept;

import java.util.HashMap;

/**
 * 
 * @author Karen Khachatryan S.
 * @detailed The Container which holds all situations.
 *
 */

public class SituationLibrary {
	/**
	 * @brief The list of situations.
	 */
	private HashMap<String, Situation> m_situations = new HashMap<String, Situation>();
	
	/**
	 * @param adds the given situation to the situations.
	 */
	public void addSituation(Situation situation) {
		m_situations.put(situation.getName(), situation);
	}
	
	/**
	 * @return the situation which has the specified name, 
	 */
	public Situation getSituation(String name) {
		return m_situations.get(name);
	}
	
	public void reset() {
		m_situations.clear();
	}
}
