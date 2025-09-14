package org.ppit.db;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;
import org.ppit.core.concept.primitive.PrimitiveConcept;
import org.ppit.core.concept.primitive.PrimitiveType;
import org.ppit.core.concept.rules.IRule;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

/**
 * 
 * @author SedrakG
 * @brief DBPrimitiveManager This is to manage actions
 * related with primitive concepts and DB
 * This is singleton
 */
public class DBPrimitiveManager {
	/**
	 * @brief primitiveCollection, The collection to keep primitive concepts 
	 */
	private DBCollection m_primitiveCollection = DBHandler.getInstance().getDB().getCollection("primitive");

	// This is needed temporary
	private DBCollection m_nucleusCollection = DBHandler.getInstance().getDB().getCollection("nucleus");
	
	/**
	 * @brief m_instance, Instance of class
	 */
	private static DBPrimitiveManager m_instance;
	
	/**
	 * @brief create the instance of this class
	 */
	static
	{
		m_instance = new DBPrimitiveManager();
	}
	
	/**
	 * @brief Constructor, It is private because this is singleton
	 */
	private DBPrimitiveManager(){};
	
	/**
	 * @brief getInstance() get the instance of the class
	 * @return DBPrimitiveManager instance
	 */
	public static DBPrimitiveManager getInstance()
	{
		return m_instance;
	}
	
	/**
	 * @brief WriteToMongo() Write the given primitive concept to DB
	 * @param primitiveConcept The concept to be saved
	 * @param level the level of the concept (as an attribute of another concept)in the hierarchy of the concept
	 * if the level is 0 then the concept is not an attribute
	 */
	public void writeToMongo(PrimitiveConcept primitiveConcept, int level)
	{
		m_primitiveCollection.insert(GetConcBasicDBObject(primitiveConcept, level));
	}
	
	/**
	 * @brief GetConcBasicDBObject() This is to get an object of BasicDBObject class (which is the type to be saved in DB);
	 * @param primitiveConcept the primitive concept to be saved in DB 
	 * @param level the level of concept (as an attribute of another concept)
	 * @return Returns BasicDBObject object of the primitive concept, to be saved
	 */
	public BasicDBObject GetConcBasicDBObject(PrimitiveConcept primitiveConcept, int level)
	{		
		BasicDBObject primitiveConcContent = new BasicDBObject();
		// put the name of the concept
		primitiveConcContent.put("name", primitiveConcept.getName());
		primitiveConcContent.put("parent", primitiveConcept.getParent() != null? primitiveConcept.getParent().getName() : "");
		// put the value of key
		primitiveConcContent.put("is_key", primitiveConcept.isKey() ? "1" : "0");
		// put the name of parent
		// we keep it's primitive type as parent
		PrimitiveType type = primitiveConcept.getType();
		// level of the concept (as attribute)
		primitiveConcContent.put("level", level);
		//TODO: if fixed in interface change value attribute...
		// value attribute for concept
		ArrayList<BasicDBObject> valueAttrArray = new ArrayList<BasicDBObject>(1);		
		
		BasicDBObject valueAttrContent = new BasicDBObject();
		//name of the attribute
		valueAttrContent.put("name", primitiveConcept.getAttributeName());
		// rule of the attribute
		IRule primitiveConceptRule = primitiveConcept.getRule();
		//operator
		String operator = primitiveConceptRule.getOperator();	
		valueAttrContent.put("oper", operator);
		if (operator.equals("IN")) {
			BasicDBObject operValues = new BasicDBObject();
			String s;
			String expressionString = primitiveConceptRule.getExpressionString();
			int indexOfSpace = expressionString.indexOf(",");
			s = expressionString.substring(0, indexOfSpace);
			operValues.put("minValue", s);
			s = expressionString.substring(indexOfSpace + 1);
			operValues.put("maxValue", s);
			valueAttrContent.put("value", operValues);
		}
		else
		{
			valueAttrContent.put("value", primitiveConceptRule.getExpressionString());
		}
		valueAttrArray.add(valueAttrContent);
		primitiveConcContent.put("nucleusConceptValueAttrs", valueAttrArray);
		
		ArrayList<BasicDBObject> derivedRules = new ArrayList<BasicDBObject>(1);
		// We don't need this for now as because we assume that parent shall always exist.
//		for (IRule rule : primitiveConcept.getExternalDependencies().getRules()) {
//			BasicDBObject ruleContent = new BasicDBObject();
//			//operator
//			operator = rule.getOperator();	
//			ruleContent.put("oper", operator);
//			if (operator.equals("IN")) {
//				BasicDBObject operValues = new BasicDBObject();
//				String s;
//				String expressionString = rule.getExpressionString();
//				int indexOfSpace = expressionString.indexOf(",");
//				s = expressionString.substring(0, indexOfSpace);
//				operValues.put("minValue", s);
//				s = expressionString.substring(indexOfSpace + 1);
//				operValues.put("maxValue", s);
//				ruleContent.put("value", operValues);
//			}
//			else
//			{
//				ruleContent.put("value", rule.getExpressionString());
//			}
//			derivedRules.add(ruleContent);
//		}
		primitiveConcContent.put("rules", derivedRules);
		return primitiveConcContent;
	}
	
