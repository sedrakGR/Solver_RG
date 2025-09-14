package org.ppit.core.concept;

import java.util.HashSet;

import org.json.*;

import org.ppit.core.concept.composite.CompositeConcept;
import org.ppit.core.concept.primitive.PrimitiveConcept;
import org.ppit.core.concept.primitive.PrimitiveType;
import org.ppit.core.concept.set.SetConcept;

import org.ppit.core.concept.rules.*;
import org.ppit.core.plans.Criteria;
import org.ppit.core.plans.CriteriaType;
import org.ppit.core.plans.Goal;
import org.ppit.core.plans.Plan;
import org.ppit.core.plans.PlanWrapper;
import org.ppit.db.DBManager;
import org.ppit.util.exception.*;
import org.ppit.util.Definitions;
import org.ppit.util.Logger;
import org.ppit.util.Pair;
import org.ppit.util.ParseUtil;

public class ConceptCreator {
	DBManager m_dbManager = null;
	ConceptLibrary m_library = null;
	
	public ConceptCreator(ConceptLibrary library) {
		m_library = library;
		m_dbManager = DBManager.getInstance();
	}
	
	/**
	 * @brief Creates the rule
	 * @param expression The expression of the rule
	 * @param operator The operator of the rule
	 * @throws InvalidExpression 
	 * @throws InvalidOperator 
	 */
	public IRule createRule(String expression, String operator) throws InvalidExpression, InvalidOperator
	{
		IRule rule = null;
		if (expression.equals(Definitions.ruleAbstractValue)) {
			rule = new RuleAbstract();
		} else if (expression.equals(Definitions.ruleWildcardValue)) {
			rule = new RuleWildcard();
		} 
		else if (operator.equals(Definitions.greaterOperator)) {
			rule = new RuleG(expression);
		} else if (operator.equals(Definitions.greaterEqualsOperator)) {
			rule = new RuleGE(expression);
		} else if (operator.equals(Definitions.lessOperator)) {
			rule = new RuleL(expression);
		} else if (operator.equals(Definitions.lessEqualsOperator)) {
			rule = new RuleLE(expression);
		} else if (operator.equals(Definitions.inOperator)) {
			rule = new RuleIn(expression);
		} else if (operator.equals(Definitions.equalsOperator)) {
			rule = new RuleE(expression);
		} else if (operator.equals(Definitions.notEqualsOperator)) {
			rule = new RuleNE(expression);
		} else {
			throw new InvalidOperator("Error: " + operator + " is not supported.");
		}
		return rule;
	}

	private String getAttrValue(String operator, JSONObject ruleJSON) throws JSONException
	{
		String attributeValue = null; 
		if(operator.equals(Definitions.inOperator))
		{
			//If operator is 'IN', then handle this way
			//Get the value of the attribute in a JSON object
			JSONObject ruleValue = ruleJSON.getJSONObject(Definitions.valueJSON);
			String minValue = ruleValue.getString(Definitions.minValueJSON);
			String maxValue = ruleValue.getString(Definitions.maxValueJSON);
			attributeValue = minValue + ParseUtil.separator + maxValue;
		}
		else {
			attributeValue = ruleJSON.getString(Definitions.valueJSON);
		}
		return attributeValue;
	}
	
