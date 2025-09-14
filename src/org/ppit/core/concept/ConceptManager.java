package org.ppit.core.concept;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

import org.json.JSONException;
import org.json.JSONObject;
import org.ppit.core.brain.instance.action.ActionInstance;
import org.ppit.core.cognition.CognitionManager;
import org.ppit.core.concept.action.Action;
import org.ppit.core.concept.action.ActionManager;
import org.ppit.core.concept.composite.CompositeConcept;
import org.ppit.core.concept.primitive.PrimitiveConcept;
import org.ppit.core.concept.primitive.PrimitiveType;
import org.ppit.core.concept.set.SetConcept;
import org.ppit.core.plans.Goal;
import org.ppit.core.plans.Plan;
import org.ppit.core.plans.PlanWrapper;
import org.ppit.db.DBManager;
import org.ppit.util.Pair;
import org.ppit.util.exception.PPITException;

public class ConceptManager {
	
	private static ConceptManager m_instance;
	
	private ConceptLibrary m_library = null;
	private ConceptCreator m_creator = null;
	private DBManager m_dbManager = DBManager.getInstance();
	
	private ActionManager m_actionManager = null;
	private CognitionManager m_cogManager = CognitionManager.getInstance();
	
	private boolean m_initializing = false;
	
	private ConceptManager(){
		m_library = new ConceptLibrary();
		m_creator = new ConceptCreator(m_library);
		
		m_actionManager = new ActionManager(this);
	}
	
	public ConceptCreator getCreator() {
		return m_creator;
	}
	
	static {
		m_instance = new ConceptManager();
	}
	
	public static ConceptManager getInstance() {
		return m_instance;
	}
	
	public PrimitiveConcept createPrimitiveConcept(JSONObject conceptJSON) throws JSONException, PPITException	{
		Pair<PrimitiveConcept, Boolean> res = m_creator.createPrimitiveConcept(conceptJSON);
		PrimitiveConcept concept = res.first;
		m_dbManager.writeConcept(concept);
		
		Boolean isUpdate = res.second;
		if(isUpdate == true) {
			reloadGA();
			return m_library.getPrimitiveConcept(concept.getName());
		}
		else if(!m_initializing) {
			m_cogManager.insertAbstract(concept);
		}
		return concept;
	}
	
	public CompositeConcept createCompositeConcept(JSONObject compConcJSON) throws JSONException, PPITException{
		Pair<CompositeConcept, Boolean> res = m_creator.createCompositeConcept(compConcJSON);
		CompositeConcept concept = res.first;
		m_dbManager.writeConcept(concept);
		
		Boolean isUpdate = res.second;
		if(isUpdate == true) {
			reloadGA();
			return m_library.getCompositeConcept(concept.getName());
		}
		else if(!m_initializing) {
			m_cogManager.insertAbstract(concept);
		}
		return concept;
	}
	
	public SetConcept createSetConcept(JSONObject setJSON) throws JSONException, PPITException {
		Pair<SetConcept, Boolean> res = m_creator.createSetConcept(setJSON);
		SetConcept concept = res.first;
		m_dbManager.writeConcept(concept);

		Boolean isUpdate = res.second;
		if(isUpdate == true) {
			reloadGA();
			return m_library.getSetConcept(concept.getName());
		}
		else if(!m_initializing) {
			m_cogManager.insertAbstract(concept);
		}
		return concept;
	}
	
	public Action createAction(JSONObject actionJSON) throws JSONException, PPITException {
		Pair<Action, Boolean> res = m_actionManager.createAction(actionJSON);
		Action action = res.first;
		
		Boolean isUpdate = res.second;
		if(isUpdate == true) {
			reloadGA();
			return m_actionManager.getAction(action.getName());
		}
		else if(!m_initializing) {
			m_cogManager.insertAbstract(action);
		}
		return action;
	}
	
