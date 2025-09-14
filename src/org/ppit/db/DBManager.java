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
import org.ppit.actions.dumpDB.UnDump;
import org.ppit.core.concept.Concept;
import org.ppit.core.concept.ConceptManager;
import org.ppit.core.concept.ConceptType;
import org.ppit.core.concept.composite.CompositeConcept;
import org.ppit.core.concept.primitive.PrimitiveConcept;
import org.ppit.core.concept.set.SetConcept;
import org.ppit.core.percept.Situation;
import org.ppit.core.plans.Goal;
import org.ppit.core.plans.Plan;
import org.ppit.core.plans.PlanWrapper;
import org.ppit.util.ConfigManager;
import org.ppit.util.JSONToBasicDBObjectConverter;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;

/**
 * 
 * @author SedrakG
 * @brief DBManager DB manager, it is Singleton
 */
public class DBManager {
	/**
	 * @brief m_DBPrimitiveManager manager of primitives
	 */
	private DBPrimitiveManager m_DBPrimitiveManager = DBPrimitiveManager.getInstance();
	
	/**
	 * @brief m_DBPrimitiveManager manager of composites
	 */
	private DBCompositeManager m_DBCompositeManager = DBCompositeManager.getInstance();
	
	/**
	 * @brief m_DBSetManager manager of sets
	 */
	private DBSetManager m_DBSetManager = DBSetManager.getInstance();
	
	private DBSituationManager m_DBSituationManager = DBSituationManager.getInstance();
	
	private DBGoalManager m_DBGoalManager = DBGoalManager.getInstance();
	
	private DBPlanManager m_DBPlanManager = DBPlanManager.getInstance();
	
	private DBPlanWrapperManager m_DBPlanWrapperManager = DBPlanWrapperManager.getInstance();
	
	/**
	 * @brief instance of DBManager, it is singleton class
	 */
	private static DBManager m_instance = null;
	private static boolean m_loaded = false;
	
	/**
	 * @brief create an instance of this class
	 */
	static
	{
		m_instance = new DBManager();
	}
	
	
	/**
	 * @brief getInstance(), Get the instance of DB concept manager
	 * @return DBManager
	 */
	public static DBManager getInstance()
	{
		load();
		return m_instance;
	}
	
	private static void load() {
		if(m_loaded) {
			return;
		}
		m_loaded = true;
		if(ConfigManager.getInstance().isAutoDumpEnabled()) {
			UnDump und = new UnDump();
			und.execute();
		}
	}
	
	
	/**
	 * @brief Constructor, it is private
	 */
	private DBManager(){};
	
	/**
	 * @brief removeConcept() removes the given concept from DB
	 * @param concept, the concept to be removed
	 */
	public void removeConcept(Concept concept) 
	{
		switch (concept.CONCEPT_TYPE)
		{
		case PRIMITIVE:
			m_DBPrimitiveManager.removeConcept(concept.getName());
			break;
		case COMPOSITE:
			m_DBCompositeManager.removeConcept(concept.getName());
			break;
		case SET:
			m_DBSetManager.removeConcept(concept.getName());
			break;
		default:
			break;
		}
	}
	
	/**
	 * @brief removeConcept() removes the given primitive concept from DB
	 * @param concept, the primitive concept to be removed from DB
	 */
	public void removeConcept(PrimitiveConcept concept)
	{
		m_DBPrimitiveManager.removeConcept(concept.getName());
	}
	
	/**
	 * @brief removeConcept() removes the given composite concept from DB
	 * @param concept, the composite concept to be removed from DB
	 */
	public void removeConcept(CompositeConcept concept) {
		m_DBCompositeManager.removeConcept(concept.getName());
	}
	
	/**
	 * @brief removeConcept() removes the given set concept from DB
	 * @param concept, the set concept to be removed from DB
	 */
	public void removeConcept(SetConcept concept) {
		m_DBSetManager.removeConcept(concept.getName());
	}
	
	public void removeSituation(Situation situation) {
		m_DBSituationManager.removeSituation(situation.getName());
	}
	
	public void removeGoal(Goal goal) {
		m_DBGoalManager.removeGoal(goal.getName());
	}
	
