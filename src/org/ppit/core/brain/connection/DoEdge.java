package org.ppit.core.brain.connection;

import org.ppit.core.brain.GAEdge;
import org.ppit.core.brain.gaNode.GANode;

/**
 * 
 * @author Karen
 * @detailed Modeling Do connection.
 *  
 */

public class DoEdge extends GAEdge {

	public DoEdge(GANode master, GANode slave) {
		super(master, slave);
	}

	@Override
	/**
	 * The edge is added to Master as a "Action" and to the Slave as a "Performer".
	 */
	public void buildConnection() {
		getMaster().addConnectionAction(this);
		getSlave().addConnectionPerformer(this);
	}

	@Override
	public EdgeType getEdgeType() {
		return EdgeType.DO_EDGE;
	}
}