	public Concept removeConcept(String name) throws PPITException {
		Concept concept = m_library.removeConcept(name);
		m_dbManager.removeConcept(concept);
		
		// Remove can be called only when Manager is initialized.
		m_cogManager.removeAbstract(concept);
		return concept;
	}
	
	public Action removeAction(String name) throws PPITException {
		Action action = m_actionManager.removeAction(name);

		// Remove can be called only when Manager is initialized.
		m_cogManager.removeAbstract(action);
		return action;
	}
	
	/**
	 * @brief getConcept() get the concept with the given name from the concept library 
	 * @param name, Name of the concept to be get
	 * @return Returns the concept if it is found and null in other case
	 */
	public Concept getConcept(String name) {
		return m_library.getConcept(name);
	}
	/**
	 * @param name - name of the primitive concept.
	 * @return null if there is no such primitive concept.
	 */
	public PrimitiveConcept getPrimitiveConcept(String name) {
		return m_library.getPrimitiveConcept(name);
	}
	
	/**
	 * @param name - name of the primitive type.
	 * @return null if there is no such primitive type.
	 */
	public PrimitiveType getPrimitiveType(String name) {
		return m_library.getNucleusConcept(name);
	}
	
	private void reloadGA() {
		// TODO this is an ugly implementation. Reset everything and reload from the scratch!
		// This shall be improved to impact only GA reset.
		initialLoadAll();		
	}
	
	// return
	public Goal createGoal(JSONObject goalJSON) throws JSONException, PPITException{
		Goal goal = m_creator.createGoal(goalJSON);
		if (goal != null) {
			m_dbManager.writeGoal(goal);
		}
		return goal;
	}
	
	public Goal removeGoal(String name) throws PPITException {
		Goal goal = m_library.removeGoal(name);
		m_dbManager.removeGoal(goal);
		return goal;
	}
	
	public Goal getGoal(String name) {
		return m_library.getGoal(name);
	}
	
	public Plan createPlan(JSONObject planJSON) throws JSONException, PPITException{
		Plan plan = m_creator.createPlan(planJSON);
		m_dbManager.writePlan(plan);
		return plan;
	}
	
	public Plan removePlan(String name) throws PPITException {
		Plan plan = m_library.removePlan(name);
		m_dbManager.removePlan(plan);
		return plan;
	}
	
	public Plan getPlan(String name) {
		return m_library.getPlan(name);
	}
	
	public PlanWrapper createPlanWrapper(JSONObject planWrapperJSON) throws JSONException, PPITException{
		PlanWrapper planWrapper = m_creator.createPlanWrapper(planWrapperJSON);
		m_dbManager.writePlanWrapper(planWrapper);
		return planWrapper;
	}
	
	public PlanWrapper getPlanWrapper(String name) {
		return m_library.getPlanWrapper(name);
	}
	
	public Collection<PlanWrapper> getPlanWrapperList() {
		return m_library.getPlanWrapperList();
	}
	
	public void resetPlanMatchingCount() {
		m_library.resetPlanMatchinCount();
	}
	
	public PlanWrapper removePlanWrapper(String name) throws PPITException {
		PlanWrapper planWrapper = m_library.removePlanWrapper(name);
		m_dbManager.removePlanWrapper(planWrapper);
		return planWrapper;
	}
	
	public String getPlanWrapperNames() {
		int counter = 0;
		Set<String> planWrapperNames = m_library.getListOfPlanWrapperNames();
		String planWrapperList = "{'PlanWrappersList' : [";
		for(String planWrapperName : planWrapperNames) {
			planWrapperList += "{ 'name' : '" + planWrapperName + "' }";
			if (++counter < planWrapperNames.size()) {
				planWrapperList += ',';
			}
		}
		planWrapperList += "]}";
		return planWrapperList;
	}
	
