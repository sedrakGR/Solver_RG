package org.ppit.core.concept.rules;

import java.util.ArrayList;

import org.ppit.core.brain.instance.nucleus.NucleusInstance;
import org.ppit.core.concept.expression.Expression;
import org.ppit.util.Definitions;
import org.ppit.util.exception.InvalidExpression;
import org.ppit.util.exception.UnsolvedDependency;

/**
 * 
 * @author SedrakG
 * @description this is RuleLE class that describes rule Less than or equals to ("<=")
 *
 */
public class RuleLE extends IRule {
	private Expression m_expression = null;
	/**
	 * @brief Constructor
	 * @param ruleExpression String expression of the Rule Less than or equals to
	 * @throws InvalidExpression 
	 */
	public RuleLE(String ruleExpression) throws InvalidExpression
	{
		m_expression = new Expression(ruleExpression);
	}

	private RuleLE() {
	}
	@Override
	public RuleLE clone() {
		RuleLE clone = new RuleLE();
		clone.m_expression = m_expression.clone();
		return clone;
	}
	@Override
	/**
	 * @ setDependency sets a reference to the dependent node
	 * @param String depNodeName, the name of dependent node in expression
	 * @param Expression refToExpr, keeps a reference to expression of the dependent node
	 * @return Returns true if dependency was set and false if not 
	 */
	public boolean setDependency(String depNodeName, NucleusInstance refToInstance) {
		return m_expression.setDependency(depNodeName, refToInstance);
	}
	@Override
	/**
	 * @ getDependencies, retrieves the list of dependent node names
	 * @return returns dependent node names in ArrayList<String> 
	 */
	public ArrayList<String> getDependencies() {
		return m_expression.getDependencies();
	}
	@Override
	/**
	 * @ check, checks whether the value is allowed for the rule 
	 * @param int value, value to be checked
	 * @return returns true if the value is allowed in the rule, and false in the other case
	 */
	public boolean check(int value) throws UnsolvedDependency {
		return value <= m_expression.evaluate();
	}
	
	@Override
	/**
	 * @brief returns the operator type
	 */
	public String getOperator()
	{
		return Definitions.lessEqualsOperator;
	}
	
	/**
	 * @brief returns the expression of the rule
	 */
	public String getExpressionString()
	{
		return m_expression.getExprString();
	}

	/**
	 * @brief NNTF: new not tested and final
	 */
	public ArrayList<Integer> getValues()
	{
		ArrayList<Integer> values = new ArrayList<Integer>();
		
		try {
			values.add(m_expression.evaluate());
		} catch (UnsolvedDependency e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return values;
	}

	/**
	 * @brief NNTF: new not tested and final
	 */
	public ArrayList<IRule> conjunction(IRule rule) {
		IRule rule0 = null;
		try {
			rule0 = new RuleL((this.getValues().get(0) + 1) + "");
		} catch (InvalidExpression e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return rule0.conjunction(rule);
	}
}
