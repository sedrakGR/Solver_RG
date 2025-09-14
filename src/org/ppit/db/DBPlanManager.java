package org.ppit.db;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;
import org.ppit.core.plans.Criteria;
import org.ppit.core.plans.Goal;
import org.ppit.core.plans.Plan;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

public class DBPlanManager {
	/**
	 * @brief m_planCollection collection of plans
	 */
	private DBCollection m_planCollection = DBHandler.getInstance().getDB().getCollection("plan");
	
	/**
	 * @brief instance of DBPlanManager which is singleton
	 */
	private static DBPlanManager m_instance;
	
	static
	{
		m_instance = new DBPlanManager();
	}
	
	/**
	 * @Constructor This is singleton
	 */
	private DBPlanManager() {};
	
	/**
	 * @brief getInstance to get the instance of singleton class DBPlanManager
	 * @return DBPlanManager instance
	 */
	public static DBPlanManager getInstance()
	{
		return m_instance;
	}
	
	/**
	 * @brief writeToMongo() write the given goal into DB with the given level
	 * @param goal, the goal to be saved
	 */
	public void writeToMongo(Plan plan)
	{
		m_planCollection.insert(getPlanBasicDBObject(plan));
	}
	
	/**
	 * @brief getPlanBasicDBObject() get the BasicDBObject object of the plan to save into DB
	 * @param goal, the plan concept to be saved
	 * @return Returns BasicDBObject object, the type kept is DB 
	 */
	public BasicDBObject getPlanBasicDBObject(Plan plan)
	{
		BasicDBObject planContent = new BasicDBObject();
		
		planContent.put("name", plan.getName());
		HashMap<Integer, Goal> goals = plan.getGoals();
		ArrayList<BasicDBObject> goalsListObject = new ArrayList<BasicDBObject>(goals.size());
		for (int priority : goals.keySet()) {
			BasicDBObject goalJSON = new BasicDBObject();
			goalJSON.put("name", goals.get(priority).getName());
			goalJSON.put("priority", priority);
			goalsListObject.add(goalJSON);
		}
		planContent.put("goals", goalsListObject);
		return planContent;
	}
	
	/**
	 * @brief removePlan() removes the goal
	 * @param planName 
	 */
	public void removePlan(String planName) {
		BasicDBObject planID = new BasicDBObject();
		planID.put("name", planName);
		m_planCollection.remove(planID);
	}
	
	/**
	 * @brief loadPlanJSON() to get the JSON of the plan
	 * @return Returns JSONObject of the plan 
	 */
	public JSONObject loadPlanJSON(String planName) {
		BasicDBObject planID = new BasicDBObject();
		planID.put("name", planName);
		String planString;
		JSONObject planJSON = null;
		planString = m_planCollection.findOne(planID).toString();
		String planJsonString = "{" + planString.substring(planString.indexOf("}") + 3);
		try {
			planJSON = new JSONObject(planJsonString);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return planJSON;
	}
	
	/**
	 * @brief loadFromMongo() this method is to get goals from Mongo db
	 * @return ArrayList of JSONObjects of goals
	 */
	public ArrayList<JSONObject> loadFromMongo()
	{
		DBCursor cursor = m_planCollection.find();
		ArrayList<JSONObject> planJSONList = new ArrayList<JSONObject>();
		while(cursor.hasNext()) {
			String planString;
			planString = cursor.next().toString();
			String planJSONString = "{" + planString.substring(planString.indexOf("}") + 3);
			try {
				JSONObject planJSON = new JSONObject(planJSONString);
				planJSONList.add(planJSON);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return planJSONList;
	}
}
