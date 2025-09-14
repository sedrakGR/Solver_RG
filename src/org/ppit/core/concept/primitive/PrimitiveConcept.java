package org.ppit.core.concept.primitive;

import org.ppit.core.concept.Concept;
import org.ppit.core.concept.ConceptReference;
import org.ppit.core.concept.ConceptType;
import org.ppit.core.concept.rules.*;
import org.ppit.util.Definitions;
import org.ppit.util.exception.*;

/**
 * 
 * @author SedrakG
 * @detailed Primitive concept class, inherits from Concept class
 *
 */
public class PrimitiveConcept extends Concept {
	
	/**
	 * These are the dependent rules of the Primitive Concept. This contain both own and Parent rules
	 * if there are dependencies.
	 */
	private RuleGroup m_dependentRules = null;

	/**
	 * Represents the list of changed (compared to parent) but independent rules.
	 */
	private RuleGroup m_changedRules = null;
	
	/**
	 *  This is for the getJSON, need to own rule separately from parent's rules
	 */
	private IRule m_ownRule = null;
	
	/**
	 * @brief Indicates is the concept key for playing side 
	 */
	private boolean m_key = false;
	
	/**
	 * @brief This is to keep the nucleus type for concept
	 */
	private PrimitiveType m_type = null;
	
	/**
	 * @brief Reference to the parent concept, which is also a primitive Concept
	 */
	private ConceptReference m_parent = null;
	
	/**
	 * @brief This is the name of containing attribute
	 */
	private String m_attributeName = null;
	
	/**
	 * @brief Constructor
	 * @param name The name of the concept
	 * @param parentName the name of parent concept
	 */
	public PrimitiveConcept(String name, PrimitiveType type, PrimitiveConcept parent) {
		super(name, ConceptType.PRIMITIVE);
		m_type = type;
		if (parent != null) {
			m_parent = new ConceptReference(parent.getName(), parent);
		}
	}
	
	/**
	 * @return Returns the parent concept as Primitive Concept if you don't mind :)
	 * (it can also return concept, but I think this is better)
	 */
	public PrimitiveConcept getParent() {
		return (m_parent != null )? (PrimitiveConcept)m_parent.getConcept() : null;
	}
	
	/**
	 * @brief setKey Sets the value for the key
	 * @param key The value for the key
	 */
	public void setKey(boolean key) {
		m_key = key;
	}
	
	/**
	 * @brief isKey method to indicate is the concept key for playing side or not
	 * @return Returns true if it is a key concept and false in other case
	 */
	public boolean isKey() {
		return m_key;
	}
	
	/**
	 * @return true if the concept is nucleus.
	 */
	public boolean isNucleus() {
		return getName().equals(m_type.getName());
	}
	
	/**
	 * @return Returns the nucleus type of concept
	 */
	public PrimitiveType getType() {
		return m_type;
	}
	
	/**
	 * @brief Sets the attribute completely, the name and it's rule
	 * @param attributeName The name of the attribute
	 * @param rule The rule of the attribute
	 */
	public void setAttribute(String attributeName, IRule rule)
	{
		m_attributeName = attributeName;
		//m_rules.add(m_ownRule = rule);
		m_ownRule = rule;
	}
	
	/**
	 * @brief Get own rule of concept
	 * @return Return IRule 
	 * @note This is temporary solution, may be not used after
	 */
	public IRule getRule() {
		return m_ownRule;
	}
	
	/**
	 * @brief isAbstract() Indicates whether the concept is abstract
	 * @return Return boolean true if the concept is abstract and false in other case
	 */
	public boolean isAbstract() {
		return m_ownRule.getExpressionString().contains(Definitions.ruleAbstractValue); 
	}
	
	@Override
	public RuleGroup getChangedEIndependentRules() throws IncorrectDependency {
		if (m_changedRules == null) {
			this.evaluateDependencies();
		}
		return (RuleGroup)m_changedRules.clone();
	}
	
	@Override
	public RuleGroup getExternalDependencies() throws IncorrectDependency {
		if (m_dependentRules == null) {
			this.evaluateDependencies();
		}
		return (RuleGroup)m_dependentRules.clone();
	}
	
	/**
	 * @throws IncorrectDependency 
	 * @brief Generate the list of dependent rules.
	 */
	public void evaluateDependencies() throws IncorrectDependency {
		if(this.getParent() != null) {
			m_dependentRules = this.getParent().getExternalDependencies(); 
		} else {
			m_dependentRules = new RuleGroup();
		}
		
		m_changedRules = new RuleGroup();
		
		if(m_ownRule.getDependencies().isEmpty() == false) {
			// Initialize dependent rules
			Rule r = new Rule(m_ownRule, this.getRelativeName());
			m_dependentRules.addRule(r);
		} else {
			// Initialize changed rules.
			if(this.getParent() != null 
					&& !m_ownRule.getOperator().equals(Definitions.ruleAbstractValue)
					&& !m_ownRule.getOperator().equals(Definitions.ruleWildcardValue)
					&& !m_ownRule.equals(getParent().getRule())) 
			{
				Rule r = new Rule(m_ownRule, getRelativeName());
				m_changedRules.addRule(r);
			}
		}
	}
	
