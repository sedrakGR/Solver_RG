package org.ppit.core.concept.action;

import java.util.ArrayList;

import org.ppit.core.brain.instance.nucleus.NucleusInstance;
import org.ppit.core.concept.AbstractBase;
import org.ppit.core.concept.Concept;
import org.ppit.core.concept.ConceptType;
import org.ppit.core.concept.composite.CompositeConcept;
import org.ppit.core.concept.expression.Expression;
import org.ppit.core.concept.primitive.PrimitiveConcept;
import org.ppit.util.Definitions;
import org.ppit.util.exception.InvalidExpression;
import org.ppit.util.exception.PPITException;

/**
 * 
 * @author SedrakG
 * @brief Action - this class represents the action,
 * which has one composite attribute and list of actionAttributes (it's pair <conceptName, expression>)
 */
public class Action extends AbstractBase {
	/**
	 * @brief m_preConditionConcept preCondition composite concept for action
	 */
	private CompositeConcept m_preConditionConcept = null;
	/**
	 * @brief m_postConditionAttributeList list of post conditions of the action
	 */
	private ArrayList<PostConditionAttribute> m_postConditionAttributeList = null;
	
	// the expression that indicates the which makes this action,
	// this is the dependency which shows which attribute is key for actor
	private Expression m_sideIndicatorExpression = null;
	
	

	/**
	 * @Constructor
	 * @param name, Name of the action
	 * @param preConditionConcept, preCondition of the action, which is composite concept
	 * @param postConditionAttributeList, list of post condition attributes
	 */
	public Action(String name) {
		super(ConceptType.ACTION, name);
	}
	
	public void setSideIndicator(String indicatorString) throws PPITException {
		m_sideIndicatorExpression = new Expression(indicatorString);
		for (String dependency : m_sideIndicatorExpression.getDependencies()) {
			if (dependency.startsWith(Definitions.preCondConceptJSON)) {
				dependency = dependency.substring(Definitions.preCondConceptJSON.length());
			}
			Concept concept = getPreConditionConcept().getAttribute(dependency);
			// should be nucleus
			if (!(concept instanceof PrimitiveConcept)) {
				throw new PPITException("wrong expression to indicate side of action");
			}
		}
	}
	
	public Expression getSideIndicator() {
		return m_sideIndicatorExpression;
	}
	
	/**
	 * @param preConditionConcept Concept to be set as precondition
	 */
	public void setPreCondition(CompositeConcept preConditionConcept) {
		m_preConditionConcept = preConditionConcept;
	}
	
	/**
	 * @param postConditionAttributeList, list of post condition attributes
	 */
	public void setPostCondition(ArrayList<PostConditionAttribute> postConditionAttributeList)
	{
		m_postConditionAttributeList = postConditionAttributeList;
	}
		
	/**
	 * @return Returns the attributeConcept of set, which is composite 
	 */
	public CompositeConcept getPreConditionConcept() {
		return m_preConditionConcept;
	}
	
	/**
	 * @return Returns the list of post conditions
	 */
	public ArrayList<PostConditionAttribute> getPostCondition() {
		return m_postConditionAttributeList;
	}
	
	/**
	 * @brief getJSON() get JSON for the action
	 */
	public String getJSON()
	{
		String actionJSON = "{ 'name' : '" + getName() + "', ";
		actionJSON += "'sideIndicator' : '" + m_sideIndicatorExpression.getExprString() + "', ";
		actionJSON += "'precondition' : " + m_preConditionConcept.getJSON() + ", ";
		actionJSON += "'expressions' : [";
		boolean isFirst = true;
		for(PostConditionAttribute attribute : m_postConditionAttributeList)
		{
			if (isFirst == false)
				actionJSON += ", ";
			else
				isFirst = false;
			actionJSON += "{ 'right' : '" + attribute.getExpression() + "', "; // the right part is the expression of attribute
			actionJSON += " 'left' : '" + attribute.getAttributeName() + "', "; // the right part is the name of the attribute and a primitive NODE in the precondition 
			actionJSON += " 'oper' : '" + Definitions.equalsOperator + "' }"; // it is supposed that actions can have only equals operator
		}
		actionJSON += "]}";
		return actionJSON;
	}
	
	/**
	 * @brief getDepencies get the list of dependencies of the action's post condition
	 * If the return list is empty then there are not unresolved dependency
	 * @return Returns the list of dependencies of post condition of action in an ArrayList<String>
	 */
	public ArrayList<String> getDependencies()
	{
		ArrayList<String> dependencies = new ArrayList<String>();
		for (PostConditionAttribute attribute : m_postConditionAttributeList)
		{
			dependencies.addAll(attribute.getDependencies());
		}
		return dependencies;
	}
	
	/**
	 * @brief setDependency Sets dependency of the action
	 * @param depNodeName The name of dependent node
	 * @param primitiveConcept reference to a concept which is dependent
	 * @return returns true if dependency is set and false in other case
	 */
	public boolean setDependency(String depNodeName, NucleusInstance nucleusInstance)
	{
		boolean isSet = false;
		// Get rules from map with the key is depNodeName,
		// and set the dependency for that rule
		for (PostConditionAttribute attribute : m_postConditionAttributeList)
		{
			if (attribute.setDependency(depNodeName, nucleusInstance))
				isSet = true;
		}
		
		return isSet;
	}
}