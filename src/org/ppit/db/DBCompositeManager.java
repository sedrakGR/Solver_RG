package org.ppit.db;

import java.util.ArrayList;
import java.util.Collection;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.ppit.core.concept.Concept;
import org.ppit.core.concept.composite.CompositeConcept;
import org.ppit.core.concept.primitive.PrimitiveConcept;
import org.ppit.core.concept.set.SetConcept;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

/**
 * 
 * @author SedrakG
 * @brief DBCompositeManager, singleton
 */
public class DBCompositeManager {
	/**
	 * @brief m_compositeCollection collection of composites
	 */
	private DBCollection m_compositeCollection = DBHandler.getInstance().getDB().getCollection("composite");
	
	/**
	 * @brief instance of DBCompositeManager which is singleton
	 */
	private static DBCompositeManager m_instance;
	
	static
	{
		m_instance = new DBCompositeManager();
	}
	
	/**
	 * @Constructor This is singleton
	 */
	private DBCompositeManager() {};
	
	/**
	 * @brief getInstance to get the instance of singleton class DBCompositeManager
	 * @return DBCompositeManager instance
	 */
	public static DBCompositeManager getInstance()
	{
		return m_instance;
	}
	
	/**
	 * @brief writeToMongo() write the given composite concept into DB with the given level
	 * @param compositeConcept, composite concept to be saved
	 * @param level, level of the attribute
	 * if level is 0, then the concept is not attribute 
	 */
	public void writeToMongo(CompositeConcept compositeConcept, int level )
	{
		m_compositeCollection.insert(GetConcBasicDBObject(compositeConcept, level));
	}
	
	/**
	 * @brief GetConcBasicDBObject() get the BasicDBObject object of the concept to save into DB
	 * @param compositeConcept, composite concept to be saved
	 * @param level, level of the composite concept
	 * @return Returns BasicDBObject object, the type kept is DB 
	 */
	public BasicDBObject GetConcBasicDBObject(CompositeConcept compositeConcept, int level)
	{
		Collection<Concept> composConceptAttrList = compositeConcept.getAttributes();
		BasicDBObject compositeConcContent = new BasicDBObject();
		compositeConcContent.put("name", compositeConcept.getName());
		String parentName = compositeConcept.getParent() != null ? compositeConcept.getParent().getName() : "";
		String cr1 = compositeConcept.isCR1() ? "1" : "0";
		
		// it can be null by now
		if (parentName == null) {
			parentName = "";
		}
		compositeConcContent.put("parent", parentName);
		compositeConcContent.put("level", level);
		compositeConcContent.put("cr1", cr1);
		
		ArrayList<BasicDBObject> attributeArray = new ArrayList<BasicDBObject>(composConceptAttrList.size());
		for (Concept attributeConcept : composConceptAttrList) {
			BasicDBObject attributeContent = new BasicDBObject();
			attributeContent.put("name", attributeConcept.getName());

			String negated = attributeConcept.isNegated() ? "1" : "0";
			attributeContent.put("negated", negated);

			switch (attributeConcept.CONCEPT_TYPE)
			{
			case COMPOSITE:
				attributeContent.put("type", "c");
				writeToMongo((CompositeConcept)attributeConcept, level + 1);
				break;
			case PRIMITIVE:
				attributeContent.put("type", "p");
				DBPrimitiveManager.getInstance().writeToMongo((PrimitiveConcept)attributeConcept, level + 1);
				break;
			case SET:
				attributeContent.put("type", "s");
				DBSetManager.getInstance().writeToMongo((SetConcept)attributeConcept, level + 1);
				break;
			default:
				break;
			}
			attributeArray.add(attributeContent);
		}
		compositeConcContent.put("compConceptAttrs", attributeArray);
		return compositeConcContent;
		
	}
	
	/**
	 * @brief removeConcept() removes the composite concept
	 * it removes it's attribute concepts too 
	 * @param concName 
	 */
	public void removeConcept(String concName) {
		BasicDBObject concId = new BasicDBObject();
		concId.put("name", concName);
		// need to keep the removing concept to access it's attributes and remove them
		DBObject removedObj = m_compositeCollection.findOne(concId);
		if(removedObj == null) {
			return;
		}
		String removedConc = removedObj.toString();
		m_compositeCollection.remove(concId);
		try {
			JSONObject removedConcJSON = new JSONObject(removedConc);
			JSONArray attributes = removedConcJSON.getJSONArray("compConceptAttrs");
			for (int i = 0; i < attributes.length(); ++i) {
				JSONObject attribute = attributes.getJSONObject(i);
				char attributeType = attribute.getString("type").charAt(0);
				String attributeName = attribute.getString("name");
				switch (attributeType)
				{
				case 'c':
					removeConcept(attributeName);
					break;
				case 'p':
					DBPrimitiveManager.getInstance().removeConcept(attributeName);
					break;
				case 's':
					DBSetManager.getInstance().removeConcept(attributeName);
					break;
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * @brief loadAttributeConceptJSON() to get the JSON of the attribute concept
	 * if attribute is loaded from DB then it is being loaded level by level
	 * @param concName, Attribute name, which is being loaded
	 * @return Returns JSONObject of the attribute concept 
	 */
	public JSONObject loadConceptJSON(String concName) {
		BasicDBObject concId = new BasicDBObject();
		concId.put("name", concName);
		String compositeConcString;
		JSONObject compositeConcJSON = null;
		compositeConcString = m_compositeCollection.findOne(concId).toString();
		String compositeConcJsonString = "{" + compositeConcString.substring(compositeConcString.indexOf("}") + 3);
		try {
			compositeConcJSON = new JSONObject(compositeConcJsonString);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return compositeConcJSON;
	}
	
	/**
	 * @brief loadFromMongo() this method is to get the list of 0 level concepts JSONS
	 * @return ArrayList of JSONObjects of concepts
	 */
	public ArrayList<JSONObject> loadFromMongo()
	{
		BasicDBObject concLevel = new BasicDBObject();
		concLevel.put("level", 0);
		// by now it is loading all the concepts
		// but then it must load at first only firstLevel concepts
		DBCursor cursor = m_compositeCollection.find(concLevel);
		DBCursor cursor2 = m_compositeCollection.find();
		ArrayList<JSONObject> conceptsJSONList = new ArrayList<JSONObject>();
//		while(cursor.hasNext()) {
//			String compositeConcString;
//			compositeConcString = cursor.next().toString();
//			String compositeConcJsonString = "{" + compositeConcString.substring(compositeConcString.indexOf("}") + 3);
//			try {
//				JSONObject compositeConcJSON = new JSONObject(compositeConcJsonString);
//				conceptsJSONList.add(compositeConcJSON);
//			} catch (JSONException e) {
//				e.printStackTrace();
//			}
//		}
		
		// TODO: the same as in the primitive manager needs to be reverted
		while(cursor2.hasNext()) {
			String compositeConcString;
			compositeConcString = cursor2.next().toString();
			String compositeConcJsonString = "{" + compositeConcString.substring(compositeConcString.indexOf("}") + 3);
			try {
				JSONObject compositeConcJSON = new JSONObject(compositeConcJsonString);
				conceptsJSONList.add(compositeConcJSON);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return conceptsJSONList;
	}
}