	/**
	 * @brief removeConcept() remove the primitive concept from DB
	 * @param concName name of the concept to be removed
	 */
	public void removeConcept(String concName)
	{
		BasicDBObject ConcId = new BasicDBObject();
		ConcId.put("name", concName);
		m_primitiveCollection.remove(ConcId);
		
	}
	
	/**
	 * @brief LoadAttributeConcepts() Load the primitive concept as an attribute of another concept
	 * @param concName the name of the primitive concept to be loaded
	 * @return returns the primitive concept loaded from DB 
	 */
	public JSONObject loadConceptJSON(String concName)
	{
		BasicDBObject ConcId = new BasicDBObject();
		ConcId.put("name", concName);
		DBObject conceptDBObject = m_primitiveCollection.findOne(ConcId);
		if (conceptDBObject == null)
		{
			//TODO: it is not needed, for now just used to test concepts loading
			// if it does not exist in primitive collection try to find it in nucleus one
			conceptDBObject = m_nucleusCollection.findOne(ConcId);
		}
		String primitiveConcString = conceptDBObject.toString();
		JSONObject primitiveConcJSON = null;
		String primitiveConcJsonString = "{" + primitiveConcString.substring(primitiveConcString.indexOf("}") + 3);
		try {
			primitiveConcJSON = new JSONObject(primitiveConcJsonString);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return primitiveConcJSON;
	}
	
	/**
	 * @brief Load all the 0 level concepts
	 */
	public ArrayList<JSONObject> loadFromMongo()
	{
		BasicDBObject concLevel = new BasicDBObject();
		concLevel.put("level", 0);
		// by now it is loading all the concepts
		// but then it must load at first only firstLevel concepts
		DBCursor cursor = m_primitiveCollection.find(concLevel);
		DBCursor cursor2 = m_primitiveCollection.find();
		ArrayList<JSONObject> conceptsJSONList = new ArrayList<JSONObject>();
//		while(cursor.hasNext()) {
//			String primitiveConcString;
//			primitiveConcString = cursor.next().toString();
//			String primitiveConcJsonString = "{" + primitiveConcString.substring(primitiveConcString.indexOf("}") + 3);
//			try {
//				conceptsJSONList.add(new JSONObject(primitiveConcJsonString));
//			} catch (JSONException e) {
//				e.printStackTrace();
//			}
//		}
		
		//TODO: this is a workaround just for undump, as soon as undumping issue is fixed we need to use the code above, which is commented out 
		while(cursor2.hasNext()) {
			String primitiveConcString;
			primitiveConcString = cursor2.next().toString();
			String primitiveConcJsonString = "{" + primitiveConcString.substring(primitiveConcString.indexOf("}") + 3);
			try {
				conceptsJSONList.add(new JSONObject(primitiveConcJsonString));
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return conceptsJSONList;
            
	}
}
