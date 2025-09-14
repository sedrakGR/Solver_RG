package org.ppit.core.concept.rules;

import java.util.ArrayList;
import org.ppit.core.brain.instance.nucleus.NucleusInstance;
import org.ppit.core.concept.expression.Expression;
import org.ppit.util.Definitions;
import org.ppit.util.ParseUtil;
import org.ppit.util.exception.InvalidExpression;
import org.ppit.util.exception.UnsolvedDependency;

/**
 * 
 * @author SedrakG
 * @description this is RuleIn class that describes rule IN
 *
 */
public class RuleIn extends IRule {
	/**
	 * expressions for lower and higher bounds of the rule IN 
	 */
	private Expression m_lowerBound = null, m_upperBound = null;
	/**
	 * @brief Constructor
	 * @param lower String describing the expression of lower bound
	 * @param higher String describing the expression of higher bound 
	 * @throws InvalidExpression 
	 */
	public RuleIn(String expression) throws InvalidExpression
	{
		ArrayList<String> values = ParseUtil.splitExpression(expression);
		if(values.size() != 2){
			throw new InvalidExpression("The number of sub expressions for 'IN' rule must be 2, but " + values.size() + " has been passed. " + expression + " is incorrect.");
		}
		m_lowerBound = new Expression(values.get(0));
		m_upperBound = new Expression(values.get(1));
	}

	private RuleIn() {
	}
	
	@Override
	public RuleIn clone() {
		RuleIn clone = new RuleIn();
		clone.m_lowerBound = m_lowerBound.clone();
		clone.m_upperBound = m_upperBound.clone();
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
		//TODO: for just now it is supposed to not have list inside
		boolean isSet = false;
		if (m_lowerBound.setDependency(depNodeName, refToInstance))
		{
			isSet = true;
		}
		if (m_upperBound.setDependency(depNodeName, refToInstance) && !isSet)
		{
			isSet = true;
		}
		return isSet;
	}
	@Override
	/**
	 * @ getDependencies, retrieves the list of dependent node names
	 * @return returns dependent node names in Set<String> 
	 */
	public ArrayList<String> getDependencies()
	{
		// initialize with dependencies from m_lowerBound expression
		ArrayList<String> allDependecies = m_lowerBound.getDependencies();
		// add dependencies from m_higherBound
		allDependecies.addAll(m_upperBound.getDependencies());
		return allDependecies;
	}
	@Override
	/**
	 * @ check, checks whether the value is allowed for the rule 
	 * @param int value, value to be checked
	 * @return returns true if the value is allowed in the rule, and false in the other case
	 */
	public boolean check(int value) throws UnsolvedDependency
	{
		if (value >= m_lowerBound.evaluate() && value <= m_upperBound.evaluate()) {
			return true;
		}
		return false;
	}
	
	public int check_min(int value) {
		int lim = 0;
		try {
			lim = m_lowerBound.evaluate();
		} catch (UnsolvedDependency e) {
			return RI_PENDING;
		}
		if(value < lim) {
			return RI_FAIL;
		}
		else if( value == lim) {
			return RI_L_LIM;
		}
		return Rule.R_OK;
	}

	public int check_max(int value) {
		int lim = 0;
		try {
			lim = m_upperBound.evaluate();
		} catch (UnsolvedDependency e) {
			return RI_PENDING;
		}
		if(value > lim) {
			return RI_FAIL;
		}
		else if( value == lim) {
			return RI_U_LIM;
		}
		return Rule.R_OK;
	}

	@Override
	/**
	 * @brief returns the operator type
	 */
	public String getOperator()
	{
		return Definitions.inOperator;
	}
	
	@Override
	/**
	 * @brief Returns the expression of the rule
	 */
	public String getExpressionString()
	{
		ArrayList<String> exprList = new ArrayList<String>();
		exprList.add(m_lowerBound.getExprString());
		exprList.add(m_upperBound.getExprString());
		return ParseUtil.composeExpression(exprList);
	}
	
	public static final int RI_OK = 1;	// The Rule is satisfied.
	public static final int RI_L_LIM = 2;	// Rule is satisfied and equal to lower bound.
	public static final int RI_U_LIM = 3;	// Rule is satisfied and equal to upper bound.
	public static final int RI_PENDING = 4; // Expression is pending
	public static final int RI_FAIL = 5; // Rule is failed.
	
	/**
	 * @brief NNTF: new not tested and final
	 */
	public ArrayList<Integer> getValues()
	{
		ArrayList<Integer> values = new ArrayList<Integer>();
		
		try {
			values.add(m_lowerBound.evaluate());
			values.add(m_upperBound.evaluate());		
		} catch (UnsolvedDependency e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return values;
	}
	
	/**
	 * @brief NNTF: new not tested and final
	 */
	public ArrayList<IRule> conjunction(IRule rule)
	{
		IRule rule0 = null;
		ArrayList<IRule> rg = new ArrayList<IRule>();
		int lower, upper;
		String str;
		lower = this.getValues().get(0);
		upper = this.getValues().get(1);
		
		try {		
			if(rule.getOperator() == "IN") {
				if(lower < rule.getValues().get(0))
					lower = rule.getValues().get(0);
				if(upper > rule.getValues().get(1))
					upper = rule.getValues().get(1);
				if(upper > lower)
					rule0 = new RuleIn(lower + ", " + upper);
				else if(upper == lower)
					rule0 = new RuleE(lower + "");
				rg.add(rule0);
			}
			else if(rule.getOperator() == "=") {
				str = "";
				for(int i: rule.getValues()) {
					if(lower <= i && i <= upper) {
						if(!str.isEmpty())
							str += ", ";
						str += i;
					}
				}
				if(!str.isEmpty())
					rule0 = new RuleE(str);
				rg.add(rule0);
			}
			else if(rule.getOperator() == "!=") {
				for(int i: rule.getValues()) {											//for sorted array
					if(lower <= i && i <= upper) {
						if(lower == i) {
							lower++;
							continue;
						}
						else if(lower == i-1)
							rule0 = new RuleE(lower + "");
						else
							rule0 = new RuleIn(lower + ", " + (i-1));
						lower = i+1;
						rg.add(rule0);
					}
				}
				if(lower < upper)
					rule0 = new RuleIn(lower + ", " + upper);
				else if(lower == upper)
					rule0 = new RuleE(lower + "");
				else
					rule0 = null;
				rg.add(rule0);
			}
			else if(rule.getOperator() == ">=") {
				if(lower >= rule.getValues().get(0))
					rule0 = this;
				else if(upper < rule.getValues().get(0))
					rule0 = null;
				else if(upper == rule.getValues().get(0))
					rule0 = new RuleE(upper + "");
				else
					rule0 = new RuleIn(rule.getValues().get(0) + ", " + upper);
				rg.add(rule0);
			}
			else if(rule.getOperator() == ">") {
				IRule rule1 = new RuleGE((rule.getValues().get(0) + 1) + "");			//RuleG(4) same as RuleGE(5)
				rg = this.conjunction(rule1);
			}
			else if(rule.getOperator() == "<") {
				if(upper < rule.getValues().get(0))
					rule0 = this;
				else if(lower >= rule.getValues().get(0))
					rule0 = null;
				else if(lower == rule.getValues().get(0)-1)
					rule0 = new RuleE(lower + "");
				else
					rule0 = new RuleIn(lower + ", " + (rule.getValues().get(0)-1));
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
