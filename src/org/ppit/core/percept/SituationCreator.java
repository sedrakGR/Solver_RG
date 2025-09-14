package org.ppit.core.percept;

import java.util.ArrayList;

import org.json.*;
import org.ppit.core.brain.instance.nucleus.*;
import org.ppit.core.concept.*;
import org.ppit.core.concept.primitive.*;
import org.ppit.util.*;
import org.ppit.util.exception.*;

/**
 * @brief the class is responsible to create Situations as well as 
 * check the CR1 rules when creating them.
 * @author Khachatryan Karen S.
 *
 */
public class SituationCreator {
	private ConceptManager m_conceptManager = null;
	
	public SituationCreator() {
		m_conceptManager = ConceptManager.getInstance();
	}
	
	/**
	 * @brief checks if the given collection contains the element with the specified type.
	 * @param collection - the collection to search
	 * @param type - the required type
	 * @return false if there is no element with 'type' PrimitiveType. 
	 */
	static public boolean existsDuplicate(ArrayList<Pair<PrimitiveType, Integer>> collection,
			PrimitiveType type) {    
		for (Pair<PrimitiveType, Integer> el : collection) {
			if (el.first == type) {
				return true;
			}
		}
		return false; 
	}
	
	/**
	 * @brief Extracts AR1 abstract's attribute list from the given JSON
	 * object. Checks if the CR1 requirements are satisfied i.e. 
	 * are the duplicates for the same PrimitiveType, is the value distinct.
	 * @param attr - the json.
	 * @return the IdGroup containing the nucleus instances. 
	 * @throws JSONException
	 * @throws PPITException
	 */
	public IdGroup extractAR1Inst(JSONObject attr) throws JSONException, PPITException {
		String groupIdStr = attr.getString(Definitions.situationElementIdJSON);
		Integer groupId = null;
		try {
			groupId = new Integer(groupIdStr);
		} catch (NumberFormatException e) {
			throw new PPITException("Expected an integer value for IdGroup but received: " + groupIdStr + 
					". The original exception is:" + e.getMessage());
		}
		
		IdGroup ar1Instance = new IdGroup(groupId);
		
		JSONArray instances = attr.getJSONArray(Definitions.situationElementInstancesJSON);
		for (int i = 0; i < instances.length(); ++i) {			
			JSONObject instance = instances.getJSONObject(i);
			
			String elType = instance.getString(Definitions.typeJSON);

			// Finding the appropriate PrimitiveType
			PrimitiveType elPType = m_conceptManager.getPrimitiveType(elType);
			if(elPType == null) {
				throw new PPITException ("The element type: " + elType + " used in the situation is not defined. " +
						"Please make sure that you are using same names in Meaning definition and Situation formation.");
			}
			
			String elValueStr = instance.getString(Definitions.valueJSON);
			Integer elValue = null;
			try {
				elValue = new Integer(elValueStr);
			} catch (NumberFormatException e) {
				throw new PPITException("Expected an integer value for Nucleus Instance in the Situation but received: " 
						+ elValueStr + ". The original exception is:" + e.getMessage());
			}
			
			// The instance will automatically register itself into the IdGroup.
			new NucleusInstance(elValue, elPType, ar1Instance, null);
		}
		return ar1Instance;
	}
	
	/**
	 * @brief Crates and returns a situation, if there was another situation
	 * with the same name then the old one is removed.
	 * @param sitJSON
	 * @return the new Situation
	 * @throws JSONException
	 * @throws PPITException 
	 */
	public Situation createSituation(JSONObject sitJSON) throws JSONException, PPITException
	{
		String sitName = sitJSON.getString(Definitions.nameJSON);

		Situation situation = new Situation(sitName);
		JSONArray sitElements = sitJSON.getJSONArray(Definitions.situationElementsJSON);
		for (int i = 0; i < sitElements.length(); ++i) {			
			JSONObject attr = sitElements.getJSONObject(i);
			IdGroup ar1Instance = extractAR1Inst(attr);
			situation.addElement(ar1Instance);
		}

		return situation;
	}
}
