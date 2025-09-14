package org.ppit.core.concept.expression;

import java.util.ArrayList;

import org.ppit.core.brain.instance.nucleus.NucleusInstance;
import org.ppit.core.concept.expression.analyze.*;
import org.ppit.core.concept.expression.node.*;
import org.ppit.util.exception.InvalidExpression;
import org.ppit.util.exception.UnsolvedDependency;

/**
 *  @author Emil
 *  @date 24.11.2011 
 *
 */
public class Expression implements Cloneable {
	private String m_expression;
	private SplitExpression m_se = null;
	private InfixToPostfix m_itp = null;
	
	private ArrayList<String> m_postfixArray = null;
	
	private DependencyContainer m_dependencyMap = null;
	
	private Node m_root = null;
	/**
	 * constructor 
	 * @param expression: infix expression
	 * @throws InvalidExpression 
	 * 
	 */
	public Expression(String expression) throws InvalidExpression {		
		m_expression = expression;
		m_se = new SplitExpression(m_expression);
		m_itp = new InfixToPostfix();
		m_postfixArray = m_itp.doTrans(m_se.separateString());
		m_dependencyMap = new DependencyContainer();
		
		this.createTree();
	}
	
	@Override
	public Expression clone() {
		Expression clone;
		try {
			clone = (Expression)super.clone();
			// The following attributes don't need a copy
			clone.m_expression = m_expression;
			clone.m_se = m_se;
			clone.m_itp = m_itp;
			clone.m_postfixArray = m_postfixArray;

			// First cloning the Node tree, and then updating the dependency map.
			clone.m_root = (Node) m_root.clone();
			clone.reEvaluateDependencies();
		}catch (CloneNotSupportedException e) {
			e.printStackTrace();
			return null;
		}
		return clone;
	}
	
	/**
	 * Create tree for postfix expression, this is invoked in the constructor.
	 * @throws InvalidExpression 
	 * 
	 */	
	public void createTree() throws InvalidExpression{	
		int n = m_postfixArray.size();
		int i = 0;
		String token;
		Node rightChild = null;
		Node leftChild = null;
		java.util.Stack<Node> stack = new java.util.Stack<Node>();
		while(i != n){
			token = m_postfixArray.get(i++);
			
			if(token.equals("+") || token.equals("-") || 
					token.equals("*") || token.equals("/")){
				/*
				 * Creates instance of NodeOperator, 
				 * adds two previous tokens from stack, 
				 * and push it back to stack.
				 */
				leftChild = stack.pop();
				rightChild = stack.pop();
				NodeOperator operator = new NodeOperator(token, rightChild, leftChild);
				stack.push(operator);
			} else
			if(token.indexOf(".") != -1){  
				/*
				 * Creates instance of NodeReference,				  
				 * and push it to stack.
				 */
				NodeReference ref = new NodeReference(token);
				stack.push(ref);
				m_dependencyMap.addDependent(token, ref);
			} else {
				/*
				 * Creates instance of NodeConstant,				  
				 * and push it to stack.
				 */
				NodeConstant constant = new NodeConstant(Integer.parseInt(token));
				stack.push(constant);
			}
		}
		if (stack.size() != 1) {
			throw new InvalidExpression("The given expression is invalid. There are " 
					+ stack.size() + " elements in the final stack when '1' is expected.");
		}
		m_root = stack.pop();
	}
	
	private void reEvaluateDependencies() {
		m_dependencyMap = new DependencyContainer();
		m_root.extractDepenencies(m_dependencyMap);
	}
	
	/**
	 * Evaluate experssion value
	 * @return value of expression
	 * @throws UnsolvedDependency 
	 */
	public int evaluate() throws UnsolvedDependency {
		return m_root.evaluate();
	}
	
	public boolean setDependency(String depNodeName, NucleusInstance refToInstance){
		return m_dependencyMap.setDependency(depNodeName, refToInstance);
	}
	
	/**
	 * 
	 * @return List of dependences in expression, for instance p1.x, p2.x.
	 */
	public ArrayList<String> getDependencies(){
		return m_dependencyMap.getDependencies();
	}
	
	/**
	 * 
	 * @return true if there are dependencies in the expression.
	 */
	public boolean isDependent() {
		return m_dependencyMap.isEmpty() == false;
	}
	
	/**
	 * 
	 * @return Return the expression
	 */
	public String getExprString(){
		return m_expression;
	}
}
