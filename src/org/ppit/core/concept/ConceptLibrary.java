package org.ppit.core.concept;

/**
 * 
 * @author Karen Khachatryan S.
 * @detailed The Container which holds and manages all primitive, set and composite
 *  concepts as well as the list of primitive types.
 *  Also holds lists of goals and plans
 *
 */

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.ppit.core.concept.primitive.*;
import org.ppit.core.concept.set.SetConcept;
import org.ppit.core.concept.composite.*;
import org.ppit.core.plans.Goal;
import org.ppit.core.plans.Plan;
import org.ppit.core.plans.PlanWrapper;

public class ConceptLibrary {
	/**
	 * @brief The list of all primitive concepts.
	 */
	private HashMap<String, PrimitiveConcept> m_primitiveConcepts = new HashMap<String, PrimitiveConcept>();
	/**
	 * @brief The list of all composite concepts.
	 */
	private HashMap<String, CompositeConcept> m_compositeConcepts = new HashMap<String, CompositeConcept>();
	/**
	 * @brief The list of all primitive types.
	 */
	private HashMap<String, PrimitiveType> m_primitiveTypes = new HashMap<String, PrimitiveType>(); 
	/**
	 * @brief The list of all set concepts.
	 */
	private HashMap<String, SetConcept> m_setConcepts = new HashMap<String, SetConcept>();
	
	/**
	 * @brief The list of all goals
	 */
	private HashMap<String, Goal> m_goals = new HashMap<String, Goal>();
	
	/**
	 * @brief The list of all plans
	 */
	private HashMap<String, Plan> m_plans = new HashMap<String, Plan>();
	
	/**
	 * @brief The list of all plan Wrappers
	 */
	private HashMap<String, PlanWrapper> m_planWrappers = new HashMap<String, PlanWrapper>();

	
	/**
	 * This function resets the content of the Library. It is designed to be used 
	 * ONLY in testing purposes.
	 */
	public void reset() {
		m_primitiveTypes.clear();
		m_primitiveConcepts.clear();
		m_compositeConcepts.clear();
		m_setConcepts.clear();
		m_goals.clear();
		m_plans.clear();
	}
	
	/**
	 * @param adds the given primitive concept to the primitive concepts.
	 * If the concept has no parent (NUCLEUS) then it is also added to the type list.
	 */
	public void addPrimitiveConcept(PrimitiveConcept primitiveConcept) {
		m_primitiveConcepts.put(primitiveConcept.getName(), primitiveConcept);
		if (primitiveConcept.isNucleus()){
			m_primitiveTypes.put(primitiveConcept.getName(), primitiveConcept.getType());
		}
	}
	
	/**
	 * @return the primitive concept which has the specified name, 
	 * if concept is missing then null is returned
	 */
	public PrimitiveConcept getPrimitiveConcept(String name) {
		return m_primitiveConcepts.get(name);
	}

	/**
	 * @param adds the given composite concept to the composite concepts.
	 */
	public void addCompositeConcept(CompositeConcept compositeConcept) {
		m_compositeConcepts.put(compositeConcept.getName(), compositeConcept);
	}

	/**
	 * @return the composite concept which has the specified name, 
	 * if concept is missing then null is returned
	 */
	public CompositeConcept getCompositeConcept(String name) {
		return m_compositeConcepts.get(name);
	}

	/**
	 * @param adds the given composite concept to the composite concepts.
	 */
	public void addSetConcept(SetConcept set) {
		m_setConcepts.put(set.getName(), set);
	}

	/**
	 * @return the composite concept which has the specified name, 
	 * if concept is missing then null is returned
	 */
	public SetConcept getSetConcept(String name) {
		return m_setConcepts.get(name);
	}
	
	/**
	 * @return the primitive type which has the specified name, 
	 * if NUCLEUS type is missing then null is returned
	 */
	public PrimitiveType getNucleusConcept(String name) {
		return m_primitiveTypes.get(name);
	}
	
	public void addGoal(Goal goal) {
		if (m_goals.containsKey(goal.getName())) {
			m_goals.remove(goal.getName());
		}
		m_goals.put(goal.getName(), goal);
	}
	
	public Goal getGoal(String name) {
		return m_goals.get(name);
	}
	
	public void addPlan(Plan plan) {
		if (m_plans.containsKey(plan.getName())) {
			m_plans.remove(plan.getName());
		}
		m_plans.put(plan.getName(), plan);
	}
	
	public Plan getPlan(String name) {
		return m_plans.get(name);
	}
	
	public void addPlanWrapper(PlanWrapper planWrapper){
		if (m_planWrappers.containsKey(planWrapper.getName())) {
			m_planWrappers.remove(planWrapper.getName());
		}
		m_planWrappers.put(planWrapper.getName(), planWrapper);
	}
	
	public PlanWrapper getPlanWrapper(String name) {
		return m_planWrappers.get(name);
	}
	
