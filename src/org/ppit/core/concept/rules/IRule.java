package org.ppit.core.concept.rules;

import java.util.ArrayList;
import java.util.List;
/**
 * @author SedrakG
 */

import org.ppit.core.brain.instance.nucleus.NucleusInstance;
import org.ppit.util.Definitions;
import org.ppit.util.ParseUtil;
import org.ppit.util.exception.UnsolvedDependency;


//class Expression {
//	private String m_expression; 
//	public String getExprString()
//	{
//		return "String";
//	}
//	public Expression(String expression)
//	{
//		m_expression = expression;
//	}
//	boolean setDependency(String depNodeName, PrimitiveConcept refToConcept){return true;}
//	ArrayList<String> getDependencies(){return null;}
//	int evaluate(){return 0;}
//}

/**
 * @description This is the base interface for all Rules
 */

public abstract class IRule implements Cloneable {
	/**
	 * @brief Sets the dependency of contained expression to proper Primitive Concept 
	 * @param depNodeName The node name, to have dependency
	 * @param refToConcept Concept, to be referred in the dependent node
	 * @return Returns true if dependency was set and false if not
	 */
	public abstract boolean setDependency(String depNodeName, NucleusInstance refToInstance);
	/**
	 * @brief Returns set of names for dependencies in the rule that are not resolved yet
	 * @par if dependency is resolved it is not contained in the list
	 * @return Set of names for dependencies
	 */
	public abstract  ArrayList<String> getDependencies();
	/**
	 * @brief Checks if the value is correct for the rule or not
	 * @param value The value to be checked
	 * @return returns true, if the value is correct for rule, and false in other case
	 * @throws UnsolvedDependency 
	 */
	public abstract boolean check(int value) throws UnsolvedDependency;
	
	public abstract IRule clone();
	
	/**
	 * @brief Returns the operator of the rule
	 */
	public abstract String getOperator();
	
	/**
	 * @brief Returns the expression of the rule
	 * @return Rule expression
	 */
	public abstract String getExpressionString();
	
	/**
	 * @brief Creates and returns the JSON of the rule. Can be further abstarcted to be implemented in
	 *        derived rules. 
	 * @return
	 */
	public String getJSON() {
		String ruleJSON = new String();
		ruleJSON += "'value' : ";
		if (this.getOperator().equals(Definitions.inOperator))
		{
			ArrayList<String> values = ParseUtil.splitExpression(this.getExpressionString());
			ruleJSON += "{ 'minValue' : '" + values.get(0) + "', ";
			ruleJSON += "'maxValue' : '" + values.get(1) + "' }, ";
		}
		else
		{
			ruleJSON += "'" + this.getExpressionString() + "', ";
		}
		ruleJSON += "'oper' : '" + this.getOperator() + "' ";
		return ruleJSON;
	}
	
	/**
	 * @brief equals indicates if the given rule equals to the current rule
	 * this is checking expressions to be equal and operators to be the same  	
	 */
	public boolean equals(IRule rule) {
		if (getOperator().equals(rule.getOperator())
				&& getExpressionString().equals(rule.getExpressionString())) {
			return true;
		}
		return false;
	}
	
	/**
	 * @brief NNTF: new not tested and final (returns the values which rule contains, it can be 1(RuleL), 2(RuleIn)
	 * 											or 100(RuleNE) of values)
	 */
	public abstract ArrayList<Integer> getValues();
	
	/**
	 * @brief NNTF: new not tested and final (return the list of rules from two rules conjunction)
	 */
	public abstract ArrayList<IRule> conjunction(IRule rule);
	
	/**
	 * @brief NNTF: new not tested and final (return the list of rules from two rules conjunction)
	 */
	public void replaceDependencies(String oldName, String newName) {
		String expression = getExpressionString();
		expression = expression.replaceFirst(oldName, newName);
	}
	
	public String toText() {
	    String op = this.getOperator();
	    String expr = this.getExpressionString();
	    String operatorText;
	    String expressionText;
	    
	    if (op.equals(Definitions.inOperator)) {
	        // When operator is 'in', we expect the expression to include two values:
	        // the lower and upper bounds of the range.
	        ArrayList<String> values = ParseUtil.splitExpression(expr);
	        operatorText = "equals to ";
	        try {
	            // Parse the lower and upper bounds as integers.
	            int lower = Integer.parseInt(values.get(0).trim());
	            int upper = Integer.parseInt(values.get(1).trim());
	            // Create a list of all numbers between lower and upper (inclusive).
	            List<String> rangeValues = new ArrayList<String>();
	            for (int i = lower; i <= upper; i++) {
	                rangeValues.add(String.valueOf(i));
	            }
	            // Join them into a string separated by " and "
	            expressionText = String.join(" and ", rangeValues);
	        } catch (NumberFormatException e) {
	            // If parsing fails, fall back to simply joining the provided values.
	            expressionText = String.join(" and ", values);
	        }
	    } else if (op.equals(Definitions.equalsOperator)) {
	        operatorText = "equals to ";
	        expressionText = expr;
	    } else if (op.equals(Definitions.greaterOperator)) {
	        operatorText = "greater than ";
	        expressionText = expr;
	    } else if (op.equals(Definitions.lessOperator)) {
	        operatorText = "less than ";
	        expressionText = expr;
	    } else if (op.equals(Definitions.greaterEqualsOperator)) {
	        operatorText = "greater than or equal to ";
	        expressionText = expr;
	    } else if (op.equals(Definitions.lessEqualsOperator)) {
	        operatorText = "less than or equal to ";
	        expressionText = expr;
	    } else if (op.equals(Definitions.notEqualsOperator)) {
	        operatorText = "not equal to ";
	        expressionText = expr;
	    } else {
	        // Default to a simple concatenation if operator not recognized
	        operatorText = op + " ";
	        expressionText = expr;
	    }
	    
	    return operatorText + expressionText;
	}

}
