package org.ppit.db;

import java.util.ArrayList;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;
import org.ppit.core.plans.Criteria;
import org.ppit.core.plans.Goal;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

public class DBGoalManager {
	/**
	 * @brief m_goalCollection collection of goals
	 */
	private DBCollection m_goalCollection = DBHandler.getInstance().getDB().getCollection("goal");
	
	/**
	 * @brief instance of DBGoalManager which is singleton
	 */
	private static DBGoalManager m_instance;
	
	static
	{
		m_instance = new DBGoalManager();
	}
	
	/**
	 * @Constructor This is singleton
	 */
	private DBGoalManager() {};
	
	/**
	 * @brief getInstance to get the instance of singleton class DBGoalManager
	 * @return DBGoalManager instance
	 */
	public static DBGoalManager getInstance()
	{
		return m_instance;
	}
	
	/**
	 * @brief writeToMongo() write the given goal into DB with the given level
	 * @param goal, the goal to be saved
	 */
	public void writeToMongo(Goal goal)
	{
		m_goalCollection.insert(getGoalBasicDBObject(goal));
	}
	
	/**
	 * @brief getGoalBasicDBObject() get the BasicDBObject object of the goal to save into DB
	 * @param goal, the goal concept to be saved
	 * @return Returns BasicDBObject object, the type kept is DB 
	 */
	public BasicDBObject getGoalBasicDBObject(Goal goal)
	{
		BasicDBObject goalContent = new BasicDBObject();
		goalContent.put("name", goal.getName());
		goalContent.put("primary", goal.isPrimary());
		goalContent.put("preCondition", goal.getPreCondition().getName());
		goalContent.put("postCondition", goal.getPostCondition().getName());
		goalContent.put("depth", goal.getDepth());
		
		
		Map<Integer, Criteria> criteriaMap = goal.getEvaluator().getCriteriaMap();
		ArrayList<BasicDBObject> criteriaListObject = new ArrayList<BasicDBObject>(criteriaMap.size());
		for (Map.Entry<Integer, Criteria> entry : criteriaMap.entrySet()) {
			BasicDBObject entryObject = new BasicDBObject();
			entryObject.put("priority", entry.getKey());
			entryObject.put("criteria", entry.getValue().getExpressionString());
			entryObject.put("type", entry.getValue().getCriteriaType().getText());
			criteriaListObject.add(entryObject);
		}
		goalContent.put("evaluator", criteriaListObject);
		
		
		return goalContent;
		
	}
	
	/**
	 * @brief removeGoal() removes the goal
	 * @param goalName 
	 */
	public void removeGoal(String goalName) {
		BasicDBObject goalId = new BasicDBObject();
		goalId.put("name", goalName);
		m_goalCollection.remove(goalId);
	}
	
	/**
	 * @brief loadGoalJSON() to get the JSON of the goal
	 * @return Returns JSONObject of the goal 
	 */
	public JSONObject loadGoalJSON(String goalName) {
		BasicDBObject goalID = new BasicDBObject();
		goalID.put("name", goalName);
		String goalString;
		JSONObject goalJSON = null;
		goalString = m_goalCollection.findOne(goalID).toString();
		String goalJsonString = "{" + goalString.substring(goalString.indexOf("}") + 3);
		try {
			goalJSON = new JSONObject(goalJsonString);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return goalJSON;
	}
	
	/**
	 * @brief loadFromMongo() this method is to get goals from Mongo db
	 * @return ArrayList of JSONObjects of goals
	 */
	public ArrayList<JSONObject> loadFromMongo()
	{
		DBCursor cursor = m_goalCollection.find();
		ArrayList<JSONObject> goalJSONList = new ArrayList<JSONObject>();
		while(cursor.hasNext()) {
			String goalString;
			goalString = cursor.next().toString();
			String goalJSONString = "{" + goalString.substring(goalString.indexOf("}") + 3);
			try {
				JSONObject goalJSON = new JSONObject(goalJSONString);
				goalJSONList.add(goalJSON);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return goalJSONList;
	}
}
