package org.ppit.core.concept.action;

import java.util.ArrayList;

import org.ppit.core.brain.instance.nucleus.NucleusInstance;
import org.ppit.core.concept.expression.Expression;
import org.ppit.util.exception.InvalidExpression;
import org.ppit.util.exception.UnsolvedDependency;

/**
 * @author SedrakG
 * @brief This is the post condition of the action
 * which contains name of the referenced attribute and expression
 */
public class PostConditionAttribute {
	/**
	 * @brief m_name Name of the attribute (i's concept name)
	 * @note The name always represents a name of concept, to which the expression should be applied
	 */
	private String m_name = null;
	/**
	 * @brief m_expression
	 */
	private Expression m_expression = null;
	
	/**
	 * @Constructor
	 * @param name Name of the attribute concept
	 * @param expressionString Expression to be applied on the attribute concept
	 * @throws InvalidExpression 
	 */
	public PostConditionAttribute(String name, String expressionString) throws InvalidExpression
	{
		m_name = name;
		m_expression = new Expression(expressionString);
	}
	
	/**
	 * @return Returns the name of the attribute concept
	 */
	public String getAttributeName()
	{
		return m_name;
	}
	
	/**
	 * @return Returns the String of expression
	 */
	public String getExpression()
	{
		return m_expression.getExprString();
	}
	
	/**
	 * 
	 * @param depNodeName, Name of the dependent Node
	 * @param primitiveConcept, dependent node concept
	 * @return Returns true if dependency is set, and false in other case
	 */
	public boolean setDependency(String depNodeName, NucleusInstance nucleusInstance)
	{
		return m_expression.setDependency(depNodeName, nucleusInstance);
	}
	
	/**
	 * @return Returns the list of dependent node names
	 */
	public ArrayList<String> getDependencies()
	{
		return m_expression.getDependencies();
	}
	
	/**
	 * @brief Evaluate expression value
	 * @return value of expression
	 * @throws UnsolvedDependency 
	 */
	public int evaluate() throws UnsolvedDependency {
		return m_expression.evaluate();
	}
	
}
