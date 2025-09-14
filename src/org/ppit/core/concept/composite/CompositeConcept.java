package org.ppit.core.concept.composite;

import java.util.*;

import org.ppit.core.brain.GAEdge;
import org.ppit.core.brain.gaNode.GANode;
import org.ppit.core.concept.Concept;
import org.ppit.core.concept.ConceptReference;
import org.ppit.core.concept.ConceptType;
import org.ppit.core.concept.rules.BaseRule;
import org.ppit.core.concept.rules.RuleGroup;
import org.ppit.util.Definitions;
import org.ppit.util.exception.*;

/**
 * 
 * @author Khachatryan Karen S.
 * @detailed Composite concept class, inherits from Concept class
 *
 */
public class CompositeConcept extends Concept {
	
	/**
	 * @brief The list of attributes. The attribute names are not the full names.
	 */
	private HashMap<String, Concept> m_attributes = null;
	
	/**
	 * @brief Indicates if the concept is CR1 or not (definition of CR1 can be found in docs)
	 */
	private boolean m_CR1 = false;
		
	/**
	 * @brief Indicates if the composite concept abstract or not
	 * The concept is abstract if it has one or more abstract attributes
	 */
	private boolean m_abstract = false;
	
	/**
	 * @brief Indicates if the concept abstractNess is checked already or not
	 * This is in order to avoid double checking inside the concept
	 */
	private boolean m_checkAbstract = false;
	
	/**
	 * @brief The name of the parent of the concept. This attribute shall be used to solve
	 * abstraction.
	 * null - if there is no parent.
	 */
	private ConceptReference m_parent = null;

	/**
	 * @brief external dependencies. These shall be checked outside of the abstract.
	 */
	RuleGroup m_dependentRules = null;
	
	/**
	 * @brief changed independent rules are used in GA Nodes of USAGE abstracts
	 * if the abstract is usage we have to check these rules in the node of abstract
	 */
	RuleGroup m_changedIndependentRules = null;
	
	/**
	 * @brief internal dependencies. If these are satisfied then the instance is fired.
	 */
	RuleGroup m_internalRules = null;
	
	/**
	 * @brief The constructor, which takes the name of the concept
	 * and the name to the parent concept. 
	 * @param name
	 * @param parent_name
	 */
	public CompositeConcept(String name, String parent_name){
		super(name, ConceptType.COMPOSITE);
		if(!parent_name.equals(Definitions.emptyString)) {
			m_parent = new ConceptReference(parent_name);
		}
		m_attributes = new HashMap<String, Concept>();
	}
	
	/**
	 * @brief getParent() get parent concept
	 * @return Returns the composite concept parent concept as composite concept
	 */
	public CompositeConcept getParent() {
		return (m_parent != null) ? (CompositeConcept)m_parent.getConcept() : null;
	}
	
	/**
	 * @brief Sets the concept CR1 
	 */
	public void setCR1() {
		m_CR1 = true;
	}
	
	/**
	 * @brief Indicates if the concept is CR1 or not
	 */
	public boolean isCR1() {
		return m_CR1;
	}
	
	/**
	 * @brief Adds the attribute to the attribute list
	 * @param name - the name as it appears in the definition. (Not the full name!)
	 * @param attribute
	 */
	public void addAttribute(String name, Concept attribute) {
		m_attributes.put(name, attribute);
	}
	
