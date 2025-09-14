package org.ppit.db;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;
import org.ppit.core.concept.composite.CompositeConcept;
import org.ppit.core.concept.primitive.PrimitiveConcept;
import org.ppit.core.concept.rules.RuleIn;
import org.ppit.core.concept.set.SetConcept;
import org.ppit.util.Definitions;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;


//TODO: after completing set concepts implementation make sure it works correctly
/**
 * @author SedrakG
 * @brief manager of sets in db
 */
public class DBSetManager {
	/**
	 * @brief DB collection of sets
	 */
	private DBCollection m_setCollection = DBHandler.getInstance().getDB().getCollection("set");
	/**
	 * @brief m_instance, Instance of class
	 */
	private static DBSetManager m_instance;
	
	/**
	 * @brief create the instance of this class
	 */
	static
	{
		m_instance = new DBSetManager();
	}
	
	/**
	 * @brief Constructor, It is private because this is singleton
	 */
	private DBSetManager(){};
	
	/**
	 * @brief getInstance() get the instance of the class
	 * @return DBPrimitiveManager instance
	 */
	public static DBSetManager getInstance()
	{
		return m_instance;
	}
	
	/**
	 * @details Removes a set concept with the given name from DB
	 * fully, with it's attribute
	 * @param setName, name of the set concept to be removed
	 */
	public void removeConcept(String setName)
	{
		BasicDBObject setId = new BasicDBObject();
		setId.put("name", setName);
		DBObject removedObject = m_setCollection.findOne(setId);
		if(removedObject == null) {
			return;
		}
		String removedSet = removedObject.toString();
		m_setCollection.remove(setId);
		try {
			JSONObject removedSetJSON = new JSONObject(removedSet);
			JSONObject attribute = removedSetJSON.getJSONObject("attribute");
			String attributeConcName = attribute.getString("name");
			// remove the composite concept attribute
			DBCompositeManager.getInstance().removeConcept(attributeConcName);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
	}
	
	/**
	 * @brief loadAttributeConceptJSON loads set concept as an attribute of another concept 
	 * @param setName, the name of the attribute to be loaded
	 * @return Returns JSONObject of the attribute
	 */
	public JSONObject loadConceptJSON(String setName) {
		BasicDBObject concId = new BasicDBObject();
		concId.put("name", setName);
		String setString;
		setString = m_setCollection.findOne(concId).toString();
		JSONObject setJSON = null;
		String setJSONString = "{" + setString.substring(setString.indexOf("}") + 3);
		try {
			setJSON = new JSONObject(setJSONString);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return setJSON;
	}
	
	/**
	 * @brief loadFromMongo loads all 0 level set concepts from DB
	 * @return
	 */
	public ArrayList<JSONObject> loadFromMongo()
	{
		BasicDBObject setLevel = new BasicDBObject();
		setLevel.put("level", 0);
		ArrayList<JSONObject> setConceptsList = new ArrayList<JSONObject>(); 
		DBCursor cursor = m_setCollection.find(setLevel);
		DBCursor cursor2 = m_setCollection.find();
//		while(cursor.hasNext()) {
//			String setString;
//			setString = cursor.next().toString();
//			String setJsonString = "{" + setString.substring(setString.indexOf("}") + 3);
//			try {
//				setConceptsList.add(new JSONObject(setJsonString));
//			} catch (JSONException e) {
//				e.printStackTrace();
//			}
//		}
		
		//TODO: this needs to be reverted as PrimitiveManager
		while(cursor2.hasNext()) {
			String setString;
			setString = cursor2.next().toString();
			String setJsonString = "{" + setString.substring(setString.indexOf("}") + 3);
			try {
				setConceptsList.add(new JSONObject(setJsonString));
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return setConceptsList;
	}
	
	/**
	 * @brief writeToMongo Writes the given set concept with the given level in DB
	 * @param set, the set concept to be saved
	 * @param level, the level of the set concept
	 */
	public void writeToMongo(SetConcept set, int level)
	{
		m_setCollection.insert(GetConcBasicDBObject(set, level));
	}
	
	private BasicDBObject GetConcBasicDBObject(SetConcept set, int level)
	{
		BasicDBObject setContent = new BasicDBObject();
		setContent.put(Definitions.nameJSON, set.getName());
		BasicDBObject setAttributeConc = new BasicDBObject();
		CompositeConcept attributeConc = set.getElement();
		setAttributeConc.put(Definitions.nameJSON, attributeConc.getName());
		SetConcept parent = set.getParent();
		String parentName = "";
		// it can be null by now
		if (parent != null) {
			parentName = parent.getName();
		}
		setContent.put(Definitions.parentJSON, parentName);
		// TODO: add definitions for these too.
		setContent.put("level", level);
		setContent.put("attribute", setAttributeConc);
		DBCompositeManager.getInstance().writeToMongo(attributeConc, level + 1);
		
		//add mandatory attributes
		RuleIn elementCoung = set.getElementCount();
		ArrayList<BasicDBObject> setMandatoryAttrs = new ArrayList<BasicDBObject>();
		BasicDBObject mandatoryAttribute = new BasicDBObject();
		// TODO: for rule write a function to get it's BasicDBObject
		// this block is only to show that it's some different part 
		{
			mandatoryAttribute.put(Definitions.operatorJSON, elementCoung.getOperator());
			BasicDBObject operValues = new BasicDBObject();
			String s;
			String expressionString = elementCoung.getExpressionString();
			int indexOfSpace = expressionString.indexOf(",");
			s = expressionString.substring(0, indexOfSpace);
			// TODO: add these min and max value names in Definitions
			operValues.put("minValue", s);
			s = expressionString.substring(indexOfSpace + 1);
			operValues.put("maxValue", s);
			mandatoryAttribute.put(Definitions.valueJSON, operValues);
		}
		setMandatoryAttrs.add(mandatoryAttribute);
		setContent.put(Definitions.setMandatoryAttributes, setMandatoryAttrs);

		//TODO: custom set addition attributes are not being handled now
//		ArrayList<PrimitiveConcept> addAttributes = set.getAttributes();
//
//		// Additional Attributes shall not be null. They can be empty!!!
//		{
//			ArrayList<BasicDBObject> addAttributeArray = new ArrayList<BasicDBObject>();
//			for (PrimitiveConcept addAttribute : addAttributes) {
//				// Save the attribute concept in DB
//				DBPrimitiveManager.getInstance().writeToMongo(addAttribute, level + 1);
//				// write it's name in the list of set's attributes
//				BasicDBObject attributeContent = new BasicDBObject();
//				attributeContent.put(Definitions.nameJSON, addAttribute.getName());
//				addAttributeArray.add(attributeContent);
//			}
//			setContent.put(Definitions.setAdditionalAttributes, addAttributeArray);
//		}
		
		ArrayList<BasicDBObject> additionalAttributesArray = new ArrayList<BasicDBObject>();
		for (String constant : set.getConstants()) {
			BasicDBObject constantContent = new BasicDBObject();
			constantContent.put(Definitions.operatorJSON, Definitions.equalsOperator);
			constantContent.put(Definitions.valueJSON, Definitions.constantAttributeValueJSON);
			constantContent.put(Definitions.nameJSON, constant);
			additionalAttributesArray.add(constantContent);
		}
		
		if (set.getContinuous() != null && !set.getContinuous().isEmpty()) {
			BasicDBObject continuousContent = new BasicDBObject();
			continuousContent.put(Definitions.operatorJSON, Definitions.equalsOperator);
			continuousContent.put(Definitions.valueJSON, Definitions.continuousAttributeValueJSON);
			continuousContent.put(Definitions.nameJSON, set.getContinuous());
			additionalAttributesArray.add(continuousContent);
		}
		setContent.put(Definitions.setAdditionalAttributes, additionalAttributesArray);
		return setContent;
	}
}