	public void removePlan(Plan plan) {
		m_DBPlanManager.removePlan(plan.getName());
	}
	
	public void removePlanWrapper(PlanWrapper planWrapper) {
		m_DBPlanWrapperManager.removePlanWrapper(planWrapper.getName());
	}
	
	/**
	 * @brief loadConceptJSON() Loads a concept with the given name and given type
	 * @param attributeName concept name to be loaded from DB
	 * @param type type of the concept to be loaded
	 * @return Returns the concept that is loaded from DB
	 */
	public JSONObject loadConceptJSON(String attributeName, ConceptType type) {
		JSONObject conceptJSON = null;
		switch (type)
		{
		case COMPOSITE:
			conceptJSON = m_DBCompositeManager.loadConceptJSON(attributeName);
			break;
		case PRIMITIVE:
			// this else may be if instance of nucleus, but if we use Sets too
			conceptJSON = m_DBPrimitiveManager.loadConceptJSON(attributeName);
			break;
		case SET:
			conceptJSON = m_DBSetManager.loadConceptJSON(attributeName);
			break;
		default:
			break;
		}
		return conceptJSON;
	}
	
	public JSONObject loadGoalJSON(String goalName) {
		return m_DBGoalManager.loadGoalJSON(goalName);
	}
	
	public JSONObject loadPlanJSON(String planName) {
		return m_DBPlanManager.loadPlanJSON(planName);
	}
	
	public JSONObject loadPlanWrapperJSON(String planWrapperName) {
		return m_DBPlanWrapperManager.loadPlanWrapperJSON(planWrapperName);
	}
		
	/**
	 * @brief initialLoad() Load all the 0 level primitive concepts from DB during the initialization
	 * @return Returns the list of JSONObjects of all primitive concepts loaded from DB
	 */
	public ArrayList<JSONObject> initialPrimitiveConceptsLoad()
	{
		return m_DBPrimitiveManager.loadFromMongo();
	}
	
	/**
	 *
	 * @brief initialLoad() Load all the 0 level composite concepts from DB during the initialization
	 * @return Returns the list of JSONObjects of all composite concepts loaded from DB
	 */
	public ArrayList<JSONObject> initialCompositeConceptsLoad()
	{
		return m_DBCompositeManager.loadFromMongo();
	}
	
	/**
	 * @brief initialLoad() Load all the 0 level set concepts from DB during the initialization
	 * @return Returns the list of JSONObjects of all set concepts loaded from DB
	 */
	public ArrayList<JSONObject> initialSetConceptsLoad()
	{
		return m_DBSetManager.loadFromMongo();
	}
	
	/**
	 * @brief initialSituationsLoad() Loads all the situations from DB during the initialization
	 * @return Returns the list of JSONObjects situations loaded from DB
	 */
	public ArrayList<JSONObject> initialSituationsLoad() throws JSONException
	{
		return m_DBSituationManager.loadFromMongo();
	}
	
	public ArrayList<JSONObject> initialGoalsLoad() throws JSONException
	{
		return m_DBGoalManager.loadFromMongo();
	}
	
	public ArrayList<JSONObject> initialPlansLoad() throws JSONException
	{
		return m_DBPlanManager.loadFromMongo();
	}
	
	public ArrayList<JSONObject> initialPlanWrappersLoad() throws JSONException
	{
		return m_DBPlanWrapperManager.loadFromMongo();
	}
	
	/**
	 * @brief writeConcept() Save the given concept into DB
	 * the level is not given, that means that is is 0 level concept
	 * @param concept, concept to be saved into DB
	 */
	public void writeConcept(Concept concept)
	{
		// Before Writing we shall make sure that there is no duplicate in the DB
		// Therefore, first we initiate removeConcept()
		removeConcept(concept);
		
		// Here, DB is clear and we can safely write the concept there.
		writeConcept(concept, 0);
	}
	
	/**
	 * @brief writeConcept() Save the given concept into DB with the given level
	 * @param concept, concept to be saved into DB
	 * @param level, the level of the concept
	 */
	public void writeConcept(Concept concept, int level)
	{
		switch (concept.CONCEPT_TYPE)
		{
		case COMPOSITE:
			m_DBCompositeManager.writeToMongo((CompositeConcept)concept, level);
			break;
		case PRIMITIVE:
			m_DBPrimitiveManager.writeToMongo((PrimitiveConcept)concept, level);
			break;
		case SET:
			m_DBSetManager.writeToMongo((SetConcept)concept, level);
			break;
		default:
			break;
		}
	}
	