	/**
	 * @brief isFree Indicates whether the concept is free or no
	 * @return Returns boolean, true if the concept is free, false if not
	 */
	public boolean isFree() {
		return m_dependentRules.isEmpty();
	}
	
	/**
	 * @brief get the name of concept's attribute
	 * @return Returns the name of concept's attribute
	 */
	public String getAttributeName() {
		return m_attributeName;
	}
	
	@Override
	/**
	 * @brief getJSON() get JSON for primitive concept
	 */
	public String getJSONPure(String containingConceptName)
	{
		// Get DB name of concept
		String concDBName = getName();
		
		// Get name of concept in the context. It is either the full name or relative name.
		String concRealName;

		if (!containingConceptName.equals("")) {
			// If containingConceptName is not empty then parse it's real name
			concRealName = concDBName.substring(containingConceptName.length() + 1);
		} else {
			// If no one contains this concept, then it's real and DB names are the same 
			concRealName = concDBName;
		}
		// Start collection of JSON string of concept with adding it's name
		String concJSON = "'name' : '" + concRealName + "', ";
		// Mark the type of concept 'p' if primitive and 'n' if nucleus
		String type = "'n'";
		// Get the parent name of the concept
		String parent = "";
		if (isNucleus() == false) 
		{
			parent = m_parent.getName();
			type = "'p'";
		}
		
		// Add parent name to the JSON string of concept
		concJSON += "'parent' : '" + parent + "',";
		// Add the type of concept
		concJSON += "'type' : " + type + " , ";
		// Add is_key field with m_key
		concJSON += "'is_key' : '"+ ((m_key == true) ? "1" : "0") +"', ";
		// Fill nucleusConceptIndexAttrs, which is empty
		concJSON += "'nucleusConceptIndexAttrs': [], ";
		// Fill the value attribute
		concJSON += "'nucleusConceptValueAttrs' : [ ";
		// The name of value attribute
		String name = m_attributeName;
		// Add the attribute
		concJSON += "{'name': '" + name + "', ";
		concJSON += m_ownRule.getJSON();
		concJSON += "}]";
		if (isZeroLevel()) {
			System.out.println(toText(true));
		}
	    return concJSON;
	}
	
	@Override
	public String toText(boolean full) {
	    // Get the concept's own name
	    String conceptName = getName();
	    // Use the toText() method of the m_ownRule if it's set, otherwise indicate it's undefined
	    String ruleText = (m_ownRule != null) ? m_ownRule.toText() : "undefined";
	    
	    // Start with the immediate parent
	    Concept parent = getParent();
	    // Traverse up until you find a zero-level parent or run out of parents.
	    while (parent != null && !parent.isZeroLevel()) {
	        parent = parent.getParent();
	    }
	    
	    if (full) {

	    	// If a zero-level parent exists, include it; otherwise, omit that portion.
	    	if (parent != null) {
	    		return "classifier " + conceptName + " is a " + parent.getName() + " with value " + ruleText;
	    	} else {
	    		return "classifier " + conceptName + " with value " + ruleText;
	    	}
	    } else {
	    	return " with value " + ruleText;
	    }
	}


	
	@Override
	/**
	 * @brief Constructs and returns the JSON representation of the primitive concept header.
	 */
	public String getHeaderJson(){
		return "'type' : 'p', 'parent' : '" + ((m_parent == null) ? this.getName() : m_parent.getName()) + "', 'name' : '" + getName() + "'";
	}
	
	public String getDuplicatePure(String name)
	{
		// The current concept will serve as a parent, hence, its full (db) name is the parent name for the new concept.
		String parentName = getName();
		
		// If new name is not specified, then the name of the concept is being used as a new name.
		if(name == null || name.equals("")) {
			name = getRelativeName();
		}

		IRule expression = m_ownRule; 
		if(!(m_ownRule instanceof RuleAbstract)) {
			expression = new RuleWildcard();
		}
		
		String concJSON = "'name' : '" + name + "', ";
		concJSON += "'parent' : '" + parentName + "',";
		concJSON += "'type' : 'p', "; 					// New type is always primitive.
		concJSON += "'is_key' : '"+ ((m_key == true) ? "1" : "0") +"', ";
		concJSON += "'nucleusConceptIndexAttrs': [], ";
		concJSON += "'nucleusConceptValueAttrs' : [ ";
		concJSON += "{'name': '" + m_attributeName + "', ";
		concJSON += expression.getJSON();
		concJSON += "}]";
	    return concJSON;		
	}

	@Override
	public Concept getAttribute(String path) {
		return null;
	}
}