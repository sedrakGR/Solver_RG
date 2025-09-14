package org.ppit.core.concept.action;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.ppit.core.concept.*;
import org.ppit.core.concept.composite.CompositeConcept;
import org.ppit.db.DBManager;
import org.ppit.util.Definitions;
import org.ppit.util.Pair;
import org.ppit.util.exception.PPITException;

/**
 * @author SedrakG
 * @brief This class is responsible for creating actions
 */
public class ActionCreator {
	
	DBManager m_dbManager = null;
	ActionLibrary m_library = null;
	ConceptManager m_conceptManager = null;
	
	/**
	 * 
	 * @param library
	 * @param manager, the concept manager, who holds this manager
	 */
	public ActionCreator(ActionLibrary library, ConceptManager manager) {
		m_library = library;
		m_dbManager = DBManager.getInstance();
		m_conceptManager = manager;
	}
	
	public Pair<Action, Boolean> createAction(JSONObject actionJSON) throws JSONException, PPITException{
		String name = actionJSON.getString(Definitions.nameJSON);

		// If there is with the same name in DB then modify it (delete + add new one).
		Action duplicateAction = m_library.getAction(name);
		Boolean isUpdate = false;
		if(duplicateAction != null) {
			isUpdate = true;
		}
		
		// TODO: possible memory issue in the library due to not deleting concept tree during modification (see doc for details).
		// The duplicated action can be overridden but we shall not call 
		// m_library.removeAction(actionName); because it will remove also leaf concepts,
		//   hence an important information.
		Action action = createActionInternal(actionJSON);
		
		m_library.addAction(action);
		return new Pair<Action, Boolean>(action, isUpdate);
	}
	
	public Action createActionInternal(JSONObject actionJSON) throws JSONException, PPITException 
	{
		String name = actionJSON.getString(Definitions.nameJSON);
		
		Action action = new Action(name);
		
		// precondition is a composite concept
		JSONObject precondConcJSON = actionJSON.getJSONObject(Definitions.preCondConceptJSON);
		CompositeConcept preConditionConcept;
		
		// if it is being loaded from DB, it doesn't have any parent, or others, only name
		// and then we just get it by it's name - "ELSE"
		String conceptName = precondConcJSON.getString(Definitions.nameJSON);
		//TODO: I think there is some issue here with this checking. Need to fix this has("parent") is ok or not for this 
		if (precondConcJSON.has(Definitions.parentJSON))
		{
			// the concept is being created, so we need to change it's name to real conc name which will be like this
			// 'action.preConditionName'
			precondConcJSON.remove(Definitions.nameJSON);
			precondConcJSON.put(Definitions.nameJSON, name + '.' + conceptName);
			preConditionConcept = m_conceptManager.createCompositeConcept(precondConcJSON);
		} else {
			//TODO: if we decide that concepts should be loaded at first and actions at second time, then it's ok
			preConditionConcept = (CompositeConcept)m_conceptManager.getConcept(conceptName);
		}
		
		action.setPreCondition(preConditionConcept);
		//side indicator
		String sideIndicatorString = actionJSON.getString(Definitions.sideIndicatorJSON);
		action.setSideIndicator(sideIndicatorString);

		//Create postCondition Attributes
		JSONArray postcondJSON = actionJSON.getJSONArray(Definitions.postConditionListJSON);
		ArrayList<PostConditionAttribute> postCondAttrs = new ArrayList<PostConditionAttribute>();
		PostConditionAttribute postConditionAttribute;
		for (int i = 0; i < postcondJSON.length(); ++i) {
			JSONObject postCondAttrJSON = postcondJSON.getJSONObject(i);
			String attributeName = postCondAttrJSON.getString(Definitions.postCondNameJSON);
			String attributeExpression = postCondAttrJSON.getString(Definitions.postCondExpJSON);
			
			// this is not used for now // or maybe forever
			//String oper = postCondAttrJSON.getString("oper");
			
			postConditionAttribute = new PostConditionAttribute(attributeName, attributeExpression);
			postCondAttrs.add(postConditionAttribute);
		}
		// set precondition and postcondition in action and it's creation is completed
		action.setPreCondition(preConditionConcept);
		action.setPostCondition(postCondAttrs);
		
		return action;
	}
}
