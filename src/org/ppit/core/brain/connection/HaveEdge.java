package org.ppit.core.brain.connection;

import org.ppit.core.brain.GAEdge;
import org.ppit.core.brain.gaNode.GANode;

/**
 * 
 * @author Karen
 * @detailed Modeling Have connection.
 *  
 */

public class HaveEdge extends GAEdge {

	private String m_slaveName = null;
	
	public HaveEdge(GANode master, GANode slave, String slaveName) {
		super(master, slave);
		m_slaveName = slaveName;
	}

	public String getAttributeName() {
		return m_slaveName;
	}
	
	@Override
	/**
	 * The edge is added to Master as a "Element" and to the Slave as a "Composer".
	 */
	public void buildConnection() {
		getMaster().addConnectionElement(this);
		getSlave().addConnectionComposer(this);
	}

	@Override
	public EdgeType getEdgeType() {
		return EdgeType.HAVE_EDGE;
	}
}
