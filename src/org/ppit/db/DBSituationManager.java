package org.ppit.db;

import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;
import org.ppit.core.brain.instance.nucleus.IdGroup;
import org.ppit.core.brain.instance.nucleus.NucleusInstance;
import org.ppit.core.percept.Situation;
import org.ppit.util.Definitions;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;

/**
 * @brief This is the manager of situation in DB
 * @author SedrakG
 *
 */
public class DBSituationManager {
	/**
	 * @brief DB collection of sets
	 */
	private DBCollection m_situationCollection = DBHandler.getInstance().getDB().getCollection("situation");
	/**
	 * @brief m_instance, Instance of class
	 */
	private static DBSituationManager m_instance;
	
	/**
	 * @brief create the instance of this class
	 */
	static
	{
		m_instance = new DBSituationManager();
	}
	
	/**
	 * @brief Constructor, It is private because this is singleton
	 */
	private DBSituationManager(){};
	
	/**
	 * @brief getInstance() get the instance of the class
	 * @return DBPrimitiveManager instance
	 */
	public static DBSituationManager getInstance()
	{
		return m_instance;
	}
	
	/**
	 * @breif Writes situation to the mongoDB
	 * @param situation
	 */
	public void writeToMongo(Situation situation) {
		// First remove situation to make sure that modifications take place.
		removeSituation(situation.getName());
		
		m_situationCollection.insert(getSituationBasicDBObject(situation));
	}
	

	private BasicDBObject getSituationBasicDBObject(Situation situation) {
		BasicDBObject situationContent = new BasicDBObject();
		situationContent.put(Definitions.nameJSON, situation.getName());
		ArrayList<BasicDBObject> elements = new ArrayList<BasicDBObject>();
		for (IdGroup element : situation.getElements()) {
			BasicDBObject elementDBObject = new BasicDBObject();
			elementDBObject.put(Definitions.situationElementIdJSON, "" + element.getIdGroup());
			ArrayList<BasicDBObject> nucleusInstances = new ArrayList<BasicDBObject>();
			for (NucleusInstance nucleusInstance : element.getElements()) {
				BasicDBObject nucleusInstanceDBObject = new BasicDBObject();
				nucleusInstanceDBObject.put(Definitions.valueJSON, "" + nucleusInstance.getValue());
				nucleusInstanceDBObject.put(Definitions.typeJSON, "" + nucleusInstance.getType().getName());
				nucleusInstances.add(nucleusInstanceDBObject);
				elementDBObject.put(Definitions.situationElementInstancesJSON, nucleusInstances);
			}
			elements.add(elementDBObject);
		}
		situationContent.put(Definitions.situationElementsJSON, elements);
		return situationContent;
	}
	
	/**
	 * @brief remove the situations from situation collection in DB
	 * @param situationName the name of situation to delete
	 */
	public void removeSituation(String situationName) {
		BasicDBObject situationId = new BasicDBObject();
		situationId.put("name", situationName);
		m_situationCollection.findAndRemove(situationId);
	}
	
	/**
	 * @brief loads all situations from DB
	 * @return return the list of JSONObjects of all situations in DB
	 * @throws JSONException
	 */
	public ArrayList<JSONObject> loadFromMongo() throws JSONException {
		return new ArrayList<JSONObject>();
//		ArrayList<JSONObject> situations = new ArrayList<JSONObject>();
//		DBCursor cursor = m_situationCollection.find();
//		while(cursor.hasNext()) {
//			String situationString;
//			situationString = cursor.next().toString();
//			String situationJsonString = "{" + situationString.substring(situationString.indexOf("}") + 3);
//			situations.add(new JSONObject(situationJsonString));
//		}		
//		return situations;
	}
}
