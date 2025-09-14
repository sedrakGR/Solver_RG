package org.ppit.core.concept.rules;

import java.util.ArrayList;
import java.util.HashMap;

import org.ppit.core.brain.instance.nucleus.NucleusInstance;
import org.ppit.core.concept.expression.Expression;
import org.ppit.util.Definitions;
import org.ppit.util.ParseUtil;
import org.ppit.util.exception.InvalidExpression;
import org.ppit.util.exception.UnsolvedDependency;

/**
 * 
 * @author SedrakG
 * @description this is RuleNE class that describes rule Not equals to ("!=")
 *
 */
public class RuleNE extends IRule {
	/**
	 * @brief This is an array to keep expressions
	 */
	private ArrayList<Expression> m_expressions = null;
	/**
	 * @brief This rule needs to keep a map of dependencies to make their setting easier
	 * key is dependency name, value is list of dependencies
	 */
	private HashMap<String, ArrayList<Expression>> m_dependencyMap;
	/**
	 * @brief Constructor
	 * @param expression String expression of the Rule Not equals
	 * @throws InvalidExpression 
	 */
	public RuleNE(String expression) throws InvalidExpression
	{
		ArrayList<String> ruleExpressions = ParseUtil.splitExpression(expression);
		m_expressions = new ArrayList<Expression>();
		for (String ex : ruleExpressions) {
			m_expressions.add(new Expression(ex));
		}
		this.evaluateDependencies();
	}
	
	private RuleNE() {
	}
	
	@Override
	public RuleNE clone() {
		RuleNE clone = new RuleNE();
		clone.m_expressions = new ArrayList<Expression>();
		for(Expression expr : m_expressions) {
			clone.m_expressions.add(expr.clone());
		}
		// Clone the expressions and then evaluate dependencies.
		clone.evaluateDependencies();
		return clone;
	}
	
	@Override
	/**
	 * @ setDependency sets a reference to the dependent node
	 * @param String depNodeName, the name of dependent node in expression
	 * @param Expression refToExpr, keeps a reference to expression of the dependent node
	 * @return Returns true if dependency was set and false if not 
	 */
	public boolean setDependency(String depNodeName, NucleusInstance refToInstance)
	{
		boolean isSet = true;
		if(m_dependencyMap == null) 
			return false;

		for (Expression expression : m_dependencyMap.get(depNodeName))
		{
			// isSet is true when all dependencies are set
			// even if only one is false then it should be false
			isSet = expression.setDependency(depNodeName, refToInstance) ? isSet : false;
		}
		return isSet;
	}
	
	public void evaluateDependencies() {
		m_dependencyMap = new HashMap<String, ArrayList<Expression>>();
		for (Expression expression : m_expressions) {
			for (String dependence : expression.getDependencies()) {
				if (m_dependencyMap.get(dependence) == null) {
					m_dependencyMap.put(dependence, new ArrayList<Expression>());
				}
				m_dependencyMap.get(dependence).add(expression);
			}
		}
	}
	
	@Override
	/**
	 * @ getDependencies, retrieves the list of dependent node names
	 * @return returns dependent node names in ArrayList<String> 
	 */
	public ArrayList<String> getDependencies()
	{
		ArrayList<String> dependencyArray = new ArrayList<String>();
		if (m_dependencyMap == null) 
			evaluateDependencies();
		
		for (Expression expression : m_expressions) {
			dependencyArray.addAll(expression.getDependencies());
		}
		return dependencyArray;
	}
	
	@Override
	/**
	 * @ check, checks whether the value is allowed for the rule 
	 * @param int value, value to be checked
	 * @return returns true if the value is allowed in the rule, and false in the other case
	 */
	public boolean check(int value) throws UnsolvedDependency
	{
		for (Expression expression : m_expressions)
		{
			if (value == expression.evaluate())
			{
				return false;
			}
		}
		return true;
		
	}
	
	@Override
	/**
	 * @brief returns the operator type
	 */
	public String getOperator()
	{
		return Definitions.notEqualsOperator;
	}
	
	/**
	 * @brief returns the expression of the rule
	 */
	public String getExpressionString()
	{
		ArrayList<String> exprList = new ArrayList<String>();
		for (Expression ex : m_expressions)	{
			exprList.add(ex.getExprString());
		}
		return ParseUtil.composeExpression(exprList);
	}
	
	/**
	 * @brief NNTF: new not tested and final
	 */
	public ArrayList<Integer> getValues()
	{
		ArrayList<Integer> values = new ArrayList<Integer>();
		
		try {
			for (Expression expression : m_expressions)
				values.add(expression.evaluate());
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
		String str;
		boolean found;
		int lower, upper;
		
		try {		
			if(rule.getOperator() == "IN") {
				rg = rule.conjunction(this);
			}
			else if(rule.getOperator() == "=") {
				rg = rule.conjunction(this);
			}
			else if(rule.getOperator() == "!=") {										//need some changes
				str = "";
				for(int i: this.getValues()) {
					found = false;
					for(int j: rule.getValues()) {
						if(i == j) {
							found = true;
							break;
						}
					}
					if(!found) {
						if(!str.isEmpty())
							str += ", ";
						str += i;
					}
				}
				for(int j: rule.getValues()) {
					if(!str.isEmpty())
						str += ", ";
					str += j;
				}
				if(!str.isEmpty()) {
					rule0 = new RuleNE(str);
					rg.add(rule0);
				}
			}
			else if(rule.getOperator() == ">=") {
				str = "";
				upper = rule.getValues().get(0);
				for(int i: this.getValues()) {
					if(i >= upper) {
						lower = upper;
						upper = i;
						if(upper == lower) {
							upper++;
							continue;
						}
						else if(upper == lower+1)
							rule0 = new RuleE(lower + "");
						else
							rule0 = new RuleIn(lower + ", " + (upper-1));
						upper++;
						rg.add(rule0);
					}
				}
				rule0 = new RuleGE(upper + "");
				rg.add(rule0);
			}
			else if(rule.getOperator() == ">") {
				IRule rule1 = new RuleGE((rule.getValues().get(0) + 1) + "");
				rg = this.conjunction(rule1);
			}
			else if(rule.getOperator() == "<") {										//need some changes
				str = "";
				upper = this.getValues().get(0);
				rule0 = new RuleL(upper + "");
				rg.add(rule0);
				for(int i: this.getValues()) {
					if(i == this.getValues().get(0))
						continue;
					if(i < rule.getValues().get(0)) {
						lower = upper;
						upper = i;
						if(upper == lower+1)
							continue;
						else if(upper == lower+2)
							rule0 = new RuleE((lower+1) + "");
						else
							rule0 = new RuleIn((lower+1) + ", " + (upper-1));
						rg.add(rule0);
					}
				}
				lower = upper;
				upper = rule.getValues().get(0);
				if(upper != lower+1) {
					if(upper == lower+2)
						rule0 = new RuleE((lower+1) + "");
					else
						rule0 = new RuleIn((lower+1) + ", " + (upper-1));
					rg.add(rule0);
				}
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
