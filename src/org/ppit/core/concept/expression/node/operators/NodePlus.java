package org.ppit.core.concept.expression.node.operators;

import org.ppit.core.concept.expression.node.Node;
import org.ppit.core.concept.expression.node.NodeOperator;
import org.ppit.util.exception.UnsolvedDependency;



/**
 *  @author Emil
 *  @date 24.11.2011 
 *
 */
public class NodePlus extends NodeOperator{

	
	public NodePlus(String operator, Node rightNode, Node leftNode) {
		super(operator, rightNode, leftNode);
	}
	
	
	public int evaluate() throws UnsolvedDependency{
		int rightValue = super.getRightNode().evaluate();
		int leftValue = super.getLeftNode().evaluate();
		int result = rightValue + leftValue;
		return result; 
	}
}