	@Override
	/**
	 * @brief getJSON() get JSON for composite concept
	 */
	public String getJSONPure(String containingConceptName)
	{
		String concDBName = getName();
		String concRealName;
		
		if (!containingConceptName.equals("")) {
			// If containingConceptName is not empty then parse it's real name
			concRealName = concDBName.substring(containingConceptName.length() + 1);
		} else {
			// If no one contains this concept, then it's real and DB names are the same 
			concRealName = concDBName;
		}

		String conceptJSON = "'name' : '" + concRealName + "', ";		
		
		if (m_parent == null || m_parent.getConcept() == null) {
			conceptJSON += "'parent' : '" + getName() + "',";
		} else {
			conceptJSON += "'parent' : '" + m_parent.getName() + "',";
		}
		
		// if the concept is cr1 then 'cr1' value is 1
		conceptJSON += "'cr1' : '" + (m_CR1 ? '1' : '0') + "',";
		
		conceptJSON += "'type' : 'c', ";
		
		// We don't want to pass the index attributes to the interface
		// however we would like to have them left there, as because they might 
		// be useful when debugging.		
		conceptJSON += "'compositeConceptIndexAttrs' : [";		
		conceptJSON += "], 'compConceptAttrs' : [";
		
		boolean theFirst = true;
		for (Map.Entry<String, Concept> attribute : m_attributes.entrySet()) {
		    Concept concept = attribute.getValue();
			if (theFirst == false)
				conceptJSON += ", ";
			else
				theFirst = false;
			
			conceptJSON += "{"; 
			// if the concept is negated then 'negated' value is 1
			conceptJSON += "'negated' : '" + (concept.isNegated() ? '1' : '0') + "',";
			conceptJSON += concept.getJSONPure(concDBName);
			conceptJSON += "}";
		}
		conceptJSON += "]";
		return conceptJSON;
	}

	@Override
	public RuleGroup getChangedEIndependentRules() throws IncorrectDependency {
		if( m_changedIndependentRules == null) {
			this.evaluateDependencies();
		}
		return (RuleGroup)(m_changedIndependentRules.clone());
	}
	
	@Override
	public RuleGroup getExternalDependencies() throws IncorrectDependency {
		if( m_dependentRules == null) {
			this.evaluateDependencies();
		}
		return (RuleGroup)(m_dependentRules.clone());
	}
	
	/**
	 * Returns the list of internal Rules, 
	 * This set of rules are used in GAAbstract to check and fire the instance.
	 */
	public RuleGroup getInternalDependencies() throws IncorrectDependency {
		if( m_internalRules == null) {
			this.evaluateDependencies();
		}
		return (RuleGroup)(m_internalRules.clone());
	}	
	
	@Override
	public void evaluateDependencies() throws IncorrectDependency {
		m_dependentRules = new RuleGroup();
		m_internalRules = new RuleGroup();
		m_changedIndependentRules = new RuleGroup();
		
		for(Concept attr : m_attributes.values()) {
			RuleGroup attrDeps = attr.getExternalDependencies();
			if (isUsage()) {
				RuleGroup attributeChangedIndependentRules = attr.getChangedEIndependentRules();
				attributeChangedIndependentRules.addPrefix(attr.getRelativeName());
				
				m_changedIndependentRules.addRuleGroup(attributeChangedIndependentRules);
			}
			if(attrDeps.isEmpty()) {
				continue;
			}
			// Try to solve the external dependencies of the attribute.
			for(BaseRule r : attrDeps.getRules()) 
			{
				ArrayList<String> deps = r.getDependencies();
				if(deps == null) {
					continue;
				}
				boolean external = false;
				for(String source : deps) {
					int rootEnd = source.indexOf(Definitions.subattributeAccessor);
					String root = source.substring(0, rootEnd);
					
					// If the dependency root is not one of the children 
					//    then we shall add it to the external list,
					//    else we shall add the Rule to the internal list after the loop
					Concept depSource = m_attributes.get(root);
					if (depSource == null) {
						external = true;
						break;
					}
				}
				if(external) {
					m_dependentRules.addRule(r);
				} else {
					m_internalRules.addRule(r);
				}
			}
		}
		m_dependentRules.addPrefix(this.getRelativeName());
	}

	@Override
	public String getHeaderJson(){
		return "'type' : 'c', 'parent' : '" + ((m_parent == null) ? this.getName() : m_parent.getName()) + "', 'name' : '" + this.getName() + "'"; 
	}
	

	@Override
	public String getDuplicatePure(String name) {
		// The current concept will serve as a parent, hence, its full (db) name is the parent name for the new concept.
		String parentName = getName();
		
		// If new name is not specified, then the name of the concept is being used as a new name.
		if(name == null || name.equals("")) {
			name = getRelativeName();
		}

		String conceptJSON = "'name' : '" + name + "', ";		
		conceptJSON += "'parent' : '" + parentName + "',";
		conceptJSON += "'cr1' : '" + (m_CR1 ? '1' : '0') + "',";
		conceptJSON += "'type' : 'c', ";
		conceptJSON += "'compositeConceptIndexAttrs' : [";		
		conceptJSON += "], 'compConceptAttrs' : [";
		
		boolean theFirst = true;
		for (Map.Entry<String, Concept> attribute : m_attributes.entrySet()) {
		    Concept concept = attribute.getValue();
			if (theFirst == false)
				conceptJSON += ", ";
			else
				theFirst = false;
			
			conceptJSON += "{"; 
			// if the concept is negated then 'negated' value is 1
			conceptJSON += "'negated' : '" + (concept.isNegated() ? '1' : '0') + "',";
			// We want to reuse the names of attributes.
			conceptJSON += concept.getDuplicatePure(null);
			conceptJSON += "}";
		}
		conceptJSON += "]";
		return conceptJSON;	
	}
	