	/**
	 * @brief Create a Primitive Concept from the given JSON object 
	 * @param conceptJSON JSON object of the concept, to create the concept from
	 * @return Returns the created Primitive Concept
	 * @throws JSONException
	 * @throws PPITException
	 */
	public Pair<PrimitiveConcept, Boolean> createPrimitiveConcept(JSONObject conceptJSON) throws JSONException, PPITException
	{
		// Get the name of concept from it's JSON
		String conceptName = conceptJSON.getString(Definitions.nameJSON);
		String parentName = conceptJSON.getString(Definitions.parentJSON);

		PrimitiveConcept primConcept = null;
		
		boolean isKey = false;
		Boolean isUpdate = false;
		
		// Handle PrimitiveType
		if(parentName.equals(Definitions.emptyString) || parentName.equals(conceptName)) { 
			PrimitiveType type = new PrimitiveType(conceptName);
			primConcept = new PrimitiveConcept(conceptName, type, null);
		} else {
			// If concept exists in the library then modify it - update only (nod delete).
			Concept duplicateConc = m_library.getConcept(conceptName);
			if(duplicateConc != null) {
				isUpdate = true;
				
				// I think we must use the existing concept if it already exists (maybe these are changes)
				primConcept = (PrimitiveConcept)duplicateConc;
				// If this is not a modification then throw 
				if(duplicateConc.CONCEPT_TYPE != ConceptType.PRIMITIVE) {
					throw new NameDuplication(conceptName);
				}
			}
			else {
				PrimitiveConcept parent = m_library.getPrimitiveConcept(parentName);
				if(null == parent) {
					JSONObject parentConceptJSON = m_dbManager.loadConceptJSON(parentName, ConceptType.PRIMITIVE);
					if (parentConceptJSON != null) {
						Pair<PrimitiveConcept, Boolean> res = createPrimitiveConcept(parentConceptJSON); 
						parent = res.first;
						isUpdate = isUpdate || res.second;
						if (parent == null) {
							throw new ConceptNotFound(parentName);
						} 
					} else {
						throw new ConceptNotFound(parentName);
					}
				}
				primConcept = new PrimitiveConcept(conceptName, parent.getType(), parent);
				isKey = parent.isKey();
			}
		}
		if(conceptJSON.has(Definitions.keyJSON)) {
			if(conceptJSON.getString(Definitions.keyJSON) != null) {
				isKey = conceptJSON.getString(Definitions.keyJSON).equals(Definitions.trueJSON) ? true : false;
			}
		}
		primConcept.setKey(isKey);
		JSONArray conceptValueAttribute = conceptJSON.getJSONArray(Definitions.valueAttributeJSON);
		JSONObject conceptAttribute = conceptValueAttribute.getJSONObject(0);
		if( null == conceptAttribute) {
			throw new NoValueAttribute(conceptName);
		}
		// Get the name of concept attribute
		// This is not needed, but we keep it because it is used in the front end
		String attributeName = conceptAttribute.getString(Definitions.nameJSON);
		// Get the operator of concept attribute 
		String operator = conceptAttribute.getString(Definitions.operatorJSON);
		String attributeValue = getAttrValue(operator, conceptAttribute); 
		
		IRule rule= createRule(attributeValue, operator);
		primConcept.setAttribute(attributeName, rule);
		primConcept.evaluateDependencies();
		
		m_library.addPrimitiveConcept(primConcept);
		return new Pair<PrimitiveConcept, Boolean>(primConcept, isUpdate);
	}

