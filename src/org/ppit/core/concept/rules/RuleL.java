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
 * @description this is RuleL class that describes rule less ("<")
 *
 */
public class RuleL extends IRule {
	private Expression m_expression = null;
	/**
	 * @brief Constructor
	 * @param ruleExpression String expression of the Rule Less
	 * @throws InvalidExpression 
	 */
	public RuleL(String ruleExpression) throws InvalidExpression
	{
		m_expression = new Expression(ruleExpression);
	}
	
	private RuleL() {
	}

	@Override
	public RuleL clone() {
		RuleL clone = new RuleL();
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
		return value < m_expression.evaluate();
	}
	
	@Override
	/**
	 * @brief returns the operator type
	 */
	public String getOperator()
	{
		return Definitions.lessOperator;
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
		ArrayList<IRule> rg = new ArrayList<IRule>();
		int lower;
		
		try {		
			if(rule.getOperator() == "IN") {
				rg = rule.conjunction(this);
			}
			else if(rule.getOperator() == "=") {
				rg = rule.conjunction(this);
			}
			else if(rule.getOperator() == "!=") {
				rg = rule.conjunction(this);
			}
			else if(rule.getOperator() == ">=") {
				rg = rule.conjunction(this);
			}
			else if(rule.getOperator() == ">") {
				rg = rule.conjunction(this);
			}
			else if(rule.getOperator() == "<") {
				lower = this.getValues().get(0) < rule.getValues().get(0) ? this.getValues().get(0) : rule.getValues().get(0);
				rule0 = new RuleL(lower + "");
				rg.add(rule0);
			}
			else if(rule.getOperator() == "<=") {
				IRule rule1 = new RuleL((rule.getValues().get(0) + 1) + "");
				rg = this.conjunction(rule1);
			}
			
		} catch (InvalidExpression e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return rg;
	}
}