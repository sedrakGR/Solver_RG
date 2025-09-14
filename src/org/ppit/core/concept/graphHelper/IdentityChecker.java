package org.ppit.core.concept.graphHelper;

import java.util.Collection;

import org.ppit.core.concept.Concept;
import org.ppit.core.concept.composite.CompositeConcept;
import org.ppit.core.concept.primitive.PrimitiveConcept;
import org.ppit.core.concept.rules.IRule;
import org.ppit.core.concept.rules.RuleWildcard;
import org.ppit.core.concept.set.SetConcept;
import org.ppit.util.exception.PPITException;

/**
 * 
 * @author SedrakG
 * @brief This is to check if two concepts are identical 
 */
public class IdentityChecker {
	/**
	 * 
	 * @brief check checks whether these two primitive concepts are identical
	 * @param baseConcept the concept, against which is compared
	 * @param checkConcept the concept, which is being compared against base
	 * @return Returns true if they are identical, and false if they aren't 
	 */
	public static boolean check(PrimitiveConcept baseConcept, PrimitiveConcept checkConcept) throws PPITException
	{
		// check if full names are identical, then they are the same concept,
		// as name is the ID for the concept, so there is only one concept with a certain full name
		if (baseConcept.getName().equals(checkConcept.getName())) {
			return true;
		}
		boolean identical = (checkConcept.getParent() == baseConcept
				|| checkConcept.getParent() == baseConcept.getParent());
		if (identical)
		{
			//  Rules are the same 
			identical = (areIdentical(baseConcept.getRule(), checkConcept.getRule()));
		}
		return identical;
	}
	
	/**
	 * 
	 * @brief check checks whether these two composite concepts are identical
	 * @param baseConcept the concept, against which is compared
	 * @param checkConcept the concept, which is being compared against base
	 * @return Returns true if they are identical, and false if they aren't
	 * @throws PPITException
	 */
	public static boolean check(CompositeConcept baseConcept, CompositeConcept checkConcept) throws PPITException
	{
		// check if full names are identical, then they are the same concept,
		// as name is the ID for the concept, so there is only one concept with a certain full name
		if (baseConcept.getName().equals(checkConcept.getName())) {
			return true;
		}
		if (!(baseConcept.isNegated() && checkConcept.isNegated()
				|| !baseConcept.isNegated() && !checkConcept.isNegated())) {
			return false;
		}
		// TODO: we may an issue here related with negated attributes.
		// e.g. if A and B have all the same attributes {x, y, z}, but in A x is negated,
		// then this will return TRUE, and I guess we expect FALSE  
		Collection<Concept> collection = checkConcept.getAttributes(); 
		boolean identical = collection.size() == baseConcept.getAttributes().size();
		Concept attribute;
		for (Concept concept : collection) {
			if ((attribute = baseConcept.getAttribute(concept.getRelativeName())) != null
					&& concept.CONCEPT_TYPE == attribute.CONCEPT_TYPE) {
				switch (concept.CONCEPT_TYPE)
				{
				case PRIMITIVE:
					identical &= check((PrimitiveConcept)attribute, (PrimitiveConcept)concept);
					break;
				case COMPOSITE:
					identical &= check((CompositeConcept)attribute, (CompositeConcept)concept);
					break;
				case SET:
					identical &= check((SetConcept)attribute, (SetConcept)concept);
					break;
				default:
					throw new PPITException("unknown concept type!!!");
				}
			} else {
				// if attribute does not exist in the base concept
				// or the attribute with the same name is different type
				// then these two concepts are not identical
				identical = false;
			}
			// if some attribute does not match then it doesn't sense to continue
			if (!identical)
				break;
		}
		return identical;
	}
	
	/**
	 * 
	 * @brief check checks whether these two set concepts are identical
	 * @param baseConcept the concept, against which is compared
	 * @param checkConcept the concept, which is being compared against base
	 * @return Returns true if they are identical, and false if they aren't 
	 * @throws PPITException
	 */
	public static boolean check(SetConcept baseConcept, SetConcept checkConcept) throws PPITException
	{
		// check if full names are identical, then they are the same concept,
		// as name is the ID for the concept, so there is only one concept with a certain full name
		if (baseConcept.getName().equals(checkConcept.getName())) {
			return true;
		}
				
		boolean identical = true;
		CompositeConcept baseAttribute = baseConcept.getElement(), checkAttribute = checkConcept.getElement();
		identical &= check(baseAttribute, checkAttribute);
		if (identical) {
			// Checking the 'count' attribute
			identical = areIdentical(baseConcept.getElementCount(), checkConcept.getElementCount())
					|| (!checkConcept.getElementCount().getDependencies().isEmpty());
		}
		return identical;
	}

	
	/**
	 * @brief areIdentical This is to check if these rules are identical to each other
	 * @param baseRule, base rule, against which is compared
	 * @param checkRule, rule which is compared
	 * @return Returns true if these rules are identical
	 * @throws PPITException
	 */
	public static boolean areIdentical(IRule baseRule, IRule checkRule) throws PPITException {
		if (baseRule == null || checkRule == null) {
			throw new PPITException("null rule passed to checkRules function, which is not expected");
		}
		boolean identical = false;
		if (checkRule.getOperator().equals(baseRule.getOperator())) {
			if (checkRule.getExpressionString().equals(baseRule.getExpressionString())) {
				identical = true;
			}
		}
		return identical;
	}
}