	private CompositeConcept createCompositeConceptInternal (JSONObject compConcJSON) throws JSONException, PPITException{
		String concName = compConcJSON.getString(Definitions.nameJSON);
		String concParentName = compConcJSON.getString(Definitions.parentJSON);
		
		CompositeConcept parent = null;
		if(concParentName.equalsIgnoreCase(concName)){
			concParentName = "";
		} else {
			if (!concParentName.equals(Definitions.emptyString)) {
				if ((parent = m_library.getCompositeConcept(concParentName)) == null) {
					// if not found in library, search in DB
					JSONObject parentConceptJSON = m_dbManager.loadConceptJSON(concParentName, ConceptType.COMPOSITE);
					if (parentConceptJSON != null) {
						parent = createCompositeConcept(parentConceptJSON).first;
						if (parent == null) {
							throw new ConceptNotFound(concParentName);
						} 
					} else {
						throw new ConceptNotFound(concParentName);
					}
				}
			}
		}
		CompositeConcept composConcept = new CompositeConcept(concName, concParentName);
		if (parent == null) {
			if (compConcJSON.getString(Definitions.cr1JSON).equals(Definitions.trueJSON)) {
				composConcept.setCR1();
			}
		} else if (parent.isCR1()) {
			// TODO check that AR1 Rules are not broken, 
			// because it is possible to inherit from AR1 abstract and add extra attributes.
			composConcept.setCR1();
		}
		
		JSONArray concAttributes = compConcJSON.getJSONArray("compConceptAttrs");
		String concAttrName = "";
		String attrParentName ="";
		Concept attribute;
		boolean needLoadFromDB = compConcJSON.has(Definitions.levelJSON);
		HashSet<String> duplicateAttributeFilteringSet = new HashSet<String>();
		
		for (int i = 0; i < concAttributes.length(); ++i) {			
			JSONObject attr = concAttributes.getJSONObject(i);
			concAttrName = attr.getString(Definitions.nameJSON);
			boolean isNew = duplicateAttributeFilteringSet.add(concAttrName);
			if(false == isNew) {
				Logger.getInstance().log(Logger.WARNING, "Duplicate attribute: " + concAttrName + " arrived for " + concName + ".");
				continue;
			}
			
			boolean negated = attr.getString(Definitions.negatedJSON).equals(Definitions.trueJSON);
			
			// if attribute has no level, that means that it is just being created
			// and if it has level, that means that is has already been created 
			// and, therefore, must be loaded from DB
			ConceptType type;

			if(false == needLoadFromDB){
				concAttrName = concName + '.' + concAttrName;
				attr.remove(Definitions.nameJSON);
				attr.put(Definitions.nameJSON, concAttrName);
				
				attrParentName = attr.getString(Definitions.parentJSON);
				Concept parentConc = m_library.getConcept(attrParentName);
				if(parentConc == null){
					throw new ConceptNotFound(attrParentName);
				}
				type = parentConc.CONCEPT_TYPE;
			} else {
				char typeChar = attr.getString(Definitions.typeJSON).charAt(0);
				type = ConceptType.getConceptTypeByType(typeChar);
				attr = m_dbManager.loadConceptJSON(concAttrName, type);
			}
			switch (type) {
			case PRIMITIVE:
				attribute = this.createPrimitiveConcept(attr).first;
				break;
			case COMPOSITE:
				attribute = this.createCompositeConcept(attr).first;
				break;
			case SET:
				attribute = this.createSetConcept(attr).first;
				break;
			default:
				attribute = null;
				throw new PPITException("Received unknown attriubte type: " + type);
			}
			if (negated) {
				attribute.setNegated();
			}
			composConcept.addAttribute(attribute.getRelativeName(), attribute);			
		}
		return composConcept;
	}
	
	public Pair<CompositeConcept, Boolean> createCompositeConcept(JSONObject compConcJSON) throws JSONException, PPITException{
		String concName = compConcJSON.getString(Definitions.nameJSON);
		Concept duplicateConc = m_library.getConcept(concName);
		Boolean isUpdate = false;
		if(duplicateConc != null) {
			isUpdate = true;
			// If this is not a modification then throw 
			if(duplicateConc.CONCEPT_TYPE != ConceptType.COMPOSITE){
				throw new NameDuplication(concName);
			}
		}
		
		// TODO possible memory issue in the library due to not deleting concept tree during modification (see doc for details).
		// The duplicated composite concept can be overridden but we shall not call 
		// m_library.removeCompositeConcept(concName); because it will remove also leaf concepts,
		//   hence an important information.
		CompositeConcept conc = createCompositeConceptInternal(compConcJSON);
		
		// TODO maintain Virtual Tables
		m_library.addCompositeConcept(conc);
		return new Pair<CompositeConcept, Boolean>(conc, isUpdate);
	}
	
	public Pair<SetConcept, Boolean> createSetConcept(JSONObject setConcJSON) throws JSONException, PPITException{
		String name = setConcJSON.getString(Definitions.nameJSON);

		// If there is with the same name in DB then modify it (delete + add new one).
		Concept duplicateConc = m_library.getConcept(name);
		Boolean isUpdate = false;
		if(duplicateConc != null) {
			isUpdate = true;
			// If this is not a modification then throw 
			if(duplicateConc.CONCEPT_TYPE != ConceptType.SET){
				throw new NameDuplication(name);
			}
		}

		// TODO possible memory issue in the library due to not deleting concept tree during modification (see doc for details).
		// The duplicated set concept can be overridden but we shall not call 
		// m_library.removeSetConcept(concName); because it will remove also leaf concepts,
		//   hence an important information.
		SetConcept conc = createSetConceptInternal(setConcJSON);
		
		// TODO maintain Virtual Tables
		m_library.addSetConcept(conc);
		return new Pair<SetConcept, Boolean>(conc, isUpdate);
	}
	
