package org.ppit.core.concept.set;

/**
 * 
 * @author SedrakG, Emil, Karen Khachatryan S.
 * @detailed The type of Set concept.
 *
 */

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.ppit.core.concept.Concept;
import org.ppit.core.concept.ConceptReference;
import org.ppit.core.concept.ConceptType;
import org.ppit.core.concept.composite.CompositeConcept;
import org.ppit.core.concept.primitive.PrimitiveConcept;
import org.ppit.core.concept.rules.BaseRule;
import org.ppit.core.concept.rules.CountRule;
import org.ppit.core.concept.rules.RuleGroup;
import org.ppit.core.concept.rules.RuleIn;
import org.ppit.core.concept.rules.SetRule;
import org.ppit.util.Definitions;
import org.ppit.util.ParseUtil;
import org.ppit.util.exception.*;

import com.mongodb.BasicDBObject;

public class SetConcept extends Concept {
	
	/**
	 * @brief The parent concept. It can be null, which means that concept has no parent.  
	 */
	private ConceptReference m_parent = null;
	
	/**
	 * @brief the element of the Set Concept.
	 */
	private CompositeConcept m_element = null;
	
	/**
	 * @brief the minimum and maximum number of elements in the Set.
	 */
	private RuleIn m_elementCount = null;
	
	/**
	 * @brief the additional attributes of the Set Concept.
	 */
	
	//TODO: this is not a valid data
	private ArrayList<PrimitiveConcept> m_additionalAttributes = new ArrayList<PrimitiveConcept>(); 

	/**
	 * @brief external dependencies
	 */
	RuleGroup m_dependentRules = null;
	
	/**
	 * @brief constant attributes for Set, if there are.
	 */
	ArrayList<String> m_constantAttributes = new ArrayList<String>();
	
	//The only continuous attribute
	String m_continuous = "";
	
	/**
	 * @brief The Constructor
	 * @param name - the name of the Set concept
	 * @param parent - it is not used for now.
	 */
	public SetConcept(String name, String parent_name) {
		super(name, ConceptType.SET);
		if(!parent_name.equals(Definitions.emptyString)) {
			m_parent = new ConceptReference(parent_name);
		}
	}
	
	/**
	 * @brief getParent() get parent of the Set Concept
	 * @return returns the parent concept, which is Set
	 */
	public SetConcept getParent(){
		return (m_parent != null) ? (SetConcept)m_parent.getConcept() : null;
	}
	
	/**
	 * @brief Set the only element of the set object.
	 * @param element
	 */
	public void setElement(CompositeConcept element) {
		m_element = element;
	}
	
	/**
	 * @brief Set the count of the elements in set as a 'IN' rule. 
	 * @param elementCount
	 */
	public void setElementCount(RuleIn elementCount) {
		m_elementCount = elementCount;
	}
	
	/**
	 * @brief Get the count of the elements in set as a 'IN' rule. 
	 */
	public RuleIn getElementCount() {
		return m_elementCount;
	}
	
	/**
	 * @brief Get the only element of the set concept, which is composite
	 * @return Composite concept element attribute of set concept
	 */
	public CompositeConcept getElement(){
		return m_element;
	}
	
	/**
	 * @brief Get the list of additional attributes.
	 * @return the list of attributes.
	 */
	public ArrayList<PrimitiveConcept> getAttributes(){
		return m_additionalAttributes;
	}
	
	/**
	 * @brief Adds additional attributes to the Set.
	 * @param attribute
	 */
	public void addAttribute(PrimitiveConcept attribute){
		m_additionalAttributes.add(attribute);
	}
	
	@Override
	public String getJSONPure(String containingConceptName)
	{
		// String setDBName = getName();
		String setRealName = getRelativeName();

		String parentName = getName();
		if(this.getParent() != null) {
			parentName = m_parent.getName();
		}
		
		String setJSON = "'name' : '" + setRealName + "', ";
		setJSON += "'parent' : '" + parentName + "', ";
		setJSON += "'type' : 's', ";
		setJSON += "'" + Definitions.setAttributeJSON + "' : " + m_element.getJSON() + ", ";
		
		
		setJSON += "'" + Definitions.setMandatoryAttributes + "' : [";
		setJSON += "{ 'name':'" + Definitions.setElementCountJSON + "' , " + m_elementCount.getJSON();
		setJSON += "}]";
		
		setJSON += "," + "'" + Definitions.setAdditionalAttributes + "' : [";
		
			
		for(int i = 0; i < m_additionalAttributes.size(); i ++){
			if (i > 0 ) {
				setJSON += ", ";
			}
			PrimitiveConcept attribute = m_additionalAttributes.get(i);
			setJSON += attribute.getJSON();			
		}
		int i;
		for (i = 0; i < m_constantAttributes.size(); ++i) {
			String constantAttribute = "{ 'oper' : '=' , 'value' : 'const' , 'name' : '" + m_constantAttributes.get(i) + "'}";
			if (i > 0 ) {
				setJSON += ", ";
			}
			setJSON += constantAttribute;
			
		}
		
		if (m_continuous != null && !m_continuous.isEmpty()) {
			if (i > 0) {
				setJSON += ", ";
			}
			setJSON += "{ 'oper' : '=' , 'value' : '-' , 'name' : '" + m_continuous + "'}";
		}
		setJSON += "]";
		
		System.out.print(setJSON);
		
		return setJSON;
	}


