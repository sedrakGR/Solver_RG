package org.ppit.db;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;

import org.json.JSONException;
import org.json.JSONObject;
import org.ppit.core.concept.ConceptManager;
import org.ppit.core.concept.action.Action;
import org.ppit.core.concept.action.PostConditionAttribute;
import org.ppit.core.concept.composite.CompositeConcept;
import org.ppit.util.Definitions;
import org.ppit.util.JSONToBasicDBObjectConverter;
import org.ppit.util.exception.PPITException;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;


/**
 * @author SedrakG
 * @brief DB manager for actions, this is singleton
 */
public class DBActionManager {
	// DB collection for actions
	//TODO: move collection names to the Definitions
	private DBCollection m_actionCollection = DBHandler.getInstance().getDB().getCollection("action");
	// instance of DBActionManager
	private static DBActionManager m_instance = null;
	
	/**
	 * @brief always initialize m_instance once
	 */
	static {
		m_instance = new DBActionManager();
	}
	
	/**
	 * @brief private Constructor
	 */
	private DBActionManager()
	{};
	
	/**
	 * @brief getInstance() to get the instance of singleton DBActionManager
	 * @return returns the instance of DBActionManager
	 */
	public static DBActionManager getInstance() {
		return m_instance;
	}
	
	/**
	 * @brief save the given action in DB
	 * @param action to be saved
	 */
	public void writeAction(Action action) {
		// Remove action first to make sure that modifications take place.
		removeAction(action);
		
		m_actionCollection.insert(GetActionBasicDBObject(action));
	}
	
	/**
	 * @brief GetActionBasicDBObject creates proper basicDBObject for the given action  
	 * @param action, the action for which DBObject is generated
	 * @return Returns BasicDBObject of the action 
	 */
	private BasicDBObject GetActionBasicDBObject(Action action)
	{
		BasicDBObject actionContent = new BasicDBObject();
		actionContent.put(Definitions.nameJSON, action.getName());
		actionContent.put(Definitions.sideIndicatorJSON, action.getSideIndicator().getExprString());
		BasicDBObject actionPreConditionConc = new BasicDBObject();
		CompositeConcept attributeConc = action.getPreConditionConcept();
		actionPreConditionConc.put(Definitions.nameJSON, attributeConc.getName());
		
		actionContent.put(Definitions.preCondConceptJSON, actionPreConditionConc);
		DBCompositeManager manager = DBCompositeManager.getInstance();
		manager.writeToMongo(attributeConc, 0);
		
		ArrayList<PostConditionAttribute> attributeList = action.getPostCondition();
		ArrayList<BasicDBObject> attributeArray = new ArrayList<BasicDBObject>(attributeList.size());
		for (int i = 0; i < attributeList.size(); ++i) {
			PostConditionAttribute attribute = attributeList.get(i);
			// this is the concept of attribute
			BasicDBObject attributeContent = new BasicDBObject();
			attributeContent.put(Definitions.postCondExpJSON, attribute.getExpression());
			attributeContent.put(Definitions.postCondNameJSON, attribute.getAttributeName());
			attributeArray.add(attributeContent);
		}
		actionContent.put(Definitions.postConditionListJSON, attributeArray);
		return actionContent;
		
	}
	
	/**
	 * @detailed Remove action from DB also removing it's precondition attribute concept from DB
	 * @param action, the action to be removed
	 */
	public void removeAction(Action action)
	{
		BasicDBObject actionId = new BasicDBObject();
		actionId.put("name", action.getName());
		// we suppose to have only one object with this name in DB
		DBObject removedObject = m_actionCollection.findOne(actionId);
		if(removedObject == null) {
			return;
		}
		String removedAction = removedObject.toString();
		m_actionCollection.remove(actionId);
		try {
			//TODO: make sure we need to delete it's precondition concept
			JSONObject removedActionJSON = new JSONObject(removedAction);
			JSONObject attribute = removedActionJSON.getJSONObject(Definitions.preCondConceptJSON);
			String precondConcName = attribute.getString(Definitions.nameJSON);
			DBCompositeManager.getInstance().removeConcept(precondConcName);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
	}
	
	/**
	 * @brief dumps all contained in the actionCollection to the given file with the given path
	 * @param dirPath, path of folder where should be dumped
	 */
	public void dumpActionCollection(String dirPath)
	{
		File fileToWriteIn = new File(dirPath + "/actions.txt");
		try {
			if (!fileToWriteIn.exists()) {
				fileToWriteIn.createNewFile();
			}
			Writer writerToFile = null;
			writerToFile = new BufferedWriter(new FileWriter(fileToWriteIn, false));
			DBCursor cursor = m_actionCollection.find();
			while(cursor.hasNext()) {
				String actionString;
				actionString = cursor.next().toString();
				String actionJsonString = "{" + actionString.substring(actionString.indexOf("}") + 3);
				
				
				writerToFile.write(actionJsonString + '\n');
				
			}
			writerToFile.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * @brief load all actions from the file of the given folder to actionCollcetion
	 * @param dirPath
	 */
	public void unDump(String dirPath)
	{
		BasicDBObject action = new BasicDBObject();
		File readFile = new File(dirPath + "/actions.txt");
		if (!readFile.exists()) {
			System.out.println("There is no dumped file");
		}
		try {
			FileInputStream fis = new FileInputStream(readFile);
			BufferedInputStream bis = new BufferedInputStream(fis);
			DataInputStream dis = new DataInputStream(bis);
			while (dis.available() != 0) {
				String nucleusConceptContent = dis.readLine();
				JSONObject actionJSON = new JSONObject(nucleusConceptContent);
				ConceptManager.getInstance().createAction(actionJSON);
				m_actionCollection.insert(action);
			}
			fis.close();
	    	bis.close();
	    	dis.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (PPITException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * @brief Load all the actions from DB
	 * @return Returns the list of JSON objects of all actions 
	 */
	public ArrayList<JSONObject> loadFromMongo()
	{
		DBCursor cursor = m_actionCollection.find();
		ArrayList<JSONObject> actionJSONList = new ArrayList<JSONObject>();
		while(cursor.hasNext()) {
			String actionString;
			actionString = cursor.next().toString();
			String actionJsonString = "{" + actionString.substring(actionString.indexOf("}") + 3);
			try {
				actionJSONList.add(new JSONObject(actionJsonString));
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return actionJSONList;
            
	}
}