	private SetConcept createSetConceptInternal(JSONObject setJSON) throws JSONException, PPITException 
	{
		String name = setJSON.getString(Definitions.nameJSON);
		
		String parentName = setJSON.getString(Definitions.parentJSON);
		if(parentName.equals(name)) {
			parentName = Definitions.emptyString;
		}
		if (!parentName.equals(Definitions.emptyString)) {
			SetConcept parent = null;
			if ((parent = m_library.getSetConcept(parentName)) == null) {
				// of not found in library, search in DB
				JSONObject parentConceptJSON = m_dbManager.loadConceptJSON(parentName, ConceptType.SET);
				if (parentConceptJSON != null) {
					parent = createSetConcept(parentConceptJSON).first;
					if (parent == null) {
						throw new ConceptNotFound(parentName);
					} 
				} else {
					throw new ConceptNotFound(parentName);
				}
			}
		}
		SetConcept setConcept = new SetConcept(name, parentName);
		
		// create composite concept, which is set's attribute
		CompositeConcept attributeConcept;
		JSONObject attribute = setJSON.getJSONObject(Definitions.setAttributeJSON);
		
		String attibuteConcName = attribute.getString(Definitions.nameJSON);
		boolean loadingFromDB = setJSON.has(Definitions.levelJSON);
		if (loadingFromDB){
			attibuteConcName = attribute.getString(Definitions.nameJSON);
			attribute = m_dbManager.loadConceptJSON(attibuteConcName, ConceptType.COMPOSITE);
		} else {
			attribute.remove(Definitions.nameJSON);
			attribute.put(Definitions.nameJSON, name + '.' + attibuteConcName);	
		}
		
		attributeConcept = createCompositeConcept(attribute).first;
		setConcept.setElement(attributeConcept);
		
		// Create mandatory attribute
		JSONObject setManJSON = setJSON.getJSONArray(Definitions.setMandatoryAttributes).getJSONObject(0);
		
		// The element count is represented as an IN rule.
		String elCountExp = getAttrValue(Definitions.inOperator, setManJSON);
		RuleIn elCount = new RuleIn(elCountExp);
		
		setConcept.setElementCount(elCount);
		
		// Create additional attributes
		JSONArray setAddJSON = setJSON.getJSONArray(Definitions.setAdditionalAttributes);
		for (int i = 0; i < setAddJSON.length(); ++i) {
			JSONObject attr = setAddJSON.getJSONObject(i);
			// TODO: fix loading from DB
//			if (loadingFromDB) {
//				attr = m_dbManager.loadConceptJSON(attr.getString(Definitions.nameJSON), ConceptType.PRIMITIVE);
//			}
			
			if (attr.getString(Definitions.operatorJSON).equals(Definitions.equalsOperator) && attr.getString(Definitions.valueJSON).equalsIgnoreCase(Definitions.constantAttributeValueJSON)) {
				setConcept.setConstant(attr.getString(Definitions.nameJSON));
			}
			
			//this somehow tries to handle the continuous attribute
			if (attr.getString(Definitions.operatorJSON).equals(Definitions.equalsOperator) && attr.getString(Definitions.valueJSON).equalsIgnoreCase(Definitions.continuousAttributeValueJSON)) {
				setConcept.setContinuous(attr.getString(Definitions.nameJSON));
			}
			//setConcept.addAttribute(additionalAttribute);
			//TODO: fix non constant and non continuous attribute handlings if there are any
			//PrimitiveConcept additionalAttribute = ((PrimitiveConcept)conc).getDuplicate(additionalRuleName);
		}
		return setConcept;
	}
	

