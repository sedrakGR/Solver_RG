package org.ppit.core.brain.connection;

import org.ppit.core.brain.GAEdge;
import org.ppit.core.brain.gaNode.GANode;


/**
 * 
 * @author Karen
 * @detailed Modeling Be connection.
 *  
 */

public class BeEdge extends GAEdge {

	public BeEdge(GANode master, GANode slave) {
		super(master, slave);
	}

	@Override
	/**
	 * The edge is added to Master as a "Child" and to the Slave as a "Parent".
	 */
	public void buildConnection() {
		getMaster().addConnectionChild(this);
		getSlave().addConnectionParent(this);
	}

	@Override
	public EdgeType getEdgeType() {
		return EdgeType.BE_EDGE;
	}
}