	/**
	 * get the list of composite concept's attributes
	 * @return Returns Collection<Concept> collection of attributes
	 */
	public Collection<Concept> getAttributes() {
		return m_attributes.values();
	}

	@Override
	public Concept getAttribute(String path) {
		int rootEnd = path.indexOf(Definitions.subattributeAccessor);
		String root = path;
		if(rootEnd != -1) {
			root = path.substring(0, rootEnd);
		}
		// The attributes inside the concept are relative, hence, the root is the name of the attribute.
		Concept attr = m_attributes.get(root);
		if (attr == null) {
			return null;
		} else {
			if(rootEnd == -1) {
				return attr;
			} else {
				return attr.getAttribute(path.substring(rootEnd + 1));
			}
		}
	}
	
	@Override
	public boolean isAbstract() {
		if (!m_checkAbstract) {
			m_checkAbstract = true;
			m_abstract = false;
			for (Concept attribute : getAttributes()) {
				if (attribute.isAbstract() && !attribute.isUsage()) {
					// if any attribute is abstract but not usage stop running through.
					m_abstract = true;
					break;
				}
			}
		}
		m_abstract = m_abstract || isUsage();
		return m_abstract;
	}
	
	@Override
	public String toText(boolean full) {
	    // Get the relative name of this concept.
	    String conceptRelativeName = getRelativeName();
	    
	    // Find the zero-level parent. Traverse up the parent chain until a parent has no subattribute accessor.
	    CompositeConcept parentConcept = getParent();
	    while (parentConcept != null && parentConcept.getName().indexOf(Definitions.subattributeAccessor) != -1) {
	        parentConcept = parentConcept.getParent();
	    }
	    String parentText = (parentConcept != null) ? parentConcept.getName() : "";
	    
	    // Build a comma-separated list of immediate attribute relative names.
	    StringBuilder attributesText = new StringBuilder();
	    boolean first = true;
	    for (Concept attribute : m_attributes.values()) {
	        if (!first) {
	            attributesText.append(", ");
	        }
	        Concept baseAttr = attribute;

		    while (baseAttr != null && !baseAttr.isZeroLevel()) {
		    	baseAttr = baseAttr.getParent();
		    }
	        attributesText.append(baseAttr.getName());
	        if (attribute.CONCEPT_TYPE == ConceptType.NUCLEUS
	        		|| attribute.CONCEPT_TYPE == ConceptType.PRIMITIVE) {
	        	attributesText.append(attribute.toText(false));
	        }
	        first = false;
	    }
	    
	    // Construct the final descriptive text.
	    StringBuilder sb = new StringBuilder();
	    sb.append("classifier ").append(conceptRelativeName);
	    if (!parentText.isEmpty()) {
	        sb.append(" is ").append(parentText).append(",");
	    }
	    sb.append(" has attributes: ").append(attributesText.toString());

	    boolean hasSpecification = false;
	    try {
			for (GAEdge edge : getGANode().getChildren()) {
			    // Retrieve the slave node; adjust this if you need to call a getter (e.g., edge.getSlave())
			    Concept spec = ((Concept)edge.getSlave().getAbstract());
			    // Check if this slave meets the specification condition
			    if (spec.isSpecification()) {
			    	if (!hasSpecification) {
			    		sb.append(", specified by ");
			    	}
			    	hasSpecification = true;
			        // Print the name from the m_abstract object; adjust if using a getter (e.g., slave.getMAbstract().getName())
			        sb.append(" ").append(spec.getName());
			    }
			}
		} catch (PPITException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	    
	    return sb.toString();
	}

}