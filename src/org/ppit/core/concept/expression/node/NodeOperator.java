package org.ppit.core.concept.expression.node;

import org.ppit.core.concept.expression.node.operators.*;
import org.ppit.util.exception.UnsolvedDependency;


/**
 * 
 * @author Emil
 * @date 24.11.2011
 *
 */
public class NodeOperator extends Node {
	private String m_operator = null;
	private Node m_rightNode = null;
	private Node m_leftNode = null;
	
	/**
	 * 
	 * @param operator Operator
	 * @param rightNode Right Operand
	 * @param leftNode Left Operand
	 */
	public NodeOperator(String operator, Node rightNode, Node leftNode) {
		this.m_operator = operator;
		this.m_rightNode = rightNode;
		this.m_leftNode = leftNode;
	}	
	
	private NodeOperator() {
	}

	@Override
	public NodeOperator clone () {
		NodeOperator clone = new NodeOperator();
		// Operator doesn't need to be copied.
		clone.m_operator = m_operator;
		clone.m_leftNode = m_leftNode.clone();
		clone.m_rightNode = m_rightNode.clone();
		return clone;
	}
	
	/**
	 * Evaluate expression value 
	 * @throws UnsolvedDependency 
	 */
	@Override
	public int evaluate() throws UnsolvedDependency {		
		NodeOperator oper = null;
		if(m_operator.equals("+")){
			oper = new NodePlus("+", m_rightNode, m_leftNode);			
		} else
		if(m_operator.equals("-")){
			oper = new NodeMinus("-", m_rightNode, m_leftNode);			
		} else
		if(m_operator.equals("*")){
			oper = new NodeMult("*", m_rightNode, m_leftNode);			
		} else
		if(m_operator.equals("/")){
			oper = new NodeSep("/", m_rightNode, m_leftNode);			
		}		
		return oper.evaluate();
	}
	
	/**
	 * 
	 * @return right node
	 */
	public Node getRightNode() {
		return m_rightNode;
	}

	public void setRightNode(Node rightNode) {
		this.m_rightNode = rightNode;
	}
	
	/**
	 * 
	 * @return left node
	 */
	public Node getLeftNode() {
		return m_leftNode;
	}

	public void setLeftNode(Node leftNode) {
		this.m_leftNode = leftNode;
	}

	@Override
	public void extractDepenencies(DependencyContainer container){
		m_leftNode.extractDepenencies(container);
		m_rightNode.extractDepenencies(container);
	}
}