	/**
	 * @brief saves the plan into DB
	 */
	public void writePlan(Plan plan) {
		m_DBPlanManager.writeToMongo(plan);
	}
	
	public void writePlanWrapper(PlanWrapper planWrapper) {
		m_DBPlanWrapperManager.writeToMongo(planWrapper);
	}
	
	/**
	 * @brief saves the goal into DB
	 */
	public void writeGoal(Goal goal) {
		m_DBGoalManager.writeToMongo(goal);
	}
	
	public void writeSituation(Situation situation) {
		m_DBSituationManager.writeToMongo(situation);
	}
	
	
	/**
	 * @brief dumpConcepts() Dumps the concepts to files
	 * primitives.txt, composites.txt, sets.txt,
	 * with the given directoryPath
	 * @param dirPath Directory path where dumping should be done
	 */
	public void dumpConcepts(String dirPath)
	{
		dumpConceptCollection(dirPath + "/primitives.txt", "primitive");
		dumpConceptCollection(dirPath + "/composites.txt", "composite");
		dumpConceptCollection(dirPath + "/sets.txt", "set");
	}
	
	/**
	 * @brief unDump() Restore the DB from the given Directory
	 * @param dirPath, The path of directory where concept files are. 
	 */
	public void unDump(String dirPath)
	{
		DBHandler.getInstance().getDB().dropDatabase();
		unDump(dirPath + "/primitives.txt", "primitive");
		unDump(dirPath + "/composites.txt", "composite");
		unDump(dirPath + "/sets.txt", "set");
		ConceptManager.getInstance().initialLoadAll();
	}
	
	/**
	 * @brief Save concepts from the given file to the given collection in DB
	 * @param fileName File name to read from
	 * @param collectionName The name of the collection
	 */
	private void unDump(String fileName, String collectionName)
	{
		DBCollection conceptCollection = DBHandler.getInstance().getDB().getCollection(collectionName);
		BasicDBObject concept = new BasicDBObject();
		File readFile = new File(fileName);
		if (!readFile.exists()) {
			System.out.println("There is no dumped file");
		}
		try {
			FileInputStream fis = new FileInputStream(readFile);
			BufferedInputStream bis = new BufferedInputStream(fis);
			DataInputStream dis = new DataInputStream(bis);
			while (dis.available() != 0) {
				@SuppressWarnings("deprecation")
				String nucleusConceptContent = dis.readLine();
				JSONObject nucleusConcJSON = new JSONObject(nucleusConceptContent);
				concept = JSONToBasicDBObjectConverter.getDBObjectFromJSONObject(nucleusConcJSON);
				conceptCollection.insert(concept);
			}
			fis.close();
	    	bis.close();
	    	dis.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * @brief dumpConceptCollection() Dumps the content of the collection into the given file
	 * @param fileName, file where collection is being dumped
	 * @param collectionName the name of collection to be dumped
	 */
	private void dumpConceptCollection(String fileName, String collectionName)
	{
		DBCollection conceptCollection = DBHandler.getInstance().getDB().getCollection(collectionName);
		File fileToWriteIn = new File(fileName);
		try {
			if (!fileToWriteIn.exists()) {
				fileToWriteIn.createNewFile();
			}
			Writer writerToFile = null;
			writerToFile = new BufferedWriter(new FileWriter(fileToWriteIn, false));
			DBCursor cursor = conceptCollection.find();
			String concString;
			while(cursor.hasNext()) {
				concString = cursor.next().toString();
				String concJsonString = "{" + concString.substring(concString.indexOf("}") + 3);
				writerToFile.write(concJsonString + '\n');
				
			}
			writerToFile.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * @brief reset current DB
	 */
	public void resetDB()
	{
		DBHandler.getInstance().resetDB();
	}
	
	
	/**
	 * @brief Close the DB port and etc when finalizing
	 */
	protected void finalize()
	{
		DBHandler.getInstance().closeMongo();
	}
	
}
