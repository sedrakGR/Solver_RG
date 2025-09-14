package org.ppit.db;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;
import org.ppit.core.plans.Criteria;
import org.ppit.core.plans.Goal;
import org.ppit.core.plans.PlanWrapper;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

public class DBPlanWrapperManager {
	/**
	 * @brief m_planWrapperCollection collection of plans
	 */
	private DBCollection m_planWrapperCollection = DBHandler.getInstance().getDB().getCollection("planWrapper");
	
	/**
	 * @brief instance of DBPlanManager which is singleton
	 */
	private static DBPlanWrapperManager m_instance;
	
	static
	{
		m_instance = new DBPlanWrapperManager();
	}
	
	/**
	 * @Constructor This is singleton
	 */
	private DBPlanWrapperManager() {};
	
	/**
	 * @brief getInstance to get the instance of singleton class DBPlanWrapperManager
	 * @return DBPlanManager instance
	 */
	public static DBPlanWrapperManager getInstance()
	{
		return m_instance;
	}
	
	/**
	 * @brief writeToMongo() write the given goal into DB with the given level
	 * @param goal, the goal to be saved
	 */
	public void writeToMongo(PlanWrapper planWrapper)
	{
		m_planWrapperCollection.insert(getPlanWrapperBasicDBObject(planWrapper));
	}
	
	/**
	 * @brief getPlanBasicDBObject() get the BasicDBObject object of the planWrapper to save into DB
	 */
	public BasicDBObject getPlanWrapperBasicDBObject(PlanWrapper planWrapper)
	{
		BasicDBObject planWrapperContent = new BasicDBObject();
		
		planWrapperContent.put("name", planWrapper.getName());
		planWrapperContent.put("plan", planWrapper.getPlan().getName());
		planWrapperContent.put("preCondition", planWrapper.getPreCondition().getName());
		Map<Integer, Criteria> criteriaMap = planWrapper.getEvaluator().getCriteriaMap();
		ArrayList<BasicDBObject> criteriaListObject = new ArrayList<BasicDBObject>(criteriaMap.size());
		for (Map.Entry<Integer, Criteria> entry : criteriaMap.entrySet()) {
			BasicDBObject entryObject = new BasicDBObject();
			entryObject.put("priority", entry.getKey());
			entryObject.put("criteria", entry.getValue().getExpressionString());
			entryObject.put("type", entry.getValue().getCriteriaType().getText());
			criteriaListObject.add(entryObject);
		}
		planWrapperContent.put("evaluator", criteriaListObject);
		return planWrapperContent;
	}
	
	/**
	 * @brief removePlanWrapper() removes the goal
	 * @param planWrapperName 
	 */
	public void removePlanWrapper(String planWrapperName) {
		BasicDBObject planWrapperID = new BasicDBObject();
		planWrapperID.put("name", planWrapperName);
		m_planWrapperCollection.remove(planWrapperID);
	}
	
	/**
	 * @brief loadPlanWrapperJSON() to get the JSON of the planWrapper
	 * @return Returns JSONObject of the planWrapper
	 */
	public JSONObject loadPlanWrapperJSON(String planWrapperName) {
		BasicDBObject planWrapperID = new BasicDBObject();
		planWrapperID.put("name", planWrapperName);
		String planWrapperString;
		JSONObject planWrapperJSON = null;
		planWrapperString = m_planWrapperCollection.findOne(planWrapperID).toString();
		String planWrapperJsonString = "{" + planWrapperString.substring(planWrapperString.indexOf("}") + 3);
		try {
			planWrapperJSON = new JSONObject(planWrapperJsonString);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return planWrapperJSON;
	}
	
	/**
	 * @brief loadFromMongo() this method is to get wrappers from Mongo db
	 * @return ArrayList of JSONObjects of wrappers
	 */
	public ArrayList<JSONObject> loadFromMongo()
	{
		DBCursor cursor = m_planWrapperCollection.find();
		ArrayList<JSONObject> planWrapperJSONList = new ArrayList<JSONObject>();
		while(cursor.hasNext()) {
			String planWrapperString;
			planWrapperString = cursor.next().toString();
			String planWrapperJSONString = "{" + planWrapperString.substring(planWrapperString.indexOf("}") + 3);
			try {
				JSONObject planWrapperJSON = new JSONObject(planWrapperJSONString);
				planWrapperJSONList.add(planWrapperJSON);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return planWrapperJSONList;
	}
}