	public void initialLoadAll()
	{
		m_initializing = true;
		m_library.reset();
		m_actionManager.getLibrary().reset();
		
		m_cogManager.reset();
		try {
			for (JSONObject primitiveConceptJSON : m_dbManager.initialPrimitiveConceptsLoad()) {
				m_creator.createPrimitiveConcept(primitiveConceptJSON);
			}
			for (JSONObject compositeConceptJSON : m_dbManager.initialCompositeConceptsLoad()) {
				m_creator.createCompositeConcept(compositeConceptJSON);
			}
			for (JSONObject setConceptJSON : m_dbManager.initialSetConceptsLoad()) {
				m_creator.createSetConcept(setConceptJSON);
			}

			m_actionManager.initialLoadActions();
			
			for (JSONObject goalJSON : m_dbManager.initialGoalsLoad()) {
				m_creator.createGoal(goalJSON);
			}
			
			for (JSONObject planJSON : m_dbManager.initialPlansLoad()) {
				m_creator.createPlan(planJSON);
			}
			
			for (JSONObject planWrapperJSON : m_dbManager.initialPlanWrappersLoad()) {
				m_creator.createPlanWrapper(planWrapperJSON);
			}
			
			// After loading all concepts, start initializing the Graph of Abstracts.
			m_cogManager.addConcepts(m_library);
			m_cogManager.addActions(m_actionManager.getLibrary());
			
			m_initializing = false;
			
		} catch (JSONException e) {
			e.printStackTrace();
			System.out.println("**************************************\ntraqaaaaaaa");
			System.out.println("(y) 1 peoples Like this.");
		} catch (PPITException e) {
			System.out.println("Error when loading concepts" + e.getMessage());
			e.printStackTrace();
		}
	}
	
	/**
	 * @return Returns String containing names of nucleus concepts
	 */
	public String getNucleusNames()
	{
		String nucleusList = "{'NucleusList' : [";
		Set<String> types = m_library.getListOfTypeNames();
		Iterator<String> k = types.iterator();
		while (k.hasNext()) {
			String key = k.next();
			nucleusList += "{ 'name' : '" + key + "' },";
		}
		nucleusList += "{}]}";
		return nucleusList;
	}
	
	/**
	 * @return Returns String containing names of primitive concepts
	 */
	public String getPrimitiveNames()
	{
		String primitiveList = "{'PrimitiveList' : [";
		for(PrimitiveConcept concept : m_library.getListOfPrimitives()) {
			if (concept.isZeroLevel()) { 
				primitiveList += "{ 'name' : '" + concept.getName() + "' },";
			}
		}
		primitiveList += "{}]}";
		return primitiveList;
	}
	
	/**
	 * @return Returns String containing names of set concept
	 */
	public String getSetNames()
	{
		String setList = "{'setList' : [";
		for(SetConcept concept : m_library.getListOfSets()) {
			if (concept.isZeroLevel()) { 
				setList += "{ 'name' : '" + concept.getName() + "' },";
			}
		}
		setList += "{}]}";
		return setList;
	}
	
	/**
	 * @return Returns String containing names of composite concepts
	 */
	public String getCompositeNames()
	{
		String compositeList = "{'ComposteList' : [";
		for(CompositeConcept concept : m_library.getListOfComposites()) {
			// Remove this If case and the others in similar functions
			// if you want to derive from Valod's Car's Motor.
			if (concept.isZeroLevel()) { 
				compositeList += "{ 'name' : '" + concept.getName() + "' },";
			}
		}
		compositeList += "{}]}";
		return compositeList;
	}
	
	public String getActionNames() {
		return m_actionManager.getActionNames();
	}

	public Action getAction(String name) {
		return m_actionManager.getAction(name);
	}
	
