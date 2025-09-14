package org.ppit.core.concept.rules;

import java.util.ArrayList;

import org.ppit.core.brain.instance.nucleus.NucleusInstance;
import org.ppit.util.Definitions;
import org.ppit.util.exception.UnsolvedDependency;
/**
 * 
 * @author SedrakG
 * @description this is RuleAbstract class that describes Abstract Rule
 *
 */
public class RuleAbstract extends IRule {

	@Override
	public RuleAbstract clone() {
		// Nothing to clone.
		return this;
	}
	
	@Override
	/**
	 * @ setDependency sets a reference to the dependent node
	 * @param String depNodeName, the name of dependent node in expression
	 * @param Expression refToExpr, keeps a reference to expression of the dependent node
	 * @return Returns true if dependency was set and false if not
	 *  @note This returns always false as no dependency can be set in abstract Rule
	 */
	public boolean setDependency(String depNodeName, NucleusInstance refToInstance)
	{
		return false;
	}
	@Override
	/**
	 * @ getDependencies, retrieves the list of dependent node names
	 * @return returns dependent node names in ArrayList<String>
	 * @note Returns null as it has no list of dependencies 
	 */
	public ArrayList<String> getDependencies()
	{
		return new ArrayList<String>();
	}
	@Override
	/**
	 * @ check, checks whether the value is allowed for the rule 
	 * @param int value, value to be checked
	 * @return returns true if the value is allowed in the rule, and false in the other case
	 * @note always returns false because no value can be satisfied for a abstract rule
	 */
	public boolean check(int value) throws UnsolvedDependency
	{
		return false;
	}
	
	@Override
	/**
	 * @brief returns the operator type
	 * @note for Abstract it supposed to be '='
	 */
	public String getOperator()
	{
		return Definitions.equalsOperator;
	}
	
	/**
	 * @brief returns the expression of the rule
	 * @note It is decided to have '?' for abstract rules as expression
	 */
	public String getExpressionString()
	{
		return Definitions.ruleAbstractValue;
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
	
	public String toText() {
		return Definitions.ruleAbstractValue;
	}
}