	public Collection<PlanWrapper> getPlanWrapperList() {
		return m_planWrappers.values();
	}
	
	public Set<String> getListOfPlanWrapperNames() {
		return m_planWrappers.keySet();
	}
	
	
	
	public void resetPlanMatchinCount() {
		for (Plan plan : m_plans.values()) {
			plan.resetMatchingCount();
		}
	}
		
	/**
	 * @brief Checks concept (both primitive and composite) lists
	 * and returns the concept if find a match.
	 * @return the primitive or composite concept for the corresponding name
	 */
	public Concept getConcept(String name) {
		Concept result = this.getCompositeConcept(name);
		if(result == null){
			result = this.getPrimitiveConcept(name);			
		}
		if(result == null){
			result = this.getSetConcept(name);			
		}
		return result;
	}
	
	/**
	 * @param remove the specified primitive concept from the library. 
	 * If it is a NUCLEUS concept, then it is returned from the type list, too.
	 * @return Concept, if concept is removed, null otherwise.
	 */
	public Concept removePrimitiveConcept(String name) {
		PrimitiveConcept removable = m_primitiveConcepts.remove(name);
		if( removable != null) {
			if (removable.isNucleus()) {
				m_primitiveTypes.remove(removable.getName());
			}
		}
		return removable;
	}
	
	/**
	 * @param remove the specified composite concept and its subconcepts from the library.
	 * @return Concept, if concept is found and removed, null otherwise.
	 */
	public Concept removeCompositeConcept(String name) {
		CompositeConcept concept = m_compositeConcepts.remove(name);
		if(concept != null) {
			for (Concept attr : concept.getAttributes()) {
				removeConcept(attr.getName());
			}
		}
		return concept;
	}
	
	/**
	 * @param remove the specified set concept and its elements and attributes from the library. 
	 * @return SetConcept, if concept is found and removed, null otherwise.
	 */
	public Concept removeSetConcept(String name) {
		SetConcept concept = m_setConcepts.remove(name);
		if(concept == null)
			return null;
		removeCompositeConcept(concept.getElement().getName());
		for (PrimitiveConcept attr : concept.getAttributes()) {
			removePrimitiveConcept(attr.getName());
		}
		return concept;
	}
	
	/**
	 * @brief removes the <b>Concept Tree</b> from the library.
	 * @return Concept if there was concept with specified name, null otherwise.
	 */
	public Concept removeConcept(String name) {
		Concept removable = removePrimitiveConcept(name); 
		if(removable == null) {
			removable = removeSetConcept(name);
			if(removable == null) {
				removable = removeCompositeConcept(name);
			}
		}
		return removable;
	}
	
	/**
	 * @return the list of all NUCLEUS concept names.
	 */
	public Set<String> getListOfTypeNames() {
		return m_primitiveTypes.keySet();
	}

	/**
	 * @return the list of all Composite Concept names.
	 */
	public Set<String> getListOfCompositeNames() {
		return m_compositeConcepts.keySet();
	}

	/**
	 * @return the list of all Primitive Concept names.
	 */
	public Set<String> getListOfPrimitiveNames() {
		return m_primitiveConcepts.keySet();
	}

	/**
	 * @return the list of all Set Concept names.
	 */
	public Set<String> getListOfSetNames() {
		return m_setConcepts.keySet();
	}
	
	/**
	 * @return the list of all NUCLEUS concepts.
	 */
	public Collection<PrimitiveType> getListOfTypes() {
		return m_primitiveTypes.values();
	}

	/**
	 * @return the list of all Composite Concepts.
	 */
	public Collection<CompositeConcept> getListOfComposites() {
		return m_compositeConcepts.values();
	}

	/**
	 * @return the list of all Primitive Concepts.
	 */
	public Collection<PrimitiveConcept> getListOfPrimitives() {
		return m_primitiveConcepts.values();
	}

	/**
	 * @return the list of all Set Concepts.
	 */
	public Collection<SetConcept> getListOfSets() {
		return m_setConcepts.values();
	}
	
	/**
	 * @return the list of all Plans.
	 */
	public Collection<Plan> getListOfPlans() {
		return m_plans.values();
	}
	
	/**
	 * @return the list of all Goals.
	 */
	public Collection<Goal> getListOfGoals() {
		return m_goals.values();
	}
	
	/**
	 * @return the list of all Plan names.
	 */
	public Set<String> getListOfPlanNames() {
		return m_plans.keySet();
	}

	/**
	 * @return the list of all Goal names.
	 */
	public Set<String> getListOfGoalNames() {
		return m_goals.keySet();
	}
	
	public Plan removePlan(String name) {
		Plan removable = m_plans.remove(name);
		return removable;
	}
	
	public PlanWrapper removePlanWrapper(String name) {
		PlanWrapper removable = m_planWrappers.remove(name);
		return removable;
	}
	
	public Goal removeGoal(String name) {
		Goal removable = m_goals.remove(name);
		return removable;
	}
}