	/**
	 * @brief getConceptNames() get the list of concept names, types, and parent names in a string of json format
	 * This is used in the interface when showing list of concepts
	 * @param type Concept type, to be get,
	 * if it equals to "all" then all the types of concepts are queried
	 * @return Returns String that contains the list of concepts
	 */
	// TODO review Composite Parenting.
	public String getConceptNames(String type)
	{
		boolean needAllList = type.equals("all");
		String conceptsList = "{'ConceptList' : [";
		if (needAllList || type.equals("nucleus")) {
			for (PrimitiveType concept : m_library.getListOfTypes()){
				conceptsList += "{" + concept.getHeaderJson() + "},";
			}
		}
		if (needAllList || type.equals("primitive")) {
			for (PrimitiveConcept concept : m_library.getListOfPrimitives()) {
				if(!concept.isZeroLevel()) {
					continue;
				}
				if (concept.isNucleus() != true) {
					conceptsList += "{"+ concept.getHeaderJson()+ "},";
				}
			}
		}
		if (needAllList || type.equals("composite")) {
			for (CompositeConcept concept : m_library.getListOfComposites()) {
				if(!concept.isZeroLevel()) {
					continue;
				}
				conceptsList += "{" + concept.getHeaderJson()+ "},";
			}
		}
		if (needAllList || type.equals("set")) {
			for (SetConcept concept : m_library.getListOfSets()) {
				if(!concept.isZeroLevel()) {
					continue;
				}
				conceptsList += "{" + concept.getHeaderJson() + " },";
			}
		}
		conceptsList += "{}]}";
		return conceptsList;
	}
	
	/**
	 * @brief getConcContent() get the content of given concept - i.e. generates the header of the concept. 
	 * It is used in the interface
	 * @param concept Concept, for which we need to get the content
	 * @return Returns String, the content of the concept
	 */
	public String getConcContent(Concept concept)
	{
		String conceptContent = null;
		switch (concept.CONCEPT_TYPE)
		{
		case PRIMITIVE:
			// parent null means that it is NUCLEUS type
			if (((PrimitiveConcept)concept).isNucleus()) {
				conceptContent = "{" + ((PrimitiveConcept)concept).getType().getHeaderJson() + " }";
			} else {
				conceptContent = "{" + ((PrimitiveConcept)concept).getHeaderJson() + " }";
			}
			break;
		case COMPOSITE:
			conceptContent = "{" + ((CompositeConcept)concept).getHeaderJson() + ", ";
			conceptContent += "'ConceptList' : [";
			Collection<Concept> attributes = ((CompositeConcept)concept).getAttributes();
			for (Concept attribute : attributes) {
				conceptContent += getConcContent(attribute) + ", ";
			}
			conceptContent += "{}]}";
			break;
		case SET:
			conceptContent = "{" + ((SetConcept)concept).getHeaderJson() + ", ";
			conceptContent += "'ConceptList' : [";
			CompositeConcept attribute = ((SetConcept)concept).getElement();
			conceptContent += getConcContent(attribute);
			conceptContent += "]}";
			break;
		default:
			break;
		}
		return conceptContent;
	}
	
	public String getGoalNames() {
		String goalsList = "{'GoalsList' : [";
		int counter = 0;
		Set<String> goalNames = m_library.getListOfGoalNames();
		for(String goalName : goalNames) {
			goalsList += "{ 'name' : '" + goalName + "' }";
			if (++counter < goalNames.size()) {
				goalsList += ',';
			}
		}
		goalsList += "]}";
		return goalsList;
	}
	
	// {PlansList}
	public String getPlanNames() {
		int counter = 0;
		Set<String> planNames = m_library.getListOfPlanNames();
		String plansList = "{'PlansList' : [";
		for(String planName : planNames) {
			plansList += "{ 'name' : '" + planName + "' }";
			if (++counter < planNames.size()) {
				plansList += ',';
			}
		}
		plansList += "]}";
		return plansList;
	}
	
	/**
	 * @return concept Library
	 * @note THIS IS ONLY FOR TESTING PURPOSES
	 */
	public ConceptLibrary getLibraryForTest(){
		return m_library;
	}
	
}
