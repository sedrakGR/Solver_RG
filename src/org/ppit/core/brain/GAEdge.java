package org.ppit.core.brain;

import org.ppit.core.brain.connection.EdgeType;
import org.ppit.core.brain.gaNode.GANode;

/**
 * 
 * @author Karen
 * @detailed The Base class for Connections between GANodes
 *  We follow to the following definition    
 *  	Be  	= Parent - is master, Child is slave
 *  	Have 	= Composer  - is master, Have is slave
 *  	Do 		= Performer - is master, Action is slave
 *  
 */

public abstract class GAEdge {
	private GANode m_master = null;
	private GANode m_slave = null;
	
	/**
	 * @brief indicates if the node is already visited by some algorithm.
	 * 		  this is designed to be used by visualization tools, but can be used
	 * 		  for the arbitrary algorithm. However, one shall keep in mind to reset the valus 
	 * 		  of all nodes before running the algorithm.
	 */
	private Object m_isVisited = null;

	public GAEdge(GANode master, GANode slave) {
		m_master = master;
		m_slave = slave;
		buildConnection();
	}
	
	public GANode getMaster(){
		return m_master;
	}
	
	public GANode getSlave() {
		return m_slave;
	}
	
	public abstract void buildConnection();
	
	public abstract EdgeType getEdgeType();
	
	public void setVisited(Object visited) {
		m_isVisited = visited;
	}
	
	public boolean isVisited() {
		return m_isVisited != null;
	}
	
	public Object getVisitor() {
		return m_isVisited;
	}
	
	public static final int BE_CONNECTION = 1;
	public static final int HAVE_CONNECTION = 2;
	public static final int DO_CONNECTION = 3;
}