	@Override
	public RuleGroup getChangedEIndependentRules() throws IncorrectDependency {
		
		// TODO add fix for this later.
		
		return null;
	}
	
	@Override
	public RuleGroup getExternalDependencies() throws IncorrectDependency {
		if( m_dependentRules == null) {
			this.evaluateDependencies();
		}
		return (RuleGroup)m_dependentRules.clone();
	}
	
	@Override
	public void evaluateDependencies() throws IncorrectDependency {
		m_dependentRules = new RuleGroup();
		
		RuleGroup elementDeps = m_element.getExternalDependencies();
		
		if(!elementDeps.isEmpty()) {
			// Post-process the external dependencies of the element. 
			// At this point the prefix of the elements in the RuleGroup contain 'element' as a prefix
			// and have a type of RuleGeneric. They have to be encapsulated into a new RuleSet object.
			
			for(BaseRule r : elementDeps.getRules()) {
				r.removePrefix();
				m_dependentRules.addRule(new SetRule("", r));
			}
		}
 		
		if(m_elementCount.getDependencies().isEmpty() == false) {
			m_dependentRules.addRule(new CountRule("", m_elementCount));
		}
		
		if (!m_dependentRules.isEmpty())
			m_dependentRules.addPrefix(this.getRelativeName());
	}

	@Override
	public String getHeaderJson() {
		String parentName = getName();
		// TODO: maybe we don't need to show its own name as its parent name if parent name is empty string
		if(this.getParent() != null) {
			parentName = this.getParent().getName();
		}
		return "'type' : 's', 'parent' : '" + parentName + "', 'name' : '" + this.getName() + "'";
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

		conceptJSON += "'type' : 's', ";
		conceptJSON += "'" + Definitions.setAttributeJSON + "' : " + m_element.getDuplicate(null) + ", ";
		
		conceptJSON += "'" + Definitions.setMandatoryAttributes + "' : [";
		conceptJSON += "{ 'name':'" + Definitions.setElementCountJSON + "' , " + m_elementCount.getJSON();
		conceptJSON += "}]";
		
		conceptJSON += "," + "'" + Definitions.setAdditionalAttributes + "' : [";
			
		for(int i = 0; i < m_additionalAttributes.size(); i ++){
			if (i > 0 ) {
				conceptJSON += ", ";
			}
			PrimitiveConcept attribute = m_additionalAttributes.get(i);
			conceptJSON += attribute.getDuplicate(null);			
		}
		conceptJSON += "]";
		return conceptJSON;
	}
	@Override
	public Concept getAttribute(String path) {
		
		int rootEnd = path.indexOf(Definitions.subattributeAccessor);
		if (rootEnd != -1) {
			String root = path.substring(0, rootEnd);

			if (root.equals(Definitions.setElementName)) {
				return m_element.getAttribute(path.substring(rootEnd + 1));
			}
		}
		return m_element.getAttribute(path);
	}
	
	@Override
	public boolean isAbstract() {
		// We don't support Abstract Sets!
		
		return false;
	}
	
	public void setConstant(String conceptName) {
		m_constantAttributes.add(conceptName);
	}
	
	public ArrayList<String> getConstants() {
		return m_constantAttributes;
	}
	
	public void setContinuous(String conceptName) {
		m_continuous = conceptName;
	}
	
	public String getContinuous() {
		return m_continuous;
	}
	
	@Override
	public String toText(boolean full) {
	    String setName = getRelativeName();

	    SetConcept parent = getParent();
	    while (parent != null && !parent.isZeroLevel()) {
	        parent = parent.getParent();
	    }
	    String parentText = (parent != null) ? parent.getName() : "";

	    List<String> elemAttrs = new ArrayList<String>();
	    if (m_element != null) {
	        for (Concept attr : m_element.getAttributes()) {
	            elemAttrs.add(attr.getRelativeName());
	        }
	    }

	    String minCount = "", maxCount = "";
	    if (m_elementCount != null) {
	        ArrayList<String> vals = ParseUtil.splitExpression(m_elementCount.getExpressionString());
	        if (vals.size() >= 2) {
	            minCount = vals.get(0).trim();
	            maxCount = vals.get(1).trim();
	        }
	    }

	    StringBuilder sb = new StringBuilder();
	    sb.append("Set concept ").append(setName);
	    if (!parentText.isEmpty()) {
	        sb.append(" has parent ").append(parentText);
	    }
	    if (!elemAttrs.isEmpty()) {
	        sb.append(", element attributes: ")
	          .append(String.join(", ", elemAttrs));
	    }
	    sb.append(", has minimal = ")
	      .append(minCount)
	      .append(" and maximal = ")
	      .append(maxCount)
	      .append(" appearances");

	    if (!m_constantAttributes.isEmpty()) {;
	        for (int i = 0; i < m_constantAttributes.size(); i++) {
	            if (i > 0) sb.append(", ");
	            sb.append(m_constantAttributes.get(i)).append(" is constant");
	        }
	    }

	    return sb.toString();
	}
}