	//e.g. {"name":"aaaaa", "primary" : "true", "preCondition":"comp1", "postCondition":"comp2", "depth":"8",
	// "evaluator" : [ {"criteria" : "expression1", "priority" : "1", "type" : "MIN"}, {"criteria" : "expression2", "priority" : "2", "type" : "MAX"}]}
	// TODO: move json attribute names to definitions part
	// returns null when goal with the same name already exists in the library
	public Goal createGoal(JSONObject goalJSON) throws JSONException, PPITException {
		String goalName = goalJSON.getString(Definitions.nameJSON);
		if (m_library.getGoal(goalName) != null) {
			return null;
		}
		boolean isPrimary = goalJSON.getBoolean("primary");
		Goal goal = new Goal(goalName, isPrimary);
		
		String preConditionName = goalJSON.getString("preCondition");
		String postConditionName = goalJSON.getString("postCondition");
		int depth = goalJSON.getInt("depth");
		CompositeConcept concept;
		if (preConditionName != null && !preConditionName.isEmpty()) {
			concept = m_library.getCompositeConcept(preConditionName);
			if (concept == null) {
				throw new PPITException("no composite concept found with the given name of the precondition in library");
			}
			goal.setPreCondition(concept);
		}
		if (postConditionName != null && !postConditionName.isEmpty()) {
			concept = m_library.getCompositeConcept(postConditionName);
			if (concept == null) {
				throw new PPITException("no composite concept found with the given name of the postcondition in library");
			}
			goal.setPostCondition(concept);
		}
		goal.setDepth(depth);
		
		JSONArray criteriaArray = goalJSON.getJSONArray("evaluator");
		for (int i = 0; i < criteriaArray.length(); ++i) {			
			JSONObject criterion = criteriaArray.getJSONObject(i);
			String criteria = criterion.getString("criteria");
			int priority = criterion.getInt("priority");
			String type = criterion.getString("type");
			goal.setCriteria(priority, new Criteria(CriteriaType.stringAsType(type), criteria));
		}
		
		m_library.addGoal(goal);
		return goal;
	}
	
	//{"name":"name","goals":[ {"name" : "Goal 1" , "priority" : "1"}, {"name" : "Goal 3", "priority" : "2"}]}
	public Plan createPlan(JSONObject planJSON) throws JSONException, PPITException{
		String planName = planJSON.getString(Definitions.nameJSON);
		Plan plan = new Plan(planName);
		
		JSONArray goals = planJSON.getJSONArray("goals");
		
		for (int i = 0; i < goals.length(); ++i) {
			JSONObject goalData = goals.getJSONObject(i);
			String goalName = goalData.getString("name");
			int priority = goalData.getInt("priority");
			
			// most probably there will be no issue with this
			Goal goal = m_library.getGoal(goalName);
			if (goal != null) {
				plan.addGoal(goal, priority);
			} else {
				throw new PPITException("no goal found with the given name when creating a plan");
			}
		}
		m_library.addPlan(plan);
		return plan;
	}
	
	// {'name':'wrapperName', 'precondtion':'pcName', 'plan':'planName', 
	//"evaluator" : [ {"criteria" : "expression1", "priority" : "1", "type" : "MIN"}, {"criteria" : "expression2", "priority" : "2", "type" : "MAX"}]}
	public PlanWrapper createPlanWrapper(JSONObject planWrapperJSON) throws JSONException, PPITException {
		String name = planWrapperJSON.getString("name");
		if (name.isEmpty()) {
			throw new PPITException("no name given to planWrapper: createPlanWrapper");
		}
		String preconditionName = planWrapperJSON.getString("preCondition");
		CompositeConcept concept;
		if ((concept = m_library.getCompositeConcept(preconditionName)) == null) {
			throw new PPITException("no compositeConcept found with given name: createPlanWrapper");
		}
		String planName = planWrapperJSON.getString("plan");
		Plan plan;
		if ((plan = m_library.getPlan(planName)) == null) {
			throw new PPITException("no plan found with given name: createPlanWrapper");
		}
	
		PlanWrapper wrapper = new PlanWrapper(name, plan, concept);
		
		JSONArray criteriaArray = planWrapperJSON.getJSONArray("evaluator");
		for (int i = 0; i < criteriaArray.length(); ++i) {			
			JSONObject criterion = criteriaArray.getJSONObject(i);
			String criteria = criterion.getString("criteria");
			int priority = criterion.getInt("priority");
			String type = criterion.getString("type");
			wrapper.setCriteria(priority, new Criteria(CriteriaType.stringAsType(type), criteria));
		}
		
		m_library.addPlanWrapper(wrapper);
		return wrapper;
		
	}
}
