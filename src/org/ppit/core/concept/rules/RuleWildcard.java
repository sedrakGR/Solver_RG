package org.ppit.core.concept.rules;

import java.util.ArrayList;

import org.ppit.core.brain.instance.nucleus.NucleusInstance;
import org.ppit.util.Definitions;
import org.ppit.util.exception.UnsolvedDependency;
/**
 * 
 * @author Karen 
 * @description this is RuleWildcard (*) class that describes Wildcard Rule
 *
 */
public class RuleWildcard extends IRule {

	@Override
	public RuleWildcard clone() {
		// Nothing to clone.
		return this;
	}
	
	@Override
	public boolean setDependency(String depNodeName, NucleusInstance refToInstance) {
		return false;
	}
	@Override
	public ArrayList<String> getDependencies() {
		return new ArrayList<String>();
	}
	@Override
	/**
	 * @ check, checks whether the value is allowed for the rule 
	 * @param int value, value to be checked
	 * @return always return true becuase this is a wildcard rule :)
	 */
	public boolean check(int value) throws UnsolvedDependency {
		return true;
	}
	
	@Override
	/**
	 * @brief returns the operator type
	 * @note for Wildcard it supposed to be '='
	 */
	public String getOperator() {
		return Definitions.equalsOperator;
	}
	
	/**
	 * @brief returns the expression of the rule
	 * @note It is decided to have '*' for abstract rules as expression
	 */
	public String getExpressionString() {
		return Definitions.ruleWildcardValue;
	}

	@Override
	public ArrayList<Integer> getValues() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ArrayList<IRule> conjunction(IRule rule) {
		// TODO Auto-generated method stub
		return null;
	}
}
