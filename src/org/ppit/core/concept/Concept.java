package org.ppit.core.concept;

import org.ppit.core.concept.rules.RuleGroup;
import org.ppit.util.Definitions;
import org.ppit.util.exception.IncorrectDependency;
import org.ppit.util.exception.PPITException;

/**
 * 
 * @author SedrakG
 * @brief abstract class Concept,
 * this is the base class for primitive and composite concepts 
 */
public abstract class Concept extends AbstractBase {
	/**
	 * @brief Indicates if this abstract is already checked for being USAGE or not
	 */
	private boolean m_checkedUsage = false;
	
	/**
	 * @brief Indicates if the abstract is usage or not
	 * If m_checkedUsage is false, then this value is meaningless 
	 */
	private boolean m_isUsage = false;
	
	/**
	 * @brief Indicates if the concept is negated or not (only for composite concept's attributes)
	 */
	private boolean m_negated = false;

	/**
	 * @brief Constructor
	 */
	public Concept(String conceptName, ConceptType conceptType) {
		super(conceptType, conceptName);
	}
			
	/**
	 * @brief getRelativeName Get the relative name of concept (as appears in the holder concept)
	 * @return Returns concept relative name
	 */
	public String getRelativeName() {
		int pos = getName().lastIndexOf(Definitions.subattributeAccessor);
		if (pos == -1)
			return getName();
		return getName().substring(pos + 1);
	}
	
	/**
	 * @brief getJSON get JSON of the concept
	 * @return Returns JSON of the concept 
	 */
	public String getJSON() {
		return getJSON("");
	}

	/**
	 * @brief Sets that the concept is negated 
	 */
	public void setNegated() {
		m_negated = true;
	}
	
	/**
	 * @brief Indicates if the concept is negated
	 */
	public boolean isNegated() {
		return m_negated;
	}
	
	/**
	 * @brief Returns the concept which was used to create this concept and null 
	 * 		  if it is only a composition.
	 */
	public abstract Concept getParent();
	
	public String getJSON(String containingConceptName) {
		if (isZeroLevel()) {
			System.out.println(toText(true));
		}
		return "{" + getJSONPure(containingConceptName) + "}";
	}
	
	/**
	 * @brief getJSON() get JSON of the concept
	 * @param conctaingConcepName concept name, which contains current concept.
	 * If containing concept name is empty that means that current concept is
	 * not contains in any concept 
	 * @return Returns JSON of the concept
	 */
	public abstract String getJSONPure(String containingConceptName);
	
	/**
	 * @brief re-evaluates all dependencies (recursively, including child attributes).
	 * 		  This shall be called after creating or modifying a concept.
	 * @throws IncorrectDependency
	 */
	public abstract void evaluateDependencies() throws IncorrectDependency;
	
	/**
	 * @brief getExternalDependencies get the cached list of external dependencies as a 
	 * 		  RuleGroup object. Each concept updates the left attribute name appropriately 
	 * 		  adding its name in front of the prefix ( etc. x => a.x => b.a.x ) 
	 * 		  If it is called before evaluateDependencies() then it will invoke the function.
	 * @return RuleGroup
	 * @throws IncorrectDependency 
	 */
	public abstract RuleGroup getExternalDependencies() throws IncorrectDependency;
	
	/**
	 * brief the list of changed independent rules in the abstract
	 * this is meaningful only for USAGE abstracts,
	 * they are being checked if USAGE abstract is activated
	 * @return Returns the list of changed independent rules 
	 * @throws IncorrectDependency 
	 */
	public abstract RuleGroup getChangedEIndependentRules() throws IncorrectDependency;
	
	/**
	 * @brief Constructs and returns the JSON representation of the concept header.
	 * 		    	Including concept type, name, parent if any.
	 * @return the JSON header
	 */
	public abstract String getHeaderJson();

	/**
	 * @brief Finds and returns the reference to the attribute with specified path.
	 * @param path - this is the relative path 
	 * 		  (if concept name is A, then this path is b.x instead of A.b.x) 
	 * @return the concept with the given path.
	 */
	public abstract Concept getAttribute(String path);
	
	
	/**
	 * @brief Indicates if the concept is abstract or not
	 */
	public abstract boolean isAbstract();
	
	/**
	 * @brief creates and returns a new abstract using the source one as a parent.
	 * 		  it preserves the names of the attributes in the source abstract but
	 * 	      requires a new name for a duplciated one.
	 * @param name the name of the new concept.
	 * @return the duplicated abstarct
	 * @throws PPITException (do not throws more)
	 */
	public abstract String getDuplicatePure(String name);
	
	//we don't need these throws
	public String getDuplicate(String name)
	{
		return "{" + getDuplicatePure(name) + "}";
	}
	
	/**
	 * @brief Indicates if the abstract is USAGE.<div/> 
	 * <B>NOTE:</B> We are not interested in it�s being USAGE or SPECIFICATION if it�s not PURE VIRTUAL abstract
	 * @return returns true if the abstract is USAGE and false if the abstract is specification or not virtual 
	 */
	public boolean isUsage()
	{
	    if (m_checkedUsage) {
	    	return m_isUsage;
	    }
		Concept parent= getParent();
		// If there is no parent, then abstract can't be a usage.
		if (parent != null) {
			// Either the parent is Usage or
			// The parent of the Usage abstract is Virtual
			// and the abstract shall be a usage by its name (nested stuff)
		    if (parent.isUsage() || (parent.isAbstract() && isUsageByName() ) ) {
		    	return true;
		    }
		}
	    return false;
	}
	
	public boolean isSpecification()
	{
		Concept parent = getParent();
		if ((m_checkedUsage && m_isUsage)
				|| parent == null) {
			return false;
		}
		if (parent.isAbstract() && !parent.isUsage() && !isUsageByName() ) {
			return true;
		}
		return false;
	}

	/**
	 * @brief Indicates if the concept can be usage looking at it's name
	 * @return Returns true if it supposed to be usage by name
	 */
	private boolean isUsageByName() {
	    Concept parent = getParent();
	    // If the abstract name has the same level of nesting as its parent, then it
	    // can't be a usage, it is rather a specification.
	    if (getNameDepth() > parent.getNameDepth()) {
			// The attributes of the Virtual abstract are not transforming to Usages when 
	    	//  the later ones are. 
	    	//  For example, check.fuc.tar is still Virtual and not Usage
	    	//  even though chech.fuc is a Usage (it is still Virtual)
	    	// In other words: "If its container�s parent is the same as its parent�s container, then
	    	//  this is not a Usage"
	    	String containerName = getName().substring(0, 
	    			getName().lastIndexOf(Definitions.subattributeAccessorChar));

    		ConceptManager manager = ConceptManager.getInstance();
    		Concept containerParent = manager.getConcept(containerName).getParent();

    		// If the container has no parent then this is a Usage of the abstract.
    		if(containerParent == null ) {
    			return true;
    		}
	    	
	    	// If parent has no container, then this is the first level and hence a Usage.
	    	int parentContPos = parent.getName().lastIndexOf(Definitions.subattributeAccessorChar);
	    	if(parentContPos == -1) {
	    		return true;
	    	}
	    	String parentContainerName = parent.getName().substring(0, parentContPos);
	    	
	    	// If the ContainersParent is not the same as ParentsContainer then this is a Usage.
	    	if(!containerParent.getName().equals(parentContainerName)) {
	    		return true;
	    	}
    	}
	    return false;
    }
	
	/**
	 * @brief depth of the name, aka number of �.� in the name of the abstract
	 * @return returns the number of '.'s in the name of the abstract
	 */
	private int getNameDepth() {
		int depth = 0;
		int index = getName().indexOf(Definitions.subattributeAccessorChar);
		int lastIndex = getName().lastIndexOf(Definitions.subattributeAccessorChar);
	    if (index != -1) {
	    	++depth;
	    	while (index != lastIndex) {
	    		index = getName().indexOf(Definitions.subattributeAccessorChar, index + 1);
	    		++depth;
	    	}
	    }
	    return depth;
	}
	
}
